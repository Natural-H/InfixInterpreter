import java.util.*;

public class Interpreter {
    public static boolean isValid(String expression) {
        System.out.println("Comprobando si " + expression + " es válido.\n");

        CustomStack<Integer> parenthesisCustomStack = new CustomStack<>("Pila índices de paréntesis");
        parenthesisCustomStack.setLogging(true);

        boolean lastWasOperand = false;
        int lastOperator = 0;

        if (expression.isEmpty()) {
            System.out.println("Error: La expresión no puede estar vacía!");
            return false;
        }

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            logReading(c);

            if (c == ' ')
                continue;

            if (c == '(') {
                System.out.println("Fué paréntesis de apertura.");
                parenthesisCustomStack.push(i);

                if (lastWasOperand) {
                    markErrorAt(expression, i, "¡Este paréntesis requiere un operador antes!");
                    return false;
                }
            } else if (c == ')') {
                System.out.println("Fué paréntesis de cierre.");
                if (parenthesisCustomStack.isEmpty()) {
                    markErrorAt(expression, i,
                            expression.contains("(") ? "¡')' no tiene paréntesis de apertura!"
                                    : "¡Nunca se encontró el carácter '('!");
                    return false;
                }

                if (!lastWasOperand) {
                    markErrorAt(expression, i, "¡La expresión está incompleta!");
                    return false;
                }

                parenthesisCustomStack.pop();
            } else if (isOperand(c)) {
                System.out.println("Fué operando.");
                if (lastWasOperand) {
                    markErrorAt(expression, i, "¡Se encontró un operando inesperado! Se esperaba: Operador");
                    return false;
                }

                lastWasOperand = true;
            } else if (isOperator(c)) {
                System.out.println("Fué operador.");
                if (!lastWasOperand) {
                    markErrorAt(expression, i, "¡Se encontró un operador inesperado! Se esperaba: Operando");
                    return false;
                }

                lastWasOperand = false;
                lastOperator = i;
            } else {
                markErrorAt(expression, i, "¡Se encontró un carácter inválido!");
                return false;
            }

            System.out.println();
        }

        if (!parenthesisCustomStack.isEmpty()) {
            markErrorAt(expression, parenthesisCustomStack.peek(), "¡'(' no tiene paréntesis de cierre!");
            return false;
        }

        if (!lastWasOperand) {
            markErrorAt(expression, lastOperator,
                    "¡La expresión no puede terminar con un operador ('" + expression.charAt(lastOperator) + "')!");
            return false;
        }

        System.out.println("¡La expresión es válida!");
        return true;
    }

    static String infixToPostfix(String infix) {
        System.out.println("Iniciando conversión a Postfija. Expresión: " + infix + "\n");
        String postfix = "";
        CustomStack<Character> operators = new CustomStack<>("Pila operadores");
        operators.setLogging(true);

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            logReading(c);

            if (c == ' ')
                continue;

            if (isOperand(c)) {
                postfix = postfix.concat(c + "");
                System.out.println("Fue operando, se concatena. Expresión actual: " + postfix);
            } else if (c == '(') {
                System.out.println("Fue paréntesis de apertura.");
                operators.push(c);
            } else if (c == ')') {
                System.out.println("Fue paréntesis de cierre.");
                while (!operators.isEmpty() && operators.peek() != '(') {
                    postfix = postfix.concat(operators.pop() + "");
                    System.out.println("Se concatena el operador entre paréntesis. Expresión: " + postfix);
                }

                operators.pop();
            } else {
                System.out.println("Fue operador.");

                while (!operators.isEmpty() && lesserEqualPrecedence(c, operators.peek())) {
                    postfix = postfix.concat(operators.pop() + "");
                    System.out.println("Se concatena el operador. Expresión: " + postfix);
                }

                operators.push(c);
            }

            System.out.println();
        }

        if (!operators.isEmpty())
            System.out.println("Final de la expresión alcanzado, se concatenan los contenidos de la pila.");

        while (!operators.isEmpty())
            postfix = postfix.concat(operators.pop() + "");

        return postfix;
    }

    static String infixToPrefix(String infix) {
        System.out.println("Iniciando conversión a Prefija. Expresión: " + infix + "\n");
        String prefix = "";
        CustomStack<Character> operators = new CustomStack<>("Pila operadores");
        operators.setLogging(true);

        for (int i = infix.length() - 1; i >= 0; i--) {
            char c = infix.charAt(i);
            logReading(c);

            if (c == ' ')
                continue;

            if (isOperand(c)) {
                prefix = prefix.concat(c + "");
                System.out.println("Fue operando, se concatena. Expresión actual: " + prefix);
            } else if (c == ')') {
                System.out.println("Fue paréntesis de cierre.");
                operators.push(c);
            } else if (c == '(') {
                System.out.println("Fue paréntesis de apertura.");
                while (!operators.isEmpty() && operators.peek() != ')') {
                    prefix = prefix.concat(operators.pop() + "");
                    System.out.println("Se concatena el operador entre paréntesis. Expresión: " + prefix);
                }

                operators.pop();
            } else {
                System.out.println("Fue operador.");

                while (!operators.isEmpty() && lesserPrecedence(c, operators.peek())) {
                    prefix = prefix.concat(operators.pop() + "");
                    System.out.println("Se concatena el operador. Expresión: " + prefix);
                }

                operators.push(c);
            }

            System.out.println();
        }

        if (!operators.isEmpty())
            System.out.println("Final de la expresión alcanzado, se concatenan los contenidos de la pila.");

        while (!operators.isEmpty()) {
            prefix = prefix.concat(operators.pop() + "");
        }

        System.out.println("Invirtiendo expresión.");
        prefix = new StringBuilder(prefix).reverse().toString();
        return prefix;
    }

    private static boolean lesserPrecedence(char first, char second) {
        System.out.printf("Comprobando si '%s' tiene menor prioridad que '%s'%n", first, second);
        System.out.println(precedenceOf(first) < precedenceOf(second) ? "Lo fue." : "No lo fue.");
        return precedenceOf(first) < precedenceOf(second);
    }

    private static boolean lesserEqualPrecedence(char first, char second) {
        System.out.printf("Comprobando si '%s' tiene menor prioridad que '%s'%n", first, second);
        System.out.println(precedenceOf(first) <= precedenceOf(second) ? "Lo fue." : "No lo fue.");
        return precedenceOf(first) <= precedenceOf(second);
    }

    public static double evaluateInfix(String expression, HashMap<Character, Integer> mappedValues) {
        System.out.println("Evaluando expresión infija.");
        CustomStack<Double> operandsCustomStack = new CustomStack<>("Pila operandos");
        CustomStack<Character> operatorsCustomStack = new CustomStack<>("Pila operadores");

        operandsCustomStack.setLogging(true);
        operandsCustomStack.setLogging(true);

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            logReading(c);

            if (c == ' ')
                continue;

            if (isOperand(c)) {
                System.out.println("Es operando.");
                operandsCustomStack.push(Double.valueOf(mappedValues.get(c)));
            } else if (c == '(') {
                System.out.println("Es paréntesis de apertura.");
                operatorsCustomStack.push(c);
            } else if (c == ')') {
                System.out.println("Es paréntesis de cierre.");
                while (!operatorsCustomStack.isEmpty() && operatorsCustomStack.peek() != '(') {
                    operandsCustomStack.push(
                            applyOperator(
                                    operatorsCustomStack.pop(), operandsCustomStack.pop(),
                                    operandsCustomStack.pop()));
                }
                operatorsCustomStack.pop();
            } else if (isOperator(c)) {
                System.out.println("Es operador.");

//                while (!operatorsCustomStack.isEmpty() && precedenceOf(c) <= precedenceOf(operatorsCustomStack.peek())) {
                while (!operatorsCustomStack.isEmpty() && lesserEqualPrecedence(c, operatorsCustomStack.peek()))
                    operandsCustomStack.push(applyOperator(operatorsCustomStack.pop(), operandsCustomStack.pop(), operandsCustomStack.pop()));

                operatorsCustomStack.push(c);
            }

            System.out.println();
        }

        System.out.println("Se alcanzó el fin de la expresión." +
                "Se aplican los operadores respectivos de la pila de operadores a los elementos de la pila de operandos");

        while (!operatorsCustomStack.isEmpty())
            operandsCustomStack.push(applyOperator(operatorsCustomStack.pop(), operandsCustomStack.pop(), operandsCustomStack.pop()));

        return operandsCustomStack.pop();
    }

    static double evaluatePostfix(String postfix, HashMap<Character, Integer> mappedValues) {
        System.out.println("Evaluando expresión postfija.");
        CustomStack<Double> operands = new CustomStack<>("Pila operandos");
        operands.setLogging(true);

        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            logReading(c);

            if (isOperand(c)) {
                System.out.println("Es operando.");
                operands.push(Double.valueOf(mappedValues.get(c)));
            }
            else if (isOperator(c)) {
                System.out.println("Es operador.");
                operands.push(applyOperator(c, operands.pop(), operands.pop()));
            }

            System.out.println();
        }

        System.out.println("Se alcanzó el final de la expresión.");

        return operands.pop();
    }

    static double evaluatePrefix(String prefix, HashMap<Character, Integer> mappedValues) {
        System.out.println("Evaluando expresión prefija.");
        CustomStack<Character> operatorsCustomStack = new CustomStack<>("Pila operadores");
        CustomStack<Double> operandsCustomStack = new CustomStack<>("Pila operandos");

        operatorsCustomStack.setLogging(true);
        operandsCustomStack.setLogging(true);

        for (int i = prefix.length() - 1; i >= 0; i--) {
            char c = prefix.charAt(i);
            logReading(c);

            if (isOperand(c)) {
                System.out.println("Es operando.");
                operandsCustomStack.push(Double.valueOf(mappedValues.get(c)));
            }
            else if (isOperator(c)) {
                System.out.println("Es operador.");
                operatorsCustomStack.push(c);
                if (operandsCustomStack.getSize() >= 2) {
                    double a = operandsCustomStack.pop();
                    double b = operandsCustomStack.pop();
                    operandsCustomStack.push(applyOperator(operatorsCustomStack.pop(), b, a));
                }
            }

            System.out.println();
        }

        System.out.println("Se alcanzó el fin de la expresión." +
                "Se aplican los operadores respectivos de la pila de operadores a los elementos de la pila de operandos");

        while (!operatorsCustomStack.isEmpty()) {
            double a = operandsCustomStack.pop();
            double b = operandsCustomStack.pop();
            operandsCustomStack.push(applyOperator(operatorsCustomStack.pop(), b, a));
        }

        return operandsCustomStack.pop();
    }

    public static int precedenceOf(Character character) {
        return switch (character) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> 0;
        };
    }

    static boolean isOperator(Character c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    static boolean isOperand(Character c) {
        return c >= 'a' && c <= 'z';
    }

    public static List<Character> getOrderedVars(String expression) {
        ArrayList<Character> characters = new ArrayList<>();
        expression.chars().forEach(c -> {
            if (!characters.contains((char) c) && isOperand((char) c)) {
                characters.add((char) c);
            }
        });

        return characters.stream().sorted().toList();
    }

    public static int ensureInt() {
        int value;
        try {
            value = Main.sc.nextInt();
        } catch (Exception e) {
            System.out.println("Well, that's not an Int, try again");
            return ensureInt();
        }
        return value;
    }

    private static double applyOperator(char operator, double b, double a) {
        System.out.printf("Se aplica el operador '%s' a '%.2f' y '%.2f'%n",
                operator, a, b);
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0)
                    throw new ArithmeticException("Division by zero!");
                yield a / b;
            }
            case '^' -> Math.pow(a, b);
            default -> 0;
        };
    }

    static void markErrorAt(String expression, int index, String error) {
        System.out.println("Error: " + error);
        System.out.println(expression.substring(0, index) + "'" + expression.charAt(index) + "'"
                + expression.substring(index + 1));
        System.out.println(" ".repeat(index) + '^');
        System.out.println(" ".repeat(index) + '|');
        System.out.println(" ".repeat(index) + '|');
    }

    private static void logReading(char element) {
        System.out.println("Leyendo carácter: " + element);
    }
}
