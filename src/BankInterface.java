import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankInterface extends Remote {
    void registerUser (String imie, String nazwisko, String pesel, double oszczednosci) throws RemoteException;
    double total () throws RemoteException;
    ClientInfo get (String pesel) throws RemoteException;
    void generatePasswords (String pesel) throws RemoteException;
    boolean przelew(String nrKontaSRC, String nrKontaTRG, double amount, String password) throws RemoteException;
    void showBankHistory() throws RemoteException;
    boolean validateUser(String nrKonta, String pesel) throws RemoteException;
}
