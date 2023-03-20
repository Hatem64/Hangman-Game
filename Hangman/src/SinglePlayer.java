import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SinglePlayer {

//    private String oldScore = "";
//    private String newScore = "";

    int score = 0;

    //user line in the text note.
    private String user = null;

    //user line in the text note, but already split into the array.
    private ArrayList<String>  userArr = null;
    String clientMsg = "";
    String serverMsg = "";
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String selected = "";
    ClientHandler clientHandler;
    int max = 12;
    int min = 1;
    int range = max - min + 1;
    int numOfAttempts = 0;
    Socket client = null;


    public SinglePlayer(String user, Socket client) throws IOException {
        this.user = user;
        this.client = client;
        clientHandler = new ClientHandler(client);
        dataOutputStream = new DataOutputStream(client.getOutputStream());
        dataInputStream = new DataInputStream(client.getInputStream());
    }

    public SinglePlayer(ArrayList<String> userArr, Socket client) throws IOException {
        this.userArr = userArr;
        this.client = client;
        clientHandler = new ClientHandler(client);
        dataOutputStream = new DataOutputStream(client.getOutputStream());
        dataInputStream = new DataInputStream(client.getInputStream());
    }

//    public String getOldScore() {
//        return oldScore;
//    }
//
//    public void setOldScore(String oldScore) {
//        this.oldScore = oldScore;
//    }
//
//    public String getNewScore() {
//        return newScore;
//    }
//
//    public void setNewScore(String newScore) {
//        this.newScore = newScore;
//    }

    public void selectGameDifficulty(){
        try {
            while (true){
                String[] returnedMsg = null;
                serverMsg = "1,Please select a game difficulty " + userArr.get(0) + "\n 1-Easy \n 2-Hard \n 3-Show Score History \n 3-Exit";
                dataOutputStream.writeUTF(serverMsg);
                dataOutputStream.flush();
                selected = dataInputStream.readUTF();
                switch (selected){
                    case "1":
                        easyGame();
                        break;
                    case "2":
                        hardGame();
                        break;
                    case "3":
                        clientHandler.registrationAndLoginMenu();
                }

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void easyGame() throws IOException {
        numOfAttempts = 3;
        int iteration = 0;
        int rand = (int)(Math.random() * range) + min;
        BufferedReader bf = new BufferedReader(new FileReader("EasyGame.txt"));
        Scanner reader;
        //array contains the number of letters in the word
        String[] word = null;

        try {
            reader = new Scanner(bf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i != rand; i++){
            String line = reader.nextLine();
            word = line.split(",");
        }
        int numOfDashes = Integer.parseInt(word[0]);
        String dashes = "";
//        for(int i = 0; i<numOfDashes; i++){
//            dashes.concat("_");
//        }

        serverMsg = "3,Be ware The server will only read the first character \nof the string if one is entered. \nso please enter a single character!";
        dataOutputStream.writeUTF(serverMsg);
        dataOutputStream.flush();
        while (true){
            boolean rightLetter = false;
            serverMsg = "3,The word: ";
            dataOutputStream.writeUTF(serverMsg);
            dataOutputStream.flush();
            if(iteration == 0){
                for(int i = 0; i<numOfDashes; i++){
                    dashes = dashes.concat("_");
                }
            }else {
                for(int i = 0; i<numOfDashes; i++){
                    if(word[1].charAt(i) == clientMsg.charAt(0)){
                        StringBuilder stringBuilder = new StringBuilder(dashes);
                        stringBuilder.setCharAt(i, clientMsg.charAt(0));
                        dashes = stringBuilder.toString();
//                        dashes = dashes.concat(String.valueOf(clientMsg.charAt(0)));
                        score++;
                        rightLetter = true;
                    }
//                    else {
//                        dashes = dashes.concat("_");
//                    }
                }
            }
            if (numOfAttempts == 0){
                break;
            }
            else if (score == Integer.parseInt(word[0])){
                break;
            }

            if (rightLetter == false){
                numOfAttempts--;
            }
            dataOutputStream.writeUTF("1,"+dashes);
            dataOutputStream.flush();
            clientMsg = ((String) dataInputStream.readUTF());
            iteration++;

        }
    }

    public void hardGame(){
        int rand = (int)(Math.random() * range) + min;


    }

    public void addScoreToUser(String Score){
        //rewrite the line, and add ",0" in the end of the user line.

    }

    public void modifyScoreOfUser(String Score){
        //gets that specific line, and adds, or subtracts a point to the last score.
    }
}
