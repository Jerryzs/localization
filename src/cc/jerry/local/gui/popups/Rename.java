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

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static cc.jerry.commons.util.Localization.get;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.utils.ProjectConfig; 

public class Rename extends Application {
	
	//--------------------
	Scene scene; 
	GridPane root; 
	
	TextField newName; 
	
	HBox aBtns; 
	Button cancel; 
	Button rename; 
	//--------------------
	
	Font labelsFont = MainGUI.labelsFont; 
	
	public static boolean closed; 
	
	public void start(Stage primaryStage) { 
		root = new GridPane(); 
		root.setPadding(new Insets(10)); 
		root.setHgap(10);
		root.setVgap(10); 
		
		newName = new TextField(FilenameUtils.removeExtension(ProjectConfig.file().getName())); 
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		aBtns.setAlignment(Pos.CENTER_RIGHT); 
		
		cancel = new Button(get("gui.label.cancel")); 
		cancel.setFont(labelsFont); 
		cancel.setCancelButton(true); 
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.close(); 
			}
			
		});
		
		rename = new Button(get("gui.label.rename")); 
		rename.setFont(labelsFont); 
		rename.setDefaultButton(true); 
		rename.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				boolean success = ProjectConfig.file().renameTo(new File(ProjectConfig.file().getParentFile().getAbsolutePath() + newName.getText() + "." + FilenameUtils.getExtension(ProjectConfig.file().getName()))); 
				
				if (success) {
					ProjectConfig.file(new File(ProjectConfig.file().getParentFile().getAbsolutePath() + newName.getText() + "." + FilenameUtils.getExtension(ProjectConfig.file().getName()))); 
					
					ProjectConfig.read(); 
					JSONObject obj = ProjectConfig.json(); 
					obj.put("Project Name", newName.getText()); 
					
					ProjectConfig.write(obj);
					
					closed = true; 
					primaryStage.close(); 
				}
				else {
					PopUpMessage.message(get("gui.message.filenotcreatederror")); 
					(new PopUpMessage()).start(new Stage()); 
				}
			}
			
		});
		
		aBtns.getChildren().addAll(cancel, rename); 
		
		root.add(newName, 0, 0, 2, 1); 
		root.add(aBtns, 1, 1);
		
		GridPane.setHalignment(aBtns, HPos.RIGHT);
		GridPane.setHgrow(newName, Priority.ALWAYS);
		GridPane.setHgrow(aBtns, Priority.ALWAYS); 
		
		scene = new Scene(root, 350, 75); 
		primaryStage.setScene(scene); 
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.rename")); 
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
	
}
