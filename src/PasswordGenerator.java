package generator;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String AMBIGUOUS = "O0Il1";

    private int length;
    private boolean useUpper;
    private boolean useLower;
    private boolean useDigits;
    private boolean useSpecial;
    private boolean excludeAmbiguous;

    private SecureRandom random;

    public PasswordGenerator(int length, boolean useUpper, boolean useLower, boolean useDigits, boolean useSpecial, boolean excludeAmbiguous) {
        this.length = length;
        this.useUpper = useUpper;
        this.useLower = useLower;
        this.useDigits = useDigits;
        this.useSpecial = useSpecial;
        this.excludeAmbiguous = excludeAmbiguous;
        this.random = new SecureRandom();
    }

    public String generate() {
        StringBuilder charPool = new StringBuilder();
        if (useUpper) charPool.append(UPPER);
        if (useLower) charPool.append(LOWER);
        if (useDigits) charPool.append(DIGITS);
        if (useSpecial) charPool.append(SPECIAL);

        String pool = charPool.toString();
        if (excludeAmbiguous) {
            StringBuilder filtered = new StringBuilder();
            for (char c : pool.toCharArray()) {
                if (AMBIGUOUS.indexOf(c) < 0) filtered.append(c);
            }
            pool = filtered.toString();
        }

        if (pool.isEmpty()) {
            throw new IllegalArgumentException("Character pool is empty. Enable at least one character type.");
        }

        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(pool.length());
            password.append(pool.charAt(index));
        }

        return password.toString();
    }
}
