package application;

public class Reservation {
	private String num;
	private String stat;
	private String name;
	private String email;
	private String phone;
	
	public Reservation(String r, String v, String n, String e, String p) {
		this.num = r;
		this.stat = v;
		this.name = n;
		this.email = e;
		this.phone = p;
	}
	public String getRoom() {return num;}
	public String getStatus() {return stat;}
	public String getName() {return name;}
	public String getEmail() {return email;}
	public String getPhone() {return phone;}
	
	public void setRoom(String value) {
		num = value;
	}
	public void setStatus(String value) {
		stat = value;
	}
	public void setName(String value) {
		name = value;
	}
	public void setEmail(String value) {
		email = value;
	}
	public void setPhone(String value) {
		phone = value;;
	}
	
	
	
	
	
	

}
