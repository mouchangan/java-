
import java.net.*;
import java.io.*;
import javax.swing.*;

import static java.lang.Thread.sleep;
//ʵ��������
//��������
//��¼������Ϣ����
//����
//�ͻ���
public class Client 
{
	private InfoSave infoSave;
	private Socket socket;
	private String userName;
	private static int  keepTrying=0 ;
	private int count;
	public int setInfo(String name , String ip , int port , String sex , int icon , InfoSave infoSave,int count) throws InterruptedException {

		try {
			this.keepTrying=0;
			this.infoSave = infoSave;
			this.socket = new Socket(ip, port);
			this.count = count;

			this.infoSave.setSocket(socket);

			PrintStream ps = new PrintStream(this.socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			ps.println(MyProtocol.USER_ROUND + name + ":" + sex + ";" + icon + MyProtocol.USER_ROUND);
			userName = br.readLine();
			if (userName.equals(MyProtocol.NAME_REP)) {
				JOptionPane.showMessageDialog(null, "���û��Ѵ��ڣ�����������", "����", JOptionPane.ERROR_MESSAGE);
				return 0;
			}
			this.infoSave.getLoginDialog().setVisible(false);//��¼���½������
			this.infoSave.getGameHall().init(this.infoSave);//������ʾ
			sleep(300);
			ClientThread clientThread = new ClientThread(this.infoSave);
			clientThread.setDaemon(true);
			//----------ȷ�������������ҵ�tableModel�����Ѿ�ʵ�������������߳�----------//

			clientThread.start();
		} catch (Exception e) {

//				JOptionPane.showMessageDialog(null, "���ӷ�����ʧ�ܣ���������ȷ�������������ӣ�������", "����", JOptionPane.ERROR_MESSAGE);

			int i=JOptionPane.showConfirmDialog(null, "���ӷ�����ʧ�ܣ��������ٴγ�����������"+(count--)+"��"+"\t\n�Ƿ�����������",
					"����!", JOptionPane.YES_NO_OPTION);

			if(i ==JOptionPane.NO_OPTION){
				keepTrying=0;
			}else{
				keepTrying=1;
			}

		}
		return keepTrying;
	}

}
