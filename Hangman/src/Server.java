import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        String username = "";
        String password = "";
        try{
            ServerSocket serverSocket = new ServerSocket(6666);
            while (true){
                Socket client = serverSocket.accept();
                ImpUserServices impUserServices= new ImpUserServices();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(client);
                new Thread(clientHandler).start();
                //list ->
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

//    private static class GameServer implements Runnable {
//
//        //dah hy2sm el servers le 3dd el clients
//        //lo single player hyfdl 3la el server bta3o
//        //lo multiplayer hy2dr y2ma y3ml room 3la el server dah, ya hy3ml join le server tany
//        //m4 3arf lessa hal 23ml function bta5od clients tanyeen 2zay in case of multiplayer...
//        //y3ny hal 22dr 25ly function bt3ml accept lel clients el tanyeen? wla el function deh bta5od array?
//        //ana h4oof 2wl wa7da.
//
////    private int port;
////    public GameServer(int port) throws IOException {
////        this.port = port;
////    }
//
//
//
//        public void run()
//        {
//
//        }
//
//
//    }

}


