package com.bettingapp;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class Player {
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
