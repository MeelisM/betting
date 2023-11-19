package com.bettingapp;

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

public class Main {
    // Constants for input and output files.
    private static final String MATCH_DATA_FILE = "resource/match_data.txt";
    private static final String PLAYER_DATA_FILE = "resource/player_data.txt";
    private static final String RESULTS_DATA_FILE = "src/com/bettingapp/results.txt";

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