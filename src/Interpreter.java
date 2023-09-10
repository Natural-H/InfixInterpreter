import java.util.Map;
import static java.util.Map.entry;

public class Interpreter {
    public static final Map<String, Integer> priorityOf = Map.ofEntries(
            entry("+", 1),
            entry("-", 1),
            entry("*", 2),
            entry("/", 2),
            entry("^", 3)
    );

    static boolean isValid(String expression) {
        String normExpression = expression.toLowerCase();
        Stack<Integer> parenthesisStack = new Stack<>();
        boolean lastWasOperand = false;

        for (int i = 0; i < normExpression.length(); i++) {
            char element = normExpression.charAt(i);
            if (element == ' ')
                continue;

            if (element == '(') {
                parenthesisStack.push(i);
                lastWasOperand = false;
                continue;
            } else if (element == ')') {
                parenthesisStack.pop(false);
                if ((parenthesisStack.getUsed() - 1) < 0) {
                    System.out.println(
                            markErrorAt(expression,
                                    i,
                                    expression.contains("(") ? "')' has no opening parenthesis" : "Never found a '(' character"));
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
