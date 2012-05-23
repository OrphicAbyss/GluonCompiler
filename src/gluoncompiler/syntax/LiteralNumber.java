package gluoncompiler.syntax;

import gluoncompiler.Token;

/**
 * Represents a literal number constant
 */
public class LiteralNumber extends SyntaxObject {
	private String value;
	private Token token;
	
	public LiteralNumber(Token token){
		assert(token.isLiteral());
		this.value = token.getValue();
		this.token = token;
	}

	@Override
	public Token parse() {
		return token.getNext();
	}
	
	@Override
	public String emitCode(){
		return value;
	}

	@Override
	public void print(int level) {
		printLevel(level);
		printLn("NUMBER " + value);
	}
}
