package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * TODO: These statements should be handled somewhere else
 * Statement Other := <Function call> | <Assignment Expression>
 * Function Call := <Identifier>()
 * Assignment Expression := <Identifier> (=|+=|-=|*=|/=) <BooleanExpression>
 */
public class Statement extends SyntaxObject {
	
	Token first;
	String ident;
	boolean isFunctionCall;
	boolean isAssignment;
	Operator assignmentOp;
	AssignmentExpression assignmentExp;
	FunctionCall functionCall;
	
	public Statement(Token next, ScopeObject parentScope) {
		first = next;
		isFunctionCall = false;
		isAssignment = false;
		scope = parentScope;
	}
	
	@Override
	public Token parse() {
		ident = first.getValue();
		Token test = first.getNext();		
		
		if (test.isOperator(Operator.BRACKET_LEFT)) {
			isFunctionCall = true;
			functionCall = new FunctionCall(first);
			test = functionCall.parse();
		} else if (test.isOperator()) {
			isAssignment = true;
			assignmentExp = new AssignmentExpression(first, scope);
			test = assignmentExp.parse();
		} else {
			throw new RuntimeException("Expected '(' or '=', found: " + test);
		}
		
		return test;
	}

	@Override
	public void emitCode(StringBuilder code) {
		if (isAssignment){
			assignmentExp.emitCode(code);
		} else if (isFunctionCall){
			functionCall.emitCode(code);
		} else {
			throw new RuntimeException("Emit Code: Unknown statement type.");
		}	
	}

	@Override
	public void print(int level) {
		if (isFunctionCall){
			System.out.println("Is function");
		} else if (isAssignment) {
			assignmentExp.print(level);
		} else {
			printClass(level);
			printLevel(level);
			System.out.println("Unknown statement.");
		}
	}
	
}
