import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Random;
import java.util.Scanner;

/*

- Administrator tworzy użytkowników, ich konta i daje na początek ileś pieniędzy,
- tworzy listę haseł potrzebnych przy przelewach (dla każdego użytkownika).

*/

public class RMIServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        System.setProperty("java.rmi.server.hostname", "localhost");
        LocateRegistry.createRegistry(1099);
        Naming.rebind("//localhost:1099/Bank", new BankImpl());
        System.out.println("System bankowy gotowy do działania.");
        BankInterface bank = (BankInterface) Naming.lookup("rmi://localhost:1099/Bank");
        bank.registerUser("Baltazar", "Gąbka", "12345678901", 100.13);
        bank.registerUser("Andrzej", "Makłowicz", "12345678902", 4100.13);
        while(true){
            System.out.println("Wybierz: ");
            Scanner in = new Scanner(System.in);
            System.out.println("1. Dodaj użytkownika");
            System.out.println("2. Dodaj hasło użytkownikowi");
            System.out.println("3. Pokaż historie banku");
            System.out.println("4. Pokaż użytkownika");
            String option = in.nextLine();
            if(option.equals("1")){
                System.out.print("imie: ");
                String imie = in.nextLine();
                System.out.print("nazwisko: ");
                String naz = in.nextLine();
                System.out.print("PESEL: ");
                String pesel = in.nextLine();
                System.out.print("oszczednosci: ");
                double oszcz = Double.parseDouble(in.nextLine());
                bank.registerUser(imie, naz, pesel, oszcz);
            }
            else if(option.equals("2")){
                System.out.print("Podaj pesel: ");
                String pesel = in.nextLine();
                bank.generatePasswords(pesel);
            }
            else if(option.equals("3")){
                bank.showBankHistory();
            }
            else if(option.equals("4")){
                System.out.print("Podaj pesel: ");
                String pesel = in.nextLine();
                bank.showUser(pesel);
            }
            else{
                System.out.println("Niepoprawna opcja");
            }





        }
    }
}
