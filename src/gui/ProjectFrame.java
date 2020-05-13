package gui;

import javax.swing.*;

import network.User;

import function.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import java.util.*;
import java.util.List;

public class ProjectFrame extends JFrame implements ActionListener
{
	//gui관련 멤버	
	private GridBagLayout gridBagLayout = new GridBagLayout();
	
	private JMenuBar jMenuBar = new JMenuBar();
	private JMenu information = new JMenu("정보(I)");
	private JMenuItem help = new JMenuItem("      도움말(L)");
	private JMenuItem programInformation =      new JMenuItem("      프로그램 정보(P)");
	private JTextField chatTextField = new JTextField(50);
	private JTextArea chatTextArea = new JTextArea();
	private JScrollPane chatTextAreaScroll = new JScrollPane(getChatTextArea());
	private JLabel image = new JLabel("이미지");
	private JLabel plan = new JLabel("공지/계획");
	private JLabel crew = new JLabel("팀원목록/작업상태");
	private JButton fileTransferButton = new JButton(new ImageIcon("./images/FileTransferIcon.jpg"));
	private JList jList = new JList();
	//private Vector<User> userVector = new Vector<User>();
	//채팅관련
	private String receiver;
	private ClientChatter clientChatter;
	private enum ChatType{NORMAR, WHISPER, REPLY};
	ChatType chatType = ChatType.NORMAR;
	private List<User> userList=null;
	//파일전송관련
	private JFileChooser jFileChooser = new JFileChooser("./"); 
	
	public ProjectFrame(String nickName, String serverIP, String serverPort, Socket socket,List<User> userList)
	{
		this.clientChatter = new ClientChatter(nickName, this.chatTextArea, this.chatTextField, socket, userList);
		Thread thread = new Thread(clientChatter);	
		thread.start();
		
		this.setLayout(gridBagLayout);
		this.setTitle("OPM                                                                             " +
				"                                                                                      " +
				"                                                       "+
				"IP: "+serverIP+"        port: "+serverPort);

		this.setJMenuBar(jMenuBar);
		//텍스트가 가로길이초과시 자동줄바꿈
		this.chatTextArea.setLineWrap(true);
		//편집금지설정
		this.chatTextArea.setEditable(false);
		image.setName("image");
		this.insert(image,0,0,599,400);
		plan.setName("plan");
		this.insert(plan,600,0,150,400);
		chatTextAreaScroll.setName("chatTextArea");
		this.insert(chatTextAreaScroll,0,400,600,270);

		this.insert(chatTextField,0,670,600,29);
		this.fileTransferButton.setSize(100,300);
		this.fileTransferButton.setMargin(new Insets(0,0,0,0));
		//this.fileTransferButton.setBorderPainted(false);
		this.insert(fileTransferButton,600,400,100,290);
		this.insert(new JScrollPane(jList), 700, 400, 50,299);
		crew.setName("crew");
		
		this.chatTextField.addActionListener(this);
		this.fileTransferButton.addActionListener(this);
		this.fileTransferButton.setMnemonic(KeyEvent.VK_F);
		
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(res.width/5.5), (int)(res.height/7));
		this.setVisible(true);
		this.setSize(1240,775);
		this.setResizable(false);

		chatTextField.requestFocus();
		
		jMenuBar.add(information);				information.setMnemonic(KeyEvent.VK_I);
		information.add(help);					help.addActionListener(this);			help.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.CTRL_DOWN_MASK));
												help.setMnemonic('L');
		information.add(programInformation);	programInformation.addActionListener(this); 
												programInformation.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.CTRL_DOWN_MASK));
												programInformation.setMnemonic('P');
												
		jList.setModel(clientChatter.getlistModel());
		jList.setFont(new Font(null,20,20));
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.addWindowListener(new WindowAdapter()
		{	
			public void windowClosing(WindowEvent e)
			{
				clientChatter.write("disConnect/");
				System.exit(0);
			}
		});
	}
	public ProjectFrame(String nickName,String serverPublicIP, String serverLocalIP, String serverPort, Socket socket, List<User> userList)
	{
		new ProjectFrame(nickName, serverLocalIP, serverPort, socket, userList)
		.setTitle("OPM                                                                                                            "+
				  "                  "+
				"Public IP: "+serverPublicIP+"       Local IP: "+serverLocalIP+"       port: "+serverPort);
	}
	
	//GridBagLayout에 컴포넌트 삽입을 쉽게 하기위한 메소드
    private void insert(Component cmpt, int x, int y, int w, int h) { 
        GridBagConstraints gbc = new GridBagConstraints();  
        if("image"==cmpt.getName())
        {
        	gbc.ipadx = 500;
        	gbc.ipady = 500;
        }else if("plan"==cmpt.getName())
        {
        	gbc.ipadx = 500;
        	gbc.ipady = 500;
        }else if("chatTextArea"==cmpt.getName())
        {
        	gbc.ipadx = 650;
        	gbc.ipady = 150;
        }else if("crew"==cmpt.getName())
        {
        	gbc.ipadx = 350;
        	gbc.ipady = 200;
        }
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;  
        gbc.gridy = y;  
        gbc.gridwidth  = w;  
        gbc.gridheight = h;  
        this.gridBagLayout.setConstraints(cmpt, gbc);  
        this.add(cmpt); 
    } 
	public void actionPerformed(ActionEvent e)
	{
		Object object = e.getSource();
		if(chatTextField == object)
		{
			if(chatTextField.getText().equals("bye"))
			{
				clientChatter.write("disConnect/");
				System.exit(0);
			}
			String string="";
			if(2 <= chatTextField.getText().length())
			{
				string = chatTextField.getText().substring(0, 2);
			}
			//귓속말 모드
			if(string.equals("/W")||string.equals("/w")||string.equals("/ㅈ"))
			{
				receiver = chatTextField.getText().substring(2);
				if(receiver.equals(""))
				{
					this.chatTextArea.append("==수신자 대화명을 입력하세요.==\n");
					this.chatTextField.setText("");
					//텍스트가 아래로 넘치면 스크롤바 포커스를 자동이동
					int pos = chatTextArea.getText().length();
					chatTextArea.setCaretPosition(pos-1);
				}
				else
				{
					chatType = ChatType.WHISPER;
					this.chatTextField.setText("");
					this.chatTextArea.append("=="+receiver+"님에게 귓속말 시작==\n");
					//텍스트가 아래로 넘치면 스크롤바 포커스를 자동이동
					int pos = chatTextArea.getText().length();
					chatTextArea.setCaretPosition(pos-1);
				}
			}
			else if(string.equals("/R")||string.equals("/r")||string.equals("/ㄱ"))
			{
				//미구현
				chatType = ChatType.REPLY;
			}
			else if(string.equals("/Q")||string.equals("/q")||string.equals("/ㅂ"))
			{
				chatType = ChatType.NORMAR;
				this.chatTextField.setText("");
				this.chatTextArea.append("==귓속말 종료==\n");
				//텍스트가 아래로 넘치면 스크롤바 포커스를 자동이동
				int pos = chatTextArea.getText().length();
				chatTextArea.setCaretPosition(pos-1);
			}
			else
			{
				switch(chatType)
				{
					case NORMAR:
					{
						clientChatter.write("chat/");
						break;
					}
					case WHISPER:
					{
						clientChatter.write("whisper/"+receiver+"@");
						break;
					}
					case REPLY:
					{
						//미구현
						break;
					}
				}
			}
		}
		else if(help == object)
		{
			JOptionPane.showMessageDialog( null, "\n                       ※기능설명\n\n" +
					"●귓속말            - /w(또는/W, /ㅈ)수신자대화명\n\n		●귓속말종료 - /q(또는 /Q, /ㅂ)\n\n" +
					"●파일전송(한명에게) - 우측 접속자리스트에서 보내고자 하는 유저를 클릭 후\n                                             파일전송버튼누름\n\n" +
					"●파일전송(다수에게) - Ctrl키를 누른 상태에서 보내려는 유저이름을 다수 \n                                              선택한 후 파일전송버튼누름 " +
					"\n\n"
                    , "도움말", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(programInformation == object)
		{
			JOptionPane.showMessageDialog( null, "     Online Project Manager\n                      By\n" +
					"              신흥대학교\n       소프트웨어개발전공\n            team FOREST"
                    , "프로그램 정보", JOptionPane.NO_OPTION);
		}
		else if(fileTransferButton == object)
		{
			if(jList.getSelectedValue().toString() != null)
			{
				Object[] nickName = jList.getSelectedValues();
				int result = jFileChooser.showDialog(fileTransferButton, "파일전송");
				if(result == 0)
				{
					for(int i=0; i<nickName.length;i++)
					{
						System.out.println(clientChatter.getUserIp(nickName[i].toString()));
						new ClientFileSender(clientChatter.getUserIp(nickName[i].toString()), jFileChooser.getSelectedFile());
					}
				}
			}
		}
	}
	public JTextArea getChatTextArea() 
	{
		return this.chatTextArea;
	}
	public JTextField getChatTextField()
	{
		return this.chatTextField;
	}
}
