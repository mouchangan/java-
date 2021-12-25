
//import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
//判定用户输入的信息
//大厅更新图标
//服务器线程
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
				//用户登录的名字
				if (buffer.startsWith(MyProtocol.USER_ROUND) && buffer.endsWith(MyProtocol.USER_ROUND))
				{
					String userInfo = buffer.substring(2 , buffer.length()-2);
					String userName = userInfo.substring(0 , userInfo.indexOf(":"));//名称
					String userSex = userInfo.substring(userInfo.indexOf(":") + 1 , userInfo.indexOf(";"));//性别
					String userIcon = userInfo.substring(userInfo.indexOf(";") + 1);//头像

					//名称存在的话
					if (Server.clients.containsKey(userName))
					{
						System.out.println("重复");
						ps.println(MyProtocol.NAME_REP);					
					}
					else
					{
						synchronized(this)
						{
							System.out.println("成功");
							ps.println(MyProtocol.LOGIN_SUCCESS);
							Hashtable player = new Hashtable();
							player.put("sex" , userSex);
							player.put("icon" , userIcon);
							Server.playerLogin.put(userName , player);
							
							Thread.sleep(1000);


							//把所有已在线玩家的信息发给新登录的用户
							for (String name : Server.clients.keySet())
							{
								ps.println(MyProtocol.USER_NAME + name + ":" +
											Server.playerLogin.get(name).get("sex") + ";" +
											Server.playerLogin.get(name).get("icon") + MyProtocol.USER_NAME);
							}
							Server.clients.put(userName , socket);


							//每新登录一个用户就把自己用户的登录信息发给所有在线的玩家
							for (Socket client : Server.clients.valueSet())
							{
								ps = new PrintStream(client.getOutputStream());
								ps.println(MyProtocol.USER_NAME + userName + ":" + userSex + ";" +
											userIcon + MyProtocol.USER_NAME);
							}
							Thread.sleep(300);


							//把所有在桌子中玩家的信息发给新登录的用户更新他们的大厅桌子头像
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


							//把所有在游戏中的信息发给新登录的用户更新他们的大厅桌子头像
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
				//私聊信息
				else if (buffer.startsWith(MyProtocol.MSG_PRIVATE) && buffer.endsWith(MyProtocol.MSG_PRIVATE))
				{
					String name = buffer.substring(2 , buffer.indexOf(MyProtocol.NAME_MSG));
					msg = buffer.substring(buffer.indexOf(MyProtocol.NAME_MSG) + 1 , buffer.length()-2);
					//给自己显示
					ps.println(MyProtocol.MSG_PRIVATE + msg + MyProtocol.MSG_PRIVATE);
					//对方显示对话信息
					ps = new PrintStream(Server.clients.get(name).getOutputStream());
					ps.println(MyProtocol.MSG_PRIVATE + msg + MyProtocol.MSG_PRIVATE);
				}
				//玩家点击桌子进入游戏的信息
				else if (buffer.startsWith(MyProtocol.TABLE_INFO) && buffer.endsWith(MyProtocol.TABLE_INFO))
				{
					String tableInfo = buffer.substring(2 , buffer.length()-2);
					String tableNumber = tableInfo.substring(0 , tableInfo.indexOf(":"));
					String way =  tableInfo.substring(tableInfo.indexOf(":") + 1 , tableInfo.indexOf(";"));
					String name = tableInfo.substring(tableInfo.indexOf(";") + 1 , tableInfo.indexOf("."));
					String sex = tableInfo.substring(tableInfo.indexOf(".") + 1 , tableInfo.indexOf(","));
					String iconNumber = tableInfo.substring(tableInfo.indexOf(",") + 1);
					//向每个在线玩家传送进入游戏的玩家的信息，以更新他们在大厅所看到的头像
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.TABLE_HEAD + tableNumber + ":"
								  + way + ";" + iconNumber + MyProtocol.TABLE_HEAD);
					}
					//创建一个存放所有进入桌子的玩家信息的集合
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
					//如果桌子有两个人的话就互相通讯
					if (Server.table.get(tableNumber).size() == 2)
					{
						String otherWay = String.valueOf(Integer.parseInt(way) * (-1));
						Hashtable a = (Hashtable)(Server.table.get(tableNumber).get(otherWay));
						Object otherName = a.get("name");
						Object otherSex = a.get("sex");
						Object otherNumber = a.get("iconNumber");
						
						ps = new PrintStream(socket.getOutputStream());
						//他进入之前对方已经准备好了？？？？？？？？？？？？？
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
						//----------左手边的玩家先下棋----------//
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
				//处理游戏里面的聊天室信息
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
				//----------处理用户退出游戏----------//
				else if (buffer.startsWith(MyProtocol.GAME_EXIT) && buffer.endsWith(MyProtocol.GAME_EXIT))
				{
					String table = buffer.substring(2 , buffer.length()-2);
					String tableNumber = table.substring(0 , table.indexOf(":"));
					String location = table.substring(table.indexOf(":") + 1 , table.indexOf(";"));
					String size = table.substring(table.indexOf(";") + 1);
					//从集合中删除退出游戏的用户
					Server.table.get(tableNumber).remove(location);

					if (Server.table.get(tableNumber).size() == 0)
					{
						Server.table.remove(tableNumber);
					}
					//把退出游戏的用户信息发给所有在线的玩家
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.GAME_EXIT + tableNumber + ":" + location + MyProtocol.GAME_EXIT);
					}
					//把玩家退出的信息发给对手
					if (size.equals("2"))
					{
						ps = new PrintStream(Server.clients.get(getOtherName(location , tableNumber)).getOutputStream());
						ps.println(MyProtocol.GAMEUSER_EXIT);
					}
				}
				//玩家游戏途中强行退出
				else if (buffer.startsWith(MyProtocol.PLAYING_EXIT) && buffer.endsWith(MyProtocol.PLAYING_EXIT))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					Server.playerReady.remove(tableNumber);
					//清除大厅游戏中状态的台图片
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				//收到游戏下棋的坐标信息
				else if (buffer.startsWith(MyProtocol.X_Y) && buffer.endsWith(MyProtocol.X_Y))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String tableNumber = game.substring(0 , game.indexOf(":"));
					String way = game.substring(game.indexOf(":") + 1 , game.indexOf(";"));
					String X = game.substring(game.indexOf(";") + 1 , game.indexOf(","));
					String Y = game.substring(game.indexOf(",") + 1);
					//把信息发去对方玩家
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.X_Y + way + ":" + X + "," + Y + MyProtocol.X_Y);

				}
				//----------收到玩家准备好游戏的信息----------//
				else if (buffer.startsWith(MyProtocol.GAME_READY) && buffer.endsWith(MyProtocol.GAME_READY))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String tableNumber = game.substring(0 , game.indexOf(":"));
					String way = game.substring(game.indexOf(":") + 1 , game.indexOf(";"));
					String name = game.substring(game.indexOf(";") + 1);
					String otherWay = String.valueOf(Integer.parseInt(way) * (-1));
					
					if (Server.table.get(tableNumber).get(otherWay) != null)
					{
						//把此消息发给对方
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
						//把两边都准备好的信息发给左手边(黑棋)的玩家让他可以开始下棋
						for (Object w : Server.playerReady.get(tableNumber).keySet())
						{
							Object userName = Server.playerReady.get(tableNumber).get(w);
							ps = new PrintStream(Server.clients.get(userName).getOutputStream());
							ps.println(MyProtocol.ALL_READY);
						}
						
						//把该台开始游戏的信息发给所有人更新他们在大厅显示的该桌面的图象
						for (Socket client : Server.clients.valueSet())
						{
							ps = new PrintStream(client.getOutputStream());
							ps.println(MyProtocol.PLAYING + tableNumber + MyProtocol.PLAYING);
						}
					}
				}
				//收到玩家悔棋信息
				else if (buffer.startsWith(MyProtocol.REGRET_STEP) && buffer.endsWith(MyProtocol.REGRET_STEP))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.REGRET_STEP);
				}
				//收到玩家同意悔棋信息
				else if (buffer.startsWith(MyProtocol.REGRET_AGREE) && buffer.endsWith(MyProtocol.REGRET_AGREE))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.REGRET_AGREE);
				}
				//收到玩家拒绝悔棋信息
				else if (buffer.startsWith(MyProtocol.REGRET_REJECT) && buffer.endsWith(MyProtocol.REGRET_REJECT))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.REGRET_REJECT);
				}
				//收到玩家求和请求
				else if (buffer.startsWith(MyProtocol.PEACE) && buffer.endsWith(MyProtocol.PEACE))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.PEACE);
				}
				//收到对手同意求和的信息
				else if (buffer.startsWith(MyProtocol.PEACE_AGREE) && buffer.endsWith(MyProtocol.PEACE_AGREE))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					Server.playerReady.remove(tableNumber);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.PEACE_AGREE);
					//清除大厅游戏中状态的台图片
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				//收到对手拒绝求和的信息
				else if (buffer.startsWith(MyProtocol.PEACE_REJECT) && buffer.endsWith(MyProtocol.PEACE_REJECT))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.PEACE_REJECT);
				}
				//----------收到对手认输的信息----------//
				else if (buffer.startsWith(MyProtocol.LOST) && buffer.endsWith(MyProtocol.LOST))
				{
					String state = buffer.substring(2 , buffer.length()-2);
					String tableNumber = state.substring(0 , state.indexOf(":"));
					String way = state.substring(state.indexOf(":") + 1);
					Server.playerReady.remove(tableNumber);
					ps = new PrintStream(Server.clients.get(getOtherName(way , tableNumber)).getOutputStream());
					ps.println(MyProtocol.LOST + way + MyProtocol.LOST);
					//清除大厅游戏中状态的台图片
					for (Socket client : Server.clients.valueSet())
					{
						ps = new PrintStream(client.getOutputStream());
						ps.println(MyProtocol.PLAYING_EXIT + tableNumber + MyProtocol.PLAYING_EXIT);
					}
				}
				//----------收到该桌子的游戏已经有人胜利结束了，清除该桌子游戏中状态的图象----------//
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
				//----------通过退出用户的socket找出退出用户的name----------//
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
				//----------把退出用户的用户名发给全部在线的用户----------//
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
	//获得对方信息
	public String getOtherName(String way , String tableNumber)
	{
		String otherWay = String.valueOf(Integer.parseInt(way) * (-1));
		Hashtable a = (Hashtable)(Server.table.get(tableNumber).get(otherWay));
		Object otherName = a.get("name");
		return (String)otherName;
	}
}
