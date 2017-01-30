package com.archmage.commander;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Command {

	private Commander commander;

	private final ArrayList<String>			TOKENS	= new ArrayList<>();
	private String									id			= null;
	private Supplier<String>					label		= this::getDefaultLabel;
	private BiPredicate<String, String[]>	matcher	= this::getDefaultMatcher;
	private Predicate<String[]>				action	= null;
	private Supplier<String>					usage		= this::getDefaultUsage;
	private Supplier<String>					summary	= this::getDefaultSummary;
	private Supplier<String[]>					helpText	= this::getDefaultHelpText;

	private Command(String id, Supplier<String> summary, String... tokens) {
		addTokens(id);
		addTokens(tokens);
		this.summary = summary;
	}

	public static Command make(String id, Supplier<String> summary) {
		return new Command(id, summary, new String[0]);
	}

	public static Command make(String id, Supplier<String> summary, String... tokens) {
		return new Command(id, summary, tokens);
	}

	public Command addTokens(String... tokens) {
		for(String token : tokens) {
			if(token == null) continue;
			if(!TOKENS.contains(token)) {
				TOKENS.add(token);
				if(id == null) {
					id = token;
				}
			}
		}
		return this;
	}

	public Command removeTokens(String... tokens) {
		for(String token : tokens) {
			if(TOKENS.contains(token)) {
				TOKENS.add(token);
			}
		}
		return this;
	}

	public Command setLabel(Supplier<String> label) {
		this.label = label;
		return this;
	}

	public String getLabel() {
		return label.get();
	}

	public final String getDefaultLabel() {
		return TOKENS.isEmpty() ? "" : TOKENS.get(0);
	}

	public Command setMatcher(BiPredicate<String, String[]> matcher) {
		this.matcher = matcher;
		return this;
	}

	public final boolean getDefaultMatcher(String cmd, String[] args) {
		for(String token : TOKENS) {
			if(cmd.equals(token)) return true;
		}
		return false;
	}

	public Command setAction(Predicate<String[]> action) {
		this.action = action;
		return this;
	}

	public Command setUsage(Supplier<String> usage) {
		this.usage = usage;
		return this;
	}

	public String getUsage() {
		return "Usage: " + usage.get();
	}

	public String getDefaultUsage() {
		return getId();
	}

	public Command setSummary(Supplier<String> summary) {
		this.summary = summary;
		return this;
	}

	public String getSummary() {
		return summary.get();
	}

	public String getDefaultSummary() {
		return "(no summary available for this command)";
	}

	public Command setHelpText(Supplier<String[]> helpText) {
		this.helpText = helpText;
		return this;
	}

	public String[] getDefaultHelpText() {
		return new String[] {"(no help available for this command)"};
	}

	public void outputHelp() {
		output(getId());
		if(TOKENS.size() > 1) {
			String aliases = "    aliases: ";
			for(String token : TOKENS) {
				if(token.equals(getId())) continue;
				aliases += token + ", ";
			}
			output(aliases.substring(0, aliases.length() - 2));
		}
		output("    " + getUsage());
		output("");
		output(summary.get());
		if(!helpText.get()[0].equals(getDefaultHelpText()[0])) {
			output("");
			for(String help : helpText.get())
				output(help);
		}
		output("");
	}

	public boolean matches(String token, String[] args) {
		BiPredicate<String, String[]> tempMatcher = (matcher != null ? matcher : this::getDefaultMatcher);
		return tempMatcher.test(token, args);
	}

	public void run(String[] args) {
		if(action != null) {
			if(!action.test(args)) output(getUsage());
		}
	}

	public Command setCommander(Commander commander) {
		this.commander = commander;
		return this;
	}

	public Commander getCommander() {
		return commander;
	}

	public Command output(String output) {
		commander.output(output);
		return this;
	}

	public Command output(String output, boolean newline) {
		commander.output(output, newline);
		return this;
	}

	public String getId() {
		return id;
	}
}
