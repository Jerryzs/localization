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

package cc.jerry.local.gui.editor;

import static cc.jerry.commons.util.Localization.get;

import org.json.JSONObject;

import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.utils.ProjectConfig;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewString {
	//--------------------
	Scene scene; 
	GridPane root; 
	
	Label key; 
	TextField keyEntry; 
	
	Label string; 
	TextField stringEntry; 
	
	HBox aBtns; 
	Button cancel; 
	Button save; 
	//--------------------
	
	static JSONObject prjConfig; 
	
	static Font labelsFont = MainGUI.labelsFont; 
	
	static boolean saveClicked = false; 
	
	private static String folder; 
	
	public void start(Stage primaryStage) {
		saveClicked = false; 
		
		root = new GridPane(); 
		root.setPadding(new Insets(10)); 
		root.setHgap(10); 
		root.setVgap(10);
		
		key = new Label(get("gui.label.key")); 
		key.setFont(labelsFont);
		
		keyEntry = new TextField(); 
		
		string = new Label(get("gui.label.string")); 
		string.setFont(labelsFont); 
		
		stringEntry = new TextField(); 
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		
		cancel = new Button(get("gui.label.cancel")); 
		cancel.setFont(labelsFont); 
		cancel.setCancelButton(true); 
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.close(); 
			}
			
		});
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont); 
		save.setDefaultButton(true); 
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!keyEntry.getText().contains("|")) {
					prjConfig = ProjectConfig.json(); 
					
					String entry = ""; 
					
					for (char c : stringEntry.getText().toCharArray()) {
						entry += "\\u" + Integer.toHexString(c | 0x10000).substring(1); 
					}
					
					prjConfig.getJSONArray("Keys").put(keyEntry.getText()); 
					prjConfig.getJSONArray("Strings").put(entry); 
					prjConfig.getJSONArray("Class").put(prjConfig.getJSONArray("Folders").toList().indexOf(folder)); 
					
					for (String key : prjConfig.getJSONObject("Target Languages").keySet().toArray(new String[prjConfig.getJSONObject("Target Languages").keySet().size()])) {
							prjConfig.getJSONObject("Target Languages").getJSONArray(key).put(""); 
					}
					
					ProjectConfig.write(prjConfig); 
					saveClicked = true; 
					
					primaryStage.close(); 
				}
			}
			
		});
		
		aBtns.getChildren().addAll(cancel, save); 
		aBtns.setAlignment(Pos.CENTER_RIGHT);
		
		root.add(key, 0, 0); 
		root.add(keyEntry, 1, 0);
		root.add(string, 0, 1); 
		root.add(stringEntry, 1, 1); 
		root.add(aBtns, 1, 2); 
		
		GridPane.setHgrow(keyEntry, Priority.ALWAYS);
		GridPane.setHgrow(stringEntry, Priority.ALWAYS); 
		GridPane.setHgrow(aBtns, Priority.ALWAYS); 
		
		scene = new Scene(root, 350, 125);
	    primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
	    primaryStage.setTitle(get("gui.main.addstring"));
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
	
	public static void setFolder(String folder) {
		NewString.folder = folder; 
	}
}
