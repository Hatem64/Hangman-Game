import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SinglePlayer {


    //user line in the text note.
    private String user = null;
    String[] userArr;

    //user line in the text note, but already split into the array.
    private String userString = null;
    String clientMsg = "";
    String serverMsg = "";
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String selected = "";
    ClientHandler clientHandler;
    int max = 20;
    int min = 1;
    int range = max - min + 1;
    int numOfAttempts = 0;
    Socket client = null;
    int score = 0;

    public SinglePlayer(String userString, Socket client) throws IOException {
        this.userString = userString;
        try {
            BufferedReader bf = new BufferedReader(new FileReader("RegisteredUsers.txt"));
            Scanner reader;
            try {
                reader = new Scanner(bf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            while (reader.hasNext()) {
                String line = reader.nextLine();
                if (line.contains(userString)) {
                    user = line;
                    userArr = user.split(",");
                    break;
                }
            }
            this.client = client;
            clientHandler = new ClientHandler(client);
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataInputStream = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectGameDifficulty(){
        try {
            while (true){
                String[] returnedMsg = null;
                serverMsg = "1,Please select a game difficulty " + userArr[0] + "\n 1-Easy \n 2-Hard \n 3-Show Score History \n 4-Exit";
                dataOutputStream.writeUTF(serverMsg);
                dataOutputStream.flush();
                selected = dataInputStream.readUTF();
                switch (selected){
                    case "1":
                        game(4, "EasyGame.txt");
                        break;
                    case "2":
                        //yet to add hard words.
                        game(6, "HardGame.txt");
                        break;
                    case "3":
                        clientHandler.gameMenu();

                }

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void game(int numOfAttempts, String fileName) throws IOException {
        this.numOfAttempts = numOfAttempts;
        score = 0;
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
        serverMsg = "3,Be ware The server will only read the first character \nof the string if one is entered. \nso please enter a single character!";
        dataOutputStream.writeUTF(serverMsg);
        dataOutputStream.flush();
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
//                        dashes = dashes.concat(String.valueOf(clientMsg.charAt(0)));
                        score++;
                        rightLetter = true;
                    }
                }
            }
            if (numOfAttempts == 0){
                serverMsg = "3,You have failed! Shame on you... \nThe word was: "+ word +"\nBetter luck next time...." ;
                String num = Integer.toString(score);
                addScoreToUser(num);
                dataOutputStream.writeUTF(serverMsg);
                dataOutputStream.flush();
                break;
            }
            else if (score == word.length()){
                serverMsg = "3,Well Done!!!\nScore: "+ score ;
                String num = Integer.toString(score);
                addScoreToUser(num);
                dataOutputStream.writeUTF(serverMsg);
                dataOutputStream.flush();
                break;
            }
            if (rightLetter == false){
                numOfAttempts--;
            }
            serverMsg = "3,\nScore: "+ score +"\nNumber of attempts: "+ numOfAttempts +"\nThe word: ";
            dataOutputStream.writeUTF(serverMsg);
            dataOutputStream.flush();
            dataOutputStream.writeUTF("1,"+dashes);
            dataOutputStream.flush();
            clientMsg = ((String) dataInputStream.readUTF());
            iteration++;
        }
    }


    public void addScoreToUser(String Score){
        try( BufferedWriter bw = new BufferedWriter(new FileWriter("Score.txt", false));)
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
