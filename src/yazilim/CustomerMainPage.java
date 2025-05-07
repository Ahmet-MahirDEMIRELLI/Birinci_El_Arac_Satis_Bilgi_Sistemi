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

public class CustomerMainPage {
	private JFrame frame;
	private static Connection conn;
	private int usrId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Connection dummyConn = null;
				try {
					CustomerMainPage window = new CustomerMainPage(1, dummyConn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public CustomerMainPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		usrId = 1;
		initialize();
	}

	/**
	 * Create the application.
	 */
	public CustomerMainPage(int userId, Connection parent_conn) {
		usrId = userId;
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Müşteri Ana Sayfası");
		frame.setBounds(100, 100, 450, 230);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton offerButton = new JButton("Fiyat Teklifi Al");
		offerButton.setFocusable(false);
		offerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				PriceOfferRequestPage offerPage = new PriceOfferRequestPage(usrId, conn);
				offerPage.showFrame();
			}
		});
		offerButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		offerButton.setBounds(150, 30, 200, 30);
		frame.getContentPane().add(offerButton);
		
		
		JButton testDriveButton = new JButton("Test Sürüşü Talep Et");
		testDriveButton.setFocusable(false);
		testDriveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				TestDriveRequestPage testDrivePage = new TestDriveRequestPage(usrId, conn);
				testDrivePage.showFrame();
			}
		});
		testDriveButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		testDriveButton.setBounds(150, 80, 200, 30);
		frame.getContentPane().add(testDriveButton);
		
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
		returnButton.setBounds(150, 130, 200, 30);
		frame.getContentPane().add(returnButton);
		returnButton.setFocusable(false);
		
	}

	public void showFrame() {
		frame.setVisible(true);
	}
}