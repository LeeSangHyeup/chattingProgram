package function;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClientFileReceiver extends Thread{
	ServerSocket serverSocket=null;
	Socket socket=null;
    public ClientFileReceiver() 
    {
    	try 
    	{
			serverSocket = new ServerSocket(9002);
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    }
    public void run()
    {
    	while(true)
    	{
			try 
			{
				socket = serverSocket.accept();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
	        FileReceiver fr = new FileReceiver(socket);
	        fr.start();
    	}
    }
}
 
class FileReceiver extends Thread 
{
    Socket socket;
    DataInputStream dis;
    FileOutputStream fos;
    BufferedOutputStream bos;
 
    public FileReceiver(Socket socket) {
        this.socket = socket;
    }
 
    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
 
            // ���ϸ��� ���� �ް� ���ϸ� ����.
            String fName = dis.readUTF();
	        if(JOptionPane.showConfirmDialog(null, fName+"������ ���ۿ�û�� �ֽ��ϴ�.\n�ٿ�����ðڽ��ϱ�?","�������ۿ�û",
	        		JOptionPane.YES_NO_OPTION) !=0 ) return;
	        
            fName = fName.replaceAll("a", "b");
 
            // ������ �����ϰ� ���Ͽ� ���� ��� ��Ʈ�� ����
            File f = new File(fName);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
 
            // ����Ʈ �����͸� ���۹����鼭 ���
            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = dis.read(data)) != -1) {
                bos.write(data, 0, len);
            }
 
            bos.flush();
            bos.close();
            fos.close();
            dis.close();
            JOptionPane.showMessageDialog( null, "���� ���� �۾��� �Ϸ��Ͽ����ϴ�.\n"+"���ϸ�: "+fName+
            		"\n���� ������ ������ : " + f.length()+"bytes", "�������ۿϷ�", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
