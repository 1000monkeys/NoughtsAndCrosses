package com.kjellvos.aletho.nac;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kjellvos.aletho.nac.canvas.GameCanvasController;
import com.kjellvos.aletho.nac.gamemode.GameMode;
import com.kjellvos.aletho.nac.gamemode.MultiPlayerMode;
import com.kjellvos.aletho.nac.networking.Client;
import com.kjellvos.aletho.nac.networking.Server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{
	private Stage primaryStage;
	private List<Scene> sceneList = new ArrayList<Scene>();

	private Client client;
	private Server server;
	
	private String multiplayerStateJSON = "{GameState: [0, 0, 0, 0, 0, 0, 0, 0, 0]}";
	private MultiPlayerMode multiPlayerMode = null;
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setServer(Server server) {
		this.server = server;
	}
	
	public Server getServer() {
		return server;
	}
	
	public MultiPlayerMode getMutiPlayerMode() {
		return multiPlayerMode;
	}
	
	public void setMultiPlayerMode(MultiPlayerMode multiPlayerMode) {
		this.multiPlayerMode = multiPlayerMode;
	}
	
	public void changeScene(String fxml){	
		FXMLLoader loader = new FXMLLoader();
		URL fxmlLocation = getClass().getResource(fxml);
		loader.setLocation(fxmlLocation);
		
		Pane pane = null;
		try {
			pane = loader.<Pane>load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (pane != null) {
			Scene scene = new Scene(pane);
			
			ScreenController controller = loader.getController();
			controller.setMain(this);

			sceneList.add(scene);
			primaryStage.setScene(scene);
		}
	}
	
	public void changeScene(String fxml, GameMode gameMode){	
		FXMLLoader loader = new FXMLLoader();
		URL fxmlLocation = getClass().getResource(fxml);
		loader.setLocation(fxmlLocation);
		
		Pane pane = null;
		try {
			pane = loader.<Pane>load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (pane != null) {
			Scene scene = new Scene(pane);
			
			ScreenController controller = loader.getController();
			controller.setMain(this);
			
			((GameCanvasController) controller).setGameMode(gameMode);
			
			sceneList.add(scene);
			primaryStage.setScene(scene);
		}
	}
	
	public void goBackScene() {
		sceneList.remove(sceneList.get(sceneList.size() - 1));
		primaryStage.setScene(sceneList.get(sceneList.size() - 1));
	}
	
	public List<Scene> getSceneList() {
		return sceneList;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void setMultiplayerGameStateJSON(String JSON) {
		this.multiplayerStateJSON = JSON;
	}

	public String getMultiplayerGameStateJSON() {
		return multiplayerStateJSON;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
		changeScene("MainScreen.fxml");
        
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				Platform.exit();
		        System.exit(0);
			}
		});
		
		primaryStage.setTitle("Noughts And Crosses Kjell Vos");
		primaryStage.setResizable(false);
        //stageprimaryS.sizeToScene();
		primaryStage.show();
	}
	
	public static boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}
