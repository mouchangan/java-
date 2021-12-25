
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
//将右侧聊天窗、信息窗与游戏大厅合并
//设置大小

public class GameHall extends JFrame
{
	private InfoSave infoSave;
	ChatSystem chatSystem = new ChatSystem();
	HallPanel hallPanel = new HallPanel(25 , 5);
	private JSplitPane split;	//分割面板
	private JScrollPane scroll;	//滚动面板

	public void init(InfoSave infoSave)
	{
		this.infoSave = infoSave;
		chatSystem.setInfoSave(infoSave);
		chatSystem.	init();		//右侧信息区
		hallPanel.setInfoSave(infoSave);

		scroll = new JScrollPane(hallPanel);//左侧大厅区
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);//中部分隔
		split.setLeftComponent(scroll);		//左侧
		split.setRightComponent(chatSystem);//右侧
		scroll.setMinimumSize(new Dimension(703, 600));
		this.add(split);
		this.setPreferredSize(new Dimension(1021, 737));
		this.pack();	//自动设置最佳大小
		this.setResizable(false);
		//设置窗台在屏幕中心显示
		this.setLocationRelativeTo(null);
		//设置按关闭窗体时退出程序，默认只是隐藏窗体
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		this.setTitle("五子棋游戏大厅");
		this.setVisible(true);
	}
}
