// 服务器端程序
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class TCPServer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServerSocket ss = null;//服务器套接字
	private boolean bStart = false;//标志服务器应用程序是否已打开
	private JPanel  p1=new JPanel();
	private JTextArea taContent = new JTextArea();
	private JTextArea nowContent= new JTextArea();//用于显示服务器状态和目前的用户人数
	
	private int index = 0;//记录目前用户的数量
	
	List<Client> clients = new ArrayList<Client>();//用于添加用户
	
	
	/*
	 * 服务器UI
	 */
	public void launchFrame() {
//		Container con = this.getContentPane();
		taContent.setEditable(false);//是否可编辑文本域
		nowContent.setEditable(false);
		
		
		taContent.setBackground(Color.DARK_GRAY);//设置背景颜色
		taContent.setForeground(Color.YELLOW);//设置前景颜色也就是字体颜色
		
		p1.setLayout(new GridLayout(1, 2));
		p1.add(nowContent);
		p1.add(taContent);
		this.add(p1,"Center");
	
		this.setSize(350, 350);
		this.setLocation(400, 200);
		this.setTitle("TCP Server");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tcpMonitor();
	}
	
	public void tcpMonitor() {
		try {
			ss = new ServerSocket(8888);//连接到指定的端口
			bStart = true;              //服务器程序已打开
			nowContent.append("服务器启动成功...\n");
			nowContent.append("监听端口: "+ss.getLocalPort()+"\n");
			nowContent.append("当前在线人数: "+clients.size());
		} catch (IOException e) {//必须处理IOEException
			
			e.printStackTrace();
		}
		
		try {
			while (bStart) {
				index++;
				Socket s = ss.accept();      //等待客户端的连接,若连接则创建一个套接字
				Client c = new Client(s);    //
				clients.add(c);
				nowContent.setText("");
				nowContent.append("服务器启动成功...\n");
				nowContent.append("监听端口: "+ss.getLocalPort()+"\n");
				nowContent.append("当前在线人数: "+clients.size());
				taContent.append(s.getInetAddress().getHostAddress()
						+ " connected " + index + " clients\n");
				new Thread(c).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String args[]) {
		TCPServer ts = new TCPServer();
		ts.launchFrame();
	}
	
	private class Client implements Runnable {
		DataInputStream dis = null;   //数据写入流
		DataOutputStream dos = null;   //数据读出流
		
		Socket s = null;           
		boolean bStart = false;
		
		Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			bStart = true;
		}
		
		public void sendToEveryClient(String str) {   //将信息发送到每一位用户
			try {
				dos.writeUTF(str);
				dos.flush();
				
			} catch (IOException e) {
				index--;
				clients.remove(this);
				taContent.append(s.getInetAddress().getHostAddress()
						+ " exited " + index + " clients\n");
				System.out.println("对方退出了！我从List里面去掉了！");
			}
		}
		
		public void run() {
			try {
				while (bStart) {
					String str = dis.readUTF();
					System.out.println(str);
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.sendToEveryClient(str);//将信息发往每个用户
					}
				}
			} catch (EOFException e) {//当侦听到用户断开连接时捕捉该异常
				clients.remove(this);
				nowContent.setText("");
				nowContent.append("服务器启动成功...\n");
				nowContent.append("监听端口: "+ss.getLocalPort()+"\n");
				nowContent.append("当前在线人数: "+clients.size());
				taContent.append(s.getInetAddress().getHostAddress()
						+ " exited " + clients.size() + " clients\n");
				System.out.println("client closed");
			} catch (SocketException e) {//当侦听到客户端关闭时捕捉该异常
				clients.remove(this);
				nowContent.setText("");
				nowContent.append("服务器启动成功...\n");
				nowContent.append("监听端口: "+ss.getLocalPort()+"\n");
				nowContent.append("当前在线人数: "+clients.size());
				System.out.println("client closed");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (s != null)
						s.close();
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
