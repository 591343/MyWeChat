import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/*
 * ����һ��ע�ᴰ��
 */
public class Tool extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JButton sure=new JButton("ȷ��");
	private String userName="";
     
     JTextField signIn=new JTextField(20);
     
     
	public void launchframe() {
		// TODO Auto-generated constructor stub
		this.setTitle("ע��");
		this.add(signIn,"Center");
		this.add(sure,"South");
		this.setBounds(300, 300, 300,100);
		this.setVisible(true);
	    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    sure.addActionListener(new SureTo());
			
	}
	
	private class SureTo implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getActionCommand()=="ȷ��") {
				JOptionPane.showMessageDialog(Tool.this, "ע��ɹ�", "ϵͳ��Ϣ", 1);
				userName=signIn.getText();
				
				dispose();
			}
		}
	}
	
	public String getName() {
		return userName;
	}
	
	
	
}
