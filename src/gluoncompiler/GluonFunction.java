package gluoncompiler;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Deals with function details
 */
public class GluonFunction {
	
	static ArrayList<String> functions;

	/**
	 * Register a function in our list of functions
	 */
	public static void registerFunction(String name) {
		if (!functions.contains(name)) {
			functions.add(name);
		} else {
			Error.abort("Function already defined in program: " + name);
		}
	}

	/**
	 * Create an error if we try can call a function that doesn't exist in the
	 * program.
	 */
	public static void testFunctionExists(String name) {
		if (!functions.contains(name)) {
			Error.abort("Undefined function in call: " + name);
		}
	}

	/**
	 * Currently just return the name as is, but in the future, the name will
	 * be base on the parameters.
	 */
	public static String getLabel(String name){
		return name;
	}
	
	public static Collection<String> getFunctions() {
		return functions;
	}

	public static void init() {
		functions = new ArrayList<>();
	}

}
