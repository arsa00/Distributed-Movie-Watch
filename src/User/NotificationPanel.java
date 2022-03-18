package User;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

public class NotificationPanel extends JPanel
{
	public static class NotificationItem extends JPanel
	{
		public NotificationItem()
		{
			this.setLayout(new GridLayout(0, 1));
			this.setPreferredSize(new Dimension(WelcomeFrame.frameWidth - 100, 150));
			JLabel notificationText = new JLabel("OBAVEŠTENJE", SwingConstants.CENTER);
			notificationText.setFont(new Font("Arial", Font.BOLD, 14));
			this.add(notificationText);
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.fillRect(0, 5, WelcomeFrame.frameWidth, 2);
		}
		
		public void addItem(Component comp)
		{
			this.add(comp);
		}
	}
	private JScrollPane body;
	private NotificationMenuItem notifyMenu;
	private WelcomeFrame parentFrame;
	private JPanel notifications;
	private GridBagConstraints c;
	
	public NotificationPanel(WelcomeFrame mainFrame, NotificationMenuItem menuItem)
	{
		notifications = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH; // TODO: proveritiii
	    body = new JScrollPane(notifications);
		body.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		body.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		body.setPreferredSize(new Dimension(WelcomeFrame.frameWidth - 65, WelcomeFrame.frameHeight - 50));
		notifyMenu = menuItem;
		parentFrame = mainFrame;
		this.add(body);
	}
	
	public /*synchronized*/ void addNotification(NotificationItem item)
	{
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy++;
		c.fill = GridBagConstraints.VERTICAL;
		notifications.add(item, c);
		
		if(!WelcomeFrame.selectNotificationMenuItem.equals(parentFrame.getSelectedMenuItem()))
			notifyMenu.setNotificationSatus();

		item.repaint();
	}
	
	public static void main(String[] args)
	{
		/*JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(500, 500));
		
		NotificationPanel panelN = new NotificationPanel();
		frame.add(panelN, BorderLayout.CENTER);
		
		NotificationItem item = new NotificationItem();
		item.add(new JLabel("ITEM 1:"));
		item.add(new JButton("Button 1"));
		panelN.addNotification(item);
		
		NotificationItem item2 = new NotificationItem();
		item2.add(new JLabel("ITEM 1:"));
		item2.add(new JButton("Button 1"));
		panelN.addNotification(item2);
		
		NotificationItem item3 = new NotificationItem();
		item3.add(new JLabel("ITEM 1:"));
		item3.add(new JButton("Button 1"));
		panelN.addNotification(item3);
		
		NotificationItem item4 = new NotificationItem();
		item4.add(new JLabel("ITEM 1:"));
		item4.add(new JButton("Button 1"));
		panelN.addNotification(item4);
		
	/*	Container c = frame.getContentPane();
		c.add(panelN.body);*/
	}
}
