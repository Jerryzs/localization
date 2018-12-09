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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.commons.javafx.ListViewWithSearchBar;
import cc.jerry.local.gui.popups.PopUpMessage;
import cc.jerry.local.utils.ProjectConfig;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewProject extends Application{
	//--------------------
	GridPane root; 
	Scene scene; 
	
	Label projectName; 
	TextField projectNameEntry; 
	
	Label projectDir; 
	Label projectDirEntry; 
	Button projectDirBtn; 
	
	Label setSourceLanguage; 
	ComboBox<String> sourceLanguageSelections; 
	
	Label selectTargetLanguages; 
	ListView<String> selectedLanguages; 
	Button addTLanguage; 
	Button removeSLanguage; 
	ListView<String> allLanguages; 
	
	HBox aButtons; 
	Button save; 
	Button cancel; 
	//--------------------
	
	Font labelsFont = MainGUI.labelsFont;
	String fileChosen = ""; 
	
	public static boolean closed = false; 
	
	public void start(Stage primaryStage) {
		root = new GridPane(); 
		root.setHgap(10);
		root.setVgap(10); 
		root.setPadding(new Insets(10));
		
		projectName = new Label(get("gui.label.projectname")); 
		projectName.setFont(labelsFont); 
		
		projectNameEntry = new TextField(); 
		projectNameEntry.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				projectDirEntry.setText(fileChosen + File.separator + projectNameEntry.getText() + ".jlcf");
			}
			
		});
		
		projectDir = new Label(get("gui.label.projectdirectory")); 
		projectDir.setFont(labelsFont); 
		
		projectDirEntry = new Label(); 
		projectDirEntry.setFont(labelsFont); 
		
		projectDirBtn = new Button("..."); 
		projectDirBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser dirChooser = new DirectoryChooser();
				File selectedDirectory = dirChooser.showDialog(new Stage());
				System.out.println(selectedDirectory);
				if (selectedDirectory != null) {
					fileChosen = selectedDirectory.getAbsolutePath();
					if (!fileChosen.endsWith(File.separator))
						fileChosen += File.separator; 
					projectDirEntry.setText(fileChosen + projectNameEntry.getText() + ".jlcf");
				}
				else {
					
				}
			}
			
		});
		
		setSourceLanguage = new Label(get("gui.label.setsourcelanguage")); 
		setSourceLanguage.setFont(labelsFont); 
		
		sourceLanguageSelections = new ComboBoxWithSearchBar<String>(); 
		for (Locale locale : Locale.getAvailableLocales()) {
			if (locale.getDisplayName().contains("("))
				sourceLanguageSelections.getItems().add(locale.getDisplayName()); 
		}
		sourceLanguageSelections.getItems().sort(null); 
		
		selectTargetLanguages = new Label(get("gui.label.selecttargetlanguages")); 
		selectTargetLanguages.setFont(labelsFont); 
		
		selectedLanguages = new ListView<String>(); 
		
		addTLanguage = new Button("<-"); 
		addTLanguage.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!allLanguages.getSelectionModel().isEmpty()) {
					selectedLanguages.getItems().add(allLanguages.getSelectionModel().getSelectedItem()); 
					allLanguages.getItems().remove(allLanguages.getSelectionModel().getSelectedIndex()); 
					if (!(allLanguages.getSelectionModel().isSelected(0) || allLanguages.getSelectionModel().isSelected(allLanguages.getItems().size() - 1)))
						allLanguages.getSelectionModel().select(allLanguages.getSelectionModel().getSelectedIndex() + 1); 
				}
			}
			
		});
		
		removeSLanguage = new Button("->"); 
		removeSLanguage.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!selectedLanguages.getSelectionModel().isEmpty()) {
					allLanguages.getItems().add(selectedLanguages.getSelectionModel().getSelectedItem()); 
					allLanguages.getItems().sort(null); 
					selectedLanguages.getItems().remove(selectedLanguages.getSelectionModel().getSelectedIndex()); 
				}
			}
			
		});
		
		allLanguages = new ListViewWithSearchBar<String>(projectNameEntry); 
		for (Locale locale : Locale.getAvailableLocales()) {
			if (!locale.getDisplayName().equals("") && locale.getDisplayName().contains("("))
				allLanguages.getItems().add(locale.getDisplayName()); 
		}
		allLanguages.getItems().sort(null); 
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont); 
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				save(primaryStage); 
			}
			
		});
		
		cancel = new Button(get("gui.label.cancel")); 
		cancel.setFont(labelsFont); 
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				primaryStage.close(); 
			}
			
		});
		
		aButtons = new HBox(); 
		aButtons.getChildren().addAll(cancel, save); 
		aButtons.setSpacing(10); 
		aButtons.setAlignment(Pos.CENTER_RIGHT);
		
		root.add(projectName, 0, 0);
		root.add(projectNameEntry, 1, 0, 4, 1); 
		root.add(projectDir, 0, 1); 
		root.add(projectDirEntry, 1, 1, 3, 1); 
		root.add(projectDirBtn, 4, 1); 
		root.add(setSourceLanguage, 0, 2);
		root.add(sourceLanguageSelections, 1, 2, 4, 1);
		root.addRow(3, selectTargetLanguages); 
		root.add(selectedLanguages, 0, 4, 2, 3);
		root.add(addTLanguage, 2, 4); 
		root.add(removeSLanguage, 2, 5);
		root.add(allLanguages, 3, 4, 1, 3);
		root.add(aButtons, 4, 7);
		
		GridPane.setHgrow(aButtons, Priority.ALWAYS);
		GridPane.setHgrow(selectedLanguages, Priority.ALWAYS); 
		GridPane.setHgrow(projectDirEntry, Priority.NEVER); 
		GridPane.setHalignment(aButtons, HPos.RIGHT); 
		GridPane.setHalignment(projectDirBtn, HPos.RIGHT); 
		
		//root.setGridLinesVisible(true); 
		
		scene = new Scene(root, 650, 350);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("");
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	    
	}
	
	private void save(Stage primaryStage) {
		if (!projectNameEntry.getText().isEmpty() && fileChosen != null && !fileChosen.isEmpty() && !sourceLanguageSelections.getSelectionModel().getSelectedItem().isEmpty() && !selectedLanguages.getItems().isEmpty()) {
			JSONObject prjConfig = new JSONObject(); 
			
			prjConfig.put("Project Name", projectNameEntry.getText()); 
			prjConfig.put("Directory", fileChosen); 
			prjConfig.put("Src Language", sourceLanguageSelections.getSelectionModel().getSelectedItem()); 
			prjConfig.put("Keys", new JSONArray()); 
			prjConfig.put("Strings", new JSONArray()); 
			
			JSONObject tarLangList = new JSONObject(); 
			
			String[] langs = selectedLanguages.getItems().toArray(new String[selectedLanguages.getItems().size()]); 
			Arrays.sort(langs);

			for (String langDisName : langs) {
				tarLangList.put(langDisName, new JSONArray()); 
			}
			
			prjConfig.put("Target Languages", tarLangList); 
			
			JSONArray twoEArrays = new JSONArray(); 
			twoEArrays.put(new JSONArray()); 
			twoEArrays.put(new JSONArray()); 
			
			prjConfig.put("Custom File Formats", (new JSONArray()).put(new JSONArray()).put(new JSONArray())); 
			
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(fileChosen + File.separator + projectNameEntry.getText() + ".jlcf"));
				writer.write(prjConfig.toString()); 
			} catch (FileNotFoundException ex) {
				PopUpMessage.message(get("gui.message.filenotcreatederror")); 
				(new PopUpMessage()).start(new Stage()); 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			
			ProjectConfig.file(new File(fileChosen + File.separator + projectNameEntry.getText() + ".jlcf"));
			closed = true; 
			
			System.out.println("Success!"); 
			primaryStage.close(); 
		}
	}
}
