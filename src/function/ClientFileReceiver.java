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
 
            // 파일명을 전송 받고 파일명 수정.
            String fName = dis.readUTF();
	        if(JOptionPane.showConfirmDialog(null, fName+"파일의 전송요청이 있습니다.\n다운받으시겠습니까?","파일전송요청",
	        		JOptionPane.YES_NO_OPTION) !=0 ) return;
	        
            fName = fName.replaceAll("a", "b");
 
            // 파일을 생성하고 파일에 대한 출력 스트림 생성
            File f = new File(fName);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
 
            // 바이트 데이터를 전송받으면서 기록
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
            JOptionPane.showMessageDialog( null, "파일 전송 작업을 완료하였습니다.\n"+"파일명: "+fName+
            		"\n받은 파일의 사이즈 : " + f.length()+"bytes", "파일전송완료", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
