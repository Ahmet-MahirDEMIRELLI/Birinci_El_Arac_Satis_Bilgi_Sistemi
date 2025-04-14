package yazilim;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class StartPage {
	private JFrame frame;
	private static Connection conn;
	private JTextField nameField;
	private JLabel nameLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Connection dummyConn = null;
				try {
					StartPage window = new StartPage(dummyConn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StartPage(Connection parent_conn) {
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Start Page");
		frame.setBounds(100, 100, 373, 293);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(30, 70, 125, 25);
		frame.getContentPane().add(nameField);
		nameLabel = new JLabel("Name:");
		nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		nameLabel.setBounds(30, 50, 100, 15);
		frame.getContentPane().add(nameLabel);

		
		String query = "SELECT name FROM customers WHERE customer_id = 1;";
		PreparedStatement statement;
		try {
			statement = conn.prepareStatement(query);
			ResultSet r = statement.executeQuery();
			if (r.next()) {
				nameField.setText(r.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void showFrame() {
		frame.setVisible(true);
	}
}