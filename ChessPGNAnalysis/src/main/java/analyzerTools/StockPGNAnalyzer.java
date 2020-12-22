package analyzerTools;

import dataextract.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StockPGNAnalyzer implements Analyzer {
    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from a single String containing a PGN chess game
     *
     * @param pgn    a String containing the moves made in a single game in PGN format
     * @param player a Player enum reference {BLACK, WHITE} to specify which CV to show
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    @Override
    public double analyzeGame(String pgn, Player player) {
        return 0;
    }

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from a single pgn file containing a chess game
     *
     * @param filePath a file containing the moves made in a single game in PGN format
     * @param player   a Player enum reference {BLACK, WHITE} to specify which CV to show
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    @Override
    public double analyzeGame(Path filePath, Player player) {
        try {
            ArrayList<String> lines = Files.lines(filePath)
                    .filter(w -> !w.startsWith("["))
                    .filter(w -> !w.equals(""))
                    .collect(Collectors.toCollection(ArrayList::new));
            String pgn = "";
            for(String line : lines) {
                pgn += line;
                if(line.contains("1-0") || line.contains("0-0") || line.contains("0-1")) break;
            }
            Game game = new Game(Player.BLACK, pgn, new dataextract.Analysis());
            PlayerStats playerStats = new PlayerStats(game, "aaronshawcsh", 0.0, false);
            return playerStats.getCV();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return 0;
    }

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from a single String containing a PGN chess game
     *
     * @param pgn a String containing the moves made in a single game in PGN format
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    @Override
    public Analysis analyzeGame(String pgn) {
        return null;
    }

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from a single pgn file containing a chess game
     *
     * @param filePath a file containing the moves made in a single game in PGN format
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    @Override
    public Analysis analyzeGame(Path filePath) {
        return null;
    }

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from multiple Strings containing PGN chess games
     *
     * @param pgns       String[] containing the moves made in games in PGN format
     * @param playerName a String to specify which player's moves to analyze in each game
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    @Override
    public ArrayList<Double> analyzeGames(String[] pgns, String playerName) {
        return null;
    }

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from multiple pgn files containing chess games
     *
     * @param pgns       Paths to files containing the moves made in games in PGN format
     * @param playerName a String to specify which player's moves to analyze in each game
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    @Override
    public ArrayList<Double> analyzeGames(ArrayList<Path> pgns, String playerName) {
        return null;
    }

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from multiple Strings containing PGN chess games
     *
     * @param pgns String[] containing the moves made in games in PGN format
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    @Override
    public ArrayList<Analysis> analyzeGames(String[] pgns) {
        return null;
    }

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from multiple pgn files containing chess games
     *
     * @param pgns Paths to files containing the moves made in games in PGN format
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    @Override
    public ArrayList<Analysis> analyzeGames(ArrayList<Path> pgns) {
        return null;
    }

    /**
     * OPTIONAL. splits a file containing multiple PGN games into multiple files containing single games
     *
     * @param filePath a Path referencing the file containing the games
     * @return an ArrayList of Paths containing references to created PGN game files
     */
    @Override
    public ArrayList<Path> splitFile(Path filePath) {
        return null;
    }

    /**
     * OPTIONAL. splits files containing multiple PGN games into multiple one-to-one subdirectories and files containing single games
     *
     * @param filePath an ArrayList of Paths referencing the files containing the games
     * @return an ArrayList of Paths containing references to created subdirectories and files
     */
    @Override
    public ArrayList<ArrayList<Path>> splitFiles(ArrayList<Path> filePath) {
        return null;
    }
}
