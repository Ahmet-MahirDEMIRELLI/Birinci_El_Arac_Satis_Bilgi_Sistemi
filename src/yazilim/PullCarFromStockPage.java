package yazilim;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import yazilim.classes.Vehicle;
import yazilim.classes.VehicleStock;
import yazilim.classes.WarehouseOrDealer;

import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class PullCarFromStockPage {
	private JFrame frame;
	private static Connection conn;
	private WarehouseOrDealer dealer;
	private JComboBox<String> vehicleSelector;
	private ArrayList<VehicleStock> vehicleStockList = new ArrayList<>();
	private ArrayList<Vehicle> vehicles = new ArrayList<>();
	private ArrayList<Integer> vehicleIdMap = new ArrayList<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
					PullCarFromStockPage window = new PullCarFromStockPage(new WarehouseOrDealer(), conn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public PullCarFromStockPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		dealer = new WarehouseOrDealer();
		initialize();
	}

	/**
	 * Create the application.
	 */
	public PullCarFromStockPage(WarehouseOrDealer dlr, Connection parent_conn) {
		dealer = dlr;
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("Stoktan Araç Çekme Sayfası");
	    frame.setBounds(100, 100, 450, 230);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.getContentPane().setLayout(null);

	    vehicleStockList = getVehicleStock();
	    vehicles = getVehicles();

	    vehicleSelector = new JComboBox<>();
	    vehicleSelector.setBounds(30, 30, 380, 30);
	    frame.getContentPane().add(vehicleSelector);

	    // Araçları listeye doldur
	    vehicleIdMap = new ArrayList<>();
	    for (VehicleStock vs : vehicleStockList) {
	        for (Vehicle v : vehicles) {
	            if (v.getVehicleId() == vs.getVehicleId()) {
	                String item = String.format("%s / %s / %d / %s / %.2f TL / Stok: %d",
	                    v.getBrand(),
	                    v.getModel(),
	                    v.getYear(),
	                    v.getPckg(),
	                    v.getPrice(),
	                    vs.getStock());
	                vehicleSelector.addItem(item);
	                vehicleIdMap.add(v.getVehicleId()); // index -> vehicle_id eşlemesi
	                break;
	            }
	        }
	    }

	    JButton pullButton = new JButton("Araç Çek");
	    pullButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    pullButton.setBounds(150, 80, 150, 30);
	    frame.getContentPane().add(pullButton);
	    pullButton.setFocusable(false);

	    pullButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            int selectedIndex = vehicleSelector.getSelectedIndex();
	            if (selectedIndex != -1) {
	                int vehicleId = vehicleIdMap.get(selectedIndex);
	                boolean success = pullVehicleToDealerFromWarehouse(vehicleId);
	                if (success) {
	                    JOptionPane.showMessageDialog(null, "Araç çekme başarılı.");
	                    reloadVehicleData();
	                } else {
	                    JOptionPane.showMessageDialog(null, "Araç çekme başarısız.");
	                }
	            }
	        }
	    });



	    JButton returnButton = new JButton("Çıkış Yap");
	    returnButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            StartPage start_page = new StartPage(conn);
	            start_page.showFrame();
	            frame.setVisible(false);
	        }
	    });
	    returnButton.setFont(new Font("Tahoma", Font.BOLD, 18));
	    returnButton.setBounds(30, 130, 100, 30);
	    frame.getContentPane().add(returnButton);
	    returnButton.setFocusable(false);
	}
	
	public boolean pullVehicleToDealerFromWarehouse(int vehicleId) {
        try {
            PreparedStatement updateWarehouseStmt = conn.prepareStatement(
                "UPDATE stock SET quantity = quantity - 1 WHERE vehicle_id = ? AND location_type = 'warehouse'"
            );
            updateWarehouseStmt.setInt(1, vehicleId);
            int rowsAffected = updateWarehouseStmt.executeUpdate();

            if (rowsAffected > 0) {
                PreparedStatement checkWarehouseStmt = conn.prepareStatement(
                    "SELECT quantity FROM stock WHERE vehicle_id = ? AND location_type = 'warehouse'"
                );
                checkWarehouseStmt.setInt(1, vehicleId);
                ResultSet rsWarehouse = checkWarehouseStmt.executeQuery();

                if (rsWarehouse.next()) {
                    int warehouseQuantity = rsWarehouse.getInt("quantity");
                    if (warehouseQuantity == 0) {
                        PreparedStatement deleteWarehouseStmt = conn.prepareStatement(
                            "DELETE FROM stock WHERE vehicle_id = ? AND location_type = 'warehouse'"
                        );
                        deleteWarehouseStmt.setInt(1, vehicleId);
                        deleteWarehouseStmt.executeUpdate();
                    }
                }

                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM stock WHERE vehicle_id = ? AND location_type = 'dealer'"
                );
                checkStmt.setInt(1, vehicleId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    PreparedStatement updateDealerStmt = conn.prepareStatement(
                        "UPDATE stock SET quantity = quantity + 1 WHERE vehicle_id = ? AND location_type = 'dealer'"
                    );
                    updateDealerStmt.setInt(1, vehicleId);
                    updateDealerStmt.executeUpdate();
                    return true;
                } 
                else {
                    PreparedStatement insertDealerStmt = conn.prepareStatement(
                        "INSERT INTO stock (vehicle_id, location_type, quantity) VALUES (?, 'dealer', 1)"
                    );
                    insertDealerStmt.setInt(1, vehicleId);
                    insertDealerStmt.executeUpdate();
                    return true;
                }
            } else {
            	return false;
            }
        } catch (SQLException ex) {
        	return false;
        }
	}

	private void reloadVehicleData() {
		vehicleStockList = getVehicleStock();
	    vehicles = getVehicles();
	    vehicleSelector.removeAllItems();
	    vehicleIdMap.removeAll(vehicleIdMap);
	    for (VehicleStock vs : vehicleStockList) {
	        for (Vehicle v : vehicles) {
	            if (v.getVehicleId() == vs.getVehicleId()) {
	                String item = String.format("%s / %s / %d / %s / %.2f TL / Stok: %d",
	                    v.getBrand(),
	                    v.getModel(),
	                    v.getYear(),
	                    v.getPckg(),
	                    v.getPrice(),
	                    vs.getStock());
	                vehicleIdMap.add(v.getVehicleId()); // index -> vehicle_id eşlemesi
	                vehicleSelector.addItem(item);
	                break;
	            }
	        }
	    }
	}
	
	public ArrayList<VehicleStock> getVehicleStock() {
	    ArrayList<VehicleStock> vehicleStockList = new ArrayList<>();

	    String query = "SELECT vehicle_id, quantity FROM stock WHERE location_type = 'warehouse'";

	    try {
	        PreparedStatement ps = conn.prepareStatement(query);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            int vehicleId = rs.getInt("vehicle_id");
	            int quantity = rs.getInt("quantity");

	            vehicleStockList.add(new VehicleStock(vehicleId, quantity));
	        }

	        rs.close();
	        ps.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return vehicleStockList;
	}

	
	public ArrayList<Vehicle> getVehicles() {
		ArrayList<Vehicle> vehicles = new ArrayList<>();

	    String query = "SELECT v.vehicle_id, v.brand, v.model, v.year, v.package, v.price " +
	                   "FROM vehicle v " +
	                   "JOIN stock s ON v.vehicle_id = s.vehicle_id " +
	                   "WHERE s.location_type = 'warehouse'";

	    try {
	        PreparedStatement ps = conn.prepareStatement(query);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            int vehicleId = rs.getInt("vehicle_id");
	            String brand = rs.getString("brand");
	            String model = rs.getString("model");
	            int year = rs.getInt("year");
	            String pckg = rs.getString("package");
	            BigDecimal price = rs.getBigDecimal("price");

	            Vehicle vehicle = new Vehicle(vehicleId, brand, model, year, pckg, price);
	            vehicles.add(vehicle);
	        }

	        rs.close();
	        ps.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return vehicles;
	}

	public void showFrame() {
		frame.setVisible(true);
	}
}