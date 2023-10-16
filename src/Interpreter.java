import java.util.*;

public class Interpreter {
    public static boolean isValid(String expression) {
        Stack<Integer> parenthesisStack = new Stack<>();
        boolean lastWasOperand = false;
        int lastOperator = 0;

        if (expression.isEmpty()) {
            System.out.println("Error: Expression can't be empty!");
            return false;
        }

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ')
                continue;

            if (c == '(') {
                parenthesisStack.push(i);

                if (lastWasOperand) {
                    markErrorAt(expression, i, "Parenthesis can't be placed without an operator!");
                    return false;
                }
            } else if (c == ')') {
                if (parenthesisStack.isEmpty()) {
                    markErrorAt(expression, i,
                            expression.contains("(") ? "')' has no opening parenthesis!"
                                    : "Never found a '(' character!");
                    return false;
                }

                if (!lastWasOperand) {
                    markErrorAt(expression, i, "Expression is incomplete!");
                    return false;
                }

                parenthesisStack.pop();
            } else if (isOperand(c)) {
                if (lastWasOperand) {
                    markErrorAt(expression, i, "Unexpected operand found! Expected: Operator");
                    return false;
                }

                lastWasOperand = true;
            } else if (isOperator(c)) {
                if (!lastWasOperand) {
                    markErrorAt(expression, i, "Unexpected operator found! Expected: Operand");
                    return false;
                }

                lastWasOperand = false;
                lastOperator = i;
            } else {
                markErrorAt(expression, i, "Invalid character found!");
                return false;
            }
        }

        if (!parenthesisStack.isEmpty()) {
            markErrorAt(expression, parenthesisStack.peek(), "'(' has no closing parenthesis!");
            return false;
        }

        if (!lastWasOperand) {
            markErrorAt(expression, lastOperator,
                    "Expression finishes with operator '" + expression.charAt(lastOperator) + "'!");
            return false;
        }

        System.out.println("Expression is valid!");
        return true;
    }

    static String infixToPostfix(String infix) {
        String postfix = "";
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (c == ' ')
                continue;

            if (isOperand(c))
                postfix = postfix.concat(c + "");
            else if (c == '(')
                operators.push(c);
            else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(')
                    postfix = postfix.concat(operators.pop() + "");

                operators.pop();
            } else {
                while (!operators.isEmpty() && precedenceOf(c) <= precedenceOf(operators.peek()))
                    postfix = postfix.concat(operators.pop() + "");

                operators.push(c);
            }
        }

        while (!operators.isEmpty())
            postfix = postfix.concat(operators.pop() + "");

        return postfix;
    }

    static String infixToPrefix(String infix) {
        String prefix = "";
        Stack<Character> operators = new Stack<>();

        for (int i = infix.length() - 1; i >= 0; i--) {
            char c = infix.charAt(i);

            if (c == ' ')
                continue;

            if (isOperand(c))
                prefix = prefix.concat(c + "");
            else if (c == ')')
                operators.push(c);
            else if (c == '(') {
                while (!operators.isEmpty() && operators.peek() != ')')
                    prefix = prefix.concat(operators.pop() + "");

                operators.pop();
            } else {
                while (!operators.isEmpty() && precedenceOf(c) < precedenceOf(operators.peek()))
                    prefix = prefix.concat(operators.pop() + "");

                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            prefix = prefix.concat(operators.pop() + "");
        }

        prefix = new StringBuilder(prefix).reverse().toString();
        return prefix;
    }

    public static double evaluateInfix(String expression, HashMap<Character, Integer> mappedValues) {
        Stack<Double> operandsStack = new Stack<>();
        Stack<Character> operatorsStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ')
                continue;

            if (isOperand(c))
                operandsStack.push(Double.valueOf(mappedValues.get(c)));
            else if (c == '(')
                operatorsStack.push(c);
            else if (c == ')') {
                while (!operatorsStack.isEmpty() && operatorsStack.peek() != '(')
                    operandsStack.push(applyOperator(operatorsStack.pop(), operandsStack.pop(), operandsStack.pop()));
                operatorsStack.pop();
            } else if (isOperator(c)) {
                while (!operatorsStack.isEmpty() && precedenceOf(operatorsStack.peek()) >= precedenceOf(c))
                    operandsStack.push(applyOperator(operatorsStack.pop(), operandsStack.pop(), operandsStack.pop()));
                operatorsStack.push(c);
            }
        }

        while (!operatorsStack.isEmpty())
            operandsStack.push(applyOperator(operatorsStack.pop(), operandsStack.pop(), operandsStack.pop()));

        return operandsStack.pop();
    }

    static double evaluatePostfix(String postfix, HashMap<Character, Integer> mappedValues) {
        Stack<Double> operands = new Stack<>();

        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);

            if (isOperand(c))
                operands.push(Double.valueOf(mappedValues.get(c)));
            else if (isOperator(c))
                operands.push(applyOperator(c, operands.pop(), operands.pop()));
        }

        return operands.pop();
    }

    static double evaluatePrefix(String prefix, HashMap<Character, Integer> mappedValues) {
        Stack<Character> operatorsStack = new Stack<>();
        Stack<Double> operandsStack = new Stack<>();

        for (int i = prefix.length() - 1; i >= 0; i--) {
            char c = prefix.charAt(i);

            if (isOperand(c))
                operandsStack.push(Double.valueOf(mappedValues.get(c)));
            else if (isOperator(c)) {
                operatorsStack.push(c);
                if (operandsStack.getSize() >= 2) {
                    double a = operandsStack.pop();
                    double b = operandsStack.pop();
                    operandsStack.push(applyOperator(operatorsStack.pop(), b, a));
                }
            }
        }

        while (!operatorsStack.isEmpty()) {
            double a = operandsStack.pop();
            double b = operandsStack.pop();
            operandsStack.push(applyOperator(operatorsStack.pop(), b, a));
        }

        return operandsStack.pop();
    }

    public static int precedenceOf(Character character) {
        switch (character) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
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
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Division by zero!");
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                return 0;
        }
    }

    static void markErrorAt(String expression, int index, String error) {
        System.out.println("Error: " + error);
        System.out.println(expression.substring(0, index) + "'" + expression.charAt(index) + "'"
                + expression.substring(index + 1));
        System.out.println(" ".repeat(index) + '^');
        System.out.println(" ".repeat(index) + '|');
        System.out.println(" ".repeat(index) + '|');
    }
}
