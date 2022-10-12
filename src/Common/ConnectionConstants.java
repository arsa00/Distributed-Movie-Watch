package Common;

public class ConnectionConstants
{
	public static final String mainServerDiscoverReqMsg = "USER_MAIN_SERVER_DISCOVER_REQUEST";
	public static final String mainServerDiscoverResMsg = "USER_MAIN_SERVER_DISCOVER_RESPONSE";
	public static final String subserverReconnectionReq = "SUBSERVER_RECONNECTION_REQUEST"; // add old subserver & userName to end of request
	public static final String subserverReconnectionRes = "SUBSERVER_RECONNECTION_RESPONSE";
	public static final String subserverConnectionReq = "SUBSERVER_CONNECTION_REQUEST"; // add userName to end of request
	public static final String subserverConnectionRes = "SUBSERVER_CONNECTION_RESPONSE";
	public static final String subserverConnectionErr = "SUBSERVER_CONNECTION_ERROR";
	
	public static final String userLoginReq = "LOGIN_USER_REQUEST";
	public static final String userLoginRes = "LOGIN_USER_RESPONSE";
	public static final String userLoginSuccess = "LOGIN_USER_SUCCESS";
	public static final String userLoginPassErr = "LOGIN_USERR_PASS";
	public static final String userLoginUserNameErr = "LOGIN_USERR_USERNAME";
	public static final String userLoginAlreadyLoggedErr = "LOGIN_USERR_ALREADY_LOGGED";
	
	public static final String configFileType = ".conf";
	public static final String configDir = "Config/";
	public static final String subserversConfigDir = "Config/Subservers/";
	public static final String ipAddrConfigFile = "Config/ip.conf";
	public static final String moviesAndRoomsfileName = "MoviesAndRooms";
	public static final String moviesAndRoomsDelimiterWord = "ROOMS:";
	
	public static final String mainServerMulticastAddress = "225.0.0.0";
	public static final String subserverMulticastAddress = "225.0.0.1";
	public static final String userMulticastAddress = "225.0.0.2";

	public static final int userMulticastPort = 5001;
	public static final int userUDPort = 5002;
	public static final int userTCPort = 5003;
	public static final int subserverUDPort = 5004;
	public static final int subserverTCPort = 5005;
	public static final int subserverMulticastPort = 5006;
	public static final int mainServerUDPort = 5007;
	public static final int mainServerTCPort = 5008;
	public static final int mainServerMulticastPort = 5009;
}
