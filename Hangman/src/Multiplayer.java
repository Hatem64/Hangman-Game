import java.io.*;
import java.util.*;

public class Multiplayer {

    String selected = "";
    int max = 20;
    int min = 1;
    int range = max - min + 1;
    String serverMsg = "";
    String clientMsg = "";
    private String user = null;
//    int score = 0;
    Team team1;
    Team team2;
    ClientHandler clientHandler;
    int mode = 0;
    String roomName = "";
    String word = "";
    String dashes = "";
    int itertaion=0;

    boolean gameStart = false;

    // List of all players waiting to join a team
    // I'm not sure about the type of the list ya hatem
    private volatile List<ClientHandler> waitingPlayers = new ArrayList<ClientHandler>();
    // Map of all teams
    private final Map<String, Team> teams = new HashMap<>();

//    ArrayList<Team> gameRoom = new ArrayList<>();
//    Team team1 = new Team();
//    Team team2 = new Team();

    public Multiplayer(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
//        List<ImpUserServices> players = loadLoggedInPlayers("RegisteredUsers.txt");
        // Add all players to waiting list
//        waitingPlayers.addAll(players);
    }

    public List<ClientHandler> getWaitingPlayers() {
        return waitingPlayers;
    }

    public void gameMenu(){
        try {
            while (true){
                ArrayList<String> clientMsgs = new ArrayList<>();
                String[] returnedMsg = null;
                serverMsg = "1,Please select one of the below options!! \n 1-Start Game \n 2-Reload game teams \n 3-Check players score history \n 4-Exit Program";
                sendMessage(serverMsg);
                selected = readMessage();
                System.out.println("Client: " + selected);
                switch (selected){
                    case "1":
                        startGame();
                        break;
                    case "2":
                        sendMessage("3,Team 1 contains: ");
                        for (ClientHandler player : team1.getPlayers()) {
                            sendMessage("3,"+player.getImpUserServices().getName());
                        }
                        sendMessage("3,Team 2 contains: ");
                        for (ClientHandler player : team2.getPlayers()) {
                            sendMessage("3,"+player.getImpUserServices().getName());
                        }
                        break;
                    case "3":
                        
                }

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void gameMenuForJoinedPlayers(){
        try {
            while (true){
                ArrayList<String> clientMsgs = new ArrayList<>();
                String[] returnedMsg = null;
                serverMsg = "1,Please select one of the below options!! \n 1-Start Game \n 2-Reload game teams \n 3-Check players score history \n 4-Exit Program";
                sendMessage(serverMsg);
                selected = readMessage();
                System.out.println("Client: " + selected);
                switch (selected){
                    case "1":
                        startGame();
                        break;
                    case "2":
                        sendMessage("3,Team 1 contains: ");
                        for (ClientHandler player : team1.getPlayers()) {
                            sendMessage("3,"+player.getImpUserServices().getName());
                        }
                        sendMessage("3,Team 2 contains: ");
                        for (ClientHandler player : team2.getPlayers()) {
                            sendMessage("3,"+player.getImpUserServices().getName());
                        }
                        break;
                    case "3":

                }

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void createGameRoom(Team team1, Team team2, String roomName, int mode) throws FileNotFoundException {
        this.team1 = team1;
        this.team2 = team2;
        this.mode = mode;
        this.roomName = roomName;

        int rand = (int)(Math.random() * range) + min;
        BufferedReader bf = new BufferedReader(new FileReader("EasyGame.txt"));
        Scanner reader;
        try {
            reader = new Scanner(bf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i != rand; i++){
            word = reader.nextLine();
        }
    }

    public void joinGame(ClientHandler playerToJoin, Multiplayer multiplayer) throws InterruptedException {

        if(mode == 1){
            team2.addPlayer(playerToJoin);
        }else {
            if (team1.getNumPlayers() < 2){
                team1.addPlayer(playerToJoin);
            }else {
                team2.addPlayer(playerToJoin);
            }
        }

        multiplayer.gameMenuForJoinedPlayers();
    }



    //Function to start a game between two teams
    // The parameter mode will be used for selecting 1v1 / 2v2

    //I think this should check the mode first, then decide whether the team count is equal or not.
    public void startGame() throws IOException {
        if (team1.getNumPlayers() != team2.getNumPlayers()) {
            for (ClientHandler player : team1.getPlayers()) {
                player.sendMessage("3,Error: Number of players in both teams is not equal.");
            }
            for (ClientHandler player : team2.getPlayers()) {
                player.sendMessage("3,Error: Number of players in both teams is not equal.");
            }
        } else {
            gameStart = true;
            turnTracker(team1, team2);
        }
    }


    public void playerWait() throws InterruptedException {
        Thread.currentThread().wait();
    }

    public void playerNotify() throws InterruptedException {
        Thread.currentThread().notify();
    }


    public void turnTracker(Team team1, Team team2) throws IOException {
        int currentPlayerIndex=0;
        int opponentPlayerIndex=0;
        Team currentTeam = team1;
        Team opponentTeam = team2;

        while (team1.numAttempts!=0 || team2.numAttempts!=0){
            synchronized (currentTeam.getPlayer(currentPlayerIndex)){
                ClientHandler currentPlayer = currentTeam.getPlayer(currentPlayerIndex);
                currentPlayer.sendMessage("3,Your turn");

                game(currentTeam, currentPlayer);

                if (!dashes.contains("_")){
                    //for loop to track all players and tell them they won
                    for (ClientHandler player : currentTeam.getPlayers()) {
                        player.sendMessage("3,Well Done!!!\nScore: "+ currentTeam.score);
                    }
                    break;
                }

                if (currentTeam == team1) {
                    currentTeam = team2;
                    opponentTeam = team1;
                } else {
                    currentTeam = team1;
                    opponentTeam = team2;
                }

                // % currentTeam.size() wrap it around to 0 when it reaches the end of the list.
                currentPlayerIndex = (currentPlayerIndex + 1) % currentTeam.getNumPlayers();
                opponentPlayerIndex = (opponentPlayerIndex + 1) % opponentTeam.getNumPlayers();
                itertaion++;
            }
        }
        if (team1.numAttempts == 0){
            for (ClientHandler player : team1.getPlayers()) {
                player.sendMessage("3,You have failed! Shame on you... \nThe word was: "+ word +"\nBetter luck next time....");
            }
        }
        if (team2.numAttempts == 0){
            for (ClientHandler player : team2.getPlayers()) {
                player.sendMessage("3,You have failed! Shame on you... \nThe word was: "+ word +"\nBetter luck next time....");
            }
        }
            addScoreToUser(currentTeam);
            addScoreToUser(opponentTeam);
    }


    public void game(Team team, ClientHandler player) throws IOException {
        player.sendMessage("3,Be ware The server will only read the first character \nof the string if one is entered. \nso please enter a single character!");
        boolean rightLetter = false;
        if(itertaion == 0){
            for(int i = 0; i<word.length(); i++){
                dashes = dashes.concat("_");
            }
        }else {
            player.sendMessage("3,\nScore of "+team.getTeamName()+" is: "+ team.score +"\nNumber of attempts: "+ team.numAttempts +"\nThe word: ");
            player.sendMessage("1" + dashes);
            clientMsg=player.readMessage();
            for(int i = 0; i<word.length(); i++){
                clientMsg = clientMsg.toLowerCase();
                if(word.charAt(i) == clientMsg.toLowerCase().charAt(0)){
                    StringBuilder stringBuilder = new StringBuilder(dashes);
                    stringBuilder.setCharAt(i, clientMsg.charAt(0));
                    dashes = stringBuilder.toString();
                    team.score++;
                    rightLetter = true;
                }
            }
        }
            if (rightLetter == false){
                team.numAttempts--;
            }
    }
    public void addScoreToUser(Team team){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("Score.txt", false));)
        {
//            int lineNum = 0;
            BufferedReader bf = new BufferedReader(new FileReader("RegisteredUsers.txt"));
            Scanner reader;
            try {
                reader = new Scanner(bf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (ClientHandler player : team.getPlayers()) {
                while (reader.hasNext()) {
                    String line = reader.nextLine();
                    if (line.contains(player.getImpUserServices().fileLine)) {
                        bw.write(line + "," + team.score);
                        bw.newLine();
                        user = line + "," + team.score;
                    }else{
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try( BufferedWriter bw = new BufferedWriter(new FileWriter("RegisteredUsers.txt", false));)
        {
//            int lineNum = 0;
            BufferedReader bf = new BufferedReader(new FileReader("Score.txt"));
            Scanner reader;
            try {
                reader = new Scanner(bf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            while (reader.hasNext()) {
                String line = reader.nextLine();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTeamsToGameRoom(Team team1, Team team2){

    }

    public void sendMessage(String message) {
        try {
            DataOutputStream outputStream = new DataOutputStream(clientHandler.getClient().getOutputStream());
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() throws IOException {
        String message = null;
        if (clientHandler.getDataInputStream() != null) {
            message = clientHandler.getDataInputStream().readUTF();
        }
        return message;
    }







}
