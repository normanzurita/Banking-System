package banking;

import java.util.Random;

public class Card {

    private final String pin;
    private final String cardNumber;

    Database db = Database.getInstance();

    Card () {
        String accountIdentifier = this.createAccountIdentifier();
        String BIN = "400000";
        String checkSum = this.computeCheckSum(BIN + accountIdentifier);
        this.pin = this.createPin();
        this.cardNumber = BIN + accountIdentifier + checkSum;
    }

    private String createAccountIdentifier() {
        Random random = new Random();
        StringBuilder accountIdentifier = new StringBuilder();
        while (true) {
            accountIdentifier.setLength(0); // resets builder
            for (int i = 0; i < 9; i++) {
                accountIdentifier.append(random.nextInt(10));
            }
            if (!db.accountIdentifierExists(accountIdentifier.toString())) {
                break;
            }
        }
        return accountIdentifier.toString();
    }

    public static String computeCheckSum(String cardNumber) {
        int[] numbers = new int[cardNumber.length()];

        for (int i = 0; i < cardNumber.length(); i++) {
            numbers[i] = Character.getNumericValue(cardNumber.charAt(i));
        }

        for (int i = 0; i < numbers.length; i++) {
            if ((i + 1) % 2 != 0) {
                numbers[i] = numbers[i] * 2;
            }
        }
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > 9) {
                numbers[i] -= 9;
            }
        }
        int sum = 0;
        for (int number: numbers) {
            sum += number;
        }
        return Integer.toString((10 - (sum % 10)) % 10) ;
    }

    private String createPin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
    public String getPin() {
        return this.pin;
    }
}
