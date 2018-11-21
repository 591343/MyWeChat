// �������˳���
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
	private ServerSocket ss = null;//�������׽���
	private boolean bStart = false;//��־������Ӧ�ó����Ƿ��Ѵ�
	private JPanel  p1=new JPanel();
	private JTextArea taContent = new JTextArea();
	private JTextArea nowContent= new JTextArea();//������ʾ������״̬��Ŀǰ���û�����
	
	private int index = 0;//��¼Ŀǰ�û�������
	
	List<Client> clients = new ArrayList<Client>();//��������û�
	
	
	/*
	 * ������UI
	 */
	public void launchFrame() {
//		Container con = this.getContentPane();
		taContent.setEditable(false);//�Ƿ�ɱ༭�ı���
		nowContent.setEditable(false);
		
		
		taContent.setBackground(Color.DARK_GRAY);//���ñ�����ɫ
		taContent.setForeground(Color.YELLOW);//����ǰ����ɫҲ����������ɫ
		
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
			ss = new ServerSocket(8888);//���ӵ�ָ���Ķ˿�
			bStart = true;              //�����������Ѵ�
			nowContent.append("�����������ɹ�...\n");
			nowContent.append("�����˿�: "+ss.getLocalPort()+"\n");
			nowContent.append("��ǰ��������: "+clients.size());
		} catch (IOException e) {//���봦��IOEException
			
			e.printStackTrace();
		}
		
		try {
			while (bStart) {
				index++;
				Socket s = ss.accept();      //�ȴ��ͻ��˵�����,�������򴴽�һ���׽���
				Client c = new Client(s);    //
				clients.add(c);
				nowContent.setText("");
				nowContent.append("�����������ɹ�...\n");
				nowContent.append("�����˿�: "+ss.getLocalPort()+"\n");
				nowContent.append("��ǰ��������: "+clients.size());
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
		DataInputStream dis = null;   //����д����
		DataOutputStream dos = null;   //���ݶ�����
		
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
		
		public void sendToEveryClient(String str) {   //����Ϣ���͵�ÿһλ�û�
			try {
				dos.writeUTF(str);
				dos.flush();
				
			} catch (IOException e) {
				index--;
				clients.remove(this);
				taContent.append(s.getInetAddress().getHostAddress()
						+ " exited " + index + " clients\n");
				System.out.println("�Է��˳��ˣ��Ҵ�List����ȥ���ˣ�");
			}
		}
		
		public void run() {
			try {
				while (bStart) {
					String str = dis.readUTF();
					System.out.println(str);
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.sendToEveryClient(str);//����Ϣ����ÿ���û�
					}
				}
			} catch (EOFException e) {//���������û��Ͽ�����ʱ��׽���쳣
				clients.remove(this);
				nowContent.setText("");
				nowContent.append("�����������ɹ�...\n");
				nowContent.append("�����˿�: "+ss.getLocalPort()+"\n");
				nowContent.append("��ǰ��������: "+clients.size());
				taContent.append(s.getInetAddress().getHostAddress()
						+ " exited " + clients.size() + " clients\n");
				System.out.println("client closed");
			} catch (SocketException e) {//���������ͻ��˹ر�ʱ��׽���쳣
				clients.remove(this);
				nowContent.setText("");
				nowContent.append("�����������ɹ�...\n");
				nowContent.append("�����˿�: "+ss.getLocalPort()+"\n");
				nowContent.append("��ǰ��������: "+clients.size());
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
