import java.util.*;
import java.io.*;

class Player {
    String playerId;
    long balance;
    int totalBets;
    int wonBets;

    Player(String playerId) {
        this.playerId = playerId;
        this.balance = 0;
        this.totalBets = 0;
        this.wonBets = 0;
    }

    void deposit(long amount) {
        this.balance += amount;
    }

    boolean withdraw(long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        } else {
            return false;
        }
    }

    boolean placeBet(long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            this.totalBets += amount;
            return true;
        } else {
            return false;
        }
    }

    void winBet(long winnings) {
        this.balance += winnings;
        this.wonBets++;
    }

    String getId() {
        return this.playerId;
    }

    long getBalance() {
        return this.balance;
    }

}

class Match {
    String matchId;
    double rateA;
    double rateB;
    String result;

    Match(String matchId, double rateA, double rateB, String result) {
        this.matchId = matchId;
        this.rateA = rateA;
        this.rateB = rateB;
        this.result = result;
    }
}

class Bet {
    String playerId;
    String matchId;
    long amount;
    String bet;

    Bet(String playerId, String matchId, long amount, String bet) {
        this.playerId = playerId;
        this.matchId = matchId;
        this.amount = amount;
        this.bet = bet;
    }
}

public class Main {
    Map<String, Match> matches = new HashMap<>();
    Map<String, Player> players = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Main bettingApp = new Main();

        bettingApp.readMatchData("data/match_data.txt");
        bettingApp.readPlayerData("data/player_data.txt");

        System.out.println("-------------------- MATCH DATA --------------------");
        bettingApp.printMatchData();
        System.out.println("-------------------- PLAYER DATA --------------------");
        bettingApp.printPlayerData();
    }

    void readMatchData(String fileName) {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String matchId = parts[0];
                double rateA = Double.parseDouble(parts[1]);
                double rateB = Double.parseDouble(parts[2]);
                String result = parts[3];

                Match match = new Match(matchId, rateA, rateB, result);
                matches.put(matchId, match);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readPlayerData(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String playerId = parts[0];
                Player player = players.getOrDefault(playerId, new Player(playerId));

                /// TODO

                players.put(playerId, player);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printPlayerData() {
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();

            System.out.println("Player ID: " + player.playerId);
            System.out.println("Balance: " + player.balance);
            System.out.println("Total Bets: " + player.totalBets);
            System.out.println("Won Bets: " + player.wonBets);
        }
    }

    void printMatchData() {
        for (Map.Entry<String, Match> entry : matches.entrySet()) {
            Match match = entry.getValue();

            System.out.println("Match ID: " + match.matchId);
            System.out.println("Rate A: " + match.rateA);
            System.out.println("Rate B: " + match.rateB);
            System.out.println("Result: " + match.result);

        }
    }
}