# Betting calculator

Reads player and match data from `.txt` files. Calculates the betting results and writes them to a file.

## How to use

1. Input files:

-   Match data should be provided in the `"resource/match_data.txt"` file.
-   Player data should be provided in the `"resource/player_data.txt"` file.

2. Run the application:

-   Execute the `main` method in the `Main` class.
-   Betting results will be calculated based on match outcomes and player bets.

3. Output File:

-   Legitimate player data, including player ID, balance and win rate (rounded to two decimal places), will be written to the `"src/com/bettingapp/results.txt"` file.
-   Illegitimate player data, including player ID and details of the first illegal operation, will also be included in the output file.
-   The balance of the casino will be written to the end of the file.
