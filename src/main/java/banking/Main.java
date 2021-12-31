package banking;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        final String dbFileName;

        if (args.length == 2 && "-fileName".equals(args[0])) {
            dbFileName = args[1];
        } else {
            dbFileName = "card.s3db";
        }

        Database db = Database.getInstance("jdbc:sqlite:" + dbFileName);

        boolean exit = false;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("1. Create an account\n" + "2. Log into account\n" + "0. Exit");

            Card card;

            int input = scanner.nextInt();scanner.nextLine();

            switch(input) {
                case 1:
                    card = new Card();
                    db.insert(card.getCardNumber(), card.getPin());

                    System.out.println("\nYour card has been created\n" + "Your card number:\n" + card.getCardNumber());
                    System.out.println("Your card PIN:\n"+ card.getPin() + "\n");
                    break;

                case 2:
                    System.out.println("\nEnter your card number:");
                    String cardNumber = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String pin = scanner.nextLine();

                    // check whether of not the card exists and the pin is correct
                    if (db.cardNumberExists(cardNumber) && db.checkPin(cardNumber, pin)) {
                        System.out.println("You have successfully logged in!");

                        boolean logOut = false;
                        do {
                            System.out.println("1. Balance\n" + "2. Add income\n" + "3. Do transfer\n" + "4. Close account\n" + "5. Log out\n" + "0. Exit");
                            int option = scanner.nextInt();scanner.nextLine();
                            switch (option) {
                                case 1:
                                    System.out.println("Balance: " + db.getBalance(cardNumber));
                                    break;
                                case 2:
                                    System.out.println("Enter income:");
                                    int income = scanner.nextInt();
                                    db.addIncome(cardNumber, income);
                                    System.out.println("Income was added!");
                                    break;
                                case 3:
                                    System.out.println("Transfer\n" + "Enter card number:");
                                    String receiverCardNumber = scanner.nextLine();
                                    if (!receiverCardNumber.equals(cardNumber)) {
                                        if (receiverCardNumber.substring(15).equals(Card.computeCheckSum(receiverCardNumber.substring(0,15)))) {
                                            if (db.cardNumberExists(receiverCardNumber)) {
                                                System.out.println("Enter how much money you want to transfer:");
                                                int amount = scanner.nextInt();
                                                if (amount > Integer.parseInt(db.getBalance(cardNumber))) {
                                                    System.out.println("Not enough money!");
                                                } else {
                                                    db.doTransfer(cardNumber, receiverCardNumber, amount);
                                                }

                                            } else {
                                                System.out.println("Such a card does not exist");
                                            }

                                        } else {
                                            System.out.println("Probably you made a mistake in the card number. Please try again!");
                                        }
                                    } else {
                                        System.out.println("You can't transfer money to the same account!");
                                    }

                                    break;
                                case 4:
                                    db.closeAccount(cardNumber);
                                    System.out.println("\nThe account has been closed");
                                    break;
                                case 5:
                                    logOut = true;
                                    System.out.println("\nYou have successfully logged out!");
                                    break;
                                case 0:
                                    exit = true;
                                    System.out.println("Bye!");
                                    break;
                            }
                        } while (!logOut && !exit); // if any logOut or exit is true break the while;

                    } else {
                        System.out.println("Wrong card number or PIN");
                    }
                    break;
                case 0:
                    System.out.println("Bye!");
                    exit = true;
                    break;
            }
        } while(!exit);
    }
}
