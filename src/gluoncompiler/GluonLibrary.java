package gluoncompiler;

import java.util.Collection;

/**
 * Contains library code for including in the assembler output.
 *
 * @author DrLabman
 */
public class GluonLibrary {
	public static String varToLabel(String varName){
		return "var_"+varName;
	}

	public static String funcToLabel(String funcName){
		return "func_"+funcName;
	}


	public static void printASMStart(GluonOutput output){
		output.outputLine("org 100h",false);
		output.outputLine(";jump to start of program",false);
		output.outputLine("JMP start",true);
		//TODO: include lib stuff like print char functions here

		output.outputLine(";print a number, AX contains the number, BX contains the base",false);
		output.outputLine("print_number:",false);
		output.outputLine("MOV DX, 0",true);
		output.outputLine("PUSH 0       ; push 0 on the stack as a marker when printing",true);
		output.outputLine("calculate_digit:",false);
		output.outputLine("DIV BX       ; divide by base",true);
		output.outputLine("ADD DX, '0'  ; add '0' char to dx value to get correct char",true);
		output.outputLine("PUSH DX      ; push the char onto stack for printing later",true);
		output.outputLine("MOV DX, 0",true);
		output.outputLine("TEST AX, AX  ; test if there is anything left",true);
		output.outputLine("JNE calculate_digit ; jump back up to deal with the rest of the number",true);
		output.outputLine(";print the chars on the stack until we get a zero",false);
		output.outputLine("MOV AH, 2    ; set ah (print char when int 21h called)",true);
		output.outputLine("print_off_stack:",false);
		output.outputLine("POP DX       ; pop a character",true);
		output.outputLine("TEST DX,DX   ; test if it's a null char",true);
		output.outputLine("JE num_end   ; exit if null",true);
		output.outputLine("INT 21h      ; otherwise print char",true);
		output.outputLine("JMP print_off_stack",true);
		output.outputLine("num_end:",false);
		output.outputLine("MOV DX,0Dh   ; print CR",true);
		output.outputLine("INT 21h",true);
		output.outputLine("MOV DX,0Ah   ; print LF",true);
		output.outputLine("INT 21h",true);
		output.outputLine("RET",true);
		output.outputLine(";end of print number",false);
		output.outputLine("",false);
		output.outputLine(";start of program",false);
		output.outputLine("start:",false);
	}

	public static void printASMEnd(GluonOutput output){
		output.outputLine(";Dos Exit Call",false);
		output.outputLine("MOV AX,4C00h",true);
		output.outputLine("INT 21h",true);
	}

	public static void printVariables(GluonOutput output, Collection<String> variables){
		output.outputLine("; print all vars", false);
		output.outputLine("print:", false);
		output.outputLine("MOV BX, 10",true);
		for (String var: variables){
			output.outputLine("MOV EAX, [" + varToLabel(var) + "]", true);
			output.outputLine("CALL print_number", true);
		}
		output.outputLine("RET",true);
		output.outputLine("", false);
		output.outputLine("; data section", false);
		for (String var: variables){
			output.outputLine(varToLabel(var) + "\tdd\t?", true);
		}
	}
}
