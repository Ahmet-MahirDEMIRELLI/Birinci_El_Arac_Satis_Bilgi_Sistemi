package yazilim;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.text.DecimalFormat;

import yazilim.classes.Vehicle;
import yazilim.classes.WarehouseOrDealer;

public class ReportPage {
    private JFrame frame;
    private static Connection conn;
    private WarehouseOrDealer dealer;
    private ChartPanel countChartPanel;
    private ChartPanel priceChartPanel;
    private int reportAgainstOthersVehicleId = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    conn = java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
                    ReportPage window = new ReportPage(new WarehouseOrDealer(), conn);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ReportPage() throws SQLException {
        conn = java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
        dealer = new WarehouseOrDealer();
        initialize();
    }

    public ReportPage(WarehouseOrDealer dlr, Connection parent_conn) {
        dealer = dlr;
        conn = parent_conn;
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Rapor Sayfası");
        frame.setBounds(100, 100, 1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        ArrayList<Vehicle> vehicleList = getAllVehicles();
        Map<Integer, Integer> indexToVehicleIdMap = new HashMap<>();
        JComboBox<String> vehicleSelector = new JComboBox<>();
        vehicleSelector.setBounds(450, 30, 300, 30);
        vehicleSelector.setFont(new Font("Tahoma", Font.PLAIN, 14));
        int index = 0;
        for (Vehicle v : vehicleList) {
            String displayText = String.format("%s / %s / %d / %s", v.getBrand(), v.getModel(), v.getYear(), v.getPckg());
            vehicleSelector.addItem(displayText);
            indexToVehicleIdMap.put(index, v.getVehicleId());
            index++;
        }
        frame.getContentPane().add(vehicleSelector);

        JButton reportAgainstOthersButton = new JButton("Diğer Araçlara İle Karşılaştır");
        reportAgainstOthersButton.setBounds(375, 80, 200, 30);
        frame.getContentPane().add(reportAgainstOthersButton);
        reportAgainstOthersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = vehicleSelector.getSelectedIndex();
                if (selectedIndex != -1) {
                    int vehicleId = indexToVehicleIdMap.get(selectedIndex);
                    if(reportAgainstOthersVehicleId != vehicleId) {
                    	clearReportAgainstOthersCharts();
                        reportAgainstOthersVehicleId = vehicleId;
                        try {
                            // Seçilen aracın satış adedi
                            PreparedStatement selectedCountStmt = conn.prepareStatement(
                                "SELECT COUNT(*) FROM sales WHERE vehicle_id = ?"
                            );
                            selectedCountStmt.setInt(1, vehicleId);
                            ResultSet selectedCountRs = selectedCountStmt.executeQuery();
                            int selectedCount = selectedCountRs.next() ? selectedCountRs.getInt(1) : 0;

                            // Diğer araçların satış adedi
                            PreparedStatement otherCountStmt = conn.prepareStatement(
                                "SELECT COUNT(*) FROM sales WHERE vehicle_id != ?"
                            );
                            otherCountStmt.setInt(1, vehicleId);
                            ResultSet otherCountRs = otherCountStmt.executeQuery();
                            int otherCount = otherCountRs.next() ? otherCountRs.getInt(1) : 0;

                            // Seçilen aracın satış fiyat toplamı
                            PreparedStatement selectedPriceStmt = conn.prepareStatement(
                                "SELECT SUM(sale_price) FROM sales WHERE vehicle_id = ?"
                            );
                            selectedPriceStmt.setInt(1, vehicleId);
                            ResultSet selectedPriceRs = selectedPriceStmt.executeQuery();
                            double selectedTotalPrice = selectedPriceRs.next() ? selectedPriceRs.getDouble(1) : 0;

                            // Diğer araçların satış fiyat toplamı
                            PreparedStatement otherPriceStmt = conn.prepareStatement(
                                "SELECT SUM(sale_price) FROM sales WHERE vehicle_id != ?"
                            );
                            otherPriceStmt.setInt(1, vehicleId);
                            ResultSet otherPriceRs = otherPriceStmt.executeQuery();
                            double otherTotalPrice = otherPriceRs.next() ? otherPriceRs.getDouble(1) : 0;

                            // İlk pie chart: adet bazlı satış dağılımı
                            DefaultPieDataset countDataset = new DefaultPieDataset();
                            countDataset.setValue("Seçilen Araç", selectedCount);
                            countDataset.setValue("Diğer Araçlar", otherCount);
                            JFreeChart countChart = ChartFactory.createPieChart("Satış Adedi Dağılımı", countDataset, true, true, false);
                            // Yüzde dilimlerini göster
                            PiePlot countPlot = (PiePlot) countChart.getPlot();
                            countPlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                                "{0}: {1} adet ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%")));
                            countChartPanel = new ChartPanel(countChart);
                            countChartPanel.setBounds(150, 150, 400, 300);
                            frame.getContentPane().add(countChartPanel);

                            // İkinci pie chart: fiyat bazlı satış dağılımı
                            DefaultPieDataset priceDataset = new DefaultPieDataset();
                            priceDataset.setValue("Seçilen Araç", selectedTotalPrice);
                            priceDataset.setValue("Diğer Araçlar", otherTotalPrice);
                            JFreeChart priceChart = ChartFactory.createPieChart("Satış Fiyatı Dağılımı", priceDataset, true, true, false);
                            // Yüzde dilimlerini göster
                            PiePlot pricePlot = (PiePlot) priceChart.getPlot();
                            pricePlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                                "{0}: {1}₺ ({2})", new DecimalFormat("#,##0.00"), new DecimalFormat("0.0%")));
                            priceChartPanel = new ChartPanel(priceChart);
                            priceChartPanel.setBounds(650, 150, 400, 300);
                            frame.getContentPane().add(priceChartPanel);

                            frame.revalidate();
                            frame.repaint();

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir araç seçin.");
                }
            }
        });

        JButton forecastButton = new JButton("Satış Tahmini (3 yıl)");
        forecastButton.setBounds(625, 80, 200, 30);
        frame.getContentPane().add(forecastButton);
        forecastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clearReportAgainstOthersCharts();
            }
        });
    }
    
    private void clearReportAgainstOthersCharts() {
    	if (countChartPanel != null) {
            frame.getContentPane().remove(countChartPanel);
            countChartPanel = null;
        }
        if (priceChartPanel != null) {
            frame.getContentPane().remove(priceChartPanel);
            priceChartPanel = null;
        }
        frame.revalidate();
        frame.repaint();
    }
    
    private ArrayList<Vehicle> getAllVehicles() {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vehicle");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicleList.add(new Vehicle(
                    rs.getInt("vehicle_id"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("package"),
                    rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicleList;
    }

    public void showFrame() {
        frame.setVisible(true);
    }
}
