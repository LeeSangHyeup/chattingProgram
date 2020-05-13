package function;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.*;

import network.User;

public class ServerChatter extends Chatter
{
	private PrintWriter printWriter;
	private List<User> userList;
	private Socket socket;
	
	public ServerChatter(Socket socket, List<User> userList)
	{
		this.socket = socket;
		this.userList = userList;
		try 
		{
			User user;
			printWriter = new PrintWriter(socket.getOutputStream());
			if(userList.isEmpty())
			{
				user = new User(printWriter, "server");	
			}
			else
			{
				user = new User(printWriter, socket.getInetAddress().toString().substring(1));
			}
			userList.add(user);
		} 
		catch (IOException e1) 
		{
			JOptionPane.showMessageDialog( null, "printWriter = new PrintWriter(socket.getOutputStream()); 실패", "PrintWrit 생성 실패", JOptionPane.ERROR_MESSAGE);
		}
		try 
		{
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog( null, "입력스트림을 얻을 수 없습니다.", "스트림 얻기 실패", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void write(){}
	
	public void run() 
	{
		while(true)
		{
			try {
				String string = bufferedReader.readLine();
				
				if(null == string){break;}
				else
				{
					/*String protocol
					 * connect = 접속메시지
					 * disConnect = 접속종료
					 * chat = 채팅메시지
					 * whisper = 귓속말
					 */
					int protocolEndOffset = string.indexOf("/");
					String protocol = string.substring(0, protocolEndOffset );
					//접속메시지에 대한 처리
					if("connect".equals(protocol))
					{
						String nickName=null;
						String nickNameIp = null;
						User currentUser = null;
						if (!userList.isEmpty()) 
						{
							currentUser = userList.get(userList.size()-1);
							currentUser.setNickName(nickName=string.substring(protocolEndOffset+1));
						}
						sendAll("connect/             -- "+nickName+
								"님이 접속하셨습니다.  "+ currentUser.getIp()  +"--                               "
								+Calendar.getInstance().getTime());
						for(User user : userList)
						{
							if(user.getNickName().equals(nickName)) nickNameIp = user.getIp();
						}
						for(User user : userList)
						{
							{
								sendOne("connect/-  "+user.getNickName()+"님"+user.getIp(), nickName);
								if(!user.getNickName().equals(nickName))
								{
									sendOne("connect/-  "+nickName+"님"+nickNameIp, user.getNickName());
								}
							}
						}
					}
					//접속종료메시지에 대한 처리
					else if("disConnect".equals(protocol))
					{
						sendAll("disConnect/             -- "+string.substring(protocolEndOffset+1)+
								"님이 접속을 종료하였습니다. --                  "
								+Calendar.getInstance().getTime());
					}
					//채팅메시지에 대한 처리
					else if("chat".equals(protocol))
					{
						sendAll("chat/"+string.substring(protocolEndOffset+1));
					}
					//귓속말에 대한 처리
					else if("whisper".equals(protocol))
					{
						int receiverEndOffset = string.indexOf("@");
						String receiver = string.substring(protocolEndOffset+1,receiverEndOffset);
						int nickNameEndOffset = string.indexOf(">")-1;	
						String nickName = string.substring(receiverEndOffset+1, nickNameEndOffset);
						sendOne("whisper/whisper-"+string.substring(receiverEndOffset+1), receiver);
						sendOne("whisper/whisper-"+receiver+string.substring(receiverEndOffset+1+nickName.length()), nickName);
					}
				}
			} 
			catch (Exception ignord) 
			{
			}
		}
	}
	
	private void sendAll(String string)
	{
		for(User printWriter : userList)
		{
			{
				printWriter.getPrintWriter().println(string);
				printWriter.getPrintWriter().flush();
			}
		}
	}
	private void sendOne(String string, String nickName)
	{
		for(User user : userList)
		{
			if(user.getNickName().equals(nickName))
			{
				user.getPrintWriter().println(string);
				user.getPrintWriter().flush();
				break;
			}
		}
	}
	
	public void finalize()
	{
		super.finalize();
		printWriter.close();
	}
}
