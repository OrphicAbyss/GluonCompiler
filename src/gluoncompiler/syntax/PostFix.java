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
	
	public PostFix(Token start, ScopeObject parentScope) {
		first = start;
		scope = parentScope;
	}
	
	@Override
	public Token parse() {
		Token test = first;
		
		if (test.isLiteral()) {
			value = new LiteralNumber(test, scope);
			test = value.parse();
		} else if (test.isIdentifier()) {
			var = new Variable(test, scope);
			test = var.parse();
			if (test.isOperator(Operator.INCREMENT)) {
				inc = true;
				test = test.getNext();
			} else if (test.isOperator(Operator.DECREMENT)) {
				dec = true;
				test = test.getNext();
			}
		} else {
			throw new RuntimeException("Expecting value (literal or variable), found: " + test);
		}
		
		return test;
	}

	@Override
	public void emitCode(StringBuilder code) {
		if (value != null) {
			value.emitCode(code);
			return;
		}
		
		var.emitCode(code);
				
		if (inc)
			code.append(GluonOutput.codeLine("INC [" + GluonLibrary.varToLabel(var.getName()) + "]"));
		else if (dec)
			code.append(GluonOutput.codeLine("DEC [" + GluonLibrary.varToLabel(var.getName()) + "]"));
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
