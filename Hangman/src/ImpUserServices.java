import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ImpUserServices implements UserServices{
    private String name;
    private String userName;
    private String password;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private String word;
    private String guess;

    public ImpUserServices() {}

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



//    public ImpUserServices(Socket clientSocket) {
//        this.clientSocket = clientSocket;
//        try {
//            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
                String line = reader.nextLine();
                user = line.split(",");
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
                    String str = "2,Welcome " + user[0];
                    return str;
                }
            }
    }
}
