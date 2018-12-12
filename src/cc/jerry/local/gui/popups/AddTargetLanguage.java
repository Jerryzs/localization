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

import org.json.JSONArray;
import org.json.JSONObject;

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.utils.LocaleUtils;
import cc.jerry.local.utils.ProjectConfig;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage; 

public class AddTargetLanguage extends Application {
	//--------------------
	Scene scene; 
	GridPane root; 
	
	ComboBox<String> languages; 
	
	HBox aBtns; 
	Button cancel;
	Button add; 
	//--------------------
	
	Font labelsFont = MainGUI.labelsFont; 
	
	public static boolean success; 
	
	public void start(Stage primaryStage) {
		success = false; 
		
		root = new GridPane(); 
		root.setPadding(new Insets(10)); 
		root.setHgap(10);
		root.setVgap(10); 
		
		languages = new ComboBoxWithSearchBar<String>(); 
		A: for (String lang : LocaleUtils.getAllLocaleNames()) {
			for (String item : ProjectConfig.json().getJSONObject("Target Languages").keySet().toArray(new String[0]))
				if (item.contains(lang)) continue A; 
			languages.getItems().add(lang); 
		}
		languages.getItems().sort(null); 
		languages.getItems().remove(0); 
		
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
		
		add = new Button(get("gui.label.add")); 
		add.setFont(labelsFont); 
		add.setDefaultButton(true); 
		add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!languages.getSelectionModel().isEmpty()) {
					JSONObject prjConfig = ProjectConfig.json(); 
					
					JSONArray newLangItem = new JSONArray(); 
					
					for (int i = 0; i < prjConfig.getJSONArray("Strings").length(); i++) 
						newLangItem.put(""); 
					
					prjConfig.getJSONObject("Target Languages").put(languages.getSelectionModel().getSelectedItem(), newLangItem); 
					
					ProjectConfig.write(prjConfig); 
					success = true; 
					primaryStage.close(); 
				}
			}
			
		});
		
		aBtns.getChildren().addAll(cancel, add); 
		aBtns.setAlignment(Pos.CENTER_RIGHT);
		
		root.add(languages, 0, 0, 2, 1); 
		root.add(aBtns, 1, 1); 
		
		GridPane.setHgrow(aBtns, Priority.ALWAYS);
		
		scene = new Scene(root, 350, 85); 
		primaryStage.setScene(scene); 
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.selectlanguage"));
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
}
