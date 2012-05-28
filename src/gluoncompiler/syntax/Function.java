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
		GluonFunction.registerFunction(funcName.getValue());
		
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
		Operator[] targets = {Operator.BRACE_RIGHT};
		logic = new StatementGroup(test, targets);
		test = logic.parse();
		
		if (!test.isOperator(Operator.BRACE_RIGHT))
			throw new RuntimeException("Expected closing brace for function logic, found: " + test);
		
		return test.getNext();
	}

	@Override
	public String emitCode() {
		StringBuilder sb = new StringBuilder();
		String funcLabel = GluonFunction.getLabel(funcName.getValue());
		sb.append(GluonOutput.commentLine("Function: " + funcName.getValue()));
		sb.append(GluonOutput.labelLine(funcLabel));
		// Push BP
		sb.append(GluonOutput.codeLine("PUSH BP"));
		// Mov BP, SP
		sb.append(GluonOutput.codeLine("MOV BP, SP"));
		// Push all reg
		sb.append(GluonOutput.codeLine("PUSH EAX"));
		sb.append(GluonOutput.codeLine("PUSH EBX"));
		sb.append(GluonOutput.codeLine("PUSH ECX"));
		sb.append(GluonOutput.codeLine("PUSH EDX"));
		// Actual function logic
		sb.append(logic.emitCode());
		// Pop all reg
		sb.append(GluonOutput.codeLine("PUSH EDX"));
		sb.append(GluonOutput.codeLine("PUSH ECX"));
		sb.append(GluonOutput.codeLine("PUSH EBX"));
		sb.append(GluonOutput.codeLine("PUSH EAX"));
		// Pop BP
		sb.append(GluonOutput.codeLine("POP BP"));
		// Ret paramaters
		sb.append(GluonOutput.codeLine("RET"));
		return sb.toString();
	}

	@Override
	public void print(int level) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
