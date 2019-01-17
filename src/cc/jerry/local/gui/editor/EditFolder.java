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

public class EditFolder {
	//--------------------
	Scene scene; 
	GridPane root; 
	
	Label folder; 
	TextField folderEntry; 
	
	HBox aBtns; 
	Button save; 
	Button cancel; 
	//--------------------
	
	public static JSONObject prjConfig; 
	private static Font labelsFont = MainGUI.labelsFont; 
	public static boolean saveClicked = false; 
	
	private static String _folder; 
	
	public static void setFolder(String folder) {
		EditFolder._folder = folder; 
	}
	
	public void start(Stage primaryStage) {
		saveClicked = false; 
		
		root = new GridPane(); 
		root.setPadding(new Insets(10)); 
		root.setHgap(10); 
		root.setVgap(10); 
		
		folder = new Label(get("gui.label.name")); 
		folder.setFont(labelsFont); 
		
		folderEntry = new TextField(_folder); 
		folderEntry.setFont(labelsFont); 
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont);
		save.setDefaultButton(true); 
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				prjConfig = ProjectConfig.json(); 
				
				if (!folderEntry.getText().equals(get("gui.text.all")) && !folderEntry.getText().equals(get("gui.text.unclassified")) && !prjConfig.getJSONArray("Folders").toList().contains(folderEntry.getText())) {
					int index = prjConfig.getJSONArray("Folders").toList().indexOf(_folder); 
					
					prjConfig.getJSONArray("Folders").put(index, folderEntry.getText()); 
					ProjectConfig.write(prjConfig); 

					saveClicked = true; 
					primaryStage.close(); 
				}
			}
			
		});
		
		cancel = new Button(get("gui.label.cancel")); 
		cancel.setFont(labelsFont); 
		cancel.setCancelButton(true); 
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.close(); 
			}
			
		});
		
		aBtns.getChildren().addAll(cancel, save); 
		aBtns.setAlignment(Pos.CENTER_RIGHT); 
		
		root.add(folder, 0, 0); 
		root.add(folderEntry, 1, 0); 
		root.add(aBtns, 0, 1, 2, 1);
		
		GridPane.setHgrow(folderEntry, Priority.ALWAYS); 
		GridPane.setHgrow(aBtns, Priority.ALWAYS);
		
		scene = new Scene(root, 350, 80); 
		primaryStage.setScene(scene); 
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.editfolder")); 
		primaryStage.initModality(Modality.APPLICATION_MODAL); 
		primaryStage.showAndWait(); 
	}
}
