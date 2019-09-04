package com.kjellvos.aletho.nac.lobby;

public class LobbyGame {
	String ip;
	
	public LobbyGame(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}
	
	@Override
    public String toString() 
    {
        return ip.toString() + " as IP.";
    }
}
