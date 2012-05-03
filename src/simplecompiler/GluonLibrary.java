package simplecompiler;

/**
 * Contains library code for including in the assembler output.
 * 
 * @author DrLabman
 */
public class GluonLibrary {
	static void printASMStart(){
		GluonOutput.emitLn("org 100h",false);
		GluonOutput.emitLn(";jump to start of program",false);
		GluonOutput.emitLn("JMP start",true);
		//TODO: include lib stuff like print char functions here
		
		GluonOutput.emitLn(";print a number, AX contains the number, BX contains the base",false);
		GluonOutput.emitLn("print_number:",false);
		GluonOutput.emitLn("MOV DX, 0",true);
		GluonOutput.emitLn("PUSH 0       ; push 0 on the stack as a marker when printing",true);
		GluonOutput.emitLn("calculate_digit:",false);
		GluonOutput.emitLn("DIV BX       ; divide by base",true);
		GluonOutput.emitLn("ADD DX, '0'  ; add '0' char to dx value to get correct char",true);
		GluonOutput.emitLn("PUSH DX      ; push the char onto stack for printing later",true);
		GluonOutput.emitLn("MOV DX, 0",true);
		GluonOutput.emitLn("TEST AX, AX  ; test if there is anything left",true);
		GluonOutput.emitLn("JNE calculate_digit ; jump back up to deal with the rest of the number",true);
		GluonOutput.emitLn(";print the chars on the stack until we get a zero",false);
		GluonOutput.emitLn("MOV AH, 2    ; set ah (print char when int 21h called)",true);
		GluonOutput.emitLn("print_off_stack:",false);
		GluonOutput.emitLn("POP DX       ; pop a character",true);
		GluonOutput.emitLn("TEST DX,DX   ; test if it's a null char",true);
		GluonOutput.emitLn("JE num_end   ; exit if null",true);
		GluonOutput.emitLn("INT 21h      ; otherwise print char",true);
		GluonOutput.emitLn("JMP print_off_stack",true);
		GluonOutput.emitLn("num_end:",false);
		GluonOutput.emitLn("MOV DX,0Dh   ; print CR",true);
		GluonOutput.emitLn("INT 21h",true);
		GluonOutput.emitLn("MOV DX,0Ah   ; print LF",true);
		GluonOutput.emitLn("INT 21h",true);
		GluonOutput.emitLn("RET",true);
		GluonOutput.emitLn(";end of print number",false);
		GluonOutput.emitLn("",false);
		GluonOutput.emitLn(";start of program",false);
		GluonOutput.emitLn("start:",false);
	}
	
	static void printASMEnd(){
		GluonOutput.emitLn(";Dos Exit Call",false);
		GluonOutput.emitLn("MOV AX,4C00h",true);
		GluonOutput.emitLn("INT 21h",true);
	}
}
