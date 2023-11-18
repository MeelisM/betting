import java.util.*;
import java.io.*;

class Player {
    String Id;
    int coinsBalance;
    int totalBets;
    int wonBets;
    boolean isLegitimate;
    String firstIllegalAction;

    Player(String Id) {
        this.Id = Id;
        this.coinsBalance = 0;
        this.totalBets = 0;
        this.wonBets = 0;
        this.isLegitimate = true;
    }

    void deposit(int amount) {
        this.coinsBalance += amount;
    }

    boolean withdraw(int amount) {
        if (this.coinsBalance >= amount) {
            this.coinsBalance -= amount;
            return true;
        } else {
            return false;
        }
    }

    boolean placeBet(int amount) {
        if (this.coinsBalance >= amount) {
            this.coinsBalance -= amount;
            this.totalBets++;
            return true;
        } else {
            return false;
        }
    }

    void winBet(int winnings) {
        this.coinsBalance += winnings;
        this.wonBets++;
    }

    String getId() {
        return this.Id;
    }

    long getBalance() {
        return this.coinsBalance;
    }

    boolean isLegitimate() {
        return this.isLegitimate;
    }

    double getWinRate() {
        if (this.totalBets > 0) {
            double winRate = (double) this.wonBets / this.totalBets;
            winRate = Math.floor(winRate * 100) / 100;
            return winRate;
        } else {
            return 0;
        }
    }

    String getFirstIllegalAction() {
        return this.firstIllegalAction;
    }

    void setFirstIllegalAction(String data) {
        this.isLegitimate = false;
        if (this.firstIllegalAction == null) {
            this.firstIllegalAction = data;
        }
    }
}

class Match {
    String Id;
    double rateA;
    double rateB;
    String result;

    Match(String Id, double rateA, double rateB, String result) {
        this.Id = Id;
        this.rateA = rateA;
        this.rateB = rateB;
        this.result = result;
    }

    String getId() {
        return this.Id;
    }

    double getRateA() {
        return this.rateA;
    }

    double getRateB() {
        return this.rateB;
    }

    String getResult() {
        return this.result;
    }
}

class Bet {
    String playerId;
    String matchId;
    int betAmount;
    String betSide;

    Bet(String playerId, String matchId, int betAmount, String betSide) {
        this.playerId = playerId;
        this.matchId = matchId;
        this.betAmount = betAmount;
        this.betSide = betSide;
    }

    String getPlayerId() {
        return this.playerId;
    }

    String getMatchId() {
        return this.matchId;
    }

    long getBetAmount() {
        return this.betAmount;
    }

    String getBetSide() {
        return this.betSide;
    }
}

public class Main {
    Map<String, Match> matches = new HashMap<>();
    Map<String, Player> players = new HashMap<>();
    List<Bet> bets = new ArrayList<>();

    int casinoBalance = 0;

    public static void main(String[] args) throws Exception {
        Main bettingApp = new Main();

        bettingApp.readMatchData("data/match_data.txt");
        bettingApp.readPlayerData("data/player_data.txt");
        bettingApp.calculateBettingResults();
        bettingApp.writeToFile("src/results.txt");

        System.out.println("-------------------- MATCH DATA --------------------");
        System.out.println();
        bettingApp.printMatchData();
        System.out.println();

        System.out.println("-------------------- PLAYER DATA --------------------");
        System.out.println();
        bettingApp.printPlayerData();
        System.out.println();

        System.out.println("-------------------- BET RESULTS --------------------");
        System.out.println();
        bettingApp.printBetResults();

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
                String playerAction = parts[1];
                Player player = players.getOrDefault(playerId, new Player(playerId));

                switch (playerAction) {
                    case "DEPOSIT":
                        int depositAmount = Integer.parseInt(parts[3]);
                        player.deposit(depositAmount);
                        break;
                    case "WITHDRAW":
                        int withdrawAmount = Integer.parseInt(parts[3]);
                        if (!player.withdraw(withdrawAmount)) {
                            player.setFirstIllegalAction("WITHDRAW null " + withdrawAmount + " null");
                        }
                        break;
                    case "BET":
                        String matchId = parts[2];
                        int betAmount = Integer.parseInt(parts[3]);
                        String betSide = parts[4];
                        if (player.placeBet(betAmount)) {
                            Bet bet = new Bet(playerId, matchId, betAmount, betSide);
                            bets.add(bet);
                        } else {
                            player.setFirstIllegalAction("BET " + matchId + " " + betAmount + " " + betSide);
                        }
                        break;

                }

                players.put(playerId, player);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void calculateBettingResults() {
        for (Bet bet : bets) {
            Player player = players.get(bet.getPlayerId());
            if (player.isLegitimate) {
                Match match = matches.get(bet.getMatchId());
                double rate;
                if (bet.getBetSide().equals("A")) {
                    rate = match.getRateA();
                } else {
                    rate = match.getRateB();
                }
                if (bet.getBetSide().equals(match.getResult())) {
                    int winnings = (int) (bet.getBetAmount() * rate);
                    player.winBet((int) (winnings + bet.getBetAmount()));
                    casinoBalance -= winnings;
                } else if (match.getResult().equals("DRAW")) {
                    player.deposit((int) (bet.getBetAmount()));
                } else {
                    casinoBalance += bet.getBetAmount();
                }
            }
        }
        System.out.println("Casino Balance: " + casinoBalance);
    }

    void writeToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, Player> entry : players.entrySet()) {
                Player player = entry.getValue();
                if (player.isLegitimate()) {
                    String winRateString = String.format("%.2f", player.getWinRate());
                    winRateString = winRateString.replace('.', ',');
                    writer.write(player.getId() + " " + player.getBalance() + " "
                            + winRateString + "\n");
                }
            }
            writer.newLine();

            for (Map.Entry<String, Player> entry : players.entrySet()) {
                Player player = entry.getValue();
                if (!player.isLegitimate()) {
                    writer.write(player.getId() + " " + player.getFirstIllegalAction() + "\n");
                }
            }
            writer.newLine();
            writer.write(String.valueOf(casinoBalance));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printPlayerData() {
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();

            System.out.println("Player ID: " + player.Id);
            System.out.println("Balance: " + player.coinsBalance);
            System.out.println("Total Bets: " + player.totalBets);
            System.out.println("Won Bets: " + player.wonBets);
            System.out.println("Is legit: " + player.isLegitimate);
            System.out.println("Winrate: " + player.getWinRate());
        }
    }

    void printMatchData() {
        for (Map.Entry<String, Match> entry : matches.entrySet()) {
            Match match = entry.getValue();

            System.out.println("Match ID: " + match.Id);
            System.out.println("Rate A: " + match.rateA);
            System.out.println("Rate B: " + match.rateB);
            System.out.println("Result: " + match.result);
        }
    }

    void printBetResults() {
        for (Bet bet : bets) {
            Player player = players.get(bet.getPlayerId());
            Match match = matches.get(bet.getMatchId());

            System.out.println("Player ID: " + player.getId());
            System.out.println("Match ID: " + match.getId());
            System.out.println("Bet Amount: " + bet.getBetAmount());
            System.out.println("Bet Side: " + bet.getBetSide());
            System.out.println("Match Result: " + match.getResult());
            System.out.println("Is Legitimate: " + player.isLegitimate());
            System.out.println();
        }
    }

}