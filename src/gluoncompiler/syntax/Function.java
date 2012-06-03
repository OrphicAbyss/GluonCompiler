package gluoncompiler.syntax;

import gluoncompiler.GluonFunction;
import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Function Def :=  def <Identifier>(<Paramaters>):<Returns> {
 *						<Statement Group>
 *                  }
 * 
 * Parameters := [<Identifier>[, <Identifier>]+]
 * 
 * Returns := [<Identifier>[, <Identifier>]+]
 */
public class Function extends SyntaxObject {

	Token first;
	Token funcName;
	ArrayList<Token> parameters;
	ArrayList<Token> returns;
	StatementGroup logic;
	
	public Function(Token next, ScopeObject parentScope){
		first = next;
		parameters = new ArrayList<>();
		scope = new ScopeObject(parentScope);
	}
	
	@Override
	public Token parse() {
		funcName = first.getNext();
		if (!funcName.isIdentifier()){
			throw new RuntimeException("Expected identifier for function name, found: " + funcName);
		}
		GluonFunction.registerFunction(funcName.getValue());
		
		Token test = funcName.getNext();
		if (!test.isOperator(Operator.BRACKET_LEFT))
			throw new RuntimeException("Expected '(' for function parmater list, found: " + test);
		
		test = test.getNext();
		while (test.isIdentifier()){
			parameters.add(test);
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
		Operator[] targets = {Operator.BRACE_RIGHT};
		logic = new StatementGroup(test, targets, scope);
		test = logic.parse();
		
		if (!test.isOperator(Operator.BRACE_RIGHT))
			throw new RuntimeException("Expected closing brace for function logic, found: " + test);
		
		return test.getNext();
	}

	@Override
	public void emitCode(GluonOutput code) {
		code.comment("Function: " + funcName.getValue());
		String funcLabel = GluonFunction.getLabel(funcName.getValue());
		// Setup variables passed in on stack for use
		int offset = 2 + 4 * parameters.size();
		int used = 0;
		for (Token parameter: parameters){
			int stackOffset = offset - used;
			used += 4;
			code.code(parameter.getText() + " EQU " + stackOffset + "[bp]");
		}
		// Function
		code.label(funcLabel);
		// Push BP
		code.code("PUSH BP");
		// Mov BP, SP
		code.code("MOV BP, SP");
		// Push all reg
		code.code("PUSH EAX");
		code.code("PUSH EBX");
		code.code("PUSH ECX");
		code.code("PUSH EDX");
		// Actual function logic
		logic.emitCode(code);
		// Pop all reg
		code.code("PUSH EDX");
		code.code("PUSH ECX");
		code.code("PUSH EBX");
		code.code("PUSH EAX");
		// Pop BP
		code.code("POP BP");
		// Ret paramaters
		code.code("RET");
		
		for (Token parameter: parameters){
			code.code("RESTORE " + parameter.getText());
		}
		code.comment("Function end: " + funcName.getValue());
	}

	@Override
	public void print(int level) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
