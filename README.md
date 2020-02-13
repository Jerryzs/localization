# Localization Strings Manager [![Build Status](https://travis-ci.org/Jerryzs/localization.svg?branch=master)](https://travis-ci.org/Jerryzs/localization) [![License](https://img.shields.io/github/license/Jerryzs/localization.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![Release](https://img.shields.io/github/release-pre/Jerryzs/localization.svg)](https://github.com/Jerryzs/localization/releases)

This project is licensed under Apache License 2.0, see [here](https://www.apache.org/licenses/LICENSE-2.0). <br>
[Go to the Official Website](http://jerry.cc/projects/page.php?id=2)

## Introduction

This application allows you to organize translation strings and export them as localization files for your applications. <br>

## Contribute

If you'd like to contribute to this project, please follow these requirements: 
* Use this Integrated Development Environment (IDE): **JetBrains IntelliJ IDEA** 
* Use this version of Java Development Kit (JDK): **OpenJDK 11**
  * This project also uses the **jpackage packaging tool**. Click [here](https://jdk.java.net/jpackage/) for more details. 

## Exporting Files

### Java Properties File
**Example:**
*en_US.properties* (filenames and extensions can all be changed)

```Properties
this.is.key.one=Translation
this.is.key.two=Translation
...
```
### JavaScript Object Notation (JSON)
**Example:**
*en_US.json* (filenames and extensions can all be changed)
```JSON
{
  "this.is.key.one": "Translation", 
  "this.is.key.two": "Translation"
}
```
### Customized File Format
*Comming Soon*