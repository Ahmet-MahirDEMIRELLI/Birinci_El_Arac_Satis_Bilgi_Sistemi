package yazilim.tests;

import org.junit.jupiter.api.*;

import yazilim.AddCarToStockPage;
import yazilim.PullCarFromStockPage;
import yazilim.classes.WarehouseOrDealer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testAddCarToStock {
    private static Connection conn;
    private AddCarToStockPage page;
    private WarehouseOrDealer warehouse = new WarehouseOrDealer(2, "warehouse@example.com", "WAREHOUSE");

    @BeforeAll
    public static void setupConnection() throws Exception {
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
    }

    @BeforeEach
    public void init() {
        page = new AddCarToStockPage(warehouse, conn);
    }

    @Test
    public void testAddVehicle_1() {
        int result = page.addCarToStock("Ford", "Focus", 2025, "Full", BigDecimal.valueOf(1000000.00), 5);
        assertTrue(result == 1, "Ford Focus 2025 Full paket ekleme başarılı olmalı.");
        result = page.addCarToStock("Ford", "Focus", 2025, "Full", BigDecimal.valueOf(1000000.00), 5);
        assertTrue(result == 0, "Ford Focus 2025 Full paket ekleme başarısız olmalı.");
    }

    @Test
    public void testAddVehicle_2() {
    	int result = page.addCarToStock("Volkswagen", "Tiguan", 2021, "Full", BigDecimal.valueOf(1500000.00), 2);
        assertTrue(result == 1, "Volkswagen Tiguan 2021 Full paket ekleme başarılı olmalı.");
       
        int expectedStock = 2;
        int actualStock = getWarehouseStockForVehicle(getVehicleIdFromFeatures("Volkswagen", "Tiguan", 2021, "Full"));
        assertEquals(expectedStock, actualStock, "Depodaki stok 2 olmalı.");
    }

    @AfterAll
    public static void closeConnection() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
    
    private int getVehicleIdFromFeatures(String brand, String model, int year, String pckg) {
        int vehicle_id = 0;
        String query = "SELECT vehicle_id FROM vehicles WHERE brand = ? AND model = ? AND year = ? AND package = ?;";
        PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, brand);
	        stmt.setString(2, model);
	        stmt.setInt(3, year);
	        stmt.setString(4, pckg);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	        	vehicle_id = rs.getInt("vehicle_id");
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return vehicle_id;
    }
    
    private int getWarehouseStockForVehicle(int vehicleId) {
        int stock = -1;
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT quantity FROM stock WHERE vehicle_id = ? AND location_type = 'warehouse'")) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Veritabanı hatası: " + e.getMessage());
        }
        return stock;
    }
}