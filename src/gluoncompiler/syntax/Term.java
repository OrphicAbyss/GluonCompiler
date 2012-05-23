package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Term := Factor [ (*|/) Factor ]+
 */
class Term extends SyntaxObject {
	
	Token first;
	Factor factor;
	ArrayList<Factor> factors;
	ArrayList<Operator> ops;
	
	public Term(Token test) {
		first = test;
		factors = new ArrayList<>();
		ops = new ArrayList<>();
	}

	@Override
	public Token parse() {
		Token test = first;
		
		factor = new Factor(test);
		test = factor.parse();
		
		while (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (Operator.MULTIPLY.equals(testOp) || Operator.DIVIDE.equals(testOp)) {
				ops.add(testOp);
				Factor f = new Factor(test.getNext());
				factors.add(f);
				test = f.parse();
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

	@Override
	public void print(int level) {
		factor.print(level);
		for (int i=0; i<ops.size(); i++){
			printLevel(level);
			printLn(ops.get(i).name());
			factors.get(i).print(level);
		}
	}
	
}
