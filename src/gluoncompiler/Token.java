package gluoncompiler;

import java.util.ArrayList;

/**
 * Represents a token in a source file.
 *
 * Tokens can be of type: Keyword, Identifier, Operator, Literal
 */
public class Token {

	/**
	 * Builds a set of tokens out of the word passed in.
	 *
	 * @param tokens ArrayList to add tokens to
	 * @param word The string to tokenise
	 */
	public static void buildTokens(ArrayList<Token> tokens, String word) {
		while (!"".equals(word)){
			int wordLen = word.length();

			char first = word.charAt(0);
			// Try building an identifier
			if (Character.isLetter(first))
				word = matchIdentifier(tokens, word);
			else if (Character.isDigit(first))
				word = matchLiteral(tokens, word);
			else
				word = matchOperator(tokens, word);

			// If the word length hasn't changed (remove a char as an invalid token)
			if (word.length() == wordLen){
				tokens.add(new Token(TokenType.UNKNOWN,word.substring(0,1)));
				word = word.substring(1);
			}
		}
	}

	public static Token createNewlineToken(){
		return new Token(TokenType.NEWLINE,"");
	}

	private static String matchIdentifier(ArrayList tokens, String word){
		StringBuilder token = new StringBuilder();
		int i=0;

		for (; i<word.length(); i++){
			char current = word.charAt(i);
			if (!Character.isLetterOrDigit(current)){
				break;
			}
			token.append(current);
		}

		if (i != 0) {
			Keyword key = testForKeyword(token.toString());
			if (key != null)
				tokens.add(new Token(TokenType.KEYWORD, key.toString()));
			else
				tokens.add(new Token(TokenType.IDENTIFIER,token.toString()));
		}

		return word.substring(i);
	}

	private static Keyword testForKeyword(String token){
		String upperValue = token.toUpperCase();

		for (Keyword key: Keyword.values()){
			if (key.name().equals(upperValue)){
				return key;
			}
		}

		return null;
	}

	private static String matchLiteral(ArrayList tokens, String word){
		StringBuilder token = new StringBuilder();
		int i = 0;

		for (; i<word.length(); i++){
			char current = word.charAt(i);
			if (!Character.isDigit(current)){
				break;
			}
			token.append(current);
		}

		if (i != 0)
			tokens.add(new Token(TokenType.LITERAL,token.toString()));

		return word.substring(i);
	}

	private static String matchOperator(ArrayList tokens, String word){
		// Try building a symbol
		for (Operator testOp: Operator.values()) {
			String testOpStr = testOp.getValue();
			int testOpLen = testOpStr.length();
			if (testOpLen <= word.length()){
				String testWord = word.substring(0, testOpLen);
				if (testOpStr.equals(testWord)) {
					tokens.add(new Token(TokenType.OPERATOR,testWord));
					word = word.substring(testOpLen);
					break;
				}
			}
		}

		return word;
	}

	private TokenType type;
	private String value;

	public Token(TokenType type, String value){
		switch (type){
			case IDENTIFIER:
			case KEYWORD:
			case LITERAL:
			case OPERATOR:
			case NEWLINE:
			case UNKNOWN:
				this.type = type;
				this.value = value;
				return;
			default:
				this.type = TokenType.UNKNOWN;
				break;
		}
	}

	@Override
	public String toString(){
		return getType().toString() + ": " + getValue();
	}

	/**
	 * @return the type
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
