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
	BRACKET_LEFT("("),
	BRACKET_RIGHT(")"),
	ASSIGN("="),
	LESS_THAN("<"),
	GREATER_THAN(">"),
	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	COLON(":"),
	SEMICOLON(";");

	String value;

	Operator(String value) {
		this.value = value;
	}

	String getValue() {
		return value;
	}
}
