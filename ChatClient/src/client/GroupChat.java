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
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

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
	private int line=0;

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
		receivedMessageText.setBounds(10, 10, 516, 325);
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
					line+=2;
					//if(line>=16){
					//	receivedMessageText.setText("");
					//	line=0;
					//}
					receivedMessageText.append(dateFormat.format(new Date())+'\n');
					receivedMessageText.append("   "+message+'\n');
				} catch (IOException e) {
					receivedMessageText.append("***YOU ARE OFFLINE***");
					break;
					//e.printStackTrace();
				}
			}
		}
		
	}
}
