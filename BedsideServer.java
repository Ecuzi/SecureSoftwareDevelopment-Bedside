//	Author:	Corey Ferguson
//	Date:	29 November 2022
//	File:	BedsideServer.java

package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class BedsideServer
{	
	public enum ServiceType { RoomService, CleaningService };
	
	private Connection connection = null;
	
	//	CONSTRUCTORS
	
	public BedsideServer(String URL, String username, String password)
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			try
			{
				connection = DriverManager.getConnection(URL, username, password);
				
			} catch (SQLException e)
			{
				e.printStackTrace();
				
			}
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	//	MEMBER FUNCTIONS
	
	//	postcondition:	returns 0 or guests, 1 for service staff, and 2 for front desk staff, otherwise returns -1
	
	public int accessLevel(String emailAddress, String password)
	{
		checkEmailAddress(emailAddress);
		
		if (password == null)
			throw new IllegalArgumentException("null");
		
		//	if (password.length() < 8)
			//	throw new IllegalArgumentException("length < 8");
		
		try
		{
			Statement statement = connection.createStatement();
			
			String sql = "select access_level from people where email_address = '";
			
			sql += emailAddress + "' and passwd = '" + password + "';";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (rs.next())				
				return rs.getInt(1);
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}	
		
		return -1;
	}
	
	//	postcondition:	returns true if service was cancelled successfully, otherwise returns false
	
	public boolean cancelService(String emailAddress, String password, int reservationID, ServiceType serviceType)
	{
		//	available to service and front desk
		
		checkEmailAddress(emailAddress);
		
		if (password == null)
			throw new IllegalArgumentException("null");
		
		//	if (password.length() < 8)
			//	throw new IllegalArgumentException("length < 8");
		
		if (reservationID < 0)
			throw new IllegalArgumentException("< 0");
		
		if (serviceType == null)
			throw new IllegalArgumentException("null");
		
		try
		{	
			if (accessLevel(emailAddress, password) < 1)
			{
				System.out.print("Unauthorized");
				
				return false;
			}
			
			Statement statement = connection.createStatement();
			
			String sql = "select cleaning_service, room_service from reservations where id =" + reservationID + ";";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next())
			{
				System.out.println("Reservation does not exist");
				
				return false;
			}
			
			sql = "update reservations set ";
			
			sql += serviceType == ServiceType.CleaningService ? "cleaning_service" : "room_service";
			
			sql += "= false where id =" + reservationID + ";";
			
			statement.executeUpdate(sql);
			
			return true;
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return false;
	}
	
	//	postcondition: returns true if check out is successful, otherwise returns false 
	
	public boolean checkOut(String emailAddress, int roomNumber)
	{
		checkEmailAddress(emailAddress);
		
		if (roomNumber < 0)
			throw new IllegalArgumentException("< 0");
		
		try
		{
			Statement statement =  connection.createStatement();
						
			String sql = "select id from reservations where email_address = '";
			
			sql += emailAddress + "' and room_number =" + roomNumber + ";";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next())
			{
				System.out.println("Reservation does not exist");
				
				return false;
			}
						
			sql = "update rooms set reserved = false where room_number =" + roomNumber + ";";
			
			statement.executeUpdate(sql);
			
			sql = "delete from reservations where email_address = '";
			
			sql += emailAddress + "' and room_number = " + roomNumber + ";";
			
			statement.executeUpdate(sql);
			
			return true;
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return false;
	}
	
	//	postcondition:	terminates connection to the server
	
	public void close()
	{
		if (connection == null)
			throw new IllegalArgumentException("null");
		
		try
		{
			connection.close();
			connection = null;
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//	postcondition:	returns reservation if authorized, otherwise returns null
	
	public Reservation getReservation(String emailAddress, int reservationID)
	{
		checkEmailAddress(emailAddress);
		
		if (reservationID < 0)
			throw new IllegalArgumentException("< 0");
		
		try
		{
			Statement statement = connection.createStatement();
			
			String sql = "select * from reservations where email_address = '";
			
			sql += emailAddress + "' and id =" + reservationID + ";";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next())
			{
				System.out.println("Reservation does not exist");
				
				return null;
			}
			
			String[] data = new String[7];
			
			for (int i = 0; i < 7; ++i)
				data[i] = rs.getString(i + 1);
			
			return new Reservation(data);
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	//	postcondition:	returns all reservations if authorized, otherwise returns null
	
	public Reservation[] getReservations(String emailAddress, String password)
	{
		checkEmailAddress(emailAddress);
		
		if (password == null)
			throw new IllegalArgumentException("null");
		
		if (accessLevel(emailAddress, password) < 2)
		{
			System.out.println("Unauthorized");
			
			return null;
		}
		
		try
		{
			Statement statement = connection.createStatement();
			
			String sql = "select * from reservations;";
			
			ResultSet rs = statement.executeQuery(sql);
			
			Reservation[] reservations = new Reservation[10];
			int n = 0;
			
			while (rs.next())
			{
				String[] data = new String[7];
				
				for (int i = 0; i < 7; ++i)
					data[i] = rs.getString(i + 1);
				
				if (n == reservations.length)
				{
					Reservation[] tmp = new Reservation[n * 2];
					
					for (int j = 0; j < n; ++j)
						tmp[j] = reservations[j];
					
					reservations = tmp;
				}
				
				reservations[n++] = new Reservation(data);
					
			}
			
			if (n != reservations.length)
			{
				Reservation tmp[] = new Reservation[n];
				
				for (int i = 0; i < n; ++i)
					tmp[i] = reservations[i];
				
				reservations = tmp;
			}
			
			return reservations;
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}	
		
		return null;
	}
	
	//	postcondition: returns reservations less Personally Identifiable Information (PII) omitted if authorized, otherwise returns null 
	
	public Reservation[] getServiceRequests(String emailAddress, String password)
	{
		checkEmailAddress(emailAddress);
		
		if (password == null)
			throw new IllegalArgumentException("null");
		
		if (accessLevel(emailAddress, password) == 0)
		{
			System.out.println("Unauthorized");
			
			return null;
		}
		
		try
		{
			Statement statement = connection.createStatement();
			
			String sql = "select id, room_number, cleaning_service, room_service from reservations where cleaning_service = true or room_service = true;";
			
			ResultSet rs = statement.executeQuery(sql);
			
			Reservation[] reservations = new Reservation[10];
			int n = 0;
			
			while (rs.next())
			{
				String[] data = new String[7];
				
				data[0] = rs.getString(1);
				
				int i;
				for (i = 1; i < 4; ++i)
					data[i] = null;
				
				for (; i < 7; ++i)
					data[i] = rs.getString(i - 2);
				
				if (n == reservations.length)
				{
					Reservation[] tmp = new Reservation[n * 2];
					
					for (int j = 0; j < n; ++j)
						tmp[j] = reservations[j];
					
					reservations = tmp;
				}
				
				reservations[n++] = new Reservation(data);
					
			}
			
			if (n != reservations.length)
			{
				Reservation tmp[] = new Reservation[n];
				
				for (int i = 0; i < n; ++i)
					tmp[i] = reservations[i];
				
				reservations = tmp;
			}
			
			return reservations;
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	public boolean isConnected() { return connection != null; }
	
	//	postcondition:	enables cleaning_service or room_service flag if authorized and returns true, otherwise returns false
	
	public boolean requestService(String emailAddress, int roomNumber, ServiceType serviceType)
	{
		//	available to guest and front desk
		
		checkEmailAddress(emailAddress);
		
		if (roomNumber < 0)
			throw new IllegalArgumentException("< 0");
		
		if (serviceType == null)
			throw new IllegalArgumentException("null");
		
		try
		{	
			int accessLevel = accessLevel(emailAddress, String.valueOf(roomNumber));
			
			if (accessLevel == -1 || accessLevel == 1)
			{
				System.out.println("Unauthorized");
				
				return false;
			}
			
			Statement statement = connection.createStatement();
			
			String sql = "select cleaning_service, room_service from reservations where room_number =" + roomNumber + ";";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next())
			{
				System.out.println("Room does not exist");
				
				return false;
			}
			
			sql = "update reservations set ";
			
			sql += serviceType == ServiceType.CleaningService ? "cleaning_service" : "room_service";
			
			sql += "= true where room_number =" + roomNumber + ";";
			
			statement.executeUpdate(sql);
			
			return true;
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return false;
	}
	
	//	postcondition:	reserves room and returns reservationID if successful, otherwise returns false
	
	public int reserve(int roomNumber, String guestName, String emailAddress, String phoneNumber)
	{
		if (roomNumber < 0)
			throw new IllegalArgumentException("< 0");
		
		if (guestName == null)
			throw new IllegalArgumentException("null");
		
		for (int i = 0; i < guestName.length(); ++i)
			if (!Character.isLetter(guestName.charAt(i)) && !Character.isSpaceChar(guestName.charAt(i)) && !Character.isWhitespace(guestName.charAt(i)))
				throw new IllegalArgumentException("invalid guest name");
		
		checkEmailAddress(emailAddress);
		
		if (phoneNumber.length() < 10)
			throw new IllegalArgumentException("< 10");
		
		for (int i = 0; i < phoneNumber.length(); ++i)
			if (phoneNumber.charAt(i) != '-' && !Character.isDigit(phoneNumber.charAt(i)))
				throw new IllegalArgumentException("invalid phone number");
		
		try
		{
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			String sql = "select reserved from rooms where room_number =" + roomNumber + ";";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next())
			{
				System.out.println("Invalid room");
				
				return -1;
			}
			
			if (rs.getBoolean(1))
			{
				System.out.println("Room is already reserved");
				
				return -1;
			}
			
			sql = "delete from people where email_address ='" + emailAddress + "';";
			
			statement.executeUpdate(sql);
			
			sql = "insert into people values ('";
			
			sql += guestName + "','" + emailAddress + "','" + phoneNumber + "'," + roomNumber + ",0);";
			
			statement.executeUpdate(sql);
			
			sql = "insert into reservations(guest_name, email_address, phone_number, room_number, room_service, cleaning_service) values ('";
					
			sql += guestName + "','" + emailAddress + "','" + phoneNumber + "'," + roomNumber + ",false,false"; 
			
			sql += ");";
			
			statement.executeUpdate(sql);
			
			sql = "update rooms set reserved = true where room_number =" + roomNumber + ";";
			
			statement.executeUpdate(sql);
			
			sql = "select id from reservations;";
			
			rs = statement.executeQuery(sql);
			
			rs.last();
			
			return rs.getInt(1);
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return -1;
	}
	
	public boolean isRoomAvailable(int roomNumber)
	{
		try
		{
			Statement statement = connection.createStatement();
			
			String sql = "select reserved from rooms where room_number =" + roomNumber + ";";
			
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next())
			{
				System.out.println("Room does not exist");
				
				return false;
			}
			
			return (!rs.getBoolean(1));
			
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		return false;	
	}
	
	//	NON-MEMBER FUNCTIONS
	
	//	postcondition:	sanitizes input to prevent against SQL injection
	
	private static void checkEmailAddress(String emailAddress)
	{
		if (emailAddress == null)
			throw new IllegalArgumentException("null");
		
		if (emailAddress.isEmpty())
			throw new IllegalArgumentException("empty");
		
		if (!Character.isLetter(emailAddress.charAt(0)))
			throw new IllegalArgumentException("invalid email address");
		
		int i = 0;
		for (int j = 0; j < emailAddress.length(); ++j)
			if (emailAddress.charAt(j) == '@')
				++i;
		
		if (i != 1)
			throw new IllegalArgumentException("invalid email address");
		
		i = 0;
		while (i < emailAddress.length() && emailAddress.charAt(i) != '.')
			++i;
		
		if (i == emailAddress.length())
			throw new IllegalArgumentException("invalid email address");
		
		for (i = 1; i < emailAddress.length(); ++i)
			if (emailAddress.charAt(i) != '_' && emailAddress.charAt(i) != '@' && emailAddress.charAt(i) != '.'
			&& !Character.isDigit(emailAddress.charAt(i)) && !Character.isLetter(emailAddress.charAt(i)))
				throw new IllegalArgumentException("invalid email address");
	}
	
	//	TYPEDEF
	
	public final class Reservation
	{
		//	MEMBER FIELDS
		
		private int _reservationID;
		private String _guestName;
		private String _emailAddress;
		private String _phoneNumber;
		private int _roomNumber;
		private boolean _cleaningServiceRequested;
		private boolean _roomServiceRequested;
		
		//	CONSTRUCTORS
		
		private Reservation(String[] data)
		{
			if (data == null)
				throw new IllegalArgumentException("null");
			
			if (data.length != 7)
				throw new IllegalArgumentException(String.valueOf(data.length));
			
			this._reservationID = Integer.parseInt(data[0]);
			this._guestName = data[1];
			this._emailAddress = data[2];
			this._phoneNumber = data[3];
			this._roomNumber = Integer.parseInt(data[4]);
			this._cleaningServiceRequested = Integer.parseInt(data[5]) == 0 ? false : true; 
			this._roomServiceRequested = Integer.parseInt(data[6]) == 0 ? false : true;
			
		}
		
		//	MEMBER FUNCTIONS
		
		public int reservationID() { return _reservationID; }
		
		public String guestName() { return _guestName; }
		
		public String emailAddress() { return _emailAddress; }
		
		public String phoneNumber() { return _phoneNumber; }
		
		public int roomNumber() { return _roomNumber; }
		
		public boolean cleaningServiceRequested() { return _cleaningServiceRequested; }
		
		public boolean roomServiceRequested() { return _roomServiceRequested; }
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("Reservation ID:\t\t" + reservationID() + "\n");
			sb.append("Guest Name:\t\t" + guestName() + "\n");
			sb.append("Email Address:\t\t" + emailAddress() + "\n");
			sb.append("Phone Number:\t\t" + phoneNumber() + "\n");
			sb.append("Cleaning Service?\t" + (cleaningServiceRequested() ? "yes" : "no") + "\n");
			sb.append("Room Service?\t\t" + (roomServiceRequested() ? "yes" : "no"));
			
			return sb.toString();
		}
	}
}