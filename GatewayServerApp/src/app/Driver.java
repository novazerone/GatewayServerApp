package app;

public class Driver {
	private final static String host = "localhost";
	private final static int clientPort = 8080;
	private final static int serverPort = 8081;
	
	private final static String serverPassword = "ADVANCEOS";
	
	/**
	 * @return the host
	 */
	public static String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public static int getClientPort() {
		return clientPort;
	}
	
	public static int getServerPort(){
		return serverPort;
	}
	
	public static String getServerPassword(){
		return serverPassword;
	}
}
