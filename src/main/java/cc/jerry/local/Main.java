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

package cc.jerry.local;

import java.io.*;
import java.util.Locale;

import javax.swing.ImageIcon;

import cc.jerry.commons.util.Localization;
import cc.jerry.commons.util.OS;
import cc.jerry.commons.util.OS.SystemType;
import cc.jerry.local.gui.MainGUI;
import cc.jerry.local.utils.AppConfig;
import cc.jerry.local.utils.LocaleUtils;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

public class Main extends Application {
	public static final String langFolder = "langs";
	public static final String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "Jerryzs" + File.separator + "Localization Strings Manager";
	public static final File icon = new File(tempDir + File.separator + "icon.png");
	
	@SuppressWarnings("deprecation")
	public Main() {
		icon.deleteOnExit();

		try {
			FileUtils.copyInputStreamToFile(Main.class.getResourceAsStream("/icon.png"), icon);
		} catch (IOException e) {
			System.out.println("[WARNING] Failed to fetch application icon. ");
		}

		if (OS.os() == SystemType.MACOS) {
			try {
			    String className = "com.apple.eawt.Application";
			    Class<?> cls = Class.forName(className);
			    
			    Object application = cls.newInstance().getClass().getMethod("getApplication")
			        .invoke(null);
			    
			    application.getClass().getMethod("setDockIconImage", java.awt.Image.class)
			        .invoke(application, (new ImageIcon(icon.toURL()).getImage()));
			} catch (Exception e) {
				System.out.println("[WARNING] Exception caught: " + e.toString());
			}
		}
		
		new AppConfig(); 
		new LocaleUtils();

		new File(tempDir + File.separator + langFolder).deleteOnExit();

		for (Locale locale : LocaleUtils.allLocales) {
			String langTag = locale.getLanguage() + "-" + locale.getCountry();
			String fileSuffix = ".lang";
			File target = new File(tempDir + File.separator + langFolder + File.separator + langTag + fileSuffix);
			target.deleteOnExit();

			try (InputStream in = Main.class.getResourceAsStream("/" + langFolder + "/" + langTag + fileSuffix)) {
				if (in != null)
					FileUtils.copyInputStreamToFile(in, target);
			} catch (IOException e) {
				System.out.println("[WARNING] Exception while caching language files. ");
			}
		}
		
//		Localization.setAllLocales(LocaleUtils.allLocales);

		if (AppConfig.config().getProperty("language").equals("System Default"))
			new Localization(tempDir + File.separator + langFolder, "en-US", Locale.getDefault().toLanguageTag(), ".lang");
		else {
			new Localization(tempDir + File.separator + langFolder, "en-US", AppConfig.config().getProperty("language"), ".lang");
		}
	}

	public static void setIcon(Stage primaryStage) {
		try (InputStream in = new FileInputStream(Main.icon)){
			primaryStage.getIcons().add(new Image(in));
		} catch (FileNotFoundException e) {
			System.out.println("[WARNING] App icon cannot be loaded: " + e.toString());
		} catch (IOException e) {
			System.out.println("[WARNING] Resource not closed: " + e.toString());
		}
	}
	
	public void start(Stage primaryStage) {
		(new MainGUI()).start(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args); 
	}
}
