package gluoncompiler;

/**
 * Represents a token in a source file.
 *
 * Tokens can be of type: Keyword, Identifier, Operator, Literal
 */
class Token {
	enum Type { UNKNOWN, KEYWORD, IDENTIFIER, OPERATOR, LITERAL };
	enum Keywords { VAR, IF, WHILE, FOR, END };
	enum Operator {
		PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/");

		String token;

		Operator(String token){
			this.token = token;
		}

		String getToken(){
			return token;
		}

		static String []tokens(){
			String tokens[] = {};
			int i = 0;
			for (Operator val: values()){
				tokens[i++] = val.getToken();
			}
			return tokens;
		}
	};

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
				this.type = Type.LITERAL;
				this.value = value;
				return;
			case UNKNOWN:
				// test for symbol
				this.type = Type.UNKNOWN;
				this.value = value;
				return;
			default:
				invalid = true;
				this.type = Type.UNKNOWN;
				break;
		}
		
//
//		// Tokens that start with a character but don't match a keyword are identifiers
//		char first = value.charAt(0);
//		if (Character.isLetter(first)){
//			type = Type.IDENTIFIER;
//			for (int i=0; i<value.length(); i++){
//				if (!Character.isLetterOrDigit(value.charAt(i))){
//					invalid = true;
//					break;
//				}
//			}
//			return;
//		}
//
//		if (Character.isDigit(first)){
//			type = Type.LITERAL;
//			for (int i=0; i<value.length(); i++){
//				if (!Character.isDigit(value.charAt(i))){
//					invalid = true;
//					break;
//				}
//			}
//			return;
//		}
//
//		for (String token: Operator.tokens()){
//			if (token.equals(value)){
//				type = Type.OPERATOR;
//				return;
//			}
//		}

		// Unknown token
	}

	@Override
	public String toString(){
		return type.toString() + ": " + value;
	}
}
