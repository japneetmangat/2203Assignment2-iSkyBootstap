package se2203.isky2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Set;

public class HelloController {

    // ---------------- FXML fields (match fx:id exactly) ----------------
    @FXML private TextField MasterUsername;
    @FXML private TextField ContactEmail;

    // New password (pair)
    @FXML private PasswordField NewPasswordPF;
    @FXML private TextField NewPasswordTF;
    @FXML private CheckBox Show1;

    // Confirm password (pair)
    @FXML private PasswordField ConfirmPasswordPF;
    @FXML private TextField ConfirmPasswordTF;
    @FXML private CheckBox Show2;

    // Password rules labels
    @FXML private Label Characters;
    @FXML private Label Common;
    @FXML private Label Same;
    @FXML private Label Spaces;

    // Buttons
    @FXML private Button CreateMasterAccount;
    @FXML private Button Exit;

    // ---------------- Rules / constants ----------------
    private static final int MIN_LEN = 12;

    // Small weak list for prototype (good enough for assignment)
    private static final Set<String> WEAK_PASSWORDS = Set.of(
            "password", "password123", "qwerty", "letmein", "admin", "welcome",
            "1234567890", "", "abc123"
    );

    @FXML
    public void initialize() {
        // Start disabled until valid
        CreateMasterAccount.setDisable(true);

        // Keep mirrors synced for each password box
        bindMirror(NewPasswordPF, NewPasswordTF);
        bindMirror(ConfirmPasswordPF, ConfirmPasswordTF);

        // Start hidden (PasswordFields visible)
        setShowNewPassword(false);
        setShowConfirmPassword(false);

        // Independent show toggles
        Show1.selectedProperty().addListener((obs, oldV, show) -> {
            setShowNewPassword(show);
            validateForm();
        });

        Show2.selectedProperty().addListener((obs, oldV, show) -> {
            setShowConfirmPassword(show);
            validateForm();
        });

        // Validate live on all text changes
        MasterUsername.textProperty().addListener((obs, o, n) -> validateForm());
        ContactEmail.textProperty().addListener((obs, o, n) -> validateForm());

        NewPasswordPF.textProperty().addListener((obs, o, n) -> validateForm());
        NewPasswordTF.textProperty().addListener((obs, o, n) -> validateForm());

        ConfirmPasswordPF.textProperty().addListener((obs, o, n) -> validateForm());
        ConfirmPasswordTF.textProperty().addListener((obs, o, n) -> validateForm());

        // Button actions
        Exit.setOnAction(e -> closeWindow());
        CreateMasterAccount.setOnAction(e -> handleCreate());

        // First validation pass
        validateForm();
    }

    // ---------------- Show / Hide helpers ----------------
    private void setShowNewPassword(boolean show) {
        NewPasswordTF.setVisible(show);
        NewPasswordTF.setManaged(show);
        NewPasswordPF.setVisible(!show);
        NewPasswordPF.setManaged(!show);
    }

    private void setShowConfirmPassword(boolean show) {
        ConfirmPasswordTF.setVisible(show);
        ConfirmPasswordTF.setManaged(show);
        ConfirmPasswordPF.setVisible(!show);
        ConfirmPasswordPF.setManaged(!show);
    }

    private void bindMirror(PasswordField pf, TextField tf) {
        // PF -> TF
        pf.textProperty().addListener((obs, oldV, newV) -> {
            if (!tf.getText().equals(newV)) tf.setText(newV);
        });
        // TF -> PF
        tf.textProperty().addListener((obs, oldV, newV) -> {
            if (!pf.getText().equals(newV)) pf.setText(newV);
        });
    }

    // ---------------- Validation ----------------
    private void validateForm() {
        String username = safeTrim(MasterUsername.getText());
        String email = safeTrim(ContactEmail.getText());

        String pw = getNewPassword();
        String confirm = getConfirmPassword();

        // Rule checks
        boolean lenOk = pw.length() >= MIN_LEN;
        boolean commonOk = !isWeak(pw);
        boolean notSameAsUsernameOk = !username.isEmpty() && !pw.equalsIgnoreCase(username);
        boolean spacesAllowedOk = true; // informational only

        // Update rule label colors
        setRuleColor(Characters, lenOk);
        setRuleColor(Common, commonOk);
        setRuleColor(Same, notSameAsUsernameOk);
        setRuleColor(Spaces, spacesAllowedOk);

        // Form checks
        boolean usernameOk = !username.isEmpty();
        boolean emailOk = email.isEmpty() || looksLikeEmail(email);
        boolean pwOk = lenOk && commonOk && notSameAsUsernameOk;
        boolean confirmOk = !confirm.isEmpty() && pw.equals(confirm);

        // Highlight invalid inputs (optional but helpful)
        markInvalid(MasterUsername, usernameOk);
        markInvalid(getActiveNewPasswordControl(), pwOk || pw.isEmpty());
        markInvalid(getActiveConfirmPasswordControl(), confirmOk || confirm.isEmpty());
        markInvalid(ContactEmail, emailOk);

        boolean formValid = usernameOk && emailOk && pwOk && confirmOk;
        CreateMasterAccount.setDisable(!formValid);
    }

    private String getNewPassword() {
        String s = Show1.isSelected() ? NewPasswordTF.getText() : NewPasswordPF.getText();
        return s == null ? "" : s;
    }

    private String getConfirmPassword() {
        String s = Show2.isSelected() ? ConfirmPasswordTF.getText() : ConfirmPasswordPF.getText();
        return s == null ? "" : s;
    }

    private Control getActiveNewPasswordControl() {
        return Show1.isSelected() ? NewPasswordTF : NewPasswordPF;
    }

    private Control getActiveConfirmPasswordControl() {
        return Show2.isSelected() ? ConfirmPasswordTF : ConfirmPasswordPF;
    }

    // ---------------- Create / Exit ----------------
    private void handleCreate() {
        // Final safety check
        if (CreateMasterAccount.isDisable()) {
            showAlert(Alert.AlertType.ERROR,
                    "Form not valid",
                    "Please complete all required fields and ensure the password rules are met.");
            return;
        }

        // Required modal dialog
        showAlert(Alert.AlertType.INFORMATION,
                "Master account created",
                "Continue to login to sign in as the master user.");

        // Close window
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) Exit.getScene().getWindow();
        stage.close();
    }

    // ---------------- UI helpers ----------------
    private void setRuleColor(Label label, boolean ok) {
        // Match your FXML red/green scheme
        label.setStyle(ok ? "-fx-text-fill: #2cb22c;" : "-fx-text-fill: #fc0606;");
    }

    private void markInvalid(Control c, boolean ok) {
        if (c == null) return;
        c.setStyle(ok ? "" : "-fx-border-color: #fc0606; -fx-border-width: 1.2; -fx-border-radius: 4;");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ---------------- Validation helpers ----------------
    private boolean isWeak(String pw) {
        String s = pw.trim().toLowerCase();
        if (WEAK_PASSWORDS.contains(s)) return true;
        if (s.matches("\\d+")) return true;         // all digits
        if (s.contains("password")) return true;
        if (s.length() <= 8) return true;           // extra strict "weak" heuristic
        return false;
    }

    private boolean looksLikeEmail(String email) {
        // Simple heuristic (fine for prototype)
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}