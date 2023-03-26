import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static volatile ArrayList<ClientHandler> loggedInPlayers = new ArrayList<>();
    private static ArrayList<ClientHandler> masters = new ArrayList<>();

    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(6666);
            while (true){
                Socket client = serverSocket.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(client);
                addPlayer(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static void addPlayer(ClientHandler player){
        loggedInPlayers.add(player);
    }

    public static boolean checkLogged(String name){
        for(int i=0; i<loggedInPlayers.size(); i++){
            System.out.println(loggedInPlayers.get(i).getImpUserServices().getUserName());
            if (loggedInPlayers.get(i).getImpUserServices().getUserName().equals(name))
                return loggedInPlayers.get(i).getImpUserServices().isLoggedIn();
        }
        return false;
    }
    public static ArrayList<ClientHandler> getGameMasters(){
        for(int i=0; i<loggedInPlayers.size(); i++){
            if(loggedInPlayers.get(i).isGameMaster()){
                if (!masters.contains(loggedInPlayers.get(i))){
                    masters.add(loggedInPlayers.get(i));
                }
            }
        }
        return masters;
    }

    public static void reloadGameMasters(){
        for(int i=0; i<loggedInPlayers.size(); i++){
            if(loggedInPlayers.get(i).isGameMaster()){
                if (!masters.contains(loggedInPlayers.get(i))){
                    masters.add(loggedInPlayers.get(i));
                }
            }
        }
    }

    public static boolean checkUniqueness(String name){
        ArrayList<ClientHandler> temp = masters;
        for(ClientHandler client : temp){
            if(client.getGameRoomName().equals(name))
                return false;
        }
        return true;
    }

    public static void createNewThread(ClientHandler clientHandler2){
        ClientHandler clientHandler = new ClientHandler(clientHandler2, 2);
        new Thread(clientHandler).start();
    }
}


