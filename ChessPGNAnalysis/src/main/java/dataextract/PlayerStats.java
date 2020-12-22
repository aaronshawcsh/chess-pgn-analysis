package dataextract;
/*
 *  This file is part of uci-analyser: a UCI-based Chess Game Analyser
 *  Copyright (C) 2013-2017 David J. Barnes
 *
 *  uci-analyser is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  uci-analyser is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with uci-analyser.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  David J. Barnes may be contacted as d.j.barnes@kent.ac.uk
 *  https://www.cs.kent.ac.uk/people/staff/djb/
 */

import analyzerTools.Analyzer;

/**
 * Provide details of a particular player in a particular game.
 * 
 * @author David J. Barnes (d.j.barnes@kent.ac.uk)
 */
public class PlayerStats {
    // Used for formatting player names.
    private static final int MAX_NAME_LENGTH = 30;
    
    // The game.
    private final Game game;
    // Whether the player was white or black.
    private final boolean isWhite;
    // The low threshold for returning scores.
    private final double lowThreshold;
    // Whether full scores should be shown.
    private final boolean showFullScores;
    // The name of the player.
    private final String playerName;
    // The scores for each move.
    private final int[] scores;
    // Textual version of the score differences for each move.
    private final String[] textDifferences;
    // Overall coincidence value of the moves.
    private final double CV;
    
    /**
     * Record stats for a player in a game.
     * @param game The game.
     * @param playerName The player.
     * @param lowThreshold The low threshold for scores.
     * @param showFullScores Whether to show full scores or not.
     */
    public PlayerStats(Game game, String playerName, double lowThreshold, boolean showFullScores) {
        this.game = game;
        this.playerName = playerName;
        this.isWhite = game.getPlayer() == Analyzer.Player.WHITE;
        this.lowThreshold = lowThreshold;
        this.showFullScores = showFullScores;

        scores = game.getNonMateScores(playerName);
        textDifferences = game.getScoresAsText(playerName);
        CV = getPercentageWithinThreshold(scores, lowThreshold);
    }

    /**
     * Return the scores.
     * @return The scores.
     */
    public int[] getScores() {
        return scores;
    }
    
    /**
     * Return the number of scores for this player.
     * @return The number of scores.
     */
    public int getNumScores()
    {
        return scores.length;
    }

    /**
     * Return a text version of the score differences.
     * @return The score differences.
     */
    public String[] getTextDifferences() {
        return textDifferences;
    }

    /**
     * Return the percentage of scores that are within
     * the threshold.
     * The Coincidence Value (CV).
     * @return The coincidence value.
     */
    public double getCV() {
        return CV;
    }
    
    /**
     * Return the percentage of scores that are within the threshold.
     * @param scores
     * @return 
     */
    private double getPercentageWithinThreshold(int[] scores, double lowThreshold) {
        int withinCount = 0;
        for(int score : scores) {
            if(score >= lowThreshold) {
                withinCount++;
            }
        }
        return ((double) withinCount) / scores.length;
    }
    
    /**
     * Return the mean score.
     * The Average Error (AE).
     *
     * @return
     */
    public double getAE() {
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return ((double) sum) / scores.length;
    }

    /**
     * Return the standard deviation of the scores.
     *
     * @param mean The mean of the scores.
     * @return The standard deviation.
     */
    private double sd(double mean) {
        double sum = 0;
        for (int score : scores) {
            double diff = mean - score;
            sum += diff * diff;
        }
        return Math.sqrt(sum / scores.length);
    }

}
