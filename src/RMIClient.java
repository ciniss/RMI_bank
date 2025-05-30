import java.rmi.Naming;
import java.util.Scanner;

/*
 - przelewy na konta innych użytkowników,
 - przy przelewie trzeba podać odpowiednie hasło z listy.
*/
public class RMIClient {
    public static void main(String[] a) throws Exception {
        BankInterface bank = (BankInterface) Naming.lookup("rmi://localhost:1099/Bank");

    }
}