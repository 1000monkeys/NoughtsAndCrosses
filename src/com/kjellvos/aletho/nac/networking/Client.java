package com.kjellvos.aletho.nac.networking;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.gamemode.MultiPlayerMode;

import javafx.application.Platform;

public class Client extends Thread {
	private Main main = null;
	private MultiPlayerMode multiPlayerMode = null;
	public static int PORT = 4444;
	private static Socket socket;
	private static String serverIP = "127.0.0.1";

	private PrintWriter out;
	private BufferedReader in;
	
	private boolean start = true;
	private int runCount = 0;
	private int[] gameField = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
	private boolean madeMove = false;
	private int inputMove = -1;
	private boolean playerOne;
	
	public Client(Main main, String serverIP) {
		this.main = main;
		Client.serverIP = serverIP;
	}

	public void setupVariables() {
		try {
			socket = new Socket(serverIP, PORT);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
			System.out.println("Client run #" + runCount);

			try {
	    		String JSON = "";
				if (start) {
					setupVariables();
					
					out.println("setup");
					System.out.println("Client send: setup");
					
					start = false;
				}
				
				JSON = in.readLine();
				System.out.println("Client received: " + JSON);
				
				if (JSON.equals("startgame-0") || JSON.equals("startgame-1")) {
					if (JSON.equals("startgame-0")) {
						playerOne = true;
					}
					
					multiPlayerMode = new MultiPlayerMode(main, playerOne);
					multiPlayerMode.setIsServer(false);
					main.setMultiPlayerMode(multiPlayerMode);
					
					Runnable runnable = () -> {
						Platform.runLater(() -> {
							main.changeScene("/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml",
									multiPlayerMode);
						});
					};
					runnable.run();
					
					if (JSON.equals("startgame-0")) {					
						synchronized (this) {
							while (!madeMove) {
		            			wait(25);
		            		}
						}
	            		
	            		int move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
						
						gameField[move] = 2;
						
						System.out.println("Client send: move-" + move);
						out.println("move-" + move);
					}else if (JSON.equals("startgame-1")) {
						playerOne = false;
						JSON = in.readLine();
						System.out.println("Client received: " + JSON);
					}
				}
				
				if (JSON.contains("move")) {
					int move = Integer.parseInt(JSON.split("-")[1]);
	        		gameField[move] = 1;
	        		
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
	        		
		        	multiPlayerMode.setPlayersTurn(true);
		        	
	        		if (moveLeft()) {
	        			synchronized (this) {
		        			while (!madeMove) {
		            			wait(25);
		            		}
	        			}
	            		
	            		move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
	        			
	        			gameField[move] = 2;
	        			
	        			System.out.println("Client send: move-" + move);
		        		out.println("move-" + move);
	        		}else {
	        			System.out.println("Client send: gameover");
	        			out.println("gameover");
	        			
	        			System.exit(0);
	        		}
				}else if (JSON.equals("gameover")) {
					System.out.println("[" + gameField[0] + "][" + gameField[1] + "][" + gameField[2] + "]");
	        		System.out.println("[" + gameField[3] + "][" + gameField[4] + "][" + gameField[5] + "]");
	        		System.out.println("[" + gameField[6] + "][" + gameField[7] + "][" + gameField[8] + "]");
					
					System.exit(0);
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
}
