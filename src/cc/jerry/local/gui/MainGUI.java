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

import java.io.File;
import java.util.Calendar;

import org.json.JSONObject;

import cc.jerry.commons.util.Localization;
import cc.jerry.commons.util.OS;
import cc.jerry.commons.util.OS.SystemType;
import cc.jerry.local.gui.editor.Editor;
import cc.jerry.local.gui.export.Export;
import cc.jerry.local.gui.popups.AddTargetLanguage;
import cc.jerry.local.gui.popups.ChangeSrcLanguage;
import cc.jerry.local.gui.popups.RemoveConfirmation;
import cc.jerry.local.gui.popups.Rename;
import cc.jerry.local.gui.popups.WaitForLangInit;
import cc.jerry.local.utils.ProjectConfig;
import cc.jerry.local.utils.References;
import de.codecentric.centerdevice.MenuToolkit;
import de.codecentric.centerdevice.dialogs.about.AboutStageBuilder;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainGUI extends Application {
	
	//--------------------
	Scene scene;
	BorderPane root;
	GridPane body; 
	
	MenuToolkit tk; 
	MenuBar menu; 
	MenuBar menuGlobal; 
	
	Menu defaultMenu; 
	Menu fileMenu; 
	Menu editMenu; 
	Menu windowMenu; 
	
	//fileMenu
	MenuItem newFileItem; 
	MenuItem openFileItem; 
	MenuItem exportFileItem; 
	MenuItem settingsFileItem; 
	
	Label projectName;
	Label projectNameEntry; 
	Button projectRenameBtn; 
	
	Label projectDir; 
	Label projectDirEntry; 
	Button projectChangeDirBtn; 
	
	Label projectSrcLang; 
	Label projectSrcLangEntry; 
	Button projectChangeSrcLangBtn; 
	
	Label projectLangs; 
	ListView<String> projectLangsList;
	
	Button projectLangsAdd; 
	Button projectLangsRemove; 
	Button projectLangsEdit; 
	//--------------------
	
	public static Font labelsFont = new Font("Georgia", 14);
	
	public void start(Stage primaryStage) {
		
		root = new BorderPane(); 
		
		body = new GridPane();
		body.setHgap(10);
		body.setVgap(10);
		body.setPadding(new Insets(10));
		
		menu = new MenuBar();
		if (OS.os() == SystemType.Mac) {
			menu.useSystemMenuBarProperty().set(true);
			
			tk = MenuToolkit.toolkit(); 
			defaultMenu = tk.createDefaultApplicationMenu(get("gui.main.apptitle")); 
			AboutStageBuilder aboutStageBuilder = AboutStageBuilder.start(get("gui.text.about") + " " + get("gui.main.apptitle"))
			        .withAppName(get("gui.main.apptitle")).withCloseOnFocusLoss().withVersionString(get("gui.text.version") + " " + References.version)
			        .withCopyright("Copyright \u00A9 " + Calendar.getInstance().get(Calendar.YEAR) + " Jerry Zhang");
			
			aboutStageBuilder = aboutStageBuilder.withImage(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		     
			tk.setApplicationMenu(defaultMenu); 
			defaultMenu.getItems().set(0, tk.createAboutMenuItem(get("gui.main.apptitle"), aboutStageBuilder.build())); 

			windowMenu = new Menu("Window");
			windowMenu.getItems().addAll(tk.createMinimizeMenuItem(), tk.createZoomMenuItem());
		}
		
		fileMenu = new Menu(get("gui.menu.file"));
		
		newFileItem = new MenuItem(get("gui.menu.file.new")); 
		newFileItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+N"));
		newFileItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				(new NewProject()).start(new Stage()); 
			}
			
		});
		
		openFileItem = new MenuItem(get("gui.menu.file.open"));
		openFileItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
		openFileItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser filChooser = new FileChooser();
				filChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JLC File (*.jlcf)", "*.jlcf"), new FileChooser.ExtensionFilter("All Files", "*.*"));
				File selectedDirectory = filChooser.showOpenDialog(new Stage());
				if (selectedDirectory != null) {
					ProjectConfig.file(selectedDirectory); 
					System.out.println(ProjectConfig.file().getAbsolutePath()); 
					parseConfigFile(); 
				}
			}
			
		});
		
		exportFileItem = new MenuItem(get("gui.menu.file.export")); 
		exportFileItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+E"));
		exportFileItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (ProjectConfig.file() != null)
					(new Export()).start(new Stage()); 
			}
			
		});
		
		settingsFileItem = new MenuItem(get("gui.menu.file.settings")); 
		settingsFileItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (Localization.listFolderThread.isAlive())
					(new WaitForLangInit()).start(new Stage()); 
				else 
					(new Settings()).start(new Stage()); 
			}
			
		});
		
		fileMenu.getItems().add(newFileItem);
		fileMenu.getItems().add(openFileItem); 
		fileMenu.getItems().add(new SeparatorMenuItem()); 
		fileMenu.getItems().add(exportFileItem); 
		fileMenu.getItems().add(new SeparatorMenuItem()); 
		fileMenu.getItems().add(settingsFileItem); 
		
		menu.getMenus().addAll(fileMenu);
		if (OS.os() == SystemType.Mac) 
			menu.getMenus().add(windowMenu);
		
		projectName = new Label(get("gui.label.projectname"));
		projectName.setFont(labelsFont);
		
		projectNameEntry = new Label(get("gui.message.plsopenprojectfile")); 
		projectNameEntry.setFont(labelsFont); 
		
		projectRenameBtn = new Button(get("gui.label.rename")); 
		projectRenameBtn.setFont(labelsFont); 
		projectRenameBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (ProjectConfig.file() != null)
					(new Rename()).start(new Stage()); 
			}
			
		});
		
		projectDir = new Label(get("gui.label.projectdirectory")); 
		projectDir.setFont(labelsFont); 
		
		projectDirEntry = new Label(); 
		projectDirEntry.setFont(labelsFont); 
		
		projectChangeDirBtn = new Button(get("gui.label.move")); 
		projectChangeDirBtn.setFont(labelsFont); 
		projectChangeDirBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (ProjectConfig.file() != null) {
					DirectoryChooser dirChooser = new DirectoryChooser();
					File selectedDirectory = dirChooser.showDialog(new Stage());
					System.out.println(selectedDirectory);
					if (selectedDirectory != null) {
						ProjectConfig.file().renameTo(new File(selectedDirectory.getAbsolutePath() + File.separator + ProjectConfig.file().getName())); 
						ProjectConfig.file(new File(selectedDirectory.getAbsolutePath() + File.separator + ProjectConfig.file().getName())); 
						
						ProjectConfig.read(); 
						
						JSONObject obj = ProjectConfig.json(); 
						obj.put("Directory", selectedDirectory.getAbsolutePath()); 
						
						ProjectConfig.write(obj); 
						
						parseConfigFile(); 
					}
					else {
						
					}
				}
			}
			
		});
		
		projectSrcLang = new Label(get("gui.label.projectsourcelanguage")); 
		projectSrcLang.setFont(labelsFont); 
		
		projectSrcLangEntry = new Label(); 
		projectSrcLangEntry.setFont(labelsFont); 
		
		projectChangeSrcLangBtn = new Button(get("gui.label.change")); 
		projectChangeSrcLangBtn.setFont(labelsFont); 
		projectChangeSrcLangBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (ProjectConfig.file() != null) 
					(new ChangeSrcLanguage()).start(new Stage()); 
			}
			
		});
		
		projectLangs = new Label(get("gui.label.projectlangs")); 
		projectLangs.setFont(labelsFont); 
		
		projectLangsList = new ListView<>();
		projectLangsList.setPrefHeight(50);
		
		projectLangsAdd = new Button(get("gui.label.add")); 
		projectLangsAdd.setFont(labelsFont); 
		projectLangsAdd.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (ProjectConfig.file() != null)
					(new AddTargetLanguage()).start(new Stage()); 
			}
			
		});
		
		projectLangsRemove = new Button(get("gui.label.remove")); 
		projectLangsRemove.setFont(labelsFont); 
		projectLangsRemove.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!projectLangsList.getSelectionModel().isEmpty())
					(new RemoveConfirmation()).start(new Stage());
			}
			
		});
		
		projectLangsEdit = new Button(get("gui.label.edit")); 
		projectLangsEdit.setFont(labelsFont); 
		projectLangsEdit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!projectLangsList.getSelectionModel().isEmpty()) {
					Editor.setupEditor(ProjectConfig.json(), projectLangsList.getSelectionModel().getSelectedItem()); 
					(new Editor()).start(new Stage()); 
				}
			}
			
		});
		
		body.add(projectName, 0, 0); 
		body.add(projectNameEntry, 1, 0, 3, 1); 
		body.add(projectRenameBtn, 4, 0); 
		body.add(projectDir, 0, 1); 
		body.add(projectDirEntry, 1, 1, 2, 1);
		body.add(projectChangeDirBtn, 4, 1);
		body.add(projectSrcLang, 0, 2); 
		body.add(projectSrcLangEntry, 1, 2, 2, 1);
		body.add(projectChangeSrcLangBtn, 4, 2);
		body.add(projectLangs, 0, 3);
		body.add(projectLangsList, 1, 3, 1, 3);
		body.add(projectLangsAdd, 2, 3);
		body.add(projectLangsRemove, 2, 4); 
		body.add(projectLangsEdit, 2, 5); 
		
		GridPane.setHalignment(projectRenameBtn, HPos.RIGHT);
		GridPane.setHalignment(projectChangeDirBtn, HPos.RIGHT);
		GridPane.setHalignment(projectChangeSrcLangBtn, HPos.RIGHT);
		GridPane.setHgrow(projectNameEntry, Priority.ALWAYS); 
		GridPane.setHgrow(projectDirEntry, Priority.ALWAYS);
		GridPane.setHgrow(projectSrcLangEntry, Priority.ALWAYS);
		GridPane.setHgrow(projectLangsList, Priority.ALWAYS);
		//body.setGridLinesVisible(true);
		
		root.setTop(menu);
		root.setCenter(body); 
		
		scene = new Scene(root, 650, 350);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
	    primaryStage.setScene(scene);
	    primaryStage.setTitle(get("gui.main.apptitle"));
	    primaryStage.show();
	    
	    primaryStage.setOnCloseRequest(e -> System.exit(0));
	    
	    primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (AddTargetLanguage.success) {
					parseConfigFile(); 
					AddTargetLanguage.success = false; 
				}
				if (RemoveConfirmation.removeConfirmed) {
					JSONObject obj = ProjectConfig.json(); 
					obj.getJSONObject("Target Languages").remove(projectLangsList.getSelectionModel().getSelectedItem()); 
					ProjectConfig.write(obj); 
					parseConfigFile(); 
					RemoveConfirmation.removeConfirmed = false; 
				}
				if (NewProject.closed) {
					parseConfigFile(); 
					NewProject.closed = false; 
				}
				if (Rename.closed) {
					parseConfigFile(); 
					Rename.closed = false; 
				}
				if (ChangeSrcLanguage.closed) {
					parseConfigFile(); 
					ChangeSrcLanguage.closed = false; 
				}
				if (Settings.closed) {
					Settings.closed = false; 
					(new MainGUI()).start(primaryStage); 
				}
			}
	    	
	    });
	}
	
	public void parseConfigFile() {
		ProjectConfig.read(); 
		
		if (ProjectConfig.file() != null) {
			projectLangsList.getItems().clear(); 
			
			try {
				projectNameEntry.setText(ProjectConfig.json().getString("Project Name"));
				projectSrcLangEntry.setText(ProjectConfig.json().getString("Src Language")); 
				projectDirEntry.setText(ProjectConfig.json().getString("Directory")); 
				
				for (String key : ProjectConfig.json().getJSONObject("Target Languages").keySet().toArray(new String[ProjectConfig.json().getJSONObject("Target Languages").keySet().size()])) {
					projectLangsList.getItems().add(key); 
				}
	
				projectLangsList.getItems().sort(null); 
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("File is invalid"); 
			}
		}
		else
			projectNameEntry.setText(get("gui.message.fileisinvalid")); 
	}
}
