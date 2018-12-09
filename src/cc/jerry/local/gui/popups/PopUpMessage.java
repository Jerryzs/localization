/*
 * Copyright 2018 Siyuan "Jerry" Zhang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jerry.local.gui.popups;

import static cc.jerry.commons.util.Localization.get;

import cc.jerry.local.gui.MainGUI;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUpMessage extends Application {
	//--------------------
	Scene scene; 
	BorderPane root; 
	
	Label message; 
	
	Button done; 
	//--------------------
	
	Font labelsFont = MainGUI.labelsFont; 
	private static String _message; 
	
	public void start(Stage primaryStage) {
		root = new BorderPane(); 
		root.setPadding(new Insets(10));
		
		message = new Label(_message); 
		message.setFont(labelsFont); 
		
		root.setCenter(message); 
		BorderPane.setAlignment(message, Pos.CENTER_LEFT); 
		
		done = new Button(get("gui.label.done")); 
		done.setFont(labelsFont); 
		done.setDefaultButton(true); 
		done.setCancelButton(true); 
		done.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				primaryStage.close(); 
			}
		});
		
		root.setBottom(done); 
		BorderPane.setAlignment(done, Pos.CENTER_RIGHT); 
		
		scene = new Scene(root, 350, 75); 
		primaryStage.setScene(scene); 
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.message")); 
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
	
	public static void message(String message) {
		_message = message; 
	}
}
