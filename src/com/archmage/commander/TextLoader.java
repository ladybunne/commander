package com.archmage.commander;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class TextLoader {

	private ObservableMap<String, String> text;

	public TextLoader() {
		text = FXCollections.observableHashMap();
	}

	public TextLoader(File file) {
		text = FXCollections.observableHashMap();
		load(file);
	}

	public void load(File file) {
		if(file == null) return;

		JSONObject object = null;
		try {
			object = (JSONObject)JSONValue.parse(new FileReader(file));
		}
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}

		JSONObject textObject = (JSONObject)object.get("text");
		for(Object entry : textObject.keySet()) {
			text.put((String)entry, (String)textObject.get(entry));
		}

	}

	public String get(String label) {
		return text.containsKey(label) ? text.get(label) : null;
	}

	public void set(String label, String text) {
		this.text.put(label, text);
	}
}
