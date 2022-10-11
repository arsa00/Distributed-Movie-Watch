package User;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.MediaPlayerSpecs.EmbeddedMediaPlayerSpec;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.videosurface.ComponentVideoSurface;

public class Player extends JFrame
{
	private class TimeLineThread extends Thread
	{
		private boolean inc;
		private boolean toRun;
		public TimeLineThread()
		{
			super("Time Line Thread");
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			toRun = true;
			while(toRun)
			{
				inc = true;
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					inc = false;
				}
				if(toRun == false) break;
				int sec = getTimeElapsed() / 1000;
				int min = (sec / 60) % 60;
				int hours = sec / 3600;
				sec = sec % 60;
				
				setTimeLine(String.format("%02d:%02d:%02d", hours, min, sec));
			}

			//close();   // ovo svakako mora da se izbaci 
		}
		
		public void finish()
		{
			toRun = false;
		}
	}
	
	private class PlayerMouseListener extends Thread
	{
		private JFrame parent;
		
		public PlayerMouseListener(JFrame parentFrame)
		{
			super("Player Mouse Listener");
			parent = parentFrame;
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			while(true)
			{
				Point pos = parent.getMousePosition();
				try
				{
					if(pos.getY() >= parent.getHeight() - controlsHeight)
					{
						if(mediaPlayer.fullScreen().isFullScreen())
							controlsPane.setVisible(true);
					}
					else
					{
						if(mediaPlayer.fullScreen().isFullScreen())
							controlsPane.setVisible(false);
					}
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
	
	private EmbeddedMediaPlayerComponent player;
	private EmbeddedMediaPlayer mediaPlayer;
	private JLabel playButton, stopButton, fullScreenButton, rewindButton, skipButton;
	private JPanel controlsPane;
	private JSlider timelineSlider;
	private JLabel timeLabel, volumeLabel;
	private final int controlsHeight = 70;
	
	private boolean adminPrivilege, videoPlaying, videoLoaded;
	private String moviePath = "G:\\Filmovi & Serije\\Despicable Me (2010)\\Despicable.Me.2010.720p.BluRay.x264.YIFY.mp4";
	private TimeLineThread timeLineThread;
	private PlayerMouseListener playerMouseThread;
	private boolean isMousePressed = false, isClosed = false;
	
	public Player(boolean administratorPrivilege)
	{
		this.adminPrivilege = administratorPrivilege;
		timeLineThread = new TimeLineThread();
		playerMouseThread = new PlayerMouseListener(this);
		playerMouseThread.start();
		videoPlaying = false;
		videoLoaded = false;
		this.setBounds(50, 50, 800, 500);
		
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				close();
			}
		});
		
		NativeLibrary.addSearchPath(uk.co.caprica.vlcj.binding.RuntimeUtil.getLibVlcLibraryName(), "vlcLib");
		player = new EmbeddedMediaPlayerComponent();
		mediaPlayer = player.mediaPlayer();
		mediaPlayer.fullScreen().strategy(new AdaptiveFullScreenStrategy(this));
		
		JPanel contentPane = new JPanel();
	    contentPane.setLayout(new BorderLayout());   	 
	    contentPane.add(player, BorderLayout.CENTER);
	    controlsPane = new JPanel(new GridBagLayout());
	    controlsPane.setBackground(Color.WHITE);
	    GridBagConstraints c = new GridBagConstraints();
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridwidth = 5;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    
	    JPanel timelinePanel = new JPanel(new FlowLayout());
	    timelinePanel.setBackground(Color.WHITE);
	    timeLabel = new JLabel("00:00:00");
	    timelinePanel.add(timeLabel);
	    timelineSlider = new JSlider();
	    timelineSlider.setValue(0);
	    timelineSlider.setEnabled(false);
	    timelineSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    timelineSlider.setPreferredSize(new Dimension(this.getWidth() - 100, 20));
	    timelineSlider.setBackground(Color.WHITE);
	    
	    timelineSlider.addMouseListener(new MouseAdapter()
		{
	    	@Override
	    	public void mousePressed(MouseEvent e)
	    	{
	    		isMousePressed = true;
	    	}
	    	
			@Override
			public void mouseReleased(MouseEvent e)
			{
				isMousePressed = false;
			}
		});
	    
	    timelineSlider.addChangeListener(new ChangeListener()
		{
			
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if(isMousePressed)
					changeVideoTime(timelineSlider.getValue());
			}
		});
	    
	    timelinePanel.add(timelineSlider);
	    controlsPane.add(timelinePanel, c);
	    
	    c.gridx = 0;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    c.ipadx = 10;
	    playButton = new JLabel();
	    playButton.setIcon(new ImageIcon("Icons/playIcon.png"));
	    playButton.setHorizontalAlignment(SwingConstants.CENTER);
	    playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    playButton.addMouseListener(new MouseAdapter()
		{			
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(playButton.isEnabled())
					playVideo();
			}
		});
	    controlsPane.add(playButton, c);
	    
	    c.gridx = 1;
	    c.gridy = 1;
	    rewindButton = new JLabel();
	    rewindButton.setIcon(new ImageIcon("Icons/rewindIcon.png"));
	    rewindButton.setHorizontalAlignment(SwingConstants.CENTER);
	    rewindButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    rewindButton.addMouseListener(new MouseAdapter()
		{			
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(rewindButton.isEnabled())
					rewindVideo(10); // 10 sec
			}
		});
	    controlsPane.add(rewindButton, c);
	    
	    c.gridx = 2;
	    c.gridy = 1;
	    skipButton = new JLabel();
	    skipButton.setIcon(new ImageIcon("Icons/skipIcon.png"));
	    skipButton.setHorizontalAlignment(SwingConstants.CENTER);
	    skipButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    skipButton.addMouseListener(new MouseAdapter()
		{			
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(skipButton.isEnabled())
					skipVideo(10);  // 10 sec
			}
		});
	    controlsPane.add(skipButton, c);
		  	    
	    c.gridx = 3;
	    c.gridy = 1;
	    c.weightx = 1;
	    JPanel volumePanel = new JPanel(new FlowLayout());
	    volumePanel.setBackground(Color.WHITE);
	    volumeLabel = new JLabel("100");
	    volumeLabel.setIcon(new ImageIcon("Icons/volumeIcon.png"));
	    volumePanel.add(volumeLabel);
	    JSlider volumeSlider = new JSlider(0, 200, 100);
	    volumeSlider.setBackground(Color.WHITE);
	    volumeSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    volumeSlider.setPreferredSize(new Dimension(250, 20));
	    volumeSlider.addChangeListener(new ChangeListener()
		{
			
			@Override
			public void stateChanged(ChangeEvent e)
			{
				changeVolume(volumeSlider.getValue());
			}
		});
	    volumePanel.add(volumeSlider);
	    controlsPane.add(volumePanel, c);
	    
	    c.gridx = 4;
	    c.gridy = 1;
	    c.weightx = 0;
	    fullScreenButton = new JLabel();
	    fullScreenButton.setIcon(new ImageIcon("Icons/fullscreen.png"));
	    fullScreenButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    fullScreenButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				toggleFullscreen();
			}
		});
	    controlsPane.add(fullScreenButton, c);
	    controlsPane.setPreferredSize(new Dimension(0, controlsHeight));
	    
	    contentPane.add(controlsPane, BorderLayout.SOUTH);
		this.add(contentPane);
		
		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				timelineSlider.setPreferredSize(new Dimension(getWidth() - 100, 20));
			}
		});
		
		setPrivilege();
		this.setVisible(true);
	}
	
	private void loadVideo()
	{
		mediaPlayer.media().startPaused(moviePath);
		videoLoaded = true;
		timelineSlider.setMaximum((int)mediaPlayer.status().length());
		timelineSlider.setEnabled(true);
		timeLineThread.start();
	}
	
	private void playVideo()
	{
		if(!videoLoaded)
			loadVideo();
		
		if(!videoPlaying) 
		{
			mediaPlayer.controls().play();
			videoPlaying = true;
			playButton.setIcon(new ImageIcon("Icons/pauseIcon.png"));
		}
		else 
		{
			mediaPlayer.controls().pause();
			videoPlaying = false;
			playButton.setIcon(new ImageIcon("Icons/playIcon.png"));
		}
	}
	
	private void stopVideo()
	{
		mediaPlayer.controls().stop();
		videoPlaying = false;
		videoLoaded = false;
		timeLabel.setText("00:00:00");
		timelineSlider.setValue(0);
		timelineSlider.setEnabled(false);
		playButton.setIcon(new ImageIcon("Icons/playIcon.png"));
	}
	
	private void toggleFullscreen()
	{
		mediaPlayer.fullScreen().toggle();
		timelineSlider.setPreferredSize(new Dimension(getWidth() - 100, 20));
		if(!mediaPlayer.fullScreen().isFullScreen())
			controlsPane.setVisible(true);
	}
	
	private void changeVolume(int value)
	{
		mediaPlayer.audio().setVolume(value);
		volumeLabel.setText(String.format("%d", value));
	}
	
	private void rewindVideo(int seconds)
	{
		mediaPlayer.controls().skipTime(-seconds*1000);
		timeLineThread.interrupt();
	}
	
	private void skipVideo(int seconds)
	{
		mediaPlayer.controls().skipTime(seconds*1000);
		timeLineThread.interrupt();
	}
	
	private void changeVideoTime(int milis)
	{
		if(!videoLoaded) return;
		if(milis > mediaPlayer.status().length()) milis = (int)mediaPlayer.status().length()/* - 10*/;
		mediaPlayer.controls().setTime(milis);
		timeLineThread.interrupt();
	}
	
	private synchronized void setTimeLine(String time)
	{
		if(!videoLoaded) return;

		timeLabel.setText(time);
		timelineSlider.setValue((int)mediaPlayer.status().time());
		if(mediaPlayer.status().time() == mediaPlayer.status().length())
		{
			mediaPlayer.controls().setTime(0);
			//timeLineThread.finish();
			stopVideo(); // TODO: moze ovako, ali ne sme da se pokrece timeLineThread svaki put u load-u videa, treba popraviti i sam timeLineThread...
		}
	}
	
	private synchronized int getTimeElapsed()
	{
		int ret = 0;
		try
		{
			ret = (int)mediaPlayer.status().time();
		}
		catch(Exception e) {}
		
		return ret;
	}
	
	private void setPrivilege()
	{
		playButton.setEnabled(adminPrivilege);
		skipButton.setEnabled(adminPrivilege);
		rewindButton.setEnabled(adminPrivilege);
	}
	
	
	private void close()
	{
		if(isClosed) return;
		
		try
		{
			timeLineThread.finish();
			stopVideo();
			player.release();
			this.dispose();
			isClosed = true;
			UserMain.mainFrame.setEnabled(true);
		}
		catch(Exception e)
		{
			System.out.println("AAA" + e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		Player p = new Player(true);
	}
}
