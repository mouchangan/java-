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

//大厅聊天室和对弈聊天室
//聊天室监听用户输入信息
//添加聊天室上方用户列表
//聊天系统框架
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
		showMes = new JTextArea("欢迎");
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
	
	//游戏中的聊天室
	public ChatSystem(boolean inGame , int tableNumber)
	{
		this.tableNumber = tableNumber;
		this.inGame = inGame;
	}
	//大厅中的聊天室
	public ChatSystem()
	{	
	}
	//聊天框界面设设置
	public void init()
	{
		person = new Person(this.infoSave);
		person.setPreferredSize(new Dimension(300,340));
		
		tabbedPane.addTab("所有信息" ,  new ImageIcon("D:\\0\\fiveStone\\ie.gif") , scroll , "显示所有的信息");
		tabbedPane.addTab("私聊信息" ,  new ImageIcon("D:\\0\\fiveStone\\xnview.gif") , scrollPrivate , "只显示私聊的信息");
		tabbedhead.addTab("房间信息" , new ImageIcon("D:\\0\\fiveStone\\vmware.gif") , scrollPane);
		tabbedhead.addTab("个人信息" , new ImageIcon("D:\\0\\fiveStone\\emule_01.gif") , person);
		
		showMesPrivate.setForeground(new Color(123,80,163));
		showMesPrivate.setBackground(new Color(200,221,242));
		//选择私聊对象
		cb.addItem("所有人");
		
		table.setBackground(Color.white);
		table.setRowHeight(32);
		
		tabbedPane.setPreferredSize(new Dimension(300,255));
		tabbedhead.setPreferredSize(new Dimension(300,340));

		send = new JButton("发送");
		
	    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel panelWhole = new JPanel();
		JPanel panelSplit = new JPanel();
		panelButton.setBorder(BorderFactory.createEtchedBorder() );
		panelButton.setPreferredSize(new Dimension(300 , 50));
		panelButton.setBackground(new Color(200,221,242));
		//制造容器的边界
		chat.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createLineBorder(new Color(40, 106, 196), 1),"输入信息",
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
		//右上角房间及个人信息
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
		//滚条移到最底
		scrollBar = scroll.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	public void showMesPrivate(String msg)
	{
		this.showMesPrivate.append(msg + "\n");
		//滚条移到最底
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
	//发送按钮监听者
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
			//对所有人说
			if (chat.getText().length() == 0)
			{
				return;
			}
			infoSave.setMessage(chat.getText());
			ps = new PrintStream(infoSave.getSocket().getOutputStream());
			//在大厅中发信息
			if (!inGame)	//不在游戏中
			{
				if (cb.getSelectedIndex() == 0)
				{
					ps.println(MyProtocol.MSG_ROUND + infoSave.getUserName() + " 对大家说：" +
								infoSave.getMessage() + MyProtocol.MSG_ROUND);
				}
				else
				{
					String name = (String)cb.getSelectedItem();
					ps.println(MyProtocol.MSG_PRIVATE + name + MyProtocol.NAME_MSG + infoSave.getUserName() + 
								" 对 " + name + "说：" + infoSave.getMessage() + MyProtocol.MSG_PRIVATE);
				}
				chat.setText("");//发送后清空聊天框
			}
			//在游戏中发信息
			else
			{
				ps.println(MyProtocol.GAME_MSG + this.tableNumber + "," + infoSave.getUserName() + " 说：" +
						   infoSave.getMessage() + MyProtocol.GAME_MSG);
				chat.setText("");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	//tableModel设置修改表格的各种信息
	class PlayersModel extends AbstractTableModel
	{
		private int idIndex = 3;
		private Vector player = null;
		private String[] title_name = { "头像", "昵称", "性别"};

		public PlayersModel()
		{
			player = new Vector();
		}

		public PlayersModel(int count)
		{
			player = new Vector(count);
		}

		//添加用户信息
		public void addPlayer(ImageIcon headIcon, String name, String sex)
		{
			Vector v = new Vector(3);
			v.add(0, headIcon);//头像图标
			v.add(1, name);
			v.add(2, sex);
			player.add(v);
		}

		//删除用户信息
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

		//使修改的内容生效
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

		//返回数据类型
		public Class getColumnClass(int col)
		{
			return getValueAt(0, col).getClass();
		}
	}

	public void inGame()
	{
		this.inGame = true;
	}

	//设置组件大小
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
