import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Socket client;
    private Scanner scanner = null;
    String clientMsg = "";

    String serverMsg = "";
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    ImpUserServices impUserServices = new ImpUserServices();
    String selected = "";
    public ClientHandler(Socket socket) {
        this.client = socket;
        try {
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataInputStream = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scanner = new Scanner(System.in);

    }

    @Override
    public void run() {
        try {
            while (!clientMsg.equals("-")){
                ArrayList<String> clientMsgs = new ArrayList<>();
                serverMsg = "Plz select one of the following! \n 1-login \n 2-register";
                dataOutputStream.writeUTF(serverMsg);
                dataOutputStream.flush();
                selected = dataInputStream.readUTF();
                System.out.println("Client: " + selected);
                switch (selected){
                    case "1":
                        for(int i =0; i<2; i++){
                            serverMsg = "";
                            dataOutputStream.writeUTF(serverMsg);
                            dataOutputStream.flush();
                            clientMsgs.add((String) dataInputStream.readUTF());
                            System.out.println("Client: " + clientMsg);
                        }
                        serverMsg = impUserServices.login(clientMsgs.get(0), clientMsgs.get(1));
                        dataOutputStream.writeUTF(serverMsg);
                        dataOutputStream.flush();
                        break;
                    case "2":
                        for(int i =0; i<3; i++){
                            serverMsg = "";
                            dataOutputStream.writeUTF(serverMsg);
                            dataOutputStream.flush();
                            clientMsgs.add((String) dataInputStream.readUTF());
                            System.out.println("Client: " + clientMsg);
                        }
                        impUserServices.register(clientMsgs.get(0),clientMsgs.get(1),clientMsgs.get(2));
                        break;
                }

            }
            dataInputStream.close();
            dataOutputStream.close();
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
