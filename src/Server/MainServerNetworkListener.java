package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import Common.ConnectionConstants;

public class MainServerNetworkListener
{
	private class MulticastListener extends Thread
	{  // prima i obradjuje zahteve primljene na multicast-u centralnog servera
		private MulticastSocket connSocketUDP = null;
		private InetAddress multicastAddr = null;
		
		@SuppressWarnings("deprecation")
		public MulticastListener()
		{
			setDaemon(true);
			try
			{
				connSocketUDP = new MulticastSocket(ConnectionConstants.mainServerMulticastPort);
				multicastAddr = InetAddress.getByName(ConnectionConstants.mainServerMulticastAddress);
				connSocketUDP.joinGroup(multicastAddr);
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
				System.out.println(receivedStrings[0].trim());
				if(receivedStrings[0].trim().equals(ConnectionConstants.mainServerDiscoverReqMsg))
				{ // main server discover request 
					MainServerConnectionService conn = MainServerConnectionService.getInstance();
					conn.sendMainServerDiscoverResponse(recvPacket.getAddress());
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
					connSocketUDP.leaveGroup(multicastAddr);
					connSocketUDP.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public MainServerNetworkListener()
	{
		MulticastListener multicastListener = new MulticastListener();
		multicastListener.start();
	}
	
	public static void main(String[] args)
	{
		MainServerNetworkListener networkListener = new MainServerNetworkListener();
		for(int i = 0; i < 1000000000; i++)
			for(int j = 0; j < 1000000000; j++)
				for(int k = 0; k < 1000000000; k++);
	}
}
