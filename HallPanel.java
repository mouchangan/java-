
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.net.*;
//大厅中图片接口类
//图片大小使用
//大厅判断规则、使用-2或2判断左右座
//将图片和接口绑定
//大厅面板
public class HallPanel extends JPanel
{
	private InfoSave infoSave;
	private int tableTotal;
	private int rowTable;
	private int column;
	private int tableHigh;
	private int tableWide;
	private int panelHigh;
	private int panelWide;
	private int posX;
	private int posY;
	private int oX;
	private int oY;
    private int[][] tablePeople;
	private int location;
	private String ip = "192.168.38.150";
	private int port = 50000;
	private BufferedImage table;
	private BufferedImage tableVacancy;
	private BufferedImage selectTable;
	private BufferedImage tableFull;
	private Rectangle leftTableArea = new Rectangle(10, 50, 30, 30);
	private Rectangle rightTableArea = new Rectangle(100, 50, 30, 30);
	private PrintStream ps;
	private final int IMAGE_SUM = 30;
	private int headRow;
	private int headColumn;
	private int fullTalbeRow;
	private int fullTalbeColumn;
	private int number;		    //台号
	private int icon = 0;
	private int[][][] headImageLeft;
	private int[][][] headImageRight;
	private int[][]	tablePlay;
	
	public HallPanel(int tableTotal , int rowTable)
	{
		try
		{
			this.tableTotal = tableTotal;
			this.rowTable = rowTable;
			tablePeople = new int[tableTotal][2];
			table = ImageIO.read(new File("D:\\0\\fiveStone\\table.jpg"));
			tableVacancy = ImageIO.read(new File("D:\\0\\fiveStone\\tableFull.jpg"));
			selectTable = ImageIO.read(new File("D:\\0\\fiveStone\\selectTable.gif"));
			tableFull = ImageIO.read(new File("D:\\0\\fiveStone\\tableFull.jpg"));
			
			this.tableHigh = table.getHeight();
			this.tableWide = table.getWidth();
			this.column = tableTotal / rowTable;
			headImageLeft = new int[rowTable][column][IMAGE_SUM];
			headImageRight = new int[rowTable][column][IMAGE_SUM];
			tablePlay = new int[rowTable][column];
			if (tableTotal <= rowTable)
			{
				panelHigh = tableHigh;
				panelWide = tableWide * tableTotal;
			}
			else
			{
				if (tableTotal % rowTable != 0)
				{
					this.column++;
				}
				panelHigh = tableHigh * column;
				panelWide = tableWide * rowTable;
			}
			this.setPreferredSize(new Dimension(panelWide, panelHigh));
			event();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void event()
	{
		this.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				mouseClick(e.getX() , e.getY());
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{			
				mouseMoving(e.getX(), e.getY());
			}
		});
	}

	public void mouseMoving(int x , int y)
	{
		Graphics g = this.getGraphics();
		oX = x % tableWide;    //图片table X轴的偏移量
		oY = y % tableHigh;	   //图片table Y轴的偏移量
		if (leftTableArea.contains(oX, oY))
		{
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			g.drawImage(selectTable , (int)(tableWide * (x / tableWide)) + 7, (int)(tableHigh * (y / tableHigh)) + 49, null);
		}
		else if (rightTableArea.contains(oX, oY))
		{
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			g.drawImage(selectTable , (int)(tableWide * (x / tableWide)) + 97, (int)(tableHigh * (y / tableHigh)) + 49, null);
		}
		else
		{
			this.setCursor(Cursor.getDefaultCursor());
			this.repaint();
		}
		
	}
	
	public void mouseClick(int x , int y)
	{
		Graphics g = this.getGraphics();
		oX = x % tableWide;    //图片table X轴的偏移量
		oY = y % tableHigh;	   //图片table Y轴的偏移量
		int tableNumber = (x - 10)/ tableWide + (y - 52)/ tableHigh * rowTable + 1;
		try
		{
			countNumber(tableNumber);
			//-----点击左边的桌子------//			
			if (leftTableArea.contains(oX, oY))
			{
				//----------判断点击的桌子是否已经有人了----------//
				int left = 0;
				for (int i = 0; i < IMAGE_SUM ; i++)
				{
					if (this.headImageLeft[headRow][headColumn][i] == 1)
					{
						left++;
					}
					
				}
				if (left == 0)
				{
					if (tableNumber >= 0 && tableNumber <= tableTotal)
					{
						ps = new PrintStream(infoSave.getSocket().getOutputStream());
						HostGame hg = new HostGame(tableNumber , MyProtocol.LEFT_TABLE , infoSave);
						infoSave.getGameHall().setVisible(false);
						infoSave.setHostGame(hg);
						ps.println(MyProtocol.TABLE_INFO + tableNumber + ":" + MyProtocol.LEFT_TABLE + ";" + 
								  infoSave.getUserName() + "." +  infoSave.getSex() + "," + 
								  infoSave.getHeadPanel().getIconNumber() + MyProtocol.TABLE_INFO);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(this, "桌子已经有人了！", "警告",JOptionPane.WARNING_MESSAGE);
				}
			}
			//-----点击右边的桌子------//
			if (rightTableArea.contains(oX, oY))
			{
				//------判断点击的桌子的右边是否已经有人------//
				int right = 0;
				for (int i = 0; i < IMAGE_SUM ; i++)
				{
					if (this.headImageRight[headRow][headColumn][i] == 1)
					{
						right++;
					}				
				}
				if (right == 0)
				{
					if (tableNumber >= 0 && tableNumber <= tableTotal)
					{
						ps = new PrintStream(infoSave.getSocket().getOutputStream());
						ps.println(MyProtocol.TABLE_INFO + tableNumber + ":" + MyProtocol.RIGHT_TABLE + ";" + 
								  infoSave.getUserName() + "." +  infoSave.getSex() + "," + 
								  infoSave.getHeadPanel().getIconNumber() + MyProtocol.TABLE_INFO);
						HostGame hg = new HostGame(tableNumber , MyProtocol.RIGHT_TABLE , infoSave);
						infoSave.getGameHall().setVisible(false);
						infoSave.setHostGame(hg);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(this, "桌子已经有人了！", "警告",JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}	
		
	}
	
	//-----根据台号计算出座位的行数和列数-----//
	public void countNumber(int number)
	{
		if (number <= rowTable)
		{
			this.headRow = number - 1;
			this.headColumn = 0;
		}
		else
		{
			if (number % rowTable == 0)
			{
				this.headRow = rowTable - 1;
				this.headColumn = number / rowTable - 1;
			}
			else
			{
				this.headRow = number % rowTable - 1;
				this.headColumn = number / rowTable;
			}		
		}
	}
	
	public void setFullTable(String tableNumber)
	{
		int tableN = Integer.parseInt(tableNumber);
		countNumber(tableN);
		this.tablePlay[headRow][headColumn] = 1;
		this.repaint();
	}
	
	public void cleanTable(String tableNumber)
	{
		int tableN = Integer.parseInt(tableNumber);
		countNumber(tableN);
		this.tablePlay[headRow][headColumn] = 0;
		this.repaint();
	}

	//----------当用户点击桌子进入游戏时在大厅显示用户的头像----------//
	public void setNumber(String tableNumber , String location ,String iconNumber)
	{
		this.icon = Integer.parseInt(iconNumber);
		this.number = Integer.parseInt(tableNumber);
		countNumber(number);

		if (location.equals(MyProtocol.LEFT_TABLE))
		{
			this.headImageLeft[headRow][headColumn][icon] = 1;
		}
		else if (location.equals(MyProtocol.RIGHT_TABLE))
		{
			this.headImageRight[headRow][headColumn][icon] = 1;
		}
	}
	//----------当用户退出游戏时清除大厅的头像----------//
	public void cleanHead(String tableNumber , String location)
	{
		int number = Integer.parseInt(tableNumber);
		countNumber(number);
		if (location.equals(MyProtocol.LEFT_TABLE))
		{
			for (int i = 0; i < IMAGE_SUM ; i++ )
			{
				this.headImageLeft[headRow][headColumn][i] = 0;
			}
		}
		else if (location.equals(MyProtocol.RIGHT_TABLE))
		{
			for (int i = 0; i < IMAGE_SUM ; i++ )
			{
				this.headImageRight[headRow][headColumn][i] = 0;
			}			
		}
	}
	
	public void paint(Graphics g)//
	{	
		int n = 0;
		String tableNumber = null;
		int width = 0;
		int height = 0;
		FontMetrics fm = null;
		for (int i=0 ; i<column ;i++ )
		{
			for (int j=0; j<rowTable ;j++ )
			{
				n++;
				g.drawImage(table , j * tableWide , i * tableHigh , null);
				fm = g.getFontMetrics();
				tableNumber = new String(n + "号台");
				width = fm.stringWidth(tableNumber);	//得到字符串的像素长度
				height = fm.getHeight();				//得到字符串的像素高度
				g.setColor(Color.WHITE); 
				g.setFont(new Font("" , Font.BOLD , 12));
				//画桌子号
				g.drawString(tableNumber, j * tableWide + tableWide / 2 - width / 2, 
							 (i + 1) * tableHigh - height / 2);
				//游戏中的桌子
				if (tablePlay[j][i] == 1)
				{
					g.drawImage(tableFull , j * tableWide + 43, 
								i * tableHigh + 41 , null);
				}
				for (int k = 0; k < IMAGE_SUM ; k++)
				{
					//左边桌子的头像
					if (headImageLeft[j][i][k] == 1)
					{
						g.drawImage(infoSave.getHeadPanel().getHeadIcon(k), j * tableWide + 10, 
								    i * tableHigh + 52 , null);
					}
					//右边桌子的头像
					if (headImageRight[j][i][k] == 1)
					{
						g.drawImage(infoSave.getHeadPanel().getHeadIcon(k), j * tableWide + 100, 
								    i * tableHigh + 52 , null);
					}
				}
			}
		}
	}

    public void setInfoSave(InfoSave infoSave) 
    {
        this.infoSave = infoSave;
    }
}
