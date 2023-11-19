package com.bettingapp;

import java.util.UUID;

public class Bet {
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
