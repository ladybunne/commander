package com.archmage.commander;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Commander {

	private final ArrayList<Menu>	MENUS				= new ArrayList<>();
	private Menu						welcomeMenu		= null;
	private Menu						activeMenu		= null;
	private ArrayList<Menu>			menuMovement	= new ArrayList<>();

	public static final String	NO_INPUT				= "";
	private String					input					= NO_INPUT;
	private boolean				inputFromExternal	= false;

	private ObservableList<String>	output				= FXCollections.observableArrayList(new ArrayList<>());
	private boolean						outputToConsole	= true;

	private boolean readyToExit = false;

	private Commander() {}

	public static Commander make() {
		return new Commander();
	}

	public static Commander make(Menu... menus) {
		Commander commander = new Commander();
		commander.addMenu(menus);
		return commander;
	}

	public void addMenu(Menu... menus) {
		for(Menu menu : menus) {
			MENUS.add(menu);
			menu.setCommander(this);
		}
		if(welcomeMenu == null) {
			welcomeMenu = (!MENUS.isEmpty() ? MENUS.get(0) : null);
		}
		if(activeMenu == null) {
			activeMenu = (!MENUS.isEmpty() ? MENUS.get(0) : null);
		}

	}

	public void setActiveMenu(int menuId) {
		MENUS.forEach((menu) -> {
			if(menuId == menu.ID) {
				setActiveMenu(menu);
			}
		});
	}

	public void setActiveMenu(String label) {
		MENUS.forEach((menu) -> {
			if(label.equals(menu.getLabel())) {
				setActiveMenu(menu);
			}
		});
	}

	private void setActiveMenu(Menu menu) {
		// check for back-and-forth movement between menus
		if(menuMovement.size() > 1 && menu == menuMovement.get(menuMovement.size() - 2)) {
			menuMovement.remove(menuMovement.size() - 1);
		}
		else {
			menuMovement.add(menu);
		}
		activeMenu = menu;
	}

	public Menu getMenu(String label) {
		for(Menu menu : MENUS) {
			if(menu.getLabel().equals(label)) return menu;
		}
		return null;
	}

	public Menu getActiveMenu() {
		return activeMenu;
	}

	/**
	 * Main logic of Commander. Returns a boolean that allows abortion.
	 * 
	 * @return false if commander is ready to exit; true otherwise
	 */
	public boolean run() {
		if(activeMenu == null) return false;
		else {
			if(menuMovement.isEmpty()) {
				menuMovement.add(activeMenu);
			}
			activeMenu.run();
		}
		return !readyToExit;
	}

	public void setOutputToConsole(boolean outputToConsole) {
		this.outputToConsole = outputToConsole;
	}

	public void input(String input) {
		this.input = input;
	}

	public void output(String output) {
		output(output, true);
	}

	public void output(String output, boolean newline) {
		if(newline == true) {
			output += "\n";
		}
		this.output.add(output);
		if(outputToConsole) {
			System.out.print(output);
		}
	}

	public void setInputFromExternal(boolean inputFromExternal) {
		this.inputFromExternal = inputFromExternal;
	}

	public boolean getInputFromExternal() {
		return inputFromExternal;
	}

	public String getInput() {
		return input;
	}

	public void previousMenu() {
		if(menuMovement.size() > 1) {
			activeMenu = menuMovement.get(menuMovement.size() - 2);
			menuMovement.remove(menuMovement.size() - 1);
		}
	}

	public void restart() {
		menuMovement.clear();
		setActiveMenu(welcomeMenu.ID);
	}

	public void exit() {
		readyToExit = true;
	}

	public ObservableList<String> getOutput() {
		return output;
	}
}
