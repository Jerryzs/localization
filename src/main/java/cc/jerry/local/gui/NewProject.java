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

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.local.gui.popups.PopUpMessage;
import cc.jerry.local.main.Main;
import cc.jerry.local.utils.LocaleUtils;
import cc.jerry.local.utils.ProjectConfig;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;

import static cc.jerry.commons.util.Localization.get;

public class NewProject {
    //--------------------
    BorderPane root;
    GridPane center;
    Scene scene;

    Label projectName;
    TextField projectNameEntry;

    Label projectDir;
    Label projectDirEntry;
    Button projectDirBtn;

    Label setSourceLanguage;
    ComboBoxWithSearchBar<String> sourceLanguageSelections;

    Label specifyCountryInFilenames;
    HBox specifyCountryInFilenamesCont;
    ToggleGroup specifyCountryInFilenamesOptions;
    RadioButton specifyCountryInFilenamesYes;
    RadioButton specifyCountryInFilenamesNo;

    Label selectTargetLanguages;
    ListView<String> selectedLanguages;
    Button addTLanguage;
    Button removeSLanguage;
    ListView<String> allLanguages;

    HBox aBtns;
    Button save;
    Button cancel;
    //--------------------

    final Font labelsFont = MainGUI.labelsFont;
    String fileChosen = "";

    public static boolean closed = false;

    public void start(Stage primaryStage) {
        root = new BorderPane();

        center = new GridPane();
        center.setHgap(10);
        center.setVgap(10);
        center.setPadding(new Insets(10));

        projectName = new Label(get("gui.label.projectname"));
        projectName.setFont(labelsFont);

        projectNameEntry = new TextField();
        projectNameEntry.setOnKeyReleased(e -> {
            if (!projectNameEntry.getText().isEmpty())
                projectDirEntry.setText(fileChosen + projectNameEntry.getText() + ".jlcf");
            else if (fileChosen.isEmpty())
                projectDirEntry.setText("");
            else
                projectDirEntry.setText(fileChosen);
        });

        projectDir = new Label(get("gui.label.projectdirectory"));
        projectDir.setFont(labelsFont);

        projectDirEntry = new Label();
        projectDirEntry.setFont(labelsFont);

        projectDirBtn = new Button("...");
        projectDirBtn.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File selectedDirectory = dirChooser.showDialog(new Stage());
            System.out.println(selectedDirectory);
            if (selectedDirectory != null) {
                fileChosen = selectedDirectory.getAbsolutePath();
                if (!fileChosen.endsWith(File.separator))
                    fileChosen += File.separator;
                if (!projectNameEntry.getText().isEmpty())
                    projectDirEntry.setText(fileChosen + projectNameEntry.getText() + ".jlcf");
                else
                    projectDirEntry.setText(fileChosen);
            }
        });

        setSourceLanguage = new Label(get("gui.label.setsourcelanguage"));
        setSourceLanguage.setFont(labelsFont);

        sourceLanguageSelections = new ComboBoxWithSearchBar<>();
        for (String locale : LocaleUtils.getAllLocaleNames(true)) {
            sourceLanguageSelections.getItems().add(locale);
        }
        sourceLanguageSelections.getItems().sort(null);

        specifyCountryInFilenames = new Label(get("gui.label.specifycountryinfilenames"));
        specifyCountryInFilenames.setFont(labelsFont);

        specifyCountryInFilenamesCont = new HBox();
        specifyCountryInFilenamesCont.setSpacing(10);

        specifyCountryInFilenamesOptions = new ToggleGroup();

        specifyCountryInFilenamesYes = new RadioButton(get("gui.label.yes"));
        specifyCountryInFilenamesYes.setToggleGroup(specifyCountryInFilenamesOptions);
        specifyCountryInFilenamesYes.setSelected(true);
        specifyCountryInFilenamesYes.setOnAction(e -> {
            updateLanguages();
            sourceLanguageSelections.updateItems();
        });

        specifyCountryInFilenamesNo = new RadioButton(get("gui.label.no"));
        specifyCountryInFilenamesNo.setToggleGroup(specifyCountryInFilenamesOptions);
        specifyCountryInFilenamesNo.setOnAction(e -> {
            updateLanguages();
            sourceLanguageSelections.updateItems();
        });

        specifyCountryInFilenamesCont.getChildren().addAll(specifyCountryInFilenamesYes, specifyCountryInFilenamesNo);

        selectTargetLanguages = new Label(get("gui.label.selecttargetlanguages"));
        selectTargetLanguages.setFont(labelsFont);

        selectedLanguages = new ListView<>();

        addTLanguage = new Button("<-");
        addTLanguage.setOnAction(e -> {
            if (!allLanguages.getSelectionModel().isEmpty()) {
                selectedLanguages.getItems().add(allLanguages.getSelectionModel().getSelectedItem());
                allLanguages.getItems().remove(allLanguages.getSelectionModel().getSelectedIndex());
                if (!(allLanguages.getSelectionModel().isSelected(0) || allLanguages.getSelectionModel().isSelected(allLanguages.getItems().size() - 1)))
                    allLanguages.getSelectionModel().select(allLanguages.getSelectionModel().getSelectedIndex() + 1);
            }
        });

        removeSLanguage = new Button("->");
        removeSLanguage.setOnAction(e -> {
            if (!selectedLanguages.getSelectionModel().isEmpty()) {
                allLanguages.getItems().add(selectedLanguages.getSelectionModel().getSelectedItem());
                allLanguages.getItems().sort(null);
                selectedLanguages.getItems().remove(selectedLanguages.getSelectionModel().getSelectedIndex());
            }
        });

        allLanguages = new ListView<>();
        for (String locale : LocaleUtils.getAllLocaleNames(true)) {
            allLanguages.getItems().add(locale);
        }
        allLanguages.getItems().sort(null);

        save = new Button(get("gui.label.save"));
        save.setFont(labelsFont);
        save.setDefaultButton(true);
        save.setOnAction(e -> save(primaryStage));

        cancel = new Button(get("gui.label.cancel"));
        cancel.setFont(labelsFont);
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> primaryStage.close());

        aBtns = new HBox();
        aBtns.getChildren().addAll(cancel, save);
        aBtns.setSpacing(10);
        aBtns.setPadding(new Insets(10));
        aBtns.setAlignment(Pos.CENTER);

        center.add(projectName, 0, 0);
        center.add(projectNameEntry, 1, 0, 4, 1);
        center.add(projectDir, 0, 1);
        center.add(projectDirEntry, 1, 1, 3, 1);
        center.add(projectDirBtn, 4, 1);
        center.add(setSourceLanguage, 0, 2);
        center.add(sourceLanguageSelections, 1, 2, 4, 1);
        center.add(specifyCountryInFilenames, 0, 3);
        center.add(specifyCountryInFilenamesCont, 1, 3);
        center.addRow(4, selectTargetLanguages);
        center.add(selectedLanguages, 0, 5, 2, 3);
        center.add(addTLanguage, 2, 5);
        center.add(removeSLanguage, 2, 6);
        center.add(allLanguages, 3, 5, 2, 3);

        //center.setGridLinesVisible(true);

        GridPane.setHgrow(selectedLanguages, Priority.ALWAYS);
        GridPane.setHgrow(projectDirEntry, Priority.ALWAYS);
        GridPane.setHgrow(projectDirBtn, Priority.ALWAYS);
        GridPane.setHgrow(sourceLanguageSelections, Priority.ALWAYS);
        GridPane.setHgrow(allLanguages, Priority.ALWAYS);
        GridPane.setHgrow(specifyCountryInFilenamesCont, Priority.SOMETIMES);
        GridPane.setHgrow(addTLanguage, Priority.NEVER);
        GridPane.setHgrow(removeSLanguage, Priority.NEVER);
        GridPane.setHalignment(projectDirBtn, HPos.RIGHT);

        root.setCenter(center);
        root.setBottom(aBtns);

        scene = new Scene(root, 650, 350);

        Main.setIcon(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle(get("gui.main.newproject"));
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.showAndWait();
    }

    private void updateLanguages() {
        selectedLanguages.getItems().clear();

        allLanguages.getItems().clear();
        for (String locale : LocaleUtils.getAllLocaleNames(specifyCountryInFilenamesYes.isSelected())) {
            allLanguages.getItems().add(locale);
        }
        allLanguages.getItems().sort(null);

        sourceLanguageSelections.getItems().clear();
        for (String locale : LocaleUtils.getAllLocaleNames(specifyCountryInFilenamesYes.isSelected())) {
            sourceLanguageSelections.getItems().add(locale);
        }
        sourceLanguageSelections.getItems().sort(null);
    }

    private void save(Stage primaryStage) {
        if (!projectNameEntry.getText().isEmpty() && fileChosen != null && !fileChosen.isEmpty() && !sourceLanguageSelections.getSelectedItem().isEmpty() && !selectedLanguages.getItems().isEmpty()) {
            JSONObject prjConfig = new JSONObject();

            prjConfig.put("Project Name", projectNameEntry.getText());
            prjConfig.put("Directory", fileChosen);
            prjConfig.put("Src Language", sourceLanguageSelections.getSelectedItem());
            prjConfig.put("Folders", new JSONArray());
            prjConfig.put("Keys", new JSONArray());
            prjConfig.put("Strings", new JSONArray());
            prjConfig.put("Class", new JSONArray());

            JSONObject tarLangList = new JSONObject();

            String[] langs = selectedLanguages.getItems().toArray(new String[0]);
            Arrays.sort(langs);

            for (String langDisName : langs) {
                tarLangList.put(langDisName, new JSONArray());
            }

            prjConfig.put("Target Languages", tarLangList);

            JSONArray twoEArrays = new JSONArray();
            twoEArrays.put(new JSONArray());
            twoEArrays.put(new JSONArray());

            prjConfig.put("Custom File Formats", (new JSONArray()).put(new JSONArray()).put(new JSONArray()));

            prjConfig.put("Specify Country in Filenames", specifyCountryInFilenamesYes.isSelected());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChosen + File.separator + projectNameEntry.getText() + ".jlcf"))) {
                writer.write(prjConfig.toString());
            } catch (FileNotFoundException ex) {
                PopUpMessage.message(get("gui.message.filenotcreatederror"));
                (new PopUpMessage()).start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProjectConfig.file(new File(fileChosen + File.separator + projectNameEntry.getText() + ".jlcf"));
            closed = true;

            System.out.println("[INFO] Successfully created a new project");
            primaryStage.close();
        }
    }
}
