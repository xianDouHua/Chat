package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.util.regex.Pattern;
 
public class PrivateChat {
	//�������Ļ������
    JFrame f = new JFrame("BuptChat");
    JLabel lbRemoteIP = new JLabel("Ŀ��IP");
    JLabel lbRemotePort = new JLabel("Ŀ��˿�");
    JLabel lbLocalSendPort = new JLabel("���ط��Ͷ˿�");
    JLabel lbLocalReceivePort = new JLabel("���ؽ��ն˿�");
    JTextField tfRemoteIP = new JTextField(15);
    JTextField tfRemotePort = new JTextField(15);
    JTextField tfLocalSendPort = new JTextField(15);
    JTextField tfLocalReceivePort = new JTextField(15);
    TextArea allChatContent = new TextArea();
    JTextField sendChatContent = new JTextField(27);
    JButton connect = new JButton("����");
    JButton disConnect = new JButton("�Ͽ�");
    JButton bt = new JButton("Send");
 
    //��������
    String remoteIP = null;
    int remotePort = 0;
    int localSendPort = 0;
    int localReceivePort = 0;
    Thread receiveThread = null;
 
    public PrivateChat() {
    	initFrame();
    }

    //�����������UI
    public void initFrame() {
    	f.setSize(400, 500);
    	f.setLayout(new BorderLayout());
    	JPanel p1 = new JPanel();
    	JPanel p2 = new JPanel();
        // �����������ò���
        p1.setLayout(new GridLayout(5, 5));
        p1.add(lbRemoteIP);
        p1.add(tfRemoteIP);
        p1.add(lbRemotePort);
        p1.add(tfRemotePort);
        p1.add(lbLocalSendPort);
        p1.add(tfLocalSendPort);
        p1.add(lbLocalReceivePort);
        p1.add(tfLocalReceivePort);
        p1.add(connect);
        p1.add(disConnect);
        f.add("North", p1);   
        f.add("Center", allChatContent);
        p2.setLayout(new FlowLayout());
        p2.add(sendChatContent);
        p2.add(bt);
        f.add("South", p2);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		f.setVisible(false);
                f.dispose();
                System.exit(0);
                }
        	});
        
        connect.addActionListener(new ActionListener() {      
        	public void actionPerformed(ActionEvent e) {
        		try{
        			remoteIP = tfRemoteIP.getText();
        			remotePort = Integer.parseInt(tfRemotePort.getText());
                    localSendPort = Integer.parseInt(tfLocalSendPort.getText());
                    localReceivePort = Integer.parseInt(tfLocalReceivePort.getText());
                    }catch (Exception exception) {
                    	prompt("������Ϣ����Ϊ�ջ��ʽ����");
                        return;
                        }
        		if (!checkIP(remoteIP)) {
        			prompt("Ŀ��IP���ô���");
                    return;
                    }
                if (!checkPort(remotePort)) {
                    prompt("Ŀ��˿����ô���");
                    return;
                    }
                if (!checkPort(localSendPort)) {
                    prompt("���ط��Ͷ˿����ô���");
                    return;
                    }
                if (!checkPort(localReceivePort)) {
                    prompt("���ؽ��ն˿����ô���");
                    return;
                    }
                prompt("���ӳɹ�");
                tfRemoteIP.setEditable(false);
                tfRemotePort.setEditable(false);
                tfLocalReceivePort.setEditable(false);
                tfLocalSendPort.setEditable(false);
                receiveMessage();
            }
        });
 
        disConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	tfRemoteIP.setEditable(true);
                tfRemotePort.setEditable(true);
                tfLocalReceivePort.setEditable(true);
                tfLocalSendPort.setEditable(true);
                tfLocalReceivePort.setText("");
                tfLocalSendPort.setText("");
                tfRemoteIP.setText("");
                tfRemotePort.setText("");
 
                remoteIP = null;
                remotePort = 0;
                localSendPort = 0;
                localReceivePort = 0;
                receiveThread.stop();
                prompt("�Ͽ��ɹ�");
            }
            });

       //�������Ͱ�ť�¼�
       bt.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent e) {
    		   sendMessage();
    		   }
    	   });

 
       // �����ı���Ĵ����¼�    
       sendChatContent.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent e) {
    		   sendMessage();
               }
           });
       }
 
    // ������Ϣ
    public void sendMessage() { 
    	DatagramSocket ds = null;
    	try {
    		ds = new DatagramSocket(localSendPort);
    		} catch (SocketException e1) {
    			prompt("���ط��Ͷ˿��ѱ�ʹ��");
                return;
                }
    //���÷�����Ϣ
       String sendMessage = sendChatContent.getText().trim();
       if (sendMessage.equals("")) {
            prompt("���͵���Ϣ����Ϊ�գ�");
            ds.close();
            return;
            }
       byte[] buf = sendMessage.getBytes();//���Ҫ���͵�����
 
    //���ý������ݵ�Զ��IP��ַ
       InetAddress inetAddress = null;
       try {
            inetAddress = InetAddress.getByName(tfRemoteIP.getText().trim());
           } catch (UnknownHostException e) {
                prompt("�Ƿ�Զ��IP��ַ");
                return;
                }
 
    //����
       DatagramPacket dp = new DatagramPacket(buf, 0, buf.length, inetAddress,remotePort);
       try {
    	   ds.send(dp);
           } catch (IOException e) {
               prompt("������ϣ�����ʧ��");
               return;
               }
       sendChatContent.setText("");
       allChatContent.append("[  "+dp.getAddress()+" - "+dp.getPort()+"  ]"+" ������ "+new java.text.SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date())+"\n" );
       allChatContent.append(sendMessage + "\n");
       ds.close();
       }
 
    //���� ���ܶԷ�IP����Ϣ ��״̬
    class MyRunnable implements Runnable {
    	byte buf[] = new byte[1024];
        DatagramSocket ds = null;
        DatagramPacket dp = null;
        public void run() {
        	dp = new DatagramPacket(buf, 0, 1024);
        	try {
        		ds = new DatagramSocket(localReceivePort);
        		} catch (SocketException e1) {
        			prompt("���ؽ��ն˿��ѱ�ʹ��");
                    return;
                    }
        	while (true) {
        		try {
        			ds.receive(dp);
        			} catch (IOException e) {
        				ds.close();
                        e.printStackTrace();
                        }
                String receiveMessage = new String(dp.getData(), 0, dp.getLength());
                allChatContent.append("[  "+dp.getAddress()+" - "+dp.getPort()+  "]"+" ������ "+new java.text.SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date())+"\n" );
                allChatContent.append(receiveMessage+"\n");
                sendChatContent.setCaretPosition(sendChatContent.getText().length());
                }
        	}
        }
 
    //������Ϣ
    public void receiveMessage() {
    	receiveThread = new Thread(new MyRunnable());
        receiveThread.start();
        }

    //��������
    public void prompt(String promptMessage) {
    	JOptionPane.showConfirmDialog(null, promptMessage, "������ʾ",JOptionPane.WARNING_MESSAGE);
    }
 
    //���˿�
    public boolean checkPort(int port) {
    	return String.valueOf(port).matches("\\d+") && port > 1024 && port <= 65535;
    }
 
    //���˿�
    public boolean checkPort(String port) {
    return port.matches("\\d+") && Integer.parseInt(port) > 1024 && Integer.parseInt(port) <= 65535;
    }
 
    //���IP��ַ�Ƿ�Ϸ�
    public boolean checkIP(String ip) {
    	java.util.regex.Matcher m = Pattern.compile("(\\d{1,3}).(\\d{1,3}).(\\d{1,3}).(\\d{1,3})").matcher(ip);
    	if (m.find()) {
    		for (int i = 1; i <= 4; i++)
    			if (Integer.parseInt(m.group(i)) < 0 || Integer.parseInt(m.group(i)) > 255)
    				return false;
    		return true;
    		}
    	return true;
    	}
    
    public static void main(String args[]) {
    	new PrivateChat();
    }
    
    
    }