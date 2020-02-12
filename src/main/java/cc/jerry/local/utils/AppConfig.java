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

package cc.jerry.local.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class AppConfig {
	
	private static final Properties config = new Properties();
	private static File file; 
	
	public AppConfig() {
		String path = System.getProperty("user.home") + File.separator + ".Jerryzs Configs" + File.separator + "localization.cfg";
		
		file = new File(path); 
		
		if (file.exists()) {
			read(); 
			return; 
		}

		if (makeFile())
			System.out.println("[INFO] Config file created successfully");

		config.put("language", "System Default");
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
			config.store(writer, null); 
		} catch (FileNotFoundException e) {
			handleFileNotFound();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		read(); 
	}

	private static void handleFileNotFound() {
		System.out.println("[WARNING] Config file not found, attempting to create one... ");
		if (!makeFile()) System.err.println("[ERROR] Config file not found nor can be created. ");
		else System.out.println("[INFO] Config file created successfully");
	}
	
	public static void read() {
		try {
			config.load(new FileInputStream(file)); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void update(String key, String value) {
		config.setProperty(key, value); 
	}
	
	public static void write() {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
			config.store(writer, null); 
		} catch (FileNotFoundException e) {
			handleFileNotFound();
		} catch (IOException e1) {
			System.out.println("[ERROR] Exception caught: " + e1.toString());
		} finally {
			read(); 
		}
	}

	private static boolean makeFile() {
		try {
			if (file.getParentFile().mkdirs() && file.createNewFile()) {
				return true;
			}
		} catch (IOException e) {
			System.out.println("[WARNING] Cannot create new config file");
		}
		return false;
	}
	
	public static Properties config() {
		return config; 
	}
}
