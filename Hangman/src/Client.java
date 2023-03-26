import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client{
    public static void main(String[] args) {
        String address="localhost";
        int port=6666;
        Socket socket = null;
        Scanner scanner = null;
        String clientMsg = "",serverMsg = "";
        DataInputStream reader;
        DataInputStream input = null;
        String[] returnedMsg = null;
        boolean toChat = false;
        try {
            socket = new Socket(address, port);
            reader = new DataInputStream(socket.getInputStream());
            scanner = new Scanner(System.in);

            while(!clientMsg.equals("exit")){
                serverMsg = (String) reader.readUTF();
                returnedMsg = serverMsg.split(",");
                if(!serverMsg.equals(""))
                {
                    System.out.println("Server: " + returnedMsg[1]);
                } if (returnedMsg[0].equals("2") || returnedMsg[0].equals("3") || returnedMsg[0].equals("5")) {
                    continue;
                } if (returnedMsg[0].equals("4")){
                    toChat = false;
                    while (!toChat){
                        serverMsg = (String) reader.readUTF();
                        returnedMsg = serverMsg.split(",");
                        if(returnedMsg[0].equals("5") || returnedMsg[0].equals("3")){
                            System.out.println("Server: " + returnedMsg[1]);
                            break;
                        }
                    }
                    continue;
                }
                System.out.print("Client: ");
                try {
                    clientMsg = scanner.nextLine();

                }catch (Exception e){
                    System.out.println("Please enter a valid input");
                }
                send(socket, clientMsg);
            }
            close(scanner, socket, reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void send(Socket socket, String message) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void close(Scanner scanner, Socket socket, DataInputStream reader) {
        try {
            scanner.close();
            socket.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
