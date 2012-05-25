package gluoncompiler.syntax;

import gluoncompiler.GluonLabels;
import gluoncompiler.GluonOutput;
import gluoncompiler.Keyword;
import gluoncompiler.Token;

/**
 * While Statement := While <BooleanExpression>
 *						  <Statement Group>
 *					  End
 */
class WhileStatement extends Statement {
	BooleanExpression testExp;
	StatementGroup statements;
	
	public WhileStatement(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		Token next = first.getNext();
		testExp = new BooleanExpression(next);
		next = testExp.parse();
		assert(next.isNewline());
		next = next.getNext();
		
		Keyword[] target = { Keyword.END };
		statements = new StatementGroup(next,target);
		next = statements.parse();
		
		return next.getNext();
	}

	@Override
	public String emitCode() {
		// Creae labels
		String labelStart = GluonLabels.createLabel(first, "start");
		String labelEnd = GluonLabels.createLabel(first, "end");
		GluonLabels.addEndLabel(labelEnd);
		
		StringBuilder sb = new StringBuilder();
		sb.append(GluonOutput.commentLine("While Statement"));
		sb.append(GluonOutput.labelLine(labelStart));
		sb.append(testExp.emitCode());
		sb.append(GluonOutput.codeLine("TEST EAX, EAX"));
		sb.append(GluonOutput.codeLine("JZ " + labelEnd));
		sb.append(statements.emitCode());
		sb.append(GluonOutput.codeLine("JMP " + labelStart));
		sb.append(GluonOutput.labelLine(labelEnd));
		sb.append(GluonOutput.commentLine("End While"));
		
		GluonLabels.removeEndLabel(labelEnd);
		
		return sb.toString();
	}
	
	@Override
	public void print(int level) {
		printLevel(level);
		printLn("WHILE");
		testExp.print(level + 1);
		
		printLevel(level);
		printLn("DO");
		statements.print(level + 1);
	}
	
}
