package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Login {

	private String username=null;
	private String password=null;
	
	public Login(){
		this.username="a";
		this.password="1";
	}
	public Login(String username,String password){
		this.username=username;
		this.password=password;
	}
	
	public int register(){
		FileWriter writer;
		File file=new File("UserInfo.dat");
		BufferedReader reader=null;
		String userinfo="read";
		try {
			reader=new BufferedReader(new FileReader(file));
			while(userinfo!=null){
				userinfo=reader.readLine();
				if(userinfo==username+password){
					reader.close();
					return -1;
				}
			}
			reader.close();
			writer=new FileWriter(file);
			writer.write(username);
			writer.flush();
			writer.write(password+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int login(){
		File file=new File("UserInfo.dat");
		BufferedReader reader=null;
		String userinfo="read";
		try {
			reader=new BufferedReader(new FileReader(file));
			while(userinfo!=null){
				try {
					userinfo=reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(userinfo==username+password){
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return 0;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
