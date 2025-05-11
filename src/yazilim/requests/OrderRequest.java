package yazilim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class OrderRequest extends CustomerRequest{
	private Connection conn;
	
	public OrderRequest(int userId, int vehicleId, LocalDate requestDate, Connection conn) {
		super(userId, vehicleId, requestDate);
	}

	@Override
	public boolean processRequest(int userId, int vehicleId, LocalDate requestDate) {
        String priceOfferQuery = "SELECT offer_date FROM price_offers WHERE user_id = ? AND vehicle_id = ? ORDER BY offer_date DESC";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(priceOfferQuery);
            stmt.setInt(1, userId);
            stmt.setInt(2, vehicleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                LocalDate offerDate = rs.getDate("offer_date").toLocalDate();
                LocalDate currentDate = LocalDate.now();
                
                if (offerDate.plusDays(30).isBefore(currentDate)) {
                    System.out.println("The price offer has expired. Order request is invalid.");
                    return false; 
                }
                
                String query = "INSERT INTO requests (user_id, vehicle_id, request_type, request_date, status) VALUES (?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, vehicleId);
                stmt.setString(3, "order"); 
                stmt.setDate(4, java.sql.Date.valueOf(requestDate));
                stmt.setString(5, "pending");
                stmt.executeUpdate(); 
                return true; 
            } else {
                System.out.println("No price offer found for this vehicle.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
}
