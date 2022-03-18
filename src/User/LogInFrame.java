package User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import Errors.ErrorFatalConnectionLost;
import User.ConnectingToServerPanel.ConnectingToServerFrame;
import User.ConnectionToServerLostFrame;

public class LogInFrame extends JFrame
{
	private JPanel mainPanel;
	private JLabel errorMessage;
	private JLabel passwordRptLbl;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private JPasswordField passwordRptField;
	private JLabel requiredField3;
	private JLabel mainLbl;
	private JButton logInBtn;
	private JButton createUserBtn;
	private boolean isLogging = true; // if false ==> register
	
	private static final Color backgroundColor = new Color(0xcb3c3e);
	private static final Color buttonsColor = new Color(0xffffff);
	private static final Color textFieldsColor = new Color(0x831618);
	
	private static final Color buttonsTextColor = new Color(0);
	private static final Color textFieldsTextColor = new Color(0xffffff);
	private static final Color labelsTextColor = new Color(0xffcf7d);
	
	private static final Font buttonsFont = new Font("Lucida Console", Font.BOLD, 15);
	private static final Font labelsFont = new Font("Lucida Sans Unicode", Font.BOLD, 18);
	private static final Font textFieldsFont = new Font("Arial", Font.BOLD, 14);
	private static final Font headerFont = new Font("Lucida Sans Unicode", Font.BOLD, 35);
	private static final Font errorMessageFont = new Font("Arial", Font.BOLD, 11);
	
	private static final LineBorder textFieldDefBorder = new LineBorder(Color.WHITE, 3);
	private static final LineBorder textFieldFocusBorder = new LineBorder(Color.YELLOW, 3);
	private static final LineBorder buttonDefBorder = new LineBorder(Color.WHITE, 3);
	private static final LineBorder buttonFocusBorder = new LineBorder(Color.BLACK, 3);
	private static final LineBorder buttonActiveBorder = new LineBorder(Color.RED, 3);
	
	static final String errRequiredFields = "Sva polja sa zvezdicom(*) su obavezna!";
	static final String errUserNameInUser = "Uneseno koriničko ime je već u upotrebi";
	static final String errPassNotSame = "Lozinke se ne podudaraju";
	static final String errPassShort = "Lozinka mora sadržati barem 8 karaktera";
	static final String errWrongUserName = "Korisničko ime nije validno";
	static final String errWrongPass = "Lozinka ime nije validna";
	// static final String errUserAlreadyLogged = "Korisnik je već prijavljen na nekom računaru.";
	static final String errServerCommunication = "<html>Došlo je do greške prilikom povezivanja<br />Pokušajte ponovo</html>";
	
	public LogInFrame()
	{
		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(backgroundColor);
		GridBagConstraints c = new GridBagConstraints();
		
		// header label
		JPanel headerPanel = new JPanel(new FlowLayout());
		headerPanel.setBackground(backgroundColor);
		
		mainLbl = new JLabel("Prijavite se", SwingConstants.CENTER);
		mainLbl.setForeground(Color.BLACK);
		mainLbl.setBackground(backgroundColor);
		mainLbl.setOpaque(true);
		mainLbl.setFont(headerFont);
		
		headerPanel.add(mainLbl);
		this.add(headerPanel, BorderLayout.NORTH);
		
		// user name
		JLabel userNameLbl = new JLabel("KORISNIČKO IME:    ");
		userNameLbl.setFont(labelsFont);
		userNameLbl.setForeground(labelsTextColor);
		userNameField = new JTextField();
		userNameField.setPreferredSize(new Dimension(250, 30));
		userNameField.setToolTipText("Ovde uneti korisničko ime");
		userNameField.setBackground(textFieldsColor);
		userNameField.setFont(textFieldsFont);
		userNameField.setForeground(textFieldsTextColor);
		userNameField.setCaretColor(textFieldsTextColor);
		userNameField.setBorder(textFieldDefBorder);
		userNameField.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusLost(FocusEvent e)
			{
				userNameField.setBorder(textFieldDefBorder);
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				userNameField.setBorder(textFieldFocusBorder);
			}
		});
		JLabel requiredField = new JLabel(" *");
		requiredField.setFont(new Font("Arial", Font.BOLD, 25));
		requiredField.setForeground(Color.YELLOW);
		
		c.gridx = 1;
		c.weighty = 0.1;
		c.gridy++;
		mainPanel.add(userNameLbl, c);
		c.gridx = 2;
		mainPanel.add(userNameField, c);
		c.gridx = 3;
		mainPanel.add(requiredField, c);
		
		// password
		JLabel passwordLbl = new JLabel("LOZINKA:");
		passwordLbl.setFont(labelsFont);
		passwordLbl.setForeground(labelsTextColor);
		passwordField = new JPasswordField();
		passwordField.setPreferredSize(new Dimension(250, 30));
		passwordField.setToolTipText("Ovde uneti lozinku");
		passwordField.setBackground(textFieldsColor);
		passwordField.setFont(textFieldsFont);
		passwordField.setForeground(textFieldsTextColor);
		passwordField.setCaretColor(textFieldsTextColor);
		passwordField.setBorder(textFieldDefBorder);
		passwordField.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusLost(FocusEvent e)
			{
				passwordField.setBorder(textFieldDefBorder);
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				passwordField.setBorder(textFieldFocusBorder);
			}
		});
		JLabel requiredField2 = new JLabel(" *");
		requiredField2.setFont(new Font("Arial", Font.BOLD, 25));
		requiredField2.setForeground(Color.YELLOW);
		
		c.gridx = 1;
		c.gridy++;
		mainPanel.add(passwordLbl, c);
		c.gridx = 2;
		mainPanel.add(passwordField, c);
		c.gridx = 3;
		mainPanel.add(requiredField2, c);
		
		// password repeat
		passwordRptLbl = new JLabel("PONOVITE LOZINKU:    ");
		passwordRptLbl.setFont(labelsFont);
		passwordRptLbl.setForeground(labelsTextColor);
		passwordRptField = new JPasswordField();
		passwordRptField.setPreferredSize(new Dimension(250, 30));
		passwordRptField.setToolTipText("Ovde ponovo uneti lozinku");
		passwordRptField.setBackground(textFieldsColor);
		passwordRptField.setFont(textFieldsFont);
		passwordRptField.setForeground(textFieldsTextColor);
		passwordRptField.setCaretColor(textFieldsTextColor);
		passwordRptField.setBorder(textFieldDefBorder);
		passwordRptField.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusLost(FocusEvent e)
			{
				passwordRptField.setBorder(textFieldDefBorder);
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				passwordRptField.setBorder(textFieldFocusBorder);
			}
		});
		requiredField3 = new JLabel(" *");
		requiredField3.setFont(new Font("Arial", Font.BOLD, 25));
		requiredField3.setForeground(Color.YELLOW);
		
		// error message
		JPanel lastItemPanel = new JPanel(new GridLayout(2, 1, 0, 5));
		lastItemPanel.setBackground(backgroundColor);
		errorMessage = new JLabel();
		errorMessage.setFont(errorMessageFont);
		errorMessage.setForeground(Color.YELLOW);
		errorMessage.setOpaque(true);
		errorMessage.setBackground(backgroundColor);
		
		Image errIcon = null;
		try
		{
			errIcon = ImageIO.read(new File("Icons/errorIcon.png"));
			errorMessage.setIcon(new ImageIcon(errIcon));
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		errorMessage.setVisible(false);
		
		lastItemPanel.add(passwordRptField);
		lastItemPanel.add(errorMessage);
		
		// design fix
		JPanel lastItemPanel2 = new JPanel(new GridLayout(2, 1, 0, 5));
		lastItemPanel2.setBackground(backgroundColor);
		JLabel errorMessage2 = new JLabel("");
		errorMessage2.setFont(new Font("Arial", Font.BOLD, 12));
		errorMessage2.setVisible(false);
		lastItemPanel2.add(passwordRptLbl);
		lastItemPanel2.add(errorMessage2);
		
		// design fix 2
		JPanel lastItemPanel3 = new JPanel(new GridLayout(2, 1, 0, 5));
		lastItemPanel3.setBackground(backgroundColor);
		JLabel errorMessage3 = new JLabel("");
		errorMessage3.setFont(new Font("Arial", Font.BOLD, 12));
		errorMessage3.setVisible(false);
		lastItemPanel3.add(requiredField3);
		lastItemPanel3.add(errorMessage3);
		
		c.gridx = 1;
		c.gridy++;
		mainPanel.add(lastItemPanel2, c);
		c.gridx = 2;
		mainPanel.add(lastItemPanel, c);
		c.gridx = 3;
		mainPanel.add(lastItemPanel3, c);
		
		
		// log in button
		logInBtn = new JButton("PRIJAVITE SE");
		logInBtn.setFont(buttonsFont);
		logInBtn.setPreferredSize(new Dimension(200, 40));
		logInBtn.setBackground(buttonsColor);
		logInBtn.setForeground(buttonsTextColor);
		logInBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logInBtn.setBorder(buttonActiveBorder);
		logInBtn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(isLoggingPage())
					logInBtn.setBorder(buttonActiveBorder);
				else
					logInBtn.setBorder(buttonDefBorder);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				logInBtn.setBorder(buttonFocusBorder);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(isLoggingPage())
					startLogIn();
				else
					changeToLogInForm();
			}
		});
		
		createUserBtn = new JButton("KREIRAJTE NALOG");
		createUserBtn.setFont(buttonsFont);
		createUserBtn.setPreferredSize(new Dimension(200, 40));
		createUserBtn.setBackground(buttonsColor);
		createUserBtn.setForeground(buttonsTextColor);
		createUserBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		createUserBtn.setBorder(buttonDefBorder);
		createUserBtn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(!isLoggingPage())
					createUserBtn.setBorder(buttonActiveBorder);
				else
					createUserBtn.setBorder(buttonDefBorder);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				createUserBtn.setBorder(buttonFocusBorder);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(!isLoggingPage())
					createUser();
				else
					changeToRegisterForm();
			}
		});
		
		JPanel btnPanel = new JPanel();
		btnPanel.setBackground(backgroundColor);
		btnPanel.add(createUserBtn);
		btnPanel.add(logInBtn);
		this.add(btnPanel, BorderLayout.SOUTH);
		
		BufferedImage iconImage = null;
		try
		{
			iconImage = ImageIO.read(new File(WelcomeFrame.iconPath));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		repeatPassSetVisible(false);
		
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				UserMain.closeUserProgram();
				dispose();
			}
		});
		
		this.getContentPane().setBackground(backgroundColor);
		this.setTitle(WelcomeFrame.programName);
		this.setIconImage(iconImage);
		this.add(mainPanel);
		this.setBounds(25, 25, 500, 500);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private static String logPage = "Log";
	private static String registerPage = "Reg";
	
	synchronized void changePage(String page)
	{
		if(logPage.equals(page) && !isLogging)
			changeToLogInForm();
		else if(registerPage.equals(page) && isLogging)
			changeToRegisterForm();
	}
	
	private synchronized boolean isLoggingPage()
	{
		return isLogging;
	}
	
	private synchronized void changeToLogInForm()
	{
		isLogging = true;
		clearErrorMessage();
		mainLbl.setText("Prijavite se");
		repeatPassSetVisible(false);
		logInBtn.setBorder(buttonActiveBorder);
		createUserBtn.setBorder(buttonDefBorder);
	}
	
	private synchronized void changeToRegisterForm()
	{
		isLogging = false;
		clearErrorMessage();
		mainLbl.setText("Kreirajte nalog");
		repeatPassSetVisible(true);
		createUserBtn.setBorder(buttonActiveBorder);
		logInBtn.setBorder(buttonDefBorder);
	}
	
	private synchronized void repeatPassSetVisible(boolean visible)
	{
		// set register part (not)visible
		passwordRptLbl.setVisible(visible);
		passwordRptField.setVisible(visible);
		requiredField3.setVisible(visible);
	}
	
	private synchronized void setErrorMessage(String errorMsg)
	{
		errorMessage.setText(errorMsg);
		errorMessage.setVisible(true);
	}
	
	private synchronized void clearErrorMessage()
	{
		errorMessage.setText("");
		errorMessage.setVisible(false);
	}
	
	private synchronized void createUser()
	{
		
	}
	
	// log in
	private ConnectingToServerFrame connFrame = null;
	static final String logInSuccessMsg = "LOGIN_SUCCESS";
	private class LogInThread extends Thread
	{
		public LogInThread()
		{
			super("LogIn Thread");
			// setDaemon(true);
		}
		
		@Override
		public void run()
		{
			String userName = userNameField.getText();
			String password = new String(passwordField.getPassword());
			String result = null;
			try
			{
				result = UserMain.connectionService.logInUser(userName, password);
			} catch (ErrorFatalConnectionLost e)
			{
				e.printStackTrace();
				ConnectionToServerLostFrame connLostFrame = new ConnectionToServerLostFrame(ConnectionToServerLostFrame.logInFrame, logPage);
				setVisible(false);
				if(connFrame != null) connFrame.dispose();
				connFrame = null;
				return;
			}
			password = null;
			finishLogIn(result, userName);
		}
	}
	private synchronized void startLogIn()
	{
		connFrame = new ConnectingToServerFrame();
		this.setVisible(false);
		LogInThread logInThread = new LogInThread();
		logInThread.start();
	}
	
	private synchronized void finishLogIn(String result, String userName)
	{
		if(connFrame != null) connFrame.dispose();
		connFrame = null;

		if(logInSuccessMsg.equals(result))
		{
			UserMain.mainFrame = new WelcomeFrame(userName);
			this.dispose();
		}
		else
		{
			setErrorMessage(result);
			this.setVisible(true);
		}
	}
	
	public synchronized void close()
	{
		this.dispose();
	}
	
	public static void main(String[] args)
	{
		LogInFrame logInWindow = new LogInFrame();
	}
}
