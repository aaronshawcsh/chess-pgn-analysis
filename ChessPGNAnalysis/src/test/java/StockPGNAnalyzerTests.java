import analyzerTools.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StockPGNAnalyzerTests {
    final Path TEST_FILE = Paths.get("src/main/resources/chess_com_games_2020-12-20.pgn");
    StockPGNAnalyzer stockPGNAnalyzer;

    @BeforeEach
    public void init() {
        stockPGNAnalyzer = new StockPGNAnalyzer();
    }

    @Test
    public void analyzeGameTest() {
        double eval = stockPGNAnalyzer.analyzeGame(TEST_FILE, Analyzer.Player.BLACK);
        boolean condition = eval > 25 && eval < 50;
        Assertions.assertTrue(condition);
    }
}
