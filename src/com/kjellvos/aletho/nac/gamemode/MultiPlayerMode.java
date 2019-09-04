package com.kjellvos.aletho.nac.gamemode;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;

public class MultiPlayerMode implements GameMode{
	private Main main = null;
	
	public MultiPlayerMode(Main main) {
		this.main = main;
	}
	
	@Override
	public void squareClicked(int squareId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getGameBoard() {
		String JSON = main.getMultiplayerGameStateJSON();
		
		if (Main.isJSONValid(JSON)) {
			JSONObject gameStateJSON = new JSONObject(JSON);
			
			JSONArray gameStateJSONArray = gameStateJSON.getJSONArray("GameState");
			
			int[] gameboard = new int[9];
			
			for (int i = 0; i < gameStateJSONArray.length(); i++) {
				gameboard[i] = gameStateJSONArray.getInt(i);
			}
			
		}
		
		return null;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getGameStatus() {
		// TODO Auto-generated method stub
		return false;
	}

}
