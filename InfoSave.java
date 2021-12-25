
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
//信息保存存储信息与读取
//将识别信息存储到类，用方法提取
// 可用于判断操作，与输出流信息控制操作
public class InfoSave 
{	
	private Socket socket;

	private String message;

	private String userName;

	private int icon;

	private ImageIcon imageIcon;

	private BufferedImage headIcon;

	private String sex;

	private String ip;

	private int port;

	private HallPanel hallPanel;

	private HeadPanel headPanel;

	private GameHall gameHall;

	private HostGame hostGame;

	private ChatSystem chatSystem;

	private LoginDialog loginDialog;

	private MainGame mainGame;

	private Client client;

	
	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}
	public Socket getSocket()
	{
		return this.socket;
	}
	
	public void setUserName(String name)
	{
		this.userName = name;
	}
	public String getUserName()
	{
		return this.userName;
	}
	
	public void setIcon(int icon)
	{
		this.icon = icon;
	}
	public int getIcon()
	{
		return this.icon;
	}
	
	public void setHeadIcon(BufferedImage headIcon)
	{
		this.headIcon = headIcon;
	}
	public BufferedImage getHeadIcon()
	{
		return this.headIcon;
	}

	public void setImageIcon(ImageIcon imageIcon)
	{
		this.imageIcon = imageIcon;
	}
	public ImageIcon getImageIcon()
	{
		return this.imageIcon;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}
	public String getSex()
	{
		return this.sex;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
	public String getMessage()
	{
		return this.message;
	}
	
	public void setIP(String ip)
	{
		this.ip = ip;
	}
	public String getIP()
	{
		return this.ip;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	public int getPort()
	{
		return this.port;
	}

	public void setGameHall(GameHall gameHall)
	{
		this.gameHall = gameHall;
	}
	public GameHall getGameHall()
	{
		return this.gameHall;
	}

	public void setChatSystem(ChatSystem chatSystem)
	{
		this.chatSystem = chatSystem;
	}
	public ChatSystem getChatSystem()
	{
		return this.chatSystem;
	}
	
	public void setLoginDialog(LoginDialog loginDialog)
	{
		this.loginDialog = loginDialog;
	}
	public LoginDialog getLoginDialog()
	{
		return this.loginDialog;
	}
	
	public void setHallPanel(HallPanel hallPanel)
	{
		this.hallPanel = hallPanel;
	}
	public HallPanel getHallPanel()
	{
		return this.hallPanel;
	}

	public void setHostGame(HostGame hostGame)
	{
		this.hostGame = hostGame;
	}
	public HostGame getHostGame()
	{
		return this.hostGame;
	}

	public void setHeadPanel(HeadPanel headPanel)
	{
		this.headPanel = headPanel;
	}
	public HeadPanel getHeadPanel()
	{
		return this.headPanel;
	}

	public void setMainGame(MainGame mainGame) { this.mainGame = mainGame; }
	public MainGame getMainGame() { return this.mainGame;}

	public void setClient(Client client)
	{
		this.client = client;
	}
	public Client getClient()
	{
		return this.client;
	}
}
