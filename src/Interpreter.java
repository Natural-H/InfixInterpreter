import java.util.HashMap;
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
        int parenthesisCounter = 0;
        int lastOpenedParenthesis = 0;
        boolean lastWasOperand = false;

        for (int i = 0; i < normExpression.length(); i++) {
            char element = normExpression.charAt(i);
            if (element == ' ')
                continue;

            if (element == '(') {
                parenthesisCounter++;
                lastOpenedParenthesis = i;
                lastWasOperand = false;
                continue;
            } else if (element == ')') {
                parenthesisCounter--;
                if (parenthesisCounter < 0) {
                    System.out.println(
                            markErrorAt(expression,
                                    expression.contains("(") ? lastOpenedParenthesis : i,
                                    expression.contains("(") ? "'(' has no closing parenthesis" : "Never found a '(' character"));
                    return false;
                }
                continue;
            }

            if ((element >= 'a') && (element <= 'z') && !lastWasOperand) {
                lastWasOperand = true;
                continue;
            }

            if ("+-*/^".contains(element + "") && lastWasOperand)
            {
                lastWasOperand = false;
            } else {
                System.out.println(markErrorAt(
                        expression,
                        i,
                        lastWasOperand ? "Unexpected operand found! Expected: Operator" : "Unexpected operator found! Expected: Operand"
                ));
                return false;
            }
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
