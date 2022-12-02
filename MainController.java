package application;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainController implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML
	private Button Home;
	@FXML
	private Button Check;
	@FXML
	private Button Service;
	@FXML
	private Button Logout;
	@FXML
	private Button ProfileIcon;
	@FXML 
	private TextField RoomEntry;
	@FXML 
	private TextField NameEntry;
	@FXML 
	private TextField EmailEntry;
	@FXML 
	private TextField PhoneEntry;
	@FXML
	private Label ReserveMessage;
	@FXML
	private CheckBox RServiceCheck;
	@FXML
	private CheckBox CServiceCheck;
	@FXML
	private TextField RmEntry;
	@FXML 
	private Label completeLabel;
	@FXML
	private CheckBox completeStatus;
	@FXML
	private Label InprogressLabel;
	@FXML
	private CheckBox InProgStatus;
	@FXML
	private TableView<Reservation> BookingTable;
	@FXML
	private TableColumn<Reservation, String> TableRoom;
	@FXML
	private TableColumn<Reservation, String> TableStatus;
	@FXML
	private TableColumn<Reservation, String> TableName;
	@FXML
	private TableColumn<Reservation, String> TableEmail;
	@FXML
	private TableColumn<Reservation, String> TablePhone;
	
	@FXML 
	private TableView<Reservation> ServiceTable;
	@FXML
	private TableColumn<Reservation, String> ServiceRoom;
	@FXML
	private TableColumn<Reservation, String> ServiceStatus; 
	
	private ObservableList<Reservation> data;
	private ObservableList<Reservation> data1;
	
	private static int page = 0;
	
	
	public void navController(ActionEvent event) {
		if(event.getSource() == Home) {
			try {
				page=0;
				Parent root = FXMLLoader.load(getClass().getResource("HomePage.FXML"));
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		else if(event.getSource() == Check) {
			try {
				page = 1;
				Parent root = FXMLLoader.load(getClass().getResource("CheckPage.FXML"));
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		else if(event.getSource() == Service) {
			try {
				page=2;
				Parent root = FXMLLoader.load(getClass().getResource("ServicePage.FXML"));
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();
				
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		else if(event.getSource() == Logout) {
			try {
				Stage stage = (Stage) Logout.getScene().getWindow();
			    stage.close();
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
			
		
	}
	public void CheckIn(ActionEvent event) {
		ReserveMessage.setText("");
		String rNum = RoomEntry.getText();
		String Gname = NameEntry.getText();
		String GEmail = EmailEntry.getText();
		String GPhone = PhoneEntry.getText();
		if(ServerConnection.isRoomAvailable(Integer.parseInt(rNum))) {
			try{
				ServerConnection.reserve(Integer.parseInt(rNum), Gname, GEmail, GPhone);
			}
			catch(IllegalArgumentException IA) {
				ReserveMessage.setText("Error Invalid Inputs");
			}
		}
		else{
			ReserveMessage.setText("Room Not Available");
		}
	}
	public void CheckOut(ActionEvent event) {
		ReserveMessage.setText("");
		String rNum = RoomEntry.getText();
		String GEmail = EmailEntry.getText();
		if(ServerConnection.checkOut(GEmail, Integer.parseInt(rNum))) {
			ReserveMessage.setText("Reservation Removed");
		}
		else {
			System.out.println("error");
		}
	}
	public void ReqService(ActionEvent event) {
		boolean x = RServiceCheck.isSelected();
		boolean y = CServiceCheck.isSelected();
		String input = RmEntry.getText();
		if(y) {
			ServerConnection.requestService(GuestLoginController.email, Integer.parseInt(input), BedsideServer.ServiceType.CleaningService);
		}
		if(x) {
			ServerConnection.requestService(GuestLoginController.email, Integer.parseInt(input), BedsideServer.ServiceType.RoomService);
		}
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if(AuthenticationController.EmployeeOrGuest == 2)
		{
			Check.setDisable(true);
		}
		else if(ServerConnection.accessLevel(LoginController.email, LoginController.pass) == 1)
		{
			Check.setDisable(true);
		}
		else if(ServerConnection.accessLevel(LoginController.email, LoginController.pass) == 2)
		{
			Service.setDisable(true);
		}
		if(page == 1) {
			
			try {
				data=FXCollections.observableArrayList();
				Reservation[] r = ServerConnection.getRooms(LoginController.email, LoginController.pass);
				for (Reservation s: r) { data.add(s); }
			}	
				
			catch(Exception e) {
				System.out.println(e);
			}

			
			TableRoom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoom()));
			TableStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
			TableName.setCellValueFactory(new PropertyValueFactory<>("name"));
			TableEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
			TablePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
			
			BookingTable.setItems(data);
		}
		if(page == 2) {
			
			
			if(AuthenticationController.EmployeeOrGuest == 2) {
				completeLabel.setVisible(false);
				completeStatus.setVisible(false);
				InprogressLabel.setVisible(false);
				InProgStatus.setVisible(false);
			}
			
			
			try {
				data1=FXCollections.observableArrayList();
				Reservation[] r = ServerConnection.serviceRequests(LoginController.email, LoginController.pass);
				for (Reservation s: r) { data1.add(s); }
			}	
				
			catch(Exception e) {
				System.out.println(e);
			}

			
			ServiceRoom.setCellValueFactory(data1 -> new SimpleStringProperty(data1.getValue().getRoom()));
			ServiceStatus.setCellValueFactory(data1 -> new SimpleStringProperty(data1.getValue().getStatus()));
			
			ServiceTable.setItems(data1);
		}
		
		
	}
	
	

}
