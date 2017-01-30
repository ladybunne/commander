package com.archmage.commander;

import java.io.File;

public class CommanderTest {

	public static void main(String[] argv) {
		TextLoader loader = new TextLoader();
		loader.load(new File("teststrings.json"));

		Commander commander = Commander.make();
		// commander.setInputFromExternal(true);

		Command unlock = Command.make("unlock", () -> "Unlocks the exit.", "?");
		Command exit = Command.make("exit", () -> "Exits the program.", "quit", "bye");
		Command test = Command.make("test", () -> "Tests something.", "testing");
		Command number = Command.make("number", () -> "Tests custom matchers.");

		unlock.setAction((args) -> {
			if(unlock.getCommander().getActiveMenu().getCommands().contains(exit)) {
				unlock.output("The exit is already unlocked!");
				return true;
			}
			unlock.output(loader.get("unlock"));
			unlock.output("You may now exit.");
			unlock.getCommander().getActiveMenu().addCommand(exit);
			return true;
		});

		exit.setAction((args) -> {
			exit.output(loader.get("exit"));
			exit.getCommander().exit();
			return true;
		});

		test.setAction((args) -> {
			test.getCommander().setActiveMenu("second");
			return true;
		});

		number.setMatcher((cmd, args) -> {
			try {
				Integer.parseInt(cmd);
				return true;
			}
			catch(NumberFormatException ex) {
				return false;
			}
		});
		number.setAction((args) -> {
			number.output("You entered a number! Great job.");
			return true;
		});
		number.setLabel(() -> "any number");

		Menu welcomeMenu = Menu.make("welcome", test, exit);
		Menu secondMenu = Menu.make("second", unlock, number, Helpers.CMDLIST, Helpers.CMDHELP);

		commander.addMenu(welcomeMenu, secondMenu);

		// int time = (int)System.currentTimeMillis() / 1000 + 1;

		while(commander.run()) {

			// if(time < (int)System.currentTimeMillis() / 1000) {
			// String input = commander.getActiveMenu().getCommands().get(0).getLabel();
			// commander.output(input);
			// commander.input(input);
			// time++;
			// }
		}
	}
}
