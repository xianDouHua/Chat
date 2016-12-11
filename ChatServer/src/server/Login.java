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
		String name="read";
		String code="read";
		try {
			reader=new BufferedReader(new FileReader(file));
			while(name!=null&&code!=null){
				name=reader.readLine();
				code=reader.readLine();
				if(name!=null&&code!=null){
					if(name.equals(username)){
						reader.close();
						return -1;
					}
				}
				else 
					break;
			}
			reader.close();
			writer=new FileWriter(file,true);
			writer.write(username+"\n"+password+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int login() throws IOException{
		File file=new File("UserInfo.dat");
		BufferedReader reader=null;
		String name="read";
		String code="read";
		try {
			reader=new BufferedReader(new FileReader(file));
			while(name!=null&&code!=null){
				name=reader.readLine();
				code=reader.readLine();
				if(name!=null&&code!=null){
					if(name.equals(username)&&code.equals(password)){
						reader.close();
						return 0;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
