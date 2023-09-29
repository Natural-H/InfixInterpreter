import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String infixExpression, normExpression;
        do {
            System.out.print("Dame una expresión: ");
            infixExpression = sc.nextLine();
            normExpression = infixExpression.toLowerCase();
        } while (!Interpreter.isValid(normExpression));

        System.out.println("Expresión infija: " + infixExpression);

        String postfix = Interpreter.infixToPostfix(normExpression);
        System.out.println("Expresión posfija: " + postfix);

        String prefix = Interpreter.infixToPrefix(normExpression);
        System.out.println("Expresión prefija: " + prefix);

        System.out.println("Evaluando...");
        var mapped = new HashMap<Character, Integer>();

        for (Character c : Interpreter.getOrderedVars(normExpression)) {
            System.out.println("Dame el valor para " + c + ": ");
            mapped.put(c, Interpreter.ensureInt());
        }

        System.out.println(Interpreter.evaluateInfix(normExpression, mapped));
        System.out.println(Interpreter.evaluatePostfix(postfix, mapped));
        System.out.println(Interpreter.evaluatePrefix(prefix, mapped));

        sc.close();
    }
}