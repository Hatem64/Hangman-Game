import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    Socket client;
    Scanner scanner = null;
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
    String modeOption;
    boolean isGameMaster = false;
    boolean playerTurn = false;

    String gameRoomName = "";

    ArrayList<String> clientMsgs;

//    private Team team;
    Team team1;
    Team team2;

    ClientHandler clientHandler;
    int menuToRun;

    public ClientHandler(ClientHandler clientHandler, int menuToRun) {
        this.client = clientHandler.client;
        this.scanner = clientHandler.scanner;
        this.functionMsg = clientHandler.functionMsg;
        this.functionMsg2 = clientHandler.functionMsg2;
        this.serverMsg = clientHandler.serverMsg;
        this.dataOutputStream = clientHandler.dataOutputStream;
        this.dataInputStream = clientHandler.dataInputStream;
        this.impUserServices = clientHandler.impUserServices;
        this.singlePlayer = clientHandler.singlePlayer;
        this.multiplayer = clientHandler.multiplayer;
        this.selected = clientHandler.selected;
        this.teamOption = clientHandler.teamOption;
        this.modeOption = clientHandler.modeOption;
        this.isGameMaster = clientHandler.isGameMaster;
        this.playerTurn = clientHandler.playerTurn;
        this.gameRoomName = clientHandler.gameRoomName;
        this.clientMsgs = clientHandler.clientMsgs;
        this.team1 = clientHandler.team1;
        this.team2 = clientHandler.team2;
        this.menuToRun = menuToRun;
    }

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
        this.menuToRun = 1;

    }

    public Socket getClient() {
        return client;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    @Override
    public void run() {
        try {
            if (menuToRun == 1){
                functionMsg = registrationAndLoginMenu();
                if (functionMsg.equals("2")){
                    functionMsg2 = gameMenu();
                }
//            if (functionMsg2.equals("2")){
//
//            }
            }else {
                functionMsg = gameMenu();
//            if (functionMsg2.equals("2")){
//
//            }
            }
            dataOutputStream.close();
            scanner.close();
            //Menu Functions will be placed here!

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String registrationAndLoginMenu(){
        try {
            while (true){
                clientMsgs = new ArrayList<>();
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
//                            System.out.println("Client: " + clientMsg);
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
                String[] returnedMsg = null;
                serverMsg = "1,Select one of following options! \n 1-Single Player \n 2-Multiplayer \n 3-Show Score History \n 4-Exit";
                sendMessage(serverMsg);
                selected = readMessage();
                System.out.println("Client: " + selected);
                switch (selected){
                    case "1":
                        singlePlayer = new SinglePlayer(clientMsgs.get(0), this);
                        singlePlayer.selectGameDifficulty();
                        break;
                    case "2":
                        multiplayer=new Multiplayer(this);
                        Server.reloadGameMasters();
                        while (true){
                            sendMessage("1,Select one of the following options! \n 1-Create a game room \n 2-Join an existing game \n 3-back");
                            teamOption=readMessage();
                            switch (teamOption){
                                case "1":
                                    isGameMaster = true;
                                    sendMessage("1,Enter name of the game room! ;)");
                                    gameRoomName=readMessage();
                                    if(gameRoomName.equals("-"))
                                        continue;
                                    if(!Server.checkUniqueness(gameRoomName)){
                                        sendMessage("3,The name you entered already exists please enter another game");
                                        continue;
                                    }
                                    team1 = new Team("Team 1");
                                    team2 = new Team("Team 2");
                                    setTeam(1);
                                    sendMessage("1,Select one of the following \n 1- 1v1 \n 2- 2v2");
                                    modeOption=readMessage();
                                    switch (modeOption){
                                        case "1":
                                            multiplayer.createGameRoom(team1, team2, gameRoomName, 1);
                                            multiplayer.gameMenu();

                                            break;
                                        case "2":
                                            multiplayer.createGameRoom(team1, team2, gameRoomName, 2);
                                            multiplayer.gameMenu();
                                            break;
                                        default:
                                            sendMessage("3,Invalid option.");
                                    }
                                    break;
                                case "2":
                                    ArrayList<ClientHandler> gameMasters = Server.getGameMasters();
                                    if(gameMasters.size() == 0){
                                        sendMessage("3,Sorry there's no available rooms atm. \nPlease wait for a game master to create one!");
                                        continue;
                                    }
                                    for (int i = 0; i< gameMasters.size(); i++){
                                        sendMessage("3,Room "+(i+1)+": "+gameMasters.get(i).getGameRoomName());
                                    }
                                    sendMessage("1, Select one of the above rooms to join!");
                                    selected = readMessage();
                                    if(Integer.parseInt(selected) > gameMasters.size()) {
                                        sendMessage("3,Please choose one of the rooms!!");
                                        continue;
                                    }
                                    gameMasters.get(Integer.parseInt(selected)-1).multiplayer.joinGame(this);
                                    break;
                                case "3":
                                    gameMenu();
                                    break;
                                default:
                                    sendMessage("3,Invalid option.");
                            }
                        }
                    case "3":
                        sendMessage("3,The scores of the previous games: ");
                        sendMessage("3,"+getImpUserServices().getScores());
                        break;
                    case "4":
                        return "exit";
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
