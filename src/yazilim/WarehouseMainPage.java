package yazilim;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class WarehouseMainPage {
	private JFrame frame;
	private static Connection conn;
	private int warehouseId;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
					WarehouseMainPage window = new WarehouseMainPage(1, conn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public WarehouseMainPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		warehouseId = 1;
		initialize();
	}

	/**
	 * Create the application.
	 */
	public WarehouseMainPage(int wareHouseId, Connection parent_conn) {
		warehouseId = wareHouseId;
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Depo Ana Sayfası");
		frame.setBounds(100, 100, 450, 230);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton newCarButton = new JButton("Yeni Araç Ekle");
		newCarButton.setFocusable(false);
		newCarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				AddCarToStockPage addCarToStockPage = new AddCarToStockPage(warehouseId, conn);
				addCarToStockPage.showFrame();
			}
		});
		newCarButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		newCarButton.setBounds(150, 30, 200, 30);
		frame.getContentPane().add(newCarButton);
		
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

	public void showFrame() {
		frame.setVisible(true);
	}
}