import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

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
        String posfija = "";
        Stack<Character> operators = new Stack<>();
        infija = infija.toLowerCase();

        for (int i = 0; i < infija.length(); i++) {
            char x = infija.charAt(i);

            if ((x >= 'a' && x <= 'z') || (x >= '0' && x <= '9')) {
                posfija = posfija.concat(x + "");
            } else if (x == '(') {
                operators.push(x);
            } else if (x == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    posfija = posfija.concat(operators.pop() + "");
                }
                operators.pop();
            } else {
                while (!operators.isEmpty() && precedenceOf.get(x) <= precedenceOf.get(operators.peek())) {
                    posfija = posfija.concat(operators.pop() + "");
                }
                operators.push(x);
            }
        }

        while (!operators.isEmpty()) {
            posfija = posfija.concat(operators.pop() + "");
        }

        return posfija;
    }

    public static boolean isValid(String expression) {
        String normExpression = expression.toLowerCase();
        Stack<Integer> parenthesisStack = new Stack<>();
        boolean lastWasOperand = false;
//        int parenthesisCounter = 0;
        int lastOperator = 0;

        for (int i = 0; i < normExpression.length(); i++) {
            char element = normExpression.charAt(i);
//            parenthesisCounter++;
            if (element == '(') {
                parenthesisStack.push(i);
                lastWasOperand = false;
            } else if (element == ')') {
//                parenthesisCounter--;
//                if (parenthesisCounter < 0) {
                if (parenthesisStack.size() - 1 <= -1) {
                    System.out.println(
                            markErrorAt(expression,
                                    i,
                                    expression.contains("(") ? "')' has no opening parenthesis!" : "Never found a '(' character!"));
                    return false;
                }
                parenthesisStack.pop();
                lastWasOperand = true;
            } else if ((element >= 'a') && (element <= 'z') && !lastWasOperand) {
                lastWasOperand = true;
            } else if ("+-*/^".contains(element + "") && lastWasOperand) {
                lastWasOperand = false;
                lastOperator = i;
            } else if (element != ' ') {
                System.out.println(markErrorAt(
                        expression,
                        i,
                        lastWasOperand ? "Unexpected operand found! Expected: Operator" : "Unexpected operator found! Expected: Operand")
                );
                return false;
            }
        }

//        if (parenthesisCounter != 0) {
        if (!parenthesisStack.isEmpty()) {
            System.out.println(markErrorAt(
                    expression,
                    parenthesisStack.peek(),
                    "'(' has no closing parenthesis!")
            );
            return false;
        }

        if (!lastWasOperand)
        {
            System.out.println(markErrorAt(
                    expression,
                    lastOperator,
                    "Expression finishes with operator '%s'!".formatted(expression.charAt(lastOperator))
            ));
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
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Remove the '('
            } else if ("+-*/^".contains(c + "")) {
                while (!operators.isEmpty() && hasHigherPrecedence(operators.peek(), c)) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
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
