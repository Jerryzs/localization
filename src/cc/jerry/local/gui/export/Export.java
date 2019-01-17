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

package cc.jerry.local.gui.export;

import static cc.jerry.commons.util.Localization.get;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.gui.popups.RemoveConfirmation;
import cc.jerry.local.utils.AddLangs;
import cc.jerry.local.utils.CustomizedFileFormats;
import cc.jerry.local.utils.LocaleUtils;
import cc.jerry.local.utils.ProjectConfig;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class Export extends Application {
	
	//--------------------
	Scene scene; 
	BorderPane root; 
	GridPane center; 
	
	Label title; 
	
	Label fileFormat; 
	ComboBox<String> fileFormatList; 
	
	HBox eBtns; 
	Button editFileFormat; 
	Button removeFileFormat; 
	
	Label fileSuffix; 
	TextField fileSuffixEntry; 
	
	Label fileNameFormat; 
	ComboBox<String> fileNameFormatList; 
	
	Label directory; 
	Label directoryEntry; 
	CheckBox createNewFolder; 
	Button directoryButton; 
	
	HBox aBtns; 
	Button cancel; 
	Button export; 
	//--------------------
	
	private static Font labelsFont = MainGUI.labelsFont; 
	private static File dirChosen = new File(ProjectConfig.json().getString("Directory")); 
	
	@Override
	public void start(Stage primaryStage) {
		root = new BorderPane();
		root.setPadding(new Insets(10)); 
		
		center = new GridPane(); 
		center.setVgap(10); 
		center.setHgap(10); 
		root.setCenter(center); 
		
		title = new Label(get("gui.main.exportoptions")); 
		title.setFont(labelsFont);
		title.setPadding(new Insets(0, 0, 10, 0));
		root.setTop(title); 
		BorderPane.setAlignment(title, Pos.CENTER); 
		
		fileFormat = new Label(get("gui.label.fileformat")); 
		fileFormat.setFont(labelsFont); 
		
		fileFormatList = new ComboBox<String>(); 
		fileFormatList.getItems().add("Java Properties"); 
		fileFormatList.getItems().add("JSON"); 
		if (!CustomizedFileFormats.isEmpty()) {
			JSONArray json = CustomizedFileFormats.json().getJSONArray(0); 
			for (int i = 0; i < json.length(); i++) {
				fileFormatList.getItems().add(json.getString(i)); 
			}
		}
		fileFormatList.getItems().add(get("gui.label.customize") + " " + get("gui.message.underdevelopment")); 
		fileFormatList.getSelectionModel().select(0); 
		fileFormatList.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!fileFormatList.getSelectionModel().isEmpty()) {
					switch (fileFormatList.getSelectionModel().getSelectedIndex()) {
						case 0:  
							fileSuffixEntry.setText(".properties");
							break; 
							
						case 1: 
							fileSuffixEntry.setText(".json"); 
							break; 
							
						default: 
							if (fileFormatList.getSelectionModel().isSelected(fileFormatList.getItems().size() - 1)) {
								editFileFormat.setDisable(true); 
								removeFileFormat.setDisable(true); 
								(new FileFormatEditor()).start(new Stage()); 
								break; 
							}
							else if (fileFormatList.getSelectionModel().getSelectedIndex() < 2) {
								editFileFormat.setDisable(true); 
								removeFileFormat.setDisable(true); 
								break; 
							}
							else {
								editFileFormat.setDisable(false); 
								removeFileFormat.setDisable(false); 
								break; 
							}
					}
				}
			}
			
		});
		
		eBtns = new HBox(); 
		eBtns.setSpacing(10); 
		eBtns.setAlignment(Pos.CENTER); 
		
		editFileFormat = new Button(get("gui.label.edit")); 
		editFileFormat.setFont(labelsFont); 
		editFileFormat.setDisable(true); 
		editFileFormat.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				int index = fileFormatList.getSelectionModel().getSelectedIndex() - 2; 
				(new FileFormatEditor()).start(new Stage(), CustomizedFileFormats.name(index), CustomizedFileFormats.syntax(index), index);
			}
			
		});
		
		removeFileFormat = new Button(get("gui.label.remove")); 
		removeFileFormat.setFont(labelsFont); 
		removeFileFormat.setDisable(true); 
		removeFileFormat.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				(new RemoveConfirmation()).start(new Stage()); 
			}
			
		});
		
		eBtns.getChildren().addAll(editFileFormat, removeFileFormat); 
		
		fileSuffix = new Label(get("gui.label.filesuffix")); 
		fileSuffix.setFont(labelsFont); 
		
		fileSuffixEntry = new TextField(".properties"); 
		fileSuffixEntry.setFont(labelsFont); 
		
		fileNameFormat = new Label(get("gui.label.filenameformat")); 
		fileNameFormat.setFont(labelsFont); 
		
		fileNameFormatList = new ComboBox<String>(); 
		if (!ProjectConfig.json().getBoolean("Specify Country in Filenames")) {
			fileNameFormatList.getItems().add("en"); 
			fileNameFormatList.getItems().add("english"); 
			fileNameFormatList.getItems().add("English"); 
		}
		else {
			fileNameFormatList.getItems().add("en_US"); 
			fileNameFormatList.getItems().add("en-US"); 
		}
		fileNameFormatList.getSelectionModel().select(0); 
		
		directory = new Label(get("gui.label.exportdirectory")); 
		directory.setFont(labelsFont); 
		
		directoryEntry = new Label(); 
		if (dirChosen.getAbsolutePath().endsWith(File.separator))
			directoryEntry.setText(dirChosen.getAbsolutePath()); 
		else directoryEntry.setText(dirChosen.getAbsolutePath() + File.separator); 
		directoryEntry.setFont(labelsFont);
		
		createNewFolder = new CheckBox(get("gui.text.createnewfolder")); 
		createNewFolder.setFont(labelsFont); 
		createNewFolder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (createNewFolder.isSelected()) {
					if (dirChosen.getAbsolutePath().endsWith(File.separator))
						directoryEntry.setText(dirChosen.getAbsolutePath() + "langs" + File.separator);
					else directoryEntry.setText(dirChosen.getAbsolutePath() + File.separator + "langs" + File.separator);
				}
				else {
					if (dirChosen.getAbsolutePath().endsWith(File.separator))
						directoryEntry.setText(dirChosen.getAbsolutePath()); 
					else directoryEntry.setText(dirChosen.getAbsolutePath() + File.separator); 
				}
			}
			
		});
		
		directoryButton = new Button("..."); 
		directoryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser dirChooser = new DirectoryChooser(); 
				dirChooser.setInitialDirectory(ProjectConfig.file().getParentFile()); 
				dirChosen = dirChooser.showDialog(new Stage()); 
				if (dirChosen != null) {
					if (dirChosen.getAbsolutePath().endsWith(File.separator))
						directoryEntry.setText(dirChosen.getAbsolutePath()); 
					else 
						directoryEntry.setText(dirChosen.getAbsolutePath() + File.separator); 
				}
			}
			
		});
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		aBtns.setAlignment(Pos.CENTER); 
		root.setBottom(aBtns); 
		
		cancel = new Button(get("gui.label.cancel")); 
		cancel.setFont(labelsFont); 
		cancel.setCancelButton(true); 
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.close(); 
			}
			
		});
		
		export = new Button(get("gui.label.export")); 
		export.setFont(labelsFont); 
		export.setDefaultButton(true); 
		export.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (fileSuffixEntry.getText().contains(".") && !fileSuffixEntry.getText().contains(" ")) {
					File folder = null; 
					if (createNewFolder.isSelected())
						if (dirChosen.getAbsolutePath().endsWith(File.separator))
							folder = new File(dirChosen.getAbsolutePath() + "langs"); 
						else
							folder = new File(dirChosen.getAbsolutePath() + File.separator + "langs"); 
					else 
						folder = dirChosen; 
					
					folder.mkdirs(); 
					
					JSONObject json = ProjectConfig.json(); 
					
					
					
					for (int i = -1; i < json.getJSONObject("Target Languages").keySet().size(); i++) {
						String lang = i == -1 ? json.getString("Src Language") : json.getJSONObject("Target Languages").keySet().toArray(new String[0])[i]; 
						int langIndex = -1; 
						Locale locale = null; 
						try {
							langIndex = (Integer) LocaleUtils.nameToLocale(lang); 
						} catch (ClassCastException e) {
							locale = (Locale) LocaleUtils.nameToLocale(lang); 
						}
						
						System.out.println(lang); 
						System.out.println(langIndex);
						
						String fileName = null; 
						
						switch (fileNameFormatList.getSelectionModel().getSelectedItem()) {
							case "en": 
								if (langIndex == -1)
									fileName = locale.getLanguage(); 
								else 
									fileName = AddLangs.langTags[langIndex]; 
								break; 
								
							case "english":
								if (langIndex == -1)
									fileName = locale.getDisplayLanguage(Locale.ENGLISH).toLowerCase(); 
								else
									fileName = AddLangs.langNames[langIndex].toLowerCase(); 
								break; 
								
							case "English": 
								if (langIndex == -1)
									fileName = locale.getDisplayLanguage(Locale.ENGLISH); 
								else 
									fileName = AddLangs.langNames[langIndex]; 
								break; 
								
							case "en_US":
								if (langIndex == -1)
									fileName = locale.getLanguage() + "_" + locale.getCountry(); 
								else 
									fileName = AddLangs.langTagsFullU[langIndex]; 
								break; 
								
							case "en-US":
								if (langIndex == -1)
									fileName = locale.getLanguage() + "-" + locale.getCountry(); 
								else 
									fileName = AddLangs.langTagsFull[langIndex]; 
								break; 
								
							default:
								break; 
								
						}
						
						if (fileFormatList.getSelectionModel().getSelectedIndex() == 0) {
							Properties pFile = new Properties(); 
							
							for (int j = 0; j < json.getJSONArray("Keys").length(); j++) {
								if (i == -1)
									pFile.setProperty(json.getJSONArray("Keys").getString(j), StringEscapeUtils.unescapeJava(json.getJSONArray("Strings").getString(j))); 
								else 
									pFile.setProperty(json.getJSONArray("Keys").getString(j), StringEscapeUtils.unescapeJava(json.getJSONObject("Target Languages").getJSONArray(lang).getString(j))); 
							}
							
							try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(folder.getAbsolutePath() + File.separator + fileName + fileSuffixEntry.getText())), "UTF-8")) {
								pFile.store(writer, null); 
							} catch (IOException e) {
								e.printStackTrace();
							} 
						}
						else if (fileFormatList.getSelectionModel().getSelectedIndex() == 1) {
							try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(folder.getAbsolutePath() + File.separator + fileName + fileSuffixEntry.getText())), "UTF-8"))) {
								writer.println("{");
								for (int j = 0; j < json.getJSONArray("Keys").length(); j++) {
									if (j != 0) writer.println(","); 
									if (i == -1)
										writer.print("  \"" + json.getJSONArray("Keys").getString(j) + "\":\"" + StringEscapeUtils.unescapeJava(json.getJSONArray("Strings").getString(j)) + "\""); 
									else
										writer.print("  \"" + json.getJSONArray("Keys").getString(j) + "\":\"" + StringEscapeUtils.unescapeJava(json.getJSONObject("Target Languages").getJSONArray(lang).getString(j)) + "\""); 
								}
								writer.println(); 
								writer.println("}"); 
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
						}
						else {
							String syntax = CustomizedFileFormats.syntax(fileFormatList.getSelectionModel().getSelectedIndex() - 2); 
							System.out.println("\n" + CustomizedFileFormats.parse(syntax));
						}
						
						primaryStage.close(); 
					}
				}
			}
			
		});
		
		aBtns.getChildren().addAll(cancel, export); 
		
		center.add(fileFormat, 0, 0); 
		center.add(fileFormatList, 1, 0); 
		center.add(eBtns, 2, 0, 2, 1);
		center.add(fileSuffix, 0, 1); 
		center.add(fileSuffixEntry, 1, 1, 3, 1); 
		center.add(fileNameFormat, 0, 2); 
		center.add(fileNameFormatList, 1, 2, 3, 1); 
		center.add(directory, 0, 3); 
		center.add(directoryEntry, 1, 3, 2, 1); 
		center.add(createNewFolder, 3, 3); 
		center.add(directoryButton, 4, 3); 
		
		GridPane.setHgrow(fileSuffixEntry, Priority.ALWAYS);
		GridPane.setHgrow(fileNameFormatList, Priority.ALWAYS); 
		GridPane.setHgrow(directoryEntry, Priority.ALWAYS); 
		GridPane.setHgrow(createNewFolder, Priority.ALWAYS); 
		GridPane.setHalignment(createNewFolder, HPos.RIGHT);
		
		scene = new Scene(root, 650, 350); 
		primaryStage.setScene(scene); 
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.exportoptions"));
		
		primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) { 
				if (FileFormatEditor.saveClicked) {
					System.out.println("Custom File For Saved"); 
					
					int index = fileFormatList.getSelectionModel().getSelectedIndex(); 
					fileFormatList.getItems().clear();
					fileFormatList.getItems().add("Java Properties"); 
					fileFormatList.getItems().add("JSON"); 
					if (!CustomizedFileFormats.isEmpty()) {
						JSONArray json = CustomizedFileFormats.json().getJSONArray(0); 
						for (int i = 0; i < json.length(); i++) {
							fileFormatList.getItems().add(json.getString(i)); 
						}
					}
					fileFormatList.getItems().add(get("gui.label.customize")); 
					fileFormatList.getSelectionModel().select(index); 
					
					FileFormatEditor.saveClicked = false; 
				}
				if (RemoveConfirmation.removeConfirmed) {
					System.out.println("Removed"); 
					
					CustomizedFileFormats.delete(fileFormatList.getSelectionModel().getSelectedIndex() - 2); 
					
					int index = 0; 
					
					if (!fileFormatList.getSelectionModel().isSelected(fileFormatList.getItems().size() - 2)) 
						index = fileFormatList.getSelectionModel().getSelectedIndex(); 
					else 
						index = fileFormatList.getSelectionModel().getSelectedIndex() - 1; 
					fileFormatList.getItems().clear();
					fileFormatList.getItems().add("Java Properties"); 
					fileFormatList.getItems().add("JSON"); 
					if (!CustomizedFileFormats.isEmpty()) {
						JSONArray json = CustomizedFileFormats.json().getJSONArray(0); 
						for (int i = 0; i < json.length(); i++) {
							fileFormatList.getItems().add(json.getString(i)); 
						}
					}
					fileFormatList.getItems().add(get("gui.label.customize")); 
					fileFormatList.getSelectionModel().select(index); 
					
					RemoveConfirmation.removeConfirmed = false; 
				}
			}
			
		});
		
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}

}
