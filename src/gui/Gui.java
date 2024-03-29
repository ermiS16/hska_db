package gui;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.JSchException;
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
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Gui extends Application{

	private final String TASK_42 = "Aufgabe 4.2";
	private final String TASK_43 = "Aufgabe 4.3";
	private final String TASK_44 = "Aufgabe 4.4";
	private final String TASK_42_PATH = "src/sql/aufgabe4.2.sql";
	private final String TASK_43_PATH = "src/sql/aufgabe4.3.sql";
	private final String TASK_44_PATH = "src/sql/aufgabe4.4.sql";
	
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
	
	private TabPane tabPane;
	private Button aReset;
	private Button dbShowExit;
	private HBox dbShowResultNav;
	private BorderPane dbShowResultOption;
	private TextArea statementBox;
	private Button submitStatement;
	private TextArea currentSQLQuery;
	private TextArea solutionField;
	private Button loadFile;
	private FileChooser fileChooser;
	private File file;
	private Button clearStatementBox;
	private ComboBox<String> tasks;
	private VBox sqlQueryButtons;
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
	private VBox dbShowResult;
	
	private Button quit;
	private Button openCloseSSHTunnel;
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
	
	/**
	 * Initialzing the Nodes of the main Window
	 */
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
	
	/**
	 * Initialiting the Nodes of the SSH Tunnel Window
	 */
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
		sshWindow.setCenter(sshWindowBase);

	}
	
	/**
	 * Initializing the Nodes of the Database Connection Window
	 */
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
	
	/**
	 * Initializing the Nodes of the Database Show Window
	 */
	public void initDBShowWindow() {
		tabPane = new TabPane();
		sqlQueryButtons = new VBox();
		dbShow = new BorderPane();
		dbShowResult = new VBox();
		fileChooser = new FileChooser();
		loadFile = new Button("Load File");
		aReset = new Button("Reset");
		dbShowExit = new Button("Exit");
		statementBox = new TextArea();
		statementBox.setPromptText("Filter");
		submitStatement = new Button("Submit");
		clearStatementBox = new Button("Clear");
		tasks = new ComboBox<String>(FXCollections.observableArrayList(
									TASK_42, TASK_43, TASK_44));
		tasks.setPromptText("Aufgaben");
		solutionField = new TextArea();
		solutionField.setEditable(false);
		solutionField.setMinHeight(350);
		solutionField.setMaxHeight(350);
		solutionField.setStyle("-fx-font: monospace;" +
								"-fx-font-family: monospace");
		currentSQLQuery = new TextArea();
		currentSQLQuery.setEditable(false);
		currentSQLQuery.setStyle("-fx-background-color: grey");
		dbShowResultNav = new HBox();
		dbShowResultNav.getChildren().addAll(aReset, dbShowExit);
		dbShowResultOption = new BorderPane();
		dbShowResultOption.setCenter(statementBox);
		sqlQueryButtons.getChildren().addAll(submitStatement, loadFile,
												clearStatementBox, tasks);
		dbShowResultOption.setRight(sqlQueryButtons);
		dbShowResult = new VBox();
		dbShowResult.setPadding(new Insets(20, 10, 20, 10));
		dbShowResult.getChildren().addAll(solutionField, currentSQLQuery);
		dbShow.setTop(dbShowResultNav);
		dbShow.setCenter(dbShowResult);
		dbShow.setBottom(dbShowResultOption);		
	}
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setCenter(base);
		
		/**
		 * Implementation of the Quit Button on the Main Window
		 * Exits the Application
		 */
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});
		
		
		//Creates a new Stage, for the SSH Connection
		Stage openSSHWindow = new Stage();
		openSSHWindow.setTitle("SSH Tunnel");
		openSSHWindow.setScene(new Scene(sshWindow, 300, 150));
		
		/**
		 * If a SSH Tunnel is open, the tunnel will be closed.
		 * Otherwise the Conenction Window is shown.
		 */
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
		
		/**
		 * Shows a Alert Window, with the Information of the
		 * current Connection.
		 */
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
		
		/**
		 * Opens a new SSH Tunnel with the given parameters.
		 * The Session is set in the application.
		 */
		sshOpen.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(!sshIsOpen) {
					adHost = sshHostSelection.getValue();
					adName = sshUserName.getText();
					adPassword = sshUserPassword.getText();
					try {
						application.setSession(SSHUtils.open(adName, adHost, adPassword,
								sshServer.get(SSH_HOST_NAME)));
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
		
		/**
		 * Closes the window and resets the TextFields
		 */
		sshCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				openSSHWindow.hide();
				sshUserName.clear();
				sshUserPassword.clear();
			}
		});
		
		//Creates a new Stage for the Conenction Window for the Database
		Stage connectDBWindow = new Stage();
		connectDBWindow.setTitle("Database Connect");
		connectDBWindow.setScene(new Scene(dbWindow, 300, 150));
		
		/**
		 * If a DB connection is open, the connection will be closed.
		 * Otherwise the Window is shown, where a new connection can
		 * be established.
		 */
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
		
		/**
		 * Shows a Alert Window with the Information of the
		 * current Connection. The Information is the name of
		 * the Database and the driver, that the conneciton uses.
		 */
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
		
		/**
		 * Connect to a Database and sets the Connection in
		 * the application. Checks first if a SSH Tunnel is open
		 * or not.
		 */
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
					application.getConnection().setAutoCommit(true);
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
		
		/**
		 * Closes the Connectionwindow and resets the textFields.
		 */
		dbCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				connectDBWindow.hide();
				dbName.clear();
				dbPassword.clear();
			}
		});
		
		//Creates a new Stage for the Show Window of the Database
		Stage dbShowWindow = new Stage();
		dbShowWindow.setTitle("Showroom");
		dbShowWindow.setScene(new Scene(dbShow, 1000, 800));
		
		/**
		 * Shows the ShowDatabase Window.
		 */
		showDatabase.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
				dbShowWindow.show();
			}
		});
		
		/**
		 * CLoses the ShowDatabase Window and resets the TextAreas.
		 */
		dbShowExit.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				solutionField.clear();
				statementBox.clear();
				dbShowWindow.hide();
			}
		});
		
		/**
		 * Reinitializing of the Database with the BikeShop
		 */
		aReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				JDBCBikeShop.reInitializeDB(application.getConnection());
				currentSQLQuery.clear();
				currentSQLQuery.setText("Initialized");
			}
		});		

		/**
		 * Function to open a File and loading the content
		 * int the statementBox, if the File is a .sql File.
		 */
		loadFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				file = fileChooser.showOpenDialog(dbShowWindow);
				if(file.getName().endsWith(".sql")) {
					System.out.println("Datei geladen");
					statementBox.clear();
					try {
						setTextOnStatementBox(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}	
			}
		});
		
		/**
		 * Clears the StatementBox
		 */
		clearStatementBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				statementBox.clear();
			}
		});
		
		/**
		 * Loads predefined Files for the Database Labor
		 * and set the content to the statementBox
		 */
		tasks.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				String selection = tasks.getValue();
				switch(selection) {
				case TASK_42: file = new File(TASK_42_PATH);
					break;					
				case TASK_43: file = new File(TASK_43_PATH);
					break;
				case TASK_44: file = new File(TASK_44_PATH);
					break;
				}
				try {
					if(file.getName().endsWith(".sql")) {
						statementBox.clear();
						setTextOnStatementBox(file);
					}
				}catch(IOException ex) {
					Alert warning = new Alert(AlertType.WARNING);
					warning.setTitle("Error Occured");
					warning.setContentText(ex.getMessage());
					warning.setResizable(true);
					warning.show();
					ex.printStackTrace();
				}
			}
		});
		
		/**
		 * Submit the Content of the statementBox to the Method getResult
		 * in the JDBCBikeShop Class, which executes the sqlQuerys.
		 * If there are more than one Statement, they will be splitted
		 * and exceuted after another.
		 * The Result of the Statement will be placed in the SolutionField.
		 */
		submitStatement.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
				String sqlQuery = statementBox.getText();	
				String result = "";
				currentSQLQuery.clear();
				try {
					String[] fileContents = sqlQuery.trim().split(";");
					
					for(String query : fileContents) {
							result = JDBCBikeShop.getResult(
								application.getConnection(), query);						
						
							currentSQLQuery.appendText(query);
							solutionField.setText(result);
						
					}
					
				} catch (SQLException ex) {
					Alert sqlError = new Alert(AlertType.WARNING);
					sqlError.setTitle("SQL Exception");
					sqlError.setContentText(ex.getMessage());
					sqlError.setResizable(true);
					sqlError.show();
					currentSQLQuery.appendText("\n" + ex.getMessage());
					ex.printStackTrace();
				}
			}
		});
		
		//Creates the MainWindow
		primaryStage.setTitle("Database");
		primaryStage.setScene(new Scene(root, 400, 200));
		primaryStage.show();
		
	}
	
	/**
	 * Set the content of a File in the statementBox
	 * @param file which content must be set on the statementBox
	 * @throws IOException
	 */
	private void setTextOnStatementBox(File file) throws IOException {
		List<String> line = new ArrayList<String>();
		line = getContentFile(file);
		for(String str: line) {
			statementBox.appendText(str);
		}
		
	}
	
	/**
	 * Reads the File and puts the content as rows in a List.
	 * @param file which content will be read.
	 * @return a List of Rows
	 * @throws IOException
	 */
	private List<String> getContentFile(File file) throws IOException {
		List<String> result = new ArrayList<String>();
		FileReader reader = new FileReader(file);
		BufferedReader buff = new BufferedReader(reader);
		String line = "";
		line = buff.readLine();
		while(line != null) {
			line += "\n";
			result.add(line);
			line = buff.readLine();
		}
		buff.close();
		reader.close();
		return result;
	}
}
