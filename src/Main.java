import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        HTTPService Service = new HTTPService();
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("Continue? (1/0): ");
            int chs = scanner.nextInt();
            if (chs == 1){
                System.out.println("\n" + "Packet Name: ");
                String dependencyTreeRoot = scanner.next().toString();
                Service.getDependencyTree(Service.getDependencies(dependencyTreeRoot));
            }
            else if (chs == 0) break;
            else{
                System.out.println("try again");
            }
        }
    }
}