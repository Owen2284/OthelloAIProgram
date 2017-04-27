package main;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.HashMap;

import games.GameState;
import players.HumanPlayer;
import players.Player;
import players.PlayerFactory;
import ui.OthelloFrame;

/**
 * The main class that runs the Othello games. Can be provided numerous command line arguments to
 * change the operation of the games. (See help.txt for usage of arguments.)
 */
public class Othello {
	
	private final static boolean DEBUG = false;
	
	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		
		// Parses the command line variables.
		HashMap<String,String> argMap = new HashMap<String,String>();
		for (int argNum = 0; argNum < args.length - 1; ++argNum) {
			String thisArg = args[argNum];
			String nextArg = args[argNum+1];
			if (thisArg.substring(0,1).equals("-")) {
				argMap.put(thisArg.toUpperCase(), nextArg);
				if (DEBUG) {System.out.println(thisArg.toUpperCase() + " = " + nextArg);}
			}
		}
		
		// Setting default values for games variables and applies command line arguments if they are found.
		boolean useGUI = true;
		boolean showOutput = false;
		boolean archiveGames = true;
		boolean alternate = true;
		int delayBetweenMoves = 100;
		int maxSearchTime = 5000;
		int timesToRun = 1;
		int boardSize = 8;
		try {
			if (argMap.keySet().contains("-useGUI".toUpperCase())) {useGUI = Boolean.parseBoolean(argMap.get("-useGUI".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -useGUI argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-showOutput".toUpperCase())) {showOutput = Boolean.parseBoolean(argMap.get("-showOutput".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -showOutput argument. (" + e.getMessage() + ")");}	
		try {
			if (argMap.keySet().contains("-archiveGame".toUpperCase())) {archiveGames = Boolean.parseBoolean(argMap.get("-archiveGame".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -archiveGame argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-alternate".toUpperCase())) {alternate = Boolean.parseBoolean(argMap.get("-alternate".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -alternate argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-moveDelay".toUpperCase())) {delayBetweenMoves = Integer.parseInt(argMap.get("-moveDelay".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -moveDelay argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-AIRunTime".toUpperCase())) {maxSearchTime = Integer.parseInt(argMap.get("-AIRunTime".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -AIRunTime argument. (" + e.getMessage() + ")");}
		try {	
			if (argMap.keySet().contains("-runCount".toUpperCase())) {timesToRun = Integer.parseInt(argMap.get("-runCount".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -runCount argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-boardSize".toUpperCase())) {boardSize = Integer.parseInt(argMap.get("-boardSize".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -boardSize argument. (" + e.getMessage() + ")");}
			
		// Defines main games variables.
		GameState game = new GameState(new HumanPlayer(GameState.COUNTER_DARK, useGUI), new HumanPlayer(GameState.COUNTER_LIGHT, useGUI), 8);
		Player p1 = null;
		Player p2 = null;
		int[] playerWins = {0,0};
		int[] slotWins = {0,0};
		OthelloFrame ui = null;
		if (useGUI) {
			ui = new OthelloFrame(game);
		}
		
		// Runs the games multiple times if required.
		for (int runNumber = 0; runNumber < timesToRun; ++runNumber) {
		
			// Creates the player objects.
			int playerID = GameState.COUNTER_DARK;
			if (alternate && (runNumber % 2 == 1)) {playerID = GameState.COUNTER_LIGHT;}
			if (argMap.keySet().contains("-player1".toUpperCase())) {
				try {
					String argString = argMap.get("-player1".toUpperCase());
					if (argString.startsWith("AI")) {
						String[] aiArgs = argString.substring(3, argString.length() - 1).split(",");
						p1 = PlayerFactory.createPlayer(playerID, "AI", aiArgs[0], aiArgs[1], useGUI, maxSearchTime, false);
					} else if (argString.startsWith("Human")) {
						p1 = PlayerFactory.createPlayer(playerID, "Human", "", "", useGUI, maxSearchTime, false);
					}
				} catch (Exception e) {
					System.out.println("Error parsing -player1 argument. (" + e.getMessage() + ")");
					p1 = PlayerFactory.createPlayer(playerID, "Human", "", "", useGUI, maxSearchTime, false);
				}
			} else {
				p1 = PlayerFactory.createPlayer(playerID, "Human", "", "", useGUI, maxSearchTime, false);
			}
			playerID = GameState.COUNTER_LIGHT;
			if (alternate && (runNumber % 2 == 1)) {playerID = GameState.COUNTER_DARK;}
			if (argMap.keySet().contains("-player2".toUpperCase())) {
				try {
					String argString = argMap.get("-player2".toUpperCase());
					if (argString.startsWith("AI")) {
						String[] aiArgs = argString.substring(3, argString.length() - 1).split(",");
						p2 = PlayerFactory.createPlayer(playerID, "AI", aiArgs[0], aiArgs[1], useGUI, maxSearchTime, false);
					} else if (argString.startsWith("Human")) {
						p2 = PlayerFactory.createPlayer(playerID, "Human", "", "", useGUI, maxSearchTime, false);
					}
				} catch (Exception e) {
					System.out.println("Error parsing -player2 argument. (" + e.getMessage() + ")");
					p2 = PlayerFactory.createPlayer(playerID, "AI", "Random", "Score", useGUI, maxSearchTime, false);
				}
			} else {
				p2 = PlayerFactory.createPlayer(playerID, "AI", "Random", "Score", useGUI, maxSearchTime, false);
			}

			// Swaps the players on odd run numbers, if the alternate argument is true.
			if (alternate && (runNumber % 2 == 1)) {
				Player temp = p1;
				p1 = p2;
				p2 = temp;
			}
			
			// Creates initial games state.
			game = new GameState(p1, p2, boardSize);
			
			// Storing initial games state in file string storage.
			String fileString = game.toFileString();
					
			// Initialises other variables.
			int nextPlayerNumber = 0;
			Player playerToPlay = null;
			
			if (timesToRun > 1) {
				System.out.println("Running game " + (runNumber+1) + "...");
			}
				
			// Creates games UI.
			while (!game.isOver()) {
				
				// Retrieve the player that is next to play.
				playerToPlay = game.getPlayerByIndex(nextPlayerNumber);
				
				// Display the current games state.
				if (useGUI) {
					ui.updateUI(game, playerToPlay);
				} else {
					System.out.println(game);
				}
				
				// Temporary pause to show games state.
				try {Thread.sleep(delayBetweenMoves);} catch (Exception e) {}
				
				// Determine if the player can make a move or not.
				if (game.hasLegalMoves(playerToPlay)) {
				
					// Ask the player to determine their move.
					Point moveToPlay;
					if (useGUI) {
						moveToPlay = playerToPlay.getMove(game, ui.middle);
					} else {
						moveToPlay = playerToPlay.getMove(game, null);
					}
					
					// Plays the move onto the games board, and stores the new GameState.
					game = game.playMove(playerToPlay, moveToPlay);
					
					// Display any info about the player's move if necessary.
					if (showOutput) {
						System.out.println(playerToPlay.getOutput());
					}
					
					// Add move to file.
					fileString += "P[" + nextPlayerNumber + "]:(" + moveToPlay.x + "," + moveToPlay.y + ")\n";
				
				} else {
					
					// Add non-move to file.
					fileString += "P[" + nextPlayerNumber + "]:NOMOVE\n";
					
				}
				
				// Changes the player to play next and loops back to the start of the games loop.
				nextPlayerNumber = (nextPlayerNumber + 1) % 2;
				
			}
			
			// Display the final games state.
			if (useGUI) {
				ui.updateUI(game, playerToPlay);
			} else {
				System.out.println(game);
			}

			// Print the game's results.
			if (timesToRun > 1) {
				System.out.print(" ");
			}
			if (game.isWinning(p1)) {
				System.out.print("The dark player wins. ");
			} else {
				System.out.print("The light player wins. ");
			}
			System.out.println("Final score: " + game.getScoreOfPlayer(p1) + "-" + game.getScoreOfPlayer(p2));

			// Temporary pause to show the final game state.
			try {Thread.sleep(delayBetweenMoves*4);} catch (Exception e) {}
			
			// Update wins arrays based on outcome.
			if (alternate && (runNumber % 2 == 1)) {
				if (game.isWinning(p1)) {
					playerWins[1] += 1;
					slotWins[0] += 1;
				} else if (game.isWinning(p2)) {
					playerWins[0] += 1;
					slotWins[1] += 1;
				}
			} else {
				if (game.isWinning(p1)) {
					playerWins[0] += 1;
					slotWins[0] += 1;
				} else if (game.isWinning(p2)) {
					playerWins[1] += 1;
					slotWins[1] += 1;
				}
			}
			
			// Writes games archive to a file.
			if (archiveGames) {
				fileString += "END";
				try {
					String fileName = "dat/archive/Game" + System.currentTimeMillis() + ".txt";
					PrintWriter writer = new PrintWriter(fileName, "UTF-8");
					for (String s : fileString.split("\n")) {
						writer.println(s);
					}
					writer.close();
					if (showOutput) {
						System.out.println("Game file written to \"" + fileName + "\"");
					}
				} catch(Exception e) {
					System.out.println("Error while writing games file: " + e.getMessage());
				}
			}
			
		}
		
		if (timesToRun > 1) {
			System.out.println("FINAL RESULTS");
			System.out.println("-------------");
			System.out.println("Player 1 won " + playerWins[0] + " games(s).");
			System.out.println("Player 2 won " + playerWins[1] + " games(s).");
			System.out.println("-------------");
			System.out.println("The Dark player won " + slotWins[0] + " game(s).");
			System.out.println("The Light player won " + slotWins[1] + " game(s).");
			System.out.println("-------------");
			System.out.println("The players drew " + (timesToRun - playerWins[0] - playerWins[1]) + " time(s).");
		}
		
	}

}
