import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public class Interpreter {
    public static final Map<Character, Integer> precedenceOf = Map.ofEntries(
            entry('+', 1),
            entry('-', 1),
            entry('*', 2),
            entry('/', 2),
            entry('^', 3),
            entry('(', 0),
            entry(')', 0)
    );

    static String infijaToPostfija(String infija) {
        infija = infija.toLowerCase();
        StringBuilder posfija = new StringBuilder();
        Stack<Character> pila = new Stack<>();

        for (int i = 0; i < infija.length(); i++) {
            char x = infija.charAt(i);

            if ((x >= 'a' && x <= 'z') || (x >= '0' && x <= '9')) {
                posfija.append(x);
            } else if (x == '(') {
                pila.push(x);
            } else if (x == ')') {
                while (!pila.isEmpty() && pila.getTop() != '(') {
                    posfija.append(pila.pop(false));
                }
                pila.pop(false);
            } else {
                while (!pila.isEmpty() && precedenceOf.get(x) <= precedenceOf.get(pila.getTop())) {
                    posfija.append(pila.pop(false));
                }
                pila.push(x);
            }
        }

        while (!pila.isEmpty()) {
            posfija.append(pila.pop(false));
        }

        return posfija.toString();
    }

    public static boolean isValid(String expression) {
        String normExpression = expression.toLowerCase();
        Stack<Integer> parenthesisStack = new Stack<>();
        boolean lastWasOperand = false;
        int parenthesisCounter = 0;

        for (int i = 0; i < normExpression.length(); i++) {
            char element = normExpression.charAt(i);
            if (element == ' ')
                continue;

            if (element == '(') {
                parenthesisStack.push(i);
                parenthesisCounter++;
                lastWasOperand = false;
                continue;
            } else if (element == ')') {
                parenthesisStack.pop(false);
                parenthesisCounter--;
                if (parenthesisCounter < 0) {
                    System.out.println(
                            markErrorAt(expression,
                                    i,
                                    expression.contains("(") ? "')' has no opening parenthesis!" : "Never found a '(' character!"));
                    return false;
                }
                continue;
            }

            if ((element >= 'a') && (element <= 'z') && !lastWasOperand) {
                lastWasOperand = true;
                continue;
            }

            if ("+-*/^".contains(element + "") && lastWasOperand) {
                lastWasOperand = false;
            } else {
                System.out.println(markErrorAt(
                        expression,
                        i,
                        lastWasOperand ? "Unexpected operand found! Expected: Operator" : "Unexpected operator found! Expected: Operand")
                );
                return false;
            }
        }

        if (!parenthesisStack.isEmpty()) {
            System.out.println(markErrorAt(
                    expression,
                    parenthesisStack.getTop(),
                    "'(' has no closing parenthesis!")
            );
            return false;
        }

        return true;
    }

    public static void evaluateInfix(String expression) {
        if (isValid(expression)) {
            StringBuilder builder = new StringBuilder();
            expression.toLowerCase().chars().forEach(c -> {
                if (!builder.toString().contains((char) c + "") && (char) c >= 'a' && (char) c <= 'z')
                    builder.append((char) c);
            });

            var charArray = builder.toString().toCharArray();
            Arrays.sort(charArray);
            var letters = new String(charArray);

            for (int i = 0; i < letters.length(); i++) {
                System.out.printf("Give me the value of %s: %n", letters.charAt(i));
                expression = expression.toLowerCase().replace(letters.charAt(i) + "", Integer.toString(ensureInt()));
            }

            System.out.printf("Result of %s: %.2f%n", expression, computeInfix(expression));
        }
        else {
            System.out.println("Expression is not valid!");
        }
    }

    private static int ensureInt() {
        int value;
        try {
            value = Main.sc.nextInt();
        } catch (Exception e) {
            System.out.println("Well, that's not an Int, try again");
            return ensureInt();
        }
        return value;
    }

    private static double computeInfix(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num.append(expression.charAt(i));
                    i++;
                }
                values.push(Double.parseDouble(num.toString()));
                i--;
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.getTop() != '(') {
                    values.push(applyOperator(operators.pop(false), values.pop(false), values.pop(false)));
                }
                operators.pop(false); // Remove the '('
            } else if ("+-*/^".contains(c + "")) {
                while (!operators.isEmpty() && hasHigherPrecedence(operators.getTop(), c)) {
                    values.push(applyOperator(operators.pop(false), values.pop(false), values.pop(false)));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(false), values.pop(false), values.pop(false)));
        }

        return values.pop(false);
    }

    private static boolean hasHigherPrecedence(char op1, char op2) {
        return precedenceOf.get(op1) > precedenceOf.get(op2);
    }

    private static double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Division by zero!");
                yield a / b;
            }
            default -> 0;
        };
    }

    static String markErrorAt(String expression, int index, String error) {
        return "Error: " + error + '\n' +
                """
                        %s'%c'%s
                        %s %c
                        %s %c
                        %s %c
                        """.formatted(expression.substring(0, index), expression.charAt(index), expression.substring(index + 1),
                        " ".repeat(index), '^',
                        " ".repeat(index), '|',
                        " ".repeat(index), '|');
    }
}
