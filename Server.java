
import java.util.*;
import java.io.*;
import java.net.*;
//������ʹ��
public class Server
{
	private final int SERVER_PORT = 49000;
	public static MyMap<String , Socket> clients = new MyMap<String , Socket>();
	//�����Ϸ������ҽ������������Ϣ
	public static Map<String , Hashtable> table = new Hashtable<String , Hashtable>();
	//�����Ϸ������ҵ���Ϣ
	public static Map<String , Hashtable> playerLogin = new Hashtable<String , Hashtable>();
	//�����Ϸ������ҵ�׼��״̬����Ϣ
	public static Map<String , Hashtable> playerReady = new Hashtable<String , Hashtable>();

	public void init()throws Exception
	{
		ServerSocket ss = new ServerSocket(SERVER_PORT);
		Socket socket = null;
		while (true)
		{
			socket = ss.accept();
			new ServerThread(socket).start();
		}
	}
    public static void main(String[] args) throws Exception
    {
		Server server = new Server();
		server.init();
    }
}
