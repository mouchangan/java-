
import java.net.*;
import java.io.*;
import javax.swing.*;

import static java.lang.Thread.sleep;
//实例化对象
//弹窗警告
//登录窗口信息输入
//弹窗
//客户端
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
				JOptionPane.showMessageDialog(null, "此用户已存在，请重新输入", "警告", JOptionPane.ERROR_MESSAGE);
				return 0;
			}
			this.infoSave.getLoginDialog().setVisible(false);//登录框登陆后隐藏
			this.infoSave.getGameHall().init(this.infoSave);//大厅显示
			sleep(300);
			ClientThread clientThread = new ClientThread(this.infoSave);
			clientThread.setDaemon(true);
			//----------确保大厅和聊天室的tableModel对象已经实例化了再运行线程----------//

			clientThread.start();
		} catch (Exception e) {

//				JOptionPane.showMessageDialog(null, "连接服务器失败！！！请点击确定尝试重新连接！！！！", "警告", JOptionPane.ERROR_MESSAGE);

			int i=JOptionPane.showConfirmDialog(null, "连接服务器失败！！！将再次尝试重新连接"+(count--)+"次"+"\t\n是否尝试重新连接",
					"警告!", JOptionPane.YES_NO_OPTION);

			if(i ==JOptionPane.NO_OPTION){
				keepTrying=0;
			}else{
				keepTrying=1;
			}

		}
		return keepTrying;
	}

}
