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

import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.Main;
import javafx.geometry.HPos;
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

import static cc.jerry.commons.util.Localization.get;

public class RemoveConfirmation {
    //--------------------
    Scene scene;
    GridPane root;

    Label message;

    HBox aBtns;
    Button no;
    Button yes;
    //--------------------

    static final Font labelsFont = MainGUI.labelsFont;
    public static boolean removeConfirmed;

    public void start(Stage primaryStage) {
        root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        message = new Label(get("gui.message.removeconfirmationmessage"));
        message.setFont(labelsFont);

        aBtns = new HBox();
        aBtns.setSpacing(10);

        no = new Button(get("gui.label.no"));
        no.setFont(labelsFont);
        no.setDefaultButton(true);
        no.setCancelButton(true);
        no.setOnAction(e -> primaryStage.close());

        yes = new Button(get("gui.label.yes"));
        yes.setFont(labelsFont);
        yes.setOnAction(e -> {
            removeConfirmed = true;
            primaryStage.close();
        });

        aBtns.getChildren().addAll(no, yes);
        aBtns.setAlignment(Pos.CENTER_RIGHT);

        root.add(message, 0, 0, 2, 1);
        root.add(aBtns, 1, 1);

        GridPane.setHalignment(aBtns, HPos.RIGHT);
        GridPane.setHgrow(message, Priority.ALWAYS);
        GridPane.setHgrow(aBtns, Priority.ALWAYS);

        scene = new Scene(root, 350, 75);

        Main.setIcon(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle(get("gui.main.removeconfirmation"));
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.showAndWait();
    }
}
