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

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.Main;
import cc.jerry.local.utils.ProjectConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MoveString {
	//--------------------
	Scene scene; 
	GridPane root; 
	
	Label folder; 
	ComboBoxWithSearchBar<String> folderList; 
	
	HBox aBtns; 
	Button move; 
	Button cancel; 
	//--------------------
	
	public static JSONObject prjConfig; 
	private static final Font labelsFont = MainGUI.labelsFont;
	public static boolean saveClicked = false; 
	
	private static int index; 
	
	public static void setIndex(int index) {
		MoveString.index = index; 
	}
	
	public void start(Stage primaryStage) {
		saveClicked = false; 
		
		root = new GridPane(); 
		root.setPadding(new Insets(10)); 
		root.setHgap(10); 
		root.setVgap(10);
		
		folder = new Label(get("gui.label.folder")); 
		folder.setFont(labelsFont); 
		
		folderList = new ComboBoxWithSearchBar<>();
		folderList.getItems().add(get("gui.text.unclassified")); 

		prjConfig = ProjectConfig.json();

		for (Object o : prjConfig.getJSONArray("Folders")) folderList.getItems().add((String) o);
		
		folderList.getSelectionModel().select(0); 
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		
		move = new Button(get("gui.label.move")); 
		move.setFont(labelsFont);
		move.setDefaultButton(true); 
		move.setOnAction(arg0 -> {
			saveClicked = true;

			prjConfig.getJSONArray("Class").put(index, prjConfig.getJSONArray("Folders").toList().indexOf(folderList.getSelectedItem()));
			ProjectConfig.write(prjConfig);

			primaryStage.close();
		});
		
		cancel = new Button(get("gui.label.cancel")); 
		cancel.setFont(labelsFont); 
		cancel.setCancelButton(true); 
		cancel.setOnAction(event -> primaryStage.close());
		
		aBtns.getChildren().addAll(cancel, move); 
		aBtns.setAlignment(Pos.CENTER_RIGHT); 

		root.add(folder, 0, 0); 
		root.add(folderList, 1, 0); 
		root.add(aBtns, 0, 1, 2, 1); 
		
		GridPane.setHgrow(folderList, Priority.ALWAYS); 
		GridPane.setHgrow(aBtns, Priority.ALWAYS);
		
		scene = new Scene(root, 350, 80);

		Main.setIcon(primaryStage);
		primaryStage.setScene(scene);
		primaryStage.setTitle(get("gui.main.movestring")); 
		primaryStage.initModality(Modality.APPLICATION_MODAL); 
		primaryStage.showAndWait(); 
	}
}
