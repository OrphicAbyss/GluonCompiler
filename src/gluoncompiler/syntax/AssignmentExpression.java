package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Assignment Expression := [<Identifier> [+|-|*|/]=] <Boolean Expression>
 */
public class AssignmentExpression extends SyntaxObject {

	private Token first;
	private Variable variable;
	private Operator assignmentOp;
	private BooleanExpression assignmentExp;
	private BooleanExpression expression;
	
	public AssignmentExpression(Token next, ScopeObject parentScope){
		first = next;
		scope = parentScope;
	}
	
	@Override
	public Token parse() {
		Token test = first.getNext();
		if (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (testOp.name().startsWith("ASSIGN")) {
				variable = new Variable(first, scope);
				assignmentOp = testOp;
				test = test.getNext();
				assignmentExp = new BooleanExpression(test, scope);
				test = assignmentExp.parse();
			}
		}
		
		if (variable == null) {
			expression = new BooleanExpression(first, scope);
			test = expression.parse();
		}
		
		return test;
	}

	@Override
	public void emitCode(StringBuilder code) {
		if (variable != null){
			String varName = GluonLibrary.varToLabel(variable.getName());
			assignmentExp.emitCode(code);
			
			switch (assignmentOp){
				case ASSIGN:
					break;
				case ASSIGN_ADD:
					code.append(GluonOutput.codeLine("MOV EBX,[" + varName + "]"));
					code.append(GluonOutput.codeLine("ADD EAX,EBX"));
					break;
				case ASSIGN_SUBTRACT:
					code.append(GluonOutput.codeLine("MOV EBX, EAX"));
					code.append(GluonOutput.codeLine("MOV EAX,[" + varName + "]"));
					code.append(GluonOutput.codeLine("SUB EAX,EBX"));
					break;
				case ASSIGN_MULTIPLY:
					code.append(GluonOutput.codeLine("MOV EBX,[" + varName + "]"));
					code.append(GluonOutput.codeLine("IMUL EAX,EBX"));
					break;
				case ASSIGN_DIVIDE:
					code.append(GluonOutput.codeLine("MOV EBX, EAX"));
					code.append(GluonOutput.codeLine("MOV EDX, 0"));
					code.append(GluonOutput.codeLine("MOV EAX,[" + varName + "]"));
					code.append(GluonOutput.codeLine("IDIV EBX"));
					break;
			}
			code.append(GluonOutput.codeLine("MOV [" + varName + "],EAX"));
		} else {
			expression.emitCode(code);
		}
	}

	@Override
	public void print(int level) {
		if (assignmentExp != null) {
			variable.print(level);
			printLevel(level + 1);
			printLn(assignmentOp.name());
			assignmentExp.print(level + 1);
		} else {
			expression.print(level);
		}
	}
	
}
