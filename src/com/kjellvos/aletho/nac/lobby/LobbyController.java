package com.kjellvos.aletho.nac.lobby;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONObject;

import com.kjellvos.aletho.nac.Main;
import com.kjellvos.aletho.nac.ScreenController;
import com.kjellvos.aletho.nac.networking.Client;
import com.kjellvos.aletho.nac.networking.ClientMulticast;
import com.kjellvos.aletho.nac.networking.Server;
import com.kjellvos.aletho.nac.networking.ServerMulticast;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class LobbyController implements ScreenController {
	private Main main;
	private ClientMulticast clientMulticast;
	private Client client;

	private ServerMulticast serverMulticast = null;
	
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

	public void goBack() {
		if (joinGameButton.isDisable()) {
			if (serverMulticast != null) {
				serverMulticast.interrupt();
			}
			joinGameButton.setDisable(false);
		} else {
			main.goBackScene();
		}
	}

	public void hostGame() {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String broadcastJSON = new JSONObject("{\"ip\": \"" + ip.getHostAddress() + "\"}").toString();

		Server server = new Server(main);
		ServerMulticast serverMulticast = new ServerMulticast(server);
		serverMulticast.setJSON(broadcastJSON);
		serverMulticast.start();
		main.setServer(server);
		server.start();
		
		joinGameButton.setDisable(true);
	}

	public void joinGame() {
		LobbyGame lobbyGame = gamesListView.getSelectionModel().getSelectedItem();

		main.setClient(new Client(main, lobbyGame.getIp()));
		main.getClient().start();
	}

	public void setGamesList(ObservableList<LobbyGame> gamesList) {
		gamesListView.getItems().setAll(gamesList);
	}
}
