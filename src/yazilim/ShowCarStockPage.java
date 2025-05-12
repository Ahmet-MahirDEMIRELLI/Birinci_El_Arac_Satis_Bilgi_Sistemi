package yazilim;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import yazilim.classes.Vehicle;
import yazilim.classes.VehicleStock;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ShowCarStockPage {
    private JFrame frame;
    private JTable warehouseTable;
    private JTable dealerTable;
    private Connection conn;

    public ShowCarStockPage(Connection conn) {
        this.conn = conn;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Depo ve Bayi Araç Stokları");
        frame.setSize(800, 600);
        frame.setLayout(new GridLayout(4, 1));

         // Başlıklar
        JLabel warehouseLabel = new JLabel("Depo Stokları", SwingConstants.CENTER);
        warehouseLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        JLabel dealerLabel = new JLabel("Bayi Stokları", SwingConstants.CENTER);
        dealerLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        // Tablo modelleri
        DefaultTableModel warehouseModel = new DefaultTableModel(new Object[]{"Marka", "Model", "Yıl", "Paket", "Fiyat", "Adet"}, 0);
        DefaultTableModel dealerModel = new DefaultTableModel(new Object[]{"Marka", "Model", "Yıl", "Paket", "Fiyat", "Adet"}, 0);

        warehouseTable = new JTable(warehouseModel);
        dealerTable = new JTable(dealerModel);

        loadStockData("warehouse", warehouseModel);
        loadStockData("dealer", dealerModel);

        frame.add(new JScrollPane(warehouseTable), BorderLayout.NORTH);
        frame.add(new JScrollPane(dealerTable), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void loadStockData(String locationType, DefaultTableModel model) {
        try {
            String query = "SELECT v.brand, v.model, v.year, v.package, v.price, s.quantity " +
                           "FROM stock s JOIN vehicle v ON s.vehicle_id = v.vehicle_id " +
                           "WHERE s.location_type = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, locationType);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String brand = rs.getString("brand");
                String modelStr = rs.getString("model");
                int year = rs.getInt("year");
                String pckg = rs.getString("package");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                model.addRow(new Object[]{brand, modelStr, year, pckg, price, quantity});
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}