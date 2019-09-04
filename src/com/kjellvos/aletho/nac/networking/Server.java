package com.kjellvos.aletho.nac.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.gamemode.MultiPlayerMode;

import javafx.application.Platform;

public class Server extends Thread{
	private Main main = null;
	public static boolean searching = true;
	public static int PORT = 4444;
	public ServerSocket serverSocket = null;
	private static Socket socket;
	
	public Server(Main main) {
		this.main = main;
		
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			socket = serverSocket.accept();
			
			InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            String JSON = br.readLine(); //receiving message
            
            if (Main.isJSONValid(JSON)) {
            	searching = false;
            	
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                		
        		InetAddress ip = null;
        		String hostname = null;
        		try {
        			ip = InetAddress.getLocalHost();
        			hostname = ip.getHostAddress();
        		} catch (UnknownHostException e) {
        			e.printStackTrace();
        		}
        		
        		JSON = new JSONObject(
        				"{" + 
        				"  \"GameState\": [0, 0, 0, 0, 0, 0, 0, 0, 0]," + 
        				"  \"Turn\": \"0\"" + 
        				"}"
    				).toString();
                
        		Runnable runnable = () -> {
					Platform.runLater(() -> {
						main.changeScene("/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml",
								new MultiPlayerMode(main));
					});
				};
				runnable.run();
        		
        		main.setMultiplayerGameStateJSON(JSON);
                out.println(JSON);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}