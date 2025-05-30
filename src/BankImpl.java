
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.stream.Collectors;


public class BankImpl extends UnicastRemoteObject implements BankInterface {
    private static final SecureRandom secRand = new SecureRandom();
    private final List<ClientInfo> clients = new ArrayList<>();
    public BankImpl() throws RemoteException{}

    public ClientInfo register(ClientInfo client){
        clients.add(client);
        return client;
    }

    public double total(){
        return clients.stream().mapToDouble(client -> client.getOszczednosci()).sum();
    }

    @Override
    public ClientInfo get(String pesel) throws RemoteException {
        return clients.stream().filter(client -> client.getPesel().equals(pesel)).findFirst()
                .orElseThrow(() -> new RemoteException("Brak klienta o PESEL "+pesel));
    }

    @Override
    public void generatePasswords(String pesel) throws RemoteException {
        ClientInfo client = get(pesel);
        for(int i = 0;i < 6;i++){
            String pswd = generatePassword();
            while(client.addPassword(pswd)){
                pswd = generatePassword();
            }
            System.out.println("Added password for user: "+pesel + " with password: "+pswd);
        }
    }

    @Override
    public boolean przelew(String nrKontaSRC, String nrKontaTRG, double amount, String password) {
        ClientInfo clientSRC = clients.stream().filter(clientInfo -> Objects.equals(clientInfo.getNrKonta(), nrKontaSRC)).toList().getFirst();
        ClientInfo clientTRG = clients.stream().filter(clientInfo -> Objects.equals(clientInfo.getNrKonta(), nrKontaTRG)).toList().getFirst();
        if(clientSRC.getOszczednosci() < amount){
            return false;
        }
        if(clientSRC.consumePassword(password)){
            clientSRC.subtractMoney(amount);
            clientTRG.addMoney(amount);
            return true;
        }
        else{
            System.out.println("Niepoprawne hasło");
            //throw error
        }
        return false;
    }

    public boolean peselAvilable(String pesel) throws RemoteException {
        return clients.stream().noneMatch(client -> client.getPesel().equals(pesel));
    }
    public boolean nrKontaAvilable(String nrKonta) throws RemoteException {
        return clients.stream().noneMatch(client -> client.getNrKonta().equals(nrKonta));
    }
    public void registerUser(String imie, String nazwisko, String pesel, double oszczednosci) throws RemoteException {

            String nrKonta = generateNrKonta();
            while(!nrKontaAvilable(nrKonta)){
                nrKonta = generateNrKonta();
            }
            if(!peselAvilable(pesel)){
                System.out.println("PESEL ZNAJDUJE SIE W BAZIE DANYCH");
                //throw error;
            }
            ClientInfo client = register(new ClientInfo(imie, nazwisko, pesel, nrKonta, oszczednosci));

            System.out.println("zarejestrowano: "+client);
            System.out.println("lacznie w calym banku: "+total()+" zł");
            System.out.println("dane klienta: "+get(pesel));
    }
    private String generateNrKonta(){
        String allowedChars = "0123456789";
        StringBuilder sb = new StringBuilder(16); // długosc stringa
        for(int i = 0; i < 16; i++){
            int index = secRand.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }
    private String generatePassword() throws RemoteException {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8); // długosc stringa
        for(int i = 0; i < 16; i++){
            int index = secRand.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }
}
