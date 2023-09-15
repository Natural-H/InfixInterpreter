SubAlgoritmo valido <- EsValido(expresion)
	Definir valido Como Logico;
	Definir error Como Cadena;
	Definir stackParentesis Como Pila;
	Definir ultimoFueOperando Como Logico;
	Definir ultimoOperador Como Entero;
	
	Definir elemento Como Caracter;
	
	ultimoFueOperando <- Falso;
	ultimoOperador <- 0;
	
	Si estaVacia(expresion) Entonces
		Escribir "Error: La expresion no puede estar vacia!";
		Regresa Falso;
	FinSi
	
	Para i <- 0 Hasta Longitud(expresion) Hacer
		elemento = caracterEn(expresion, i);
		
		Si elemento = ' ' Entonces
			Continua;
		FinSi
		
		Si elemento = '(' Entonces
			push(stackParentesis, i);
			
			Si ultimoFueOperando Entonces
				marcarErrorEn(expresion, i, "Los parentesis no pueden ser puestos sin un operador!");
				Regresa Falso;
			FinSi
			
		SiNo
			Si elemento = ')' Entonces
				Si Longitud(parentesisStack) - 1 <= -1 Entonces
					
					Si contiene(expresion, '(') Entonces
						error <- ") no tiene un parentesis de apertura!";
					SiNo
						error <- "Nunca se encontro un parentesis de apertura!";
					FinSi
					
					marcarErrorEn(expresion, i, error);
					Regresa Falso;
				FinSi
				
				Si no ultimoFueOperando Entonces
					marcarErrorEn(expresion, i, "La expresion no esta completa!");
					Regresa Falso;
				FinSi
				
				pop(parentesisStack);
			SiNo
					Si (elemento <= 'a') Y (elemento >= 'z') Entonces
						Si ultimoFueOperando Entonces
							marcarErrorEn(expresion, i, "No se esperaba operando! Se esperaba: Operador");
							Regresa Falso;
						FinSi
							
						ultimoFueOperando <- Verdadero;
					SiNo
						Si elemento = '+' o elemento = '-' o elemento = '/' o elemento = '*' o elemento = '^' Entonces
							Si no ultimoFueOperando Entonces
								marcarErrorEn(expression, i, "No se esperaba operador! Se esperaba: Operando");
								Regresa Falso;
							FinSi
							
							ultimoFueOperando <- Falso;
							ultimoOperador <- i;
						SiNo
							marcarErrorEn(expression, i, "Se encontro un caracter invalido!");
							Regresa Falso;
						FinSi
					FinSi
				FinSi
			FinSi
		FinPara
		
		Si no vacio(parentesisStack) Entonces
			marcarErrorEn(expression, parenthesisStack.peek(), "( no tiene parentesis de cierre!");
			Regresa Falso;
		FinSi
		
		Si no ultimoFueOperando Entonces
			markErrorAt(expression, lastOperator, "Expresion termina con operador ", caracterEn(expresion, ultimoOperador), "!";
			Regresa Falso;
		FinSi
		
		Escribir "La expresion es valida!";
		Regresa Verdadero;
FinSubAlgoritmo

Proceso InfixInterpreter
	Definir expresionInfija Como Cadena;
	Definir expresionNormalizada Como Cadena;
	Definir expresionPostfija Como Cadena;
	
	Hacer
		Escribir "Escribe una expresion: ";
		Leer ExpresionInfija;
		expresionNormalizada <- expresionInfija;
	Hasta Que valido(expresionNormalizada)
	
	Escribir "Expresion infija: ", expresionInfija;
	evaluarInfijo(exprNormalizada);
	
	expresionPostfija <- infijaAPostfija(expresionNormalizada);
	Escribir "Expresion postfija: ", expresionPostfija;
FinProceso

