package yazilim.tests;

import org.junit.jupiter.api.*;
import yazilim.PullCarFromStockPage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testPullVehicleToDealerFromWarehouse {
    private static Connection conn;
    private PullCarFromStockPage page;

    @BeforeAll
    public static void setupConnection() throws Exception {
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YazilimMuhProje", "postgres", "12345");
    }

    @BeforeEach
    public void init() {
        page = new PullCarFromStockPage(1, conn);
    }

    @Test
    public void testPullVehicle_1() {
        int vehicleId = 1;
        boolean result = page.pullVehicleToDealerFromWarehouse(vehicleId);
        assertTrue(result, "Araç çekme işlemi başarılı olmalı. (AraÇ ID: 1)");
        
        int expectedStock = 9;
        int actualStock = getWarehouseStockForVehicle(vehicleId);
        assertEquals(expectedStock, actualStock, "Depodaki stok 9 olmalı.");
    }

    @Test
    public void testPullVehicle_2() {
        int vehicleId = 2;
        boolean result = page.pullVehicleToDealerFromWarehouse(vehicleId);
        assertFalse(result, "Araç çekme işlemi başarısız olmalı. (AraÇ ID: 2)");
    }

    @Test
    public void testPullVehicle_3() {
        int vehicleId = 5;
        boolean result = page.pullVehicleToDealerFromWarehouse(vehicleId);
        assertTrue(result, "Araç çekme işlemi başarılı olmalı (AraÇ ID: 5)");
        
        int remainingRows = getWarehouseStockRowCount(vehicleId);
        assertEquals(0, remainingRows, "Depoda bu araçtan kayıt kalmamalı (Araç ID: 5)");
    }

    @AfterAll
    public static void closeConnection() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
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
    
    private int getWarehouseStockRowCount(int vehicleId) {
        int count = -1;
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM stock WHERE vehicle_id = ? AND location_type = 'warehouse'")) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Veritabanı hatası: " + e.getMessage());
        }
        return count;
    }
}