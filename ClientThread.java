
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
//大厅显示界面
//当房间用户进行操作后大厅进行相应的操作传输至服务器端
//接口
//客户端线程
public class ClientThread extends Thread
{
	public static int RX;
	public static int RY;
	private InfoSave infoSave;


	public ClientThread(InfoSave infoSave)
	{
		this.infoSave = infoSave;
	}
	public ClientThread()
	{
	}
	public void run()
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(this.infoSave.getSocket().getInputStream()));
			String buffer = null;
			while ((buffer = br.readLine()) != null)
			{
				//收到用户新登录的信息
				if (buffer.startsWith(MyProtocol.USER_NAME) && buffer.endsWith(MyProtocol.USER_NAME))
				{
					synchronized(this)
					{
						String userInfo = buffer.substring(2 , buffer.length()-2);
						String userName = userInfo.substring(0 , userInfo.indexOf(":"));
						String userSex = userInfo.substring(userInfo.indexOf(":") + 1 , userInfo.indexOf(";"));
						int userIcon = Integer.parseInt(userInfo.substring(userInfo.indexOf(";") + 1));
						
						Thread.sleep(300);
						//向comboBox添加除了自己之外的人
						if (!infoSave.getUserName().equals(userName))
						{
							infoSave.getChatSystem().comboBoxAddItem(userName);
						}
						//向表格添加新用户
						infoSave.getChatSystem().playersModel.addPlayer(infoSave.getHeadPanel().getImage(userIcon)
																		, userName , userSex);
						//用updateUI更新表格
						infoSave.getChatSystem().table.updateUI();
					}
				}
				//收到用户退出的信息-清楚在线信息并更新窗口
				else if (buffer.startsWith(MyProtocol.USER_EXIT) && buffer.endsWith(MyProtocol.USER_EXIT))
				{
					String userName = buffer.substring(2 , buffer.length()-2);
					infoSave.getChatSystem().comboBoxRemoveItem(userName);
					infoSave.getChatSystem().playersModel.removePlayer(userName);
					infoSave.getChatSystem().table.updateUI();
				}
				//玩家点击桌子进入游戏的信息。。。。。。。。。。。。。。。。
				else if (buffer.startsWith(MyProtocol.TABLE_INFO) && buffer.endsWith(MyProtocol.TABLE_INFO))
				{
					String tableInfo = buffer.substring(2 , buffer.length()-2);
					String size = tableInfo.substring(0 , tableInfo.indexOf(":"));
					String way = null;
					if (size.equals("2"))
					{
						String name = tableInfo.substring(tableInfo.indexOf(":") + 1 , tableInfo.indexOf(";"));
						String sex = tableInfo.substring(tableInfo.indexOf(";") + 1 , tableInfo.indexOf("."));
						String iconNumber = tableInfo.substring(tableInfo.indexOf(".") + 1 , tableInfo.indexOf(","));
						int icon = Integer.parseInt(iconNumber);
						way =  tableInfo.substring(tableInfo.indexOf(",") + 1);

						Thread.sleep(300);
						infoSave.getHostGame().getChatSystem().playersModel.addPlayer(infoSave.getHeadPanel().getImage(icon)
											, name , sex);
						infoSave.getHostGame().getChatSystem().table.updateUI();
						infoSave.getMainGame().setSeat(way , name , sex , size);
					}
				}
				//其他玩家进入游戏后，将头像显示在大厅的对应桌子上
				else if (buffer.startsWith(MyProtocol.TABLE_HEAD) && buffer.endsWith(MyProtocol.TABLE_HEAD))
				{
					String tmp = buffer.substring(2 , buffer.length()-2);
					String tableNumber = tmp.substring(0 , tmp.indexOf(":"));
					String location = tmp.substring(tmp.indexOf(":") + 1 , tmp.indexOf(";"));
					String icon = tmp.substring(tmp.indexOf(";") + 1);
					infoSave.getHallPanel().setNumber(tableNumber , location , icon);
				}
				//游戏里面聊天的信息
				else if (buffer.startsWith(MyProtocol.GAME_MSG) && buffer.endsWith(MyProtocol.GAME_MSG))
				{
					String msg = buffer.substring(2 , buffer.length()-2);
					infoSave.getHostGame().getChatSystem().showMes(msg);
				}
				//收到对手退出游戏的信息，清除在大厅上显示的头像（数据）
				else if (buffer.startsWith(MyProtocol.GAME_EXIT) && buffer.endsWith(MyProtocol.GAME_EXIT))
				{
					String table = buffer.substring(2 , buffer.length()-2);
					String tableNumber = table.substring(0 , table.indexOf(":"));
					String way = table.substring(table.indexOf(":") + 1);
					infoSave.getHallPanel().cleanHead(tableNumber , way);
				}
				//收到对手退出的信息
				else if (buffer.equals(MyProtocol.GAMEUSER_EXIT))
				{
					infoSave.getMainGame().setSize("1");
					infoSave.getHostGame().getChatSystem().playersModel.removePlayer(1);//房间玩家存在信息设置
					infoSave.getHostGame().getChatSystem().table.updateUI();
					//消除对手已准备的标记
					infoSave.getMainGame().noReady();
				}
				//收到对手请求悔棋的信息
				else if (buffer.equals(MyProtocol.REGRET_STEP)){
					infoSave.getMainGame().showRegret();
				}
				//收到对方同意悔棋的信息
				else if (buffer.equals(MyProtocol.REGRET_REJECT))
				{
					infoSave.getMainGame().showAgreeRegretStep();
				}
				//收到对方拒绝悔棋的信息
				else if (buffer.equals(MyProtocol.REGRET_AGREE))
				{
					infoSave.getMainGame().showRejectRegretStep();
				}
				//收到对手要求求和的信息
				else if (buffer.equals(MyProtocol.PEACE))
				{
					infoSave.getMainGame().showPeace();
				}
				//收到对手同意求和的信息
				else if (buffer.equals(MyProtocol.PEACE_AGREE))
				{
					infoSave.getMainGame().showAgreePeace();
				}
				//收到对手拒绝求和的信息
				else if (buffer.equals(MyProtocol.PEACE_REJECT))
				{
					infoSave.getMainGame().showRejectPeace();
				}
				//收到对手游戏途中退出的信息
				else if (buffer.startsWith(MyProtocol.PLAYING_EXIT) && buffer.endsWith(MyProtocol.PLAYING_EXIT))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					infoSave.getHallPanel().cleanTable(tableNumber);
				}
				//收到对手下棋的坐标】§2:x,y】§
				else if (buffer.startsWith(MyProtocol.X_Y) && buffer.endsWith(MyProtocol.X_Y))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String way = game.substring(0 , game.indexOf(":"));
					int X = Integer.parseInt(game.substring(game.indexOf(":") + 1 , game.indexOf(",")));
					int Y = Integer.parseInt(game.substring(game.indexOf(",") + 1));
					RX = X;
					RY = Y;
					infoSave.getMainGame().setBegin();
					infoSave.getMainGame().setXY(way , X , Y);//根据对方的方位和坐标在自己这边画出对方下的棋
				}
				//收到对手准备好了
				else if (buffer.equals(MyProtocol.OTHER_READY))
				{
					infoSave.getMainGame().setReady();
				}
				//所有人都准备好可以开始游戏了
				else if (buffer.equals(MyProtocol.ALL_READY))
				{
					if (infoSave.getMainGame().getWay().equals(MyProtocol.LEFT_TABLE))
					{
						infoSave.getMainGame().setPlayChess();
						infoSave.getMainGame().setBegin();
					}
					else
					{
						infoSave.getMainGame().setTimerLeft();
					}
				}
				//收到满桌的消息
				else if (buffer.startsWith(MyProtocol.PLAYING) && buffer.endsWith(MyProtocol.PLAYING))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					infoSave.getHallPanel().setFullTable(tableNumber);
				}
				//收到对手认输的消息
				else if (buffer.startsWith(MyProtocol.LOST) && buffer.endsWith(MyProtocol.LOST))
				{
					String way = buffer.substring(2 , buffer.length()-2);
					infoSave.getMainGame().showLost(way);
				}
				//私聊信息
				else if (buffer.startsWith(MyProtocol.MSG_PRIVATE) && buffer.endsWith(MyProtocol.MSG_PRIVATE))
				{
					String msg = buffer.substring(2 , buffer.length()-2);
					infoSave.getChatSystem().showMes(msg);
					infoSave.getChatSystem().showMesPrivate(msg);
				}
				else
				{		
					infoSave.getChatSystem().showMes(buffer);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
