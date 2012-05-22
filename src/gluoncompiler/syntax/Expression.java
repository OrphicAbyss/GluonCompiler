package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Expression := <Term> [ (+|-) <Term> ]+
 */
class Expression extends SyntaxObject {
	private Token first;
	private Term term;
	ArrayList<Term> terms;
	ArrayList<Operator> ops;

	public Expression(Token next){
		first = next;
		terms = new ArrayList<>();
		ops = new ArrayList<>();
	}
	
	@Override
	public Token parse() {
		Token test = first;
		term = new Term(test);
		test = term.parse();
		
		while (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (Operator.ADD.equals(testOp) || Operator.SUBTRACT.equals(testOp)) {
				ops.add(testOp);
				Term t = new Term(test.getNext());
				terms.add(t);
				test = t.parse();
			} else {
				break;
			}
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
