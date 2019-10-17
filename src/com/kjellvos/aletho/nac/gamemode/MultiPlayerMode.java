package com.kjellvos.aletho.nac.gamemode;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.networking.Client;
import com.kjellvos.aletho.nac.networking.Server;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MultiPlayerMode implements GameMode{
	private Main main = null;
	int[] gameBoard = new int[9];
	private boolean playersTurn;
	private boolean gameEnded = false;
	private boolean isServer = false;
	
	public MultiPlayerMode(Main main, boolean playerOne) {
		this.main = main;
		playersTurn = playerOne;
	}
	
	public void setPlayersTurn(boolean playersTurn) {
		this.playersTurn = playersTurn;
	}
	
	public boolean getPlayersTurn() {
		return playersTurn;
	}
	
	public void setIsServer(boolean isServer) {
		this.isServer = isServer;
	}
	
	public boolean isServer() {
		return isServer;
	}
	
	@Override
	public void squareClicked(int squareId) {
		if (squareId > -1 && squareId < 9 && gameBoard[squareId] == 0 && playersTurn) {
			System.out.println("Clicked: " + squareId);
			
			if (isServer) {
				Server server = main.getServer();
				
				gameBoard[squareId] = 1;
				JSONObject gameState = new JSONObject();
				gameState.put("GameState", gameBoard);
				playersTurn = false;
				
				main.setMultiplayerGameStateJSON(gameState.toString());
				server.move(squareId);
				System.out.println("isserver send update");
			}else {
				Client client = main.getClient();
				
				gameBoard[squareId] = 2;
				JSONObject gameState = new JSONObject();
				gameState.put("GameState", gameBoard);
				playersTurn = false;

				main.setMultiplayerGameStateJSON(gameState.toString());
				client.move(squareId);
				System.out.println("isclient send update");
			}
		}
	}

	@Override
	public int[] getGameBoard() {
		String JSON = main.getMultiplayerGameStateJSON();
		
		gameBoard = new int[9];
		
		if (Main.isJSONValid(JSON)) {
			JSONObject gameStateJSON = new JSONObject(JSON);
			
			JSONArray gameStateJSONArray = gameStateJSON.getJSONArray("GameState");
			
			for (int i = 0; i < gameStateJSONArray.length(); i++) {
				gameBoard[i] = gameStateJSONArray.getInt(i);
			}
			
		}
		
		//System.out.println(main.getMultiplayerGameStateJSON());
		
		return gameBoard;
	}

	@Override
	public void tick() {
		Alert alert = new Alert(AlertType.INFORMATION);
		
		gameBoard = getGameBoard();
		
		if (!gameEnded) {
			int winCondition = winCondition();
			
			if (winCondition != 0) {
				alert.setTitle("Game finished.");
				alert.setHeaderText("And the winner is...");
				
				if (winCondition == 1) {
					alert.setContentText("You!");
				}else if (winCondition == 2){
					alert.setContentText("The other player!");
				}
				gameEnded = true;
				alert.show();
			}else if (gameBoardFull()) {
				alert.setTitle("Game finished.");
				alert.setHeaderText("And the winner is...");
				alert.setContentText("Nobody!");
				gameEnded = true;
				alert.show();		
			}
		}
	}

	public boolean gameBoardFull() {
		boolean gameBoardFull = true;
		for (int i = 0; i < gameBoard.length; i++) {
			if (gameBoard[i] == 0) {
				gameBoardFull = false;
			}
		}
		return gameBoardFull;
	}

	public int winCondition() {
		//regel 1
		if (gameBoard[0] == gameBoard[1] && gameBoard[1] == gameBoard[2] && (gameBoard[0] == 1 || gameBoard[0] == 2)) { 
			return 1;
		}
		
		//regel 2
		if (gameBoard[3] == gameBoard[4] && gameBoard[4] == gameBoard[5] && (gameBoard[3] == 1 || gameBoard[3] == 2)) {
			return 1;
		}
		
		//regel 3
		if (gameBoard[6] == gameBoard[7] && gameBoard[7] == gameBoard[8] && (gameBoard[6] == 1 || gameBoard[6] == 2)) {
			return 1;
		}

		
		//verticaal 1
		if (gameBoard[0] == gameBoard[3] && gameBoard[3] == gameBoard[6] && (gameBoard[0] == 1 || gameBoard[0] == 2)) {
			return 1;
		}
		
		//verticaal 2
		if (gameBoard[1] == gameBoard[4] && gameBoard[4] == gameBoard[7] && (gameBoard[1] == 1 || gameBoard[1] == 2)) {
			return 1;
		}
		
		//verticaal 3
		if (gameBoard[2] == gameBoard[5] && gameBoard[5] == gameBoard[8] && (gameBoard[2] == 1 || gameBoard[2] == 2)) {
			return 1;
		}
		
		//diagonaal 1
		if (gameBoard[0] == gameBoard[4] && gameBoard[4] == gameBoard[8] && (gameBoard[0] == 1 || gameBoard[0] == 2)) {
			return 1;
		}
		
		//diagonaal 2
		if (gameBoard[2] == gameBoard[4] && gameBoard[4] == gameBoard[6] && (gameBoard[2] == 1 || gameBoard[2] == 2)) {
			return 1;
		}
		
		return 0;
	}

	@Override
	public boolean getGameStatus() {
		return gameEnded;
	}
}
