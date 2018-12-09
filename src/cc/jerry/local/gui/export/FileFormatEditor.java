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

import java.util.ArrayList;

import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.utils.CustomizedFileFormats;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage; 

public class FileFormatEditor extends Application {
	
	//--------------------
	Scene scene; 
	BorderPane root; 
	GridPane center; 
	
	Label syntax; 
	TextField syntaxEntry; 
	
	Label name; 
	TextField nameEntry; 
	
	Label funcButtons; 
	FlowPane funcButtonsContainer; 
	Button f_curly_bracket_left; 
	Button f_curly_bracket_right; 
	Button f_square_bracket_left; 
	Button f_square_bracket_right; 
	Button f_repeat_begin; 
	Button f_repeat_end; 
	Button f_source_language; 
	Button f_target_language; 
	Button f_original_string; 
	Button f_translated_string; 
	Button f_key; 
	
	Label options; 
	CheckBox combineAllLangs; 
	
	HBox aBtns; 
	Button cancel; 
	Button save; 
	//--------------------
	
	
	private static Font labelsFont = MainGUI.labelsFont; 
	private static boolean editMode; 
	private static String _name; 
	private static String _syntax; 
	private static int _index; 
	private static ArrayList<Integer> reIndexes = new ArrayList<Integer>(); 
	public static boolean saveClicked = false; 
	
	public void start(Stage primaryStage, String name, String syntax, int index) {
		FileFormatEditor.editMode = true; 
		FileFormatEditor._syntax = syntax; 
		FileFormatEditor._name = name; 
		FileFormatEditor._index = index; 
		start(primaryStage); 
	}
	
	public void start(Stage primaryStage) {
		root = new BorderPane(); 
		root.setPadding(new Insets(10)); 
		
		center = new GridPane(); 
		center.setVgap(10); 
		center.setHgap(10); 
		root.setCenter(center); 
		
		syntax = new Label(get("gui.label.syntax")); 
		syntax.setFont(labelsFont); 
		
		syntaxEntry = new TextField(); 
		if (editMode) syntaxEntry.setText(_syntax); 
		syntaxEntry.setFont(labelsFont); 
		
		name = new Label(get("gui.label.name")); 
		name.setFont(labelsFont); 
		
		nameEntry = new TextField(); 
		if (editMode) nameEntry.setText(_name); 
		else nameEntry.setText(get("gui.text.untitled"));
		nameEntry.setFont(labelsFont); 
		
		funcButtons = new Label(get("gui.label.funcbuttons")); 
		funcButtons.setFont(labelsFont); 
		
		funcButtonsContainer = new FlowPane(); 
		funcButtonsContainer.setHgap(10); 
		funcButtonsContainer.setVgap(10); 
		
		f_curly_bracket_left = new Button("{"); 
		f_curly_bracket_left.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("{");
			}
			
		});
		
		f_curly_bracket_right = new Button("}"); 
		f_curly_bracket_right.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("}");
			}
			
		});
		
		f_square_bracket_left = new Button("["); 
		f_square_bracket_left.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("[");
			}
			
		});
		
		f_square_bracket_right = new Button("]"); 
		f_square_bracket_right.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("]");
			}
			
		});
		
		f_repeat_begin = new Button(get("gui.functions.repeat.begin")); 
		f_repeat_begin.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("$(repeat_begin[var])");
			}
			
		});
		
		f_repeat_end = new Button(get("gui.functions.repeat.end")); 
		f_repeat_end.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("$(repeat_end[var])");
			}
			
		});
		
		f_source_language = new Button(get("gui.functions.sourcelang")); 
		f_source_language.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("$(source_language)");
			}
			
		});
		
		f_target_language = new Button(get("gui.functions.targetlang")); 
		f_target_language.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				syntaxEntry.appendText("$(target_language)");
			}
			
		});
		
		options = new Label(get("gui.label.options")); 
		options.setFont(labelsFont); 
		
		combineAllLangs = new CheckBox(get("gui.label.combinealllangs")); 
		combineAllLangs.setFont(labelsFont); 
		
		funcButtonsContainer.getChildren().add(f_curly_bracket_left); 
		funcButtonsContainer.getChildren().add(f_curly_bracket_right); 
		funcButtonsContainer.getChildren().add(f_square_bracket_left); 
		funcButtonsContainer.getChildren().add(f_square_bracket_right); 
		funcButtonsContainer.getChildren().add(f_repeat_begin); 
		funcButtonsContainer.getChildren().add(f_repeat_end); 
		funcButtonsContainer.getChildren().add(f_source_language); 
		funcButtonsContainer.getChildren().add(f_target_language); 
		
		aBtns = new HBox(); 
		aBtns.setSpacing(10); 
		aBtns.setAlignment(Pos.CENTER);
		root.setBottom(aBtns); 
		
		cancel = new Button(get("gui.label.close")); 
		cancel.setFont(labelsFont); 
		cancel.setCancelButton(true); 
		cancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				editMode = false; 
				primaryStage.close(); 
			}
			
		});
		
		save = new Button(get("gui.label.save")); 
		save.setFont(labelsFont);
		save.setDefaultButton(true); 
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				save(); 
			}
			
		});
		
		aBtns.getChildren().addAll(cancel, save); 
		
		center.add(syntax, 0, 0);
		center.add(syntaxEntry, 0, 1, 3, 1); 
		center.add(funcButtons, 0, 2); 
		center.add(funcButtonsContainer, 0, 3, 3, 1); 
		center.add(name, 0, 4); 
		center.add(nameEntry, 0, 5, 2, 1); 
		center.add(combineAllLangs, 0, 6); 
		
		GridPane.setHgrow(syntaxEntry, Priority.ALWAYS);
		GridPane.setHgrow(syntax, Priority.ALWAYS);
		GridPane.setHgrow(funcButtonsContainer, Priority.ALWAYS); 
		
		scene = new Scene(root, 650, 350); 
		primaryStage.setScene(scene); 
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/appicon.png")));
		primaryStage.setTitle(get("gui.main.customizedfileformat")); 
		
		scene.getAccelerators().put(KeyCombination.keyCombination("SHORTCUT+S"), new Runnable() {

			@Override
			public void run() {
				save.fire(); 
			}
			
		}); 
		
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.showAndWait(); 
	}
	
	private void save() {
		if (editMode) {
			CustomizedFileFormats.save(syntaxEntry.getText(), nameEntry.getText(), _index);
		}
		else {
			CustomizedFileFormats.save(syntaxEntry.getText(), nameEntry.getText());
		}
		saveClicked = true; 
	}
}
