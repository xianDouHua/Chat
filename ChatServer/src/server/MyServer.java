package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.ServerThread;
import server.UserInfo;

/*��������������
 * �˿ںţ�5555*/

public class MyServer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(5555);//��ʼ��������socket,�˿ں�5555
		List<UserInfo> UserInfoList = new ArrayList<UserInfo>();//���ڴ洢��ǰ�����û���Socket���û���
		System.out.println("***The server is running***");
		
		//ѭ�������ͻ��ˣ���������һֱ��������
		while(true){
			//�������ȴ��ͻ��˵�����
			Socket socket=serverSocket.accept();
			ServerThread chatServerThread=new ServerThread(socket,UserInfoList);
			chatServerThread.start();
		}
	}

}
