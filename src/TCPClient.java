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
	
	JButton send = new JButton("����");
	JButton connect = new JButton("����");
	JButton clear = new JButton("���");
	JButton signIn=new JButton("ע��");
	
	boolean live = false;
	JPanel p1 = new JPanel();
	JPanel p2 = new JPanel();
	
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	
	boolean bConnected = false;
	
	
	
	Tool start=new Tool();//������
	
	
	Thread t = new Thread(new RecToServer());
	
	public void launchFrame() {
		
		taContent.setEditable(false);
		tfTxt.setEditable(false);
		p2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));//��ʽ���֣��ƶ����뷽ʽ,��ˮƽ����ֱ�ļ�϶��2������Ϊˮƽ��϶����3��Ϊ��ֱ��϶
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
		
		connect.addActionListener(new Connect());//Ϊ��ť���õ���¼�
		send.addActionListener(new SendMsg());
		
		clear.addActionListener(new ActionListener() {//����ı�������
			public void actionPerformed(ActionEvent e) {
				taContent.setText("");
			}
		});
		signIn.addActionListener(new SignIn());//Ϊע�����ӵ���¼�
		
		JOptionPane.showMessageDialog(TCPClient.this, "����ע��", "����", 1);//ע��Ի���
	}
	
	public void connectToServer() {
		try {
			
			s = new Socket("127.0.0.1", 8888);//����ָ����ip�ĵ�ַ�Ͷ˿�
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
		
			bConnected = true;
			
		} catch (BindException e) {
			System.out.println("�Ҳ���ָ���ķ�����");
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
			if (e.getActionCommand() == "����") {
				
				connectToServer();
				try {
					t.start();
				} catch (IllegalThreadStateException ex) {
					
				}
				
				connect.setText("�Ͽ�����");
				
			} else if (e.getActionCommand() == "�Ͽ�����") {
				disConnect();
				connect.setText("����");
			}
			
		}
	}
	
	private class SendMsg implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (connect.getActionCommand() == "����") {
				JOptionPane.showMessageDialog(TCPClient.this,
						"û���ҵ�ָ���ķ�����", "������ʾ", 1);//��ʾ�Ի���
			} else {
				
			     String str = start.getName()+":"+tfTxt.getText();
				tfTxt.setText("");
				
				
				try {
					dos.writeUTF(str);
					dos.flush();//������������ݣ�ʹ������д��
				} catch (SocketException ex) {
					System.out.println("û���ҵ�ָ���ķ�����");
					JOptionPane.showMessageDialog(TCPClient.this,
							"û���ҵ�ָ���ķ�����", "������ʾ", 1);
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
			if(signIn.getActionCommand()=="ע��") {
				start.launchframe();
				tfTxt.setEditable(true);
				signIn.setEnabled(false);//���ע������ٵ����
				
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
				System.out.println("�������ѹر�");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}