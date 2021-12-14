package DataBase;

import java.sql.*;

public class DataBase {

    private static DataBase instance;

    public static DataBase getInstance() {
        if(instance == null){
            instance = new DataBase();
        }
        return instance;
    }

    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/test";
    private static final String DBUSER = "root";
    private static final String DBPWD = "lyx020411";

    private static Connection con;
    private static Statement statement;
    private static ResultSet read;
    private static PreparedStatement write;

    /**
     * 用户注册验证
     * @param username 用户名
     * @param password 密码hash
     * @return 是否注册成功
     */
    public boolean registerVerify(String username, String password) {
        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(DBURL,DBUSER,DBPWD);
            statement = con.createStatement();
            read = statement.executeQuery("select * from user");
            while(read.next()){
                String existUser = read.getString("username");
                if(existUser.equals(username)){
                    return false;
                }
            }
            // 在数据库中并没有发现该用户，将新用户添加到数据库中
            write = con.prepareStatement("insert into user (username,password) values(?,?)");
            write.setString(1,username);
            write.setString(2,password);
            write.executeUpdate();
            System.out.println("向数据库中添加新用户: 用户名:"+username+" 密码:"+password);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码hash
     * @return 是否登录成功
     */
    public boolean loginVerify(String username, String password) {
        try {
            Class.forName(DRIVER_NAME);
            con = DriverManager.getConnection(DBURL,DBUSER,DBPWD);
            statement = con.createStatement();
            read = statement.executeQuery("select * from user");
            while(read.next()){
                String existUser = read.getString("username");
                String existPassword = read.getString("password");
                if((existUser.equals(username))&&(existPassword.equals(password))){
                    return true;
                }
            }
            return false;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
