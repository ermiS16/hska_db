package Net;

import java.util.Properties;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class SSHUtils {
	
	public static final int FORWARDING_PORT = 22222;
	
	
	/**
	 * Opens a SSH Session
	 * @param adName The Username for the SSH Session
	 * @param adHost Hostname of the Server
	 * @param password from the User
	 * @param server URL from the Server
	 * @return the Session if opened successfully, otherwise null
	 * @throws JSchException
	 */
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
	
	/**
	 * Closes a Session
	 * @param sshSession the Session to close
	 */
	public static void close(Session sshSession) {
		if(sshSession.isConnected()) {
			sshSession.disconnect();			
		}
	}
}
