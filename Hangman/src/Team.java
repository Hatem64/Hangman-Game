import java.util.ArrayList;
import java.util.*;

public class Team {

    private String name;
    int numAttempts=4;
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
}
