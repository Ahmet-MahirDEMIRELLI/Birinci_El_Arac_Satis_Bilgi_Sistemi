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
				try {
					conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
					CustomerMainPage window = new CustomerMainPage(1, conn);
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
	    frame.setBounds(100, 100, 500, 320);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().setLayout(null);

	    int buttonWidth = 200;
	    int buttonHeight = 40;
	    int buttonX = (frame.getWidth() - buttonWidth) / 2;

	    JButton offerButton = new JButton("Fiyat Teklifi Al");
	    offerButton.setBounds(buttonX, 30, buttonWidth, buttonHeight);
	    offerButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    offerButton.setFocusable(false);
	    offerButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            frame.setVisible(false);
	            PriceOfferRequestPage offerPage = new PriceOfferRequestPage(usrId, conn);
	            offerPage.showFrame();
	        }
	    });
	    frame.getContentPane().add(offerButton);

	    JButton testDriveButton = new JButton("Test Sürüşü Talep Et");
	    testDriveButton.setBounds(buttonX, 80, buttonWidth, buttonHeight);
	    testDriveButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    testDriveButton.setFocusable(false);
	    testDriveButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            frame.setVisible(false);
	            TestDriveRequestPage testDrivePage = new TestDriveRequestPage(usrId, conn);
	            testDrivePage.showFrame();
	        }
	    });
	    frame.getContentPane().add(testDriveButton);

	    JButton changePasswordButton = new JButton("Şifre Değiştir");
	    changePasswordButton.setBounds(buttonX, 130, buttonWidth, buttonHeight);
	    changePasswordButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    changePasswordButton.setFocusable(false);
	    changePasswordButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            new CustomerChangePassword(conn, usrId);
	        }
	    });
	    frame.getContentPane().add(changePasswordButton);

	    JButton returnButton = new JButton("Çıkış Yap");
	    returnButton.setBounds(buttonX, 180, buttonWidth, buttonHeight);
	    returnButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    returnButton.setFocusable(false);
	    returnButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            StartPage start_page = new StartPage(conn);
	            start_page.showFrame();
	            frame.setVisible(false);
	        }
	    });
	    frame.getContentPane().add(returnButton);
	}

	
	
	
	
	public void showFrame() {
		frame.setVisible(true);
	}
}
