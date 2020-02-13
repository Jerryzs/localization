# Changelog

## v1.0 Release

### New Features

- Added a progress bar on the overview window to show the translate progress of the selected target language. 
- Added the option to sort Strings inside renamable and removable folders. 

### Changes & Improvements

- Changed the layout of the translation editor window. 
- Migrated this project to the newer OpenJDK 11 as the software development kit and OpenJFX 11 for the graphical user interface. 
- Began to build this project with Gradle and package it with jpackage. 
- Java Runtime Environment will no longer be required to be pre-installed in order to run this application. 

### Fixes

- Fixed Strings displayed as Unicode on the translation editor window. 
- Fixed "Show Not Translated Strings First" checkbox on the editor window not functioning properly. 
- Fixed the possibility of choosing unsupported filename formats when exporting. 

## v0.6.2 Beta

## Fixes

- Fixed thread not interrupted after "Wait For Application to Initialize" window is closed. 
- Fixed project configuration file reader is not closed after application settings changed. 

## v0.6.1 Beta

## Changes & Improvements

- Significantly shortened the launch time. 
- Project configuration file will now be occupied by the application upon being launched. 

## v0.6 Beta

## New Features

- Added option to specify the regions of languages (e.g. "en_US, en_UK" or "en")
- Added more bugs. 

## Changes & Improvements

- Made the software compatible with older project configuration files. 
- Project name recorded in configuration file will be changed if filename changes. 
- Project directory recorded in configuration file will be changed if file is moved. 

## Fixes

- Fixed Issue #1: Only one file can be exported when exporting the project. 
- Fixed Issue #2: OutputStreamWriter is not closed. 
- Fixed Issue #3: Existing target language can still be added as new. 
- Fixed the wrong text on the close button of the editor window. 

## v0.5 Beta

### New Features

- Added more export options. 
- Added searchable combo boxes. 
- Added an app settings menu. 
- Added support for export to JSON files. 
- Added more bugs. 

### Changes & Improvements

- Made more basic functionalities available. 
- The project will automatically be opened after being created.
- Improved the Custom File Format parsing system (full system coming soon).
- Changed the logo for the application.

### Fixes

- Fixed the error of unable to launch the application on Windows.
- Fixed other minor problems.
