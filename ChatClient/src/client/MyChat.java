package client;
//客户端开始处，选择私聊或群聊
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class MyChat {

	private JFrame frameChat;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyChat window = new MyChat();
					window.frameChat.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MyChat() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameChat = new JFrame();
		frameChat.setTitle("Chat");
		frameChat.setBounds(100, 100, 450, 300);
		frameChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameChat.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("\u8BF7\u9009\u62E9\u60A8\u8981\u8FDB\u884C\u7684\u64CD\u4F5C");
		label.setBounds(149, 56, 134, 15);
		frameChat.getContentPane().add(label);
		
		JButton privateChatButton = new JButton("\u79C1\u804A");
		privateChatButton.setBounds(67, 122, 93, 23);
		frameChat.getContentPane().add(privateChatButton);
		
		JButton groupChatButton = new JButton("\u7FA4\u804A");
		groupChatButton.setBounds(274, 122, 93, 23);
		frameChat.getContentPane().add(groupChatButton);
		
		privateChatButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				frameChat.dispose();
				new PrivateChat();				
			}			
		});
		
		groupChatButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				frameChat.dispose();
				try {
					new GroupChatLogin();
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
			}			
		});
	}
}
