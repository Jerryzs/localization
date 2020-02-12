module cc.jerry.local {
	exports cc.jerry.local.gui.export;
	exports cc.jerry.local.gui;
	exports cc.jerry.local.gui.popups;
	exports cc.jerry.local.utils;
	exports cc.jerry.local.main;
	exports cc.jerry.local.gui.editor;

	requires Jerry.s.Java.Commons;
	requires centerdevice.nsmenufx;
	requires java.desktop;
	requires javafx.base;
	requires javafx.controls;
	requires transitive javafx.graphics;
	requires org.json;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
}