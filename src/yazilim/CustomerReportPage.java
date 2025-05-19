package yazilim;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.text.DecimalFormat;
import yazilim.classes.Customer;
import yazilim.classes.Dealer;

public class CustomerReportPage {
    private JFrame frame;
    private static Connection conn;
    private Dealer dealer;
    private ArrayList<Customer> customers;
    
    private JPanel customerInfoPanel;
    private int customerInfoCustomerId = -1;
    
    private JScrollPane saleHistoryScrollPane;
    private JLabel totalLabel;
    private int saleHistoryCustomerId = -1;
    
    private ChartPanel ageChartPanel;
    private ChartPanel incomeChartPanel;
    private ChartPanel saleStatisticsChartPanel;
    private int statisticsCustomerId = -1;
    

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    conn = java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
                    CustomerReportPage window = new CustomerReportPage(new Dealer(), conn);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CustomerReportPage() throws SQLException {
        conn = java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
        dealer = new Dealer();
        initialize();
    }

    public CustomerReportPage(Dealer dlr, Connection parent_conn) {
        dealer = dlr;
        conn = parent_conn;
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Rapor Sayfası");
        frame.setBounds(100, 100, 1330, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        customers = getAllCustomers();
        Map<Integer, Integer> indexToCustomerIdMap = new HashMap<>();
        JComboBox<String> customerSelector = new JComboBox<>();
        customerSelector.setBounds(515, 30, 300, 30);
        customerSelector.setFont(new Font("Tahoma", Font.PLAIN, 14));
        int index = 0;
        customerSelector.addItem("Tüm Müşteriler");
        indexToCustomerIdMap.put(index, 0);
        index++;
        for (Customer c : customers) {
            String displayText = String.format("%s %s / %s", c.getFirstName(), c.getLastName(), c.getEmail());
            customerSelector.addItem(displayText);
            indexToCustomerIdMap.put(index, c.getCustomerId());
            index++;
        }
        frame.getContentPane().add(customerSelector);

        JButton showAllInfoButton = new JButton("Tüm Bilgilerini Gör");
        showAllInfoButton.setBounds(315, 80, 200, 30);
        frame.getContentPane().add(showAllInfoButton);
        showAllInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clear("customer-info");
            	int selectedIndex = customerSelector.getSelectedIndex();
                if (selectedIndex != -1) {
                    int customerId = indexToCustomerIdMap.get(selectedIndex);
                    if(customerId == 0) {  // Tüm müşteriler işlemi
                    	clearCustomerInfoPanel();
                    	customerInfoCustomerId = 0;
                    	JOptionPane.showMessageDialog(null, "Bu özelliği kullanmak için belli bir müşteri seçiniz.");
                    }
                    else if(customerInfoCustomerId != customerId) {
                    	clearCustomerInfoPanel();
                    	customerInfoCustomerId = customerId;
                    	populateCustomerInfoPanel(customerId);
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir müşteri seçin.");
                }
            }
        });
        
        JButton saleHistortyButton = new JButton("Satın Alımları Gör");
        saleHistortyButton.setBounds(565, 80, 200, 30);
        frame.getContentPane().add(saleHistortyButton);
        saleHistortyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clear("show-sales");
            	int selectedIndex = customerSelector.getSelectedIndex();
                if (selectedIndex != -1) {
                    int customerId = indexToCustomerIdMap.get(selectedIndex);
                    if(customerId == 0) {  // Tüm müşteriler işlemi
                    	clearSaleHistoryScrollPane();
                    	saleHistoryCustomerId = 0;
                    	JOptionPane.showMessageDialog(null, "Bu özelliği kullanmak için belli bir müşteri seçiniz.");
                    }
                    else if(saleHistoryCustomerId != customerId) {
                    	clearSaleHistoryScrollPane();
                    	saleHistoryCustomerId = customerId;
                    	try {
                    		PreparedStatement stmt = conn.prepareStatement("SELECT vehicle_id, sale_date, sale_price FROM sales WHERE user_id = ? ORDER BY sale_date DESC;");
                    	    stmt.setInt(1, customerId);
                    	    ResultSet rs = stmt.executeQuery();
                    	    showSaleHistoryData(rs);
                    	} catch (SQLException ex) {
                    	    ex.printStackTrace();
                    	}
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir müşteri seçin.");
                }
            }
        });

        JButton showStatisticsButton = new JButton("İstatistikleri Gör");
        showStatisticsButton.setBounds(815, 80, 200, 30);
        frame.getContentPane().add(showStatisticsButton);
        showStatisticsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clear("statistics");
            	int selectedIndex = customerSelector.getSelectedIndex();
                if (selectedIndex != -1) {
                    int customerId = indexToCustomerIdMap.get(selectedIndex);
                    if(customerId == 0) {  // Tüm müşteriler işlemi
                    	clearStatisticsCharts();
                    	statisticsCustomerId = 0;
                    	addAgePieChart();
                    	addIncomePieChart();
                    	
						try {
							PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM requests WHERE request_type = 'price_offer' AND status = 'accepted';");
							ResultSet rs = stmt.executeQuery();
	                	    int priceRequestCount = 0;
	                	    if (rs.next()) {
	                	        priceRequestCount = rs.getInt(1);
	                	    }

	                	    
	                	    stmt = conn.prepareStatement("SELECT COUNT(*) FROM sales;");
	                	    rs = stmt.executeQuery();
	                	    int saleCount = 0;
	                	    if (rs.next()) {
	                	        saleCount = rs.getInt(1);
	                	    }
	                	    
	                    	addSaleAfterPriceOfferPieChart(priceRequestCount, saleCount);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                    else if(statisticsCustomerId != customerId) {
                    	clearStatisticsCharts();
                    	statisticsCustomerId = customerId;
                    	
                    	try {
							PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM requests WHERE request_type = 'price_offer' AND status = 'accepted' AND user_id = ?;");
							stmt.setInt(1, customerId);
							ResultSet rs = stmt.executeQuery();
	                	    int priceRequestCount = 0;
	                	    if (rs.next()) {
	                	        priceRequestCount = rs.getInt(1);
	                	    }

	                	    
	                	    stmt = conn.prepareStatement("SELECT COUNT(*) FROM sales WHERE user_id = ?;");
	                	    stmt.setInt(1, customerId);
	                	    rs = stmt.executeQuery();
	                	    int saleCount = 0;
	                	    if (rs.next()) {
	                	        saleCount = rs.getInt(1);
	                	    }
	                	    
	                    	addSaleAfterPriceOfferPieChart(priceRequestCount, saleCount);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir müşteri seçin.");
                }
            }
        });
        
        frame.setVisible(true);
    }
    
    private void populateCustomerInfoPanel(int customerId) {
    	Customer customer = null;
        for (Customer c : customers) {
            if (c.getCustomerId() == customerId) {
                customer = c;
                break;
            }
        }

        if (customer == null) {
            JOptionPane.showMessageDialog(null, "Müşteri bilgisi bulunamadı.");
            clearCustomerInfoPanel();
            customerInfoCustomerId = -1;
            return;
        }
        
        customerInfoPanel = new JPanel();
        customerInfoPanel.setLayout(null);
        customerInfoPanel.setBounds(365, 130, 600, 180);
        customerInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Müşteri Bilgileri", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Tahoma", Font.BOLD, 14)
        ));

        int y = 20;
        int labelHeight = 25;

        JLabel nameLabel = new JLabel("Ad Soyad: " + customer.getFirstName() + " " + customer.getLastName());
        nameLabel.setBounds(20, y, 300, labelHeight);
        customerInfoPanel.add(nameLabel);

        JLabel emailLabel = new JLabel("Email: " + customer.getEmail());
        emailLabel.setBounds(350, y, 300, labelHeight);
        customerInfoPanel.add(emailLabel);

        y += 30;
        JLabel phoneLabel = new JLabel("Telefon: " + customer.getPhoneNumber());
        phoneLabel.setBounds(20, y, 300, labelHeight);
        customerInfoPanel.add(phoneLabel);

        JLabel genderLabel = new JLabel("Cinsiyet: " + translateGender(customer.getGender()));
        genderLabel.setBounds(350, y, 300, labelHeight);
        customerInfoPanel.add(genderLabel);

        y += 30;
        JLabel ageLabel = new JLabel("Yaş: " + customer.getAge());
        ageLabel.setBounds(20, y, 300, labelHeight);
        customerInfoPanel.add(ageLabel);

        JLabel professionLabel = new JLabel("Meslek: " + customer.getProfession());
        professionLabel.setBounds(350, y, 300, labelHeight);
        customerInfoPanel.add(professionLabel);

        y += 30;
        JLabel incomeLabel = new JLabel("Gelir Seviyesi: " + translateIncomeLevel(customer.getIncomeLevel()));
        incomeLabel.setBounds(20, y, 300, labelHeight);
        customerInfoPanel.add(incomeLabel);

        JLabel cityLabel = new JLabel("Şehir: " + customer.getCity());
        cityLabel.setBounds(350, y, 300, labelHeight);
        customerInfoPanel.add(cityLabel);

        y += 30;
        JLabel visitLabel = new JLabel("İlk Ziyaret: " + translateTimestamp(customer.getFirstVisitDate()));
        visitLabel.setBounds(20, y, 300, labelHeight);
        customerInfoPanel.add(visitLabel);

        // Frame'e ekle
        frame.getContentPane().add(customerInfoPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    private void addAgePieChart() {
		try {
			ArrayList<Integer> ageList = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT age FROM customer;");
			ResultSet rs = stmt.executeQuery();
		    while (rs.next()) {
			    ageList.add(rs.getInt("age"));
			}
		    
		    Map<Integer, Integer> ageCountMap = new HashMap<>();
		    for (int age : ageList) {
		        ageCountMap.put(age, ageCountMap.getOrDefault(age, 0) + 1);
		    }

		    DefaultPieDataset ageDataset = new DefaultPieDataset();
		    for (Map.Entry<Integer, Integer> entry : ageCountMap.entrySet()) {
		        int age = entry.getKey();
		        int count = entry.getValue();
		        ageDataset.setValue(age + " yaş", count);
		    }

		    JFreeChart ageChart = ChartFactory.createPieChart("Yaş Dağılımı", ageDataset, true, true, false);
		    PiePlot countPlot = (PiePlot) ageChart.getPlot();

		    countPlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} kişi ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%")));

		    ageChartPanel = new ChartPanel(ageChart);
		    ageChartPanel.setBounds(30, 150, 400, 300);
		    frame.getContentPane().add(ageChartPanel);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void addIncomePieChart() {
		try {
			ArrayList<Integer> ageList = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT income_level FROM customer;");
			ResultSet rs = stmt.executeQuery();
			int lowCount = 0;
			int mediumCount = 0;
			int highCount = 0;
		    while (rs.next()) {
		    	String incomeLevel = rs.getString("income_level");
			    if(incomeLevel.equals("low")) {
			    	lowCount++;;
			    }
			    else if(incomeLevel.equals("medium")) {
			    	mediumCount++;;
			    }
			    else if(incomeLevel.equals("high")) {
			    	highCount++;;
			    }
			}

		    DefaultPieDataset incomeLevelDataset = new DefaultPieDataset();
		    incomeLevelDataset.setValue("Düşük", lowCount);
		    incomeLevelDataset.setValue("Orta", mediumCount);
		    incomeLevelDataset.setValue("Yüksek", highCount);

		    JFreeChart incomeChart = ChartFactory.createPieChart("Gelir Dağılımı", incomeLevelDataset, true, true, false);
		    PiePlot countPlot = (PiePlot) incomeChart.getPlot();

		    countPlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} kişi ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%")));

		    incomeChartPanel = new ChartPanel(incomeChart);
		    incomeChartPanel.setBounds(885, 150, 400, 300);
		    frame.getContentPane().add(incomeChartPanel);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void addSaleAfterPriceOfferPieChart(int priceRequestCount, int saleCount) {
    	DefaultPieDataset saleStatisticsDataset = new DefaultPieDataset();
	    saleStatisticsDataset.setValue("Tamamlanan Sipariş Sayısı", saleCount);
	    saleStatisticsDataset.setValue("Vazgeçilen Sipariş Sayısı", priceRequestCount - saleCount);

	    JFreeChart saleStatisticsChart = ChartFactory.createPieChart("Fiyat Teklifinden Sonra Sipariş Verme", saleStatisticsDataset, true, true, false);
	    PiePlot countPlot = (PiePlot) saleStatisticsChart.getPlot();

	    countPlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} adet ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%")));

	    saleStatisticsChartPanel = new ChartPanel(saleStatisticsChart);
	    saleStatisticsChartPanel.setBounds(465, 150, 400, 300);
	    frame.getContentPane().add(saleStatisticsChartPanel);
    }
    
    private void clear(String caller) {
    	if(caller.equals("customer-info")) {
    		clearSaleHistoryScrollPane();
    		saleHistoryCustomerId = -1;
    		clearStatisticsCharts();
    		statisticsCustomerId = -1;
    	}
    	else if(caller.equals("show-sales")) {
    		clearCustomerInfoPanel();
    		customerInfoCustomerId = -1;
    		clearStatisticsCharts();
    		statisticsCustomerId = -1;
    	}
    	else if(caller.equals("statistics")) {
    		clearCustomerInfoPanel();
    		customerInfoCustomerId = -1;
    		clearSaleHistoryScrollPane();
    		saleHistoryCustomerId = -1;
    	}

    	frame.revalidate();
        frame.repaint();
    }
    
    private void clearCustomerInfoPanel() {
    	if (customerInfoPanel != null) {
            frame.getContentPane().remove(customerInfoPanel);
            customerInfoPanel = null;
        }
    }
    
    private void clearSaleHistoryScrollPane() {
    	if (saleHistoryScrollPane != null) {
            frame.getContentPane().remove(saleHistoryScrollPane);
            saleHistoryScrollPane = null;
        }
    	if (totalLabel != null) {
            frame.getContentPane().remove(totalLabel);
            totalLabel = null;
        }
    }
    
    private void clearStatisticsCharts() {
    	if (ageChartPanel != null) {
            frame.getContentPane().remove(ageChartPanel);
            ageChartPanel = null;
        }
    	if (incomeChartPanel != null) {
            frame.getContentPane().remove(incomeChartPanel);
            incomeChartPanel = null;
        }
    	if (saleStatisticsChartPanel != null) {
            frame.getContentPane().remove(saleStatisticsChartPanel);
            saleStatisticsChartPanel = null;
        }
    }
    
    private void showSaleHistoryData(ResultSet rs) {
    	String[] columnNames = {"#", "Araç", "Satış Tarihi", "Satış Fiyatı", "Dönem"};
	    ArrayList<Object[]> rowData = new ArrayList<>();
	    int counter = 1;
	    double totalSalePrice = 0.00;
	    try {
			while (rs.next()) {
			    int vehicleId = rs.getInt("vehicle_id");
			    Date saleDate = rs.getDate("sale_date");
			    double salePrice = rs.getDouble("sale_price");
			    totalSalePrice += salePrice;

			    Calendar cal = Calendar.getInstance();
			    cal.setTime(saleDate);
			    int year = cal.get(Calendar.YEAR);
			    int month = cal.get(Calendar.MONTH) + 1;
			    int period = (month - 1) / 3 + 1;
			    String periodText = String.format("%d - %d. Dönem", year, period);
			    String carInfo = getCarInfo(vehicleId);
			    
			    rowData.add(new Object[]{counter, carInfo, saleDate.toString(), String.format("%.2f₺", salePrice), periodText});
			    counter++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    Object[][] data = rowData.toArray(new Object[0][]);

	    JTable table = new JTable(data, columnNames);
	    TableColumn countColumn = table.getColumnModel().getColumn(0);
	    countColumn.setMinWidth(20);
	    countColumn.setMaxWidth(20);
	    countColumn.setPreferredWidth(20);
	    
	    totalLabel = new JLabel(String.format("Toplam Satış: %.2f₺", totalSalePrice));
	    totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    totalLabel.setBounds(515, 120, 300, 25);
	    
	    saleHistoryScrollPane = new JScrollPane(table);
	    saleHistoryScrollPane.setBounds(215, 150, 900, 300);

	    frame.getContentPane().add(totalLabel);
	    frame.getContentPane().add(saleHistoryScrollPane);
    }
    
    private String getCarInfo(int vehicleId) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT brand, model, year, package FROM vehicle WHERE vehicle_id = ?");
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("brand") + " / " + rs.getString("model") + " / " + rs.getInt("year") + " / " + rs.getString("package");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Bilinmeyen Kullanıcı";
    }
    
    private ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customerList = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customer");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customerList.add(new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("phone_number"),
                    rs.getString("gender"),
                    rs.getInt("age"),
                    rs.getString("profession"),
                    rs.getString("income_level"),
                    rs.getString("city"),
                    rs.getTimestamp("first_visit_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }
    
    private String translateIncomeLevel(String incomeLevel) {
    	String translation = "";
    	switch(incomeLevel) {
    	case "low":
    		translation = "Düşük";
    		break;
    	case "medium":
    		translation = "Orta";
    		break;
    	case "high":
    		translation = "Yüksek";
    		break;
    	}
    	
    	return translation;
    }
    
    private String translateGender(String gender) {
    	String translation = "";
    	switch(gender) {
	    	case "male":
	    		translation = "Erkek";
	    		break;
	    	case "female":
	    		translation = "Kadın";
	    		break;
	    	case "other":
	    		translation = "Belirtmek İstemiyorum";
	    		break;
    	}
    	
    	return translation;
    }
    
    private String translateTimestamp(Timestamp timestamp) {
    	String date =  timestamp.toString().split(" ")[0];
    	return date.split("-")[2] + " " + getMonth(date.split("-")[1]) + " " + date.split("-")[0];
    }
    
    private String getMonth(String monthValue) {
    	String month = "";
    	switch (monthValue) {
	        case "01":
	            month = "Ocak";
	            break;
	        case "02":
	            month = "Şubat";
	            break;
	        case "03":
	            month = "Mart";
	            break;
	        case "04":
	            month = "Nisan";
	            break;
	        case "05":
	            month = "Mayıs";
	            break;
	        case "06":
	            month = "Haziran";
	            break;
	        case "07":
	            month = "Temmuz";
	            break;
	        case "08":
	            month = "Ağustos";
	            break;
	        case "09":
	            month = "Eylül";
	            break;
	        case "10":
	            month = "Ekim";
	            break;
	        case "11":
	            month = "Kasım";
	            break;
	        case "12":
	            month = "Aralık";
	            break;
	        default:
	            month = "Geçersiz ay";
	            break;
	    }
    	
    	return month;
    }


    public void showFrame() {
        frame.setVisible(true);
    }
}
