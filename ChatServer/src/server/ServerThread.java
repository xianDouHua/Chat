package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
/**
 *���û����������û��б�,����ǰ�ȷ�����ʾ��Ϣ"###UPDATEUSERLIST###",Ȼ�����û�����������б�
 */
public class ServerThread extends Thread {
	
	private Socket socket=null;//��ǰ�û���socket
	private List<UserInfo> UserInfoList=null;//�������������û���Ϣ��List
	private PrintWriter writer=null;//������������Ϣ��writer.println()
	private BufferedReader reader=null;//��������������Ϣ��reader.readLine()
	private Login login;
	String message=null;//�洢������������Ϣ���ַ���
	String message1=null;//�洢�û���
	String message2=null;//�洢����
	int state=0;
	String userName=null;//�洢�û������ַ���
	
	public ServerThread(Socket socket, List<UserInfo> userInfoList) throws IOException {
		super();
		this.socket = socket;
		UserInfoList = userInfoList;
		reader=new BufferedReader(//��ʼ��������
				new InputStreamReader(
						socket.getInputStream()));
	}
	
	public void run(){
		try {
			writer=new PrintWriter(socket.getOutputStream());
			//ע����¼
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
		//�����û�������UserInfoList
		userName=message1;
		UserInfoList.add(new UserInfo(socket,userName));
		
		//�û��ɹ���¼����ͻ��˷��������û��б�
		sendUserList();
		
		try {
			//Ⱥ�Ľ׶�
			while(true){
				message=reader.readLine();
				synchronized(this){
				//�����������û�������Ϣ
					for(int i=0;i<UserInfoList.size();i++){
						writer = new PrintWriter(UserInfoList.get(i).getSocket().getOutputStream());
						writer.println( userName+ " says: " + message);
						writer.flush();
					}
					//�û�������Ϣ��bye����ʾ�˳�Ⱥ�ģ���List��ɾ�����û�
					if(message.equals("bye")){
						//synchronized(this){//��ֹ�����߳�ͬʱɾ��
						for(int i=0;i<UserInfoList.size();i++){
							if(UserInfoList.get(i).getUserName().equals(userName)){
								UserInfoList.remove(i);
								break;
							}
						}
						//}
						//�û��б���º�����ͻ��˷������б�
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
	
	//һ�����û����߻����ߣ������пͻ��˷������µ������û��б�
	private void sendUserList(){
		try {
			PrintWriter pw=null;
			//String onlineUsers="";//���ڴ洢�û��б�
			//for(int i=0;i<UserInfoList.size();i++){
			//	onlineUsers.concat(UserInfoList.get(i).getUserName());
			//	onlineUsers.concat("\n");
			//}
			for(int i=0;i<UserInfoList.size();i++){
				pw=new PrintWriter(UserInfoList.get(i).getSocket().getOutputStream());
				pw.println("###UPDATEUSERLIST###");//������ʾ��Ϣ
				pw.flush();
				pw.println(UserInfoList.size());//�����û���
				pw.flush();
				for(int j=0;j<UserInfoList.size();j++){
					pw.println(UserInfoList.get(j).getUserName());
					pw.flush();
				}
				//pw.write(onlineUsers);//�����б�
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}




