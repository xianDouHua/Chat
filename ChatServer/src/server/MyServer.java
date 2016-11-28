package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.ServerThread;
import server.UserInfo;

/*聊天程序服务器端
 * 端口号：5555*/

public class MyServer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(5555);//初始化服务器socket,端口号5555
		List<UserInfo> UserInfoList = new ArrayList<UserInfo>();//用于存储当前在线用户的Socket和用户名
		System.out.println("***The server is running***");
		
		//循环监听客户端，服务器将一直保持运行
		while(true){
			//监听，等待客户端的连接
			Socket socket=serverSocket.accept();
			ServerThread chatServerThread=new ServerThread(socket,UserInfoList);
			chatServerThread.start();
		}
	}

}
