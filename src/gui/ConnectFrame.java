package gui;

import javax.swing.*;

import org.omg.CORBA.portable.InputStream;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

import network.Client;
import network.Server;

public class ConnectFrame extends JFrame implements ActionListener
{
	private Client client;
	private GridBagLayout gridBagLayout = new GridBagLayout();

	private JTextField nickNameTextField = new JTextField(6);
	private JTextField serverIPTextField = new JTextField(20);
	private JTextField serverPortTextField = new JTextField(5);
	
	private JLabel nickName = new JLabel("닉네임");
	private JLabel serverIP = new JLabel("서버IP");
	private JLabel serverPort = new JLabel("서버포트");
	
	private JButton connectServerButton = new JButton("서버접속");
	private JButton createServerButton = new JButton("서버생성");
	
	private Server server;
	public ConnectFrame()
	{
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(res.width/2.8), (int)(res.height/2.8));
		this.setLayout(gridBagLayout);
		this.setTitle("Connect OPM");
		
		this.insert(nickName,0,0,1,1);
		this.insert(nickNameTextField,1,0,1,1);
		
		this.insert(serverIP,0,1,1,1);
		this.insert(serverIPTextField,1,1,2,1);
		this.insert(connectServerButton,4,1,1,1);
		
		this.insert(serverPort,0,3,1,1);
		this.insert(serverPortTextField,1,3,1,1);
		
		this.insert(createServerButton,4,3,1,1);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
		
		connectServerButton.addActionListener(this);
		createServerButton.addActionListener(this);
		
		this.addWindowListener(new WindowAdapter()
		{	
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
	
	//GridBagLayout에 컴포넌트 삽입을 쉽게 하기위한 메소드
    private void insert(Component cmpt, int x, int y, int w, int h) { 
        GridBagConstraints gbc = new GridBagConstraints();  
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
		if(createServerButton == object )
		{
			try
			{
				server= new Server(serverPortTextField.getText());
				Thread thread = new Thread(server);
				thread.start();
				client = new Client("127.0.0.1", serverPortTextField.getText());
				String nickName = nickNameTextField.getText();
				int checkFlag = -1;
				checkFlag = nickName.indexOf("/");
				checkFlag = nickName.indexOf("@");
				if(-1 != checkFlag)
				{
					JOptionPane.showMessageDialog( null, "닉네임에는 '/'와 '@'를 사용할 수 없습니다.", "사용할 수 없는 문자", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					new ProjectFrame(nickName, getPublicServerIP(), getLocalServerIp(), serverPortTextField.getText(), client.getSocket(), server.getArrayList());
					this.setVisible(false);
				}
			}
			catch(NumberFormatException E)
			{
				JOptionPane.showMessageDialog( null, "서버포트값을 확인하세요.", "서버생성 실패", JOptionPane.ERROR_MESSAGE);
			}
			catch(IllegalArgumentException IE)
			{
				JOptionPane.showMessageDialog( null, "서버포트값을 확인하세요.", "서버생성 실패", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(connectServerButton == object )
		{
			try
			{
				client = new Client(serverIPTextField.getText(), serverPortTextField.getText());
				this.setVisible(false);
				new ProjectFrame(nickNameTextField.getText(), serverIPTextField.getText(), serverPortTextField.getText(), client.getSocket(), null);
			}
			catch(NumberFormatException E)
			{
				JOptionPane.showMessageDialog( null, "IP주소 또는 서버포트값을 확인하세요.", "서버접속 실패", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private String getLocalServerIp() 
	{ 
	        try 
	        { 
	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
	            { 
	                NetworkInterface intf = en.nextElement(); 
	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
	                { 
	                    InetAddress inetAddress = enumIpAddr.nextElement(); 
	                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) 
	                    { 
	                        return inetAddress.getHostAddress().toString(); 
	                    } 
	                } 
	            } 
	        } 
	        catch (SocketException ex) {} 
	        return null; 
	}
	private String getPublicServerIP()
	{

		URL url = null;
		BufferedReader in = null;
		String publicIP = null;
		try {
			url = new URL("http://gt-tests.appspot.com/ip");
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			publicIP = in.readLine();
		}
		catch (MalformedURLException e) 
		{
			JOptionPane.showMessageDialog( null, "url = new URL(\"http://gt-tests.appspot.com/ip\");실패", "공인아이피 획득 실패", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog( null, "in = new BufferedReader(new InputStreamReader(url.openStream()));\n " +
					"publicIP = in.readLine(); 실패", "공인아이피 획득 실패", JOptionPane.ERROR_MESSAGE);
		}
		return publicIP;
	}
}
