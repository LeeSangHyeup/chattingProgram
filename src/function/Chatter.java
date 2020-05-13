package function;


import java.awt.HeadlessException;
import java.io.*;
import java.net.*;

import javax.swing.*;

public abstract class Chatter implements Runnable
{
	BufferedReader bufferedReader;
	JTextArea chatTextArea;
	JTextField chatTextField;
	
	public Chatter(JTextArea chatTextArea, JTextField chatTextField, Socket socket)
	{
		this.chatTextArea = chatTextArea;
		this.chatTextField = chatTextField;

		try 
		{
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog( null, "�Է½�Ʈ���� ���� �� �����ϴ�.", "��Ʈ�� ��� ����", JOptionPane.ERROR_MESSAGE);
		}

	}
	public Chatter(){}
	
	public abstract void run();
	
	public void finalize()
	{
		try {
			bufferedReader.close();
		} catch (Exception Ignored) {}
	}
	
}
