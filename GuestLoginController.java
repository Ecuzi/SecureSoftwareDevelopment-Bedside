package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import java.net.URL;
import java.util.regex.Pattern;

public class GuestLoginController implements Initializable  {
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML
    private Button cancelButton, loginButton;
    @FXML
    private Label loginError;
    @FXML
    private ImageView brandingImageView = new ImageView();
    @FXML
    private ImageView profilePicView = new ImageView();
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField roomNumberField;

    private static String Email ="";
    public static String email = "";
    public static String pass = "";




    public void loginButtonOnAction(ActionEvent event) throws MessagingException, IOException {
        // If it's not blank validate it.
        if (usernameTextField.getText().isBlank() == false && roomNumberField.getText().isBlank() == false) {
            String to = usernameTextField.getText();

            if (patternMatches(to, "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                    + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
                //doNothing
            }
            else {
                loginError.setText("Invalid Email. Try Again.");
                return;
            }

            if(checkCredentials()) {
            	validateLogin();
            }
            // Else returns error
            else {
            	loginError.setText("Invalid Credentials or Unknown user");
            	return;
            }

            Parent guestViewParent = FXMLLoader.load(getClass().getResource("Authentication.fxml"));
            Scene guestScene = new Scene(guestViewParent);
			guestScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setScene(guestScene);
            window.show();
        }
        else {
            loginError.setText("Please enter Email and Room Number");
        }
    }

    private boolean checkCredentials() {
    	try {
			AuthenticationController AC = new AuthenticationController();
			ServerConnection.openConnection("jdbc:mysql://localhost/BSDB", "root", "password");
			email = usernameTextField.getText();		
			pass = roomNumberField.getText();
			int x = ServerConnection.accessLevel(email, pass);
			if(x == 0) {
				AC.setEmployeeOrGuest(2);
				return true;
			}
	}
	catch(Exception e) {
		System.out.println(e);
	}
	return false;
	}

	public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    public void validateLogin() throws MessagingException {
        //      Updates new code to send to user
        AuthenticationController AC = new AuthenticationController();
        AC.setCode();
        String to = usernameTextField.getText();
        setGEmail(to);
        String from = "bedsideverify@gmail.com";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("bedsideverify@gmail.com", "kngyamhqwqribflc");

            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("BedSide Authentication");
        message.setText("Here is Your 10-Digit Code: " + AC.getCode());



        Transport.send(message);

    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public void hyperlinkOnAction(ActionEvent event) throws IOException {
        Parent guestViewParent = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene guestScene = new Scene(guestViewParent);
		guestScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(guestScene);
        window.show();
    }

    public void setGEmail(String s) {
        Email = s;
    }

    public String getGEmail() {
        return Email;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
    }
}


