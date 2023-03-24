import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Socket client;
    private Scanner scanner = null;
//    String clientMsg = "";
    String functionMsg = "";
    String functionMsg2 = "";

    String serverMsg = "";
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    ImpUserServices impUserServices;
    SinglePlayer singlePlayer;
    Multiplayer multiplayer;
    String selected = "";

    ClientHandler player;
    String teamName;
    String modeOption;
    Boolean isGameMaster = false;
    String gameRoomName = "";

//    private Team team;
    Team team1=new Team("Team 1");
    Team team2=new Team("Team 2");


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

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
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
//            if (functionMsg2.equals("2")){
//
//            }
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
//                            System.out.println("Client: " + clientMsg);
                        }
                        serverMsg = impUserServices.register(clientMsgs.get(0),clientMsgs.get(1),clientMsgs.get(2));
                        dataOutputStream.writeUTF(serverMsg);
                        dataOutputStream.flush();
                        break;
                    case "3":
                        return "exit";
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
                String[] returnedMsg = null;
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
//                        multiplayer.loadLoggedInPlayers("RegisteredUsers.txt");
                        while (true){
                            sendMessage("1,Select one of the following options! \n 1-Create a game room \n 2-Join an existing game \n 3-back");
                            String teamOption=readMessage();
                            if(teamOption=="1")
                            {
                                isGameMaster = true;
                                sendMessage("1,Enter name of the game room! ;)");
                                gameRoomName=readMessage();
                                if(Server.checkUniqueness(gameRoomName)){
                                    sendMessage("3,The name you entered already exists, please enter another game");
                                    continue;
                                }
                                setTeam(1);
//                                multiplayer.createTeam(teamName,this);
                                sendMessage("1,Select one of the following \n 1- 1v1 \n 2- 2v2");
                                modeOption=readMessage();
                                if(modeOption=="1"){
                                    multiplayer.createGameRoom(team1, team2, gameRoomName, 1);
                                    multiplayer.gameMenu();
                                    return "2";
                                    //They should play 1v1
//                                    Server.add();
                                    //multiplayer.startGame(team1,team2,"1")
                                } else if (modeOption=="2") {

                                    //I want to make a function to just make the game, and that function will keep checking on the number of players and
                                    //and inside it is the startgame method.

                                    //the set team method in the two modes are going to be different, for example, in mode 1,
                                    //the other player will be autmatically be added to the other team
                                    //but in mode 2, we have several cases, case 1; both teams has empty space in it (such as 1 player in each team,
                                    //or the other team is empty.
                                    //case 2; one of the teams is full, so the player will automatically join the empty team, whether it
                                    //has no players or 1 player.

//                                    multiplayer.startGame(team1, team2, 1);
                                    // 2 teams 4 players
                                    // 2v2
                                }
                                //Two teams should be created
                            }
                            else if (teamOption=="2") {
                                //list game rooms, and then check the mode, if 1v1, then automatically go to team 2, if 2v2, and
                                //the both teams has space, then select which team to enter, if one team is full, then
                                //automatically go to the other team
                                ArrayList<ClientHandler> currentGameMasters = Server.getGameMasters();
                                for(int i = 0; i<currentGameMasters.size(); i++){
                                    sendMessage("3,Room " + (i+1) +": "+ currentGameMasters.get(i).getGameRoomName());
                                }
                                sendMessage("1,Select one of the above rooms to join!");
                                selected = readMessage();
//                                sendMessage("1, ");
                            }
                            else if (teamOption=="3") {
                                break;
                            }
                            else {
                                sendMessage("3,Invalid option.");
                                break;
                            }
                        }
                        break;
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
                        return "exit";
                    default:
                        sendMessage("3,Please enter one of the options!");
                }

            }
        }catch (IOException e){
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
