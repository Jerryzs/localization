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

package cc.jerry.local.main;

import java.util.Locale;

import javax.swing.ImageIcon;

import cc.jerry.commons.util.Localization;
import cc.jerry.commons.util.OS;
import cc.jerry.commons.util.OS.SystemType;
import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.utils.AppConfig;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	public Main() {
		if (OS.os() == SystemType.Mac) {
			try {
			    String className = "com.apple.eawt.Application";
			    Class<?> cls = Class.forName(className);
			
			    // Replace: Application application = Application.getApplication();
			    Object application = cls.newInstance().getClass().getMethod("getApplication")
			        .invoke(null);
			
			    // Replace: application.setDockIconImage(image);
			    application.getClass().getMethod("setDockIconImage", java.awt.Image.class)
			        .invoke(application, (new ImageIcon(getClass().getResource("/main/resources/appicon.png")).getImage()));
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}

		new AppConfig(); 
		
		if (AppConfig.config().getProperty("language").equals("System Default")) 
			new Localization("/main/resources/langs/", "en-US", Locale.getDefault().toLanguageTag(), ".lang", Main.class);
		else 
			new Localization("/main/resources/langs/", "en-US", AppConfig.config().getProperty("language"), ".lang", Main.class);
	}
	
	public void start(Stage primaryStage) {
		(new MainGUI()).start(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args); 
	}
}
