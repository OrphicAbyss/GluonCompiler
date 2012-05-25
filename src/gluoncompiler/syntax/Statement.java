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
	
	public Statement(Token next){
		first = next;
		isFunctionCall = false;
		isAssignment = false;
	}
	
	@Override
	public Token parse() {
		ident = first.getValue();
		Token test = first.getNext();		
		
		if (test.isOperator()){
			Operator testOp = test.getOperator();
			if (Operator.BRACKET_LEFT.equals(testOp)){
				isFunctionCall = true;
				functionCall = new FunctionCall(first);
				test = functionCall.parse();
			} else {
				isAssignment = true;
				assignmentExp = new AssignmentExpression(first);
				test = assignmentExp.parse();
			}
		} else {
			assert(false);
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		if (isAssignment){
			return assignmentExp.emitCode();
		} else if (isFunctionCall){
			return functionCall.emitCode();
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
