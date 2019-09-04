package com.kjellvos.aletho.nac;

import com.kjellvos.aletho.nac.gamemode.SinglePlayerMode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainScreenController implements ScreenController {
	private Main main;
	@FXML BorderPane borderPane;
	@FXML VBox vBox;
	@FXML Button singlePlayerButton, lobbyButton, exitButton;
	
	@Override
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void goToSinglePlayer() {
		main.changeScene("/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml", new SinglePlayerMode());
	}

	public void goToMultiPlayer() {
		main.changeScene("/com/kjellvos/aletho/nac/lobby/LobbyScreen.fxml");
	}
}
