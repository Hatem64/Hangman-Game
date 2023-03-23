import java.io.*;
import java.util.*;

public class Multiplayer {
    int numOfAttempts = 0;
    int max = 20;
    int min = 1;
    int range = max - min + 1;
    String serverMsg = "";
    String clientMsg = "";
    private String user = null;
    int score = 0;
    // List of all players waiting to join a team
    // I'm not sure about the type of the list ya hatem
    private final List<ImpUserServices> waitingPlayers = new ArrayList<ImpUserServices>();
    // Map of all teams
    private final Map<String, Team> teams = new HashMap<>();

    public Multiplayer() {
        List<ImpUserServices> players = loadLoggedInPlayers("RegisteredUsers.txt");
        // Add all players to waiting list
        waitingPlayers.addAll(players);
    }

    public void creeateTeam(String name, ClientHandler player) {
        if (teams.containsKey(name)) {
            player.sendMessage("Team name is already taken. Please choose a different name.");
        } else {
            Team team = new Team(name);
            team.addPlayer(player);
            teams.put(name, team);
            player.setTeam(team);
            player.sendMessage("Team created successfully");
        }
    }

    public void joinTeam(String name, ClientHandler player) {
        if (!teams.containsKey(name)) {
            player.sendMessage("Team does not exist. Please enter a valid team name.");
        } else {
            // Add the player to the team
            Team team = teams.get(name);
            team.addPlayer(player);
            player.setTeam(team);
            player.sendMessage("Joined the team successfully");
        }
    }

    //Function to start a game between two teams
    // The parameter mode will be used for selecting 1v1 / 2v2
    public void startGame(Team team1, Team team2, int mode) {
        if (team1.getNumPlayers() != team2.getNumPlayers()) {
            for (ClientHandler player : team1.getPlayers()) {
                player.sendMessage("Error: Number of players in both teams is not equal.");
            }
            for (ClientHandler player : team2.getPlayers()) {
                player.sendMessage("Error: Number of players in both teams is not equal.");
            }
        } else {
            //Start the game
            //To be continued
            
        }
    }

    public List<ImpUserServices> loadLoggedInPlayers(String fileName) {
        List<ImpUserServices> players = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String name = tokens[0];
                String userName = tokens[1];
                String password = tokens[2];
                List<Integer> scores = new ArrayList<>();

                if (tokens.length > 3) {
                    String[] scoresStr = tokens[3].split(",");
                    for (String scoreStr : scoresStr) {
                        int score = Integer.parseInt(scoreStr);
                        scores.add(score);
                    }
                }
                ImpUserServices player = new ImpUserServices(name, userName, password, scores);
                if(player.isLoggedIn()){
                    players.add(player);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    public void turnTracker(List<ClientHandler> team1, List<ClientHandler> team2) throws IOException {
        int currentPlayerIndex=0;
        int opponentPlayerIndex=0;
        int numAttempts=10;
        List<ClientHandler> currentTeam = team1;
        List<ClientHandler> opponentTeam = team2;

        while (numAttempts > 0){
            //synchronized to ensure that only one player can make a move at a time.
            synchronized (currentTeam.get(currentPlayerIndex)){
                //currentPlayer.wait();
                ClientHandler currentPlayer=currentTeam.get(currentPlayerIndex);
                ClientHandler opponent= opponentTeam.get(opponentPlayerIndex);

                currentPlayer.sendMessage("Your turn. Enter a letter: ");
                String guess = currentPlayer.readMessage();

                //guess must be validated somehow => call the game logic function
                game(numOfAttempts,"RegisteredUsers.txt");

                if (currentTeam == team1) {
                    currentTeam = team2;
                    opponentTeam = team1;
                } else {
                    currentTeam = team1;
                    opponentTeam = team2;
                }
                // % currentTeam.size() wrap it around to 0 when it reaches the end of the list.
                currentPlayerIndex = (currentPlayerIndex + 1) % currentTeam.size();
                opponentPlayerIndex = (opponentPlayerIndex + 1) % opponentTeam.size();
            }
        }
    }
    public void game(int numOfAttempts, String fileName) throws IOException {
        ClientHandler player = null;
        this.numOfAttempts = numOfAttempts;
        int score = 0;
        int iteration = 0;
        int rand = (int)(Math.random() * range) + min;
        BufferedReader bf = new BufferedReader(new FileReader(fileName));
        Scanner reader;
        //array contains the number of letters in the word
        String word = "";

        try {
            reader = new Scanner(bf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i != rand; i++){
            word = reader.nextLine();
        }
        int numOfDashes = word.length();
        String dashes = "";
        player.sendMessage("3,Be ware The server will only read the first character \nof the string if one is entered. \nso please enter a single character!");
        while (true){
            boolean rightLetter = false;
            if(iteration == 0){
                for(int i = 0; i<numOfDashes; i++){
                    dashes = dashes.concat("_");
                }
            }else {
                for(int i = 0; i<numOfDashes; i++){
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
            if (numOfAttempts == 0){
                player.sendMessage("3,You have failed! Shame on you... \nThe word was: "+ word +"\nBetter luck next time...." );
                String num = Integer.toString(score);
                addScoreToUser(num);
                break;
            }
            else if (score == word.length()){
                player.sendMessage("3,Well Done!!!\nScore: "+ score);
                String num = Integer.toString(score);
                addScoreToUser(num);
                break;
            }
            if (rightLetter == false){
                numOfAttempts--;
            }
            player.sendMessage("3,\nScore: "+ score +"\nNumber of attempts: "+ numOfAttempts +"\nThe word: ");
            player.sendMessage("1" + dashes);
            player.readMessage();
            iteration++;
        }
    }

    public void addScoreToUser(String Score){
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
            while (reader.hasNext()) {
                String line = reader.nextLine();
                if (line.contains(user)) {
                    bw.write(line + "," + score);
                    bw.newLine();
                    user = line + "," + score;
                }else{
                    bw.write(line);
                    bw.newLine();
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









}
