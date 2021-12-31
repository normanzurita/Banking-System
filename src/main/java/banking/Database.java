package banking;

import java.sql.*;

public class Database {

    private static Database instance;
    Connection conn = null;

    // Constructor
    private Database(String url) {
        try {
            this.conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        this.createDB();
    }

    // Lazy initialization
    public static Database getInstance(String url) {
        if (instance == null) {
            instance = new Database(url);
        }
        return instance;
    }

    public static Database getInstance() {
        return instance;
    }

    private void createDB() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS card (\n" +
                "    id INTEGER PRIMARY KEY,\n" +
                "    number TEXT,\n" +
                "    pin TEXT,\n" +
                "    balance INTEGER DEFAULT 0\n" +
                ");";

        try (Statement statement = this.conn.createStatement()) {
            // create a new table
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String cardNumber, String pin) {
        String sql = "INSERT INTO card (number, pin) VALUES (?, ?)";
        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean accountIdentifierExists(String accountIdentifier) {
        String sql = "SELECT number from card";

        try (Statement statement = this.conn.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    if (accountIdentifier.equals(resultSet.getString("number").substring(6,14))) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean cardNumberExists(String cardNumber) {
        String sql = "SELECT number FROM card WHERE number = ?";

        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean checkPin(String cardNumber, String pin) {
        String sql = "SELECT pin FROM card WHERE number = ?";

        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (pin.equals(resultSet.getString("pin"))) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String getBalance(String cardNumber) {
        String sql = "SELECT balance FROM card WHERE number = ?";

        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getString("balance");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addIncome(String cardNumber, int income) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, income);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void doTransfer(String cardNumber, String receiverCardNumber, int amount) {
        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        sql = new String("UPDATE card SET balance = balance + ? WHERE number = ?");
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, receiverCardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeAccount(String cardNumber) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (PreparedStatement preparedStatement = this.conn.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
