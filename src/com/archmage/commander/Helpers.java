package com.archmage.commander;

import java.util.List;

public class Helpers {

	public static final Command	CMDLIST	= cmdlist();
	public static final Command	CMDHELP	= cmdhelp();
	public static final Command	CMDEXIT	= cmdexit();

	private static final Command cmdlist() {
		Command output = Command.make("cmdlist", () -> "Displays a list of all available commands.", "commands");

		return output.setAction((args) -> {
			List<Command> commands = output.getCommander().getActiveMenu().getCommands();
			output.output("Commands:");
			for(Command command : commands)
				output.output(command.getId() + " - " + command.getSummary());
			output.output("");
			return true;
		});
	}

	private static final Command cmdhelp() {
		Command output = Command.make("help", () -> "Provides help on specific commands.", "?");
		output.setUsage(() -> output.getId() + " <command>");

		return output.setAction((args) -> {
			// System.out.println(Arrays.toString(args) + args.length);
			if(args.length <= 1 && args[0].equals("")) {
				if(output.getCommander().getActiveMenu().getCommands().contains(CMDLIST)) {
					output.output("Type \"cmdlist\" for a list of valid commands.");
				}
				output.output("Type \"help <command>\" (i.e. \"help open\") for help on specific commands.");
				return true;
			}
			for(Command command : output.getCommander().getActiveMenu().getCommands()) {
				if(command.getDefaultMatcher(args[0], null)) {
					command.outputHelp();
					return true;
				}
			}
			output.output("No command \"" + args[0] + "\" exists.");
			return true;
		});
	}

	private static final Command cmdexit() {
		Command output = Command.make("exit", () -> "Exits the application.", "quit", "bye");
		return output.setAction((args) -> {
			output.getCommander().exit();
			return true;
		});
	}

}
