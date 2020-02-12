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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProjectConfig {
	
	private static JSONObject config; 
	private static File file; 
	private static BufferedReader reader; 
	
	public static void write(JSONObject json) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(json.getString("Directory") + File.separator + json.getString("Project Name") + ".jlcf"))){
			writer.write(json.toString()); 
			config = json; 
			System.out.println("[INFO] Successfully written the project config file! ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read() {
		if (file.exists()) { 
			System.out.println("[INFO] Reading project config file: " + file.getAbsolutePath());
			try {
				ProjectConfig.closeReader(); 
				reader = new BufferedReader(new FileReader(file)); 
				
				StringBuilder sb = new StringBuilder();
		        int cp;
		        while ((cp = reader.read()) != -1) {
		            sb.append((char) cp);
		        }
		        
		        config = new JSONObject(sb.toString()); 
		        
		        if (!config.has("Project Name") || !config.has("Directory")
		        		|| !config.has("Src Language") || !config.has("Keys")
		        		|| !config.has("Strings") || !config.has("Target Languages")
		        		|| config.getJSONArray("Strings").length() != config.getJSONArray("Keys").length())
		        	throw new JSONException("File is invalid"); 
				if (!config.has("Custom File Formats"))
					config.put("Custom File Formats", (new JSONArray()).put(new JSONArray()).put(new JSONArray())); 
				if (!config.has("Specify Country in Filenames"))
					config.put("Specify Country in Filenames", true); 
				if (!config.has("Folders"))
					config.put("Folders", new JSONArray()); 
				
				if (!config.has("Class")) {
					JSONArray arr = new JSONArray(); 
					for (int i = 0; i < config.getJSONArray("Strings").length(); i++)
						arr.put(-1); 
					config.put("Class", arr); 
				}
				
				if (!config.getString("Project Name").equals(FilenameUtils.removeExtension(file.getName())))
					config.put("Project Name", FilenameUtils.removeExtension(file.getName())); 
				String parentPath = file.getParent().endsWith(File.separator) ? file.getParent() : file.getParent() + File.separator; 
				if (!config.getString("Directory").equals(parentPath))
					config.put("Directory", parentPath); 
				
				ProjectConfig.write(config); 
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (JSONException je) {
				file = null; 
				System.out.println("[ERROR] Project config file is invalid");
			}
		}
		
	}
	
	public static void closeReader() {
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
	}
	
	public static JSONObject json() {
		return config; 
	}
	
	public static File file() {
		return file; 
	}
	
	public static void file(File file) {
		ProjectConfig.file = file; 
	}
}
