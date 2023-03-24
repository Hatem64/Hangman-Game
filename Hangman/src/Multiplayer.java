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
    int score = 0;
    Team team1;
    Team team2;
    ClientHandler clientHandler;
    int mode = 0;
    String roomName = "";
    String word = "";
    String dashes = "";
    int itertaion=0;

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

//    public void createTeam(String name, ClientHandler player) {
//        if (teams.containsKey(name)) {
//            player.sendMessage("Team name is already taken. Please choose a different name.");
//        } else {
//            Team team = new Team(name);
//            team.addPlayer(player);
//            teams.put(name, team);
//            player.setTeam(team);
//            player.sendMessage("Team created successfully");
//        }
//    }

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

    public void joinTeam(ClientHandler playerToJoin) {

        if(mode == 1){
            team2.addPlayer(playerToJoin);
        }else {
            if (team1.getNumPlayers() < 2){
                team1.addPlayer(playerToJoin);
            }else {
                team2.addPlayer(playerToJoin);
            }
        }

//        if (!teams.containsKey(name)) {
//            player.sendMessage("Team does not exist. Please enter a valid team name.");
//        } else {
//            // Add the player to the team
//            Team team = teams.get(name);
//            team.addPlayer(player);
//            player.setTeam(team);
//            player.sendMessage("Joined the team successfully");
//        }
    }



    //Function to start a game between two teams
    // The parameter mode will be used for selecting 1v1 / 2v2

    //I think this should check the mode first, then decide whether the team count is equal or not.
    public void startGame() throws IOException {
        if (team1.getNumPlayers() != team2.getNumPlayers()) {
            for (ClientHandler player : team1.getPlayers()) {
                player.sendMessage("Error: Number of players in both teams is not equal.");
            }
            for (ClientHandler player : team2.getPlayers()) {
                player.sendMessage("Error: Number of players in both teams is not equal.");
            }
        } else {
            turnTracker(team1, team2);
        }
    }


//    public List<ImpUserServices> loadLoggedInPlayers(String fileName) {
//        List<ImpUserServices> players = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] tokens = line.split(",");
//                String name = tokens[0];
//                String userName = tokens[1];
//                String password = tokens[2];
//                List<Integer> scores = new ArrayList<>();
//
//                if (tokens.length > 3) {
//                    String[] scoresStr = tokens[3].split(",");
//                    for (String scoreStr : scoresStr) {
//                        int score = Integer.parseInt(scoreStr);
//                        scores.add(score);
//                    }
//                }
//                //hena keda fe impUser object gdeed, fa dyman hykoon false, fa m4 hy5o4 el list
//                ImpUserServices player = new ImpUserServices(name, userName, password, scores);
//                if(player.isLoggedIn()){
//                    players.add(player);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return players;
//    }

    public void turnTracker(Team team1, Team team2) throws IOException {
        int currentPlayerIndex=0;
        int opponentPlayerIndex=0;
        Team currentTeam = team1;
        Team opponentTeam = team2;

        while (team1.numAttempts!=0 || team2.numAttempts!=0){
            synchronized (currentTeam.getPlayer(currentPlayerIndex)){
                ClientHandler currentPlayer = currentTeam.getPlayer(currentPlayerIndex);
                currentPlayer.sendMessage("Your turn. Enter a letter: ");

                game(currentTeam, currentPlayer);

                if (!dashes.equals("_")){
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

//        for (int i =0 ; i<currentTeam.getNumPlayers();i++)
//        {
//            team1.setScore(currentTeam);
//            team2.setScore(opponentTeam.score);
//        }

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

    public void game(Team team, ClientHandler player) throws IOException {
        player.sendMessage("3,Be ware The server will only read the first character \nof the string if one is entered. \nso please enter a single character!");
        boolean rightLetter = false;
        if(itertaion == 0){
            for(int i = 0; i<word.length(); i++){
                dashes = dashes.concat("_");
            }
        }else {
            player.sendMessage("3,\nScore: "+ team.score +"\nNumber of attempts: "+ team.numAttempts +"\nThe word: ");
            player.sendMessage("1" + dashes);
            clientMsg=player.readMessage();
            for(int i = 0; i<word.length(); i++){
                clientMsg = clientMsg.toLowerCase();
                if(word.charAt(i) == clientMsg.toLowerCase().charAt(0)){
                    StringBuilder stringBuilder = new StringBuilder(dashes);
                    stringBuilder.setCharAt(i, clientMsg.charAt(0));
                    dashes = stringBuilder.toString();
                    score++;
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
            int lineNum = 0;
            BufferedReader bf = new BufferedReader(new FileReader("RegisteredUsers.txt"));
            Scanner reader;
            try {
                reader = new Scanner(bf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for (ClientHandler player : team.getPlayers()) {
                //Add variable in impuser of this clientHandler that contains the user line in the registeredUsers.txt
                //therefore we can call it in this for loop to check if this line located in this file so we can update the score.
                while (reader.hasNext()) {
                    String line = reader.nextLine();
                    if (line.contains(player.getNumberUsers())) {
                        bw.write(line + "," + score);
                        bw.newLine();
                        user = line + "," + score;
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
            int lineNum = 0;
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
