package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import CLI.CLI;
import GUI.GUI;

public class Server {

    Socket socket = null;//socket对象
    String portNumber;//端口号
    private boolean running = false;//标记服务器状态，初始化为关闭
    public static GUI ui;//GUI对象
    static String[][] sql = {
        {"gzb", "111111"},
        {"yjh", "222222"},
        {"wq", "333333"}
    };//允许哪些用户登录

    public static boolean IF_LEGAL(String name, String password){//用户合法验证
        for (String[] entry : sql) {
            String storedName = entry[0];
            String storedPassword = entry[1];

            if (name.equals(storedName) && password.equals(storedPassword)) {
                return true;
            }
        }
        return false;
    }
    //以下均为静态变量，允许在整个类中共享
    private static final List<ClientHandlerThread> allConnectedClients = new ArrayList<>();//客户端处理线程列表
    //使用 synchronized 关键字，解决进程对资源的共享问题
    synchronized public static void addClient(ClientHandlerThread client) { allConnectedClients.add(client); }//添加新的客户端处理线程
    synchronized public static void removeClient(ClientHandlerThread client) { allConnectedClients.remove(client); }//移除已有的客户端处理线程
    synchronized public static void broadcastMessage (String msg) {//向每个客户端发送消息
        for (ClientHandlerThread client : allConnectedClients) {
            try {
                client.output.writeUTF(msg);
                if (msg.startsWith("Server -> remove ")) {
                    String disconnectName = msg.substring(17); // 获取消息中 "disconnect " 后的部分
                    client.output.writeUTF(disconnectName+" has been removed");
                    if (client.name.equals(disconnectName)) {
                        allConnectedClients.remove(client); 
                        client.output.close();
                        client.input.close();
                        client.socket.close();
                    }
                }
            } catch (IOException e) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    synchronized public static int getTotalActiveClients () { return allConnectedClients.size(); }//统计客户端个数
    
    public Server() {//构造函数，允许类外访问
        ui = new GUI("Server");//初始化GUI对象

        portNumber = ui.read("Enter the Port Number where server should run", "3000");//用户输入端口号

        this.running = true;//标记服务器开启
        this.startAcceptingConnections();//开始接受客户端连接
    }

    private void startAcceptingConnections () {
        int port = Integer.parseInt(this.portNumber);
        try {
            ServerSocket ss = new ServerSocket(port);
            ui.write("Server listening on port: " + port);

            // 开线程处理接受消息输入并输出到各个客户端
            Thread serverInputHandlerThread = new Thread(new ServerInputHandlerThread());
            serverInputHandlerThread.setName("server-stdin-listener-thread");
            serverInputHandlerThread.start();

            while (this.running) {

                socket = ss.accept();
                // 开线程处理接收来自各个客户端的消息
                Thread clientHandlerThread = new Thread(new ClientHandlerThread(socket));
                clientHandlerThread.setName("client-thread-" + getTotalActiveClients());
                clientHandlerThread.start();

            }
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        } catch ( NoSuchElementException e) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Server Stdin Closed !");
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
