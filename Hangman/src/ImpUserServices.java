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



    public ImpUserServices(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String name,String userName,String password) throws IOException {
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
                System.out.println("User registered successfully!");
            }
            else {
                while (reader.hasNext()) {
                    String line = reader.nextLine();
                    String[] user = line.split(",");
                    if (user[1].equals(userName)) {
                        System.out.println("This User Name is being in use");
                        x=false;
                    }
                }
                if(x==true){
                    bw.write(name + "," + userName + "," + password);
                    bw.newLine();
                    System.out.println("User registered successfully!");
                }
            }
        }

    }

    @Override
    public boolean login(String userName, String password)               {
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
                String[] user = line.split(",");
                if(!user[1].equals(userName)){
                    System.out.println("404 username not found!");
                    return false;
                }
                if(!user[2].equals(password)){
                    System.out.println("401 unauthorized access!");
                    return false;
                }
                System.out.println("Welcome " + user[0]);
                return true;
            }
            System.out.println("No user exists");
            return false;
    }
}
