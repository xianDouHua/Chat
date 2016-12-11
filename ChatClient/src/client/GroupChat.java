package client;

import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * 每次客户端接受消息时，都会先接收一条在线用户列表的更新消息"###UPDATEUSERLIST###"，接收到则接收新的用户列表
 * 用户输入"@用户名B"就会进入与另一用户B的私聊，弹出一个私聊对话框（如果该用户B不存在则发送至群聊）
 * 服务器向用户B发送私聊请求的消息，B的客户端识别请求后弹出私聊对话框
 * */
public class GroupChat {

	private JFrame frame;
	private JTextArea sendMessageText;
	private TextArea receivedMessageText;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private String message;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private JScrollPane scrollPane;
	private TextArea onlineUserText;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GroupChat window = new GroupChat(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GroupChat(Socket socket) {
		this.socket=socket;
		try {
			writer=new PrintWriter(socket.getOutputStream());//输出流
			reader=new BufferedReader(//输入流
					new InputStreamReader(
							socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		initialize();
		Thread t=new Thread(new receiveMessage());
		t.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u804A\u5929\u5BA4");
		frame.setBounds(100, 100, 554, 496);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton sendButton = new JButton("\u53D1\u9001");
		sendButton.setBounds(435, 424, 93, 23);
		frame.getContentPane().add(sendButton);
		
		receivedMessageText = new TextArea();
		receivedMessageText.setBackground(Color.WHITE);
		receivedMessageText.setForeground(Color.BLACK);
		receivedMessageText.setEditable(false);
		receivedMessageText.setBounds(10, 10, 333, 325);
		frame.getContentPane().add(receivedMessageText);
		//receivedMessageText.setCaretColor(Color.LIGHT_GRAY);
		//receivedMessageText.setBorder(new TitledBorder(null, "\u804A\u5929\u5BA4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		receivedMessageText.setName("");
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 347, 516, 77);
		frame.getContentPane().add(scrollPane);
		
		sendMessageText = new JTextArea();
		scrollPane.setViewportView(sendMessageText);
		sendMessageText.setColumns(10);
		
		onlineUserText = new TextArea();
		onlineUserText.setEditable(false);
		onlineUserText.setBounds(349, 10, 179, 325);
		frame.getContentPane().add(onlineUserText);
		
		//发送消息
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				message=sendMessageText.getText();
				sendMessageText.setText("");
				writer.println(message);
				writer.flush();
				//用户输入bye退出
				if(message.equals("bye")){
					try {
						socket.close();
						onlineUserText.setText("");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		frame.setVisible(true);
	}
	
	
	//接收消息
	class receiveMessage implements Runnable{

		@Override
		public void run() {
			while(socket.isConnected()){
				try {
					message=reader.readLine();
					if(message.equals("###UPDATEUSERLIST###")){
						//更新在线用户列表
						onlineUserText.setText("当前在线用户：\n");
						message=reader.readLine();//接收到用户数
						int num=Integer.parseInt(message);
						for(int i=0;i<num;i++){
							message=reader.readLine();
							onlineUserText.append(message+'\n');
						}
					}else{
						receivedMessageText.append(dateFormat.format(new Date())+'\n');
						receivedMessageText.append("   "+message+'\n');
					}
				} catch (IOException e) {
					receivedMessageText.append("***YOU ARE OFFLINE***");
					break;
					//e.printStackTrace();
				}
			}
		}
		
	}
}
