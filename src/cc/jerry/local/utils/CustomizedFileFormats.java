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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomizedFileFormats {
	
	private static JSONArray customFileFormats; 
	
	public CustomizedFileFormats() {
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
	}
	
	public static JSONArray json() {
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
		return customFileFormats; 
	}
	
	public static boolean isEmpty() {
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
		return customFileFormats.getJSONArray(0).length() == 0; 
	}
	
	public static String name(int index) {
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
		return customFileFormats.getJSONArray(0).getString(index); 
	}
	
	public static String syntax(int index) {
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
		return customFileFormats.getJSONArray(1).getString(index); 
	}
	
	public static void delete(int index) {
		String jsonText = null; 
		JSONObject prjConfig; 
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ProjectConfig.json().getString("Directory") + File.separator + ProjectConfig.json().getString("Project Name") + ".jlcf")); 
			
			StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = reader.read()) != -1) {
	            sb.append((char) cp);
	        }
	        
	        reader.close(); 
	        
	        jsonText = sb.toString(); 
		} catch (IOException ex) {
			
		}
		
		prjConfig = new JSONObject(jsonText); 
		
		prjConfig.getJSONArray("Custom File Formats").getJSONArray(0).remove(index); 
		prjConfig.getJSONArray("Custom File Formats").getJSONArray(1).remove(index); 
		
		ProjectConfig.write(prjConfig); 
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
	}
	
	public static void save(String syntax, String name, int index) {
		String jsonText = null; 
		JSONObject prjConfig; 
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ProjectConfig.json().getString("Directory") + File.separator + ProjectConfig.json().getString("Project Name") + ".jlcf")); 
			
			StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = reader.read()) != -1) {
	            sb.append((char) cp);
	        }
	        
	        reader.close(); 
	        
	        jsonText = sb.toString(); 
		} catch (IOException ex) {
			
		}
		
		prjConfig = new JSONObject(jsonText); 
		
		prjConfig.getJSONArray("Custom File Formats").getJSONArray(0).put(index, name); 
		prjConfig.getJSONArray("Custom File Formats").getJSONArray(1).put(index, syntax); 
		
		ProjectConfig.write(prjConfig); 
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
	}
	
	public static void save(String syntax, String name) {
		String jsonText = null; 
		JSONObject prjConfig; 
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ProjectConfig.json().getString("Directory") + File.separator + ProjectConfig.json().getString("Project Name") + ".jlcf")); 
			
			StringBuilder sb = new StringBuilder();
	        int cp;
	        while ((cp = reader.read()) != -1) {
	            sb.append((char) cp);
	        }
	        
	        reader.close(); 
	        
	        jsonText = sb.toString(); 
		} catch (IOException ex) {
			
		}
		
		prjConfig = new JSONObject(jsonText); 
		
		prjConfig.getJSONArray("Custom File Formats").getJSONArray(0).put(name); 
		prjConfig.getJSONArray("Custom File Formats").getJSONArray(1).put(syntax); 
		
		ProjectConfig.write(prjConfig);
		customFileFormats = ProjectConfig.json().getJSONArray("Custom File Formats"); 
	}
	
	public static String parse(String syntax) {
		return parse(syntax, -1, -1); 
	}
	
	public static String parse(String syntax, int loop, int loopLength) {
		StringBuilder result = new StringBuilder(); 
		StringBuilder innerLoop = new StringBuilder(); 
		boolean inLoop = false; 
		String identifier = null; 
		String type = null; 
		
		for (int i = 0; i < syntax.length(); i++) {
			Character c = syntax.charAt(i); 
			System.out.print(c); 
			if (c == '$') {
				StringBuilder sb = new StringBuilder(); 
				StringBuilder osb = new StringBuilder(); 
				StringBuilder isb = new StringBuilder();
				osb.append("$(");  
				
				int j = 2; 
				while (syntax.charAt(i + j) != ')') {
//					System.out.println(syntax.charAt(i + j)); 
					if (syntax.charAt(i + j) == '[') {
						osb.append(syntax.charAt(i + j)); 
						j++; 
						while (true) {
							if (syntax.charAt(i + j) != ']') {
								isb.append(syntax.charAt(i + j)); 
								osb.append(syntax.charAt(i + j)); 
	//							System.out.println("isb+: " + isb.toString() ); 
								j++; 
							}
							else if (syntax.charAt(i + j) == '\\') {
								osb.append(syntax.charAt(i + j)); 
								j++; 
								isb.append(syntax.charAt(i + j)); 
								osb.append(syntax.charAt(i + j)); 
								j++; 
							}
							else break; 
						}
						osb.append(syntax.charAt(i + j)); 
						j++; 
						continue; 
					}
					sb.append(syntax.charAt(i + j)); 
					osb.append(syntax.charAt(i + j)); 
					j++; 
				}
				osb.append(syntax.charAt(i + j)); 
				
				if (!inLoop) {
					if (loop != -1 && loopLength != -1) {
						if (sb.toString().contains("exclude")) {
							String when = isb.toString().substring(0, isb.toString().indexOf(',')); 
							String content = isb.toString().substring(isb.toString().indexOf(',')); 
							content = content.substring(content.indexOf("\"") + 1, content.lastIndexOf("\"")); 
							if (when.equals("first") && loop > 0) {
								result.append(content);  
							}
							if (when.equals("last") && loop < loopLength - 1) {
								result.append(content); 
							}
							if (when.equals("first+last") && loop > 0 && loop < loopLength - 1) {
								result.append(content); 
							}
						}
					}
					if (isb.length() != 0) {
						if (sb.toString().contains("repeat_begin")) {
							identifier = isb.toString().split(",")[0]; 
							type = isb.toString().split(",")[1].replaceAll(" ", ""); 
						}
						if (sb.toString().contains("source_language")) {
							
						}
					}
					if (sb.toString().contains("repeat_begin")) {
						inLoop = true; 
					}
					if (sb.toString().equals("source_language")) {
						
					}
				}
				else {
//					System.out.println("sb: " + sb.toString());
//					System.out.println("isb : " + isb.toString());
					if (sb.toString().contains("repeat_end") && isb.toString().contains(identifier) && isb.toString().contains(type)) {
						inLoop = false; 
						if (type.toString().equals("target_languages")) result.append(parseLoop(innerLoop.toString(), Loop.TargetLanguages)); 
						if (type.toString().equals("translations")) result.append(parseLoop(innerLoop.toString(), Loop.KeyAndTranslations)); 
						innerLoop = new StringBuilder(); 
					}
					else {
						innerLoop.append(osb); 
					}
				}
				i += j; 
			}
			else {
				if (inLoop) innerLoop.append(c); 
				else result.append(c); 
			}
		}
		
		return result.toString(); 
	}
	
	public static String parseLoop(String loop, Loop type) {
		StringBuilder result = new StringBuilder(); 
		
		switch (type) {
		case TargetLanguages: 
			for (int i = 0; i < ProjectConfig.json().getJSONObject("Target Languages").length(); i++) {
				result.append(parse(loop, i, ProjectConfig.json().getJSONObject("Target Languages").length())); 
			}
			break; 
		case KeyAndTranslations: 
			for (int i = 0; i < ProjectConfig.json().getJSONArray("Keys").length(); i++) {
				result.append(parse(loop, i, ProjectConfig.json().getJSONArray("Keys").length())); 
			}
			break; 
		default: 
			break; 
		}
		
		return result.toString(); 
	}
	
	public static enum Loop {
		TargetLanguages, 
		KeyAndTranslations
	}
}
