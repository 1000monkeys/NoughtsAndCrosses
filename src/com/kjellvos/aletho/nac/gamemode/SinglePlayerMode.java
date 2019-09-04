package com.kjellvos.aletho.nac.gamemode;

import java.util.Random;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SinglePlayerMode implements GameMode {
	private int[] gameBoard = {0, 0, 0, 0, 0, 0, 0, 0, 0};
	private boolean playersTurn;
	private boolean gameEnded = false;
	
	public SinglePlayerMode() {
		int randInt = new Random().nextInt(2);
		if (randInt == 0) {
			playersTurn = false;
		}else {
			playersTurn = true;
		}
	}
	
	@Override
	public void squareClicked(int squareId) {
		if (squareId > -1 && squareId < 9 && gameBoard[squareId] == 0 && playersTurn) {
			gameBoard[squareId] = 1;
			playersTurn = false;
		}
	}

	@Override
	public int[] getGameBoard() {
		return gameBoard;
	}
	
	@Override
	public boolean getGameStatus() {
		return gameEnded;
	}

	@Override
	public void tick() {
		Alert alert = new Alert(AlertType.INFORMATION);
		
		if (!gameEnded) {
			if (!playersTurn) {
				computerPlay();
			}
			
			int winCondition = winCondition();
			
			if (winCondition != 0) {
				alert.setTitle("Game finished.");
				alert.setHeaderText("And the winner is...");
				
				if (winCondition == 1) {
					alert.setContentText("You!");
				}else if (winCondition == 2){
					alert.setContentText("The computer!");
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
	
	public void computerPlay() {
		Random random = new Random();
		
		int randInt = random.nextInt(9);
		
		if (gameBoard[randInt] == 0) {
			gameBoard[randInt] = 2;
			playersTurn = true;
		}
	}
	
	public int winCondition() {
		//regel 1
		if (gameBoard[0] == gameBoard[1] && gameBoard[1] == gameBoard[2]) { 
			return gameBoard[0];
		}
		
		//regel 2
		if (gameBoard[3] == gameBoard[4] && gameBoard[4] == gameBoard[5]) {
			return gameBoard[3];
		}
		
		//regel 3
		if (gameBoard[6] == gameBoard[7] && gameBoard[7] == gameBoard[8]) {
			return gameBoard[6];
		}

		
		//verticaal 1
		if (gameBoard[0] == gameBoard[3] && gameBoard[3] == gameBoard[6]) {
			return gameBoard[0];
		}
		
		//verticaal 2
		if (gameBoard[1] == gameBoard[4] && gameBoard[4] == gameBoard[7]) {
			return gameBoard[1];
		}
		
		//verticaal 3
		if (gameBoard[2] == gameBoard[5] && gameBoard[5] == gameBoard[8]) {
			return gameBoard[2];
		}
		
		//diagonaal 1
		if (gameBoard[0] == gameBoard[4] && gameBoard[4] == gameBoard[8]) {
			return gameBoard[0];
		}
		
		//diagonaal 2
		if (gameBoard[2] == gameBoard[4] && gameBoard[4] == gameBoard[6]) {
			return gameBoard[2];
		}
		
		return 0;
	}
}
