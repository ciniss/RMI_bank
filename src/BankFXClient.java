import java.rmi.Naming;
import java.rmi.RemoteException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class BankFXClient extends Application {
    private BankInterface bank;
    private Stage primaryStage;
    private String pesel;
    private String nrKonta;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        bank = (BankInterface) Naming.lookup("rmi://localhost:1099/Bank");

        primaryStage.setTitle("Bank - Klient");
        primaryStage.setScene(createLoginScene());
        primaryStage.show();
    }

    private Scene createLoginScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField peselField = new TextField();
        TextField nrKontaField = new TextField();
        Button loginBtn = new Button("Zaloguj");
        Label statusLabel = new Label();

        grid.add(new Label("PESEL:"), 0, 0);
        grid.add(peselField, 1, 0);
        grid.add(new Label("Numer konta:"), 0, 1);
        grid.add(nrKontaField, 1, 1);
        grid.add(loginBtn, 1, 2);
        grid.add(statusLabel, 1, 3);

        loginBtn.setOnAction(e -> {
            try {
                String inputPesel = peselField.getText().trim();
                String inputNrKonta = nrKontaField.getText().trim();

                if (bank.validateUser(inputNrKonta, inputPesel)) {
                    ClientInfo info = bank.get(inputPesel);
                    this.pesel = info.pesel;
                    this.nrKonta = info.nrKonta;
                    primaryStage.setScene(createUserScene());
                } else {
                    statusLabel.setText("Nieprawidłowy PESEL lub numer konta");
                }
            } catch (RemoteException ex) {
                statusLabel.setText("Błąd logowania: " + ex.getMessage());
            }
        });

        return new Scene(grid, 350, 250);
    }

    private Scene createUserScene() {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(20));

        Label nameLabel = new Label();
        Label surnameLabel = new Label();
        Label peselLabel = new Label();
        Label nrKontaLabel = new Label();
        Label balanceLabel = new Label();

        Button refreshBtn = new Button("Odśwież dane");
        refreshBtn.setOnAction(e -> updateClientInfo(nameLabel, surnameLabel, peselLabel, nrKontaLabel, balanceLabel));

        // Przelew
        GridPane transferGrid = new GridPane();
        transferGrid.setPadding(new Insets(10));
        transferGrid.setVgap(8);
        transferGrid.setHgap(8);
        transferGrid.add(new Label("Numer konta odbiorcy:"), 0, 0);
        TextField recipientField = new TextField();
        transferGrid.add(recipientField, 1, 0);
        transferGrid.add(new Label("Kwota (PLN):"), 0, 1);
        TextField amountField = new TextField();
        transferGrid.add(amountField, 1, 1);
        transferGrid.add(new Label("Hasło jednorazowe:"), 0, 2);
        PasswordField pwdField = new PasswordField();
        transferGrid.add(pwdField, 1, 2);

        Button transferBtn = new Button("Wykonaj przelew");
        transferGrid.add(transferBtn, 1, 3);
        TextArea transferOut = new TextArea();
        transferOut.setEditable(false);
        transferGrid.add(transferOut, 0, 4, 2, 1);

        transferBtn.setOnAction(e -> {
            try {
                String targetNrKonta = recipientField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String oneTimePwd = pwdField.getText().trim();

                boolean success = bank.przelew(this.nrKonta, targetNrKonta, amount, oneTimePwd);
                if (success) {
                    transferOut.appendText("Przelew wykonany\n");
                    updateClientInfo(nameLabel, surnameLabel, peselLabel, nrKontaLabel, balanceLabel);
                } else {
                    transferOut.appendText("Przelew nieudany\n");
                }
            } catch (Exception ex) {
                transferOut.appendText("Błąd: " + ex.getMessage() + "\n");
            }
        });

        Button genBtn = new Button("Wygeneruj hasła");
        genBtn.setOnAction(e -> {
            try {
                bank.generatePasswords(pesel);
                List<String> pwds = bank.getPasswords(pesel);
                Stage dialog = new Stage();
                dialog.setTitle("Hasła jednorazowe");
                VBox dpane = new VBox(8);
                dpane.setPadding(new Insets(10));
                TextArea pwdArea = new TextArea();
                pwdArea.setEditable(false);
                for (String p : pwds) pwdArea.appendText(p + "\n");
                dpane.getChildren().addAll(new Label("Twoje nowe hasła:"), pwdArea);
                dialog.setScene(new Scene(dpane, 300, 200));
                dialog.show();
            } catch (Exception ex) {
            }
        });

        Button logoutBtn = new Button("Wyloguj");
        logoutBtn.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        vbox.getChildren().addAll(
                nameLabel, surnameLabel, peselLabel, nrKontaLabel, balanceLabel,
                refreshBtn,
                new Separator(),
                new Label("Przelew:"),
                transferGrid,
                genBtn,
                logoutBtn
        );

        updateClientInfo(nameLabel, surnameLabel, peselLabel, nrKontaLabel, balanceLabel);
        return new Scene(vbox, 450, 600);
    }

    private void updateClientInfo(Label nameLabel, Label surnameLabel, Label peselLabel, Label nrKontaLabel, Label balanceLabel) {
        try {
            ClientInfo info = bank.get(pesel);
            nameLabel.setText("Imię: " + info.imie);
            surnameLabel.setText("Nazwisko: " + info.nazwisko);
            peselLabel.setText("PESEL: " + info.pesel);
            nrKontaLabel.setText("Numer konta: " + info.nrKonta);
            balanceLabel.setText(String.format("Saldo: %.2f PLN", info.oszczednosci));
        } catch (RemoteException ex) {
            balanceLabel.setText("Błąd: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}