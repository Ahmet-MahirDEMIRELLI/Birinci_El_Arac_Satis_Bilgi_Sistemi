package yazilim;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class UserRegisterPage {
	private Connection conn;
	private JFrame frame;
	private JTextField nameField;
	private JTextField surnameField;
	private JTextField phoneField;
	private JTextField emailField;
	private JTextField passwordField;
	private JTextField addressField;
	
	private JLabel nameLabel;
	private JLabel surnameLabel;
	private JLabel phoneLabel;
	private JLabel emailLabel;
	private JLabel passwordLabel;
	private JLabel addressLabel;
	private JLabel tipLabel;
	private int newId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Connection dummyConn = null;
				try {
					UserRegisterPage window = new UserRegisterPage(dummyConn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public UserRegisterPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		initialize();
	}

	/**
	 * Create the application.
	 */
	public UserRegisterPage(Connection parent_conn) {
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Kullanıcı Kayıt  Sayfası");
		frame.setBounds(100, 100, 390, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton registerButton = new JButton("Kayıt Ol");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = "SELECT register_user(?,?,?,?,?,?);";
				PreparedStatement statement;
				try {
					if (nameField.getText().length() >= 3 && surnameField.getText().length() >= 3 && addressField.getText().length() >= 3
							&& phoneField.getText().length() >= 3 && passwordField.getText().length() >= 3) {
						if(phoneField.getText().length() == 10) {
							statement = conn.prepareStatement(query);
							statement.setString(1, nameField.getText());
							statement.setString(2, surnameField.getText());
							statement.setString(3, phoneField.getText());
							statement.setString(4, emailField.getText());
							statement.setString(5, passwordField.getText());
							statement.setString(6, addressField.getText());
							ResultSet r = statement.executeQuery();
							if (r.next()) {
								JOptionPane.showMessageDialog(null,
										"Your new id is : " + r.getInt(1));
								newId = r.getInt(1);
								UserMainPage pg = new UserMainPage(newId, conn);
								pg.showFrame();
								frame.setVisible(false);
							}
						}
						else {
							JOptionPane.showMessageDialog(null, "Telefon numarası uzunluğu 10 olmalı.");
						}
					} else {
						JOptionPane.showMessageDialog(null, "3 karakterden kısa bir veri girilemez.");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		registerButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		registerButton.setBounds(30, 250, 125, 35);
		frame.getContentPane().add(registerButton);
		registerButton.setFocusable(false);

		JButton returnButton = new JButton("Geri Dön");
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartPage startPage = new StartPage(conn);
				startPage.showFrame();
				frame.setVisible(false);
			}
		});
		returnButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		returnButton.setBounds(230, 250, 125, 35);
		frame.getContentPane().add(returnButton);
		returnButton.setFocusable(false);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(30, 70, 125, 25);
		frame.getContentPane().add(nameField);
		nameLabel = new JLabel("Ad:");
		nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		nameLabel.setBounds(30, 50, 100, 15);
		frame.getContentPane().add(nameLabel);
		
		surnameField = new JTextField();
		surnameField.setColumns(10);
		surnameField.setBounds(230, 70, 125, 25);
		frame.getContentPane().add(surnameField);
		surnameLabel = new JLabel("Soyad:");
		surnameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		surnameLabel.setBounds(230, 50, 100, 15);
		frame.getContentPane().add(surnameLabel);
		
		phoneField = new JTextField();
		phoneField.setColumns(10);
		phoneField.setBounds(30, 130, 125, 25);
		frame.getContentPane().add(phoneField);
		phoneLabel = new JLabel("Telefon:");
		phoneLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		phoneLabel.setBounds(30, 110, 100, 15);
		frame.getContentPane().add(phoneLabel);
		
		addressField = new JTextField();
		addressField.setColumns(10);
		addressField.setBounds(230, 130, 125, 25);
		frame.getContentPane().add(addressField);
		addressLabel = new JLabel("Adres:");
		addressLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		addressLabel.setBounds(230, 110, 100, 15);
		frame.getContentPane().add(addressLabel);
		
		emailField = new JTextField();
		emailField.setColumns(10);
		emailField.setBounds(30, 190, 125, 25);
		frame.getContentPane().add(emailField);
		emailLabel = new JLabel("E-Posta:");
		emailLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		emailLabel.setBounds(30, 170, 100, 15);
		frame.getContentPane().add(emailLabel);
		
		passwordField = new JTextField();
		passwordField.setColumns(10);
		passwordField.setBounds(230, 190, 125, 25);
		frame.getContentPane().add(passwordField);
		passwordLabel = new JLabel("Şifre:");
		passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		passwordLabel.setBounds(230, 170, 104, 14);
		frame.getContentPane().add(passwordLabel);

		tipLabel = new JLabel("Not: Her alan en az 3 karakter olmalı (Telefon numarası 10 karakter olmalı)");
		tipLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
		tipLabel.setBounds(10, 10, 360, 30);
		frame.getContentPane().add(tipLabel);
	}

	public void showFrame() {
		frame.setVisible(true);
	}
}