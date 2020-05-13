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
			JOptionPane.showMessageDialog( null, "서버포트번호를 확인하세요.", "서버생성실패", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void run() 
	{
		while(true)
		{
			try 
			{
				Socket clientSocket = serverSocket.accept();
				//서버의 채팅담당 클래스 생성
				ServerChatter serverChatter = new ServerChatter(clientSocket, userList);
				Thread serverChatterThread = new Thread(serverChatter);		
				serverChatterThread.start();
				
			} catch (IOException e) 
			{
				JOptionPane.showMessageDialog( null, "서버와 클래이언트간의 소켓생성에 실패하였습니다.", "서버소켓생성 실패", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog( null, "서버클래스의 서버소켓닫기에 실패하였습니다..", "서버소켓닫기 실패", JOptionPane.ERROR_MESSAGE);
		}
	}
}
