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

import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.gui.popups.RemoveConfirmation;
import cc.jerry.local.main.Main;
import cc.jerry.local.utils.ProjectConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class Editor {
	//--------------------
	Scene scene; 
	BorderPane root; 
	VBox center; 
	GridPane editorCont; 
	GridPane stringsCont; 
	
	HBox folderContainer; 
	
	Label folder; 
	ComboBoxWithSearchBar<String> folderList; 
	
	HBox folderBtns; 
	Button addFolder; 
	Button removeFolder; 
	Button editFolder; 
	
	Label strings; 
	final ListView<String> stringList = new ListView<>();
	
	HBox stringBtns; 
	Button addString; 
	Button removeString; 
	Button editString; 
	Button moveString; 
	
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
	Button close; 
	//--------------------
	
	static JSONObject prjConfig; 
	static String tarLang; 
	static String translateSave = ""; 
	
	static int stringsCount; 
	static int stringsTranslated; 
	static int stringsNotTranslated; 
	
	static int remove = -1; 
	
	final Font labelsFont = MainGUI.labelsFont;
	
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
		
		stringsCont = new GridPane(); 
		stringsCont.setPadding(new Insets(10)); 
		stringsCont.setHgap(10); 
		stringsCont.setVgap(10); 
		
		folderContainer = new HBox(); 
		folderContainer.setPadding(new Insets(10)); 
		folderContainer.setAlignment(Pos.CENTER_LEFT); 
		folderContainer.setSpacing(10); 
		
		folder = new Label(get("gui.label.folder")); 
		folder.setFont(labelsFont);
		
		folderList = new ComboBoxWithSearchBar<>();
		reloadFolders(); 
		folderList.getSelectionModel().select(0); 
		folderList.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (folderList.getSelectedItem().equals(get("gui.text.all")) || folderList.getSelectedItem().equals(get("gui.text.unclassified"))) {
					editFolder.setDisable(true);
					removeFolder.setDisable(true);
				} else {
					editFolder.setDisable(false);
					removeFolder.setDisable(false);
				}
				reload();
				sortListToNTF();
			}
		});
		folderList.setOnHidden(event -> {
			if (folderList.getSelectedItem() != null) {
				if (folderList.getSelectedItem().equals(get("gui.text.all")) || folderList.getSelectedItem().equals(get("gui.text.unclassified"))) {
					editFolder.setDisable(true);
					removeFolder.setDisable(true);
				} else {
					editFolder.setDisable(false);
					removeFolder.setDisable(false);
				}
				reload();
				sortListToNTF();
			}
		});
		
		folderBtns = new HBox(); 
		folderBtns.setSpacing(10); 
		
		addFolder = new Button(get("gui.label.add")); 
		addFolder.setFont(labelsFont);
		addFolder.setOnAction(event -> (new AddFolder()).start(new Stage()));
		
		removeFolder = new Button(get("gui.label.remove")); 
		removeFolder.setFont(labelsFont);
		removeFolder.setOnAction(arg0 -> {
			remove = 1;
			(new RemoveConfirmation()).start(new Stage());
		});
		
		editFolder = new Button(get("gui.label.edit")); 
		editFolder.setFont(labelsFont); 
		editFolder.setOnAction(arg0 -> {
			EditFolder.setFolder(folderList.getSelectedItem());
			(new EditFolder()).start(new Stage());
		});

		editFolder.setDisable(true); 
		removeFolder.setDisable(true); 
		
		folderBtns.getChildren().addAll(addFolder, editFolder, removeFolder); 
		
		folderContainer.getChildren().addAll(folder, folderList, folderBtns); 
		
		HBox.setHgrow(folderBtns, Priority.ALWAYS); 
		
		center = new VBox(); 
		
		strings = new Label(get("gui.label.strings")); 
		strings.setFont(labelsFont); 
		
		stringList.setOnMouseClicked(event -> reloadEditor());
		stringList.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
				reloadEditor();
			}
		});
		
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
		addString.setOnAction(event -> {
			NewString.setFolder(folderList.getSelectedItem());
			(new NewString()).start(new Stage());
		});
		
		removeString = new Button(get("gui.label.remove")); 
		removeString.setFont(labelsFont); 
		removeString.setOnAction(event -> {
			if (!stringList.getSelectionModel().isEmpty()) {
				remove = 0;
				(new RemoveConfirmation()).start(new Stage());
			}
		});
		
		editString = new Button(get("gui.label.edit")); 
		editString.setFont(labelsFont); 
		editString.setOnAction(event -> {
			if (!stringList.getSelectionModel().isEmpty()) {
				EditString.setIndex(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1);
				EditString.setKey(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
				EditString.setString(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3));

				(new EditString()).start(new Stage());
			}
		});
		
		moveString = new Button(get("gui.label.move")); 
		moveString.setFont(labelsFont);
		moveString.setOnAction(event -> {
			if (!stringList.getSelectionModel().isEmpty()) {
				MoveString.setIndex(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1);
				(new MoveString()).start(new Stage());
			}
		});
		
		stringBtns.getChildren().addAll(addString, moveString, editString, removeString); 
		stringBtns.setAlignment(Pos.CENTER_RIGHT); 
		
		notTranslatedFirst = new CheckBox(get("gui.label.shownottranslatedstringsfirst")); 
		notTranslatedFirst.setFont(labelsFont); 
		notTranslatedFirst.setOnAction(event -> {
			if (notTranslatedFirst.isSelected()) {
				sortListToNTF();
			} else
				reload();
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
		translateEntry.setOnKeyReleased(event -> {
			if (stringList.getSelectionModel().isEmpty() || stringList.getSelectionModel().getSelectedItem().contains("\u2717"))
				translateSave = translateEntry.getText();
		});
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont); 
		save.setDefaultButton(true); 
		save.setOnAction(event -> {
			if (!stringList.getSelectionModel().isEmpty()) {
				save();
				stringList.getSelectionModel().select(stringList.getSelectionModel().getSelectedIndex() + 1);

				if (!stringList.getSelectionModel().isEmpty()) {
					keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
					stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3));

					if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
						translateEntry.setText(StringEscapeUtils.unescapeJava(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1)));
					else {
						translateEntry.setText(translateSave);
					}
				}
			}
		});
		
		close = new Button(get("gui.label.close")); 
		close.setFont(labelsFont); 
		close.setCancelButton(true); 
		close.setOnAction(event -> primaryStage.close());
		
		aBtns = new HBox(); 
		aBtns.getChildren().add(close); 
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

		stringsCont.add(strings, 0, 0);
		stringsCont.add(stringBtns, 1, 0, 2, 1); 
		stringsCont.add(stringList, 0, 1, 3, 1); 
		stringsCont.add(notTranslatedFirst, 0, 2, 3, 1);
		stringsCont.add(stringsCountLabel, 0, 3); 
		stringsCont.add(stringsTranslatedLabel, 1, 3); 
		stringsCont.add(stringsNotTranslatedLabel, 2, 3); 

		GridPane.setHgrow(strings, Priority.ALWAYS);  
		GridPane.setHgrow(stringBtns, Priority.ALWAYS);
		GridPane.setHgrow(stringList, Priority.ALWAYS); 
		GridPane.setHgrow(notTranslatedFirst, Priority.ALWAYS);
		GridPane.setHgrow(stringsCountLabel, Priority.ALWAYS); 
		GridPane.setHgrow(stringsTranslatedLabel, Priority.ALWAYS); 
		GridPane.setHgrow(stringsNotTranslatedLabel, Priority.ALWAYS);
		GridPane.setHgrow(stringEntry, Priority.ALWAYS);
		GridPane.setHgrow(keyEntry, Priority.ALWAYS); 
		GridPane.setHgrow(translateEntry, Priority.ALWAYS); 
		GridPane.setHalignment(notTranslatedFirst, HPos.RIGHT);	
		
		center.getChildren().addAll(stringsCont, editorCont); 
		
		HBox.setHgrow(stringsCont, Priority.ALWAYS); 
		HBox.setHgrow(editorCont, Priority.ALWAYS); 
		
		root.setTop(folderContainer); 
		root.setCenter(center); 
		root.setBottom(aBtns);
		
		reload(); 
		
		scene = new Scene(root, 650, 750);

		Main.setIcon(primaryStage);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle(get("gui.main.editor"));
	    
	    primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
			prjConfig = ProjectConfig.json();

			if (NewString.saveClicked) {
				stringList.getItems().add("\u2717 " + prjConfig.getJSONArray("Keys").length() + ". " + prjConfig.getJSONArray("Keys").getString(prjConfig.getJSONArray("Keys").length() - 1) + " | " + StringEscapeUtils.unescapeJava(prjConfig.getJSONArray("Strings").getString((prjConfig.getJSONArray("Strings").length() - 1))));
				stringsNotTranslated++;
				stringsCount++;

				stringsCountLabel.setText(get("gui.label.total") + stringsCount);
				stringsTranslatedLabel.setText(get("gui.label.translated") + stringsTranslated);
				stringsNotTranslatedLabel.setText(get("gui.label.nottranslated") + stringsNotTranslated);

				NewString.saveClicked = false;
			}

			if (RemoveConfirmation.removeConfirmed) {
				if (remove == 0) {
					int index = Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1;
					int listItemIndex = stringList.getSelectionModel().getSelectedIndex();

					prjConfig.getJSONArray("Keys").remove(index);
					prjConfig.getJSONArray("Strings").remove(index);
					prjConfig.getJSONArray("Class").remove(index);

					for (String key : prjConfig.getJSONObject("Target Languages").keySet().toArray(new String[prjConfig.getJSONObject("Target Languages").keySet().size()])) {
						prjConfig.getJSONObject("Target Languages").getJSONArray(key).remove(index);
					}

					ProjectConfig.write(prjConfig);

					reload();

					stringList.getSelectionModel().select(listItemIndex);

					if (!stringList.getSelectionModel().isEmpty()) {
						keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
						stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3));

						if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
							translateEntry.setText(StringEscapeUtils.unescapeJava(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1)));
						else {
							translateEntry.setText(translateSave);
						}
					} else {
						keyEntry.setText("null");
						stringEntry.setText("null");
					}

					remove = -1;
				}

				if (remove == 1) {
					int index = prjConfig.getJSONArray("Folders").toList().indexOf(folderList.getSelectedItem());
					int listIndex = folderList.getSelectionModel().getSelectedIndex();

					for (int i = 0; i < prjConfig.getJSONArray("Class").length(); i++) {
						if (prjConfig.getJSONArray("Class").getInt(i) == index)
							prjConfig.getJSONArray("Class").put(i, -1);
						if (prjConfig.getJSONArray("Class").getInt(i) > index)
							prjConfig.getJSONArray("Class").put(i, prjConfig.getJSONArray("Class").getInt(i) - 1);
					}

					prjConfig.getJSONArray("Folders").remove(index);
					ProjectConfig.write(prjConfig);

					reloadFolders();
					folderList.getSelectionModel().select(listIndex - 1);
					reload();
					remove = -1;
				}

				RemoveConfirmation.removeConfirmed = false;
			}

			if (EditString.saveClicked) {
				int listItemIndex = stringList.getSelectionModel().getSelectedIndex();

				reload();

				stringList.getSelectionModel().select(listItemIndex);
				keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
				stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3));

				if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
					translateEntry.setText(StringEscapeUtils.unescapeJava(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1)));
				else {
					translateEntry.setText(translateSave);
				}

				EditString.saveClicked = false;
			}

			if (MoveString.saveClicked) {
				reload();
				MoveString.saveClicked = false;
			}

			if (AddFolder.saveClicked || EditFolder.saveClicked) {
				int index = folderList.getSelectionModel().getSelectedIndex();
				reloadFolders();
				folderList.getSelectionModel().select(index);
				AddFolder.saveClicked = false;
				EditFolder.saveClicked = false;
			}
		});

		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
	
	private void save() {
		prjConfig = ProjectConfig.json(); 
		
		int index = Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1;
		
		StringBuilder entry = new StringBuilder();
		
		for (char c : translateEntry.getText().toCharArray()) {
			entry.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
		}
		
		prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).put(index, entry.toString());
		
		ProjectConfig.write(prjConfig); 
		
		translateSave = ""; 
		
		if (!translateEntry.getText().equals("")) {
			if (stringList.getSelectionModel().getSelectedItem().contains("\u2717")) {
				stringsTranslated++; 
				stringsNotTranslated--; 
			}
			
			stringList.getItems().set(stringList.getSelectionModel().getSelectedIndex(), "\u2713 " + (index + 1) + ". " + prjConfig.getJSONArray("Keys").getString(index) + " | " + StringEscapeUtils.unescapeJava(prjConfig.getJSONArray("Strings").getString(index))); 
		}
		else {
			if (stringList.getSelectionModel().getSelectedItem().contains("\u2713")) { 
				stringsTranslated--; 
				stringsNotTranslated++;
			}
			
			stringList.getItems().set(stringList.getSelectionModel().getSelectedIndex(), "\u2717 " + (index + 1) + ". " + prjConfig.getJSONArray("Keys").getString(index) + " | " + StringEscapeUtils.unescapeJava(prjConfig.getJSONArray("Strings").getString(index))); 
		}
		
		stringsCountLabel.setText(get("gui.label.total") + stringsCount); 
		stringsTranslatedLabel.setText(get("gui.label.translated") + stringsTranslated); 
		stringsNotTranslatedLabel.setText(get("gui.label.nottranslated") + stringsNotTranslated); 
		
		//System.out.println("Success!"); 
	}
	
	private void reload() {
		stringList.getItems().clear(); 
		stringsCount = 0; 
		stringsTranslated = 0; 
		stringsNotTranslated = 0; 
		
		for (int i = 0; i < prjConfig.getJSONArray("Keys").length(); i++) {
				if (!folderList.getSelectedItem().equals(get("gui.text.all"))) 
					if (prjConfig.getJSONArray("Class").getInt(i) != prjConfig.getJSONArray("Folders").toList().indexOf(folderList.getSelectedItem()))
						continue; 
			
			if (!prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(i).equals("")) {
				stringList.getItems().add("\u2713 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + StringEscapeUtils.unescapeJava(prjConfig.getJSONArray("Strings").getString(i))); 
				stringsTranslated++; 
			}
			else {
				stringList.getItems().add("\u2717 " + (i + 1) + ". " + prjConfig.getJSONArray("Keys").getString(i) + " | " + StringEscapeUtils.unescapeJava(prjConfig.getJSONArray("Strings").getString(i))); 
				stringsNotTranslated++; 
			}
			stringsCount++; 
		}
		
		stringsCountLabel.setText(get("gui.label.total") + stringsCount); 
		stringsTranslatedLabel.setText(get("gui.label.translated") + stringsTranslated); 
		stringsNotTranslatedLabel.setText(get("gui.label.nottranslated") + stringsNotTranslated); 
	}
	
	private void reloadFolders() {
		folderList.getItems().clear(); 
		
		folderList.getItems().add(get("gui.text.all")); 
		folderList.getItems().add(get("gui.text.unclassified"));

		for (Object o : prjConfig.getJSONArray("Folders")) folderList.getItems().add((String) o);
	}
	
	private void reloadEditor() {
		if (!stringList.getSelectionModel().isEmpty()) {
			keyEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(". ") + 2, stringList.getSelectionModel().getSelectedItem().indexOf(" | ")));
			stringEntry.setText(stringList.getSelectionModel().getSelectedItem().substring(stringList.getSelectionModel().getSelectedItem().indexOf(" | ") + 3));
			
			if (stringList.getSelectionModel().getSelectedItem().contains("\u2713"))
				translateEntry.setText(StringEscapeUtils.unescapeJava(prjConfig.getJSONObject("Target Languages").getJSONArray(tarLang).getString(Integer.parseInt(stringList.getSelectionModel().getSelectedItem().substring(2, stringList.getSelectionModel().getSelectedItem().indexOf(". "))) - 1)));
			else {
				translateEntry.setText(translateSave); 
			}
		}
	}
	
	private void sortListToNTF() {
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
}
