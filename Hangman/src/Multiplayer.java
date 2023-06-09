import java.io.*;
import java.util.*;

public class Multiplayer {

    String selected = "";
    int num = 0;
    int max = 20;
    int min = 1;
    int range = max - min + 1;
    String serverMsg = "";
    String clientMsg = "";
    private String user = null;
    Team team1;
    Team team2;
    ClientHandler clientHandler;
    int mode = 0;
    String roomName = "";
    String word = "";
    String dashes = "";
    int itertaion=0;

    boolean gameStart = false;
    ArrayList<ClientHandler> players = new ArrayList<>();


    // List of all players waiting to join a team
    // I'm not sure about the type of the list ya hatem
    private volatile List<ClientHandler> waitingPlayers = new ArrayList<ClientHandler>();
    // Map of all teams
    private final Map<String, Team> teams = new HashMap<>();


    public Multiplayer(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        players.add(clientHandler);
    }

    public List<ClientHandler> getWaitingPlayers() {
        return waitingPlayers;
    }

    public void gameMenu(){
        try {
            while (true){
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
                        sendMessage("3,Team 1 scores: ");
                        for (ClientHandler player : team1.getPlayers()) {
                            sendMessage("3,"+player.getImpUserServices().getName()+":");
                            sendMessage("3,"+player.getImpUserServices().getScores());
                        }
                        sendMessage("3,Team 2 scores: ");
                        for (ClientHandler player : team2.getPlayers()) {
                            sendMessage("3,"+player.getImpUserServices().getName()+":");
                            sendMessage("3,"+player.getImpUserServices().getScores());
                        }
                        break;
                    case "4":
                        clientHandler.gameMenu();

                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void gameLoop(ArrayList<ClientHandler> players){
        if (!gameStart){
            for (ClientHandler player : players){
                player.sendMessage("4,Wait till game starts please!");
            }
            while (!gameStart){
            }
        }
    }

    public void createGameRoom(Team team1, Team team2, String roomName, int mode) throws FileNotFoundException {
        this.team1 = team1;
        this.team2 = team2;
        this.mode = mode;
        this.roomName = roomName;
        clientHandler.playerTurn = true;
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

    public void joinGame(ClientHandler playerToJoin) throws InterruptedException {

        if(mode == 1){
            if(team2.getNumPlayers() != 1)
                team2.addPlayer(playerToJoin);
            else{
                playerToJoin.sendMessage("3,Game room is full, please try to join another room!");
                playerToJoin.gameMenu();
            }
        }else {
            if(team1.getNumPlayers() < 3 && team2.getNumPlayers() < 3){
                if(num % 2 == 0){
                    if (team2.getNumPlayers() < 3){
                        team2.addPlayer(playerToJoin);
                    }else {
                        team1.addPlayer(playerToJoin);
                    }
                }else {
                    if (team1.getNumPlayers() < 3){
                        team1.addPlayer(playerToJoin);
                    }else {
                        team2.addPlayer(playerToJoin);
                    }
                }
                num++;
            }else{
                playerToJoin.sendMessage("3,Game room is full, please try to join another room!");
                playerToJoin.gameMenu();
            }
        }

        players.add(playerToJoin);
        ArrayList<ClientHandler> playerss = new ArrayList<>();
        for(int i=1; i<players.size();i++){
            playerss.add(players.get(i));
        }
        gameLoop(playerss);

    }

    public void startGame() throws IOException {
        if(mode == 1){
            if (team1.getNumPlayers() != 1 || team2.getNumPlayers() != 1) {
                for (ClientHandler player : team1.getPlayers()) {
                    player.sendMessage("3,Error: Number of players in both teams are not equal.");
                }
                for (ClientHandler player : team2.getPlayers()) {
                    player.sendMessage("3,Error: Number of players in both teams are not equal.");
                }
            } else {
                clientHandler.playerTurn = true;
                gameSequencer(team1, team2);
            }
        }else {
            if (team1.getNumPlayers() != 2 || team2.getNumPlayers() != 2) {
                for (ClientHandler player : team1.getPlayers()) {
                    player.sendMessage("3,Error: Number of players in both teams are not equal.");
                }
                for (ClientHandler player : team2.getPlayers()) {
                    player.sendMessage("3,Error: Number of players in both teams are not equal.");
                }
            } else {
                gameStart = true;
                gameSequencer(team1, team2);
            }
        }
    }

    public void gameSequencer(Team team1, Team team2) throws IOException {
        int currentPlayerIndex=0;
        Team currentTeam = team1;
        Team opponentTeam = team2;
        for(ClientHandler player:players){
            player.sendMessage("3,Game will start shortly");
        }
        while (team1.numAttempts!=0 || team2.numAttempts!=0){
            ClientHandler currentPlayer = currentTeam.getPlayer(currentPlayerIndex);
            currentPlayer.playerTurn = true;


            currentPlayer.sendMessage("3,Your turn");
            currentPlayer.sendMessage("5,Enter one character please!");
            boolean rightLetter = false;

            if(itertaion == 0){
                for(int i = 0; i<word.length(); i++){
                    dashes = dashes.concat("_");
                }
            }
            currentPlayer.sendMessage("3,\nScore of "+currentTeam.getTeamName()+" is: "+ currentTeam.score +"\nNumber of attempts: "+ currentTeam.numAttempts +"\nThe word: ");
            currentPlayer.sendMessage("1," + dashes);
            clientMsg=currentPlayer.readMessage();

            if (clientMsg.equals("-")){
                //for loop to track all players and tell them they won
                gameStart = false;
                for (ClientHandler player : currentTeam.getPlayers()) {
                    player.sendMessage("3,Damn I think one of the players got no guts. \nHis ancestors should feel ashamed for having such a disgrace of a human being");
                    player.sendMessage("3,The word was: "+word);

                }
                for (ClientHandler player : opponentTeam.getPlayers()) {
                    player.sendMessage("3,Damn I think one of the players got no guts. \nHis ancestors should feel ashamed for having such a disgrace of a human being");
                    player.sendMessage("3,The word was: "+word);

                }
                for(int i = 1; i<players.size(); i++){
                    Server.createNewThread(players.get(i));
                }
                gameMenu();
            }

            for(int i = 0; i<word.length(); i++){
                clientMsg = clientMsg.toLowerCase();
                if(word.charAt(i) == clientMsg.toLowerCase().charAt(0)){
                    StringBuilder stringBuilder = new StringBuilder(dashes);
                    stringBuilder.setCharAt(i, clientMsg.charAt(0));
                    dashes = stringBuilder.toString();
                    currentTeam.score++;
                    rightLetter = true;
                }
            }
            if (!rightLetter){
                currentTeam.numAttempts--;
                currentPlayer.sendMessage("3,Ah sad. Wrong letter..");
                if (currentTeam == team1) {
                    currentTeam = team2;
                    opponentTeam = team1;
                } else {
                    currentTeam = team1;
                    opponentTeam = team2;
                }
            }else {
                for(ClientHandler player:players){
                    if(!player.playerTurn){
                        player.sendMessage("3,"+currentPlayer.getImpUserServices().getName()+" has guessed the right letter");
                        player.sendMessage("3," + dashes);
                    }
                }
            }
            currentPlayer.sendMessage("3," + dashes);
            if (!dashes.contains("_")){
                //for loop to track all players and tell them they won
                gameStart = false;
                if(currentTeam.score > opponentTeam.score)
                {
                    for (ClientHandler player : currentTeam.getPlayers()) {
                        player.sendMessage("3,Well Done!!!\nScore: "+ currentTeam.score);
                    }
                    for (ClientHandler player : opponentTeam.getPlayers()) {
                        player.sendMessage("3,"+currentTeam.getTeamName()+" has won. Better luck next time... \nThe word was: "+word+"\nScore: "+ opponentTeam.score);
                    }
                } else if (currentTeam.score < opponentTeam.score) {
                    for (ClientHandler player : opponentTeam.getPlayers()) {
                        player.sendMessage("3,Well Done!!!\nScore: "+ opponentTeam.score);
                    }
                    for (ClientHandler player : currentTeam.getPlayers()) {
                        player.sendMessage("3,"+opponentTeam.getTeamName()+" has won. Better luck next time... \nThe word was: "+word+"\nScore: "+ currentTeam.score);
                    }
                }else
                {
                    for (ClientHandler player : currentTeam.getPlayers()) {
                        player.sendMessage("3,A draw?!!! I can't believe it. Couldn't one of you pull it off??!\nScore: "+ currentTeam.score);
                    }
                    for (ClientHandler player : opponentTeam.getPlayers()) {
                        player.sendMessage("3,A draw?!!! I can't believe it. Couldn't one of you pull it off??!\nScore: "+ opponentTeam.score);
                    }
                }
                for(int i = 1; i<players.size(); i++){
                    Server.createNewThread(players.get(i));
                }
                break;
            }

            currentPlayer.playerTurn = false;

            for (int i = 0; i < players.size(); i++){
                if(i == players.size()-1){
                    if(players.get(i) == currentPlayer){
                        players.get(1).playerTurn = true;
                        break;
                    }
                }else {
                    if(players.get(i) == currentPlayer){
                        players.get(i+1).playerTurn = true;
                        break;
                    }
                }
            }
            // % currentTeam.size() wrap it around to 0 when it reaches the end of the list.
            if(itertaion % 2 == 1)
                currentPlayerIndex = (currentPlayerIndex + 1) % currentTeam.getNumPlayers();
            itertaion++;
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
        for (ClientHandler player : currentTeam.getPlayers()){
            addScoreToUser(player, Integer.toString(currentTeam.score));
        }
        for (ClientHandler player : opponentTeam.getPlayers()){
            addScoreToUser(player, Integer.toString(opponentTeam.score));
        }
        gameMenu();
    }
    public void addScoreToUser(ClientHandler player, String score){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("Score.txt", false));)
        {
            BufferedReader bf = new BufferedReader(new FileReader("RegisteredUsers.txt"));
            Scanner reader;
            try {
                reader = new Scanner(bf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            while (reader.hasNext()) {
                String line = reader.nextLine();
                if (line.contains(player.getImpUserServices().fileLine)) {
                    bw.write(line + "," + score);
                    bw.newLine();
                }else {
                    bw.write(line);
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try( BufferedWriter bw = new BufferedWriter(new FileWriter("RegisteredUsers.txt", false));)
        {
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
