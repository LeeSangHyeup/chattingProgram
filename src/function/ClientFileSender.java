package function;

import java.io.*;
import java.net.Socket;

import javax.swing.JOptionPane;
 


public class ClientFileSender{
    public ClientFileSender(String receiverIP, File file) {
        Socket socket = null;
 
        try {
            // ���� ����
            socket = new Socket(receiverIP, 9002);
            FileSender fs = new FileSender(receiverIP, file,socket);
            fs.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 
class FileSender extends Thread {
    Socket socket;
    DataOutputStream dos;
    FileInputStream fis;
    BufferedInputStream bis;
    File file;
    String receiverIP;
 
    public FileSender(String receiverIP,File file, Socket socket) {
        this.socket = socket;
        this.file = file;
        this.receiverIP = receiverIP;
        try {
            // ������ ���ۿ� ��Ʈ�� ����
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    @Override
    public void run() {
        try {
            String fName = file.getName();
            dos.writeUTF(fName);
 
            // ���� ������ �����鼭 ����
            File f = new File(file.getPath());
            fis = new FileInputStream(f);
            bis = new BufferedInputStream(fis);
 
            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = bis.read(data)) != -1) {
                dos.write(data, 0, len);
            }
 
            dos.flush();
            dos.close();
            bis.close();
            fis.close();
            JOptionPane.showMessageDialog( null, "���� ���� �۾��� �Ϸ��Ͽ����ϴ�.\n"+"���ϸ�: "+fName+
            		"\n���� ������ ������ : " + f.length()+"  bytes", "�������ۿϷ�", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
