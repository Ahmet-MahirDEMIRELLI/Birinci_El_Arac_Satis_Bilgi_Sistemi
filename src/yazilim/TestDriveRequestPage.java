package yazilim;

import javax.swing.*;

import yazilim.requests.TestDriveRequest;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class TestDriveRequestPage {
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
						TestDriveRequestPage window = new TestDriveRequestPage(1, conn);
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

    public TestDriveRequestPage(int userId, Connection conn) {
        this.userId = userId;
        this.conn = conn;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Test Sürüşü Talebi");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBounds(100, 100, 500, 250);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Test Sürüşü Talep Etmek İstediğiniz Araç:");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(Box.createRigidArea(new Dimension(0, 30))); // Üst boşluk
        frame.add(titleLabel);

        vehicleCombo = new JComboBox<>();
        vehicleCombo.setMaximumSize(new Dimension(350, 30));  
        vehicleCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(Box.createRigidArea(new Dimension(0, 10))); 
        frame.add(vehicleCombo);
        loadVehicles();

        JButton submitButton = new JButton("Test Sürüşü Talep Et");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setFocusable(false);
        submitButton.setPreferredSize(new Dimension(170, 40));
        frame.add(Box.createRigidArea(new Dimension(0, 20)));
        frame.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedVehicle = (String) vehicleCombo.getSelectedItem();
                if (selectedVehicle != null) {
                    try {
                        int vehicleId = Integer.parseInt(selectedVehicle.split("-")[0].trim());
                        LocalDate requestDate = LocalDate.now(); 
                        
                        TestDriveRequest request = new TestDriveRequest(userId, vehicleId, requestDate, conn);
                        if (request.processRequest(userId, vehicleId, requestDate)) {
                            JOptionPane.showMessageDialog(frame, "Test sürüşü talebi başarıyla gönderildi.");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Test sürüşü talebi gönderilemedi.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Araç bilgisi okunamadı.");
                    }
                }
            }
        });

        JButton backButton = new JButton("Geri");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFocusable(false);
        frame.add(Box.createRigidArea(new Dimension(0, 20)));
        frame.add(backButton);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); 
                CustomerMainPage mainPage = new CustomerMainPage(userId, conn);
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


