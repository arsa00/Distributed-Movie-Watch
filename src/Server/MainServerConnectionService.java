package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import Common.ConnectionConstants;

public class MainServerConnectionService
{ // da li mora da bude synchronized???
	
	private static MainServerConnectionService conn = null;
	
	private MainServerConnectionService()
	{
		
	}
	
	
	public static synchronized MainServerConnectionService getInstance()
	{
		if(conn == null)
			conn = new MainServerConnectionService();
		
		return conn;
	}
	
	public synchronized void sendMainServerDiscoverResponse(InetAddress hostIP)
	{
		DatagramSocket socketUDP = null;
		try
		{
			socketUDP = new DatagramSocket();
		} catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		byte buff[];
		buff = ConnectionConstants.mainServerDiscoverResMsg.getBytes(StandardCharsets.UTF_8);
		DatagramPacket packetToSend = new DatagramPacket(buff, buff.length, hostIP, ConnectionConstants.userUDPort);
		try
		{
			socketUDP.send(packetToSend);
		} catch (IOException e)
		{
			System.out.println("Discover response not send");
			e.printStackTrace();
		}
		
		System.out.println("Discover response has been sent");
	}
	
}
