package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Function Def :=  def <Identifier>(<Paramaters>):<Returns> {
 *						<Statement Group>
 *                  }
 * 
 * Paramaters := [<Identifier>[, <Identifier>]+]
 * 
 * Returns := [<Identifier>[, <Identifier>]+]
 */
public class Function extends SyntaxObject {

	Token first;
	Token funcName;
	ArrayList<Token> paramaters;
	ArrayList<Token> returns;
	StatementGroup logic;
	
	public Function(Token next){
		first = next;
		paramaters = new ArrayList<>();
	}
	
	@Override
	public Token parse() {
		funcName = first.getNext();
		if (!funcName.isIdentifier()){
			throw new RuntimeException("Expected identifier for function name, found: " + funcName);
		}
		
		Token test = funcName.getNext();
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' for function parmater list, found: " + test);
		
		test = test.getNext();
		while (test.isIdentifier()){
			paramaters.add(test);
			test = test.getNext();
			if (test.isOperator(Operator.COMMA))
				test = test.getNext();
		}
		
		if (!test.isOperator(Operator.BRACKET_RIGHT))
			throw new RuntimeException("Expected ')' for function parmater list, found: " + test);
		
		test = test.getNext();
		if (!test.isOperator(Operator.COLON))
			throw new RuntimeException("Expected ':' in function def, found: " + test);
		
		test = test.getNext();
		while (test.isIdentifier()){
			returns.add(test);
			test = test.getNext();
			if (test.isOperator(Operator.COMMA))
				test = test.getNext();
		}
		
		if (!test.isOperator(Operator.BRACE_LEFT))
			throw new RuntimeException("Expected opening brace for function logic, found: " + test);
		
		test = test.getNext();
		if (!test.isNewline())
			throw new RuntimeException("Expected newline before function logic, found: " + test);
		
		test = test.getNext();
		logic = new StatementGroup(test);
		test = logic.parse();
		
		if (!test.isOperator(Operator.BRACE_RIGHT))
			throw new RuntimeException("Expected closing brace for function logic, found: " + test);
		
		return test.getNext();
	}

	@Override
	public String emitCode() {
		// Push BP
		// Mov BP, SP
		// Push all reg

		// Pop all reg
		// Pop BP
		// Ret paramaters
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void print(int level) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
