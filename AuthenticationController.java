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

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.ResourceBundle;

import java.net.URL;

public class AuthenticationController implements Initializable  {
	private Stage stage;
	private Scene scene;
	private Parent root;
    @FXML
    private Button verifyButton;

    @FXML
    private Button resendButton;
    @FXML
    private Label loginError;
    @FXML
    private ImageView brandingImageView = new ImageView();
    @FXML
    private ImageView profilePicView = new ImageView();
    @FXML
    private TextField codeTextField;


    //10 Digit code that the other Interfaces will use to authenticate.
    private static String code = "";
    public static int EmployeeOrGuest = 0;

    public void resendButtonOnAction(ActionEvent event) throws MessagingException  {
        loginError.setText("Code Resent. Check Your Email");
        loginError.setStyle("-fx-text-fill: green");

        setCode();

        //Gets Email from Guest or Employee
        String to = "";

        if (EmployeeOrGuest == 1) {
            LoginController LC = new LoginController();
            to = LC.getEmail();
        }
        else {
            GuestLoginController GC = new GuestLoginController();
            to = GC.getGEmail();

        }
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
        message.setText("Here is Your 10-Digit Code: " + getCode());


        Transport.send(message);
        System.out.println(getCode());

    }

    public void verifyButtonOnAction(ActionEvent event) throws IOException {
        // If its blank validate it.
        if (codeTextField.getText().isBlank() == false) {
            if(validateLogin()) {
            	Parent root = FXMLLoader.load(getClass().getResource("HomePage.FXML"));
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();
            }
            else {
            	loginError.setText("Incorrect Code");
                loginError.setStyle("-fx-text-fill: red");
            }


        }
        else {

            loginError.setText("Invalid or Missing Code.");
            loginError.setStyle("-fx-text-fill: red");
        }
    }



    public boolean validateLogin() {
        String userInput = codeTextField.getText();

        if(userInput.equals(getCode())) {
            loginError.setText("Authenticated");
            loginError.setStyle("-fx-text-fill: green");
            return true;
        }
        else {
            loginError.setText("Invalid Code. Try Again.");
            loginError.setStyle("-fx-text-fill: red");
            return false;
        }
    }

    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltCode = salt.toString();
        return saltCode;
    }

    public String getCode(){
        return code;
    }

    public void setCode() {
        code = getSaltString();
    }
    public void setEmployeeOrGuest(int x){
        EmployeeOrGuest = x;

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
    }
}


