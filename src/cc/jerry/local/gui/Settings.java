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

package cc.jerry.local.gui;

import static cc.jerry.commons.util.Localization.get;

import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.commons.util.Localization;
import cc.jerry.local.utils.AppConfig;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage; 

public class Settings {
	
	//--------------------
	Scene scene; 
	BorderPane root; 
	GridPane body; 
	
	Label language; 
	ComboBoxWithSearchBar<String> languageSelections; 
	Label languageSelected; 
	
	HBox aBtns; 
	Button close; 
	Button save; 
	//--------------------
	
	private Font labelsFont = MainGUI.labelsFont; 
	private String appLanguage; 
	
	public static boolean closed = false; 
	
	public void start(Stage primaryStage) {
		closed = false; 
		
		root = new BorderPane(); 
		
		body = new GridPane(); 
		body.setHgap(10); 
		body.setVgap(10); 
		body.setPadding(new Insets(10)); 
		
		language = new Label(get("gui.label.language")); 
		language.setFont(labelsFont); 
		
		languageSelections = new ComboBoxWithSearchBar<String>(); 
		for (Locale locale : Locale.getAvailableLocales()) {
			for (String file : Localization.langFiles) {
				file = FilenameUtils.removeExtension(file); 
				if (locale.toLanguageTag().equals(file)) {
					languageSelections.getItems().add(locale.getDisplayName()); 
				}
			}
		} 
		
		for (Locale locale : Locale.getAvailableLocales()) {
			if (Localization.appLanguage.equals(locale.toLanguageTag())) {
				appLanguage = locale.getDisplayName(); 
			}
		}
		
		languageSelected = new Label(get("gui.label.selected") + appLanguage); 
		languageSelected.setFont(labelsFont); 
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		aBtns.setPadding(new Insets(10)); 
		
		close = new Button(get("gui.label.close")); 
		close.setFont(labelsFont);
		close.setCancelButton(true); 
		close.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				closed = true; 
				primaryStage.close(); 
			}
			
		});
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont); 
		save.setDefaultButton(true); 
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (languageSelections.getSelectedItem() != null) {
					AppConfig.read(); 
					
					String langTag = null; 
					for (Locale locale : Locale.getAvailableLocales()) {
						if (locale.getDisplayName().equals(languageSelections.getSelectedItem())) {
							langTag = locale.toLanguageTag(); 
							break; 
						}
					}
					
					AppConfig.update("language", langTag); 
					AppConfig.write(); 
					
					if (AppConfig.config().getProperty("language").equals("System Default")) 
						new Localization("/main/resources/langs/", "en-US", Locale.getDefault().toLanguageTag(), ".lang", getClass());
					else 
						new Localization("/main/resources/langs/", "en-US", AppConfig.config().getProperty("language"), ".lang", getClass());
					
					languageSelected.setText(get("gui.label.selected") + languageSelections.getSelectedItem()); 
				}
			}
			
		});
		
		aBtns.getChildren().add(close); 
		aBtns.getChildren().add(save); 
		
		body.add(language, 0, 0); 
		body.add(languageSelections, 1, 0);
		body.add(languageSelected, 2, 0); 
		
		root.setCenter(body); 
		root.setBottom(aBtns); 
		
		aBtns.setAlignment(Pos.CENTER); 
		
		scene = new Scene(root, 650, 350); 
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.settings"));
		primaryStage.initModality(Modality.APPLICATION_MODAL); 
		primaryStage.showAndWait(); 
	}
}
