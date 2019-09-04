package com.kjellvos.aletho.nac.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.lobby.LobbyController;
import com.kjellvos.aletho.nac.lobby.LobbyGame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientMulticast extends Thread {
	public static int PORT = 4446;
	public List<LobbyGame> list = new ArrayList<LobbyGame>();
	public ObservableList<LobbyGame> games = FXCollections.observableList(list);
	private LobbyController lobbyController;
	
	public ClientMulticast(LobbyController lobbyController) {
		this.lobbyController = lobbyController;
	}

	public void run() {
		MulticastSocket socket = null;
		InetAddress group = null;
		
		try {
			socket = new MulticastSocket(PORT);
			group = InetAddress.getByName("230.13.37.0");
			socket.joinGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] b = new byte[512];
		DatagramPacket packet = new DatagramPacket(b, b.length);
		
        try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        String received = new String(packet.getData(), 0, packet.getLength());
        
        if (!games.contains(received.toString())) {
        	JSONObject json = new JSONObject(received);
        	
        	games.add(new LobbyGame(json.getString("ip")));
        	lobbyController.setGamesList(games);
        }
        
        try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
        socket.close();
	}
}
