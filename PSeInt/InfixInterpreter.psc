SubAlgoritmo valido <- EsValido(expresion)
	Definir valido Como Logico;
	Definir ultimoParentesis Como Entero;
	Definir contadorParentesis Como Entero;
	Definir ultimoFueOperando Como Logico;
	Definir ultimoOperando Como Entero;
	Definir exprNormalizada Como Cadena;
	Definir caracterActual Como Cadena;
	Definir error Como Cadena;
	
	valido <- Verdadero;
	ultimoFueOperando <- Falso;
	contadorParentesis <- 0;
	ultimoParentesis <- 0;
	ultimoOperador <- 0;
	exprNormalizada <- Minusculas(expresion);
	
	Para i<-0 Hasta Longitud(exprNormalizada) Hacer
		caracterActual <- caracterEn(exprNormalizada, i);
		Si caracterActual='(' Entonces
			contadorParentesis <- contadorParentesis + 1;
			ultimoFueOperando <- Falso;
		SiNo
			Si caracterActual=')' Entonces
				contadorParentesis <- contadorParentesis-1;
				Si contadorParentesis<0 Entonces
					Si contiene(exprNormalizada,'(') Entonces
						error <- ') no tiene parentesis que lo abra';
					SiNo
						error <- 'Nunca se encontro un (';
					FinSi
					marcarErrorEn(expresion,i,error);
					valido <- Falso;
				FinSi
			SiNo
				Si caracterActual>=ASCII('a') Y caracterActual<=ASCII('z') Y  NO ultimoFueOperando Entonces
					ultimoFueOperando <- Verdadero;
				SiNo
					Si (caracterActual=ASCII('+') O caracterActual=ASCII('-') O caracterActual=ASCII('*') O caracterActual=ASCII('/') O caracterActual=ASCII('^')) Y ultimoFueOperando Entonces
						ultimoFueOperando <- Falso;
						ultimoOperador <- i;
					SiNo
						Si caracterActual<>ASCII(' ') Entonces
							Si ultimoFueOperando Entonces
								error <- 'Operador inesperado encontrado! Se esperaba operando';
							SiNo
								error <- 'Operando inesperado encontrado! Se esperaba operador';
							FinSi
							marcarErrorEn(expresion,i,error);
							valido <- Falso;
						FinSi
					FinSi
				FinSi
			FinSi
		FinSi
	FinPara
	
	Si contadorParentesis <> 0 Entonces
		marcarErrorEn(expresion,ultimoParentesis,'( no tiene un parentesis de cierre!');
		valido <- Falso;
	FinSi
	
	Si NO ultimoFueOperando Entonces
		marcarErrorEn(expresion,ultimoParentesis,Concatenar('La expresion termina con operador! Operador encontrado: ',caracterEn(expresion, ultimoOperador)));
		valido <- Falso;
	FinSi
FinSubAlgoritmo

Proceso InfixInterpreter
	
FinProceso

SubAlgoritmo loTiene <- contiene(string,busqueda)
	Definir loTiene Como Logico;
FinSubAlgoritmo

SubAlgoritmo marcarErrorEn(expresion,posicion,error)
	
FinSubAlgoritmo

SubAlgoritmo valorA <- ASCII(Caracter)
	Definir valorA Como Entero;
	valorA <- 0;
FinSubAlgoritmo

SubAlgoritmo char <- caracterEn(string,indice)
	Definir char Como Cadena;
	char <- SubCadena(string,indice,indice+1);
FinSubAlgoritmo
