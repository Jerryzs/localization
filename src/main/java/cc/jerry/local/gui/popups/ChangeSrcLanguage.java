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

package cc.jerry.local.gui.popups;

import cc.jerry.commons.javafx.ComboBoxWithSearchBar;
import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.Main;
import cc.jerry.local.utils.LocaleUtils;
import cc.jerry.local.utils.ProjectConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;

import static cc.jerry.commons.util.Localization.get;

public class ChangeSrcLanguage {
    //--------------------
    Scene scene;
    GridPane root;

    ComboBoxWithSearchBar<String> languages;

    HBox aBtns;
    Button cancel;
    Button change;
    //--------------------

    final Font labelsFont = MainGUI.labelsFont;
    public static boolean closed = false;

    public static boolean success;

    public void start(Stage primaryStage) {
        success = false;

        root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        languages = new ComboBoxWithSearchBar<>();
        for (String lang : LocaleUtils.getAllLocaleNames())
            languages.getItems().add(lang);
        languages.getItems().sort(null);
        languages.getItems().remove(0);
        languages.getSelectionModel().select(ProjectConfig.json().getString("Src Language"));

        aBtns = new HBox();
        aBtns.setSpacing(10);

        cancel = new Button(get("gui.label.cancel"));
        cancel.setFont(labelsFont);
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> primaryStage.close());

        change = new Button(get("gui.label.change"));
        change.setFont(labelsFont);
        change.setDefaultButton(true);
        change.setOnAction(e -> {
            if (!languages.getSelectedItem().isEmpty()) {
                JSONObject prjConfig = ProjectConfig.json();
                prjConfig.put("Src Language", languages.getSelectedItem());

                ProjectConfig.write(prjConfig);
                closed = true;
                primaryStage.close();
            }
        });

        aBtns.getChildren().addAll(cancel, change);
        aBtns.setAlignment(Pos.CENTER_RIGHT);

        root.add(languages, 0, 0, 2, 1);
        root.add(aBtns, 1, 1);

        GridPane.setHgrow(aBtns, Priority.ALWAYS);

        scene = new Scene(root, 350, 85);

        Main.setIcon(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle(get("gui.main.selectlanguage"));
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.showAndWait();
    }
}
