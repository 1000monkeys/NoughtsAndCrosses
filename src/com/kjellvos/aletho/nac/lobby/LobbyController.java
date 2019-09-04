package com.kjellvos.aletho.nac.lobby;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.ScreenController;
import com.kjellvos.aletho.nac.gamemode.MultiPlayerMode;
import com.kjellvos.aletho.nac.networking.Client;
import com.kjellvos.aletho.nac.networking.ClientMulticast;
import com.kjellvos.aletho.nac.networking.Server;
import com.kjellvos.aletho.nac.networking.ServerMulticast;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class LobbyController extends Thread implements ScreenController {
	private Main main;
	private ClientMulticast clientMulticast;
	private Client client;
	private boolean gameStarted = false;
	private boolean gameLoaded = false;

	@FXML
	private BorderPane borderPane;
	@FXML
	private ListView<LobbyGame> gamesListView;
	@FXML
	private Button joinGameButton, hostGameButton, cancelButton;

	@FXML
	public void initialize() {
		clientMulticast = new ClientMulticast(this);
	}

	@Override
	public void setMain(Main main) {
		this.main = main;
		clientMulticast.start();
	}

	@Override
	public void run() {
		try {
			while (Server.searching) {
				String message = client.getMessage();
				// System.out.println("Got message: " + message);

				if (message.equals(
						"{\"action\":\"changeScene\",\"scene\":\"/com/kjellvos/aletho/nac/canvas/GameCanvas.fxml\"}")) {
					gameStarted = true;

					

					interrupt();
				}
				sleep(1000);
			}
		} catch (InterruptedException e) {
		}
	}

	public void goBack() {
		if (joinGameButton.isDisable()) {
			// stop hosting
			joinGameButton.setDisable(false);
		} else {
			main.goBackScene();
		}
	}

	public void hostGame() {
		InetAddress ip = null;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String broadcastJSON = new JSONObject("{\"ip\": \"" + ip.getHostAddress() + "\"}").toString();

		Server server = new Server(main);
		ServerMulticast serverMulticast = new ServerMulticast(server);
		serverMulticast.setJSON(broadcastJSON);
		serverMulticast.start();
		server.start();

		joinGameButton.setDisable(true);
	}

	public void joinGame() {
		LobbyGame lobbyGame = gamesListView.getSelectionModel().getSelectedItem();

		InetAddress ip = null;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		String JSON = new JSONObject("{\"ip\": \"" + ip.getHostAddress() + "\"}").toString();

		client = new Client(lobbyGame.getIp(), main);
		client.start();

		start();
	}

	public void setGamesList(ObservableList<LobbyGame> gamesList) {
		gamesListView.getItems().setAll(gamesList);
	}
}
