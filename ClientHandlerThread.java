package Server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandlerThread implements Runnable {//Runnable接口，处理连接到服务器的客户端的通信，包括状态监听和多客户聊天

    Socket socket;//开socket
    DataInputStream input;//（客户端）输入流
    DataOutputStream output;//输出（到服务端）流
    String name;//存用户名称
    String password;//存用户密码
    boolean iflegal = false ;
    public ClientHandlerThread (Socket socket) {//构造函数，接受Server已开启的socket对象初始化ClientHandlerThread实例
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());

            this.name = input.readUTF().trim();//读取用户名称
            this.password = input.readUTF().trim();//读取用户密码
            iflegal = Server.IF_LEGAL(this.name, this.password);//合法性验证
            if(iflegal){
                Server.addClient(this);//添加到服务端的处理客户端线程列表中
                Server.ui.write("Connected" + " -> " + name + ", Active Clients : " + Server.getTotalActiveClients());//告知管理员客户端连接状态
                output.writeUTF("CONNECT SUCCESFUL");
                Server.broadcastMessage("Connected" + " -> " + name + ", Active Clients -> " + Server.getTotalActiveClients());//告知所有客户端连接状态

                String cause = "";
                // Read all messages from client and respond
                try {
                    String inStr = "";
                    while (true) {//无限循环，随时处理客户端的消息传输
                        inStr = input.readUTF();
                        Server.ui.write(inStr);//先发给服务端
                        Server.broadcastMessage(inStr);//再群发
                    }
                } catch (IOException e) {
                    cause = e.getMessage();
                    if (!e.getMessage().equals("Connection reset")) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
                    }
                }

                //客户端关闭连接的监听和相关资源的清理
                Server.removeClient(this);
                input.close();
                output.close();
                socket.close();
                Server.ui.write("Disconnected" + " -> " + name + ", Cause -> " + cause + ", Active Clients -> " + Server.getTotalActiveClients());
                Server.broadcastMessage("Disconnected" + " -> " + name + ", Cause -> " + cause + ", Active Clients -> " + Server.getTotalActiveClients());
            }
            } catch (IOException e) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
            }
    }
}

