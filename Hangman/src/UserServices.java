import java.io.FileNotFoundException;
import java.io.IOException;

public interface UserServices {
    public void register(String name,String userName,String password) throws IOException;
    public boolean login(String userName,String password);
}
