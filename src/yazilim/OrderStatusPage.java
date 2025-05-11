package yazilim;

import yazilim.requests.OrderRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderStatusPage {
    private JFrame frame;
    private JTable table;
    private Connection conn;
    private int userId;

    public OrderStatusPage(int userId, Connection conn) {
        this.userId = userId;
        this.conn = conn;
        initialize();
        loadOrderRequests();
    }

    private void initialize() {
        frame = new JFrame("Sipariş Taleplerim");
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

    private void loadOrderRequests() {
        String query = """
            SELECT r.request_date, r.status, v.brand, v.model, v.year
            FROM requests r
            JOIN vehicle v ON r.vehicle_id = v.vehicle_id
            WHERE r.user_id = ? AND r.request_type = 'order'
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
            case "accepted" -> "<html><span style='color:green;'>Sipariş Onaylandı</span></html>";
            case "rejected" -> "<html><span style='color:red;'>Stokta Yok</span></html>";
            default         -> status;
        };
    }

    public void showFrame() {
        frame.setVisible(true);
    }
}

