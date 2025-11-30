package generator;

public class PasswordStrengthEstimator {
    public static String estimateStrength(String password) {
        int score = 0;
        int length = password.length();

        if (length >= 8) score += 20;
        if (length >= 12) score += 20;

        if (password.chars().anyMatch(Character::isLowerCase)) score += 20;
        if (password.chars().anyMatch(Character::isUpperCase)) score += 20;
        if (password.chars().anyMatch(Character::isDigit)) score += 10;
        if (password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:,.<>?".indexOf(c) >= 0)) score += 10;

        if (score >= 80) return "STRONG";
        else if (score >= 50) return "MEDIUM";
        else return "WEAK";
    }
}
