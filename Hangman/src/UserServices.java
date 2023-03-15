import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserServices {
    private String name;
    private String userName;
    private String password;

    public UserServices() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void register(String name,String userName,String password){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("RegisteredUsers.txt",true));)
        {
            bw.write(name + "," + userName + "," + password);
            bw.newLine();
            System.out.println("User registered successfully!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
