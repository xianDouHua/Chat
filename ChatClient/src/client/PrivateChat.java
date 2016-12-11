package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.util.regex.Pattern;
 
public class PrivateChat {
	//聊天界面的基本组件
    JFrame f = new JFrame("BuptChat");
    JLabel lbRemoteIP = new JLabel("目标IP");
    JLabel lbRemotePort = new JLabel("目标端口");
    JLabel lbLocalSendPort = new JLabel("本地发送端口");
    JLabel lbLocalReceivePort = new JLabel("本地接收端口");
    JTextField tfRemoteIP = new JTextField(15);
    JTextField tfRemotePort = new JTextField(15);
    JTextField tfLocalSendPort = new JTextField(15);
    JTextField tfLocalReceivePort = new JTextField(15);
    TextArea allChatContent = new TextArea();
    JTextField sendChatContent = new JTextField(27);
    JButton connect = new JButton("连接");
    JButton disConnect = new JButton("断开");
    JButton bt = new JButton("Send");
 
    //基本数据
    String remoteIP = null;
    int remotePort = 0;
    int localSendPort = 0;
    int localReceivePort = 0;
    Thread receiveThread = null;
 
    public PrivateChat() {
    	initFrame();
    }

    //创建聊天界面UI
    public void initFrame() {
    	f.setSize(400, 500);
    	f.setLayout(new BorderLayout());
    	JPanel p1 = new JPanel();
    	JPanel p2 = new JPanel();
        // 添加组件，布置布局
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
                    	prompt("连接信息不能为空或格式错误");
                        return;
                        }
        		if (!checkIP(remoteIP)) {
        			prompt("目标IP设置错误");
                    return;
                    }
                if (!checkPort(remotePort)) {
                    prompt("目标端口设置错误");
                    return;
                    }
                if (!checkPort(localSendPort)) {
                    prompt("本地发送端口设置错误");
                    return;
                    }
                if (!checkPort(localReceivePort)) {
                    prompt("本地接收端口设置错误");
                    return;
                    }
                prompt("连接成功");
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
                prompt("断开成功");
            }
            });

       //触发发送按钮事件
       bt.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent e) {
    		   sendMessage();
    		   }
    	   });

 
       // 输入文本框的触发事件    
       sendChatContent.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent e) {
    		   sendMessage();
               }
           });
       }
 
    // 发送消息
    public void sendMessage() { 
    	DatagramSocket ds = null;
    	try {
    		ds = new DatagramSocket(localSendPort);
    		} catch (SocketException e1) {
    			prompt("本地发送端口已被使用");
                return;
                }
    //设置发送信息
       String sendMessage = sendChatContent.getText().trim();
       if (sendMessage.equals("")) {
            prompt("发送的消息不能为空！");
            ds.close();
            return;
            }
       byte[] buf = sendMessage.getBytes();//获得要发送的数据
 
    //设置接收数据的远程IP地址
       InetAddress inetAddress = null;
       try {
            inetAddress = InetAddress.getByName(tfRemoteIP.getText().trim());
           } catch (UnknownHostException e) {
                prompt("非法远程IP地址");
                return;
                }
 
    //发送
       DatagramPacket dp = new DatagramPacket(buf, 0, buf.length, inetAddress,remotePort);
       try {
    	   ds.send(dp);
           } catch (IOException e) {
               prompt("网络故障，发送失败");
               return;
               }
       sendChatContent.setText("");
       allChatContent.append("[  "+dp.getAddress()+" - "+dp.getPort()+"  ]"+" 发送于 "+new java.text.SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date())+"\n" );
       allChatContent.append(sendMessage + "\n");
       ds.close();
       }
 
    //保持 接受对方IP的消息 的状态
    class MyRunnable implements Runnable {
    	byte buf[] = new byte[1024];
        DatagramSocket ds = null;
        DatagramPacket dp = null;
        public void run() {
        	dp = new DatagramPacket(buf, 0, 1024);
        	try {
        		ds = new DatagramSocket(localReceivePort);
        		} catch (SocketException e1) {
        			prompt("本地接收端口已被使用");
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
                allChatContent.append("[  "+dp.getAddress()+" - "+dp.getPort()+  "]"+" 接收于 "+new java.text.SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date())+"\n" );
                allChatContent.append(receiveMessage+"\n");
                sendChatContent.setCaretPosition(sendChatContent.getText().length());
                }
        	}
        }
 
    //接收消息
    public void receiveMessage() {
    	receiveThread = new Thread(new MyRunnable());
        receiveThread.start();
        }

    //弹窗界面
    public void prompt(String promptMessage) {
    	JOptionPane.showConfirmDialog(null, promptMessage, "友情提示",JOptionPane.WARNING_MESSAGE);
    }
 
    //检查端口
    public boolean checkPort(int port) {
    	return String.valueOf(port).matches("\\d+") && port > 1024 && port <= 65535;
    }
 
    //检查端口
    public boolean checkPort(String port) {
    return port.matches("\\d+") && Integer.parseInt(port) > 1024 && Integer.parseInt(port) <= 65535;
    }
 
    //检查IP地址是否合法
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