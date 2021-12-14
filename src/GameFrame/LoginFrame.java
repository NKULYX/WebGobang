package GameFrame;

import Client.ClientPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private static LoginFrame instance;

    public static LoginFrame getInstance() {
        if(instance==null){
            instance = new LoginFrame();
        }
        return instance;
    }

    private static JPanel userInputFrame;
    private static JPanel buttonFrame;
    private static JLabel userNameLabel;
    private static JLabel userPasswordLabel;
    private static JTextField userNameTextField;
    private static JPasswordField userPasswordTextField;
    private static JButton loginButton;
    private static JButton registerButton;

    private LoginFrame() throws HeadlessException {

        this.setTitle("登录");
        this.setLocation(500,200);
        this.setSize(new Dimension(300, 300));
        this.setIconImage(new ImageIcon("image/logo.jpg").getImage());
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        userInputFrame = new JPanel();
        buttonFrame = new JPanel();
        userNameLabel = new JLabel("用户名");
        userNameLabel.setPreferredSize(new Dimension(75,50));
        userPasswordLabel = new JLabel("密码");
        userPasswordLabel.setPreferredSize(new Dimension(75,50));
        userNameTextField = new JTextField();
        userNameTextField.setPreferredSize(new Dimension(150,50));
        userPasswordTextField = new JPasswordField();
        userPasswordTextField.setPreferredSize(new Dimension(150,50));

        userInputFrame.setLayout(new GridLayout(2,1));
        JPanel tmp1 = new JPanel();
        tmp1.add(userNameLabel);
        tmp1.add(userNameTextField);
        userInputFrame.add(tmp1);
        JPanel tmp2 = new JPanel();
        tmp2.add(userPasswordLabel);
        tmp2.add(userPasswordTextField);
        userInputFrame.add(tmp2);

        loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(100,50));
        registerButton = new JButton("注册");
        registerButton.setPreferredSize(new Dimension(100,50));

        buttonFrame.add(loginButton);
        buttonFrame.add(registerButton);

        this.getContentPane().add(userInputFrame,BorderLayout.CENTER);
        this.getContentPane().add(buttonFrame, BorderLayout.SOUTH);
        this.setVisible(true);

        initActionListener();
    }

    /**
     * 初始化事件监听
     */
    private void initActionListener() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameTextField.getText();
                String password = userPasswordTextField.getText();
                // 如果输入为空
                if(userName.length()==0||password.length()==0){
                    JOptionPane.showMessageDialog(null, "用户名或密码为空","登录",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // 如果输入不为空，则要登录认证
                if(userLoginVerify(userName, password)){
                    ClientPlayer.getInstance().setUserName(userName);
                    ClientPlayer.getInstance().enterGameLobby();
                    LoginFrame.getInstance().setVisible(false);
                } else{
                    JOptionPane.showMessageDialog(null, "用户名或密码错误！","验证失败",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameTextField.getText();
                String password = userPasswordTextField.getText();
                // 如果输入为空
                if(userName.length()==0||password.length()==0){
                    JOptionPane.showMessageDialog(null, "用户名或密码为空","注册",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // 如果输入不为空，则要验证是否注册过
                if(userRegisterVerify(userName,password)){
                    int option = JOptionPane.showConfirmDialog(null, "注册成功！\n是否登录","注册成功",JOptionPane.OK_CANCEL_OPTION);
                    // 如果选择登陆游戏
                    if(option == 0){
                        ClientPlayer.getInstance().setUserName(userName);
                        ClientPlayer.getInstance().enterGameLobby();
                        LoginFrame.getInstance().setVisible(false);
                    }
                    // 如果不选择登陆游戏
                    else{
                        userNameTextField.setText(null);
                        userPasswordTextField.setText(null);
                    }
                } else{
                    JOptionPane.showMessageDialog(null, "用户名已存在！","验证失败",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 用户注册验证
     * @param userName
     * @param password
     * @return 验证是否通过
     */
    private boolean userRegisterVerify(String userName, String password) {
        String hashpwd = Integer.toString(password.hashCode());
        return ClientPlayer.getInstance().registerVerify(userName,hashpwd);
    }

    /**
     * 用户登录验证
     * @param userName 用户名
     * @param password 密码
     * @return 返回是否通过验证
     */
    private boolean userLoginVerify(String userName, String password) {
        String hashpwd = Integer.toString(password.hashCode());
        return ClientPlayer.getInstance().loginVerify(userName,hashpwd);
    }

}

