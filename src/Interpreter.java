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

    static String infixToPostfix(String infix) {
        String postfix = "";
        Stack<Character> operators = new Stack<>();
        infix = infix.toLowerCase();

        for (int i = 0; i < infix.length(); i++) {
            char x = infix.charAt(i);

            if (x == ' ')
                continue;

            if ((x >= 'a' && x <= 'z'))
                postfix = postfix.concat(x + "");
            else if (x == '(')
                operators.push(x);
            else if (x == ')') {
                while (!operators.isEmpty() && operators.peek() != '(')
                    postfix = postfix.concat(operators.pop() + "");

                operators.pop();
            } else {
                while (!operators.isEmpty() && precedenceOf.get(x) <= precedenceOf.get(operators.peek()))
                    postfix = postfix.concat(operators.pop() + "");

                operators.push(x);
            }
        }

        while (!operators.isEmpty()) {
            postfix = postfix.concat(operators.pop() + "");
        }

        return postfix;
    }

    public static boolean isValid(String expression) {
        Stack<Integer> parenthesisStack = new Stack<>();
        boolean lastWasOperand = false;
        int lastOperator = 0;

        if (expression.isEmpty()) {
            System.out.println("Error: Expression can't be empty!");
            return false;
        }

        for (int i = 0; i < expression.length(); i++) {
            char element = expression.charAt(i);

            if (element == ' ')
                continue;

            if (element == '(') {
                parenthesisStack.push(i);

                if (lastWasOperand) {
                    System.out.println(
                            markErrorAt(expression, i, "Parenthesis can't be placed without an operator!")
                    );
                    return false;
                }
            } else if (element == ')') {
                if (parenthesisStack.size() - 1 <= -1) {
                    System.out.println(
                            markErrorAt(expression,
                                    i,
                                    expression.contains("(") ? "')' has no opening parenthesis!" : "Never found a '(' character!"));
                    return false;
                }

                if (!lastWasOperand) {
                    System.out.println(
                            markErrorAt(expression, i, "Expression is incomplete!")
                    );
                    return false;
                }

                parenthesisStack.pop();
            } else if ((element >= 'a') && (element <= 'z')) {
                if (lastWasOperand) {
                    System.out.println(markErrorAt(expression, i, "Unexpected operand found! Expected: Operator"));
                    return false;
                }

                lastWasOperand = true;

            } else if ("+-*/^".contains(element + "")) {
                if (!lastWasOperand) {
                    System.out.println(markErrorAt(expression, i, "Unexpected operator found! Expected: Operand"));
                    return false;
                }

                lastWasOperand = false;
                lastOperator = i;
            } else {
                System.out.println(markErrorAt(
                        expression,
                        i,
                        "Invalid character found!")
                );
                return false;
            }
        }

        if (!parenthesisStack.isEmpty()) {
            System.out.println(markErrorAt(
                    expression,
                    parenthesisStack.peek(),
                    "'(' has no closing parenthesis!")
            );
            return false;
        }

        if (!lastWasOperand) {
            System.out.println(markErrorAt(
                    expression,
                    lastOperator,
                    "Expression finishes with operator '%s'!".formatted(expression.charAt(lastOperator))
            ));
            return false;
        }

        System.out.println("Expression is valid!");
        return true;
    }

    public static void evaluateInfix(String expression) {
        StringBuilder builder = new StringBuilder();
        expression.chars().forEach(c -> {
            if (!builder.toString().contains((char) c + "") && (char) c >= 'a' && (char) c <= 'z')
                builder.append((char) c);
        });

        var charArray = builder.toString().toCharArray();
        Arrays.sort(charArray);
        var letters = new String(charArray);

        for (int i = 0; i < letters.length(); i++) {
            System.out.printf("Give me the value of %s: %n", letters.charAt(i));
            expression = expression.replace(letters.charAt(i) + "", Integer.toString(ensureInt()));
        }

        System.out.printf("Result of %s: %.2f%n", expression, computeInfix(expression));

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
            case '^' -> Math.pow(a, b);
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
