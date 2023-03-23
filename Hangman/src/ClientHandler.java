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
    String serverMsg = "";
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    ImpUserServices impUserServices = new ImpUserServices();
    SinglePlayer singlePlayer;
    Multiplayer multiplayer;
    String selected = "";
    ArrayList<String> clientMsgs;
    ClientHandler player;
    String teamName;
    String modeOption;

    private Team team;


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
            //Menu Functions will be placed here!
            functionMsg = registrationAndLoginMenu();
            if (functionMsg.equals("2")){
                gameMenu();

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
                String[] returnedMsg = null;
                serverMsg = "1,Select one of following options! \n 1-Single Player \n 2-Multiplayer \n 3-Show Score History \n 4-Exit";
                dataOutputStream.writeUTF(serverMsg);
                dataOutputStream.flush();
                selected = dataInputStream.readUTF();
                System.out.println("Client: " + selected);
                switch (selected){
                    case "1":
                        singlePlayer = new SinglePlayer(clientMsgs.get(0), client);
                        singlePlayer.selectGameDifficulty();
                        break;

                    case "2":
                        multiplayer=new Multiplayer();
                        multiplayer.loadLoggedInPlayers("RegisteredUsers.txt");

                        player.sendMessage("Select one of the following options! \n 1-Create a team \n 2-Join an existing team");
                        String teamOption=player.readMessage();
                        if(teamOption=="1")
                        {
                            player.sendMessage("Enter a unique team name ;)");
                            teamName=player.readMessage();
                            Team team=new Team(teamName);
                            player.setTeam(team);
                            multiplayer.creeateTeam(teamName,player);
                        }
                        else if (teamOption=="2") {
                            player.sendMessage("Enter the name of the team you want to join: \n");
                            teamName= player.readMessage();
                            Team team=new Team(teamName);
                            player.setTeam(team);
                            multiplayer.joinTeam(teamName,player);
                        }
                        else {
                            player.sendMessage("Invalid option.");
                            break;
                        }
                        player.sendMessage("Select one of the following \n 1- 1v1 \n 2- 2v2");
                        modeOption=player.readMessage();
                        if(modeOption=="1"){
                            //Team team1 = new Team(<team-name>);
                            //Team team2 = new Team(<team-name>);
                            //They should play 1v1
                            //multiplayer.startGame(team1,team2,"1")
                        } else if (modeOption=="2") {
                            // 2 teams 4 players
                            // 2v2
                        }
                        //Two teams should be created


                        break;
                    case "3":
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

    public void setTeam(Team team){
        this.team=team;
    }

}
