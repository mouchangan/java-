
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
//jframe框框
//主体框架
public class HostGame extends JFrame
{
	private MainGame mainGame;	
	private InfoSave infoSave;
	private int tableNumber;
	private ChatSystem chatSystem;
	private String way;
//	private BufferedImage whiteDisk;
//	private BufferedImage blackDisk;
//	private BufferedImage curBlack;
//	private BufferedImage curWhite;
//	private BufferedImage seatEmpty;
//	private BufferedImage seatFull;
//	private BufferedImage gameImage;
//	private JPanel leftPanel;
//	private JPanel rightPanel;
//	private JLabel title = new JLabel(new ImageIcon("D:\\0\\fiveStone\\五子棋.jpg"));
//	static JLabel fullLabelR = new JLabel(new ImageIcon("D:\\0\\fiveStone\\seatFull.jpg"));
//	static JLabel fullLabelL = new JLabel(new ImageIcon("D:\\0\\fiveStone\\seatFull.jpg"));
//	static JLabel emptyLabelL = new JLabel(new ImageIcon("D:\\0\\fiveStone\\seatEmpty.jpg"));
//	static JLabel emptyLabelR = new JLabel(new ImageIcon("D:\\0\\fiveStone\\seatEmpty.jpg"));
	private PrintStream ps;

	public HostGame(int tableNumber , String way , InfoSave infoSave)
	{
		this.chatSystem = new ChatSystem(true , tableNumber);
		this.chatSystem.setInfoSave(infoSave);
		this.chatSystem.init();
		this.chatSystem.setMinimumSize(new Dimension(283 , 300));
		this.chatSystem.setTableSize(283 , 300);
		this.chatSystem.setTabbedPaneSize(283 , 295);
		this.chatSystem.setChatSize(283 , 40);
		this.chatSystem.setButtonSize(283 , 50);
		this.infoSave = infoSave;
		this.chatSystem.playersModel.addPlayer(infoSave.getImageIcon(), infoSave.getUserName(), infoSave.getSex());
		this.chatSystem.table.updateUI();
		this.tableNumber = tableNumber;
		this.way = way;

		mainGame = new MainGame(infoSave , way , tableNumber);

		this.infoSave.setMainGame(mainGame);

//		JPanel mainPanel = new JPanel();

		JSplitPane split = new JSplitPane();
		split.setRightComponent(chatSystem);
		split.setLeftComponent(mainGame);

//		JPanel leif = new JPanel();

		this.add(split);
			
		this.pack();
		this.setSize(new Dimension(1026 , 736));
		this.setTitle("欢迎" + infoSave.getUserName() + "来到 " + tableNumber + "号 台游戏");
		this.setVisible(true);

		windowClose();//关闭大厅窗口

		
	}

	public void windowClose()
	{	
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try
				{
					ps = new PrintStream(infoSave.getSocket().getOutputStream());
					infoSave.getGameHall().setVisible(true);
					ps.println(MyProtocol.GAME_EXIT + tableNumber + ":" + way + ";" +
							   infoSave.getMainGame().getPlayerSize() + MyProtocol.GAME_EXIT);
					if ( infoSave.getMainGame().isBegin())
					{
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				catch (Exception ee)
				{
					ee.printStackTrace();
				}
			}
		});
	}

    public ChatSystem getChatSystem() 
    {
       return chatSystem;
    }
}
