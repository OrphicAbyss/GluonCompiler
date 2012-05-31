package gluoncompiler.syntax;

import gluoncompiler.Keyword;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Represents a program
 */
public class Program extends SyntaxObject {

	Token first;
	ArrayList<Function> functions;
	
	public Program(Token start) {
		first = start;
		functions = new ArrayList<>();
		scope = new ScopeObject();
	}
	
	@Override
	public Token parse() {
		Token test = first;
		while (test.isKeyword(Keyword.DEF)){
			Function func = new Function(test, scope);
			test = func.parse();
			functions.add(func);
			
			if (!test.isNewline())
				throw new RuntimeException("Expected newline while parsing program, found: " + test);
			test = test.getNext();
		}
		
		return test;
	}
	
	@Override
	public void emitCode(StringBuilder code) {
		for (Function func: functions)
			func.emitCode(code);
	}

	@Override
	public void print(int level) {
		printClass(level);
		for (Function func: functions)
			func.print(level+1);
	}
	
}
