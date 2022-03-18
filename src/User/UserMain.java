package User;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import Common.ConnectionConstants;

public class UserMain
{
	static ConnectionService connectionService;
	static LogInFrame logInFrame;
	static WelcomeFrame mainFrame;
	
	private static final File startedFile = new File(ConnectionConstants.configDir + "Running.conf");;
	
	public static void main(String[] args)
	{
		// create running file
		if(startedFile.exists())
		{
			JOptionPane.showMessageDialog(logInFrame, "Jedna instanca programa je vec aktivirana.",
																"UPOZORENJE", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try
		{
			startedFile.createNewFile();
		} catch (IOException e)
		{
			System.err.println("Error while creating running config file");
			e.printStackTrace();
		}
		connectionService = new ConnectionService();
		logInFrame = new LogInFrame();
		//mainFrame = new WelcomeFrame("Proba");
	}
	
	static void closeUserProgram()
	{ // delete running file and close all frames and sockets
		System.out.println("Closing...");
		startedFile.delete();
		if(logInFrame != null) logInFrame.close();
		if(mainFrame != null) mainFrame.close();
		if(connectionService != null) connectionService.close();
	}
}
