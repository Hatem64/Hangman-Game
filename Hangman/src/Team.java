import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.*;

public class Team {

    private String name;
    int numAttempts=4;
    int score = 0;
    private List<ClientHandler> players = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    public void addPlayer(ClientHandler player){
        players.add(player);
    }

    public void removePlayer(ClientHandler player){
        players.remove(player);
    }

    public int getNumPlayers(){
        return players.size();
    }

    public List<ClientHandler> getPlayers(){
        return players;
    }

    public String getTeamName(){
        return name;
    }

    public ClientHandler getPlayer(int num){
        return players.get(num);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
