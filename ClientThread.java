
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
//������ʾ����
//�������û����в��������������Ӧ�Ĳ�����������������
//�ӿ�
//�ͻ����߳�
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
				//�յ��û��µ�¼����Ϣ
				if (buffer.startsWith(MyProtocol.USER_NAME) && buffer.endsWith(MyProtocol.USER_NAME))
				{
					synchronized(this)
					{
						String userInfo = buffer.substring(2 , buffer.length()-2);
						String userName = userInfo.substring(0 , userInfo.indexOf(":"));
						String userSex = userInfo.substring(userInfo.indexOf(":") + 1 , userInfo.indexOf(";"));
						int userIcon = Integer.parseInt(userInfo.substring(userInfo.indexOf(";") + 1));
						
						Thread.sleep(300);
						//��comboBox��ӳ����Լ�֮�����
						if (!infoSave.getUserName().equals(userName))
						{
							infoSave.getChatSystem().comboBoxAddItem(userName);
						}
						//����������û�
						infoSave.getChatSystem().playersModel.addPlayer(infoSave.getHeadPanel().getImage(userIcon)
																		, userName , userSex);
						//��updateUI���±��
						infoSave.getChatSystem().table.updateUI();
					}
				}
				//�յ��û��˳�����Ϣ-���������Ϣ�����´���
				else if (buffer.startsWith(MyProtocol.USER_EXIT) && buffer.endsWith(MyProtocol.USER_EXIT))
				{
					String userName = buffer.substring(2 , buffer.length()-2);
					infoSave.getChatSystem().comboBoxRemoveItem(userName);
					infoSave.getChatSystem().playersModel.removePlayer(userName);
					infoSave.getChatSystem().table.updateUI();
				}
				//��ҵ�����ӽ�����Ϸ����Ϣ��������������������������������
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
				//������ҽ�����Ϸ�󣬽�ͷ����ʾ�ڴ����Ķ�Ӧ������
				else if (buffer.startsWith(MyProtocol.TABLE_HEAD) && buffer.endsWith(MyProtocol.TABLE_HEAD))
				{
					String tmp = buffer.substring(2 , buffer.length()-2);
					String tableNumber = tmp.substring(0 , tmp.indexOf(":"));
					String location = tmp.substring(tmp.indexOf(":") + 1 , tmp.indexOf(";"));
					String icon = tmp.substring(tmp.indexOf(";") + 1);
					infoSave.getHallPanel().setNumber(tableNumber , location , icon);
				}
				//��Ϸ�����������Ϣ
				else if (buffer.startsWith(MyProtocol.GAME_MSG) && buffer.endsWith(MyProtocol.GAME_MSG))
				{
					String msg = buffer.substring(2 , buffer.length()-2);
					infoSave.getHostGame().getChatSystem().showMes(msg);
				}
				//�յ������˳���Ϸ����Ϣ������ڴ�������ʾ��ͷ�����ݣ�
				else if (buffer.startsWith(MyProtocol.GAME_EXIT) && buffer.endsWith(MyProtocol.GAME_EXIT))
				{
					String table = buffer.substring(2 , buffer.length()-2);
					String tableNumber = table.substring(0 , table.indexOf(":"));
					String way = table.substring(table.indexOf(":") + 1);
					infoSave.getHallPanel().cleanHead(tableNumber , way);
				}
				//�յ������˳�����Ϣ
				else if (buffer.equals(MyProtocol.GAMEUSER_EXIT))
				{
					infoSave.getMainGame().setSize("1");
					infoSave.getHostGame().getChatSystem().playersModel.removePlayer(1);//������Ҵ�����Ϣ����
					infoSave.getHostGame().getChatSystem().table.updateUI();
					//����������׼���ı��
					infoSave.getMainGame().noReady();
				}
				//�յ���������������Ϣ
				else if (buffer.equals(MyProtocol.REGRET_STEP)){
					infoSave.getMainGame().showRegret();
				}
				//�յ��Է�ͬ��������Ϣ
				else if (buffer.equals(MyProtocol.REGRET_REJECT))
				{
					infoSave.getMainGame().showAgreeRegretStep();
				}
				//�յ��Է��ܾ��������Ϣ
				else if (buffer.equals(MyProtocol.REGRET_AGREE))
				{
					infoSave.getMainGame().showRejectRegretStep();
				}
				//�յ�����Ҫ����͵���Ϣ
				else if (buffer.equals(MyProtocol.PEACE))
				{
					infoSave.getMainGame().showPeace();
				}
				//�յ�����ͬ����͵���Ϣ
				else if (buffer.equals(MyProtocol.PEACE_AGREE))
				{
					infoSave.getMainGame().showAgreePeace();
				}
				//�յ����־ܾ���͵���Ϣ
				else if (buffer.equals(MyProtocol.PEACE_REJECT))
				{
					infoSave.getMainGame().showRejectPeace();
				}
				//�յ�������Ϸ;���˳�����Ϣ
				else if (buffer.startsWith(MyProtocol.PLAYING_EXIT) && buffer.endsWith(MyProtocol.PLAYING_EXIT))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					infoSave.getHallPanel().cleanTable(tableNumber);
				}
				//�յ�������������꡿��2:x,y����
				else if (buffer.startsWith(MyProtocol.X_Y) && buffer.endsWith(MyProtocol.X_Y))
				{
					String game = buffer.substring(2 , buffer.length()-2);
					String way = game.substring(0 , game.indexOf(":"));
					int X = Integer.parseInt(game.substring(game.indexOf(":") + 1 , game.indexOf(",")));
					int Y = Integer.parseInt(game.substring(game.indexOf(",") + 1));
					RX = X;
					RY = Y;
					infoSave.getMainGame().setBegin();
					infoSave.getMainGame().setXY(way , X , Y);//���ݶԷ��ķ�λ���������Լ���߻����Է��µ���
				}
				//�յ�����׼������
				else if (buffer.equals(MyProtocol.OTHER_READY))
				{
					infoSave.getMainGame().setReady();
				}
				//�����˶�׼���ÿ��Կ�ʼ��Ϸ��
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
				//�յ���������Ϣ
				else if (buffer.startsWith(MyProtocol.PLAYING) && buffer.endsWith(MyProtocol.PLAYING))
				{
					String tableNumber = buffer.substring(2 , buffer.length()-2);
					infoSave.getHallPanel().setFullTable(tableNumber);
				}
				//�յ������������Ϣ
				else if (buffer.startsWith(MyProtocol.LOST) && buffer.endsWith(MyProtocol.LOST))
				{
					String way = buffer.substring(2 , buffer.length()-2);
					infoSave.getMainGame().showLost(way);
				}
				//˽����Ϣ
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
