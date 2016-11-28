package server;

import java.net.Socket;

/*
 * 此类用于将Socket对象与相应用户名打包，以便放入List。
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
