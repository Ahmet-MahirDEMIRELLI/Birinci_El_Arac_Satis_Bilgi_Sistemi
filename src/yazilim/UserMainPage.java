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

public class UserMainPage {
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
					UserMainPage window = new UserMainPage(1, dummyConn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public UserMainPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		usrId = 1;
		initialize();
	}

	/**
	 * Create the application.
	 */
	public UserMainPage(int userId, Connection parent_conn) {
		usrId = userId;
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Kullanıcı Ana Sayfası");
		frame.setBounds(100, 100, 450, 230);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
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