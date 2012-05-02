package simplecompiler;

import java.util.ArrayList;

/**
 * Simple compiler for language code named 'Gluon'
 */
public class SimpleCompiler {
	static ArrayList<String> variables;

	/**
	 * Register a variable in our list of variables if it is not already
	 * contained in it.
	 *
	 * @param name
	 */
	static void registerVariable(String name){
		if (!variables.contains(name)){
			variables.add(name);
		}
	}

	/**
	 * Test if the next token is an identifier. If it is, build the
	 * identifier out of the stream.
	 *
	 * @return The identifier
	 */
	static String getIdentifier(){
		if (!GluonScanner.isAlpha())
			Error.expected("Identifier");

		StringBuilder ident = new StringBuilder();

		while (GluonScanner.isAlpha() || GluonScanner.isDigit()){
			ident.append(GluonScanner.current);
			GluonScanner.getChar();
		}

		return ident.toString();
	}

	/**
	 * Test if the next token is an integer. If it is, build the integer
	 * out of the stream.
	 *
	 * @return The integer found
	 */
	static String getInteger(){
		if (!GluonScanner.isDigit())
			Error.expected("Integer");

		StringBuilder integer = new StringBuilder();
		while (GluonScanner.isDigit()){
			integer.append(GluonScanner.current);
			GluonScanner.getChar();
		}

		return integer.toString();
	}

	/**
	 * Output our code.
	 *
	 * @param s code to emit
	 */
	static void emitLn(String s, boolean prependTab){
		if (prependTab)
			System.out.printf("\t%s\n",s);
		else
			System.out.printf("%s\n",s);
	}

	/*
	 * Parse and Translate Parentheses
	 */
	static void Parentheses() {
		GluonScanner.matchAndAccept('(');
		Expression();
		GluonScanner.matchAndAccept(')');
	}

	/*
	 * Parse and Translate a Function or Variable
	 */
	static void Ident() {
		String name = getIdentifier();
		if (GluonScanner.matchOnly('(')) {
			GluonScanner.matchAndAccept('(');
			GluonScanner.matchAndAccept(')');
			emitLn("BSR " + name, false);
		} else {
			registerVariable(name);
			emitLn("MOV [" + name + "], EAX", true);
		}
	}

	/*
	 * Parse and Translate a Constant or Variable
	 */
	static void ConstVar() {
		if (GluonScanner.isAlpha()) {
			Ident();
		} else {
			emitLn("MOV EAX, " + getInteger(), true);
		}
	}

	/*
	 * Parse and Translate a Unary Minus
	 */
	static void UnaryMinus() {
		GluonScanner.matchAndAccept('-');
		ConstVar();
		emitLn("NEG EAX", true);
	}

	/*
	 * Parse and Translate a Math Factor
	 */
	static void Factor() {
		switch (GluonScanner.current) {
			case '(':
				Parentheses();
				break;
			case '-':
				UnaryMinus();
				break;
			default:
				ConstVar();
				break;
		}
	}

	/*
	 * Recognize and Translate a Multiply
	 */
	static void Multiply() {
		GluonScanner.matchAndAccept('*');
		Factor();
		emitLn("POP EBX", true);
		emitLn("IMUL EAX,EBX", true);
	}

	/*
	 * Recognize and Translate a Divide
	 */
	static void Divide() {
		GluonScanner.matchAndAccept('/');
		Factor();
		emitLn("MOV EBX, EAX", true);
		emitLn("MOV EDX, 0", true);
		emitLn("POP EAX", true);
		emitLn("IDIV EBX", true);
	}

	/*
	 * Parse and Translate a Math Term
	 */
	static void Term() {
		Factor();
		while (GluonScanner.current == '*' || GluonScanner.current == '/') {
			emitLn("PUSH EAX", true);
			switch (GluonScanner.current) {
				case '*':
					Multiply();
					break;
				case '/':
					Divide();
					break;
			}
		}
	}

	/*
	 * Recognize and Translate an Add
	 */
	static void Add() {
		GluonScanner.matchAndAccept('+');
		Term();
		emitLn("POP EBX",true);
		emitLn("ADD EAX,EBX", true);
	}

	/*
	 * Recognize and Translate a Subtract
	 */
	static void Subtract() {
		GluonScanner.matchAndAccept('-');
		Term();
		emitLn("POP EBX", true);
		emitLn("SUB EAX,EBX", true);
		emitLn("NEG EAX", true);
	}

	/*
	 * Parse and Translate a Math Expression
	 */
	static void Expression() {
		Term();
		while (GluonScanner.current == '+' || GluonScanner.current == '-') {
			emitLn("PUSH EAX", true);
			switch (GluonScanner.current) {
				case '+':
					Add();
					break;
				case '-':
					Subtract();
					break;
			}
		}
	}

	/*
	 * Parse and Translate an Assignment Statement
	 */
	static void Assignment() {
		String name = getIdentifier();
		registerVariable(name);
		GluonScanner.matchAndAccept('=');
		Expression();
		emitLn("MOV ["+name+"],EAX", true);
	}

	static void printVariables(){
		emitLn("; data section", false);
//		emitLn("section '.data' writeable", false);

		for (String var: variables){
			emitLn(var + "\tdd\t?", true);
		}
	}

	static void printASMStart(){
		emitLn("org 100h",false);
		emitLn("JMP start",true);
		//TODO: include lib stuff like print char functions here
		
		emitLn(";print a number, AX contains the number, BX contains the base",false);
		emitLn("print_number:",false);
		emitLn("  MOV DX, 0",true);
		emitLn("PUSH 0       ; push 0 on the stack as a marker when printing",true);
		emitLn("calculate_digit:",false);
		emitLn("DIV BX       ; divide by base",true);
		emitLn("ADD DX, '0'  ; add '0' char to dx value to get correct char",true);
		emitLn("PUSH DX      ; push the char onto stack for printing later",true);
		emitLn("MOV DX, 0",true);
		emitLn("TEST AX, AX  ; test if there is anything left",true);
		emitLn("JNE calculate_digit ; jump back up to deal with the rest of the number",true);
		emitLn(";print the chars on the stack until we get a zero",false);
		emitLn("MOV AH, 2    ; set ah (print char when int 21h called)",true);
		emitLn("print_off_stack:",false);
		emitLn("POP DX       ; pop a character",true);
		emitLn("TEST DX,DX   ; test if it's a null char",true);
		emitLn("JE num_end   ; exit if null",true);
		emitLn("INT 21h      ; otherwise print char",true);
		emitLn("JMP print_off_stack",true);
		emitLn("num_end:",false);
		emitLn("MOV DX,0Dh   ; print CR",true);
		emitLn("INT 21h",true);
		emitLn("MOV DX,0Ah   ; print LF",true);
		emitLn("INT 21h",true);
		emitLn("RET",true);
		emitLn(";end of print number",false);
		emitLn("",false);
		emitLn("start:",false);
	}
	
	static void printASMEnd(){
		emitLn(";Dos Exit Int",false);
		emitLn("MOV AX,4C00h",true);
		emitLn("INT 24h",true);
	}
	
	/*
	 * Initialize
	 */
	static void Init() {
		variables = new ArrayList<String>();
		GluonScanner.skipWhitespace();
		printASMStart();
		
		Assignment();

		printASMEnd();
		printVariables();
	}

	/**
	 * Takes a filename as the main argument.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		GluonScanner.init();
		Init();

//		try {
//			if (args.length == 0){
//				// Print out help
//				System.out.println("Gluon Compiler Usage: SimpleCompiler <filename>");
//				return;
//			}
//
//			// Make sure the source file exists
//			File source = new File(args[0]);
//			if (!source.exists()){
//				System.out.println("Unable to find file to compile: " + args[0]);
//			}
//
//			// Tokenise the source file
//			in = new FileInputStream(source);
//			Init();
//
//		} catch (FileNotFoundException ex) {
//		}

	}
}
