import java.io.FileNotFoundException;
import java.io.IOException;

public interface UserServices {
    public String register(String name,String userName,String password) throws IOException;
    public String login(String userName,String password);
}

