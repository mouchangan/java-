
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
//���Ҳ����촰����Ϣ������Ϸ�����ϲ�
//���ô�С

public class GameHall extends JFrame
{
	private InfoSave infoSave;
	ChatSystem chatSystem = new ChatSystem();
	HallPanel hallPanel = new HallPanel(25 , 5);
	private JSplitPane split;	//�ָ����
	private JScrollPane scroll;	//�������

	public void init(InfoSave infoSave)
	{
		this.infoSave = infoSave;
		chatSystem.setInfoSave(infoSave);
		chatSystem.	init();		//�Ҳ���Ϣ��
		hallPanel.setInfoSave(infoSave);

		scroll = new JScrollPane(hallPanel);//��������
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);//�в��ָ�
		split.setLeftComponent(scroll);		//���
		split.setRightComponent(chatSystem);//�Ҳ�
		scroll.setMinimumSize(new Dimension(703, 600));
		this.add(split);
		this.setPreferredSize(new Dimension(1021, 737));
		this.pack();	//�Զ�������Ѵ�С
		this.setResizable(false);
		//���ô�̨����Ļ������ʾ
		this.setLocationRelativeTo(null);
		//���ð��رմ���ʱ�˳�����Ĭ��ֻ�����ش���
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		this.setTitle("��������Ϸ����");
		this.setVisible(true);
	}
}
