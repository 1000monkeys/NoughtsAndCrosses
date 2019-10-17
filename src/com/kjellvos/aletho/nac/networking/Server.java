package com.kjellvos.aletho.nac.networking;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.gamemode.MultiPlayerMode;

import javafx.application.Platform;

public class Server extends Thread{
	private Main main = null;
	private MultiPlayerMode multiPlayerMode = null;
	public static int PORT = 4444;
	public ServerSocket serverSocket = null;
	private static Socket socket;
	private PrintWriter out;
	private BufferedReader br;
	
	private boolean start = true;
	private int runCount = 0;
	private boolean searching = true;
	
	private int[] gameField = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
	private boolean playerOne = false;
	private boolean madeMove = false;
	private int inputMove = -1;
	
	public Server(Main main) {
		this.main = main;
	}
	
	public void setupVariables() {
		try {
			serverSocket = new ServerSocket(PORT);
	
			socket = serverSocket.accept();
			
			InputStream is = socket.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);
	        
	    	out = new PrintWriter(socket.getOutputStream(), true);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void move(int move) {
		inputMove = move;
		madeMove = true;
	}

	public boolean moveLeft() {
		for (int i = 0; i < gameField.length; i++) {
			if (gameField[i] == 0) {
				return true;
			}
		}
		return false;
	}
	
	public void run() {
		while (true) {
			System.out.println("Server run #" + runCount);
			
			if(start) {
				setupVariables();
				start = false;
			}
			
			try {
	        	String JSON = br.readLine(); //receiving message
	        	System.out.println("Server received: " + JSON);
	        	
	        	if (JSON.equals("setup")) { 
	        		multiPlayerMode = new MultiPlayerMode(main, playerOne);
	        		multiPlayerMode.setIsServer(true);
					main.setMultiPlayerMode(multiPlayerMode);
	        		
	            	Runnable runnable = () -> {
						Platform.runLater(() -> {
							main.changeScene("/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml",
									multiPlayerMode);
						});
					};
					runnable.run();
	        		
	            	if (playerOne) {
	        			System.out.println("Server send: startgame-1");
	            		out.println("startgame-1");
	            		
	            		synchronized (this) {
		            		while (!madeMove) {
		            			wait(25);
		            		}
	            		}
	            		
	            		int move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
	            		
	        			gameField[move] = 1;
	            		
	            		System.out.println("Server send: move-" + move);
		        		out.println("move-" + move);
	            	}else {
	        			System.out.println("Server send: startgame-0");
	            		out.println("startgame-0");
	            	}
	        	}else if (JSON.equals("gameover")) {        		
	        		System.out.println("[" + gameField[0] + "][" + gameField[1] + "][" + gameField[2] + "]");
	        		System.out.println("[" + gameField[3] + "][" + gameField[4] + "][" + gameField[5] + "]");
	        		System.out.println("[" + gameField[6] + "][" + gameField[7] + "][" + gameField[8] + "]");
	        		
	        		System.exit(0);
	        	}else if (JSON.contains("move")) {
					int move = Integer.parseInt(JSON.split("-")[1]);
	        		gameField[move] = 2;
	        		
	        		multiPlayerMode.setPlayersTurn(true);
	        		
	        		JSON = new JSONObject(
	        				"{" + 
	        				"  \"GameState\": [" + gameField[0] + ","
	        						+ gameField[1] + ","
	        						+ gameField[2] + ","
	        						+ gameField[3] + ","
	        						+ gameField[4] + ","
	        						+ gameField[5] + ","
	        						+ gameField[6] + ","
	        						+ gameField[7] + ","
	        						+ gameField[8] + "]," + 
	        				"}"
	    				).toString();
		        	
		        	main.setMultiplayerGameStateJSON(JSON);
	        		
	        		if (moveLeft()) {
	        			synchronized (this) {
		        			while (!madeMove) {
		            			wait(25);
		            		}
	        			}
	            		
	            		move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
	        			
	        			gameField[move] = 1;
	        			
	        			System.out.println("Server send: move-" + move);
		        		out.println("move-" + move);
	        		}else {
	        			System.out.println("Server send: gameover");
	        			out.println("gameover");
	        		}
	        	}
	        	
	        	JSON = new JSONObject(
        				"{" + 
        				"  \"GameState\": [" + gameField[0] + ","
        						+ gameField[1] + ","
        						+ gameField[2] + ","
        						+ gameField[3] + ","
        						+ gameField[4] + ","
        						+ gameField[5] + ","
        						+ gameField[6] + ","
        						+ gameField[7] + ","
        						+ gameField[8] + "]," + 
        				"}"
    				).toString();
	        	
	        	main.setMultiplayerGameStateJSON(JSON);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runCount++;
		}
	}

	public boolean getIsPlayerOne() {
		return playerOne;
	}
	
	public void setIsPlayerOne(boolean playerOne) {
		this.playerOne = playerOne;
	}
	
	public boolean getIsSearching() {
		return searching;
	}
	
	public void setIsSearching(boolean searching) {
		this.searching = searching;
	}
}