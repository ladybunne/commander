package com.archmage.commander;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Supplier;

public class Menu {

	private Commander commander;

	public final int				ID;
	private static final int	START_ID					= 1;
	private static int			nextId					= START_ID;
	private String					label;
	private Supplier<String>	inputPrompt				= this::getDefaultInputPrompt;
	private String					INPUT_ERROR				= "Unrecognised command. Please try again.";
	private boolean				readyToReceiveInput	= false;

	private final ArrayList<Command> COMMANDS = new ArrayList<>();

	private static final Scanner SCANNER = new Scanner(System.in);

	private Menu(String label, Command... commands) {
		ID = nextId++;
		this.label = label;
		addCommand(commands);
	}

	public static Menu make() {
		return new Menu(null, (Command)null);
	}

	public static Menu make(Command... commands) {
		return new Menu(null, commands);
	}

	public static Menu make(String label, Command... commands) {
		return new Menu(label, commands);
	}

	public void addCommand(Command... commands) {
		addCommand(false, commands);
	}

	public void addCommand(boolean addAtStart, Command... commands) {
		for(Command command : commands) {
			if(!COMMANDS.contains(command)) {
				if(addAtStart) {
					COMMANDS.add(0, command);
				}
				else {
					COMMANDS.add(command);
				}
			}
		}
	}

	public void removeCommand(Command... commands) {
		for(Command command : commands) {
			if(COMMANDS.contains(command)) {
				COMMANDS.remove(command);
			}
		}
	}

	public final String getDefaultInputPrompt() {
		String output = "Enter a command (";
		for(Command command : COMMANDS) {
			output += command.getLabel() + ", ";
		}
		output = output.substring(0, output.length() - 2);
		output += ").\n" + "> ";
		return output;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void run() {
		if(!readyToReceiveInput) {
			output(inputPrompt.get(), false);
			readyToReceiveInput = true;
		}

		String input = null;
		if(commander.getInputFromExternal()) {
			if(!commander.getInput().equals(Commander.NO_INPUT)) {
				input = commander.getInput();
				commander.input(Commander.NO_INPUT);
			}
		}
		else {
			input = SCANNER.nextLine();
		}

		if(input == null || (input = input.trim()).equals("")) return;

		final String token = input.split(" ")[0];
		final String[] args = input.substring(token.length()).replaceAll(" +", " ").trim().split(" ");

		ArrayList<Command> matching = new ArrayList<>();

		COMMANDS.forEach((command) -> {
			if(command.matches(token, args)) {
				matching.add(command);
			}
		});

		if(matching.size() == 1) {
			matching.get(0).run(args);
		}
		else if(matching.size() == 0) {
			output(INPUT_ERROR);
		}
		else {
			output("Ambiguous token \"" + token + "\" (could refer to two or more commands).");
		}
		readyToReceiveInput = false;
	}

	public void setCommander(Commander commander) {
		this.commander = commander;
		COMMANDS.forEach((command) -> command.setCommander(commander));
	}

	public Commander getCommander() {
		return commander;
	}

	public ArrayList<Command> getCommands() {
		return COMMANDS;
	}

	public Command getCommand(String label) {
		for(Command command : COMMANDS) {
			if(command.getId().equals(label)) return command;
		}

		return null;
	}

	public void output(String output) {
		commander.output(output);
	}

	public void output(String output, boolean newline) {
		commander.output(output, newline);
	}

	public void setInputPrompt(Supplier<String> inputPrompt) {
		this.inputPrompt = inputPrompt;
	}
}
