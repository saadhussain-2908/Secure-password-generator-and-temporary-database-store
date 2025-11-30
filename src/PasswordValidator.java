package validator;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {
    private List<String> oldPasswords;

    public PasswordValidator(List<String> oldPasswords) {
        this.oldPasswords = oldPasswords;
    }

    public boolean isReused(String newPassword) {
        return oldPasswords.contains(newPassword);
    }

    public List<String> suggestImprovements(String password) {
        List<String> suggestions = new ArrayList<>();
        if (password.length() < 12) suggestions.add("Increase length to 12+ characters.");
        if (!password.chars().anyMatch(Character::isUpperCase)) suggestions.add("Add uppercase letters.");
        if (!password.chars().anyMatch(Character::isLowerCase)) suggestions.add("Add lowercase letters.");
        if (!password.chars().anyMatch(Character::isDigit)) suggestions.add("Add numbers.");
        if (!password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:,.<>?".indexOf(c) >= 0)) suggestions.add("Add special characters.");
        return suggestions;
    }
}
