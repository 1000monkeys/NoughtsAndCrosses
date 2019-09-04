package com.kjellvos.aletho.nac.canvas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.ScreenController;
import com.kjellvos.aletho.nac.gamemode.GameMode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class GameCanvasController implements ScreenController{
	private Main main;
	private boolean online = false;
	@FXML private Canvas gameCanvas;
	private Image board, nought, cross;
	private GameMode gameMode = null;
	private Timeline sixteenMillisecondsLoop;
	
	public GameCanvasController() {
		try {
			board = new Image(new FileInputStream("assets/board.png"));
			nought = new Image(new FileInputStream("assets/nought.png"));
			cross = new Image(new FileInputStream("assets/cross.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@FXML public void initialize() {
		gameCanvas.setOnMouseClicked(event -> canvasClicked(event));
		
		sixteenMillisecondsLoop = new Timeline(new KeyFrame(Duration.millis(16D), new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	if (gameMode != null) {	
			    	if (gameMode.getGameStatus()) {
			    		if (sixteenMillisecondsLoop != null) {
			    			sixteenMillisecondsLoop.stop();
				    		main.goBackScene();
			    		}
			    	}
		    		
		    		gameMode.tick();
			    	draw();
		    	}
		    }
		}));
		sixteenMillisecondsLoop.setCycleCount(Timeline.INDEFINITE);
		sixteenMillisecondsLoop.play();
	}
	
	@Override
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}
	
	public void canvasClicked(MouseEvent mouseEvent) {
		//System.out.println(mouseEvent.getX() + "X | Y " + mouseEvent.getY());
		
		int x = (int) mouseEvent.getX();
		int y = (int) mouseEvent.getY();
		
		int squareClicked = -1;
		if (0 < x && x < 300 && 0 < y && y < 300) { // regel 1 vak 0
			squareClicked = 0;
		}else if (300 < x && x < 600 && 0 < y && y < 300) { // regel 1 vak 1
			squareClicked = 1;
		}else if (600 < x && x < 900 && 0 < y && y < 300) { // regel 1 vak 2
			squareClicked = 2;
		}else if (0 < x && x < 300 && 300 < y && y < 600) { // regel 2 vak 3
			squareClicked = 3;
		}else if (300 < x && x < 600 && 300 < y && y < 600) { // regel 2 vak 4
			squareClicked = 4;
		}else if (600 < x && x < 900 && 300 < y && y < 600) { // regel 2 vak 5
			squareClicked = 5;
		}else if (0 < x && x < 300 && 600 < y && y < 900) { // regel 3 vak 6
			squareClicked = 6;
		}else if (300 < x && x < 600 && 600 < y && y < 900) { // regel 3 vak 7
			squareClicked = 7;
		}else if (600 < x && x < 900 && 600 < y && y < 900) { // regel 3 vak 8
			squareClicked = 8;
		}else {
			//wow howd u do that
		}
		
		gameMode.squareClicked(squareClicked);
	}
	
	public void draw() {
		GraphicsContext g = gameCanvas.getGraphicsContext2D();
		
		//draw grid lines
		g.drawImage(board, 0, 0, 900, 900);
			
		if (gameMode != null) {			
			//update gameBoard
			int[] gameBoard = gameMode.getGameBoard();
			
			if(gameBoard != null) {
				//1e regel
				if(gameBoard[0] == 1) {
					g.drawImage(nought, 10, 10, 280, 280);
				}else if (gameBoard[0] == 2) {
					g.drawImage(cross, 10, 10, 280, 280);
				}
				
				if(gameBoard[1] == 1) {
					g.drawImage(nought, 310, 10, 280, 280);
				}else if (gameBoard[1] == 2) {
					g.drawImage(cross, 310, 10, 280, 280);
				}
				
				if (gameBoard[2] == 1) {
					g.drawImage(nought, 610, 10, 280, 280);
				}else if (gameBoard[2] == 2) {
					g.drawImage(cross, 610, 10, 280, 280);
				}
				
				//2e regel
				if (gameBoard[3] == 1) {
					g.drawImage(nought, 10, 310, 280, 280);
				}else if (gameBoard[3] == 2) {
					g.drawImage(cross, 10, 310, 280, 280);
				}
				
				if (gameBoard[4] == 1) {
					g.drawImage(nought, 310, 310, 280, 280);
				}else if (gameBoard[4] == 2) {
					g.drawImage(cross, 310, 310, 280, 280);			
				}
				
				if (gameBoard[5] == 1) {
					g.drawImage(nought, 610, 310, 280, 280);
				}else if (gameBoard[5] == 2) {
					g.drawImage(cross, 610, 310, 280, 280);
				}
				
				//3e regel
				if (gameBoard[6] == 1) {
					g.drawImage(nought, 10, 610, 280, 280);			
				}else if (gameBoard[6] == 2) {
					g.drawImage(cross, 10, 610, 280, 280);			
				}
				
				if (gameBoard[7] == 1) {
					g.drawImage(nought, 310, 610, 280, 280);			
				}else if (gameBoard[7] == 2) {
					g.drawImage(cross, 310, 610, 280, 280);			
				}
				
				if (gameBoard[8] == 1) {
					g.drawImage(nought, 610, 610, 280, 280);
				}else if (gameBoard[8] == 2) {
					g.drawImage(cross, 610, 610, 280, 280);			
				}
			}
		}
	}
}
