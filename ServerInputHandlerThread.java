package Server;

import java.awt.event.ActionEvent;

public record ServerInputHandlerThread() implements Runnable {//管理员输入命令并将消息广播给所有客户端，Runable接口允许独立线程执行

    @Override
    public void run() {

        Server.ui.setSendBtnActionListener(this::sendButtonActionPerformed);//设置服务器界面 (Server.ui) 的 "Send" 按钮的动作监听器
                                                                            //当用户点击按钮时执行 sendButtonActionPerformed 方法。

    }

    private void sendButtonActionPerformed(ActionEvent evt) {
        String str = Server.ui.getInputTextAndClear();//获取输入文本框中文本内容后清除文本框中的内容
        if (!str.trim().isEmpty()) {//trim() 方法，去除字符串前后的空格，如果文本不为空则发送到各个客户端
            Server.broadcastMessage("Server -> " + str.trim());
        }
    }
}
