import java.rmi.Naming;
import java.util.Scanner;

/*
 - przelewy na konta innych użytkowników,
 - przy przelewie trzeba podać odpowiednie hasło z listy.
*/
public class RMIClient {
    public static void main(String[] a) throws Exception {
        BankInterface bank = (BankInterface) Naming.lookup("rmi://localhost:1099/Bank");
        String nrKonta="", pesel;
        while(true){
            Scanner sc = new Scanner(System.in);
            boolean logged = false;
            while(!logged){
                System.out.println("Log in");
                System.out.print("Numer konta: ");
                nrKonta = sc.nextLine();
                System.out.print("PESEL: ");
                pesel = sc.nextLine();
                if(bank.validateUser(nrKonta, pesel)){
                    logged = true;
                }
            }
            while(logged){
                System.out.println("1. Wykonaj przelew");
                System.out.println("2. Wyloguj");
                String opt = sc.nextLine();
                if( opt.equals("1")){
                    System.out.println("Numer konta odbiorcy: ");
                    String nrKontaTRG = sc.nextLine();
                    System.out.println("Hasło do przelewów: ");
                    String pswd = sc.nextLine();
                    System.out.println("Ile przelać[zł]: ");
                    double amount = Double.parseDouble(sc.nextLine());
                    if(bank.przelew(nrKonta, nrKontaTRG, amount, pswd)){
                        System.out.println("Przelew zakończony sukcesem");
                    }
                    else{
                        System.out.println("Nie można zrealizować przelewu");
                    }
                }
                else if(opt.equals("2")){
                    logged = false;
                }
                else{
                    System.out.println("Niepoprawna opcja");
                }
            }
        }
    }
}