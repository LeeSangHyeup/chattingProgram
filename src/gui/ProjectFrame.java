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
	//gui���� ���	
	private GridBagLayout gridBagLayout = new GridBagLayout();
	
	private JMenuBar jMenuBar = new JMenuBar();
	private JMenu information = new JMenu("����(I)");
	private JMenuItem help = new JMenuItem("      ����(L)");
	private JMenuItem programInformation =      new JMenuItem("      ���α׷� ����(P)");
	private JTextField chatTextField = new JTextField(50);
	private JTextArea chatTextArea = new JTextArea();
	private JScrollPane chatTextAreaScroll = new JScrollPane(getChatTextArea());
	private JLabel image = new JLabel("�̹���");
	private JLabel plan = new JLabel("����/��ȹ");
	private JLabel crew = new JLabel("�������/�۾�����");
	private JButton fileTransferButton = new JButton(new ImageIcon("./images/FileTransferIcon.jpg"));
	private JList jList = new JList();
	//private Vector<User> userVector = new Vector<User>();
	//ä�ð���
	private String receiver;
	private ClientChatter clientChatter;
	private enum ChatType{NORMAR, WHISPER, REPLY};
	ChatType chatType = ChatType.NORMAR;
	private List<User> userList=null;
	//�������۰���
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
		//�ؽ�Ʈ�� ���α����ʰ��� �ڵ��ٹٲ�
		this.chatTextArea.setLineWrap(true);
		//������������
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
	
	//GridBagLayout�� ������Ʈ ������ ���� �ϱ����� �޼ҵ�
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
			//�ӼӸ� ���
			if(string.equals("/W")||string.equals("/w")||string.equals("/��"))
			{
				receiver = chatTextField.getText().substring(2);
				if(receiver.equals(""))
				{
					this.chatTextArea.append("==������ ��ȭ���� �Է��ϼ���.==\n");
					this.chatTextField.setText("");
					//�ؽ�Ʈ�� �Ʒ��� ��ġ�� ��ũ�ѹ� ��Ŀ���� �ڵ��̵�
					int pos = chatTextArea.getText().length();
					chatTextArea.setCaretPosition(pos-1);
				}
				else
				{
					chatType = ChatType.WHISPER;
					this.chatTextField.setText("");
					this.chatTextArea.append("=="+receiver+"�Կ��� �ӼӸ� ����==\n");
					//�ؽ�Ʈ�� �Ʒ��� ��ġ�� ��ũ�ѹ� ��Ŀ���� �ڵ��̵�
					int pos = chatTextArea.getText().length();
					chatTextArea.setCaretPosition(pos-1);
				}
			}
			else if(string.equals("/R")||string.equals("/r")||string.equals("/��"))
			{
				//�̱���
				chatType = ChatType.REPLY;
			}
			else if(string.equals("/Q")||string.equals("/q")||string.equals("/��"))
			{
				chatType = ChatType.NORMAR;
				this.chatTextField.setText("");
				this.chatTextArea.append("==�ӼӸ� ����==\n");
				//�ؽ�Ʈ�� �Ʒ��� ��ġ�� ��ũ�ѹ� ��Ŀ���� �ڵ��̵�
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
						//�̱���
						break;
					}
				}
			}
		}
		else if(help == object)
		{
			JOptionPane.showMessageDialog( null, "\n                       �ر�ɼ���\n\n" +
					"�ܱӼӸ�            - /w(�Ǵ�/W, /��)�����ڴ�ȭ��\n\n		�ܱӼӸ����� - /q(�Ǵ� /Q, /��)\n\n" +
					"����������(�Ѹ���) - ���� �����ڸ���Ʈ���� �������� �ϴ� ������ Ŭ�� ��\n                                             �������۹�ư����\n\n" +
					"����������(�ټ�����) - CtrlŰ�� ���� ���¿��� �������� �����̸��� �ټ� \n                                              ������ �� �������۹�ư���� " +
					"\n\n"
                    , "����", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(programInformation == object)
		{
			JOptionPane.showMessageDialog( null, "     Online Project Manager\n                      By\n" +
					"              ������б�\n       ����Ʈ���������\n            team FOREST"
                    , "���α׷� ����", JOptionPane.NO_OPTION);
		}
		else if(fileTransferButton == object)
		{
			if(jList.getSelectedValue().toString() != null)
			{
				Object[] nickName = jList.getSelectedValues();
				int result = jFileChooser.showDialog(fileTransferButton, "��������");
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
