package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import gluoncompiler.Token;

/**
 * Represents a literal number constant
 */
public class LiteralNumber extends SyntaxObject {
	private String value;
	private Token token;
	
	public LiteralNumber(Token token, ScopeObject parentScope){
		scope = parentScope;
		this.token = token;
	}

	@Override
	public Token parse() {
		if (!token.isLiteral())
			throw new RuntimeException("Expected literal, found: " + token);
		
		this.value = token.getValue();
		return token.getNext();
	}
	
	@Override
	public void emitCode(StringBuilder code){
		code.append(GluonOutput.codeLine("MOV EAX, " + value));
	}

	@Override
	public void print(int level) {
		printLevel(level);
		printLn("NUMBER " + value);
	}
}
