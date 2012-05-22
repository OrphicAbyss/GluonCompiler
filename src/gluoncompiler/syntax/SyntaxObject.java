package gluoncompiler.syntax;

import gluoncompiler.Token;

/**
 * Base class for syntax objects
 */
public abstract class SyntaxObject {
	public abstract Token parse();
	public abstract String emitCode();
}
