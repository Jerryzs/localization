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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import cc.jerry.local.Main;

public class LocaleUtils {
	
	public static Locale[] allLocales = null; 
	
	public LocaleUtils() {
		ArrayList<Locale> localeList = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/" + Main.langFolder + "/all_locales")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] arr = line.split("-"); 
				
				if (arr.length == 2) {
					localeList.add(new Locale.Builder().setLanguage(arr[0]).setRegion(arr[1]).build()); 
				}
				else if (arr.length == 3) {
					localeList.add(new Locale.Builder().setLanguage(arr[0]).setScript(arr[1]).setRegion(arr[2]).build()); 
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] Locale list file cannot be found: " + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		allLocales = localeList.toArray(new Locale[0]); 
	}
	
	public static String[] getAllLocaleNames(boolean withCountryName) {
		ArrayList<String> locales = new ArrayList<>();
		
		for (int i = 0; i < LocaleUtils.allLocales.length; i++) {
			Locale locale = LocaleUtils.allLocales[i]; 
			
			if (withCountryName) 
				locales.add(locale.getDisplayName()); 
			else 
				if (i == 0 || !LocaleUtils.allLocales[i-1].getLanguage().equals(locale.getLanguage())) 
					locales.add(locale.getDisplayLanguage()); 
		}
		
		for (int i = 0; i < AddLangs.size; i++) {
			if (withCountryName) 
				locales.add(AddLangs.langNamesFull[i]); 
			else 
				locales.add(AddLangs.langNames[i]); 
		}
		
		return locales.toArray(new String[0]); 
	}
	
	public static String[] getAllLocaleNames() {
		return getAllLocaleNames(ProjectConfig.json().getBoolean("Specify Country in Filenames")); 
	}
	
	public static Object nameToLocale(String localeDisplayName) {
		for (Locale locale : LocaleUtils.allLocales) {
			if (locale.getDisplayName().equals(localeDisplayName) || locale.getDisplayLanguage().equals(localeDisplayName)) {
				return locale; 
			}
		}
		
		for (int i = 0; i < AddLangs.size; i++) {
			String locale = AddLangs.langNamesFull[i]; 
			if (locale.contains(localeDisplayName))
				return i; 
		}
		
		return null; 
	}
}
