package gui;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import Main.App;
import Net.ConnectionUtils;
import Net.JDBCBikeShop;
import Net.SSHUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Gui extends Application{

	private Map<String, String> databases;
	private Map<String, String> databasesSSH;
	private final String DATABASE_NO_SSH = "jdbc:oracle:thin:@iwi-i-db-01.hs-karlsruhe.de:1521:XE";
	private final String DATABASE_NAME = "HSKA: Oracle:XE";
	private final String DATABASE_SSH = "jdbc:oracle:thin:@localhost:"+ 
										SSHUtils.FORWARDING_PORT+":XE";
	private Map<String, String> sshServer;
	private final String SSH_HOST_NAME = "login.hs-karlsruhe.de";
	private final String SSH_SERVER_NAME = "iwi-i-db-01";
	
	private App application;
	private Button aReset;
	private Button dbShowExit;
	private HBox dbShowResultNav;
	private BorderPane dbShowResultOption;
	private TextArea statementBox;
	private Button submitStatement;
	private TextArea solutionField;
	private Text solutionFieldText;
//	private Session session;
	
	private String adName;
	private String adPassword;
	private String adHost;
	private String userNameDB;
	private String userPasswordDB;
	private boolean sshIsOpen;
	private boolean dbIsConnected;
	private Button showDatabase;
		
	private GridPane base;
	private BorderPane sshWindow;
	private GridPane sshWindowBase;
	private BorderPane dbWindow;
	private GridPane dbWindowBase;
	private BorderPane dbShow;
//	private GridPane dbShowBase;
	private VBox dbShowResult;
	
	private Button quit;
	private Button openCloseSSHTunnel;
	private Button closeSSHTunnel;
	private Button sshOpen;
	private Button sshCancel;
	private Button dbConnect;
	private Button dbCancel;
	private Button dbConnection;
	private Button sshConnectionInfo;
	private Button dbConnectionInfo;
	private Label sshUserNameLabel;
	private TextField sshUserName;
	private Label sshUserPasswordLabel;
	private PasswordField sshUserPassword;
	private Label sshHostLabel;
	private TextField sshHost;
	private ComboBox<String> sshHostSelection;
	
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
	private Separator sep5;
	
	@Override
	public void init() {
		
		//Network related stuff
		dbIsConnected = false;
		sshIsOpen = false;
		databases = new HashMap<String, String>();
		databases.put(DATABASE_NAME, DATABASE_NO_SSH);
		databasesSSH = new HashMap<String, String>();
		databasesSSH.put(DATABASE_NAME, DATABASE_SSH);
		sshServer = new HashMap<String, String>();
		sshServer.put(SSH_HOST_NAME, SSH_SERVER_NAME);
		
		initMainWindow();
		initSSHConnectionWindow();
		initDBConnectionWindow();
		initDBShowWindow();
		
		application = new App();
	}
	
	public void initMainWindow() {
		base = new GridPane();
		sep1 = new Separator();
		sep1.setMinHeight(35);
		sep1.setVisible(false);
		sep2 = new Separator();
		sep2.setMinWidth(10);
		sep2.setVisible(false);
		sep3 = new Separator();
		sep3.setMinWidth(5);
		sep3.setVisible(false);
		sep4 = new Separator();
		sep4.setMinWidth(10);
		sep4.setVisible(false);
		sep5 = new Separator();
		sep5.setVisible(false);

		quit = new Button("quit");
		openCloseSSHTunnel = new Button("Open SSH Tunnel");
		dbConnection = new Button("Connect to Database");
		
		sshConnectionInfo = new Button("SSH Tunnel Info");
		dbConnectionInfo = new Button("DB Connection Info");
		showDatabase = new Button("Show Database");
		showDatabase.setDisable(true);
		
		base.add(quit, 0, 0);
		base.add(sep1, 0, 1);
		base.add(sep2, 0, 2);
		base.add(openCloseSSHTunnel,1,2);
		base.add(sep3, 2, 3);
		base.add(sshConnectionInfo, 1, 4);
		base.add(sep4, 2, 4);
		base.add(dbConnectionInfo, 2, 4);
		base.add(dbConnection, 2, 2);
		base.add(sep5, 2, 5);
		base.add(showDatabase, 2, 6);		
	}
	
	public void initSSHConnectionWindow() {
		sshWindow = new BorderPane();
		sshWindowBase = new GridPane();
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
		sshOpen.setDefaultButton(true);
		sshCancel = new Button("Cancel");
		sshHostSelection = new ComboBox<String>(
				FXCollections.observableArrayList(SSH_HOST_NAME));
		sshHostSelection.setValue(SSH_HOST_NAME);
		
		sshWindowBase.add(sshUserNameLabel, 0, 1);
		sshWindowBase.add(sshUserName, 1, 1);
		sshWindowBase.add(sshUserPasswordLabel, 0, 2);
		sshWindowBase.add(sshUserPassword, 1, 2);
		sshWindowBase.add(sshHostLabel, 0, 3);
		sshWindowBase.add(sshHostSelection, 1, 3);
		sshWindowBase.add(sshOpen, 0, 4);
		sshWindowBase.add(sshCancel, 1, 4);
		sshWindow.setCenter(sshWindowBase);;

	}
	
	public void initDBConnectionWindow() {
		dbWindow = new BorderPane();
		dbWindowBase = new GridPane();
		dbNameLabel = new Label("Name: ");
		dbName = new TextField();
		dbName.setPromptText("g1");
		dbPasswordLabel = new Label("Password: ");
		dbPassword = new PasswordField();
		dbPassword.setPromptText("****");
		dbSelectionLabel = new Label("Database: ");
		dbSelection = new ComboBox<String>(
				FXCollections.observableArrayList(DATABASE_NAME));
		dbSelection.setValue(DATABASE_NAME);
		dbConnect = new Button("Connect");
		dbConnect.setDefaultButton(true);
		dbCancel = new Button("Cancel");		
	
		dbWindowBase.add(dbSelectionLabel, 0, 1);
		dbWindowBase.add(dbSelection, 1, 1);
		dbWindowBase.add(dbNameLabel, 0, 2);
		dbWindowBase.add(dbName, 1, 2);
		dbWindowBase.add(dbPasswordLabel, 0, 3);
		dbWindowBase.add(dbPassword, 1, 3);
		dbWindowBase.add(dbConnect, 0, 4);
		dbWindowBase.add(dbCancel, 1, 4);
		dbWindow.setCenter(dbWindowBase);

	}
	
	public void initDBShowWindow() {
		dbShow = new BorderPane();
		dbShowResult = new VBox();		
		aReset = new Button("Reset");
		dbShowExit = new Button("Exit");
		statementBox = new TextArea();
		statementBox.setPromptText("Filter");
		submitStatement = new Button("Submit");
		solutionField = new TextArea();
		solutionField.setEditable(false);
		solutionField.setMaxHeight(350);
		solutionField.setStyle("-fx-font: monospace;" +
								"-fx-font-family: monospace");
		solutionFieldText = new Text();
		solutionFieldText.setStyle("-fx-font: times new roman" +
									"-fx-font-family: times new roman");
//		solutionField.fontProperty().set(new Font("arial", 12));
		dbShowResultNav = new HBox();
		dbShowResultNav.getChildren().addAll(aReset, dbShowExit);
		dbShowResultOption = new BorderPane();
		dbShowResultOption.setCenter(statementBox);
		dbShowResultOption.setRight(submitStatement);
		dbShowResult = new VBox();
		dbShowResult.setPadding(new Insets(20, 10, 20, 10));
		dbShowResult.getChildren().add(solutionField);
//		dbShowResult.getChildren().add(solutionFieldText);
		dbShow.setTop(dbShowResultNav);
		dbShow.setCenter(dbShowResult);
		dbShow.setBottom(dbShowResultOption);
		
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
					SSHUtils.close(application.getSession());
					application.setSession(null);
					System.out.println("SSH Tunnel Closed");
					openCloseSSHTunnel.setText("Open SSH Tunnel");
					sshIsOpen = false;
				}else {
					openSSHWindow.show();
				}
			}
		});
		
		sshConnectionInfo.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				Alert info = new Alert(AlertType.INFORMATION);
				info.setTitle("SSH Info");
				info.setResizable(true);
				if(sshIsOpen) {
					info.setContentText("SSH Tunnel Open:\n" 
							+ SSH_HOST_NAME + "\n"
							+ SSH_SERVER_NAME);
					info.show();					
				}else {
					info.setContentText("Not Connected");
					info.show();
				}
			}
		});
		
		sshOpen.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(!sshIsOpen) {
					adHost = sshHostSelection.getValue();
					adName = sshUserName.getText();
					adPassword = sshUserPassword.getText();
					try {
//						sshSession = new SSHUtils(adName, adHost, 
//								adPassword, sshServer.get(SSH_HOST_NAME));
						application.setSession(SSHUtils.open(adName, adHost, adPassword,
								sshServer.get(SSH_HOST_NAME)));
//						application.setSession(application.getSession());
						System.out.println("SSH Tunnel Established");
						openSSHWindow.close();
						sshUserPassword.clear();
						sshUserName.clear();
						openCloseSSHTunnel.setText("Close SSH Tunnel");
						sshIsOpen = true;
					} catch (JSchException ex) {
						Alert noSSH = new Alert(AlertType.WARNING);
						noSSH.setResizable(true);
						String contentText = "Failed to established SSH "
								+ "Tunnel: \n" +ex.getMessage();
						noSSH.setTitle("Connection Failed");
						noSSH.setContentText(contentText);
						noSSH.setResizable(true);
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
				if(dbIsConnected) {
					try {
						ConnectionUtils.disconnect(application.getConnection());
						application.setConnection(null);
						dbConnection.setText("Connect to Database");
						dbIsConnected = false;
						showDatabase.setDisable(true);
					} catch (SQLException ex) {
						Alert dbNotClosed = new Alert(AlertType.WARNING);
						dbNotClosed.setResizable(true);
						dbNotClosed.setTitle("Connection Error");
						dbNotClosed.setContentText("Couldn't Disconnect"
								+ "from Database");
						dbNotClosed.setResizable(true);
					}
					
				}else {
					connectDBWindow.show();
				}
			}
		});
		
		dbConnectionInfo.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				Alert info = new Alert(AlertType.INFORMATION);
				info.setTitle("DB Info");
				info.setResizable(true);
				if(dbIsConnected) {
					info.setContentText("Connected to:\n" + DATABASE_NAME +
							"\nDriver: " + ConnectionUtils.getDriver(
									application.getConnection()));
					info.show();					
				}else {
					info.setContentText("Not Conected");
					info.show();
				}
			}
		});
		
		dbConnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				userNameDB = dbName.getText();
				userPasswordDB = dbPassword.getText();
				try {
					if(sshIsOpen) {
						application.setConnection(ConnectionUtils.connect(
								databasesSSH.get(DATABASE_NAME), 
								userNameDB, userPasswordDB));		
					}else {
						application.setConnection(ConnectionUtils.connect(
								databases.get(DATABASE_NAME), 
								userNameDB, userPasswordDB));
					}
					showDatabase.setDisable(false);
					dbConnection.setText("Disconect Database");
					connectDBWindow.close();
					dbName.clear();
					dbPassword.clear();
					dbIsConnected = true;
				}catch(Exception ex) {
					Alert noDBConnection = new Alert(AlertType.WARNING);
					String contentText = "Failed to Connect to DB: \n" + ex.getMessage();
					noDBConnection.setTitle("Connection Failed");
					noDBConnection.setResizable(true);
					noDBConnection.setContentText(contentText);
					noDBConnection.show();
					System.out.println("Failed to connect to Database");
					ex.printStackTrace();
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
		
		Stage dbShowWindow = new Stage();
		dbShowWindow.setScene(new Scene(dbShow, 1000, 800));
		showDatabase.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
				dbShowWindow.show();
			}
		});
		
		dbShowExit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				dbShowWindow.hide();
				initDBShowWindow();
			}
		});
		
		aReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				initDBShowWindow();
			}
		});		

		submitStatement.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
				String sqlQuery = statementBox.getText();	
				String result = new String();
				
				try {
					result = JDBCBikeShop.getResult(
							application.getConnection(), sqlQuery);
					solutionField.setText(result);
				} catch (SQLException ex) {
					Alert sqlError = new Alert(AlertType.WARNING);
					sqlError.setTitle("SQL Exception");
					sqlError.setContentText(ex.getMessage());
					sqlError.setResizable(true);
					sqlError.show();
					ex.printStackTrace();
				}
			}
		});
		
		primaryStage.setTitle("Database");
		primaryStage.setScene(new Scene(root, 400, 200));
		primaryStage.show();
		
	}
	
}
