package yazilim;

import javax.swing.*;

import yazilim.requests.PriceOfferRequest;

import java.awt.EventQueue;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class PriceOfferRequestPage {
    private JFrame frame;
    private JComboBox<String> vehicleCombo;
    private int userId;
    private static Connection conn;
    
    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
					PriceOfferRequestPage window = new PriceOfferRequestPage(1, conn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

    public PriceOfferRequestPage(int userId, Connection conn) {
        this.userId = userId;
        this.conn = conn;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Fiyat Teklifi İste");
        frame.setBounds(100, 100, 450, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel label = new JLabel("Araç Seçin:");
        label.setBounds(30, 30, 100, 25);
        frame.getContentPane().add(label);

        //Araç Seçimi
        vehicleCombo = new JComboBox<>();
        vehicleCombo.setBounds(140, 30, 250, 25);
        frame.getContentPane().add(vehicleCombo);
        loadVehicles();  // Araçları yükle
        
        int vehicleWidth = 250;
        int vehicleLabelStart = 140;

        JButton submitButton = new JButton("Fiyat Teklifi Al");
        submitButton.setFocusable(false);
        submitButton.setBounds(vehicleLabelStart+(vehicleWidth-120)/2, 80, 120, 30);
        frame.getContentPane().add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedVehicle = (String) vehicleCombo.getSelectedItem();
                if (selectedVehicle != null) {
                    try {
                        int vehicleId = Integer.parseInt(selectedVehicle.split("-")[0].trim());
                        PriceOfferRequest request = new PriceOfferRequest(userId, vehicleId, LocalDate.now(), conn);
                        if (request.processRequest(userId, vehicleId, LocalDate.now())) {
                            JOptionPane.showMessageDialog(frame, "Teklif isteği başarıyla gönderildi.");
                            frame.dispose();  
                        } else {
                            JOptionPane.showMessageDialog(frame, "İstek gönderilemedi.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Araç bilgisi okunamadı.");
                    }
                }
            }
        });
        
        JButton backButton = new JButton("Geri");
        backButton.setFocusable(false);
        backButton.setBounds(30, 150, 100, 30);
        frame.getContentPane().add(backButton);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); 
                CustomerMainPage mainPage = new CustomerMainPage(userId, conn); // Ana sayfaya dön
                mainPage.showFrame();
            }
        });
    }

    private void loadVehicles() {
        try {
            String query = "SELECT vehicle_id, brand, model, year FROM vehicle";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("vehicle_id");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                int year = rs.getInt("year");
                vehicleCombo.addItem(id + " - " + brand + " " + model + " (" + year + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Araç bilgileri yüklenemedi. Lütfen tekrar deneyin.");
        }
    }

    // Frame'i göster
    public void showFrame() {
        frame.setVisible(true);
    }
}

