import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

class Player {
    private UUID id; // Unique identifier for the player.
    private int coinsBalance; // Current balance of coins in the player's account.
    private int totalBets; // Total number of bets placed by the player.
    private int wonBets; // Total number of bets won by the player.
    private boolean isLegitimate; // Flag indicating whether the player is legitimate.
    private String firstIllegalOperation; // Details of the first illegal operation.

    /**
     * Enum representing valid bet sides (A or B).
     */
    enum BetSide {
        A, B
    }

    /**
     * Represents all possible player operations.
     */
    enum Operation {
        DEPOSIT, WITHDRAW, BET
    }

    // Map to track whether a player has placed a bet on a specific match.
    private Map<UUID, Boolean> betsPlaced = new HashMap<>();

    /**
     * Constructor to initialize a new player with a given ID and default values.
     *
     * @param playerId The unique identifier for the player.
     */
    Player(UUID playerId) {
        this.id = playerId;
        this.coinsBalance = 0;
        this.totalBets = 0;
        this.wonBets = 0;
        this.isLegitimate = true;
    }

    /**
     * Method to deposit coins into the player's account.
     *
     * @param amount The amount of coins to deposit.
     */
    void deposit(int amount) {
        this.coinsBalance += amount;
    }

    /**
     * Method to withdraw coins from the balance.
     *
     * @param amount The amount of coins to withdraw.
     * @return True if the withdrawal is successful, false if there is insufficient
     *         balance.
     */
    boolean withdraw(int amount) {
        // Check if player has sufficient balance for withdrawal.
        if (this.coinsBalance >= amount) {
            this.coinsBalance -= amount;
            return true;
        } else {
            // Insufficient balance to withdraw.
            return false;
        }
    }

    /**
     * Method to place a bet by deducting the bet amount from the balance.
     *
     * @param matchId The unique identifier of the match to bet on.
     * @param amount  The amount of coins to bet.
     * @param betSide The side of the bet (A or B).
     * @return True if the bet is successfully placed, false if the player has
     *         already bet on the same match or has insufficient balance.
     */
    boolean placeBet(UUID matchId, int amount, BetSide betSide) {
        // Check if the player has already placed a bet on current match.
        if (betsPlaced.containsKey(matchId)) {
            // Player cannot place another bet on the same match.
            return false;
        }

        // Check if the player has sufficient balance to place the bet.
        if (this.coinsBalance >= amount) {
            this.coinsBalance -= amount;
            this.totalBets++;
            // Mark that the player has placed a bet on current match.
            betsPlaced.put(matchId, true);
            return true;
        } else {
            // Insufficient balance to place the bet.
            return false;
        }
    }

    /**
     * Method to handle the winning bet.
     *
     * @param winnings The amount of coins won in the bet.
     */
    void winBet(int winnings) {
        this.coinsBalance += winnings;
        this.wonBets++;
    }

    /**
     * Getter method to get the player's ID.
     *
     * @return The unique identifier for the player.
     */
    UUID getId() {
        return this.id;
    }

    /**
     * Getter method to get the player's current balance.
     *
     * @return The current balance of coins in the player's account.
     */
    int getBalance() {
        return this.coinsBalance;
    }

    /**
     * Getter method to check if the player is legitimate.
     *
     * @return True if the player is legitimate, false otherwise.
     */
    boolean isLegitimate() {
        return this.isLegitimate;
    }

    /**
     * Method to calculate and return the player's win rate.
     *
     * @return The win rate of the player, rounded to two decimal places.
     */
    double getWinRate() {
        if (this.totalBets > 0) {
            double winRate = (double) this.wonBets / this.totalBets;
            winRate = Math.floor(winRate * 100) / 100;
            return winRate;
        } else {
            return 0;
        }
    }

    /**
     * Getter method to get the details of the first illegal operation.
     *
     * @return The details of the first illegal operation if the player is marked as
     *         illegitimate, otherwise null.
     */
    String getFirstIllegalOperation() {
        return this.firstIllegalOperation;
    }

    /**
     * Setter method to mark the player as illegitimate and save the details of the
     * first illegal operation.
     *
     * @param data Details of the first illegal operation.
     */
    void setFirstIllegalOperation(String data) {
        this.isLegitimate = false;
        if (this.firstIllegalOperation == null) {
            this.firstIllegalOperation = data;
        }
    }
}

class Match {
    private UUID id; // Unique identifier for the match.
    private double rateA; // Betting rate for side A.
    private double rateB; // Betting rate for side B.
    private String result; // Result of the match (A, B, DRAW).

    /**
     * Constructor to initialize a new match with the ID, rates, and the result.
     *
     * @param matchId The unique identifier for the match.
     * @param rateA   The betting rate for side A.
     * @param rateB   The betting rate for side B.
     * @param result  The result of the match (A, B, or DRAW).
     */
    Match(UUID matchId, double rateA, double rateB, String result) {
        this.id = matchId;
        this.rateA = rateA;
        this.rateB = rateB;
        this.result = result;
    }

    /**
     * Getter method to get the match's ID.
     *
     * @return The unique identifier for the match.
     */
    UUID getId() {
        return this.id;
    }

    /**
     * Getter method to get the betting rate for side A.
     *
     * @return The betting rate for side A.
     */
    double getRateA() {
        return this.rateA;
    }

    /**
     * Getter method to get the betting rate for side B.
     *
     * @return The betting rate for side B.
     */
    double getRateB() {
        return this.rateB;
    }

    /**
     * Getter method to get the result of the match.
     *
     * @return The result of the match (A, B, or DRAW).
     */
    String getResult() {
        return this.result;
    }
}

class Bet {
    private UUID playerId; // The unique identifier of the player making the bet.
    private UUID matchId; // The unique identifier of the match being bet on.
    private int betAmount; // The amount of coins being bet.
    private String betSide; // The side of the best (A or B).

    /**
     * Constructor to initialize a new bet with the player and match IDs, bet
     * amount, and the bet side.
     *
     * @param playerId  The unique identifier of the player making the bet.
     * @param matchId   The unique identifier of the match being bet on.
     * @param betAmount The amount of coins being bet.
     * @param betSide   The side of the bet (A or B).
     */
    Bet(UUID playerId, UUID matchId, int betAmount, String betSide) {
        this.playerId = playerId;
        this.matchId = matchId;
        this.betAmount = betAmount;
        this.betSide = betSide;
    }

    /**
     * Getter method to get the player's ID associated with the bet.
     *
     * @return The unique identifier of the player making the bet.
     */
    UUID getPlayerId() {
        return this.playerId;
    }

    /**
     * Getter method to get the match's ID associated with the bet.
     *
     * @return The unique identifier of the match being bet on.
     */
    UUID getMatchId() {
        return this.matchId;
    }

    /**
     * Getter method to get the bet amount.
     *
     * @return The amount of coins being bet.
     */
    int getBetAmount() {
        return this.betAmount;
    }

    /**
     * Getter method to get the side of the bet.
     *
     * @return The side of the bet (A or B).
     */
    String getBetSide() {
        return this.betSide;
    }
}

public class Main {
    // Constants for input and output files.
    private static final String MATCH_DATA_FILE = "data/match_data.txt";
    private static final String PLAYER_DATA_FILE = "data/player_data.txt";
    private static final String RESULTS_DATA_FILE = "src/results.txt";

    Map<UUID, Match> matches = new HashMap<>();
    Map<UUID, Player> players = new HashMap<>();
    List<Bet> bets = new ArrayList<>();

    // Overall balance of the casino.
    int casinoBalance = 0;

    public static void main(String[] args) throws Exception {
        Main bettingApp = new Main();

        // Read match and player data, calculate results and write to file.
        bettingApp.readMatchData(MATCH_DATA_FILE);
        bettingApp.readPlayerData(PLAYER_DATA_FILE);
        bettingApp.calculateBettingResults();
        bettingApp.writeToFile(RESULTS_DATA_FILE);
    }

    // Method to read match data from the file.
    void readMatchData(String fileName) {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Read each line from the file.
            while ((line = reader.readLine()) != null) {
                // Split the line into parts using comma as the delimiter.
                String[] parts = line.split(",");

                // Extract information from the parts.
                UUID matchId = UUID.fromString(parts[0]);
                double rateA = Double.parseDouble(parts[1]);
                double rateB = Double.parseDouble(parts[2]);
                String result = parts[3];

                // Create a Match object and store it in the matches map.
                Match match = new Match(matchId, rateA, rateB, result);
                matches.put(matchId, match);
            }
        } catch (FileNotFoundException e) {
            // Handle the case where the file is not found.
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read player data from the file.
    void readPlayerData(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Read each line from the file.
            while ((line = reader.readLine()) != null) {
                // Split the line into parts using comma as the delimiter.
                String[] parts = line.split(",");

                // Extract information from the parts.
                UUID playerId = UUID.fromString(parts[0]);
                Player.Operation playerOperation = Player.Operation.valueOf(parts[1]);

                /*
                 * Get existing player or create a new Player object if ID doesn't exist and
                 * store it in the matches map.
                 */
                Player player = players.getOrDefault(playerId, new Player(playerId));

                // Switch statement based on player operations.
                switch (playerOperation) {
                    case DEPOSIT:
                        // Extract deposit amount and call player's deposit method.
                        int depositAmount = Integer.parseInt(parts[3]);
                        player.deposit(depositAmount);
                        break;
                    case WITHDRAW:
                        // Extract withdrawal amount and call player's withdraw method.
                        int withdrawAmount = Integer.parseInt(parts[3]);
                        // If withdrawal fails, set the first illegal operation.
                        if (!player.withdraw(withdrawAmount)) {
                            player.setFirstIllegalOperation("WITHDRAW null " + withdrawAmount + " null");
                        }
                        break;
                    case BET:
                        UUID matchId = UUID.fromString(parts[2]);
                        int betAmount = Integer.parseInt(parts[3]);
                        Player.BetSide betSide = Player.BetSide.valueOf(parts[4]);
                        // If the player successfully places the bet, add the bet to the list.
                        if (player.placeBet(matchId, betAmount, betSide)) {
                            Bet bet = new Bet(playerId, matchId, betAmount, betSide.toString());
                            bets.add(bet);
                        } else {
                            // If placing the bet fails, set the first illegal operation.
                            player.setFirstIllegalOperation("BET " + matchId + " " + betAmount + " " + betSide);
                        }
                        break;

                }
                // Update the players map with the player data.
                players.put(playerId, player);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to calculate betting results based on match outcomes and player bets.
    void calculateBettingResults() {
        for (Bet bet : bets) {
            // Retrieve the match associated with the current bet.
            Player player = players.get(bet.getPlayerId());
            if (player.isLegitimate()) {
                Match match = matches.get(bet.getMatchId());
                double rate;

                // Determine the rate based on the side of the bet (A or B).
                if (bet.getBetSide().equals("A")) {
                    rate = match.getRateA();
                } else {
                    rate = match.getRateB();
                }
                // Check if the bet matched the match result.
                if (bet.getBetSide().equals(match.getResult())) {
                    // Calculate the winnings to the player.
                    int winnings = (int) (bet.getBetAmount() * rate);
                    player.winBet((int) (winnings + bet.getBetAmount()));
                    // Update the casino balance by deducting player winnings.
                    casinoBalance -= winnings;
                } else if (match.getResult().equals("DRAW")) {
                    // In case of a draw, return the bet amount to the player.
                    player.deposit((int) (bet.getBetAmount()));
                } else {
                    // If the player lost the bet, add the bet amount to the casino balance.
                    casinoBalance += bet.getBetAmount();
                }
            }
        }
    }

    // Method to write player data and casino balance to a file.
    void writeToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write legitimate player data to the file.
            boolean legitimatePlayersExist = false;
            for (Map.Entry<UUID, Player> entry : players.entrySet()) {
                Player player = entry.getValue();
                // Before writing, check if the player is legitimate.
                if (player.isLegitimate()) {
                    // Format the win rate with two decimal places and replace '.' with ','.
                    String winRateString = String.format("%.2f", player.getWinRate());
                    winRateString = winRateString.replace('.', ',');
                    // Write player ID, balance and win rate to the file.
                    writer.write(player.getId() + " " + player.getBalance() + " " + winRateString + "\n");
                    legitimatePlayersExist = true;
                }
            }
            writer.newLine();
            if (!legitimatePlayersExist) {
                writer.newLine();
            }
            // Write illegitimate player data to the file.
            boolean illegitimatePlayersExist = false;
            for (Map.Entry<UUID, Player> entry : players.entrySet()) {
                Player player = entry.getValue();
                // Before writing, check if the player is not legitimate.
                if (!player.isLegitimate()) {
                    // Write player ID and details of the first illegal operation to the file.
                    writer.write(player.getId() + " " + player.getFirstIllegalOperation() + "\n");
                    illegitimatePlayersExist = true;
                }
            }
            writer.newLine();
            if (!illegitimatePlayersExist) {
                writer.newLine();
            }
            // Write the casino balance to the file.
            writer.write(String.valueOf(casinoBalance));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}