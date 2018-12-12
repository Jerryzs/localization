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

import java.util.ArrayList;
import java.util.Locale;

public class LocaleUtils {
	public static String[] getAllLocaleNames(boolean withCountryName) {
		ArrayList<String> locales = new ArrayList<String>(); 
		
		for (Locale locale : Locale.getAvailableLocales()) {
			if (withCountryName && !locale.getDisplayName().equals("") && locale.getDisplayName().contains("("))
				locales.add(locale.getDisplayName()); 
			if (!withCountryName && !locale.getDisplayName().equals("") && !locale.getDisplayName().contains("("))
				locales.add(locale.getDisplayName()); 
		}
		
		return locales.toArray(new String[0]); 
	}
	
	public static String[] getAllLocaleNames() {
		return getAllLocaleNames(ProjectConfig.json().getBoolean("Specify Country in Filenames")); 
	}
	
	public static Locale nameToLocale(String localeDisplayName) {
		for (Locale locale : Locale.getAvailableLocales()) {
			if (locale.getDisplayName().contains(localeDisplayName)) {
				return locale; 
			}
		}
		
		return null; 
	}
}
