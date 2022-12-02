package application;
public class ServerConnection {
	
	private static BedsideServer server = null;
	
	public static void openConnection(String URL, String username, String password)
	{
		server = new BedsideServer(URL, username, password);
	}
	
	public static int accessLevel(String emailAddress, String password)
	{
		return server.accessLevel(emailAddress, password);
	}
	
	public static boolean cancelService(String emailAddress, String password, int reservationID, BedsideServer.ServiceType serviceType)
	{
		return server.cancelService(emailAddress, password, reservationID, serviceType);
	}
	
	public static boolean checkOut(String emailAddress, int roomNumber)
	{
		return server.checkOut(emailAddress, roomNumber);
	}
	
	public static BedsideServer.Reservation getReservation(String emailAddress, int reservationID)
	{
		return server.getReservation(emailAddress, reservationID);
	}
	
	public static BedsideServer.Reservation[] getServiceRequests(String emailAddress, String password)
	{
		return server.getServiceRequests(emailAddress, password);
	}
	public static boolean isRoomAvailable(int roomNumber)
	{
		return server.isRoomAvailable(roomNumber);
	}
	public static Reservation[] getRooms(String emailAddress, String password)
	{
		Reservation[] rooms = new Reservation[9];
		
		BedsideServer.Reservation[] reservations = server.getReservations(emailAddress, password);
		
		for (BedsideServer.Reservation res: reservations)
			rooms[res.roomNumber()] = new Reservation(String.valueOf(res.roomNumber()), "occupied", res.guestName(), res.emailAddress(), res.phoneNumber());
		
		for (int i = 0; i < 9; ++i)
			if (rooms[i] == null)
				rooms[i] = new Reservation(String.valueOf(i), "unoccupied", null, null, null);
		
		return rooms;
	}
	
	public static boolean isConnected() { return server.isConnected(); }
	
	public static boolean requestService(String emailAddress, int roomNumber, BedsideServer.ServiceType serviceType)
	{
		return server.requestService(emailAddress, roomNumber, serviceType);
	}
	public static Reservation[] serviceRequests(String emailAddress, String password)
    {
        BedsideServer.Reservation[] serviceRequests = server.getReservations(emailAddress, password);

        Reservation[] answer = new Reservation[serviceRequests.length];

        int n = 0;
        for (BedsideServer.Reservation request: serviceRequests)
        {
            String serviceType = "";

            if (request.cleaningServiceRequested())
                serviceType += "clean";

            if (request.roomServiceRequested())
            {
                if (!serviceType.isEmpty())
                    serviceType += " / ";

                serviceType += "room";
            }

            if (serviceType.isEmpty())
                serviceType = "none";

            answer[n++] = new Reservation(String.valueOf(request.roomNumber()), serviceType, request.guestName(), request.emailAddress(), serviceType);
        }

        return answer;
    }
	
	public static int reserve(int roomNumber, String guestName, String emailAddress, String phoneNumber)
	{
		return server.reserve(roomNumber, guestName, emailAddress, phoneNumber);
	}
	
	
}
