
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
//游戏规则、主体
//
public class MainGame extends JPanel
{
	private InfoSave infoSave;
	private MyPanel mp;
	private int tableNumber;
	private String name;
	private String sex;
	private String way;
	private String otherWay;
	private String size = "1";
	private BufferedImage whiteDisk;
	private BufferedImage blackDisk;
	private BufferedImage curBlack;
	private BufferedImage curWhite;
	private BufferedImage seatEmpty;
	private BufferedImage maleSeatFull;
	private BufferedImage femaleSeatFull;
	private BufferedImage gameImage;
	private BufferedImage ready;
	private BufferedImage start;
	private BufferedImage play;
	private BufferedImage peace;
	private BufferedImage lost;
	private BufferedImage regret;

	private PrintStream ps;
	private Rectangle leftName  = new Rectangle(13,  348, 66, 125);
	private Rectangle rightName = new Rectangle(645, 348, 66, 125);

	private Image board;	//棋盘图片
	private Image white;	//白棋图片
	private Image black;	//黑棋图片
	private Image selected;
	private int posX;
	private int posY;
	private static int X;
	private static int Y;
	private int preX;	  //前一步的坐标
	private int preY;	  //前一步的坐标
	private int preSX;	  //前一步的移动坐标
	private int preSY;	  //前一步的移动坐标
	private int otherX;   //对方的坐标
	private int otherY;   //对方的坐标
	private int step;	  //步数
	private int count;    //count 是计数器，为5的时候表胜利
	private int cursor;	  //光标标志
	private int winCountL;	  //胜利次数
	private int winCountR;	  //胜利次数
	private boolean startOver = false;
	private boolean peaceOver = false;
	private boolean lostOver = false;
	private boolean regretOver = false;

	private final int CHESS_SIZE = 15;    //CHESS_SIZE 是棋盘大小

	private int[][] chess = new int[CHESS_SIZE][CHESS_SIZE];		//虚拟的，判断输赢
	private int[][] select = new int[CHESS_SIZE][CHESS_SIZE];
	private int[][]	curChess = new int[CHESS_SIZE][CHESS_SIZE];    //实体的，坐标

	private boolean playChess = false;		//可否下棋
	private boolean isBegin = false;		//游戏开始了吗
	private boolean isReady = false;		//准备好了吗
	private boolean isOtherReady = false;	//对方准备好了吗
	private Rectangle startButton = new Rectangle(334, 656 , 60 , 31);
	private Rectangle peaceButton = new Rectangle(269, 653 , 42 , 35);
	private Rectangle lostButton = new Rectangle(413, 659 , 45 , 18);
	private Rectangle regretButton = new Rectangle(100,620,60,40);

	private int sec = 0;
	private int dSec = 0;
	private int min = 0;
	private int dMin = 1;
	private int secR = 0;
	private int dSecR = 0;
	private int minR = 0;
	private int dMinR = 1;
	private Timer timerLeft;
	private Timer timerRight;

	public MainGame(InfoSave infoSave , String way , int tableNumber)
	{
		try
		{
			this.tableNumber = tableNumber;
			this.infoSave = infoSave;
			this.way = way;
			mp = new MyPanel();
			ps = new PrintStream(infoSave.getSocket().getOutputStream());
			gameImage   = ImageIO.read(new File("D:\\0\\fiveStone\\room2.bmp"));
			ready = ImageIO.read(new File("D:\\0\\fiveStone\\ready.jpg"));
			start = ImageIO.read(new File("D:\\0\\fiveStone\\start.jpg"));
			play = ImageIO.read(new File("D:\\0\\fiveStone\\play.jpg"));
			peace = ImageIO.read(new File("D:\\0\\fiveStone\\peace.jpg"));
			regret = ImageIO.read(new File("D:\\0\\fiveStone\\regret.jpg"));
			lost = ImageIO.read(new File("D:\\0\\fiveStone\\lost.jpg"));
			whiteDisk = ImageIO.read(new File("D:\\0\\fiveStone\\whiteDisk.gif"));
			blackDisk = ImageIO.read(new File("D:\\0\\fiveStone\\blackDisk.gif"));
			curBlack = ImageIO.read(new File("D:\\0\\fiveStone\\curBlack.gif"));
			curWhite = ImageIO.read(new File("D:\\0\\fiveStone\\curWhite.gif"));
			maleSeatFull = ImageIO.read(new File("D:\\0\\fiveStone\\maleSeatFull.jpg"));
			femaleSeatFull = ImageIO.read(new File("D:\\0\\fiveStone\\femaleSeatFull.jpg"));
			seatEmpty = ImageIO.read(new File("D:\\0\\fiveStone\\seatEmpty.jpg"));

			white = ImageIO.read(new File("D:\\0\\fiveStone\\white.gif"));
			black = ImageIO.read(new File("D:\\0\\fiveStone\\black.gif"));
			selected = ImageIO.read(new File("D:\\0\\fiveStone\\selected.gif"));

			//用Dimension封装了一个构件的高度和宽度
			this.setMinimumSize(new Dimension(723 , 702));
			this.setPreferredSize(new Dimension(gameImage.getWidth(null), gameImage.getHeight(null))); 
			mp.setPreferredSize(new Dimension(gameImage.getWidth(null), gameImage.getHeight(null)));
			this.add(mp);
			timer();
			playChess();		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*    
	   向右横扫描4格,判断是否有5个相连的棋
	*/
	private void acrossScan(int i , int j)
	{
        //如果遇到有棋的地方就开始扫描
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((j+4) < chess[i].length)  //判断棋的右方是否足够有4个连续位置
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {                             
				
					 if (chess[i][j+k] == chess[i][j]) 
					 {
					     count++;          //如果相连的地方有相同的棋就做标记
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    /*
       向下竖扫描4格,判断是否有5个相连的棋
	*/
	private void erectScan(int i , int j)
	{
        //如果遇到有棋的地方就开始扫描
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((i+4) < chess.length)  //判断棋的下方是否足够有4个连续位置
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {
                                 
				     if (chess[i+k][j] == chess[i][j]) 
					 {
					     count++;          //如果相连的地方有相同的棋就做标记
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    /*
        向右上横扫描4格,判断是否有5个相连的棋
	*/
	private void topRightScan(int i , int j)
	{
        //如果遇到有棋的地方就开始扫描
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((i-4) >= 0 && (j+4) < chess[i].length)  //判断棋的右斜上方是否足够有4个连续位置
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {
                                 
				     if (chess[i-k][j+k] == chess[i][j]) 
					 {
					     count++;          //如果相连的地方有相同的棋就做标记
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    /*
        向右下横扫描4格,判断是否有5个相连的棋
	*/
	private void belowRightScan(int i , int j)
	{
        //如果遇到有棋的地方就开始扫描
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((i+4) < chess.length && (j+4) < chess[i].length)  //判断棋的右斜下方是否足够有4个连续位置
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {
                                 
				     if (chess[i+k][j+k] == chess[i][j]) 
					 {
					     count++;          //如果相连的地方有相同的棋就做标记
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    //逐一调用4种扫描方法判断胜负
	public void win()
	{

		for (int i = 0; i < chess.length ; i++ )
		{
			for (int j = 0 ; j < chess[i].length ; j++ )
			{
                acrossScan(i,j);
				if (count!=5)
				{
                    erectScan(i,j);
				}
				if (count!=5)
				{
				    topRightScan(i,j);
				}
                if (count!=5)
				{
				    belowRightScan(i,j);
				}
				if (count==5)
				{
					count=0;

					//判断是黑棋还是白棋赢
				    if (chess[i][j] == 1)
					{	
						timerLeft.stop();
						timerRight.stop();
						JOptionPane.showMessageDialog(mp , "黑棋胜利！！" , "胜利" ,
													  JOptionPane.INFORMATION_MESSAGE);
						winCountL++;
					}
					else if (chess[i][j] == 2)
					{
						timerLeft.stop();
						timerRight.stop();
						JOptionPane.showMessageDialog(mp , "白棋胜利！！" , "胜利" ,
													  JOptionPane.INFORMATION_MESSAGE);
						winCountR++;
					}
					try
					{
						PrintStream ps = new PrintStream(infoSave.getSocket().getOutputStream());
						ps.println(MyProtocol.WIN + this.tableNumber + MyProtocol.WIN);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					reset();	
				}
			}			
		}
	}

	/*
		初始化游戏
	*/
	public void reset()
	{
		//初始化棋盘
		for (int i=0; i<chess.length; i++ )
		{
			for (int j=0; j<chess[i].length; j++ )
			{
				chess[i][j] = 0;
				curChess[i][j] = 0;
			}
		}
		step = 0;
		cursor = 0;
		sec = 0;
		dSec = 0;
		min = 0;
		dMin = 1;
		secR = 0;
		dSecR = 0;
		minR = 0;
		dMinR = 1;
		timerLeft.stop();
		timerRight.stop();
		select[preSX][preSY] = 0;
		playChess = false;
		isBegin = false;
		isReady = false;
		isOtherReady = false;
		mp.repaint();    //调用repaint()方法重画棋盘
	}

	/*
		处理下棋事件
	*/
	public void playChess()
	{		
		mp.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					//开始按钮的事件
					if (!isBegin && !isReady && startButton.contains(e.getX() , e.getY()))
					{
						ps.println(MyProtocol.GAME_READY + tableNumber + ":" + way + ";" + 
									infoSave.getUserName() + MyProtocol.GAME_READY);
						isReady = true;
						if (isOtherReady)
						{
							isBegin = true;
						}
					}
					//悔棋按钮的事件
					else if (isBegin && !playChess && regretButton.contains(e.getX(),e.getY())){
						int result = JOptionPane.showConfirmDialog(mp, "是否向对手请求悔棋？", "悔棋",
																	JOptionPane.YES_NO_OPTION );
						if (result == 0)
						{
							ps.println(MyProtocol.REGRET_STEP + tableNumber + ":" + way + MyProtocol.REGRET_STEP);
							timerLeft.stop();
							timerRight.stop();
						}
					}
					//求和按钮的事件
					else if (isBegin && playChess && peaceButton.contains(e.getX() , e.getY()))
					{
						int result = JOptionPane.showConfirmDialog(mp, "是否向对手求和？", "求和", 
																	JOptionPane.YES_NO_OPTION );
						if (result == 0)
						{	
							playChess = false;
							select[preSX][preSY] = 0;
							ps.println(MyProtocol.PEACE + tableNumber + ":" + way + MyProtocol.PEACE);
							timerLeft.stop();
							timerRight.stop();
						}			
					}
					//认输按钮的事件
					else if (isBegin && playChess && lostButton.contains(e.getX() , e.getY()))
					{
						int result = JOptionPane.showConfirmDialog(mp, "是否要认输了？", "认输", 
																	JOptionPane.YES_NO_OPTION );
						if (result == 0)
						{
							if (way.equals(MyProtocol.LEFT_TABLE))
							{
								winCountR++;
							}
							else
							{
								winCountL++;
							}
							reset();
							ps.println(MyProtocol.LOST + tableNumber + ":" + way + MyProtocol.LOST);
						}		
					}
					//下棋事件
					if (playChess)
					{
						X = (int)(e.getX()-97)/(535/15) ;    //截取鼠标点击的坐标
						Y = (int)(e.getY()-91)/(536/15) ;

						if (X >= 0 && Y >= 0 && X <= 14 && Y <= 14 && chess[X][Y] != 1 && chess[X][Y] != 2)
						{
							if (way.equals(MyProtocol.LEFT_TABLE))    //左边玩家下黑棋
							{
								curChess[X][Y] = 1;
								chess[X][Y] = 1;    //黑棋下棋
								//因为是左手边的玩家先下棋所以要判断是否第一步
								if (step != 0)
								{
									curChess[otherX][otherY] = 0;	 //把对方下的棋的标志消除掉
								}
								timerLeft.stop();
								timerRight.start();

								
							}
							else if (way.equals(MyProtocol.RIGHT_TABLE))    //右边玩家下白棋
							{
								curChess[otherX][otherY] = 0;		//把对方下的棋的标志消除掉
								curChess[X][Y] = 2;
								chess[X][Y] = 2 ;	//白棋下棋
								timerRight.stop();
								timerLeft.start();
							}
							select[X][Y] = 0;
							mp.repaint();	//重画棋盘
							playChess = false;
							ps.println(MyProtocol.X_Y + tableNumber + ":" + way + ";" + X + "," + Y +
										MyProtocol.X_Y);
						}
						preX = X;
						preY = Y;
						step++;
						win();
					}
				}
				catch (Exception ee)
				{
					ee.printStackTrace();
				}
			}
		});
		/*
			鼠标移动时的事件
		*/
		mp.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				//System.out.println("X: " + e.getX());
				//System.out.println("Y: " + e.getY());
				if (playChess)
				{
					posX = (int)(e.getX()-97)/(535/15);    //截取鼠标点击的坐标
					posY = (int)(e.getY()-91)/(536/15);
					if (posX >= 0 && posY >= 0 && posX <= 14 && posY <= 14 )    //防止越界
					{
						setCursor();
						select[preSX][preSY] = 0; 
						select[posX][posY] = 1;    //为1的时候画selected
						preSX = posX;
						preSY = posY;
						mp.repaint();
					}
					else if (cursor == 0)
					{
						select[preSX][preSY] = 0;
						setDefaultCursor();
					}
				}
				if (!isBegin && !isReady && startButton.contains(e.getX() , e.getY()))
				{
					setCursor();//改变光标为手掌
					startOver = true;//换底部图
					cursor = 1;
					mp.repaint();
				}
				else if (isBegin  && !playChess && regretButton.contains(e.getX() , e.getY()))
				{
					setCursor();
					regretOver = true;
					cursor = 1;
					mp.repaint();
				}
				else if (isBegin  && playChess && peaceButton.contains(e.getX() , e.getY()))
				{
					setCursor();
					peaceOver = true;
					cursor = 1;
					mp.repaint();
				}
				else if (isBegin  && playChess && lostButton.contains(e.getX() , e.getY()))
				{
					setCursor();
					lostOver = true;
					cursor = 1;
					mp.repaint();
				}
				else
				{
					startOver = false;
					peaceOver = false;
					lostOver = false;
					regretOver=false;
					cursor = 0;
					if (!playChess)
					{
						setDefaultCursor();
					}
					mp.repaint();
				}
			}
		});
	}
	//游戏中的计时，每人有10分钟下棋时间
	public void timer()
	{
		timerLeft = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{		
				if (sec == 0)
				{
					sec = 9;
					if (dSec == 0)
					{
						dSec = 5;
						if (min == 0)
						{
							if (dMin != 0)
							{
								min = 9;
								dMin--;
							}
							else
							{
								if (way.equals(MyProtocol.LEFT_TABLE))
								{
									JOptionPane.showMessageDialog(mp , "时间到，你输了" , "输了" ,
																JOptionPane.INFORMATION_MESSAGE);
								}
								else
								{
									JOptionPane.showMessageDialog(mp , "恭喜！！对方时间到，你赢了" , "胜利" ,
																JOptionPane.INFORMATION_MESSAGE);
								}
								winCountR++;
								reset();
							}
						}
						else
						{
							min--;
						}
					}
					else
					{
						dSec--;
					}
				}	
				else
				{
					sec--;
				}
				mp.repaint();			
			}
		});

		timerRight = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{		
				if (secR == 0)
				{
					secR = 9;
					if (dSecR == 0)
					{
						dSecR = 5;
						if (minR == 0)
						{
							if (dMinR != 0)
							{
								minR = 9;
								dMinR--;
							}
							else
							{
								if (way.equals(MyProtocol.LEFT_TABLE))
								{
									JOptionPane.showMessageDialog(mp , "恭喜！！对方时间到，你赢了" , "胜利" ,
																JOptionPane.INFORMATION_MESSAGE);
								}
								else
								{
									JOptionPane.showMessageDialog(mp , "时间到，你输了" , "输了" ,
																JOptionPane.INFORMATION_MESSAGE);
								}
								winCountL++;
								reset();
							}
						}
						else
						{
							minR--;
						}
					}
					else
					{
						dSecR--;
					}
				}	
				else
				{
					secR--;
				}
				mp.repaint();			
			}
		});
	}

	//改变光标为手掌
	public void setCursor()
	{
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	//恢复光标
	public void setDefaultCursor()
	{
		this.setCursor(Cursor.getDefaultCursor());
	}
	
	public void setSeat(String way , String name , String sex , String size)
	{
		this.otherWay = way;
		this.name = name;
		this.sex = sex;
		this.size = size;
		mp.repaint();
	}

	class MyPanel extends JPanel
	{////////graphics艺术字/画图
		public String setName(Graphics g, String name, int width)
		{
			FontMetrics fm = g.getFontMetrics();
			int w  = fm.stringWidth(name); //得到字符串的像素长度
			if (w <= width)
			{
				return name;
			}
			else if (w > width)
			{
				for (int i = name.length() - 1; i >= 0; i--)
				{
					String str = name.substring(0, i) + "...";
					if (fm.stringWidth(str) < width)
					{
						return str;
					}
				}
			}
			return name;
		}
		public void paint(Graphics g)
		{	/////////Graphics画图
			g.drawImage(gameImage , 0 , 0 ,null);
			g.setFont(new Font("" , Font.BOLD , 13));//修改字体大小及样式Font对象必须是类成员变量
			g.setColor(Color.WHITE); 
			
			if (isOtherReady)
			{
				if (way.equals(MyProtocol.LEFT_TABLE))
				{
					g.drawImage(whiteDisk , 655 , 173 , null);
				}
				else
				{
					g.drawImage(blackDisk , 24 , 173 , null);
				}
			}
			if (isReady)
			{
				if (way.equals(MyProtocol.LEFT_TABLE))
				{
					g.drawImage(blackDisk , 24 , 173 , null);
				}
				else
				{
					g.drawImage(whiteDisk , 655 , 173 , null);
				}
			}
			if (isReady && !playChess)
			{
				g.drawImage(ready , 93 , 631 , null);
			}
			if (isBegin && playChess)
			{
				g.drawImage(play , 93 , 630 , null);
			}
			
			String strName = setName(g, infoSave.getUserName(), (int)leftName.getWidth());
			FontMetrics fm = g.getFontMetrics();
			int width  = fm.stringWidth(strName); //得到字符串的像素长度
			int height = fm.getHeight();		  //得到字符串的像素高度
			if (way.equals(MyProtocol.LEFT_TABLE))//坐在左边
			{
				if (infoSave.getSex().equals("男"))
				{
					g.drawImage(maleSeatFull , 7 , 219 , null);
				}
				else
				{	
					g.drawImage(femaleSeatFull , 7 , 219 , null);
				}			
				g.drawString(strName , (int)leftName.getX() + (int)leftName.getWidth() / 2 - width / 2 ,
							 (int)leftName.getY() + height);
			}
			else
			{
				if (infoSave.getSex().equals("男"))
				{
					g.drawImage(maleSeatFull , 638 , 219 , null);
				}
				else
				{	
					g.drawImage(femaleSeatFull , 638 , 219 , null);
				}			
				g.drawString(strName , (int)rightName.getX() + (int)rightName.getWidth() / 2 - width / 2 ,
							 (int)rightName.getY() + height);
			}
			if (size.equals("2"))//在自己的页面显示对方的小人
			{
				String strN = setName(g, name , (int)leftName.getWidth());
				int w  = fm.stringWidth(strN); 
				int h = fm.getHeight();		  
				if (otherWay.equals(MyProtocol.LEFT_TABLE))
				{
					if (sex.equals("男"))
					{
						g.drawImage(maleSeatFull , 7 , 219 , null);
					}
					else
					{
						g.drawImage(femaleSeatFull , 7 , 219 , null);
					}
					g.drawString(strN , (int)leftName.getX() + (int)leftName.getWidth() / 2 - w / 2 ,
							 (int)leftName.getY() + h);
				}
				else
				{
					if (sex.equals("男"))
					{
						g.drawImage(maleSeatFull , 638 , 219 , null);
					}
					else
					{
						g.drawImage(femaleSeatFull , 638 , 219 , null);
					}
					g.drawString(strN , (int)rightName.getX() + (int)rightName.getWidth() / 2 - w / 2 ,
							 (int)rightName.getY() + h);
				}
			}
			if (startOver)
			{
				g.drawImage(start , 94 , 629 , null);
			}
			if(regretOver)
			{
				g.drawImage(regret,93 , 631 , null);
			}
			if (peaceOver)
			{
				g.drawImage(peace , 94 , 630 , null);
			}
			if (lostOver)
			{
				g.drawImage(lost , 94 , 630 , null);
			}

			g.drawString("总时间：" , 15 , 400);
			g.drawString("总时间：" , 645 , 400);
			g.drawString("10 : 00" , 25 , 415);
			g.drawString("10 : 00" , 655 , 415);
			g.drawString("倒计时：" , 15 , 430);		
			g.drawString("倒计时：" , 645 , 430);
			g.drawString("" + dMin + min + " : " + dSec + sec , 25 , 445);		//左侧倒计时显示
			g.drawString("" + dMinR + minR + " : " + dSecR + secR , 655 , 445);	//右侧倒计时显示
			g.drawString("比分：" + winCountL , 15 , 465);
			g.drawString("比分：" + winCountR , 645 , 465);
			
			for (int i = 0; i < 15 ; i++ )
			{
				for (int j = 0; j < 15 ; j++ )
				{
					if (chess[i][j] == 1)
					{
						g.drawImage(black , i * 35 + 97 , j * 35 + 91 , null);
					}
					if (chess[i][j] == 2)
					{
						g.drawImage(white , i * 35 + 97 , j * 35 + 91 , null);
					}
					if (curChess[i][j] == 1)
					{
						g.drawImage(curBlack , i * 35 + 97 , j * 35 + 91 , null);
					}
					if (curChess[i][j] == 2)
					{
						g.drawImage(curWhite , i * 35 + 97 , j * 35 + 91 , null);
					}
					if (select[i][j] == 1)				//下棋时选择的十字，便于选择
					{
						g.drawImage(selected, i*35 + 97, j*35 + 91 , null);

					}
				}
			}
		}
	}

	public void setSize(String size)
	{
		this.size = size;
		this.repaint();
	}
	public String getPlayerSize()
	{
		return this.size;
	}

	//自己棋盘悔棋后的操作
	public void clearChess()
	{
		chess[X][Y] = 0;
		curChess[X][Y] = 0;
		mp.repaint();
	}
	//对方同意悔棋后的操作
	public void sureClearChess()
	{
		chess[ClientThread.RX][ClientThread.RY] = 0;
		curChess[ClientThread.RX][ClientThread.RY] = 0;
		mp.repaint();
	}
	//根据对方的方位和坐标在自己这边画出对方下的棋
	public void setXY(String way , int X , int Y)
	{
		if (way.equals(MyProtocol.LEFT_TABLE))
		{
			chess[X][Y] = 1;
			curChess[X][Y] = 1;
		}
		else
		{
			chess[X][Y] = 2;
			curChess[X][Y] = 2;
		}
		this.otherX = X;
		this.otherY = Y;
		if (step != 0)
		{
			curChess[preX][preY] = 0;
		}
		mp.repaint();
		win();
		if (isBegin)
		{
			setPlayChess();
		}	
	}
	//对方玩家准备好了
	public void setReady()
	{
		isOtherReady = true;
		if (isReady)
		{
			isBegin = true;
		}
		mp.repaint();
	}
	//对方玩家退出游戏了
	public void noReady()
	{
		isOtherReady = false;
		if (isBegin)
		{
			timerLeft.stop();
			timerRight.stop();
			JOptionPane.showMessageDialog(mp , "对手强行退出了！！" , "警告" ,JOptionPane.INFORMATION_MESSAGE);
			reset();
		}
		winCountL = 0;
		winCountR = 0;
		mp.repaint();
	}

	public void setPlayChess()
	{
		playChess = true;
		if (way.equals(MyProtocol.LEFT_TABLE))
		{
			timerLeft.start();
			timerRight.stop();
		}
		else
		{
			timerRight.start();
			timerLeft.stop();
		}
	}

	public void setBegin()
	{
		isBegin = true;
		mp.repaint();
	}

	public boolean isBegin()
	{
		return isBegin;
	}

	//悔棋
	public void showRegret(){
		timerLeft.stop();
		timerRight.stop();
		int result = JOptionPane.showConfirmDialog(mp, "对手请求悔棋，是否同意？", "悔棋",
				JOptionPane.YES_NO_OPTION );
		if (result == 0)
		{

			sureClearChess();//对方对我方的悔棋操作显示
			playChess = false;
			ps.println(MyProtocol.REGRET_REJECT + tableNumber + ":" + way + MyProtocol.REGRET_REJECT);
		}
		else
		{
			ps.println(MyProtocol.REGRET_AGREE + tableNumber + ":" + way + MyProtocol.REGRET_AGREE);
			if (way.equals(MyProtocol.LEFT_TABLE)) {
				timerRight.start();
			} else {
				timerLeft.start();
			}
		}
	}
	public void showAgreeRegretStep(){
		JOptionPane.showMessageDialog(mp , "对方同意了你的悔棋要求" , "同意悔棋" ,
				JOptionPane.INFORMATION_MESSAGE);
		clearChess();//我方棋盘悔棋的操作
		playChess=true;
	}
	public void showRejectRegretStep(){
		setPlayChess();
		JOptionPane.showMessageDialog(mp , "对方拒绝了你的悔棋要求" , "不同意悔棋" ,
				JOptionPane.INFORMATION_MESSAGE);
	}

	//和棋
	public void showPeace()
	{
		timerLeft.stop();
		timerRight.stop();
		int result = JOptionPane.showConfirmDialog(mp, "对手向你求和，是否同意？", "求和", 
													JOptionPane.YES_NO_OPTION );
		if (result == 0)
		{		
			reset();
			ps.println(MyProtocol.PEACE_AGREE + tableNumber + ":" + way + MyProtocol.PEACE_AGREE);
		}
		else
		{
			ps.println(MyProtocol.PEACE_REJECT + tableNumber + ":" + way + MyProtocol.PEACE_REJECT);
			if (way.equals(MyProtocol.LEFT_TABLE))
			{
				timerRight.start();
			}
			else
			{
				timerLeft.start();
			}
		}
	}

	public void showRejectPeace()
	{
		setPlayChess();
		JOptionPane.showMessageDialog(mp , "对方拒绝了你的求和要求！！" , "不同意和棋" ,
										JOptionPane.INFORMATION_MESSAGE);

	}

	public void showAgreePeace()
	{
		JOptionPane.showMessageDialog(mp , "对方同意了你的和棋要求，本局打平！！" , "同意和棋" ,
										JOptionPane.INFORMATION_MESSAGE);
		reset();
	}
	
	public void showLost(String way)
	{
		if (way.equals(MyProtocol.LEFT_TABLE))
		{
			timerLeft.stop();
			JOptionPane.showMessageDialog(mp , "黑棋认输了，恭喜你获胜！！" , "胜利" ,
											JOptionPane.INFORMATION_MESSAGE);
			winCountR++;
		}
		else
		{
			timerRight.stop();
			JOptionPane.showMessageDialog(mp , "白棋认输了，恭喜你获胜！！" , "胜利" ,
											JOptionPane.INFORMATION_MESSAGE);
			winCountL++;
		}
		reset();
	}

	public String getWay()
	{
		return way;
	}

	public void setTimerLeft()
	{
		timerLeft.start();
	}
}
