import java.io.*;
import java.net.*;

public class Server {
        String username = "";
        String password = "";
        private int port;
    public Server(int port) throws IOException {
        this.port = port;
    }
    public void run()
    {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            Socket socket = serverSocket.accept();
            ImpUserServices impUserServices= new ImpUserServices(socket);
            //bahbdd t2rebnn msh 3aref bas hass en ehna l mfrod nekhlyy ImpUserServices y implement Runnable :)
            new Thread((Runnable) impUserServices).start();


            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            //While loop d l mfrod t2rebn mkanha mykonsh hena bas fen ma3rafsh xD

            while (true){
                dataOutputStream.writeUTF("Enter Username: ");
                username = (String) dataInputStream.readUTF();
                dataOutputStream.writeUTF("Enter Password: ");
                password = (String) dataInputStream.readUTF();
//                boolean state =  object.login(username, password);

            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
