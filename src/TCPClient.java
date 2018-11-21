import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class TCPClient extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TextArea taContent = new TextArea();
	JTextField tfTxt = new JTextField(20);
	
	JButton send = new JButton("发送");
	JButton connect = new JButton("连接");
	JButton clear = new JButton("清空");
	JButton signIn=new JButton("注册");
	
	boolean live = false;
	JPanel p1 = new JPanel();
	JPanel p2 = new JPanel();
	
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	
	boolean bConnected = false;
	
	
	
	Tool start=new Tool();//工具类
	
	
	Thread t = new Thread(new RecToServer());
	
	public void launchFrame() {
		
		taContent.setEditable(false);
		tfTxt.setEditable(false);
		p2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));//流式布局，制定对齐方式,和水平及垂直的间隙低2个参数为水平间隙，第3个为垂直间隙
		p2.add(send);
		p2.add(connect);
		p2.add(clear);
	    p2.add(signIn);
		
		Container con = this.getContentPane();
		
		con.add(taContent, "North");
		con.add(tfTxt, "Center");
		con.add(p2, "South");
		
		this.setSize(300, 350);
		this.setLocation(400, 200);
		this.setTitle("Chat Client ");
		
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		connect.addActionListener(new Connect());//为按钮设置点击事件
		send.addActionListener(new SendMsg());
		
		clear.addActionListener(new ActionListener() {//清空文本域内容
			public void actionPerformed(ActionEvent e) {
				taContent.setText("");
			}
		});
		signIn.addActionListener(new SignIn());//为注册键添加点击事件
		
		JOptionPane.showMessageDialog(TCPClient.this, "请先注册", "提醒", 1);//注册对话框
	}
	
	public void connectToServer() {
		try {
			
			s = new Socket("127.0.0.1", 8888);//创建指定的ip的地址和端口
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		
			bConnected = true;
			
		} catch (BindException e) {
			System.out.println("找不到指定的服务器");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void disConnect() {
		try {
			if (s != null) {
				s.close();
			}
			
			if (dos != null) {
				dos.close();
			}
			if (dis != null) {
				dis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		TCPClient tc = new TCPClient();
		tc.launchFrame();
	}
	
	private class Connect implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "连接") {
				
				connectToServer();
				try {
					t.start();
				} catch (IllegalThreadStateException ex) {
					
				}
				
				connect.setText("断开连接");
				
			} else if (e.getActionCommand() == "断开连接") {
				disConnect();
				connect.setText("连接");
			}
			
		}
	}
	
	private class SendMsg implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (connect.getActionCommand() == "连接") {
				JOptionPane.showMessageDialog(TCPClient.this,
						"没有找到指定的服务器", "错误提示", 1);//显示对话框
			} else {
				
			     String str = start.getName()+":"+tfTxt.getText();
				tfTxt.setText("");
				
				
				try {
					dos.writeUTF(str);
					dos.flush();//清空数据流数据，使其立刻写出
				} catch (SocketException ex) {
					System.out.println("没有找到指定的服务器");
					JOptionPane.showMessageDialog(TCPClient.this,
							"没有找到指定的服务器", "错误提示", 1);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
		}
	}
	
	private class SignIn implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(signIn.getActionCommand()=="注册") {
				start.launchframe();
				tfTxt.setEditable(true);
				signIn.setEnabled(false);//点击注册后不能再点击了
				
				//System.out.println(name);
		
				
			}
			
		}
	}
	
	private class RecToServer implements Runnable {
		public void run() {
			try {
				while (bConnected) {
					String str = dis.readUTF();
					 System.out.println(str);
					
					taContent.append(str + "\n");
				}
			} catch (SocketException e) {
				System.out.println("服务器已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}