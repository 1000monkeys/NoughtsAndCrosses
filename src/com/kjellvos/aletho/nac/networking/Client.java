package com.kjellvos.aletho.nac.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.gamemode.MultiPlayerMode;

import javafx.application.Platform;

public class Client extends Thread {
	private Main main = null;
	public static int PORT = 4444;
	private static Socket socket;
	private static String serverIP;

	private PrintWriter out;
	private BufferedReader in;
	
	private static final int MAXQUEUE = 3;
	private Vector<String> messages = new Vector<String>();

	public Client(String serverIPTwo, Main main) {
		this.main = main;
		serverIP = serverIPTwo;
	}

	public void run() {
		try {
			socket = new Socket(serverIP, PORT);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			InetAddress ip = null;
    		String hostname = null;
    		try {
    			ip = InetAddress.getLocalHost();
    			hostname = ip.getHostAddress();
    		} catch (UnknownHostException e) {
    			e.printStackTrace();
    		}
			
    		if (main.getMultiplayerGameStateJSON() == null) {
				out.println(new JSONObject("{\"ip\": \"" + ip.getHostAddress() + "\"}").toString());
			}
    		
			String JSON = in.readLine();
			if (Main.isJSONValid(JSON) && new JSONObject(JSON).getInt("Turn") == 0) {
				main.setMultiplayerGameStateJSON(JSON);
				
				
				String message = new JSONObject("{\"action\": \"changeScene\", \"scene\": \"/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml\"}").toString();
				//putMessage(message);
				
				Runnable runnable = () -> {
					Platform.runLater(() -> {
						main.changeScene("/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml",
								new MultiPlayerMode(main));
					});
				};
				runnable.run();
				
				//main.changeScene("/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml", new MultiPlayerMode(main));
			}else if(Main.isJSONValid(JSON)){
				main.setMultiplayerGameStateJSON(JSON);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void putMessage(String message) throws InterruptedException {
		 
        while (messages.size() == MAXQUEUE)
            wait();
        messages.addElement(message);
        notify();
    }
	
	public synchronized String getMessage() throws InterruptedException {
        notify();
        if (messages.size() == 0)
            return System.lineSeparator();
        String message = (String) messages.firstElement();
        messages.removeElement(message);
        return message;
    }
}
