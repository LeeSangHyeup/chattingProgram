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
			JOptionPane.showMessageDialog( null, "서버호스트를 찾을 수 없습니다.", "소켓생성실패", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} 
		catch (IOException e1)
		{
			JOptionPane.showMessageDialog( null, "소켓생성 실패", "서버접속실패", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}catch(IllegalArgumentException IE)
		{
			JOptionPane.showMessageDialog( null, "서버IP주소 또는 서버포트번호를 확인하세요.", "서버접속실패", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	public void finalize()
	{
		try {
			socket.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog( null, "클라이언트클래스의 소켓닫기에 실패하였습니다.", "클라이언트소켓닫기 실패", JOptionPane.ERROR_MESSAGE);
		}
	}
	public Socket getSocket()
	{
		return socket;
	}
}
