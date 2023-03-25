import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Socket client;
    private Scanner scanner = null;
    String functionMsg = "";
    String functionMsg2 = "";

    String serverMsg = "";
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    ImpUserServices impUserServices;
    SinglePlayer singlePlayer;
    Multiplayer multiplayer;
    String selected = "";
    String teamOption = "";

    ClientHandler player;
    String modeOption;
    Boolean isGameMaster = false;
    String gameRoomName = "";
    Team team1;
    Team team2;


    public ClientHandler(Socket socket) {
        this.client = socket;
        impUserServices = new ImpUserServices(client);
        try {
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataInputStream = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scanner = new Scanner(System.in);

    }

    public Socket getClient() {
        return client;
    }
    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    @Override
    public void run() {
        try {
            //Menu Functions will be placed here!
            functionMsg = registrationAndLoginMenu();
            if (functionMsg.equals("2")){
                functionMsg2 = gameMenu();
            }
            dataOutputStream.close();
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String registrationAndLoginMenu(){
        try {
            while (true){
                ArrayList<String> clientMsgs = new ArrayList<>();
                String[] returnedMsg = null;
                serverMsg = "1,Welcome! Please login to play or create a new user!! \n 1-Login \n 2-Register \n 3-Exit Program";
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
                        }
                        serverMsg = impUserServices.login(clientMsgs.get(0), clientMsgs.get(1));
                        returnedMsg = serverMsg.split(",");
                        if ( returnedMsg[0].equals("2")){
                            dataOutputStream.writeUTF(serverMsg);
                            dataOutputStream.flush();
                            return "2";
                        }
                        dataOutputStream.writeUTF(serverMsg);
                        dataOutputStream.flush();
                        break;
                    case "2":
                        for(int i =0; i<3; i++){
                            serverMsg = "";
                            dataOutputStream.writeUTF(serverMsg);
                            dataOutputStream.flush();
                            clientMsgs.add((String) dataInputStream.readUTF());
                        }
                        serverMsg = impUserServices.register(clientMsgs.get(0),clientMsgs.get(1),clientMsgs.get(2));
                        dataOutputStream.writeUTF(serverMsg);
                        dataOutputStream.flush();
                        break;
                    case "3":
                        System.exit(0);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public String gameMenu() {
        try {
            while (true){
                ArrayList<String> clientMsgs = new ArrayList<>();
                serverMsg = "1,Select one of following options! \n 1-Single Player \n 2-Multiplayer \n 3-Show Score History \n 4-Exit";
                sendMessage(serverMsg);
                selected = readMessage();
                System.out.println("Client: " + selected);
                switch (selected){
                    case "1":
                        singlePlayer = new SinglePlayer(clientMsgs.get(0), client);
                        singlePlayer.selectGameDifficulty();
                        break;
                    case "2":
                        multiplayer=new Multiplayer(this);
                        while (true){
                            sendMessage("1,Select one of the following options! \n 1-Create a game room \n 2-Join an existing game \n 3-back");
                            teamOption=readMessage();
                            switch (teamOption){
                                case "1":
                                    isGameMaster = true;
                                    sendMessage("1,Enter name of the game room! ;)");
                                    gameRoomName=readMessage();
                                    if(!Server.checkUniqueness(gameRoomName)){
                                        sendMessage("3,The name you entered already exists please enter another game");
                                        continue;
                                    }
                                    team1 = new Team("Team 1");
                                    team2 = new Team("Team 2");
                                    setTeam(1);
//                                multiplayer.createTeam(teamName,this);
                                    sendMessage("1,Select one of the following \n 1- 1v1 \n 2- 2v2");
                                    modeOption=readMessage();
                                    switch (modeOption){
                                        case "1":
                                            multiplayer.createGameRoom(team1, team2, gameRoomName, 1);
                                            multiplayer.gameMenu();
//                                            return "2";
                                        //They should play 1v1
//                                    Server.add();
                                        //multiplayer.startGame(team1,team2,"1")
                                            break;
                                        case "2":
                                            //    2 v 2
                                            break;
                                        default:
                                            sendMessage("3,Invalid option.");
                                    }
//                                    break;
                                case "2":
                                    multiplayer=new Multiplayer(this);
                                    ArrayList<ClientHandler> gameMasters = Server.getGameMasters();
                                    for (int i = 0; i< gameMasters.size(); i++){
                                        sendMessage("3, Room "+(i+1)+": "+gameMasters.get(i).getGameRoomName());
                                    }
                                    sendMessage("1, select one of the above rooms to join!");
                                    selected = readMessage();
                                    if(Integer.parseInt(selected) > gameMasters.size()) {
                                        sendMessage("3,Please choose one of the rooms!!");
                                        continue;
                                    }
                                    gameMasters.get(Integer.parseInt(selected)-1).multiplayer.joinGame(this, multiplayer);
                                    break;
                                case "3":
                                    break;
                                default:
                                    sendMessage("3,Invalid option.");
                            }
                        }
//                        break;
                    case "3":
                        //show the score history of this player
                        for(int i =0; i<3; i++){
                            serverMsg = "";
                            dataOutputStream.writeUTF(serverMsg);
                            dataOutputStream.flush();
                            clientMsgs.add((String) dataInputStream.readUTF());
//                            System.out.println("Client: " + clientMsg);
                        }
                        serverMsg = impUserServices.register(clientMsgs.get(0),clientMsgs.get(1),clientMsgs.get(2));
                        dataOutputStream.writeUTF(serverMsg);
                        dataOutputStream.flush();
                        break;
                    case "4":
                        //Exit Case
                        return registrationAndLoginMenu();
                    default:
                        sendMessage("3,Please enter one of the options!");
                }

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendMessage(String message) {
        try {
            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readMessage() throws IOException {
        String message = null;
        if (dataInputStream != null) {
            message = dataInputStream.readUTF();
        }
        return message;
    }
    public void setTeam(int num){
        if(num == 1){
            team1.addPlayer(this);
        }else
            team2.addPlayer(this);
    }
    public String getGameRoomName(){
        return gameRoomName;
    }
    public ImpUserServices getImpUserServices() {
        return impUserServices;
    }
    public boolean isGameMaster(){
        return isGameMaster;
    }
}
