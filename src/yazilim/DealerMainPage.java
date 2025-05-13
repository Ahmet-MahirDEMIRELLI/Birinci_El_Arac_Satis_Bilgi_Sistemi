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

public class DealerMainPage {
	private JFrame frame;
	private static Connection conn;
	private int dealerId;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
					DealerMainPage window = new DealerMainPage(1, conn);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public DealerMainPage() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
		dealerId = 1;
		initialize();
	}

	/**
	 * Create the application.
	 */
	public DealerMainPage(int dlrId, Connection parent_conn) {
		dealerId = dlrId;
		conn = parent_conn;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	    frame = new JFrame();
	    frame.setTitle("Bayi Ana Sayfası");
	    frame.setBounds(100, 100, 500, 500);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().setLayout(null);

	    int buttonWidth = 220;
	    int buttonHeight = 40;
	    int buttonX = (frame.getWidth() - buttonWidth) / 2 - 8;
	    int y = 30;

	    JButton showRequestsButton = new JButton("Talepleri Göster");
	    showRequestsButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    showRequestsButton.setBounds(buttonX, y, buttonWidth, buttonHeight);
	    showRequestsButton.setFocusable(false);
	    showRequestsButton.addActionListener(e -> new DealerRequestPage(conn, dealerId));
	    frame.getContentPane().add(showRequestsButton);

	    y += 60;
	    
	    
	    JButton viewStockButton = new JButton("Stokları Görüntüle");
        viewStockButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        viewStockButton.setBounds(buttonX, y, buttonWidth, buttonHeight);
        frame.getContentPane().add(viewStockButton);
        viewStockButton.setFocusable(false);

        viewStockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ShowCarStockPage(conn); // yeni sayfayı aç
            }
        });
        
        y += 60;

	    JButton getFromStockButton = new JButton("Stoktan Araç Çek");
	    getFromStockButton.setBounds(buttonX, y, buttonWidth, buttonHeight);
	    getFromStockButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    getFromStockButton.setFocusable(false);
	    getFromStockButton.addActionListener(e -> {
	        frame.setVisible(false);
	        PullCarFromStockPage pullCarFromStockPage = new PullCarFromStockPage(dealerId, conn);
	        pullCarFromStockPage.showFrame();
	    });
	    frame.getContentPane().add(getFromStockButton);

	    y += 60;

	    JButton changePasswordButton = new JButton("Şifre Değiştir");
	    changePasswordButton.setBounds(buttonX, y, buttonWidth, buttonHeight);
	    changePasswordButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    changePasswordButton.setFocusable(false);
	    changePasswordButton.addActionListener(e -> new DealerChangePassword(conn, dealerId));
	    frame.getContentPane().add(changePasswordButton);

	    y += 60;

	    JButton approveOrdersButton = new JButton("Satış Onayı");
	    approveOrdersButton.setBounds(buttonX, y, buttonWidth, buttonHeight);
	    approveOrdersButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    approveOrdersButton.setFocusable(false);
	    approveOrdersButton.addActionListener(e -> new DealerOrderApprovalPage(conn, dealerId));
	    frame.getContentPane().add(approveOrdersButton);

	    y += 60;

	    JButton returnButton = new JButton("Çıkış Yap");
	    returnButton.setBounds(buttonX, y, buttonWidth, buttonHeight);
	    returnButton.setFont(new Font("Tahoma", Font.BOLD, 16));
	    returnButton.setFocusable(false);
	    returnButton.addActionListener(e -> {
	        StartPage start_page = new StartPage(conn);
	        start_page.showFrame();
	        frame.setVisible(false);
	    });
	    frame.getContentPane().add(returnButton);
	}

	public void showFrame() {
		frame.setVisible(true);
	}
}
