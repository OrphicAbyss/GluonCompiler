package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Postfix Exp := (<Variable>|<Literal>[++|--])
 */
public class PostFix extends SyntaxObject {

	private Token first;
	private LiteralNumber value;
	private Variable var;
	private boolean inc;
	private boolean dec;
	
	public PostFix(Token start){
		first = start;
	}
	
	@Override
	public Token parse() {
		Token test = first;
		
		if (test.isLiteral()){
			value = new LiteralNumber(test);
			test = value.parse();
		} else if (test.isIdentifier()){
			var = new Variable(test);
			test = var.parse();
			if (test.isOperator(Operator.INCREMENT)){
				inc = true;
				test = test.getNext();
			} else if (test.isOperator(Operator.DECREMENT)){
				dec = true;
				test = test.getNext();
			}
		} else {
			throw new RuntimeException("Expecting value (literal or variable), found: " + test);
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		if (value != null)
			return value.emitCode();
		
		StringBuilder sb = new StringBuilder();
		sb.append(var.emitCode());
				
		if (inc)
			sb.append(GluonOutput.codeLine("INC [" + GluonLibrary.varToLabel(var.getName()) + "]"));
		else if (dec)
			sb.append(GluonOutput.codeLine("DEC [" + GluonLibrary.varToLabel(var.getName()) + "]"));
		
		return sb.toString();
	}

	@Override
	public void print(int level) {
		if (value != null)
			value.print(level);
		else {
			if (inc) {
				printLevel(level);
				printLn("USE THEN INCREMENT");
				var.print(level + 1);
			} else if (dec) {
				printLevel(level);
				printLn("USE THEN DECREMENT");
				var.print(level + 1);
			} else {
				var.print(level);
			}
		}
	}

	public boolean isVariable() {
		return var != null;
	}
	
	public Variable getVariable() {
		return var;
	}
}
