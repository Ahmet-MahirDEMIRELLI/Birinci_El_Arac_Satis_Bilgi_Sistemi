package yazilim;

import yazilim.requests.TestDriveRequest;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TestDriveStatusPage {
	private JFrame frame;
    private JTable table;
    private Connection conn;
    private int userId;

    public TestDriveStatusPage(int userId, Connection conn) {
        this.userId = userId;
        this.conn = conn;
        initialize();
        loadTestDriveRequests();
    }

    private void initialize() {
        frame = new JFrame("Test Sürüş Taleplerim");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        String[] columnNames = {"Araç", "Tarih", "Durum"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Geri");
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            frame.dispose();
            CustomerMainPage mainPage = new CustomerMainPage(userId, conn);
            mainPage.showFrame();
        });

        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        bottomPanel.add(backButton); 
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }
    

    private void loadTestDriveRequests() {
        String query = """
            SELECT r.request_date, r.status, v.brand, v.model, v.year
            FROM requests r
            JOIN vehicle v ON r.vehicle_id = v.vehicle_id
            WHERE r.user_id = ? AND r.request_type = 'test_drive'
            ORDER BY r.request_date DESC
        """;

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            while (rs.next()) {
                String vehicleInfo = rs.getString("brand") + " " + rs.getString("model") + " (" + rs.getInt("year") + ")";
                Date date = rs.getDate("request_date");
                String status = translateStatus(rs.getString("status"));
                model.addRow(new Object[]{vehicleInfo, date.toString(), status});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Veriler alınırken hata oluştu.");
        }
    }

    private String translateStatus(String status) {
        return switch (status) {
            case "pending"  -> "<html><span style='color:orange;'>Beklemede</span></html>";
            case "accepted" -> "<html><span style='color:green;'>Sürüş Yapıldı</span></html>";
            case "rejected" -> "<html><span style='color:red;'>Talep Reddedildi/Stokta Yok</span></html>";
            default         -> status;
        };
    }

    public void showFrame() {
        frame.setVisible(true);
    }
}
