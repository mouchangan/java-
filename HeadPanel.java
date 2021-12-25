
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
//选择头像界面
//面板
public class HeadPanel extends JPanel
{
	private static final int IMAGE_SUM = 30;
	private final int ROW = 15;
	private final int COLUMN = 2;
	private int iconNumber = 22;
	private BufferedImage headerFrame;

	private BufferedImage selectedHead;
	private BufferedImage[] headImage= new BufferedImage[IMAGE_SUM];
	private BufferedImage[] headIcon= new BufferedImage[IMAGE_SUM];
	private ImageIcon[] headIconImage = new ImageIcon[IMAGE_SUM];

	private int[][] selectImage = new int[ROW][COLUMN];

	private int posX;
	private int posY;

	public HeadPanel()
	{
		try
		{		
			headerFrame = ImageIO.read(new File("D:\\0\\fiveStone\\headerFrame.bmp"));//头像框
			selectedHead = ImageIO.read(new File("D:\\0\\fiveStone\\selectedHead.gif"));//选择框

			for (int i=0; i < IMAGE_SUM ; i++ )
			{
				headImage[i] = ImageIO.read(new File("D:\\0\\headerImage\\" + (i+1) + ".jpg"));//头像
				headIcon[i] = ImageIO.read(new File("D:\\0\\Icon\\" + (i+1) + ".jpg"));
				headIconImage[i] = new ImageIcon("D:\\0\\Icon\\" + (i+1) + ".jpg");
			}

			mouseEvent();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void mouseEvent()
	{
		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent me)
			{
				posX = (me.getX() - 3) / 27;
				posY = (me.getY() - 3) / 27;
				if (posX >= 0 && posY >= 0 && posX <= 14 && posY <= 1)
				{
					selectImage[posX][posY] = 1;
					rep();					
				}				
			}
		});

		this.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				
				posX = (e.getX() - 3) / 27;
				posY = (e.getY() - 3) / 27;

				if (posX >= 0 && posY >= 0 && posX <= (ROW - 1) && posY <= (COLUMN - 1))
				{
					iconNumber = posX + posY * ROW;
					LoginDialog.headLabe.setIcon(headIconImage[iconNumber]);		
				}
			}
		});
	}

	public void rep()
	{
		this.repaint();
	}

	public void paint(Graphics g)
	{
		g.drawImage(headerFrame , 0 , 0 , null);
		for (int i = 0 , n = 0; i < COLUMN ; i++)
		{
			for (int j = 0; j < ROW ; j++ , n++)
			{
				g.drawImage(headImage[n] , j*27 + 5 , i*27 + 5 , null);//头像显示

				if (selectImage[j][i] == 1)
				{
					g.drawImage(selectedHead , j*27 + 3 , i*27 + 3 , null);//选择框显示
					selectImage[posX][posY] = 0;
				}
			}
		}
	}

	public int getIconNumber()
	{
		return iconNumber;
	}

	public ImageIcon getImage(int number)
	{
		return headIconImage[number];
	}

	public BufferedImage getHeadIcon(int number)
	{
		return headIcon[number];
	}
}
