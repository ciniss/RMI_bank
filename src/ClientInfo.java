import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class ClientInfo implements Serializable {
    private final String imie, nazwisko, pesel, nrKonta;
    private double oszczednosci;

    public String getNazwisko() {
        return nazwisko;
    }

    public String getPesel() {
        return pesel;
    }

    public String getNrKonta() {
        return nrKonta;
    }

    public double getOszczednosci() {
        return oszczednosci;
    }

    private final ArrayList<String> passwords;

    public ClientInfo(String imie, String nazwisko, String PESEL, String nrKonta, double oszczednosci) {
        //brak walidacji konta bankowego ponieważ jest nie ma możliwości wprowadzenia błędnego.
        if (oszczednosci < 0.0){
            //throw negative balance error
        }
        if(imie.isBlank() || nazwisko.isBlank() || PESEL.isBlank()){
            //throw empty credentials error
        }
        Pattern peselPattern = Pattern.compile("[0-9]{11}");
        Pattern namePattern = Pattern.compile("[A-Z][a-z]*");
        if(!PESEL.matches(peselPattern.pattern()) || imie.matches(namePattern.pattern()) || nazwisko.matches(namePattern.pattern())) {
            //throw invalid credentials error;
        }
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pesel = PESEL;
        this.oszczednosci = oszczednosci;
        this.nrKonta = nrKonta;
        this.passwords = new ArrayList<>(0);
    }
    private boolean passwordAvilable(String pwd){
        return this.passwords.stream().noneMatch(password -> password.equals(pwd));
    }
    @Override public String toString() {
        return imie+" "+nazwisko+" ("+pesel+") – "+oszczednosci+" zł ["+nrKonta+"]";
    }
    public boolean addPassword(String password){
        if(this.passwords.contains(password)){
            this.passwords.add(password);
            return true;
        }
        return false;
    }
    public boolean consumePassword(String password){
        String pswd = this.passwords.stream().filter(p -> p.equals(password)).findFirst().orElse(null);
        if(pswd == null){
            return false;
        }
        passwords.remove(pswd);
        return true;
    }
    public void subtractMoney(double amount){
        if(amount < 0.0){
            //throw error
        }
        this.oszczednosci-=amount;
    }
    public void addMoney(double amount){
        if(amount < 0.0){
            //throw error
        }
        this.oszczednosci+=amount;
    }
}