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
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "password");
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
		frame.setBounds(100, 100, 650, 370);
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
		offerButton.setBounds(100, 30, 200, 30);
		frame.getContentPane().add(offerButton);
		
		JButton offerStatusButton = new JButton("Fiyat Tekliflerim");
		offerStatusButton.setFocusable(false);
		offerStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				PriceOfferStatusPage offerStatusPage = new PriceOfferStatusPage(usrId, conn);
				offerStatusPage.showFrame();
			}
		});
		offerStatusButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		offerStatusButton.setBounds(350, 30, 200, 30);
		frame.getContentPane().add(offerStatusButton);
		
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
		testDriveButton.setBounds(100, 80, 200, 30);
		frame.getContentPane().add(testDriveButton);
		
		JButton testDriveStatusButton = new JButton("Test Sürüşlerim");
		testDriveStatusButton.setFocusable(false);
		testDriveStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				TestDriveStatusPage testDriveStatusPage = new TestDriveStatusPage(usrId, conn);
				testDriveStatusPage.showFrame();
			}
		});
		testDriveStatusButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		testDriveStatusButton.setBounds(350, 80, 200, 30);
		frame.getContentPane().add(testDriveStatusButton);
		
		JButton orderButton = new JButton("Sipariş Ver");
		orderButton.setFocusable(false);
		orderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				OrderRequestPage orderRequestPage = new OrderRequestPage(usrId, conn);
				orderRequestPage.showFrame();
			}
		});
		orderButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		orderButton.setBounds(100, 130, 200, 30);
		frame.getContentPane().add(orderButton);
		
		JButton orderStatusButton = new JButton("Sipariş Durumlarım");
		orderStatusButton.setFocusable(false);
		orderStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				OrderStatusPage orderStatusPage = new OrderStatusPage(usrId, conn);
				orderStatusPage.showFrame();
			}
		});
		orderStatusButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		orderStatusButton.setBounds(350, 130, 200, 30);
		frame.getContentPane().add(orderStatusButton);
		
		JButton myCarsButton = new JButton("Arabalarım");
		myCarsButton.setFocusable(false);
		myCarsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				CustomerCarPage customerCarPage = new CustomerCarPage(usrId, conn);
				customerCarPage.showFrame();
			}
		});
		myCarsButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		myCarsButton.setBounds(100, 180, 200, 30);
		frame.getContentPane().add(myCarsButton);
		
		JButton changePasswordButton = new JButton("Şifre Değiştir");
		changePasswordButton.setFocusable(false);
		changePasswordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CustomerChangePassword(conn, usrId);
			}
		});
		changePasswordButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		changePasswordButton.setBounds(350, 180, 200, 30);
		frame.getContentPane().add(changePasswordButton);
		
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
		returnButton.setBounds(225, 230, 200, 30);
		frame.getContentPane().add(returnButton);
		returnButton.setFocusable(false);
		
	}

	public void showFrame() {
		frame.setVisible(true);
	}
}
