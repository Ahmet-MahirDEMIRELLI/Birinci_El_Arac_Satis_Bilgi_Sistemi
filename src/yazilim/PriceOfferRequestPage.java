package yazilim;

import yazilim.requests.PriceOfferRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class PriceOfferRequestPage {
    private JFrame frame;
    private JComboBox<String> vehicleCombo;
    private int userId;
    private Connection conn;

    public PriceOfferRequestPage(int userId, Connection conn) {
        this.userId = userId;
        this.conn = conn;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Fiyat Teklifi İste");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBounds(100, 100, 500, 250);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Fiyat Teklifi Almak İstediğiniz Araç:");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(Box.createRigidArea(new Dimension(0, 30)));
        frame.add(titleLabel);

        vehicleCombo = new JComboBox<>();
        vehicleCombo.setMaximumSize(new Dimension(350, 30));  
        vehicleCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(Box.createRigidArea(new Dimension(0, 10))); 
        frame.add(vehicleCombo);
        loadVehicles();  
      
        JButton submitButton = new JButton("Fiyat Teklifi Al");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setFocusable(false);
        submitButton.setPreferredSize(new Dimension(160, 40));
        frame.add(Box.createRigidArea(new Dimension(0, 20)));
        frame.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedVehicle = (String) vehicleCombo.getSelectedItem();
                if (selectedVehicle != null) {
                    try {
                        int vehicleId = Integer.parseInt(selectedVehicle.split("-")[0].trim());
                        PriceOfferRequest request = new PriceOfferRequest(userId, vehicleId, LocalDate.now(), conn);
                        if (request.processRequest(userId, vehicleId, LocalDate.now())) {
                            JOptionPane.showMessageDialog(frame, "Teklif isteği başarıyla gönderildi.");
                        } else {
                            JOptionPane.showMessageDialog(frame, "İstek gönderilemedi.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Araç bilgisi okunamadı.");
                    }
                }
            }
        });

        // Geri Butonu
        JButton backButton = new JButton("Geri");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFocusable(false);
        frame.add(Box.createRigidArea(new Dimension(0, 20)));
        frame.add(backButton);
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

    public void showFrame() {
        frame.setVisible(true);
    }
}


