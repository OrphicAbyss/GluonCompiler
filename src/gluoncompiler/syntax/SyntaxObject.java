package gluoncompiler.syntax;

import gluoncompiler.Token;

/**
 * Base class for syntax objects
 */
public abstract class SyntaxObject {
	
	protected ScopeObject scope;
	
	public abstract Token parse();
	public abstract void emitCode(StringBuilder code);
	public abstract void print(int level);
	
	protected void printClass(int level) {
		String className = this.getClass().getSimpleName();
		printLevel(level);
		System.out.println(className);
	}
	
	protected void printLevel(int level) {
		for (int i=0; i<level; i++) {
			System.out.print("  ");
		}
	}
	
	protected void printLn(String str){
		System.out.println(str);
	}
}
