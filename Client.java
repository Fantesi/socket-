package Client;

import GUI.GUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static Socket socket = null;//静态变量socket
    String ipAddress;//目标服务器的IP地址
    String portNumber;//目标端口号
    public static DataInputStream inputStream;//静态变量（服务器）输入流
    public static DataOutputStream outputStream;//静态变量输出（到客户端）流
    public static GUI ui;//静态变量GUI
    public static String name;//静态变量用户名
    public static String password;//静态变量密码
    public Client () {//构造函数，初始化相关变量
        ui = new GUI("Client");
        this.init();
        this.startCommunication();
    }

    private void init() {
        ipAddress = ui.read("Input IP Address of Server", "localhost");
        portNumber = ui.read("Input Port Number of Server", "3000");
        name = ui.read("Enter your name :");
        password = ui.read("Enter your password :");

        try {
            socket = new Socket(ipAddress, Integer.parseInt(portNumber));//通过ip和端口号初始化socket
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(name);//告知服务器用户名
            outputStream.writeUTF(password);//告知服务器密码
            
            if(inputStream.readUTF().equals("CONNECT SUCCESSFUL")){
                ui.write("Client Connected with Server on "+ipAddress+":"+portNumber);//连接成功显示相关信息

            }
            
        } catch (IOException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
            ui.write(e.getMessage());
        }
    }

    private void startCommunication() {
        Thread readFromServer = new Thread(new ReadFromServer());//创建从服务器读取数据的线程readFromServer
        Thread writeToServer = new Thread(new WriteToServer());//创建向服务器写入数据的线程writeToServer
        //开启线程
        readFromServer.start();
        writeToServer.start();

        try {
            // 阻塞当前线程，等待readFromServer和writeToServer执行完毕
            readFromServer.join();
            writeToServer.join();
        } catch (InterruptedException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }

        // 在所有线程都执行完毕后，关闭相关的资源，确保它们被正确释放
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
