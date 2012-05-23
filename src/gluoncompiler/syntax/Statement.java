package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * TODO: These statements should be handled somewhere else
 * Statement Other := <Function call> | <Assignment Statement>
 * Function Call := <Identifier>()
 * Assignment Statement := <Identifier> (=|+=|-=|*=|/=) <BooleanExpression>
 */
public class Statement extends SyntaxObject {
	Token first;
	String ident;
	boolean isFunctionCall;
	boolean isAssignment;
	Operator assignmentOp;
	BooleanExpression assignmentExp;
	
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
				test = test.getNext();
				testOp = test.getOperator();
				assert(Operator.BRACKET_RIGHT.equals(testOp));
				test = test.getNext();
			} else {
				isAssignment = true;
				assert(testOp.name().startsWith("ASSIGN"));
				assignmentOp = testOp;
				test = test.getNext();
				assignmentExp = new BooleanExpression(test);
				test = assignmentExp.parse();
			}
		} else {
			assert(false);
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void print(int level) {
		if (isFunctionCall){
			System.out.println("Is function");
		} else if (isAssignment) {
			printLevel(level);
			System.out.println(ident + " " + assignmentOp.name());
			assignmentExp.print(level + 1);
		} else {
			printClass(level);
			printLevel(level);
			System.out.println("Unknown statement.");
		}
	}
	
}
