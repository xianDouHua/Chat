package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
/**
 *向用户发送在线用户列表,发送前先发送提示消息"###UPDATEUSERLIST###",然后发送用户数，最后发送列表
 */
public class ServerThread extends Thread {
	
	private Socket socket=null;//当前用户的socket
	private List<UserInfo> UserInfoList=null;//存有所有在线用户信息的List
	private PrintWriter writer=null;//输出流，输出消息用writer.println()
	private BufferedReader reader=null;//输入流，接收消息用reader.readLine()
	private Login login;
	String message=null;//存储输出或输入的信息的字符串
	String message1=null;//存储用户名
	String message2=null;//存储密码
	int state=0;
	String userName=null;//存储用户名的字符串
	
	public ServerThread(Socket socket, List<UserInfo> userInfoList) throws IOException {
		super();
		this.socket = socket;
		UserInfoList = userInfoList;
		reader=new BufferedReader(//初始化输入流
				new InputStreamReader(
						socket.getInputStream()));
	}
	
	public void run(){
		try {
			writer=new PrintWriter(socket.getOutputStream());
			//注册或登录
			while(true){
				message=reader.readLine();
				if(message.equals("Register")){
						message1=reader.readLine();
						message2=reader.readLine();
						login=new Login(message1,message2);
						state=login.register();
						if(state==0){
							writer.println("SUCCESS");
							writer.flush();
							break;
						}
						else if(state==-1){
							writer.println("ERROR");
							writer.flush();
						}
				}
				if(message.equals("Login")){
						message1=reader.readLine();
						message2=reader.readLine();
						login=new Login(message1,message2);
						state=login.login();
						if(state==0){
							writer.println("SUCCESS");
							writer.flush();
							break;
						}
						else if(state==-1){
							writer.println("ERROR");
							writer.flush();
						}
				}
			}			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//将该用户加入至UserInfoList
		userName=message1;
		UserInfoList.add(new UserInfo(socket,userName));
		
		//用户成功登录，向客户端发送在线用户列表
		sendUserList();
		
		try {
			//群聊阶段
			while(true){
				message=reader.readLine();
				synchronized(this){
				//向所有在线用户发送消息
					for(int i=0;i<UserInfoList.size();i++){
						writer = new PrintWriter(UserInfoList.get(i).getSocket().getOutputStream());
						writer.println( userName+ " says: " + message);
						writer.flush();
					}
					//用户发送消息“bye”表示退出群聊，从List中删除该用户
					if(message.equals("bye")){
						//synchronized(this){//防止两个线程同时删除
						for(int i=0;i<UserInfoList.size();i++){
							if(UserInfoList.get(i).getUserName().equals(userName)){
								UserInfoList.remove(i);
								break;
							}
						}
						//}
						//用户列表更新后向各客户端发送新列表
						sendUserList();
						socket.close();
						break;
					}
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//一旦该用户上线或下线，向所有客户端发送最新的在线用户列表
	private void sendUserList(){
		try {
			PrintWriter pw=null;
			//String onlineUsers="";//用于存储用户列表
			//for(int i=0;i<UserInfoList.size();i++){
			//	onlineUsers.concat(UserInfoList.get(i).getUserName());
			//	onlineUsers.concat("\n");
			//}
			for(int i=0;i<UserInfoList.size();i++){
				pw=new PrintWriter(UserInfoList.get(i).getSocket().getOutputStream());
				pw.println("###UPDATEUSERLIST###");//发送提示消息
				pw.flush();
				pw.println(UserInfoList.size());//发送用户数
				pw.flush();
				for(int j=0;j<UserInfoList.size();j++){
					pw.println(UserInfoList.get(j).getUserName());
					pw.flush();
				}
				//pw.write(onlineUsers);//发送列表
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}




