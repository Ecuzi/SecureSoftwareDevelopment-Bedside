package application;

public class ServiceReq {
	private int room;
	private String sstat;
	public ServiceReq(int r, String s) {
		this.room = r;
		this.sstat = s;
	}
	public int getRoom() {return room;}
	public String getSstat() {return sstat;}
	public void setRoom(int value) {
		room = value;
	}
	public void setSstat(String value) {
		sstat = value;
	}

}
