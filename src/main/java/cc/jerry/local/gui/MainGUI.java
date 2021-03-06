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

import org.json.JSONObject;

import cc.jerry.commons.util.OS;
import cc.jerry.commons.util.OS.SystemType;
import cc.jerry.local.gui.editor.Editor;
import cc.jerry.local.gui.export.Export;
import cc.jerry.local.gui.popups.AddTargetLanguage;
import cc.jerry.local.gui.popups.ChangeSrcLanguage;
import cc.jerry.local.gui.popups.RemoveConfirmation;
import cc.jerry.local.gui.popups.Rename;
import cc.jerry.local.Main;
import cc.jerry.local.utils.ProjectConfig;
//import cc.jerry.local.utils.References;
//import de.codecentric.centerdevice.MenuToolkit;
//import de.codecentric.centerdevice.dialogs.about.AboutStageBuilder;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainGUI {
	
	//--------------------
	Scene scene;
	BorderPane root;
	GridPane body; 
	
//	MenuToolkit tk;
	MenuBar menu;
	
	Menu defaultMenu; 
	Menu fileMenu;
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
	
	Label translateProgress; 
	HBox translateProgressCont; 
	ProgressBar translateProgressBar; 
	Label translateProgressText; 
	
	Button projectLangsAdd; 
	Button projectLangsRemove; 
	Button projectLangsEdit; 
	//--------------------
	
	public static final Font labelsFont = new Font("Times New Roman", 14);
	
	public void start(Stage primaryStage) {
		
		root = new BorderPane(); 
		
		body = new GridPane();
		body.setHgap(10);
		body.setVgap(10);
		body.setPadding(new Insets(10));
		
		menu = new MenuBar();

//		Has bug: IllegalAccessError
//		if (OS.os() == SystemType.Mac) {
//			menu.useSystemMenuBarProperty().set(true);
//
//			tk = MenuToolkit.toolkit();
//			defaultMenu = tk.createDefaultApplicationMenu(get("gui.main.apptitle"));
//			AboutStageBuilder aboutStageBuilder = AboutStageBuilder.start(get("gui.text.about") + " " + get("gui.main.apptitle"))
//			        .withAppName(get("gui.main.apptitle")).withCloseOnFocusLoss().withVersionString(get("gui.text.version") + " " + References.version)
//			        .withCopyright("Copyright \u00A9 " + Calendar.getInstance().get(Calendar.YEAR) + " Jerry Zhang");
//
//			try {
//				aboutStageBuilder = aboutStageBuilder.withImage(new Image(new FileInputStream(Main.icon)));
//			} catch (FileNotFoundException e1) {
//				System.err.println("[ERROR] Icon not found");
//			}
//
//			tk.setApplicationMenu(defaultMenu);
//			defaultMenu.getItems().set(0, tk.createAboutMenuItem(get("gui.main.apptitle"), aboutStageBuilder.build()));
//
//			windowMenu = new Menu(get("gui.menu.window"));
//			windowMenu.getItems().addAll(tk.createMinimizeMenuItem(), tk.createZoomMenuItem());
//		}
		
		fileMenu = new Menu(get("gui.menu.file"));
		
		newFileItem = new MenuItem(get("gui.menu.file.new")); 
		newFileItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+N"));
		newFileItem.setOnAction(e -> (new NewProject()).start(new Stage()));
		
		openFileItem = new MenuItem(get("gui.menu.file.open"));
		openFileItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
		openFileItem.setOnAction(e -> {
			FileChooser filChooser = new FileChooser();
			filChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JLC File (*.jlcf)", "*.jlcf"), new FileChooser.ExtensionFilter("All Files", "*.*"));
			File selectedDirectory = filChooser.showOpenDialog(new Stage());
			if (selectedDirectory != null) {
				ProjectConfig.file(selectedDirectory);
				System.out.println("[INFO] Project opened: " + ProjectConfig.file().getAbsolutePath());
				parseConfigFile();
			}
		});
		
		exportFileItem = new MenuItem(get("gui.menu.file.export")); 
		exportFileItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+E"));
		exportFileItem.setOnAction(e -> {
			if (ProjectConfig.file() != null)
				(new Export()).start(new Stage());
		});
		
		settingsFileItem = new MenuItem(get("gui.menu.file.settings")); 
		settingsFileItem.setOnAction(e -> (new Settings()).start(new Stage()));
		
		fileMenu.getItems().add(newFileItem);
		fileMenu.getItems().add(openFileItem); 
		fileMenu.getItems().add(new SeparatorMenuItem()); 
		fileMenu.getItems().add(exportFileItem); 
		fileMenu.getItems().add(new SeparatorMenuItem()); 
		fileMenu.getItems().add(settingsFileItem); 
		
		menu.getMenus().addAll(fileMenu);
		if (OS.os() == SystemType.MACOS)
			menu.getMenus().add(windowMenu);
		
		projectName = new Label(get("gui.label.projectname"));
		projectName.setFont(labelsFont);
		
		projectNameEntry = new Label(get("gui.message.plsopenprojectfile")); 
		projectNameEntry.setFont(labelsFont); 
		
		projectRenameBtn = new Button(get("gui.label.rename")); 
		projectRenameBtn.setFont(labelsFont); 
		projectRenameBtn.setOnAction(e -> {
			if (ProjectConfig.file() != null)
				(new Rename()).start(new Stage());
		});
		
		projectDir = new Label(get("gui.label.projectdirectory")); 
		projectDir.setFont(labelsFont); 
		
		projectDirEntry = new Label(); 
		projectDirEntry.setFont(labelsFont); 
		
		projectChangeDirBtn = new Button(get("gui.label.move")); 
		projectChangeDirBtn.setFont(labelsFont); 
		projectChangeDirBtn.setOnAction(e -> {
			if (ProjectConfig.file() != null) {
				DirectoryChooser dirChooser = new DirectoryChooser();
				File selectedDirectory = dirChooser.showDialog(new Stage());
				System.out.println(selectedDirectory);
				if (selectedDirectory != null) {
					File newFile = new File(selectedDirectory.getAbsolutePath() + File.separator + ProjectConfig.file().getName());

					if (ProjectConfig.file().renameTo(newFile)) {
						ProjectConfig.file(newFile);

						ProjectConfig.read();

						JSONObject obj = ProjectConfig.json();
						obj.put("Directory", selectedDirectory.getAbsolutePath());

						ProjectConfig.write(obj);

						parseConfigFile();
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
		projectChangeSrcLangBtn.setOnAction(e -> {
			if (ProjectConfig.file() != null)
				(new ChangeSrcLanguage()).start(new Stage());
		});
		
		projectLangs = new Label(get("gui.label.projectlangs")); 
		projectLangs.setFont(labelsFont); 
		
		projectLangsList = new ListView<>();
		projectLangsList.setPrefHeight(50);
		projectLangsList.setOnMouseClicked(e -> updateTranslateProgress());
		projectLangsList.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN)
				updateTranslateProgress();
		});
		
		projectLangsAdd = new Button(get("gui.label.add")); 
		projectLangsAdd.setFont(labelsFont); 
		projectLangsAdd.setOnAction(e -> {
			if (ProjectConfig.file() != null)
				(new AddTargetLanguage()).start(new Stage());
		});
		
		projectLangsRemove = new Button(get("gui.label.remove")); 
		projectLangsRemove.setFont(labelsFont); 
		projectLangsRemove.setOnAction(e -> {
			if (!projectLangsList.getSelectionModel().isEmpty())
				(new RemoveConfirmation()).start(new Stage());
		});
		
		projectLangsEdit = new Button(get("gui.label.edit")); 
		projectLangsEdit.setFont(labelsFont);
		projectLangsEdit.setOnAction(e -> {
			if (!projectLangsList.getSelectionModel().isEmpty()) {
				Editor.setupEditor(ProjectConfig.json(), projectLangsList.getSelectionModel().getSelectedItem());
				(new Editor()).start(new Stage());
			}
		});
		
		translateProgress = new Label(get("gui.label.translateprogress")); 
		translateProgress.setFont(labelsFont); 
		
		translateProgressCont = new HBox(); 
		translateProgressCont.setSpacing(10);
		
		translateProgressBar = new ProgressBar(); 
		translateProgressBar.setProgress(0); 

		translateProgressText = new Label("0%"); 
		
		translateProgressBar.prefWidthProperty().bind(body.widthProperty().subtract(70)); 
		translateProgressCont.getChildren().addAll(translateProgressBar, translateProgressText); 
		
		body.add(projectName, 0, 0); 
		body.add(projectNameEntry, 1, 0); 
		body.add(projectRenameBtn, 2, 0); 
		body.add(projectDir, 0, 1); 
		body.add(projectDirEntry, 1, 1);
		body.add(projectChangeDirBtn, 2, 1);
		body.add(projectSrcLang, 0, 2); 
		body.add(projectSrcLangEntry, 1, 2);
		body.add(projectChangeSrcLangBtn, 2, 2);
		body.add(projectLangs, 0, 3);
		body.add(projectLangsList, 1, 3, 1, 3);
		body.add(projectLangsAdd, 2, 3);
		body.add(projectLangsRemove, 2, 4); 
		body.add(projectLangsEdit, 2, 5); 
		body.add(translateProgress, 0, 6); 
		body.add(translateProgressCont, 0, 7, 3, 1); 
		
		GridPane.setHalignment(projectRenameBtn, HPos.RIGHT);
		GridPane.setHalignment(projectChangeDirBtn, HPos.RIGHT);
		GridPane.setHalignment(projectChangeSrcLangBtn, HPos.RIGHT);
		GridPane.setHalignment(projectLangsAdd, HPos.RIGHT); 
		GridPane.setHalignment(projectLangsRemove, HPos.RIGHT); 
		GridPane.setHalignment(projectLangsEdit, HPos.RIGHT);

		GridPane.setHgrow(projectNameEntry, Priority.ALWAYS);
		GridPane.setHgrow(projectDirEntry, Priority.SOMETIMES);
		GridPane.setHgrow(projectSrcLangEntry, Priority.ALWAYS);
		GridPane.setHgrow(projectLangsList, Priority.ALWAYS);

//		body.setGridLinesVisible(true);
		
		root.setTop(menu);
		root.setCenter(body); 

		scene = new Scene(root, 650, 350);

		Main.setIcon(primaryStage);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle(get("gui.main.apptitle"));
	    primaryStage.show();
	    
	    primaryStage.setOnCloseRequest(e -> System.exit(0));
	    
	    primaryStage.focusedProperty().addListener(e -> {
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
				ProjectConfig.closeReader();
				(new MainGUI()).start(primaryStage);
			}
			if (ProjectConfig.json() != null)
				updateTranslateProgress();
		});
	}
	
	public void parseConfigFile() { 
		ProjectConfig.read(); 
		
		if (ProjectConfig.file() != null) {
			projectLangsList.getItems().clear();

			String cfgPath = ProjectConfig.file().getAbsolutePath();

			projectNameEntry.setText(ProjectConfig.json().getString("Project Name"));
			projectSrcLangEntry.setText(ProjectConfig.json().getString("Src Language"));

			int lastSeparator = cfgPath.lastIndexOf(File.separator);
			projectDirEntry.setText(cfgPath.substring(0, cfgPath.indexOf(File.separator) + 1) + "..."
					+ cfgPath.substring(
							cfgPath.lastIndexOf(File.separator, lastSeparator - 1),
							lastSeparator + 1));

			for (String key : ProjectConfig.json().getJSONObject("Target Languages").keySet().toArray(new String[0])) {
				projectLangsList.getItems().add(key);
			}

			projectLangsList.getItems().sort(null);

		}
		else
			projectNameEntry.setText(get("gui.message.fileisinvalid")); 
	}
	
	private void updateTranslateProgress() {
		if (!projectLangsList.getSelectionModel().isEmpty()) {
			JSONObject json = ProjectConfig.json(); 
			double translated = 0; 
			int allStrings = json.getJSONArray("Keys").length(); 
			
			for (int i = 0; i < allStrings; i++) {
				if (!json.getJSONObject("Target Languages").getJSONArray(projectLangsList.getSelectionModel().getSelectedItem()).getString(i).isEmpty())
					translated++; 
			}
			
			translateProgressBar.setProgress(translated/allStrings); 
			translateProgressText.setText(Math.round(translated/allStrings*100) + "%"); 
		}
	}
}
