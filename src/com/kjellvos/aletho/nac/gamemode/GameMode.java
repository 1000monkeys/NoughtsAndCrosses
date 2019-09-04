package com.kjellvos.aletho.nac.gamemode;

public interface GameMode {
	public boolean getGameStatus();
	
	public void squareClicked(int squareId);
	
	public int[] getGameBoard();
	
	public void tick();
}
