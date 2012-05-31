package gluoncompiler;

import java.util.LinkedList;

/**
 * Handles labels to be output into assembler code.
 */
public class GluonLabels {
	
	private static LinkedList<String> endLabels = new LinkedList<>();
	
	public static String createLabel(Token token, String extra) {
		return token.getValue() + "_on_line_" + token.getLine() + "_" + extra;
	}
	
	public static void addEndLabel(String label) {
		endLabels.addLast(label);
	}
	
	public static void removeEndLabel(String label) {
		if (endLabels.getLast().equals(label)){
			endLabels.removeLast();
		} else {
			Error.abort("Expected end label: " + label + " Found end label: " + endLabels.getLast());
		}
	}
	
	public static String getEndLabel() {
		return endLabels.getLast();
	}
}
