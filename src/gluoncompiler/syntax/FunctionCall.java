package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Function call := <Function Identifier> '(' [Identifier [,Identifier]+] ')'
 * 
 * TODO: merge with variable into identifier
 */
public class FunctionCall extends SyntaxObject {
	
	private Token first;
	private String name;
	ArrayList<Variable> parameters;
	
	public FunctionCall(Token token, ScopeObject parentScope){
		first = token;
		parameters = new ArrayList<>();
		scope = parentScope;
	}

	@Override
	public Token parse() {
		if (!first.isIdentifier())
			throw new RuntimeException("Expected identifier for function name, found: " + first);
		
		name = first.getValue();
		Token test = first.getNext();
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' for function parmater list, found: " + test);
		
		test = test.getNext();
		while (test.isIdentifier()){
			Variable param = new Variable(test,scope);
			parameters.add(param);
			test = param.parse();
			if (test.isOperator(Operator.COMMA))
				test = test.getNext();
		}

		if (!test.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' for function parmater list, found: " + test);
		
		return test.getNext();
	}
	
	@Override
	public void emitCode(GluonOutput code) {
		for (int i=parameters.size()-1; i>=0; i--)
			code.code("PUSH " + parameters.get(i).getLabelName());
//		for (Variable param: parameters)
//			code.code("PUSH " + param.getLabelName());
		
		code.code("CALL " + name);
		
		if (parameters.size() > 0)
			code.code("ADD ESP, " + (4 * parameters.size()));
	}

	@Override
	public void print(int level) {
		print(level);
		printLevel(level);
		System.out.println(name);
	}
}
