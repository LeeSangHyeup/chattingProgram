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
			JOptionPane.showMessageDialog( null, "입력스트림을 얻을 수 없습니다.", "스트림 얻기 실패", JOptionPane.ERROR_MESSAGE);
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
