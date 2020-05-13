package network;

import java.net.*;
import java.io.*;

import javax.swing.JOptionPane;

import function.ClientFileReceiver;

public class Client {
	Socket socket;

	public Client(String ipAdress, String serverPort)
	{
		new ClientFileReceiver().start();
		try
		{
			socket = new Socket(ipAdress, Integer.parseInt(serverPort));
		} 
		catch (UnknownHostException e1) 
		{
			JOptionPane.showMessageDialog( null, "����ȣ��Ʈ�� ã�� �� �����ϴ�.", "���ϻ�������", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} 
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog( null, "���ϻ��� ����", "�������ӽ���", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}catch(IllegalArgumentException IE)
		{
			JOptionPane.showMessageDialog( null, "����IP�ּ� �Ǵ� ������Ʈ��ȣ�� Ȯ���ϼ���.", "�������ӽ���", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	public void finalize()
	{
		try {
			socket.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, "Ŭ���̾�ƮŬ������ ���ϴݱ⿡ �����Ͽ����ϴ�.", "Ŭ���̾�Ʈ���ϴݱ� ����", JOptionPane.ERROR_MESSAGE);
		}
	}
	public Socket getSocket()
	{
		return socket;
	}
}
