package Net;

import java.util.Properties;
import java.util.Scanner;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class SSHUtils {
	
	public static final int FORWARDING_PORT = 22222;
	
	public static Session open(String adName, String adHost, String password,
			String server) throws JSchException {
		
		JSch jsch = new JSch();
		Session sshSession = jsch.getSession(adName, adHost, 22);		
		sshSession.setPassword(password);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(config);
		sshSession.connect();
		sshSession.setPortForwardingL(FORWARDING_PORT, server, 1521);
		return sshSession;
	}
	
	public static void close(Session sshSession) {
		if(sshSession.isConnected()) {
			sshSession.disconnect();			
		}
	}
}
