package function;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import network.User;

public class ClientChatter extends Chatter
{
	private String nickName;
	private PrintWriter printWriter;
	private DefaultListModel<User> defaultListModel = new DefaultListModel<User>();
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private Socket socket;
	private List<User> userList;  
	
	public ClientChatter(String nickName, JTextArea chatTextArea, JTextField chatTextField, Socket socket, List<User> userList) {
		super(chatTextArea, chatTextField, socket);
		this.userList = userList;
		this.nickName = nickName;
		this.socket = socket;
		User user = new User(nickName, "127.0.0.1");
		defaultListModel.addElement(user);
		listModel.addElement(nickName);
		try 
		{
			printWriter = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) 
		{
			JOptionPane.showMessageDialog( null, "printWriter = new PrintWriter(socket.getOutputStream());; 실패", "ClientChatter 생성 실패", JOptionPane.ERROR_MESSAGE);
		}
		printWriter.println("connect/"+nickName);
		printWriter.flush();
	}

	public void write(String string) 
	{
		/*String protocol
		 * connect = 접속메시지
		 * disConnect = 접속종료
		 * chat = 채팅메시지
		 * whisper = 귓속말
		 */
		int protocolEndOffset = string.indexOf("/");
		String protocol = string.substring(0, protocolEndOffset );
		
		if("chat".equals(protocol))
		{
			printWriter.println(protocol+"/"+nickName+" > "+chatTextField.getText());
		}
		else if("disConnect".equals(protocol))
		{
			printWriter.println(protocol+"/"+nickName);
		}
		else if("whisper".equals(protocol))
		{
			int receiverEndOffset = string.indexOf("@");
			String receiver = string.substring(protocol.length()+1, receiverEndOffset );
			printWriter.println(protocol+"/"+receiver+"@"+nickName+" > "+chatTextField.getText());
		}
		printWriter.flush();
		this.chatTextField.setText("");
	}
	public void run()
	{
		String string="";
		try 
		{
			while(null != string)
			{
				/*String protocol
				 * connect = 접속메시지
				 * disConnect = 접속종료
				 * chat = 채팅메시지
				 * whisper = 귓속말
				 */
				string = bufferedReader.readLine();
				int protocolEndOffset = string.indexOf("/");
				String protocol = string.substring(0, protocolEndOffset );
				if("connect".equals(protocol))
				{
					StringTokenizer stringTokenizer = new StringTokenizer(string,"님");
					String Ip = stringTokenizer.nextToken();
					Ip = stringTokenizer.nextToken();
					if(!Ip.substring(0, 1).equals("이")){
						if(!this.getNickName(string).equals(this.nickName))
						{
							User user = new User(getNickName(string),Ip);
							defaultListModel.addElement(user);
							listModel.addElement(getNickName(string));
						}
					}
					else
					{
						
						printWithoutProtocol(string);
					}
				}
				else if("disConnect".equals(protocol))
				{
					String nickName = getNickName(string);
					defaultListModel.removeElement(nickName);
					listModel.removeElement(nickName);
					if(this.userList != null)
					{
						for(int i=0; i<userList.size(); i++)
						{
							if(userList.get(i).getNickName().equals(nickName))
							{
								userList.remove(i);
								break;
							}
						}
					}
					printWithoutProtocol(string);
				}
				else
				{
					String time = Calendar.getInstance().getTime().toString();
					int startOffset = time.indexOf(':')-3;
					int endOffset = time.indexOf('K')-2;
					time = time.substring(startOffset, endOffset);
					printWithoutProtocol(string+"        -" + time);
				}
			}
		}
		catch (HeadlessException | IOException e) 
		{
			JOptionPane.showMessageDialog( null, "서버와 접속이 끊어졌습니다.", "버퍼리더스트림 읽기 실패", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void printWithoutProtocol(String string)
	{
		int startOffset = string.indexOf("/")+1;
		String flag = string.substring(startOffset, startOffset+1);
		if(!flag.equals("/"))
		{
			String chatMasage = string.substring(startOffset, string.length());
			chatTextArea.append(chatMasage+"\n");
			//텍스트가 아래로 넘치면 스크롤바 포커스를 자동이동
			int pos = chatTextArea.getText().length();
			chatTextArea.setCaretPosition(pos-1);
		}
	}
	public String getNickName(String string)
	{
		int nickNameStartOffset = string.indexOf("-")+3;
		int nickNameEndOffset = string.indexOf("님");
		String nickName = string.substring(nickNameStartOffset, nickNameEndOffset);
		return nickName;
	}
	public DefaultListModel getlistModel()
	{
		return listModel;
	}
	public Socket getSocket()
	{
		return socket;
	}
	public void finalize()
	{
		write(nickName);
		super.finalize();
		printWriter.close();
	}
	public String getUserIp(String nickName)
	{
		int i=0;
		while(true)
		{
			User user = defaultListModel.elementAt(i);
			if(user.getNickName().equals(nickName))
			{
				String Ip = user.getIp();
				if(Ip.equals("server"))
				{
					return socket.getInetAddress().toString().substring(1);
				}
				else
				{
					return Ip;
				}
			}
			i++;
		}
	}
}
