package gluoncompiler;

/**
 * Operators for language
 */
 public enum Operator {
	ASSIGN_ADD("+="),
	ASSIGN_SUBTRACT("-="),
	ASSIGN_MULTIPLY("*="),
	ASSIGN_DIVIDE("/="),
	EQUALS("=="),
	NOT_EQUALS("!="),
	LESS_THAN_OR_EQUALS("<="),
	GREATER_THAN_OR_EQUALS(">="),
	INCREMENT("++"),
	DECREMENT("--"),
	BRACKET_LEFT("("),
	BRACKET_RIGHT(")"),
	BRACE_LEFT("{"),
	BRACE_RIGHT("}"),
	ASSIGN("="),
	LESS_THAN("<"),
	GREATER_THAN(">"),
	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	COLON(":"),
	SEMICOLON(";"),
	COMMA(",");

	String value;

	Operator(String value) {
		this.value = value;
	}

	String getValue() {
		return value;
	}
	
	boolean isAssignment(){
		final Operator[] assign = { ASSIGN_ADD, ASSIGN_SUBTRACT, ASSIGN_MULTIPLY, ASSIGN_DIVIDE, ASSIGN };

		boolean result = false;
		for (Operator op: assign){
			result = result || this.equals(op);
		}
		return result;
	}
}
