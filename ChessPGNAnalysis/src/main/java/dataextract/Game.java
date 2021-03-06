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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import analyzerTools.*;

/**
 * Store details of a game, including its PGN tags and the analysis.
 * Provide access to those details.
 * 
 * @author David J. Barnes (d.j.barnes@kent.ac.uk)
 */
public class Game {
    // Pseudo player names to match any player of a given colour.

    public static final String MATCH_ANY_WHITE_PLAYER = "<White>";
    public static final String MATCH_ANY_BLACK_PLAYER = "<Black>";
    private final Analyzer.Player player;
    private final String[] moves;
    private final Analysis analysis;
    private final int bookDepth;

    /**
     * Create a game given its tags, moves and analysis.
     * @param moveList The game's moves.
     * @param analysis The analysis of the game.
     */
    public Game(Analyzer.Player player, String moveList, Analysis analysis) {
        this.player = player;
        String[] movesAndResult = moveList.split("\\s+");
        this.moves = new String[movesAndResult.length - 2];
        System.arraycopy(movesAndResult, 1, this.moves, 0, this.moves.length);
        this.analysis = analysis;
        this.bookDepth = analysis.getBookDepth();
    }

    /**
     * Return the score differences for the given player as text. Scores
     * involving mate are return as "?".
     *
     * @param player The player of interest.
     * @return An array of score differences.
     */
    public String[] getScoresAsText(String player) {
        String[] result;
        List<String> scores = new ArrayList<>();
        boolean playerIsWhite = player.equals(Analyzer.Player.WHITE + "");
        if (bookDepth >= 0) {
            List<PlayedMove> played = analysis.getAnalysedMoves();
            int ply = bookDepth + 1;
            boolean errorInGame = false;
            try {
                for (PlayedMove move : played) {
                    if (move.isWhiteMove() == playerIsWhite) {
                        PlayedMoveScore score = getEvaluation(move);
                        if (!score.bestIsMate() && !score.playedIsMate()) {
                            scores.add("" + score.getValue());
                        } else if (score.bestIsMate() && score.playedIsMate() && score.getValue() == 0) {
                            // The same mate.
                            scores.add("0");
                        } else {
                            // One or other was a mate that differs from the other.
                            scores.add("?");
                        }
                    }
                    ply++;
                }
            } catch (IllegalStateException e) {
                if (!errorInGame) {
                    System.err.println("Warning: " + e.getMessage() + " in\n" + this);
                    errorInGame = true;
                }
            }
            if (errorInGame) {
                scores.clear();
            }
        }
        result = new String[scores.size()];
        scores.toArray(result);
        return result;
    }

    /**
     * Return the score differences for the given player as text.
     * Scores involving mate are return as "?".
     *
     * @param player The player of interest.
     * @return An array of non-mating scores.
     */
    public int[] getNonMateScores(String player) {
        int[] result;
        List<Integer> scores = new ArrayList<>();
        // Find out whether the player is black or white.
        boolean playerIsWhite = player.equals(Analyzer.Player.WHITE + "");
        if (bookDepth >= 0) {
            List<PlayedMove> played = analysis.getAnalysedMoves();
            boolean errorInGame = false;
            try {
                for (PlayedMove move : played) {
                    if (move.isWhiteMove() == playerIsWhite) {
                        PlayedMoveScore score = getEvaluation(move);
                        if (!score.bestIsMate() && !score.playedIsMate()) {
                            scores.add(score.getValue());
                        } else if (score.bestIsMate() && score.playedIsMate() && score.getValue() == 0) {
                            // The same mate.
                            scores.add(0);
                        } else {
                            // Can't add anything.
                        }
                    }
                }
            } catch (IllegalStateException e) {
                if (!errorInGame) {
                    System.err.println("Warning: " + e.getMessage() + " in\n" + this);
                    errorInGame = true;
                }
            }
            if (errorInGame) {
                scores.clear();
            }
        }
        result = new int[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            result[i] = scores.get(i);
        }
        return result;
    }
    
    /**
     * Return the book depth of this game
     * @return The games book depth.
     */
    public int getBookDepth()
    {
        return bookDepth;
    }

    /**
     * Return the game's moves.
     * @return The moves as a string.
     */
    public String[] getMoves() {
        return moves;
    }

    /**
     * Return the analysis of the game.
     * @return The game's analysis.
     */
    public Analysis getAnalysis() {
        return analysis;
    }

    /**
     * Assess the played move against the best move and return an encoding of
     * it.
     *
     * @param played The move played.
     * @return The score for the given move as a difference from the best move.
     * @throws IllegalStateException if a score cannot be given.
     */
    public PlayedMoveScore getEvaluation(PlayedMove played)
            throws IllegalStateException {
        Iterator<Evaluation> it = played.getEvaluations().iterator();
        // The first move listed (if any) is considered the best.
        if (it.hasNext()) {
            PlayedMoveScore result = null;
            String move = played.getMove().substring(0, 4);
            Evaluation ev = it.next();
            if (ev.getMove().substring(0, 4).equals(move)) {
                // The best move played.
                result = new PlayedMoveScore(0);
            } else {
                // A different move was played in the game.
                try {
                    // Determine the value of the best move.
                    String evaluation = ev.getEvaluation();
                    String value;
                    boolean bestIsMate = false;
                    if (evaluation.contains(" ")) {
                        String[] parts = evaluation.split(" ");
                        if (parts[0].equals("mate")) {
                            bestIsMate = true;
                            value = parts[1];
                        } else {
                            value = parts[0];
                        }
                    } else {
                        value = evaluation;
                    }
                    
                    // Find the evaluation of the move that was played.
                    int bestScore = Integer.parseInt(value);
                    while (result == null && it.hasNext()) {
                        ev = it.next();
                        if (move.equals(ev.getMove().substring(0, 4))) {
                            // This is the evaluation of the played move.
                            boolean playedIsMate = false;
                            evaluation = ev.getEvaluation().trim();
                            if (evaluation.contains(" ")) {
                                String[] parts = evaluation.split(" ");
                                if (parts[0].equals("mate")) {
                                    playedIsMate = true;
                                    value = parts[1];
                                } else {
                                    value = parts[0];
                                }
                            } else {
                                value = evaluation;
                            }
                            int score = Integer.parseInt(value);
                            // See if a numeric score can be returned.
                            if (bestIsMate) {
                                if (playedIsMate) {
                                    if (score == bestScore) {
                                        result = new PlayedMoveScore(0, true, true);
                                    } else {
                                        // Number of extra moves in the mate.
                                        result = new PlayedMoveScore(score - bestScore, true, true);
                                    }
                                } else {
                                    // The raw score if the mate was missed.
                                    result = new PlayedMoveScore(score, true, false);
                                }
                            } else if (playedIsMate) {
                                // Record the score it could have been.
                                result = new PlayedMoveScore(bestScore, false, true);
                            } else {
                                // Number of centipawns worse.
                                int diff = score - bestScore;
                                if (diff > 1000) {
                                    System.err.println(ev);
                                }
                                result = new PlayedMoveScore(diff);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Non-integer evaluation.
                    throw new IllegalStateException("Format error in " + played);
                }
            }
            if (result != null) {
                return result;
            } else {
                // The move played was not found.
                // This is an error.
                StringBuilder buffer = new StringBuilder();
                Iterator<Evaluation> itp = played.getEvaluations().iterator();
                while(itp.hasNext()) {
                    Evaluation evp = itp.next();
                    buffer.append(evp.getMove().substring(0, 4));
                    buffer.append(' ');
                }
                buffer.setLength(buffer.length() - 1);
                throw new IllegalStateException("Played move " + move + " not found in evaluations " + buffer.toString());
            }
        } else {
            throw new IllegalStateException("No events in " + played);
        }
    }
    
    /**
     * Output the game with evaluation annotations.
     * @param annotatedFile Where to write the game.
     * @throws IOException on I/O error.
     */
    public void annotate(Writer annotatedFile)
        throws IOException
    {
        StringBuilder builder = new StringBuilder();
        
        // Output those moves without an evaluation.
        int ply = 1;
        
        while(ply <= bookDepth) {
            builder.append(moves[ply - 1]).append(' ');
            ply++;
        }
        List<PlayedMove> annotated = analysis.getAnalysedMoves();
        // There should be exactly the same number of annotated moves as moves remaining.
        //assert moves.length - ply == annotated.size();
        builder.append('\n');
        
        Iterator<PlayedMove> it = annotated.iterator();
        while(it.hasNext()) {
            PlayedMove played = it.next();
            builder.append(played.getMove()).append(' ');
            Evaluation ev = played.getEvaluationForMove();
            if(ev != null) {
                builder.append("{ ").append(ev.getEvaluation()).append(" } ");
            }
            Evaluation first = played.getFirstEvaluation();
            if(first != ev && first != null) {
                builder.append("( ").append(first.getMove()).append(' ');
                builder.append("{ ").append(first.getEvaluation()).append(" }");
                builder.append(") ");
            }
        }
        builder.append('\n');
        annotatedFile.write(builder.toString());
    }

    public Analyzer.Player getPlayer() {
        return player;
    }
    
    /**
     * Return the AE, CV and MM values for the given player.
     * @param player Player whose stats are required.
     * @return The AE, CV and MM values.
     */
    private String getAnnotationStats(String player) {
        PlayerStats stats = new PlayerStats(this, player, 0.0, false);
        StringBuilder builder = new StringBuilder();
        builder.append(player).append(": ");
        builder.append("AE = ").append(stats.getAE()).append(", ");
        builder.append("CV = ").append(stats.getCV()).append(", ");
        builder.append("NM = ").append(stats.getNumScores());
        return builder.toString();
    }
}
