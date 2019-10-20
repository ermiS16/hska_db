package Net;

import java.util.Properties;
import java.util.Scanner;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class SSHSession {
	
	private Session sshSession;
	private String adName;
	private String adHost;
	private String password;
	
	public SSHSession() {
		sshSession = null;
		adName = new String();
		adHost = new String();
		password = new String();
	}
	public SSHSession(String adName, String adHost, String password) {
		this.adName = adName;
		this.adHost = adHost;
		this.password = password;
	}

	public Session open() throws JSchException {
		JSch jsch = new JSch();
		Session sshSession = jsch.getSession(adName, adHost, 22);		
		sshSession.setPassword(password);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(config);
		sshSession.connect();
		sshSession.setPortForwardingL(22222, "iwi-i-db-01", 1521);
		return sshSession;
	}
	
	public void close(Session sshSession) {
		if(sshSession.isConnected()) {
			sshSession.disconnect();			
		}
	}
}
