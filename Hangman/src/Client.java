import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client{
    public static void main(String[] args) {
        String score,numOfClients;
        String address="localhost";
        int port=6666;
        Socket socket = null;
        Scanner scanner = null;
        String clientMsg = "";
        String serverMsg = "";
        DataInputStream reader;
        DataInputStream input = null;
        String[] returnedMsg = null;

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
                } if (returnedMsg[0].equals("2") || returnedMsg[0].equals("3")) {
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


//    public Client(String score, String numOfClients) {
//        this.score = score;
//        this.numOfClients = numOfClients;
//    }
//    ImpUserServices [] users= null;

//    public void multipleConnections(int numOfClients){
//        for (int i =0; i<numOfClients;i++)
//        {
//            try {
//                socket = new Socket(address, port);
//                System.out.println("User number" + i+1 + "is Connected");
//
//                input = new DataInputStream(System.in);
//                out = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
//
//                // get the result from the server (asking for username and password)
//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                System.out.println(reader.readLine());
//
//
////                out.writeUTF(users[i].getUserName());
//
//                out.write(users[i].getUserName());
//                out.newLine();
//
////                out.writeUTF(users[i].getPassword());
//
//                out.write(users[i].getPassword());
//                out.newLine();
//                out.flush();
//
//                out.close();
//
//            } catch (IOException u) {
//                System.out.println(u);
//            }
//        }
//    }



//        @Override
//    public void run() {
//            try {
//                String message;
//                while ((message = reader.readLine()) != null) {
//                    System.out.println("Received message: " + message);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//    }
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
