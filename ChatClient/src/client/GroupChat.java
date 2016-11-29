package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JButton;

public class GroupChat {

	private JFrame frame;
	private JTextField sendMessageText;
	private JTextArea receivedMessageText;
	private Socket socket;
	PrintWriter writer;
	BufferedReader reader;
	String message;

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
		frame.setBounds(100, 100, 554, 496);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		receivedMessageText = new JTextArea();
		receivedMessageText.setCaretColor(Color.LIGHT_GRAY);
		receivedMessageText.setBorder(new TitledBorder(null, "\u804A\u5929\u5BA4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		receivedMessageText.setName("");
		receivedMessageText.setEditable(false);
		receivedMessageText.setBounds(10, 0, 518, 309);
		frame.getContentPane().add(receivedMessageText);
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setBounds(511, 10, 17, 299);
		frame.getContentPane().add(scrollBar);
		
		sendMessageText = new JTextField();
		sendMessageText.setBounds(10, 321, 518, 99);
		frame.getContentPane().add(sendMessageText);
		sendMessageText.setColumns(10);
		
		JButton sendButton = new JButton("\u53D1\u9001");
		sendButton.setBounds(435, 424, 93, 23);
		frame.getContentPane().add(sendButton);
		frame.setVisible(true);
		
		//发送消息
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				message=sendMessageText.getText();
				sendMessageText.setText("");
				writer.println(message);
				writer.flush();
			}
		});
	}
	
	
	
	class receiveMessage implements Runnable{

		@Override
		public void run() {
			while(true){
				try {
					message=reader.readLine();
					receivedMessageText.append(message+'\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
