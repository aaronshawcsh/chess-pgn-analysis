import java.nio.file.Path;
import java.util.ArrayList;

public interface Analyzer {
    enum Player {BLACK, WHITE}

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from a single String containing a PGN chess game
     *
     * @param pgn a String containing the moves made in a single game in PGN format
     * @param player a Player enum reference {BLACK, WHITE} to specify which CV to show
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    double analyzeGame(String pgn, Player player);

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from a single pgn file containing a chess game
     *
     * @param filePath a file containing the moves made in a single game in PGN format
     * @param player a Player enum reference {BLACK, WHITE} to specify which CV to show
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    double analyzeGame(Path filePath, Player player);

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from a single String containing a PGN chess game
     *
     * @param pgn a String containing the moves made in a single game in PGN format
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    Analysis analyzeGame(String pgn);

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from a single pgn file containing a chess game
     *
     * @param filePath a file containing the moves made in a single game in PGN format
     * @return a double corresponding to the CV of the game as given by Stockfish
     */
    Analysis analyzeGame(Path filePath);

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from multiple Strings containing PGN chess games
     *
     * @param pgns String[] containing the moves made in games in PGN format
     * @param playerName a String to specify which player's moves to analyze in each game
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    ArrayList<Double> analyzeGames(String[] pgns, String playerName);

    /**
     * analyze and return the Coincidence Value (CV) of one player's moves from multiple pgn files containing chess games
     *
     * @param pgns Paths to files containing the moves made in games in PGN format
     * @param playerName a String to specify which player's moves to analyze in each game
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    ArrayList<Double> analyzeGames(ArrayList<Path> pgns, String playerName);

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from multiple Strings containing PGN chess games
     *
     * @param pgns String[] containing the moves made in games in PGN format
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    ArrayList<Analysis> analyzeGames(String[] pgns);

    /**
     * analyze and return the Coincidence Value (CV) of both players' moves from multiple pgn files containing chess games
     *
     * @param pgns Paths to files containing the moves made in games in PGN format
     * @return an ArrayList of type Double containing the CV of the games as given by Stockfish
     */
    ArrayList<Analysis> analyzeGames(ArrayList<Path> pgns);

    /**
     * OPTIONAL. splits a file containing multiple PGN games into multiple files containing single games
     * @param filePath a Path referencing the file containing the games
     * @return an ArrayList of Paths containing references to created PGN game files
     */
    ArrayList<Path> splitFile(Path filePath);

    /**
     * OPTIONAL. splits files containing multiple PGN games into multiple one-to-one subdirectories and files containing single games
     * @param filePath an ArrayList of Paths referencing the files containing the games
     * @return an ArrayList of Paths containing references to created subdirectories and files
     */
    ArrayList<ArrayList<Path>> splitFiles(ArrayList<Path> filePath);
}
