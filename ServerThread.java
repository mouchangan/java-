
//import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
//�ж��û��������Ϣ
//��������ͼ��
//�������߳�
public class ServerThread extends Thread
{
	private Socket socket;
	private BufferedReader br;
	private PrintStream ps;
	private String buffer;
	private String msg;
	private Hashtable<String , Hashtable> player;
	private Hashtable players;
	

	public ServerThread()
	{
	}

	public ServerThread(Socket socket)
	{
		this.socket = socket;
	}

	public void run()
	{
		try
		{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((buffer = br.readLine()) != null)
			{
				ps = new PrintStream(socket.getOutputStream());//
				//�û���¼������
				if (buffer.startsWith(MyProtocol.USER_ROUND) && buffer.endsWith(MyProtocol.USER_ROUND))
				{
					String userInfo = buffer.substring(2 , buffer.length()-2);
					String userName = userInfo.substring(0 , userInfo.indexOf(":"));//����
					String userSex = userInfo.substring(userInfo.indexOf(":") + 1 , userInfo.indexOf(";"));//�Ա�
					String userIcon = userInfo.substring(userInfo.indexOf(";") + 1);//ͷ��

					//���ƴ��ڵĻ�
					if (Server.clients.containsKey(userName))
					{
						System.out.println("�ظ�");
						ps.println(MyProtocol.NAME_REP);					
					}
					else
					{
						synchronized(this)
						{
							System.out.println("�ɹ�");
							ps.println(MyProtocol.LOGIN_SUCCESS);
							Hashtable player = new Hashtable();
							player.put("sex" , userSex);
							player.put("icon" , userIcon);
							Server.playerLogin.put(userName , player);
							
							Thread.sleep(1000);


							//��������������ҵ���Ϣ�����µ�¼���û�
							for (String name : Server.clients.keySet())
							{
								ps.println(MyProtocol.USER_NAME + name + ":" +
											Server.playerLogin.get(name).get("sex") + ";" +
											Server.playerLogin.get(name).get("icon") + MyProtocol.USER_NAME);
							}
							Server.clients.put(userName , socket);


							//ÿ�µ�¼һ���û��Ͱ��Լ��û��ĵ�¼��Ϣ�����������ߵ����
							for (Socket client : Server.clients.valueSet())
							{
								ps = new PrintStream(client.getOutputStream());
								ps.println(MyProtocol.USER_NAME + userName + ":" + userSex + ";" +
											userIcon + MyProtocol.USER_NAME);
							}
							Thread.sleep(300);


							//����������������ҵ���Ϣ�����µ�¼���û��������ǵĴ�������ͷ��
							for (String tNumber : Server.table.keySet())
							{
								 for (Object ways : Server.table.get(tNumber).keySet())
								 {
									 Hashtable a = (Hashtable)( Server.table.get(tNumber).get(ways));
									 Object inumber = a.get("iconNumber");
									 ps = new PrintStream(socket.getOutputStream());
									 ps.println(MyProtocol.TABLE_HEAD + tNumber + ":"
												+ ways + ";" + inumber + MyProtocol.TABLE_HEAD);
								 }
							}
							Thread.sleep(300);


							//����������Ϸ�е���Ϣ�����µ�¼���û��������ǵĴ�������ͷ��
							for (String tableNumber : Server.playerReady.keySet())
							{
								if (Server.playerReady.get(tableNumber).size() == 2)
								{
									ps = new PrintStream(socket.getOutputStream());
									ps.println(MyProtocol.PLAYING + tableNumber + MyProtocol.PLAYING);
								}
							}
						}	
					}
				}
				//˽����Ϣ
				else if (buffer.startsWith(MyProtocol.MSG_PRIVATE) && buffer.endsWith(MyProtocol.MSG_PRIVATE))
				{
					String name = buffer.substring(2 , buffer.indexOf(MyProtocol.NAME_MSG));
					msg = buffer.substring(buffer.indexOf(MyProtocol.NAME_MSG) + 1 , buffer.length()-2);
					//���Լ���ʾ
					ps.println(MyProtocol.MSG_PRIVATE + msg + MyProtocol.MSG_PRIVATE);
					//�Է���ʾ�Ի���Ϣ
					ps = new PrintStream(Server.clients.get(name).getOutputStream());
					ps.println(MyProtocol.MSG_PRIVATE + msg + MyProtocol.MSG_PRIVATE);
				}
				//��ҵ�����ӽ�����Ϸ����Ϣ
				else if (buffer.startsWith(MyProtocol.TABLE_INFO) && buffer.endsWith(MyProtocol.TABLE_INFO))
				{
					String tableInfo = buffer.substring(2 , buffer.length()-2);
					String tableNumber = tableInfo.substring(0 , tableInfo.indexOf(":"));
					String way =  tableInfo.substring(tableInfo.indexOf(":") + 1 , tableInfo.indexOf(";"));
					String name = tableInfo.substring(tableInfo.indexOf(";") + 1 , tableInfo.indexOf("."));
					String sex = tableInfo.substring(tableInfo.indexOf(".") + 1 , tableInfo.indexOf(","));
					String iconNumber = tableInfo.substring(tableInfo.indexOf(",") + 1);
					//��ÿ��������Ҵ��ͽ�����Ϸ����ҵ���Ϣ���Ը��������ڴ�����������ͷ��
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.TABLE_HEAD + tableNumber + ":"
								  + way + ";" + iconNumber + MyProtocol.TABLE_HEAD);
					}
					//����һ��������н������ӵ������Ϣ�ļ���
					if (Server.table.get(tableNumber) == null)
					{
						player = new Hashtable<String , Hashtable>();
						Server.table.put(tableNumber , player);
					}
					Hashtable playerInfo = new Hashtable();
					playerInfo.put("name" , name);
					playerInfo.put("sex" , sex);
					playerInfo.put("iconNumber" , iconNumber);
					Server.table.get(tableNumber).put(way , playerInfo);	
					//��������������˵Ļ��ͻ���ͨѶ
					if (Server.table.get(tableNumber).size() == 2)
					{
						String otherWay = String.valueOf(Integer.parseInt(way) * (-1));
						Hashtable a = (Hashtable)(Server.table.get(tableNumber).get(otherWay));
						Object otherName = a.get("name");
						Object otherSex = a.get("sex");
						Object otherNumber = a.get("iconNumber");
						
						ps = new PrintStream(socket.getOutputStream());
						//������֮ǰ�Է��Ѿ�׼�����ˣ�������������������������
						if (Server.playerReady.get(tableNumber) != null)
						{
							Thread.sleep(1000);
							ps.println(MyProtocol.OTHER_READY);
						}

						ps.println(MyProtocol.TABLE_INFO + Server.table.get(tableNumber).size() + ":"
								  + otherName + ";" + otherSex + "." + 
							      otherNumber + "," + otherWay + MyProtocol.TABLE_INFO);

						ps = new PrintStream(Server.clients.get(otherName).getOutputStream());

						ps.println(MyProtocol.TABLE_INFO + Server.table.get(tableNumber).size() + ":" 
								  + name + ";" + sex + "." + iconNumber + "," + way + MyProtocol.TABLE_INFO);
						//----------���ֱߵ����������----------//
						Hashtable b = (Hashtable)(Server.table.get(tableNumber).get(MyProtocol.LEFT_TABLE));
						Object playerName = b.get("name");
						ps = new PrintStream(Server.clients.get(playerName).getOutputStream());
					}
					else
					{
						ps.println(MyProtocol.TABLE_INFO + Server.table.get(tableNumber).size() + ":" 
								   + way + MyProtocol.TABLE_INFO);
					}
						
				}
				//������Ϸ�������������Ϣ
				else if (buffer.startsWith(MyProtocol.GAME_MSG) && buffer.endsWith(MyProtocol.GAME_MSG))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String tableN = game.substring(0 , game.indexOf(","));
					msg = game.substring(game.indexOf(",") + 1);
					for (Object ways : Server.table.get(tableN).keySet())
				    {
						Hashtable a = (Hashtable)( Server.table.get(tableN).get(ways));
					    Object name = a.get("name");
						ps = new PrintStream(Server.clients.get(name).getOutputStream());
						ps.println(MyProtocol.GAME_MSG + msg + MyProtocol.GAME_MSG);
					}
				}
				//----------�����û��˳���Ϸ----------//
				else if (buffer.startsWith(MyProtocol.GAME_EXIT) && buffer.endsWith(MyProtocol.GAME_EXIT))
				{
					String table = buffer.substring(2 , buffer.length()-2);
					String tableNumber = table.substring(0 , table.indexOf(":"));
					String location = table.substring(table.indexOf(":") + 1 , table.indexOf(";"));
					String size = table.substring(table.indexOf(";") + 1);
					//�Ӽ�����ɾ���˳���Ϸ���û�
					Server.table.get(tableNumber).remove(location);

					if (Server.table.get(tableNumber).size() == 0)
					{
						Server.table.remove(tableNumber);
					}
					//���˳���Ϸ���û���Ϣ�����������ߵ����
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.GAME_EXIT + tableNumber + ":" + location + MyProtocol.GAME_EXIT);
					}
					//������˳�����Ϣ��������
					if (size.equals("2"))
					{
						ps = new PrintStream(Server.clients.get(getOtherName(location , tableNumber)).getOutputStream());
						ps.println(MyProtocol.GAMEUSER_EXIT);
					}
				}
				//�����Ϸ;��ǿ���˳�
				else if (buffer.startsWith(MyProtocol.PLAYING_EXIT) && buffer.endsWith(MyProtocol.PLAYING_EXIT))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					Server.playerReady.remove(tableNumber);
					//���������Ϸ��״̬��̨ͼƬ
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				//�յ���Ϸ�����������Ϣ
				else if (buffer.startsWith(MyProtocol.X_Y) && buffer.endsWith(MyProtocol.X_Y))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String tableNumber = game.substring(0 , game.indexOf(":"));
					String way = game.substring(game.indexOf(":") + 1 , game.indexOf(";"));
					String X = game.substring(game.indexOf(";") + 1 , game.indexOf(","));
					String Y = game.substring(game.indexOf(",") + 1);
					//����Ϣ��ȥ�Է����
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.X_Y + way + ":" + X + "," + Y + MyProtocol.X_Y);

				}
				//----------�յ����׼������Ϸ����Ϣ----------//
				else if (buffer.startsWith(MyProtocol.GAME_READY) && buffer.endsWith(MyProtocol.GAME_READY))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String tableNumber = game.substring(0 , game.indexOf(":"));
					String way = game.substring(game.indexOf(":") + 1 , game.indexOf(";"));
					String name = game.substring(game.indexOf(";") + 1);
					String otherWay = String.valueOf(Integer.parseInt(way) * (-1));
					
					if (Server.table.get(tableNumber).get(otherWay) != null)
					{
						//�Ѵ���Ϣ�����Է�
						ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
						ps.println(MyProtocol.OTHER_READY);
					}
					
					if (Server.playerReady.get(tableNumber) == null)
					{
						players = new Hashtable();
						Server.playerReady.put(tableNumber , players);
					}
					Server.playerReady.get(tableNumber).put(way , name);			
					
					if (Server.playerReady.get(tableNumber).size() == 2)
					{
						//�����߶�׼���õ���Ϣ�������ֱ�(����)������������Կ�ʼ����
						for (Object w : Server.playerReady.get(tableNumber).keySet())
						{
							Object userName = Server.playerReady.get(tableNumber).get(w);
							ps = new PrintStream(Server.clients.get(userName).getOutputStream());
							ps.println(MyProtocol.ALL_READY);
						}
						
						//�Ѹ�̨��ʼ��Ϸ����Ϣ���������˸��������ڴ�����ʾ�ĸ������ͼ��
						for (Socket client : Server.clients.valueSet())
						{
							ps = new PrintStream(client.getOutputStream());
							ps.println(MyProtocol.PLAYING + tableNumber + MyProtocol.PLAYING);
						}
					}
				}
				//�յ���һ�����Ϣ
				else if (buffer.startsWith(MyProtocol.REGRET_STEP) && buffer.endsWith(MyProtocol.REGRET_STEP))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.REGRET_STEP);
				}
				//�յ����ͬ�������Ϣ
				else if (buffer.startsWith(MyProtocol.REGRET_AGREE) && buffer.endsWith(MyProtocol.REGRET_AGREE))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.REGRET_AGREE);
				}
				//�յ���Ҿܾ�������Ϣ
				else if (buffer.startsWith(MyProtocol.REGRET_REJECT) && buffer.endsWith(MyProtocol.REGRET_REJECT))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.REGRET_REJECT);
				}
				//�յ�����������
				else if (buffer.startsWith(MyProtocol.PEACE) && buffer.endsWith(MyProtocol.PEACE))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.PEACE);
				}
				//�յ�����ͬ����͵���Ϣ
				else if (buffer.startsWith(MyProtocol.PEACE_AGREE) && buffer.endsWith(MyProtocol.PEACE_AGREE))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					Server.playerReady.remove(tableNumber);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.PEACE_AGREE);
					//���������Ϸ��״̬��̨ͼƬ
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				//�յ����־ܾ���͵���Ϣ
				else if (buffer.startsWith(MyProtocol.PEACE_REJECT) && buffer.endsWith(MyProtocol.PEACE_REJECT))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.PEACE_REJECT);
				}
				//----------�յ������������Ϣ----------//
				else if (buffer.startsWith(MyProtocol.LOST) && buffer.endsWith(MyProtocol.LOST))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					Server.playerReady.remove(tableNumber);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.LOST + way + MyProtocol.LOST);
					//���������Ϸ��״̬��̨ͼƬ
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				//----------�յ������ӵ���Ϸ�Ѿ�����ʤ�������ˣ������������Ϸ��״̬��ͼ��----------//
				else if (buffer.startsWith(MyProtocol.WIN) && buffer.endsWith(MyProtocol.WIN))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					Server.playerReady.remove(tableNumber);
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				else
				{
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						msg = buffer.substring(2 , buffer.length()-2);
						ps.println(msg);
					}
				}
			}
			
		}
		catch (Exception e)
		{
			try
			{
				//----------ͨ���˳��û���socket�ҳ��˳��û���name----------//
				String name = "";
				for (Object key : Server.clients.keySet())
				{
					if (Server.clients.get(key) == socket)
					{
						name = (String)key;
						break;
					}
				}
				Server.clients.removeByValue(socket);
				//----------���˳��û����û�������ȫ�����ߵ��û�----------//
				for (Socket client : Server.clients.valueSet())
				{
					ps = new PrintStream(client.getOutputStream());
					ps.println(MyProtocol.USER_EXIT + name + MyProtocol.USER_EXIT);
				}
				System.out.println(Server.clients.size());
			}
			catch (Exception ev)
			{
			}
		}
		finally
		{
			try
			{
				br.close();
				socket.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	//��öԷ���Ϣ
	public String getOtherName(String way , String tableNumber)
	{
		String otherWay = String.valueOf(Integer.parseInt(way) * (-1));
		Hashtable a = (Hashtable)(Server.table.get(tableNumber).get(otherWay));
		Object otherName = a.get("name");
		return (String)otherName;
	}
}
