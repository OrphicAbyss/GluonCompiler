package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;
import gluoncompiler.Tokeniser;

/**
 * Function call identifier
 * 
 * TODO: merge with variable into identifier
 */
public class FunctionCall extends SyntaxObject {
	private String name;
	
	public FunctionCall(Tokeniser tokeniser, Token token){
		assert(token.isIdentifier());
		name = token.getValue();
		tokeniser.matchOperator(Operator.BRACKET_LEFT, "Function Call");
		tokeniser.matchOperator(Operator.BRACKET_RIGHT, "Function Call");
	}

	@Override
	public Token parse() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	
	@Override
	public String emitCode() {
		return "CALL " + name;
	}	
}
