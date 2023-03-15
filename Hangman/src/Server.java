import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Server {


    public static void main(String[] args) throws IOException {
        String username = "";
        String password = "";
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            Socket socket = serverSocket.accept();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (true){
                dataOutputStream.writeUTF("Enter Username: ");
                username = (String) dataInputStream.readUTF();
                dataOutputStream.writeUTF("Enter Password: ");
                password = (String) dataInputStream.readUTF();
//                boolean state =  object.login(username, password);

            }
        }catch (Exception e){
            System.out.println(e);
        }

    }


}
