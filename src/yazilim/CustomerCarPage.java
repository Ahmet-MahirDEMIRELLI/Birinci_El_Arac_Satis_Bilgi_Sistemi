package yazilim;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerCarPage {
    private JFrame frame;
    public JTable table;
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
					CustomerCarPage window = new CustomerCarPage(1, conn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

    public CustomerCarPage(int userId, Connection conn) {
        this.userId = userId;
        this.conn = conn;
        initialize();
        loadPurchasedCars();
    }

    private void initialize() {
        frame = new JFrame("Satın Alınan Araçlarım");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        String[] columnNames = {"Araç", "Satın Alım Tarihi", "Fiyat"};
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

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPurchasedCars() {
        String query = """
            SELECT s.sale_date, s.sale_price, v.brand, v.model, v.year
            FROM sales s
            JOIN vehicle v ON s.vehicle_id = v.vehicle_id
            WHERE s.user_id = ?
            ORDER BY s.sale_date DESC
        """;

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            while (rs.next()) {
                String vehicle = rs.getString("brand") + " " + rs.getString("model") + " (" + rs.getInt("year") + ")";
                Date saleDate = rs.getDate("sale_date");
                String price = String.format("%.2f TL", rs.getDouble("sale_price"));

                model.addRow(new Object[]{vehicle, saleDate.toString(), price});
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"Henüz araç satın alınmamış.", "", ""});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Araçlar yüklenirken hata oluştu.");
        }
    }

    public void showFrame() {
        frame.setVisible(true);
    }
}

