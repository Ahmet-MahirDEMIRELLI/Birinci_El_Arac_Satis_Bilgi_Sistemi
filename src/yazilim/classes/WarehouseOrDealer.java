package yazilim.classes;

import java.sql.Timestamp;

public class WarehouseOrDealer {
    private int id;
    private String email;
    private String type;

    public WarehouseOrDealer(int id, String email, String type) {
        this.id = id;
        this.email = email;
        this.type = type;
    }
    
    public WarehouseOrDealer() {
    	
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    @Override
	public String toString() {
    	if(type.equals("WAREHOUSE")) {
    		return "Warehouse{" +
    	            "id=" + id +
    	            ", email='" + email + '\'' +
    	            '}';
    	}
	    
    	return "Dealer{" +
		        "id=" + id +
		        ", email='" + email + '\'' +
		        '}';
	}
}
