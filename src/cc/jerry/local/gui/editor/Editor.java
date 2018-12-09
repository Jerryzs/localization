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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.gui.popups.RemoveConfirmation;
import cc.jerry.local.utils.ProjectConfig;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class Editor extends Application{
	//--------------------
	Scene scene; 
	BorderPane root; 
	GridPane editorCont; 
	GridPane stringListCont; 
	
	Label strings; 
	ListView<String> stringList = new ListView<String>(); 
	
	HBox stringBtns; 
	Button addString; 
	Button removeString; 
	Button editString; 
	
	CheckBox notTranslatedFirst; 
	
	Label stringsCountLabel; 
	Label stringsTranslatedLabel; 
	Label stringsNotTranslatedLabel; 
	
	Label key; 
	Label keyEntry; 
	Label string; 
	Label stringEntry; 
	
	Label translate; 
	TextField translateEntry; 
	
	HBox aBtns; 
	Button save; 
	Button cancel; 
	//--------------------
	
	static JSONObject prjConfig; 
	static String tarLang; 
	static String translateSave = ""; 
	
	static int stringsCount; 
	static int stringsTranslated; 
	static int stringsNotTranslated; 
	
	Font labelsFont = MainGUI.labelsFont;
	
	public static void setupEditor(JSONObject prjConfig, String tarLang) {
		Editor.prjConfig = prjConfig; 
		Editor.tarLang = tarLang; 
	}
	
	public void start(Stage primaryStage) {
		stringsCount = 0; 
		stringsTranslated = 0; 
		stringsNotTranslated = 0; 
		
		root = new BorderPane(); 
		editorCont = new GridPane(); 
		editorCont.setPadding(new Insets(10));
		editorCont.setHgap(10); 
		editorCont.setVgap(10); 
		
		stringListCont = new GridPane(); 
		stringListCont.setPadding(new Insets(10)); 
		stringListCont.setHgap(10); 
		stringListCont.setVgap(10); 
		
		strings = new Label(get("gui.label.strings")); 
		strings.setFont(labelsFont); 
		
		for (int i = 0; i < prjConfig.getJSONArray("Keys").length(); i++) {
			if (!prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(i).equals("")) {
				stringList.getItems().add("\u2713 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + prjConfig.getJSONArray("Strings").getString(i)); 
				stringsTranslated++; 
			}
			else {
				stringList.getItems().add("\u2717 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + prjConfig.getJSONArray("Strings").getString(i)); 
				stringsNotTranslated++; 
			}
			stringsCount++; 
		}
		
		stringList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (!stringList.getSelectionModel().isEmpty()) {
					keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
					stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3, stringList.getSelectionModel().getSelectedItem().length()));
					
					if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
						translateEntry.setText(StringEscapeUtils.unescapeJava(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1)));
					else {
						translateEntry.setText(translateSave); 
					}
				}
			}
			
		});;
		
		stringsCountLabel = new Label(get("gui.label.total") + stringsCount); 
		stringsCountLabel.setFont(labelsFont); 	
		
		stringsTranslatedLabel = new Label(get("gui.label.translated") + stringsTranslated); 
		stringsTranslatedLabel.setFont(labelsFont); 
		
		stringsNotTranslatedLabel = new Label(get("gui.label.nottranslated") + stringsNotTranslated); 
		stringsNotTranslatedLabel.setFont(labelsFont); 
		
		stringBtns = new HBox(); 
		stringBtns.setSpacing(10); 
		
		addString = new Button(get("gui.label.add")); 
		addString.setFont(labelsFont); 
		addString.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				(new NewString()).start(new Stage());
			}
			
		}); 
		
		removeString = new Button(get("gui.label.remove")); 
		removeString.setFont(labelsFont); 
		removeString.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!stringList.getSelectionModel().isEmpty()) {
					(new RemoveConfirmation()).start(new Stage()); 
				}
			}
			
		});
		
		editString = new Button(get("gui.label.edit")); 
		editString.setFont(labelsFont); 
		editString.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!stringList.getSelectionModel().isEmpty()) {
					EditString.setIndex(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1); 
					EditString.setKey(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | "))); 
					EditString.setString(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3, stringList.getSelectionModel().getSelectedItem().length())); 
					
					(new EditString()).start(new Stage()); 
				}
			}
			
		});
		
		stringBtns.getChildren().addAll(editString, addString, removeString); 
		stringBtns.setAlignment(Pos.CENTER_RIGHT); 
		
		notTranslatedFirst = new CheckBox(get("gui.label.shownottranslatedstringsfirst")); 
		notTranslatedFirst.setFont(labelsFont); 
		notTranslatedFirst.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (notTranslatedFirst.isSelected()) {
					ObservableList<String> list = FXCollections.observableArrayList(); 
					
					for (String item : stringList.getItems()) {
						if (item.contains("\u2717")) {
							list.add(item); 
						}
					}
					for (String item : stringList.getItems()) {
						if (item.contains("\u2713")) {
							list.add(item); 
						}
					}
					
					stringList.setItems(list);
				}
				else {
					ObservableList<String> list = FXCollections.observableArrayList(); 
					
					for (int i = 0; i < stringList.getItems().size(); i++) {
						for (String item : stringList.getItems()) {
							if (item.startsWith(Integer.toString((i + 1)), 2)) {
								list.add(item); 
								break; 
							}
						}
					}
					
					stringList.setItems(list);  
				}
			}
			
		});
		
		key = new Label(get("gui.label.key")); 
		key.setFont(labelsFont); 
		
		keyEntry = new Label(); 
		keyEntry.setFont(labelsFont); 
		
		string = new Label(get("gui.label.string")); 
		string.setFont(labelsFont); 
		
		stringEntry = new Label(); 
		stringEntry.setFont(labelsFont); 
		
		translate = new Label(get("gui.label.translate")); 
		translate.setFont(labelsFont); 
		
		translateEntry = new TextField(translateSave); 
		translateEntry.setFont(labelsFont); 
		translateEntry.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (stringList.getSelectionModel().isEmpty() || stringList.getSelectionModel().getSelectedItem().contains("\u2717"))
					translateSave = translateEntry.getText(); 
			}
			
		});
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont); 
		save.setDefaultButton(true); 
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!stringList.getSelectionModel().isEmpty()) {
					save(); 
					stringList.getSelectionModel().select(stringList.getSelectionModel().getSelectedIndex() + 1); 
					
					if (!stringList.getSelectionModel().isEmpty()) {
						keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
						stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3, stringList.getSelectionModel().getSelectedItem().length()));
						
						if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
							translateEntry.setText(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1));
						else {
							translateEntry.setText(translateSave); 
						}
					}
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
		
		aBtns = new HBox(); 
		aBtns.getChildren().add(cancel); 
		aBtns.getChildren().add(save); 
		aBtns.setSpacing(10); 
		aBtns.setPadding(new Insets(10)); 
		aBtns.setAlignment(Pos.CENTER); 
		
		editorCont.add(key, 0, 0); 
		editorCont.add(keyEntry, 1, 0); 
		editorCont.add(string, 0, 1); 
		editorCont.add(stringEntry, 1, 1); 
		editorCont.add(translate, 0, 2); 
		editorCont.add(translateEntry, 0, 3, 2, 1); 
		
		stringListCont.add(strings, 0, 0);
		stringListCont.add(stringBtns, 1, 0, 2, 1); 
		stringListCont.add(stringList, 0, 1, 3, 1); 
		stringListCont.add(notTranslatedFirst, 0, 2, 3, 1);
		stringListCont.add(stringsCountLabel, 0, 3); 
		stringListCont.add(stringsTranslatedLabel, 1, 3); 
		stringListCont.add(stringsNotTranslatedLabel, 2, 3); 
		
		GridPane.setHgrow(stringList, Priority.ALWAYS); 
		GridPane.setHgrow(strings, Priority.ALWAYS); 
		GridPane.setHgrow(notTranslatedFirst, Priority.ALWAYS);
		GridPane.setHgrow(stringsCountLabel, Priority.ALWAYS); 
		GridPane.setHgrow(stringsTranslatedLabel, Priority.ALWAYS); 
		GridPane.setHgrow(stringsNotTranslatedLabel, Priority.ALWAYS); 
		GridPane.setHgrow(stringBtns, Priority.ALWAYS);
		GridPane.setHalignment(notTranslatedFirst, HPos.RIGHT);	
		
		root.setLeft(stringListCont); 
		root.setCenter(editorCont); 
		root.setBottom(aBtns);
		
		scene = new Scene(root, 650, 350);
	    primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
	    primaryStage.setTitle(get("gui.main.editor"));
	    
	    primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				prjConfig = ProjectConfig.json(); 
				
				if (NewString.saveClicked) {
					stringList.getItems().add("\u2717 " + (stringList.getItems().size() + 1) + ". " + prjConfig.getJSONArray("Keys").getString(prjConfig.getJSONArray("Keys").length() - 1) + " | " + prjConfig.getJSONArray("Strings").getString((prjConfig.getJSONArray("Strings").length() - 1))); 
					stringsNotTranslated++; 
					stringsCount++; 
					
					stringsCountLabel.setText(get("gui.label.total") + stringsCount); 
					stringsTranslatedLabel.setText(get("gui.label.translated") + stringsTranslated); 
					stringsNotTranslatedLabel.setText(get("gui.label.nottranslated") + stringsNotTranslated); 
					
					NewString.saveClicked = false; 
				}
				
				if (RemoveConfirmation.removeConfirmed) {
					int index = Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1; 
					int listItemIndex = stringList.getSelectionModel().getSelectedIndex(); 
					
					prjConfig.getJSONArray("Keys").remove(index); 
					prjConfig.getJSONArray("Strings").remove(index); 
					
					for (String key : prjConfig.getJSONObject("Target Languages").keySet().toArray(new String[prjConfig.getJSONObject("Target Languages").keySet().size()])) {
						prjConfig.getJSONObject("Target Languages").getJSONArray(key).remove(index);  
					}
					
					ProjectConfig.write(prjConfig); 
					
					stringList.getItems().clear(); 
					stringsCount = 0; 
					stringsTranslated = 0; 
					stringsNotTranslated = 0; 
					for (int i = 0; i < prjConfig.getJSONArray("Keys").length(); i++) {
						if (!prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(i).equals("")) {
							stringList.getItems().add("\u2713 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + prjConfig.getJSONArray("Strings").getString(i)); 
							stringsTranslated++; 
						}
						else {
							stringList.getItems().add("\u2717 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + prjConfig.getJSONArray("Strings").getString(i)); 
							stringsNotTranslated++; 
						}
						stringsCount++; 
					}

					stringsCountLabel.setText(get("gui.label.total") + stringsCount); 
					stringsTranslatedLabel.setText(get("gui.label.translated") + stringsTranslated); 
					stringsNotTranslatedLabel.setText(get("gui.label.nottranslated") + stringsNotTranslated); 
					
					stringList.getSelectionModel().select(listItemIndex);
					
					if (!stringList.getSelectionModel().isEmpty()) {
						keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
						stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3, stringList.getSelectionModel().getSelectedItem().length()));
						
						if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
							translateEntry.setText(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1));
						else {
							translateEntry.setText(translateSave); 
						}
					}
					
					else {
						keyEntry.setText("null"); 
						stringEntry.setText("null"); 
					}
					
					RemoveConfirmation.removeConfirmed = false; 
				}
				
				if (EditString.saveClicked) {
					int listItemIndex = stringList.getSelectionModel().getSelectedIndex(); 
					
					stringList.getItems().clear(); 
					for (int i = 0; i < prjConfig.getJSONArray("Keys").length(); i++) {
						if (!prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(i).equals("")) {
							stringList.getItems().add("\u2713 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + prjConfig.getJSONArray("Strings").getString(i)); 
							stringsTranslated++; 
						}
						else {
							stringList.getItems().add("\u2717 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + prjConfig.getJSONArray("Strings").getString(i)); 
							stringsNotTranslated++; 
						}
						stringsCount++; 
					}
					
					stringList.getSelectionModel().select(listItemIndex); 
					keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
					stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3, stringList.getSelectionModel().getSelectedItem().length()));
					
					if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
						translateEntry.setText(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1));
					else {
						translateEntry.setText(translateSave); 
					}
					
					EditString.saveClicked = false; 
				}
			}
	    	
	    });

		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
	
	private void save() {
		prjConfig = ProjectConfig.json(); 
		
		String entry = ""; 
		
		for (char c : translateEntry.getText().toCharArray()) {
			entry += "\\u" + Integer.toHexString(c | 0x10000).substring(1); 
		}
		
		prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).put(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1, entry); 
		
		ProjectConfig.write(prjConfig); 
		
		translateSave = ""; 
		
		if (!translateEntry.getText().equals("")) {
			if (stringList.getSelectionModel().getSelectedItem().contains("\u2717")) {
				stringsTranslated++; 
				stringsNotTranslated--; 
			}
			
			stringList.getItems().set(stringList.getSelectionModel().getSelectedIndex(), "\u2713 " + (stringList.getSelectionModel().getSelectedIndex() + 1) + ". " + prjConfig.getJSONArray("Keys").getString(stringList.getSelectionModel().getSelectedIndex()) + " | " + prjConfig.getJSONArray("Strings").getString(stringList.getSelectionModel().getSelectedIndex())); 
		}
		else {
			if (stringList.getSelectionModel().getSelectedItem().contains("\u2713")) { 
				stringsTranslated--; 
				stringsNotTranslated++;
			}
			
			stringList.getItems().set(stringList.getSelectionModel().getSelectedIndex(), "\u2717 " + (stringList.getSelectionModel().getSelectedIndex() + 1) + ". " + prjConfig.getJSONArray("Keys").getString(stringList.getSelectionModel().getSelectedIndex()) + " | " + prjConfig.getJSONArray("Strings").getString(stringList.getSelectionModel().getSelectedIndex())); 
		}
		
		stringsCountLabel.setText(get("gui.label.total") + stringsCount); 
		stringsTranslatedLabel.setText(get("gui.label.translated") + stringsTranslated); 
		stringsNotTranslatedLabel.setText(get("gui.label.nottranslated") + stringsNotTranslated); 
		
		//System.out.println("Success!"); 
	}
	
}
