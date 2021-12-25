import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.Vector;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.table.*;
//import java.awt.image.BufferedImage;

//���������ҺͶ���������
//�����Ҽ����û�������Ϣ
//����������Ϸ��û��б�
//����ϵͳ���
public class ChatSystem extends JPanel
{
	private InfoSave infoSave;
	private Person person;
	private JTextArea showMes;
	private JTextArea showMesPrivate;
	private JTextField chat;
	private JButton send;
	private JComboBox cb;
	private PrintStream ps;
	private JTabbedPane tabbedPane;
	private JTabbedPane tabbedhead;
	private JScrollPane scroll;
	private JScrollPane scrollPrivate;
	private JPanel panelButton;
	private JScrollBar scrollBar;


	private boolean inGame = false;
	private int tableNumber;
	PlayersModel playersModel ;
    JTable table;
	private JScrollPane scrollPane;

	{
		showMes = new JTextArea("��ӭ");
		showMesPrivate = new JTextArea();
		chat = new JTextField();
		cb = new JComboBox();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedhead = new JTabbedPane(JTabbedPane.TOP);
		scroll = new JScrollPane(showMes);
		scrollPrivate = new JScrollPane(showMesPrivate);
		panelButton = new JPanel();
		playersModel = new PlayersModel();
		table = new JTable(playersModel);
		scrollPane = new JScrollPane(table);
	}
	
	//��Ϸ�е�������
	public ChatSystem(boolean inGame , int tableNumber)
	{
		this.tableNumber = tableNumber;
		this.inGame = inGame;
	}
	//�����е�������
	public ChatSystem()
	{	
	}
	//��������������
	public void init()
	{
		person = new Person(this.infoSave);
		person.setPreferredSize(new Dimension(300,340));
		
		tabbedPane.addTab("������Ϣ" ,  new ImageIcon("D:\\0\\fiveStone\\ie.gif") , scroll , "��ʾ���е���Ϣ");
		tabbedPane.addTab("˽����Ϣ" ,  new ImageIcon("D:\\0\\fiveStone\\xnview.gif") , scrollPrivate , "ֻ��ʾ˽�ĵ���Ϣ");
		tabbedhead.addTab("������Ϣ" , new ImageIcon("D:\\0\\fiveStone\\vmware.gif") , scrollPane);
		tabbedhead.addTab("������Ϣ" , new ImageIcon("D:\\0\\fiveStone\\emule_01.gif") , person);
		
		showMesPrivate.setForeground(new Color(123,80,163));
		showMesPrivate.setBackground(new Color(200,221,242));
		//ѡ��˽�Ķ���
		cb.addItem("������");
		
		table.setBackground(Color.white);
		table.setRowHeight(32);
		
		tabbedPane.setPreferredSize(new Dimension(300,255));
		tabbedhead.setPreferredSize(new Dimension(300,340));

		send = new JButton("����");
		
	    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel panelWhole = new JPanel();
		JPanel panelSplit = new JPanel();
		panelButton.setBorder(BorderFactory.createEtchedBorder() );
		panelButton.setPreferredSize(new Dimension(300 , 50));
		panelButton.setBackground(new Color(200,221,242));
		//���������ı߽�
		chat.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createLineBorder(new Color(40, 106, 196), 1),"������Ϣ",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(40, 106, 196)));

		showMes.setForeground(new Color(255, 255, 255));
		showMes.setBackground(new Color(82, 128, 173));

		chat.setPreferredSize(new Dimension(300,40));
		send.setPreferredSize(new Dimension(60,25));
		showMes.setEditable(false);
		showMesPrivate.setEditable(false);

		BorderLayout border = new BorderLayout();
		panelSplit.setLayout(border);
		panelSplit.add(tabbedPane , border.NORTH);
		panelSplit.add(chat);
		panelSplit.add(panelButton , border.SOUTH);
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER , 30 , 10));
		panelButton.add(cb);
		panelButton.add(send);

		panelWhole.setLayout(new BorderLayout());
		panelWhole.add(panelSplit);
		panelWhole.add(panelButton , border.SOUTH);
		//���ϽǷ��估������Ϣ
		split.setBottomComponent(panelWhole);
		split.setTopComponent(tabbedhead);

		this.add(split);
		this.setMinimumSize(new Dimension(300 , 300));
		this.setVisible(true);

		clickEvent();
	}

	public void showMes(String msg)
	{
		this.showMes.append("\n" + msg);
		//�����Ƶ����
		scrollBar = scroll.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	public void showMesPrivate(String msg)
	{
		this.showMesPrivate.append(msg + "\n");
		//�����Ƶ����
		scrollBar = scroll.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
		scrollBar = scrollPrivate.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	public void comboBoxAddItem(Object anObject)
	{
		cb.addItem(anObject);
	}
	public void comboBoxRemoveItem(Object anObject) 
	{
		cb.removeItem(anObject);
	}
	public int comboBoxGetItemCount() { return cb.getItemCount(); }
	//���Ͱ�ť������
	public void clickEvent()
	{
		send.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendMessage(); 
			}
		});	

		chat.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)   
			{   
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					sendMessage();
				}
			}
		});
	}


	public void sendMessage()
	{
		try
		{
			//��������˵
			if (chat.getText().length() == 0)
			{
				return;
			}
			infoSave.setMessage(chat.getText());
			ps = new PrintStream(infoSave.getSocket().getOutputStream());
			//�ڴ����з���Ϣ
			if (!inGame)	//������Ϸ��
			{
				if (cb.getSelectedIndex() == 0)
				{
					ps.println(MyProtocol.MSG_ROUND + infoSave.getUserName() + " �Դ��˵��" +
								infoSave.getMessage() + MyProtocol.MSG_ROUND);
				}
				else
				{
					String name = (String)cb.getSelectedItem();
					ps.println(MyProtocol.MSG_PRIVATE + name + MyProtocol.NAME_MSG + infoSave.getUserName() + 
								" �� " + name + "˵��" + infoSave.getMessage() + MyProtocol.MSG_PRIVATE);
				}
				chat.setText("");//���ͺ���������
			}
			//����Ϸ�з���Ϣ
			else
			{
				ps.println(MyProtocol.GAME_MSG + this.tableNumber + "," + infoSave.getUserName() + " ˵��" +
						   infoSave.getMessage() + MyProtocol.GAME_MSG);
				chat.setText("");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	//tableModel�����޸ı��ĸ�����Ϣ
	class PlayersModel extends AbstractTableModel
	{
		private int idIndex = 3;
		private Vector player = null;
		private String[] title_name = { "ͷ��", "�ǳ�", "�Ա�"};

		public PlayersModel()
		{
			player = new Vector();
		}

		public PlayersModel(int count)
		{
			player = new Vector(count);
		}

		//����û���Ϣ
		public void addPlayer(ImageIcon headIcon, String name, String sex)
		{
			Vector v = new Vector(3);
			v.add(0, headIcon);//ͷ��ͼ��
			v.add(1, name);
			v.add(2, sex);
			player.add(v);
		}

		//ɾ���û���Ϣ
		public void removePlayer(int row)
		{
			player.remove(row);
		}

		public void removePlayer(String name)
		{
			for (Object o : player)
			{
				Vector v = (Vector)o;
				if (v.get(1).equals(name))
				{
					player.remove(v);
					return;
				}
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}

		//ʹ�޸ĵ�������Ч
		public void setValueAt(Object value, int row, int col)
		{
			((Vector) player.get(row)).remove(col);
			((Vector) player.get(row)).add(col, value);
			this.fireTableCellUpdated(row, col);
		}

		public String getColumnName(int col)
		{
			return title_name[col];
		}

		public int getColumnCount()
		{
			return title_name.length;
		}

		public int getRowCount()
		{
			return player.size();
		}

		public Object getValueAt(int row, int col)
		{
			return ((Vector) player.get(row)).get(col);
		}

		//������������
		public Class getColumnClass(int col)
		{
			return getValueAt(0, col).getClass();
		}
	}

	public void inGame()
	{
		this.inGame = true;
	}

	//���������С
	public void setTableSize(int w , int h)
	{
		tabbedhead.setPreferredSize(new Dimension(w , h));
	}

	public void setTabbedPaneSize(int w , int h)
	{
		tabbedPane.setPreferredSize(new Dimension(w , h));
	}
	
	public void setChatSize(int w , int h)
	{
		chat.setPreferredSize(new Dimension(w , h));
	}

	public void setButtonSize(int w , int h)
	{
		panelButton.setPreferredSize(new Dimension(w , h));
	}

	public void setInfoSave(InfoSave infoSave)
	{
		this.infoSave = infoSave;
	}
}
