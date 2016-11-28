package client;
//用于输出用户登录或注册时的错误信息
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

public class Notice {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Notice window = new Notice();
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
	public Notice() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 263, 178);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		
		JLabel label = new JLabel("\u7528\u6237\u540D\u6216\u5BC6\u7801\u9519\u8BEF\uFF01");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(10, 61, 227, 15);
		frame.getContentPane().add(label);
	}

}
