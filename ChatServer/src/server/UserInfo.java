package server;

import java.net.Socket;

/*
 * �������ڽ�Socket��������Ӧ�û���������Ա����List��
 */

public class UserInfo {
	private Socket socket=null;
	private String userName=null;
	
	
	public UserInfo(Socket socket,String userName){
		this.socket=socket;
		this.userName=userName;
	}
	
	public Socket getSocket(){
		return socket;
	}
	
	public String getUserName(){
		return userName;
	}
}
