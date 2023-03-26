import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ImpUserServices implements UserServices{
    private String name;
    private String userName;
    private String password;
    private ArrayList<String> scoresArr = new ArrayList<>();

    private String scores = "";

    String fileLine = "";

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String word;
    private String guess;

    private boolean isLoggedIn;

    public ImpUserServices(Socket clientSocket){
        this.isLoggedIn = false;
        this.clientSocket = clientSocket;
    }


    public ArrayList<String> getScoresArr() {
        return scoresArr;
    }

    public void setScoresArr(ArrayList<String> scoresArr) {
        this.scoresArr = scoresArr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String register(String name,String userName,String password) throws IOException {
        boolean x=true;
        try( BufferedWriter bw = new BufferedWriter(new FileWriter("RegisteredUsers.txt", true));)
        {
            BufferedReader bf = new BufferedReader(new FileReader("RegisteredUsers.txt"));
            Scanner reader;
            try {
                reader = new Scanner(bf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(!reader.hasNextLine()){
                bw.write(name + "," + userName + "," + password);
                bw.newLine();
                return "3,User registered successfully!";
            }
            else {
                while (reader.hasNext()) {
                    String line = reader.nextLine();
                    String[] user = line.split(",");
                    if (user[1].equals(userName)) {
                        return "3,This User Name is being in use";
                    }
                }
                bw.write(name + "," + userName + "," + password);
                bw.newLine();
                return "3,User registered successfully!";

            }
        }
    }

    @Override
    public String login(String userName, String password){
        boolean x=true;
        boolean y=true;
        String[] user = null;
        File file = new File("RegisteredUsers.txt");
            Scanner reader;
            {
                try {
                    reader = new Scanner(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            while(reader.hasNextLine()){
                fileLine = reader.nextLine();
                user = fileLine.split(",");
                setUserName(userName);
                setPassword(password);
                if(Server.checkLogged(userName))
                {
                    return "3,User already logged in!!";
                }
                if(!user[1].equals(userName)){
                    x = false;
                }else{
                    if(!user[2].equals(password)){
                        y = false;
                        break;
                    }
                    else {
                        x = true;
                        y = true;
                        setName(user[0]);
                        isLoggedIn=true;
                        break;
                    }
                }
            }

            if(x == false){
                return "3,404 username not found!";
            }else{
                if(y == false){
                    return "3,401 unauthorized access!";
                }else {
                    isLoggedIn = true;
                    if(user.length > 3) {
                        for(int i = 3; i< user.length; i++){
                            scoresArr.add(user[i]);
                            scores = scores.concat(user[i]+",");
                        }
                    }
                    String str = "2,Welcome " + user[0];
                    return str;
                }
            }
    }
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

//    public void sendMessage(String message) {
//        try {
//            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
//            outputStream.writeUTF(message);
//            outputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
