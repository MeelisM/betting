package com.bettingapp;

import java.util.UUID;

public class Match {
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
