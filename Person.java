
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/*
	画出个人信息栏目（除头像昵称外的内容不可变）
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
		if (infoSave.getSex().equals("男"))
		{
			g.drawImage(mQshow , 30 , 10 , null);
		}
		else
		{
			g.drawImage(fQshow , 30 , 10 , null);
		}
		g.drawString("头像：" , 130 , 30);
		g.drawImage(infoSave.getHeadIcon() , 170 , 10 , null);	
		g.drawString("昵称： " + infoSave.getUserName() , 130 , 60);	
		g.drawString("性别：" + infoSave.getSex(), 130 , 85);
		g.drawString("等级： 1级" , 130 , 110);
		g.drawString("称号： 菜鸟" , 130 , 135);
	}
}
