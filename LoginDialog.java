

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

import static java.lang.Thread.sleep;

//��¼����
//�˿ھ���
//��¼�Ի�����
public class LoginDialog<buttonReLink> extends JFrame
{
	private InfoSave infoSave;
	private String name;
	private String ip;
	private int icon;
	private int port;
//	private static int attempts	=0;
	private String sex;
	private JTextField portText;
	private JTextField ipText;
	private	JTextField nameText;
	private JComboBox sexSelect;
	private ImageIcon imageIcon;
	private static int count=5;
//	private JFrame cantLink = new JFrame();
//	private JPanel linkPanle = new JPanel();
//	private JTextField linkTextField = new JTextField("");
//	private JButton buttonReLink = new JButton("��������");


	static HeadPanel headPanel = new HeadPanel();
	static JLabel headLabe = new JLabel(new ImageIcon("Icon\\23.jpg"));

	public LoginDialog()
	{
		String[] sexString = {"��" , "Ů"};

		JPanel panel = new JPanel();
		JButton buttonLogin = new JButton("��¼");
		JButton buttonCancel = new JButton("ȡ��");

		sexSelect = new JComboBox(sexString);
		sexSelect.setPreferredSize(new Dimension(60,22));

		JLabel name = new JLabel("����");
		JLabel ip = new JLabel("I   P ");
		JLabel port = new JLabel("�˿�");
		JLabel sex = new JLabel("�Ա�");
		JLabel imageTitle = new JLabel(new ImageIcon("fiveStone\\login.jpg"));
		JLabel head = new JLabel("��ǰͷ��     "); 
		nameText = new JTextField("zjh" , 10);
		ipText = new JTextField("127.0.0.1" , 10);
		portText = new JTextField("49000" , 6);
		JPanel namePanel = new JPanel();
		JPanel ipPanel = new JPanel();
		JPanel portPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel sexPanel = new JPanel();
		JPanel allinfo = new JPanel();
		JPanel title = new JPanel();
		namePanel.add(name);
		namePanel.add(nameText);

		ipPanel.add(ip);
		ipPanel.add(ipText);

		portPanel.add(port);
		portPanel.add(portText);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER , 80 , 10));
		buttonPanel.add(buttonLogin);
		buttonPanel.add(buttonCancel);

		sexPanel.add(sex);
		sexPanel.add(sexSelect);

		allinfo.setLayout(new GridLayout(2, 2));
		allinfo.add(namePanel);
		allinfo.add(portPanel);
		allinfo.add(ipPanel);
		allinfo.add(sexPanel);



		JPanel headP = new JPanel();
		JLabel empty = new JLabel("     ");
		headP.setLayout(new BorderLayout());
		headP.add(head , BorderLayout.NORTH);
		headP.add(headLabe);
		headP.add(empty , BorderLayout.EAST);

		JPanel info = new JPanel();
		
		info.setLayout(new BorderLayout());
		info.add(allinfo);
		info.add(headP , BorderLayout.EAST);
		
		title.setLayout(new BorderLayout());
		title.add(imageTitle , BorderLayout.NORTH);
		title.add(info);
		
		this.setTitle("��¼��");
		this.setResizable(false);
		this.add(title , BorderLayout.NORTH);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(headPanel);
		this.setSize(418,420);
		this.setLocationRelativeTo(null);
		this.setVisible(true);


		buttonLogin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent a)
			{
				try {
					Click();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		nameText.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{	
				if (e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					try {
						Click();
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent a)
			{
				System.exit(0);
			}
		});
	}
	
	public void Click() throws InterruptedException {
		if (nameText.getText().equals(""))
		{
			JOptionPane.showMessageDialog(null, "�û�������Ϊ��", "����",JOptionPane.WARNING_MESSAGE);
		}
		else if (nameText.getText().length() > 10)
		{
			JOptionPane.showMessageDialog(null, "�û������Ȳ��ܶ���10�ַ�", "����",JOptionPane.WARNING_MESSAGE);
		}
		else if (ipText.getText().equals(""))
		{
			JOptionPane.showMessageDialog(null, "IP��ַ����Ϊ��", "����",JOptionPane.WARNING_MESSAGE);
		}
		else if (portText.getText().equals(""))
		{
			JOptionPane.showMessageDialog(null, "�˿ڲ���Ϊ��", "����",JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			this.name = nameText.getText();
			this.sex = (String)sexSelect.getSelectedItem();
			this.icon = headPanel.getIconNumber();
			this.imageIcon = headPanel.getImage(headPanel.getIconNumber());
			this.ip = ipText.getText();
			this.port = Integer.parseInt(portText.getText());
			
			infoSave = new InfoSave();
			this.infoSave.setHeadIcon(headPanel.getHeadIcon(headPanel.getIconNumber()));
			this.infoSave.setHeadPanel(headPanel);
			this.infoSave.setIP(ip);
			this.infoSave.setSex(sex);
			this.infoSave.setIcon(icon);
			this.infoSave.setImageIcon(imageIcon);
			this.infoSave.setPort(port);
			this.infoSave.setUserName(name);	
			this.infoSave.setLoginDialog(this);

			GameHall gameHall = new GameHall();	
			this.infoSave.setGameHall(gameHall);	
			this.infoSave.setChatSystem(this.infoSave.getGameHall().chatSystem);
			this.infoSave.setHallPanel(this.infoSave.getGameHall().hallPanel);

			Client client = new Client();
			this.infoSave.setClient(client);
			for(count = 5;count>0 ;count--){
				int keepTrying=client.setInfo(this.name , this.ip , this.port , this.sex , this.icon , this.infoSave,this.count);
//				client.setInfo(this.name , this.ip , this.port , this.sex , this.icon , this.infoSave,this.count);

				if(keepTrying==0)
				{
					break;
				}
			}
		}
	}


//	public void linkReStartWindows() throws InterruptedException {
//		linkPanle.setLayout(new FlowLayout());
//		linkPanle.add(buttonReLink);
//		linkPanle.add(linkTextField);
//		cantLink.setContentPane(linkPanle);
//		for(int count =5;count>0;count--){
//			linkTextField.setText("���ӷ�����ʧ�ܣ�����"+count+"����ٴγ���\t\n���ѳ���  "+(++attempts)+"��");
//			linkPanle.repaint();
//			sleep(1000);
//		}
//		linkPanle.setVisible(true);
//	}
	public static void main(String[] args)
	{
		LoginDialog dialog = new LoginDialog();

	}	
}
