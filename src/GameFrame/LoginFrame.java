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
    private static JTextField userPasswordTextField;
    private static JButton loginButton;
    private static JButton registerButton;

    private LoginFrame() throws HeadlessException {

        super();

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
        userPasswordTextField = new JTextField();
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

    private void initActionListener() {
        loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameLabel.getText();
                String password = userPasswordTextField.getText();
                if(userVerify(userName, password)){
                    ClientPlayer.getInstance().setUserName(userName);
                    ClientPlayer.getInstance().enterGameLobby();
                }
            }
        });
    }
    private String getClientName() {
        return userNameTextField.getText();
    }

    private boolean userVerify(String userName, String password) {
        return true;
    }


}

