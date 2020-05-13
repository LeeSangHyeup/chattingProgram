package network;

import java.net.*;
import java.util.*;
import java.io.*;

import javax.swing.JOptionPane;

import function.*;

public class Server implements Runnable{
	private List<User> userList = Collections.synchronizedList(new ArrayList<User>());
	private ServerSocket serverSocket;
	private Client client;

	public Server(String serverPort)
	{
		try 
		{
			serverSocket = new ServerSocket(Integer.parseInt(serverPort));
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog( null, "������Ʈ��ȣ�� Ȯ���ϼ���.", "������������", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void run() 
	{
		while(true)
		{
			try 
			{
				Socket clientSocket = serverSocket.accept();
				//������ ä�ô�� Ŭ���� ����
				ServerChatter serverChatter = new ServerChatter(clientSocket, userList);
				Thread serverChatterThread = new Thread(serverChatter);		
				serverChatterThread.start();
				
			} catch (IOException e) 
			{
				JOptionPane.showMessageDialog( null, "������ Ŭ���̾�Ʈ���� ���ϻ����� �����Ͽ����ϴ�.", "�������ϻ��� ����", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	public List<User> getArrayList()
	{
		return userList;
	}
	public ServerSocket getServerSocket()
	{
		return serverSocket;
	}
	public void finalize()
	{
		try {
			serverSocket.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, "����Ŭ������ �������ϴݱ⿡ �����Ͽ����ϴ�..", "�������ϴݱ� ����", JOptionPane.ERROR_MESSAGE);
		}
	}
}
