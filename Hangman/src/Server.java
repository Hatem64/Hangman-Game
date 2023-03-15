import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Server {


    public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter Username: ");
    String username = scanner.nextLine();
    System.out.print("Enter Password: ");
    String password = scanner.nextLine();
    System.out.println("");

    boolean state =  login(username, password);

    }

    public static boolean login(String username, String password){
        File file = new File("D:\\Ahmed\\College_Shit\\4th_Year\\Term 2\\SE for Distributed Systems\\Assignments\\Assignment 1\\Hangman-Game\\Users.txt");
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
            if(!user[1].equals(username)){
                System.out.println("404 username not found!");
                return false;
            }
            if(!user[2].equals(password)){
                System.out.println("401 unauthorized access!");
                return false;
            }
            System.out.println("Welcome " + user[3]);
            return true;
        }
        System.out.println("No user exists");
        return false;
    }
}
