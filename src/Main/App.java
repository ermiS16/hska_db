package Main;

import javafx.application.Application;
import java.sql.Connection;
import com.jcraft.jsch.Session;
import gui.Gui;

public class App {

	private Session session;
	private Connection connection;
	
	public App() {
		this.session = null;
		this.connection = null;
	}
	
	public App(Session ses, Connection con) {
		this.session = ses;
		this.connection = con;
	}

	public void setSession(Session ses) {
		this.session = ses;
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setConnection(Connection con) {
		this.connection = con;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public static void main(String[] args) {
		Application.launch(Gui.class);
	}
}
