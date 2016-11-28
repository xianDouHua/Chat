package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class GroupChatLogin {

	private JFrame frame;
	private JTextField userNameText;
	private JPasswordField passwordText;

	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GroupChatLogin window = new GroupChatLogin();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public GroupChatLogin() throws UnknownHostException, IOException {		
		initialize();
	}
	

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void initialize() throws UnknownHostException, IOException {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		
		//��ʼ���ͻ���socket
		Socket socket=new Socket("localhost",5555);
		PrintWriter writer=new PrintWriter(socket.getOutputStream());//�����
		BufferedReader reader=new BufferedReader(//������
				new InputStreamReader(
						socket.getInputStream()));
		
		
		
		JLabel userNameLabel = new JLabel("\u7528\u6237\u540D\uFF1A");
		userNameLabel.setBounds(53, 77, 54, 15);
		frame.getContentPane().add(userNameLabel);
		
		JLabel passwordLabel = new JLabel("\u5BC6\u7801  \uFF1A");
		passwordLabel.setBounds(53, 110, 54, 15);
		frame.getContentPane().add(passwordLabel);
		
		userNameText = new JTextField();
		userNameText.setBounds(117, 74, 246, 21);
		frame.getContentPane().add(userNameText);
		userNameText.setColumns(10);
		
		passwordText = new JPasswordField();
		passwordText.setBounds(117, 107, 246, 21);
		frame.getContentPane().add(passwordText);
		
		JButton loginButton = new JButton("\u767B\u5F55");
		loginButton.setBounds(53, 181, 93, 23);
		frame.getContentPane().add(loginButton);
		
		JButton cancelButton = new JButton("\u9000\u51FA");
		cancelButton.setBounds(287, 181, 93, 23);
		frame.getContentPane().add(cancelButton);
		
		JButton registerButton = new JButton("\u6CE8\u518C");
		registerButton.setBounds(172, 181, 93, 23);
		frame.getContentPane().add(registerButton);
		
		//��¼�����ܵ�ERROR����������
		loginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				writer.println("Login");
				writer.flush();
				writer.println(userNameText.getText());
				writer.flush();				
				writer.println(passwordText.getPassword().toString());
				writer.flush();
				try {
					String mes=reader.readLine();
					if(mes.equals("ERROR")){
						new Notice();
					}else if(mes.equals("SUCCESS")){
						frame.dispose();
						new GroupChat();
					}else{
						userNameText.setText(mes);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
			}			
		});
		
		//ע��
		registerButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				writer.println(userNameText.getText());
				frame.dispose();
				new GroupChat();				
			}			
		});
		
		//�˳�
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				return;				
			}			
		});
	}
}
