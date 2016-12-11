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
 * ÿ�οͻ��˽�����Ϣʱ�������Ƚ���һ�������û��б�ĸ�����Ϣ"###UPDATEUSERLIST###"�����յ�������µ��û��б�
 * �û�����"@�û���B"�ͻ��������һ�û�B��˽�ģ�����һ��˽�ĶԻ���������û�B������������Ⱥ�ģ�
 * ���������û�B����˽���������Ϣ��B�Ŀͻ���ʶ������󵯳�˽�ĶԻ���
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
			writer=new PrintWriter(socket.getOutputStream());//�����
			reader=new BufferedReader(//������
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
		
		//������Ϣ
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				message=sendMessageText.getText();
				sendMessageText.setText("");
				writer.println(message);
				writer.flush();
				//�û�����bye�˳�
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
	
	
	//������Ϣ
	class receiveMessage implements Runnable{

		@Override
		public void run() {
			while(socket.isConnected()){
				try {
					message=reader.readLine();
					if(message.equals("###UPDATEUSERLIST###")){
						//���������û��б�
						onlineUserText.setText("��ǰ�����û���\n");
						message=reader.readLine();//���յ��û���
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
