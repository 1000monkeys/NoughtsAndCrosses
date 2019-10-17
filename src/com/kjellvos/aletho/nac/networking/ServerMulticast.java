package com.kjellvos.aletho.nac.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ServerMulticast extends Thread {
	public static int PORT = 4446;
	private Server server = null;
	String broadcastJSON = null;
	
	public ServerMulticast(Server server) {
		this.server = server;
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
		
		byte[] buffer = broadcastJSON.getBytes();
		
		while (server.getIsSearching()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
        socket.close();
	}

	public void setJSON(String broadcastJSON) {
		this.broadcastJSON = broadcastJSON;
	}
}
