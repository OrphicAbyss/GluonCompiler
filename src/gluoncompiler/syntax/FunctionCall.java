package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Function call identifier
 * 
 * TODO: merge with variable into identifier
 */
public class FunctionCall extends SyntaxObject {
	
	private Token first;
	private String name;
	
	public FunctionCall(Token token){
		first = token;
	}

	@Override
	public Token parse() {
		assert(first.isIdentifier());
		name = first.getValue();
		Token next = first.getNext();
		assert(next.isOperator());
		assert(Operator.BRACKET_LEFT.equals(next.getOperator()));
		next = next.getNext();
		assert(next.isOperator());
		assert(Operator.BRACKET_RIGHT.equals(next.getOperator()));
		
		return next.getNext();
	}
	
	@Override
	public void emitCode(GluonOutput code) {
		code.code("CALL " + name);
	}

	@Override
	public void print(int level) {
		print(level);
		printLevel(level);
		System.out.println(name);
	}
}
