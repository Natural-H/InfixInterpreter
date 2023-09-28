import java.util.*;

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

    static double evaluatePostfix(String postfix, HashMap<Character, Integer> mappedValues) {
        Stack<Double> operands = new Stack<>();

        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);

            if (c >= 'a' && c <= 'z')
                operands.push(Double.valueOf(mappedValues.get(c)));
            else if ("+-*/^".contains(c + ""))
                operands.push(applyOperator(c, operands.pop(), operands.pop()));
        }

        return operands.pop();
    }

    static double evaluatePrefix(String prefix, HashMap<Character, Integer> mappedValues) {
        Stack<Character> operators = new Stack<>();
        Stack<Double> operands = new Stack<>();

        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);

            if (c >= 'a' && c <= 'z') {
                operands.push(Double.valueOf(mappedValues.get(c)));
                if (operands.size() >= 2) {
                    operands.push(applyOperator(operators.pop(), operands.pop(), operands.pop()));
                }
            } else if ("+-*/^".contains(c + ""))
                operators.push(c);
        }

        return operands.pop();
    }

    static String infixToPostfix(String infix) {
        String postfix = "";
        Stack<Character> operators = new Stack<>();

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

    static String infixToPrefix(String infix) {
        String prefix = "";
        Stack<Character> operators = new Stack<>();

        for (int i = infix.length() - 1; i >= 0; i--) {
            char x = infix.charAt(i);

            if (x == ' ')
                continue;

            if ((x >= 'a' && x <= 'z'))
                prefix = prefix.concat(x + "");
            else if (x == ')')
                operators.push(x);
            else if (x == '(') {
                while (!operators.isEmpty() && operators.peek() != ')')
                    prefix = prefix.concat(operators.pop() + "");

                operators.pop();
            } else {
                while (!operators.isEmpty() && precedenceOf.get(x) <= precedenceOf.get(operators.peek()))
                    prefix = prefix.concat(operators.pop() + "");

                operators.push(x);
            }
        }

        while (!operators.isEmpty()) {
            prefix = prefix.concat(operators.pop() + "");
        }

        prefix = new StringBuilder(prefix).reverse().toString();
        return prefix;
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
        String lettersList = getSortedVariables(expression);
        String expressionToShow = expression;

        for (int i = 0; i < lettersList.length(); i++) {
            System.out.printf("Give me the value of %s: %n", lettersList.charAt(i));

            int numericValue = ensureInt();
            expressionToShow = expressionToShow.replace(lettersList.charAt(i) + "",
                    numericValue < 0 ? "(%d)".formatted(numericValue) :
                            Integer.toString(numericValue));

            expression = expression.replace(lettersList.charAt(i) + "",
                    numericValue < 0 ? "(0 - %d)".formatted(-numericValue) :
                            Integer.toString(numericValue));
        }

        System.out.printf("Result of %s: %.2f%n", expressionToShow, computeInfix(expression));
    }

    public static String getSortedVariables(String expression) {
        StringBuilder builder = new StringBuilder();
        expression.chars().forEach(c -> {
            if (!builder.toString().contains((char) c + "") && (char) c >= 'a' && (char) c <= 'z')
                builder.append((char) c);
        });

        var charArray = builder.toString().toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }

    public static List<Character> yeah(String expression) {
        ArrayList<Character> characters = new ArrayList<>();
        expression.chars().forEach(c -> {
            if (!characters.contains((char) c) && c >= 'a' && c <= 'z') {
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

    private static double computeInfix(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ')
                continue;

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
                while (!operators.isEmpty() && hasHigherOrEqualPrecedence(operators.peek(), c)) {
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

    private static boolean hasHigherOrEqualPrecedence(char op1, char op2) {
        return precedenceOf.get(op1) >= precedenceOf.get(op2);
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
