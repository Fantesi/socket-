package GUI;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;

public class GUI {//创建用户界面以及与用户界面的交互

    String name;//界面名称
    MyFrame frame;//界面主窗口

    public String getInputTextAndClear() {//返回文本框文本后清空文本
        String str = frame.sendArea.getText();
        frame.sendArea.setText("");
        return str;
    }

    public void setSendBtnActionListener(ActionListener listener) {//为界面的 "Send" 按钮添加动作监听器
        frame.sendButton.addActionListener(listener);
    }

    public GUI (String name) {//构造函数,初始化界面
        this.name = name;
        frame = new MyFrame(name);
    }

    public static int selectClientOrServer() {//选择服务端（返回0）还是客户端（返回1），使用了 Java Swing 库中的 JOptionPane 类
        String[] options = {"Server", "Client"};
        return JOptionPane.showOptionDialog(null, "Run Client or Server ?", "Java Socket Programming", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);
    }

    public String read(String msg) throws NoSuchElementException {//读取用户输入（有提示消息）
        if (msg.isEmpty()) return this.read();//如果 msg （对话框中显示的提示消息）参数为空，递归调用 read() 方法，以便用户再次输入信息
        String option = "";
        while (option.isEmpty()) {
            option = JOptionPane.showInputDialog(null, msg);
        }
        return option;
    }

    public String read() throws NoSuchElementException {//读取用户输入（无提示消息）
        return null;
    }

    public String read(String msg, String defaultValue) throws NoSuchElementException {//读取用户输入（有提示消息，有默认值）
        String a = JOptionPane.showInputDialog(null, msg, defaultValue);
        if (a == null) System.exit(0);
        return a;
    }

    public void write(String msg) {//显示msg到界面的消息区域
        frame.convoArea.append(msg+"\n");
    }
}
