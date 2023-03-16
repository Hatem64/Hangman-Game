import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends ImpUserServices implements Runnable{

    private String score,numOfClients;
    private String address="localhost";
    private int port=6666;
    private Socket socket = null;
    private BufferedReader reader;
    private BufferedWriter out;
    private DataInputStream input = null;


    public Client(String address, int port) throws UnknownHostException, IOException {
        try {
            socket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(String score, String numOfClients) {
        this.score = score;
        this.numOfClients = numOfClients;
    }
    ImpUserServices [] users= null;

    public void multipleConnections(int numOfClients){
        for (int i =0; i<numOfClients;i++)
        {
            try {
                socket = new Socket(address, port);
                System.out.println("User number" + i+1 + "is Connected");

                input = new DataInputStream(System.in);
                out = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));

                // get the result from the server (asking for username and password)
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(reader.readLine());


//                out.writeUTF(users[i].getUserName());

                out.write(users[i].getUserName());
                out.newLine();

//                out.writeUTF(users[i].getPassword());

                out.write(users[i].getPassword());
                out.newLine();
                out.flush();

                out.close();

            } catch (IOException u) {
                System.out.println(u);
            }
        }
    }

    public void send(String message) {
//        out.println(message)
        //3ayez akhle l function d bt3ml send ll messages
        // 3mtn anadeha bas we abaselha ayyan kan l message l client hyb3tha
    }

    public void close() {
        try {
            socket.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @Override
    public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
