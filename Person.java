
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/*
	����������Ϣ��Ŀ����ͷ���ǳ�������ݲ��ɱ䣩
*/
public class Person extends JPanel
{
	private InfoSave infoSave;
	BufferedImage mQshow;
	BufferedImage fQshow;

	public Person(InfoSave infoSave)
	{
		this.infoSave = infoSave;
		try
		{
			mQshow = ImageIO.read(new File("D:\\0\\fiveStone\\Mqshow.gif"));
			fQshow = ImageIO.read(new File("D:\\0\\fiveStone\\Fqshow.gif"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void paint(Graphics g)
	{
		g.setColor(new Color(113 , 46 , 152)); 
		g.setFont(new Font("" , Font.BOLD , 12));
		if (infoSave.getSex().equals("��"))
		{
			g.drawImage(mQshow , 30 , 10 , null);
		}
		else
		{
			g.drawImage(fQshow , 30 , 10 , null);
		}
		g.drawString("ͷ��" , 130 , 30);
		g.drawImage(infoSave.getHeadIcon() , 170 , 10 , null);	
		g.drawString("�ǳƣ� " + infoSave.getUserName() , 130 , 60);	
		g.drawString("�Ա�" + infoSave.getSex(), 130 , 85);
		g.drawString("�ȼ��� 1��" , 130 , 110);
		g.drawString("�ƺţ� ����" , 130 , 135);
	}
}
