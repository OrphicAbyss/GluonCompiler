package gluoncompiler;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Handles variables, registering, using etc.
 */
public class GluonVariable {
	static ArrayList<String> variables;

	/**
	 * Register a variable in our list of variables if it is not already
	 * contained in it.
	 */
	public static void registerVariable(String name) {
		if (!variables.contains(name)){
			variables.add(name);
		} else {
			Error.abort("Variable already defined in program: " + name);
		}
	}

	/**
	 * Create an error if a variable hasn't been registered before being
	 * used.
	 */
	public static void testVariableRegistered(String name) {
		if (!variables.contains(name)){
			Error.abort("Variable used before assigned to: " + name);
		}
	}

	public static Collection<String> getVariables() {
		return variables;
	}

	public static void init() {
		variables = new ArrayList<>();
	}

}
