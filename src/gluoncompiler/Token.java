package gluoncompiler;

import java.util.ArrayList;

/**
 * Represents a token in a source file.
 *
 * Tokens can be of type: Keyword, Identifier, Operator, Literal
 */
class Token {
	enum Type { UNKNOWN, KEYWORD, IDENTIFIER, OPERATOR, LITERAL, NEWLINE };
	enum Keywords { VAR, IF, WHILE, FOR, END };
	enum Operator {
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
		DIVIDE("/");

		String value;

		Operator(String value){
			this.value = value;
		}

		String getValue(){
			return value;
		}

		static String []tokens(){
			String values[] = new String[values().length];
			int i = 0;
			for (Operator val: values()){
				values[i++] = val.getValue();
			}
			return values;
		}
	};

	static void buildTokens(ArrayList<Token> tokens, String word) {
		while (!"".equals(word)){
			int i;
			StringBuilder token = new StringBuilder();
			char first = word.charAt(0);
			// Try building an identifier
			if (Character.isLetter(first)){
				for (i = 0; i<word.length(); i++){
					char current = word.charAt(i);
					if (!Character.isLetterOrDigit(current)){
						break;
					}
					token.append(current);
				}
				if (i != 0)
					tokens.add(new Token(Type.IDENTIFIER,token.toString()));
				word = word.substring(i);
				continue;
			}

			// Try building a number constant
			if (Character.isDigit(first)){
				for (i = 0; i<word.length(); i++){
					char current = word.charAt(i);
					if (!Character.isDigit(current)){
						break;
					}
					token.append(current);
				}
				if (i != 0)
					tokens.add(new Token(Type.LITERAL,token.toString()));
				word = word.substring(i);
				continue;
			}

			boolean matched = false;
			// Try building a symbol
			for (String testOp: Operator.tokens()) {
				if (testOp.length() <= word.length()){
					String testWord = word.substring(0, testOp.length());
					if (testOp.equals(testWord)) {
						tokens.add(new Token(Type.OPERATOR,testWord));
						word = word.substring(testOp.length());
						matched = true;
						break;
					}
				}
			}
			if (!matched){
				tokens.add(new Token(Type.UNKNOWN,word.substring(0,1)));
				word = word.substring(1);
			}
		}
	}

	private Type type;
	private String value;
	private boolean invalid;

	public Token(Type type, String value){
		//incoming type should be identifier, literal or unknown
		String upperValue = value.toUpperCase();

		switch (type){
			case IDENTIFIER:
				// check identifiers agains keywords
				for (Keywords key: Keywords.values()){
					if (key.name().equals(upperValue)){
						// Uppercase the keyword
						this.value = upperValue;
						this.type = Type.KEYWORD;
						return;
					}
				}
				// it is an identifier
				this.type = Type.IDENTIFIER;
				this.value = value;
				return;
			case LITERAL:
			case OPERATOR:
			case NEWLINE:
				this.type = type;
				this.value = value;
				return;
			case UNKNOWN:
				this.type = Type.UNKNOWN;
				this.value = value;
				return;
			default:
				invalid = true;
				this.type = Type.UNKNOWN;
				break;
		}
	}

	@Override
	public String toString(){
		return type.toString() + ": " + value;
	}
}
