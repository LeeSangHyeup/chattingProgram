package network;

import java.io.PrintWriter;

import javax.swing.JOptionPane;


public class User {
	private String nickName;
	private String ip;
	private PrintWriter printWriter;
	
	public User (PrintWriter printWriter, String ip)
	{
		this.printWriter = printWriter;
		this.ip = ip;
	}
	public User(String nickName, String ip)
	{
		this.ip = ip;
		this.nickName = nickName;
	}
	
	public PrintWriter getPrintWriter()
	{
		return printWriter;
	}
	public void finalize()
	{
		printWriter.close();
	}
	public void setNickName(String string)
	{
		this.nickName = string;
	}
	public void setIp(String string)
	{
		this.ip = string;
	}
	public String getNickName()
	{
		return nickName;
	}
	public String getIp()
	{
		return this.ip;
	}
}
