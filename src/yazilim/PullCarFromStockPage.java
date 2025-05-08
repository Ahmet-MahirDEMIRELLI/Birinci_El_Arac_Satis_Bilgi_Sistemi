package yazilim;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import yazilim.classes.Vehicle;
import yazilim.classes.VehicleStock;

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
	private int dealerId;
	ArrayList<VehicleStock> vehicleStockList = new ArrayList<>();
	ArrayList<Vehicle> vehicles = new ArrayList<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Connection dummyConn = null;
				try {
					PullCarFromStockPage window = new PullCarFromStockPage(1, dummyConn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public PullCarFromStockPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		dealerId = 1;
		initialize();
	}

	/**
	 * Create the application.
	 */
	public PullCarFromStockPage(int dlrId, Connection parent_conn) {
		dealerId = dlrId;
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		vehicleStockList = getVehicleStock();
		vehicles = getVehicles();
		JComboBox<String> vehicleSelector = new JComboBox<>();
		vehicleSelector.setBounds(30, 30, 380, 30);
		frame.getContentPane().add(vehicleSelector);

		// Araçları listeye doldur
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
		            break;
		        }
		    }
		}
		
		
		JButton returnButton = new JButton("Çıkış Yap");
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartPage start_page;
				start_page = new StartPage(conn);
				start_page.showFrame();
				frame.setVisible(false);
			}
		});
		returnButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		returnButton.setBounds(30, 30, 100, 30);
		frame.getContentPane().add(returnButton);
		returnButton.setFocusable(false);
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