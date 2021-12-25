
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
//��Ϸ��������
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

	private Image board;	//����ͼƬ
	private Image white;	//����ͼƬ
	private Image black;	//����ͼƬ
	private Image selected;
	private int posX;
	private int posY;
	private static int X;
	private static int Y;
	private int preX;	  //ǰһ��������
	private int preY;	  //ǰһ��������
	private int preSX;	  //ǰһ�����ƶ�����
	private int preSY;	  //ǰһ�����ƶ�����
	private int otherX;   //�Է�������
	private int otherY;   //�Է�������
	private int step;	  //����
	private int count;    //count �Ǽ�������Ϊ5��ʱ���ʤ��
	private int cursor;	  //����־
	private int winCountL;	  //ʤ������
	private int winCountR;	  //ʤ������
	private boolean startOver = false;
	private boolean peaceOver = false;
	private boolean lostOver = false;
	private boolean regretOver = false;

	private final int CHESS_SIZE = 15;    //CHESS_SIZE �����̴�С

	private int[][] chess = new int[CHESS_SIZE][CHESS_SIZE];		//����ģ��ж���Ӯ
	private int[][] select = new int[CHESS_SIZE][CHESS_SIZE];
	private int[][]	curChess = new int[CHESS_SIZE][CHESS_SIZE];    //ʵ��ģ�����

	private boolean playChess = false;		//�ɷ�����
	private boolean isBegin = false;		//��Ϸ��ʼ����
	private boolean isReady = false;		//׼��������
	private boolean isOtherReady = false;	//�Է�׼��������
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

			//��Dimension��װ��һ�������ĸ߶ȺͿ��
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
	   ���Һ�ɨ��4��,�ж��Ƿ���5����������
	*/
	private void acrossScan(int i , int j)
	{
        //�����������ĵط��Ϳ�ʼɨ��
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((j+4) < chess[i].length)  //�ж�����ҷ��Ƿ��㹻��4������λ��
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {                             
				
					 if (chess[i][j+k] == chess[i][j]) 
					 {
					     count++;          //��������ĵط�����ͬ����������
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    /*
       ������ɨ��4��,�ж��Ƿ���5����������
	*/
	private void erectScan(int i , int j)
	{
        //�����������ĵط��Ϳ�ʼɨ��
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((i+4) < chess.length)  //�ж�����·��Ƿ��㹻��4������λ��
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {
                                 
				     if (chess[i+k][j] == chess[i][j]) 
					 {
					     count++;          //��������ĵط�����ͬ����������
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    /*
        �����Ϻ�ɨ��4��,�ж��Ƿ���5����������
	*/
	private void topRightScan(int i , int j)
	{
        //�����������ĵط��Ϳ�ʼɨ��
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((i-4) >= 0 && (j+4) < chess[i].length)  //�ж������б�Ϸ��Ƿ��㹻��4������λ��
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {
                                 
				     if (chess[i-k][j+k] == chess[i][j]) 
					 {
					     count++;          //��������ĵط�����ͬ����������
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    /*
        �����º�ɨ��4��,�ж��Ƿ���5����������
	*/
	private void belowRightScan(int i , int j)
	{
        //�����������ĵط��Ϳ�ʼɨ��
        if (chess[i][j] == 1 || chess[i][j] == 2)
		{

		     if ((i+4) < chess.length && (j+4) < chess[i].length)  //�ж������б�·��Ƿ��㹻��4������λ��
			 {    
			     count = 1;
				 for (int k=1;k<=4 ;k++ )
				 {
                                 
				     if (chess[i+k][j+k] == chess[i][j]) 
					 {
					     count++;          //��������ĵط�����ͬ����������
					 }
					 else count = 0;
				 }
			 }
		 }
	}

    //��һ����4��ɨ�跽���ж�ʤ��
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

					//�ж��Ǻ��廹�ǰ���Ӯ
				    if (chess[i][j] == 1)
					{	
						timerLeft.stop();
						timerRight.stop();
						JOptionPane.showMessageDialog(mp , "����ʤ������" , "ʤ��" ,
													  JOptionPane.INFORMATION_MESSAGE);
						winCountL++;
					}
					else if (chess[i][j] == 2)
					{
						timerLeft.stop();
						timerRight.stop();
						JOptionPane.showMessageDialog(mp , "����ʤ������" , "ʤ��" ,
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
		��ʼ����Ϸ
	*/
	public void reset()
	{
		//��ʼ������
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
		mp.repaint();    //����repaint()�����ػ�����
	}

	/*
		���������¼�
	*/
	public void playChess()
	{		
		mp.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					//��ʼ��ť���¼�
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
					//���尴ť���¼�
					else if (isBegin && !playChess && regretButton.contains(e.getX(),e.getY())){
						int result = JOptionPane.showConfirmDialog(mp, "�Ƿ������������壿", "����",
																	JOptionPane.YES_NO_OPTION );
						if (result == 0)
						{
							ps.println(MyProtocol.REGRET_STEP + tableNumber + ":" + way + MyProtocol.REGRET_STEP);
							timerLeft.stop();
							timerRight.stop();
						}
					}
					//��Ͱ�ť���¼�
					else if (isBegin && playChess && peaceButton.contains(e.getX() , e.getY()))
					{
						int result = JOptionPane.showConfirmDialog(mp, "�Ƿ��������ͣ�", "���", 
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
					//���䰴ť���¼�
					else if (isBegin && playChess && lostButton.contains(e.getX() , e.getY()))
					{
						int result = JOptionPane.showConfirmDialog(mp, "�Ƿ�Ҫ�����ˣ�", "����", 
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
					//�����¼�
					if (playChess)
					{
						X = (int)(e.getX()-97)/(535/15) ;    //��ȡ�����������
						Y = (int)(e.getY()-91)/(536/15) ;

						if (X >= 0 && Y >= 0 && X <= 14 && Y <= 14 && chess[X][Y] != 1 && chess[X][Y] != 2)
						{
							if (way.equals(MyProtocol.LEFT_TABLE))    //�������º���
							{
								curChess[X][Y] = 1;
								chess[X][Y] = 1;    //��������
								//��Ϊ�����ֱߵ��������������Ҫ�ж��Ƿ��һ��
								if (step != 0)
								{
									curChess[otherX][otherY] = 0;	 //�ѶԷ��µ���ı�־������
								}
								timerLeft.stop();
								timerRight.start();

								
							}
							else if (way.equals(MyProtocol.RIGHT_TABLE))    //�ұ�����°���
							{
								curChess[otherX][otherY] = 0;		//�ѶԷ��µ���ı�־������
								curChess[X][Y] = 2;
								chess[X][Y] = 2 ;	//��������
								timerRight.stop();
								timerLeft.start();
							}
							select[X][Y] = 0;
							mp.repaint();	//�ػ�����
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
			����ƶ�ʱ���¼�
		*/
		mp.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				//System.out.println("X: " + e.getX());
				//System.out.println("Y: " + e.getY());
				if (playChess)
				{
					posX = (int)(e.getX()-97)/(535/15);    //��ȡ�����������
					posY = (int)(e.getY()-91)/(536/15);
					if (posX >= 0 && posY >= 0 && posX <= 14 && posY <= 14 )    //��ֹԽ��
					{
						setCursor();
						select[preSX][preSY] = 0; 
						select[posX][posY] = 1;    //Ϊ1��ʱ��selected
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
					setCursor();//�ı���Ϊ����
					startOver = true;//���ײ�ͼ
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
	//��Ϸ�еļ�ʱ��ÿ����10��������ʱ��
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
									JOptionPane.showMessageDialog(mp , "ʱ�䵽��������" , "����" ,
																JOptionPane.INFORMATION_MESSAGE);
								}
								else
								{
									JOptionPane.showMessageDialog(mp , "��ϲ�����Է�ʱ�䵽����Ӯ��" , "ʤ��" ,
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
									JOptionPane.showMessageDialog(mp , "��ϲ�����Է�ʱ�䵽����Ӯ��" , "ʤ��" ,
																JOptionPane.INFORMATION_MESSAGE);
								}
								else
								{
									JOptionPane.showMessageDialog(mp , "ʱ�䵽��������" , "����" ,
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

	//�ı���Ϊ����
	public void setCursor()
	{
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	//�ָ����
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
	{////////graphics������/��ͼ
		public String setName(Graphics g, String name, int width)
		{
			FontMetrics fm = g.getFontMetrics();
			int w  = fm.stringWidth(name); //�õ��ַ��������س���
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
		{	/////////Graphics��ͼ
			g.drawImage(gameImage , 0 , 0 ,null);
			g.setFont(new Font("" , Font.BOLD , 13));//�޸������С����ʽFont������������Ա����
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
			int width  = fm.stringWidth(strName); //�õ��ַ��������س���
			int height = fm.getHeight();		  //�õ��ַ��������ظ߶�
			if (way.equals(MyProtocol.LEFT_TABLE))//�������
			{
				if (infoSave.getSex().equals("��"))
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
				if (infoSave.getSex().equals("��"))
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
			if (size.equals("2"))//���Լ���ҳ����ʾ�Է���С��
			{
				String strN = setName(g, name , (int)leftName.getWidth());
				int w  = fm.stringWidth(strN); 
				int h = fm.getHeight();		  
				if (otherWay.equals(MyProtocol.LEFT_TABLE))
				{
					if (sex.equals("��"))
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
					if (sex.equals("��"))
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

			g.drawString("��ʱ�䣺" , 15 , 400);
			g.drawString("��ʱ�䣺" , 645 , 400);
			g.drawString("10 : 00" , 25 , 415);
			g.drawString("10 : 00" , 655 , 415);
			g.drawString("����ʱ��" , 15 , 430);		
			g.drawString("����ʱ��" , 645 , 430);
			g.drawString("" + dMin + min + " : " + dSec + sec , 25 , 445);		//��൹��ʱ��ʾ
			g.drawString("" + dMinR + minR + " : " + dSecR + secR , 655 , 445);	//�Ҳ൹��ʱ��ʾ
			g.drawString("�ȷ֣�" + winCountL , 15 , 465);
			g.drawString("�ȷ֣�" + winCountR , 645 , 465);
			
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
					if (select[i][j] == 1)				//����ʱѡ���ʮ�֣�����ѡ��
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

	//�Լ����̻����Ĳ���
	public void clearChess()
	{
		chess[X][Y] = 0;
		curChess[X][Y] = 0;
		mp.repaint();
	}
	//�Է�ͬ������Ĳ���
	public void sureClearChess()
	{
		chess[ClientThread.RX][ClientThread.RY] = 0;
		curChess[ClientThread.RX][ClientThread.RY] = 0;
		mp.repaint();
	}
	//���ݶԷ��ķ�λ���������Լ���߻����Է��µ���
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
	//�Է����׼������
	public void setReady()
	{
		isOtherReady = true;
		if (isReady)
		{
			isBegin = true;
		}
		mp.repaint();
	}
	//�Է�����˳���Ϸ��
	public void noReady()
	{
		isOtherReady = false;
		if (isBegin)
		{
			timerLeft.stop();
			timerRight.stop();
			JOptionPane.showMessageDialog(mp , "����ǿ���˳��ˣ���" , "����" ,JOptionPane.INFORMATION_MESSAGE);
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

	//����
	public void showRegret(){
		timerLeft.stop();
		timerRight.stop();
		int result = JOptionPane.showConfirmDialog(mp, "����������壬�Ƿ�ͬ�⣿", "����",
				JOptionPane.YES_NO_OPTION );
		if (result == 0)
		{

			sureClearChess();//�Է����ҷ��Ļ��������ʾ
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
		JOptionPane.showMessageDialog(mp , "�Է�ͬ������Ļ���Ҫ��" , "ͬ�����" ,
				JOptionPane.INFORMATION_MESSAGE);
		clearChess();//�ҷ����̻���Ĳ���
		playChess=true;
	}
	public void showRejectRegretStep(){
		setPlayChess();
		JOptionPane.showMessageDialog(mp , "�Է��ܾ�����Ļ���Ҫ��" , "��ͬ�����" ,
				JOptionPane.INFORMATION_MESSAGE);
	}

	//����
	public void showPeace()
	{
		timerLeft.stop();
		timerRight.stop();
		int result = JOptionPane.showConfirmDialog(mp, "����������ͣ��Ƿ�ͬ�⣿", "���", 
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
		JOptionPane.showMessageDialog(mp , "�Է��ܾ���������Ҫ�󣡣�" , "��ͬ�����" ,
										JOptionPane.INFORMATION_MESSAGE);

	}

	public void showAgreePeace()
	{
		JOptionPane.showMessageDialog(mp , "�Է�ͬ������ĺ���Ҫ�󣬱��ִ�ƽ����" , "ͬ�����" ,
										JOptionPane.INFORMATION_MESSAGE);
		reset();
	}
	
	public void showLost(String way)
	{
		if (way.equals(MyProtocol.LEFT_TABLE))
		{
			timerLeft.stop();
			JOptionPane.showMessageDialog(mp , "���������ˣ���ϲ���ʤ����" , "ʤ��" ,
											JOptionPane.INFORMATION_MESSAGE);
			winCountR++;
		}
		else
		{
			timerRight.stop();
			JOptionPane.showMessageDialog(mp , "���������ˣ���ϲ���ʤ����" , "ʤ��" ,
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
