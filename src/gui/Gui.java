package gui;


import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import Main.App;
import Net.ConnectionUtils;
import Net.SSHSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application{

	private Map<String, String> databases;
	private final String DATABASE = "jdbc:oracle:thin:@iwi-i-db-01.hs-karlsruhe.de:1521:XE";
	private final String DATABASE_NAME = "HSKA: Oracle";
	
	private ConnectionUtils connection;
	private SSHSession sshSession;
	private Session session;
	
	private String adName;
	private String adPassword;
	private String adHost;
	private String userNameDB;
	private String userPasswordDB;
	private boolean sshIsOpen;
	private boolean dbIsConnected;
	
	
	private GridPane base;
	private BorderPane sshWindow;
	private GridPane sshWindowBase;
	private BorderPane dbWindow;
	private GridPane dbWindowBase;
	private Button quit;
	private Button openCloseSSHTunnel;
	private Button closeSSHTunnel;
	private Button sshOpen;
	private Button sshCancel;
	private Button dbConnect;
	private Button dbCancel;
	private Button dbConnection;
	private Label sshConnectionInfo;
	private Label dbConnectionInfo;
	private Label sshUserNameLabel;
	private TextField sshUserName;
	private Label sshUserPasswordLabel;
	private PasswordField sshUserPassword;
	private Label sshHostLabel;
	private TextField sshHost;
	private Label dbNameLabel;
	private TextField dbName;
	private Label dbPasswordLabel;
	private PasswordField dbPassword;
	private Label dbSelectionLabel;
	private ComboBox<String> dbSelection;
	private Separator sep1;
	private Separator sep2;
	private Separator sep3;
	private Separator sep4;
	
	@Override
	public void init() {
		base = new GridPane();
		sep1 = new Separator();
		sep1.setMinHeight(35);
		sep1.setVisible(false);
		sep2 = new Separator();
		sep2.setMinWidth(10);
		sep2.setVisible(false);
		sep3 = new Separator();
		sep3.setMinHeight(10);
		sep3.setVisible(false);
		sep4 = new Separator();
		sep4.setMinWidth(10);
		sep4.setVisible(false);
		sshWindow = new BorderPane();
		sshWindowBase = new GridPane();
		dbWindow = new BorderPane();
		dbWindowBase = new GridPane();
		quit = new Button("quit");
		openCloseSSHTunnel = new Button("Open SSH Tunnel");
		closeSSHTunnel = new Button("Close SSH Tunnel");
		closeSSHTunnel.setDisable(true);
		dbConnection = new Button("Connect to Database");
		sshUserNameLabel = new Label("Username: ");
		sshUserName = new TextField();
		sshUserName.setPromptText("z.b.: abcd1011");
		sshUserPasswordLabel = new Label("Password: ");
		sshUserPassword = new PasswordField();		
		sshUserPassword.setPromptText("****");
		sshHostLabel = new Label("Host: ");
		sshHost = new TextField();
		sshHost.setText("login.hs-karlsruhe.de");
		sshOpen = new Button("Open SSH");
		sshCancel = new Button("Cancel");
		sshConnectionInfo = new Label("SSH Tunnel Info");
		
		dbNameLabel = new Label("Name: ");
		dbName = new TextField();
		dbName.setPromptText("g1");
		dbPasswordLabel = new Label("Password: ");
		dbPassword = new PasswordField();
		dbPassword.setPromptText("****");
		dbSelectionLabel = new Label("Database: ");
		dbSelection = new ComboBox(FXCollections.observableArrayList(DATABASE_NAME));
		dbSelection.setValue(DATABASE_NAME);
		dbConnect = new Button("Connect");
		dbCancel = new Button("Cancel");
		dbConnectionInfo = new Label("DB Connection Info");
		
		base.add(quit, 0, 0);
		base.add(sep1, 0, 1);
		base.add(sep2, 0, 2);
		base.add(openCloseSSHTunnel,1,2);
		base.add(sep3, 0, 3);
		base.add(sshConnectionInfo, 1, 4);
//		base.add(sep4, 2, 4);
		base.add(dbConnectionInfo, 2, 4);
		base.add(dbConnection, 2, 2);
		
		sshWindowBase.add(sshUserNameLabel, 0, 1);
		sshWindowBase.add(sshUserName, 1, 1);
		sshWindowBase.add(sshUserPasswordLabel, 0, 2);
		sshWindowBase.add(sshUserPassword, 1, 2);
		sshWindowBase.add(sshHostLabel, 0, 3);
		sshWindowBase.add(sshHost, 1, 3);
		sshWindowBase.add(sshOpen, 0, 4);
		sshWindowBase.add(sshCancel, 1, 4);
		sshWindow.setCenter(sshWindowBase);;

		dbWindowBase.add(dbSelectionLabel, 0, 1);
		dbWindowBase.add(dbSelection, 1, 1);
		dbWindowBase.add(dbNameLabel, 0, 2);
		dbWindowBase.add(dbName, 1, 2);
		dbWindowBase.add(dbPasswordLabel, 0, 3);
		dbWindowBase.add(dbPassword, 1, 3);
		dbWindowBase.add(dbConnect, 0, 4);
		dbWindowBase.add(dbCancel, 1, 4);
		dbWindow.setCenter(dbWindowBase);
		
		//Network related stuff
		connection = new ConnectionUtils();
		sshSession = new SSHSession();
		dbIsConnected = false;
		sshIsOpen = false;
		databases = new HashMap<String, String>();
		databases.put(DATABASE_NAME, DATABASE);
	}
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setCenter(base);
		
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});
		
		
		Stage openSSHWindow = new Stage();
		openSSHWindow.setScene(new Scene(sshWindow, 300, 150));
		openCloseSSHTunnel.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(sshIsOpen) { 
					sshSession.close(session);
					System.out.println("SSH Tunnel Closed");
					sshConnectionInfo.setTextFill(Color.RED);;
					sshConnectionInfo.setText("SSH Tunnel Closed");
					openCloseSSHTunnel.setText("Open SSH Tunnel");
					sshIsOpen = false;
//					closeSSHTunnel.setDisable(true);
//					openSSHTunnel.setDisable(false);
				}else {
					openSSHWindow.show();
				}
			}
		});
		
		sshOpen.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(!sshIsOpen) {
					adHost = sshHost.getText();
					adName = sshUserName.getText();
					adPassword = sshUserPassword.getText();
					try {
						sshSession = new SSHSession(adName, adHost, adPassword);
						session = sshSession.open();
						System.out.println("SSH Tunnel Established");
						sshConnectionInfo.setTextFill(Color.GREEN);
						sshConnectionInfo.setText("SSH Tunnel Open");
						openSSHWindow.close();
						sshUserPassword.clear();
						sshUserName.clear();
						openCloseSSHTunnel.setText("Close SSH Tunnel");
//						openSSHTunnel.setDisable(true);
//						closeSSHTunnel.setDisable(false);
						sshIsOpen = true;
					} catch (JSchException ex) {
						Alert noSSH = new Alert(AlertType.WARNING);
						String contentText = "Failed to established SSH "
								+ "Tunnel: \n" +ex.getMessage();
						noSSH.setTitle("Connection Failed");
						noSSH.setContentText(contentText);
						noSSH.show();
						System.out.println("Failed to establish SSH Tunnel");
						ex.printStackTrace();
					}					
				}
			}
		});
		
		sshCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				openSSHWindow.hide();
				sshUserName.clear();
				sshUserPassword.clear();
			}
		});
		
		Stage connectDBWindow = new Stage();
		connectDBWindow.setScene(new Scene(dbWindow, 300, 150));
		dbConnection.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				connectDBWindow.show();
			}
		});
		
		dbConnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				userNameDB = dbName.getText();
				userPasswordDB = dbPassword.getText();
				try {
					connection.connect(databases.get(DATABASE_NAME), userNameDB, userPasswordDB);
					dbConnectionInfo.setTextFill(Color.GREEN);
					connectDBWindow.close();
					dbName.clear();
					dbPassword.clear();
				}catch(Exception ex) {
					Alert noDBConnection = new Alert(AlertType.WARNING);
					String contentText = "Failed to Connect to DB: \n" + ex.getMessage();
					noDBConnection.setTitle("Connection Failed");
					noDBConnection.setResizable(true);
					noDBConnection.setContentText(contentText);
					noDBConnection.show();
					System.out.println("Failed to connect to Database");
					ex.printStackTrace();
					dbConnectionInfo.setTextFill(Color.RED);
					dbConnectionInfo.setText("No Connection");
				}
			}
		});
		
		dbCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				connectDBWindow.hide();
				dbName.clear();
				dbPassword.clear();
			}
		});
		
		primaryStage.setTitle("Database");
		primaryStage.setScene(new Scene(root, 400, 200));
		primaryStage.show();
		
	}
	
}
