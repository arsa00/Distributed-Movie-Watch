package User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;


import Common.ConcurrentFileHandler;
import Common.ConnectionConstants;
import Common.FileBrowser;
import Common.TransferMessage;
import Errors.ErrorFatalConnectionLost;
import Errors.ErrorMulitcastGroup;
import Errors.ErrorServerNotFound;


public class ConnectionService
{
	// public static ConnectionService handle = new ConnectionService();  // this object maintains all connetions for user
	
	private static InetAddress mainServerAddr = null;
	private static InetAddress multicastMainServer = null;
	
	private static int MAIN_SERVER_RECONNECTION_ATTEMPTS = 3;	// default value is 3; can be changed later;
	private static long MAIN_SERVER_RECONNECTION_SLEEPTIME = 500;	// in ms
	private static int MAIN_SERVER_DISCOVER_RESPONSE_MAXTIME = 5000;	// in ms
	private static int MAIN_SERVER_RESPONSE_MAXTIME = 2000;	// in ms
	
	private static int SUBSERVER_RECONNECTION_ATTEMPTS = 3;	// default value is 3; can be changed later;
	private static long SUBSERVER_RECONNECTION_SLEEPTIME = 100;	// in ms
	private static int SUBSERVER_RESPONSE_MAXTIME = 2000;	// in ms
	
	private class userMulticastListener extends Thread
	{
		private MulticastSocket connSocketUDP = null;
		private InetAddress multicastUser = null;
		
		@SuppressWarnings("deprecation")
		public userMulticastListener()
		{
			setDaemon(true);
			try
			{
				connSocketUDP = new MulticastSocket(ConnectionConstants.userMulticastPort);
				multicastUser = InetAddress.getByName(ConnectionConstants.userMulticastAddress);
				connSocketUDP.joinGroup(multicastUser);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				byte buff[] = new byte[1024]; // 1KB buffer
				DatagramPacket recvPacket = new DatagramPacket(buff, buff.length);
				
				try
				{
					connSocketUDP.receive(recvPacket);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				
				String receivedStrings[] = new String(buff, StandardCharsets.UTF_8).split("\t");
				if(receivedStrings[0].equals(ConnectionConstants.subserverReconnectionRes) 
														&& receivedStrings[1].equals(userName))
				{ // subserver is changed ==> reconnect
					try
					{
						changeSubserver(InetAddress.getByName(receivedStrings[2]));
					} catch (UnknownHostException e)
					{
						e.printStackTrace();
					}
				}
				// TODO: add more operations, like main server changed...
			}
		}
		
		@SuppressWarnings("deprecation")
		void close()
		{
			if(connSocketUDP != null)
			{
				try
				{
					connSocketUDP.leaveGroup(multicastUser);
					connSocketUDP.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private class userUDPListener extends Thread
	{
		private DatagramSocket connSocketUDP = null;

		public userUDPListener()
		{
			setDaemon(true);
			try
			{
				connSocketUDP = new DatagramSocket(ConnectionConstants.userUDPort);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				byte buff[] = new byte[1024]; // 1KB buffer
				DatagramPacket recvPacket = new DatagramPacket(buff, buff.length);
				
				try
				{
					connSocketUDP.receive(recvPacket);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				
				String receivedStrings[] = new String(buff, StandardCharsets.UTF_8).split("\t");
				if(receivedStrings[0].equals(ConnectionConstants.subserverReconnectionRes))
				{ // subserver is changed ==> reconnect
					try
					{
						changeSubserver(InetAddress.getByName(receivedStrings[1]));
					} catch (UnknownHostException e)
					{
						e.printStackTrace();
					}
				}
				// TODO: add more operations, like main server changed...
			}
		}
		
		void close()
		{
			if(connSocketUDP != null)
				connSocketUDP.close();
		}
	}
	
	// non-static variables
	private DatagramSocket socketUDP = null;
	private InetAddress subserverAddr = null;
	private String userName = null;
	private userMulticastListener multicastListener;
	private userUDPListener udpListener;
	private TreeMap<String, ConcurrentFileHandler> subserversConfigFiles = new TreeMap<>();
	
	public ConnectionService()
	{
		try
		{
			multicastMainServer = InetAddress.getByName(ConnectionConstants.mainServerMulticastAddress);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		/*multicastListener = new userMulticastListener();
		udpListener = new userUDPListener();
		multicastListener.start();
		udpListener.start();*/
		new File(ConnectionConstants.configDir).mkdir();
		new File(ConnectionConstants.subserversConfigDir).mkdir();
		//loadDataFromConfigFile();
	}
	
	// working with configuration file
	private synchronized void loadDataFromConfigFile()
	{
		ConcurrentFileHandler configAddrFile = new ConcurrentFileHandler(new File(ConnectionConstants.ipAddrConfigFile));
		
		try(BufferedReader fileReader = new BufferedReader(new FileReader(configAddrFile.getFile())))
		{ // main server address (1st line)
			configAddrFile.startRead();
			String tmp = fileReader.readLine();
			configAddrFile.finishRead();
			if(!tmp.isEmpty()) mainServerAddr = InetAddress.getByName(tmp);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		String fileList[] = new File(ConnectionConstants.subserversConfigDir).list();
		for(String s : fileList)
			subserversConfigFiles.put(s, new ConcurrentFileHandler(new File(ConnectionConstants.subserversConfigDir + s)));
	}
	
	private synchronized void saveDataToConfigFile()
	{
		try
		{
			if(!(new File(ConnectionConstants.ipAddrConfigFile)).exists())
				new File(ConnectionConstants.ipAddrConfigFile).createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		ConcurrentFileHandler configAddrFile = new ConcurrentFileHandler(new File(ConnectionConstants.ipAddrConfigFile));
		try(FileWriter fw = new FileWriter(configAddrFile.getFile()))
		{ // main server address (1st line) & subserver address (2nd line)
			configAddrFile.startWrite();
			fw.write(String.format("%s", mainServerAddr.getHostAddress()));
			configAddrFile.finishWrite();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	// main server connection
	private synchronized void discoverMainServer() throws ErrorMulitcastGroup, ErrorServerNotFound
	{
		// maybe throw exception if multicastGroup is null
		if(mainServerAddr != null) 
		{
			System.out.println("Main server already found.");
			return; 
		}
		
		if(multicastMainServer == null)
			throw new ErrorMulitcastGroup(ConnectionConstants.mainServerMulticastAddress);
		
		// create UDP socket
		try
		{
			socketUDP = new DatagramSocket(ConnectionConstants.userUDPort);
		} catch (SocketException e1)
		{
			System.out.println("UDP socket not created");
			e1.printStackTrace();
			return;
		}
		
		// send automatic server discover message to multicast address
		byte buff[];
		buff = ConnectionConstants.mainServerDiscoverReqMsg.getBytes(StandardCharsets.UTF_8);
		DatagramPacket packetToSend = new DatagramPacket(buff, buff.length, multicastMainServer, ConnectionConstants.mainServerMulticastPort);
		try
		{
			socketUDP.send(packetToSend);
		} catch (IOException e)
		{
			System.out.println("Discover packet not send");
			e.printStackTrace();
			return;
		}
		
		// get server ip address
		buff = new byte[64];
		DatagramPacket receivedPacket = new DatagramPacket(buff, buff.length);
		
		try
		{ // specify maximum wait time for main server to response
			socketUDP.setSoTimeout(MAIN_SERVER_DISCOVER_RESPONSE_MAXTIME);
			socketUDP.receive(receivedPacket);
		} catch (IOException e)
		{
			System.out.println("Discover packet not received");
			e.printStackTrace();
			return;
		}
		
		//close UDP socket
		if(socketUDP != null) socketUDP.close();
		
		// check response in received packet
		if(!ConnectionConstants.mainServerDiscoverResMsg.equals(new String(receivedPacket.getData(), StandardCharsets.UTF_8).trim()))
		{
			System.out.println("Error while getting resposne message from main server. MSG: " + 
													new String(receivedPacket.getData(), StandardCharsets.UTF_8));
			throw new ErrorServerNotFound();
		}
		
		mainServerAddr = receivedPacket.getAddress();  // get main server address from received packet
		System.out.println("\n\n*** FOUND MAIN SERVER IP: " + mainServerAddr.getHostAddress() + " ***\n"); // TODO: izbrisati
		saveDataToConfigFile();
	}
	
	public synchronized Socket connectToMainServer()
	{
		if(mainServerAddr == null)
		{
			try
			{
				discoverMainServer();
				if(mainServerAddr == null) throw new ErrorServerNotFound();
			}
			catch(ErrorServerNotFound | ErrorMulitcastGroup e)
			{
				e.printStackTrace();
				return null;
			}
		}
		// create TCP connection to main server
		Socket mainServerSocketTCP = null;
		try
		{ // create new socket and try to connect to server with limited waiting time
			mainServerSocketTCP = new Socket();
			mainServerSocketTCP.connect(new InetSocketAddress(mainServerAddr, ConnectionConstants.userTCPort), MAIN_SERVER_RESPONSE_MAXTIME);
		} catch (IOException e)
		{
			System.out.println("TCP connection to main server not established");
			e.printStackTrace();
			return null;
		}
		
		if(!mainServerSocketTCP.isConnected()) return null;
		return mainServerSocketTCP;
	}
	
	public synchronized Socket reconnectToMainServer() throws ErrorFatalConnectionLost
	{
		
		Socket conn = null;
		for(int i = 0; i < MAIN_SERVER_RECONNECTION_ATTEMPTS; i++)
		{
			System.out.println("PROBA");  // TODO: izbaci ovo 
			mainServerAddr = null;
			conn = connectToMainServer();
			if(conn != null) return conn;
			try
			{ // wait specified time and try connecting again (active wait so GUI can work normally)
				Thread.sleep(MAIN_SERVER_RECONNECTION_SLEEPTIME);  // TODO: maybe create another thread ???
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		// reconnection failed
		throw new ErrorFatalConnectionLost();
	}

	
	// subserver connection
	private synchronized void changeSubserver(InetAddress address)
	{
		if(subserverAddr == null || address == null) return;
		
		// create temporary file and new conifg file (if needed)
		File tempFile = new File(ConnectionConstants.subserversConfigDir + "TEMP" + ConnectionConstants.configFileType);
		
		// get thread safe handles for all files (and create one for temp)
		ConcurrentFileHandler subserverOldConfigFileHandle = subserversConfigFiles.get(subserverAddr.getHostAddress() + ConnectionConstants.configFileType);
		ConcurrentFileHandler subserverNewConfigFileHandle = subserversConfigFiles.get(address.getHostAddress() + ConnectionConstants.configFileType);
		ConcurrentFileHandler tempFileHandle = new ConcurrentFileHandler(tempFile);
		
		// move user from old config file to new one
		try
		{
			tempFile.createNewFile();
			if(subserverNewConfigFileHandle == null)
			{
				File subserverNewConfigFile = new File(ConnectionConstants.subserversConfigDir + address.getHostAddress() + ConnectionConstants.configFileType);
				subserverNewConfigFile.createNewFile();
				subserverNewConfigFileHandle = new ConcurrentFileHandler(subserverNewConfigFile);
				subserversConfigFiles.put(address.getHostAddress() + ConnectionConstants.configFileType, subserverNewConfigFileHandle);
			}
			
			boolean oldFileEmpty = true;
			try(BufferedWriter fileWriter = new BufferedWriter(new FileWriter(subserverNewConfigFileHandle.getFile(), true));
				BufferedWriter tempWriter = new BufferedWriter(new FileWriter(tempFileHandle.getFile()));	
				BufferedReader fileReader = new BufferedReader(new FileReader(subserverOldConfigFileHandle.getFile())) )
			{
				String temp;
				while(true)
				{
					subserverOldConfigFileHandle.startRead();
					temp = fileReader.readLine();
					subserverOldConfigFileHandle.finishRead();
					if(temp == null) break;
					
					if(temp.equals(userName))
					{
						subserverNewConfigFileHandle.startWrite();
						fileWriter.append(temp + "\n");
						subserverNewConfigFileHandle.finishWrite();
					}
					else
					{
						if(oldFileEmpty) oldFileEmpty = false;
						tempFileHandle.startWrite();
						tempWriter.write(temp + "\n");
						tempFileHandle.finishWrite();
					}
				}
			}
			
			// delete old file
			subserverOldConfigFileHandle.startWrite();   // renaming file acts like writing to it
			subserverOldConfigFileHandle.getFile().delete();

			if(!oldFileEmpty)
			{ // if old file is not empty after changing, then rename temp file to old file
				tempFile.renameTo(subserverOldConfigFileHandle.getFile());
				if(tempFile.exists())
				{ // renameTo method didn't work, do it manually
					try
					{ // create new config file and copy all data from old one
						subserverOldConfigFileHandle.getFile().createNewFile();
						try(OutputStream fileWriter = new FileOutputStream(subserverOldConfigFileHandle.getFile());
							InputStream fileReader = new FileInputStream(tempFileHandle.getFile()))
						{
							byte buff[] = new byte[1 << 18]; // 256KB buffer
							int readDataSize;
							while((readDataSize = fileReader.read(buff)) > 0)
							{
								fileWriter.write(buff, 0, readDataSize);
							}
						} 
					}catch (IOException e)
					{
						System.out.println("Copying subserver config file went wrong");
						e.printStackTrace();
					}
					
					// at the end just delete temp file
					tempFile.delete();
				}
			}
			else // if old file is empty after changing, then just delete temp file as well
			{
				tempFile.delete();
				subserversConfigFiles.remove(subserverAddr.getHostAddress() + ConnectionConstants.configFileType);
			}
			
			subserverOldConfigFileHandle.finishWrite();
			
		}catch (IOException e)
		{
			System.out.println("Changing server config file went wrong");
			e.printStackTrace();
		}
		
		// after moving user, change "active" subserver
		subserverAddr = address;
	}

	private synchronized void addSubserver(InetAddress address)
	{
		File newSubserverConfigFile = new File(ConnectionConstants.subserversConfigDir + address.getHostAddress() + ConnectionConstants.configFileType);
		
		try
		{
			if(!newSubserverConfigFile.exists())
				newSubserverConfigFile.createNewFile();
		} catch (IOException e)
		{
			System.out.println("Creating subserver config file went wrong");
			e.printStackTrace();
		}
		subserversConfigFiles.put(address.getHostAddress() + ConnectionConstants.configFileType, new ConcurrentFileHandler(newSubserverConfigFile));
		subserverAddr = address;
	}
	
	public synchronized void changeActiveSubserver(InetAddress address)
	{
		if(!subserversConfigFiles.containsKey(address.getHostAddress() + ConnectionConstants.configFileType))
		{
			System.out.println("Connection to specified subserver was never established");
			return;
		}
		
		subserverAddr = address;
	}
	
	public synchronized Socket connectToSubserver()
	{
		if(subserverAddr == null) return null;
		
		Socket subserverSocketTCP = null;
		try
		{ // create new socket and try to connect to server with limited waiting time
			subserverSocketTCP = new Socket();
			subserverSocketTCP.connect(new InetSocketAddress(subserverAddr, ConnectionConstants.userTCPort), SUBSERVER_RESPONSE_MAXTIME);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(!subserverSocketTCP.isConnected()) return null;
		return subserverSocketTCP;
	}

	public synchronized Socket reconnectToSubserver() throws ErrorFatalConnectionLost
	{
		Socket conn = null;
		
		while(conn == null)
		{
			for(int i = 0; i < SUBSERVER_RECONNECTION_ATTEMPTS; i++)
			{
				conn = connectToSubserver();
				if(conn != null) return conn;
				try
				{ // wait specified time and try connecting again
					Thread.sleep(SUBSERVER_RECONNECTION_SLEEPTIME);  // TODO: maybe create another thread ???
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			// reconnecting failed, ask main server for new subserver
			// connect to main server
			conn = connectToMainServer();
			if(conn == null)
				conn = reconnectToMainServer();  // if main server connection doesn't work  ==> throw fatal connection lost exception
			
			// ask main server for reconnection
			try(ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(conn.getInputStream()))
			{
				TransferMessage msgToSend = new TransferMessage(TransferMessage.OPERATION_REQUEST);
				// request: SUBSERVER_RECONN_REQ	ip_addr_of_current_subserver	user_name
				String req = String.format("%s\t%s\t%s", ConnectionConstants.subserverReconnectionReq, subserverAddr.getHostAddress()
																													  , getUserName());
				msgToSend.setData(req.getBytes(StandardCharsets.UTF_8));
				out.writeObject(msgToSend);
				
				TransferMessage msgRecv = (TransferMessage) in.readObject();
				
				String received = new String(msgRecv.getData(), StandardCharsets.UTF_8);
				
				InetAddress tmpAddr = InetAddress.getByName(received);
				if(!subserverAddr.equals(tmpAddr))
					changeSubserver(tmpAddr);
				
			} catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			
			// try to connect to new subserver
			conn = connectToSubserver();
		}
		
		return conn;
	}

	public synchronized String getSubserverIpAddress()
	{
		if(subserverAddr == null) return null;
		return subserverAddr.getHostAddress();
	}

	
	// user operations
	public synchronized String getUserName()
	{
		return userName;
	}

	public synchronized void setUserName(String name)
	{
		this.userName = name;
	}
	
	public synchronized boolean saveCurrentUser()
	{
		if(subserverAddr == null) return false;
		
		if(!subserversConfigFiles.containsKey(subserverAddr.getHostAddress() + ConnectionConstants.configFileType)) return false;
		
		ConcurrentFileHandler subserverConfigFileHandle = subserversConfigFiles.get(subserverAddr.getHostAddress() + ConnectionConstants.configFileType);
		
		try(BufferedWriter fileWriter = new BufferedWriter(new FileWriter(subserverConfigFileHandle.getFile(), true)))
		{
			subserverConfigFileHandle.startWrite();
			fileWriter.append(String.format("%s\n", userName));
			subserverConfigFileHandle.finishWrite();
			return true;
		} catch (IOException e)
		{
			System.out.println("Saving user went wrong");
			e.printStackTrace();
			return false;
		}
		
	}
	
	public synchronized boolean setAndSaveUser(String name)
	{
		this.userName = name;
		return saveCurrentUser();
	}

	public synchronized boolean addAndSaveUser(String name, InetAddress subserverAddress)
	{
		addSubserver(subserverAddress);
		return setAndSaveUser(name);
	}
	
	
	public synchronized String logInUser(String userName, String password) throws ErrorFatalConnectionLost
	{
		Socket conn = null;
		
		//first try to find subserver address in config files
		Iterator<Entry<String, ConcurrentFileHandler>> iter = subserversConfigFiles.entrySet().iterator();
		LinkedList<FileBrowser> browsers = new LinkedList<>();
		while(iter.hasNext())
		{
			ConcurrentFileHandler fileHandle = iter.next().getValue(); 
			FileBrowser browser = new FileBrowser(fileHandle, userName);
			browsers.add(browser); // O(1)
			browser.start();
		}
		
		int n = browsers.size();
		for(int i = 0; i < n; i++)
		{
			FileBrowser browser = browsers.remove(0); // O(1)
			try
			{
				browser.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if(browser.browseResult())
			{
				try
				{
					subserverAddr = InetAddress.getByName(browser.getFileName().replace(ConnectionConstants.configFileType, ""));
				} catch (UnknownHostException e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
		
		// if there is not saved subserver address, ask main server for it
		if(subserverAddr == null)
		{
			conn = connectToMainServer();
			if(conn == null)
				conn = reconnectToMainServer();
			
			try(ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(conn.getInputStream()))
			{
				TransferMessage reqSubserver = new TransferMessage(TransferMessage.OPERATION_REQUEST);
				String req = String.format("%s\t%s", ConnectionConstants.subserverConnectionReq, userName);
				reqSubserver.setData(req.getBytes(StandardCharsets.UTF_8));
				out.writeObject(req);
				out.reset();
				
				// response: CONN_RESPONSE 	SUBSERVER_ADDR 	 or  CONN_ERR_RESPONSE	USER_NOT_EXIST
				reqSubserver = (TransferMessage) in.readObject();
				String results[] = new String(reqSubserver.getData(), StandardCharsets.UTF_8).split("\t");
				
				if(ConnectionConstants.subserverConnectionRes.equals(results[0]))
					addAndSaveUser(userName, InetAddress.getByName(results[1]));
				else if(ConnectionConstants.subserverConnectionErr.equals(results[0]))
					return LogInFrame.errWrongUserName;
				else
					return LogInFrame.errServerCommunication;
			} catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			conn = null;
		}
		
		if(subserverAddr != null) System.out.println(subserverAddr.getHostAddress());
		
		// connect to subserver and send credentials
		conn = connectToSubserver();
		if(conn == null)
			conn = reconnectToSubserver();
		
		try(ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(conn.getInputStream()))
		{
			TransferMessage msgToSend = new TransferMessage();
			// encrypt password using SHA-256
			MessageDigest passDigest = null;
			try
			{
				passDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			
			// create byte hash code and convert it to hex code
			byte codedBytePass[] = passDigest.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder codedHexPass = new StringBuilder();
	        for(byte b : codedBytePass)
	        	codedHexPass.append(String.format("%02X", b));
	        
	        // create request to send in form: USER_LOGIN_REQUEST	USER_NAME	USER_PASS_HASH
	        msgToSend.setData(String.format("%s\t%s\t%s", ConnectionConstants.userLoginReq, userName, codedHexPass).getBytes(StandardCharsets.UTF_8));
	        msgToSend.setMessageType(TransferMessage.OPERATION_REQUEST);
			out.writeObject(msgToSend);
			out.reset();
	        
			TransferMessage msgToRecv = (TransferMessage) in.readObject();
			String result[] = new String(msgToRecv.getData(), StandardCharsets.UTF_8).split("\t");
			if(ConnectionConstants.userLoginRes.equals(result[0]))
			{
				if(ConnectionConstants.userLoginSuccess.equals(result[1]))
				{
					setUserName(userName);
					return LogInFrame.logInSuccessMsg;
				}
				else if(ConnectionConstants.userLoginUserNameErr.equals(result[1]))
					return LogInFrame.errWrongUserName;
				else if(ConnectionConstants.userLoginPassErr.equals(result[1]))
					return LogInFrame.errWrongPass;
			/*	else TODO: mozda dodati da se ne moze korisnik prijviti na vise racunara
					return LogInFrame.errUserAlreadyLogged;*/
			}
			else if(ConnectionConstants.subserverReconnectionRes.equals(result[0]))
			{ // change subserver
				changeSubserver(InetAddress.getByName(result[1]));
				logInUser(userName, password); // TODO: proveriti ovu rekurziju
			}
			else
				return LogInFrame.errServerCommunication;
			
		} catch (IOException | ClassNotFoundException e1)
		{
			e1.printStackTrace();
		}
		
		throw new ErrorFatalConnectionLost();
	}
	
	public synchronized boolean createUser(String userName, String password)
	{
		// TODO: implement createUser
		return true;
	}
	
	public synchronized void close()
	{
		// TODO: implement close method
	}
	
	public static void main(String[] args)
	{
		ConnectionService conn = new ConnectionService();
		try
		{
			conn.discoverMainServer();
		} catch (ErrorMulitcastGroup e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorServerNotFound e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*try
		{
			conn.changeActiveSubserver(InetAddress.getByName("192.168.0.3"));
			conn.setUserName("proba");
			conn.changeSubserver(InetAddress.getByName("192.168.0.2"));
			
			Iterator<Entry<String, ConcurrentFileHandler>> iter = conn.subserversConfigFiles.entrySet().iterator();
			while(iter.hasNext())
			{
				System.out.println(iter.next().getKey());
			}
			
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
