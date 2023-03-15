import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        ImpUserServices us=new ImpUserServices();
        us.register("ahmed","jetstream","1234");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.println("");
        us.login(username,password);
    }
}