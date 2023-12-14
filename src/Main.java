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

        System.out.println("\n--------------------------------------------------\n");
        System.out.println("Expresión infija: " + infixExpression);

        System.out.println("\n--------------------------------------------------\n");
        String postfix = Interpreter.infixToPostfix(normExpression);
        System.out.println("Expresión posfija: " + postfix);
        System.out.println("\n--------------------------------------------------\n");

        String prefix = Interpreter.infixToPrefix(normExpression);
        System.out.println("Expresión prefija: " + prefix);
        System.out.println("\n--------------------------------------------------\n");

        System.out.println("Evaluando...");
        var mapped = new HashMap<Character, Integer>();

        for (Character c : Interpreter.getOrderedVars(normExpression)) {
            System.out.println("Dame el valor para " + c + ": ");
            mapped.put(c, Interpreter.ensureInt());
        }

        System.out.println("\n--------------------------------------------------\n");
        System.out.printf("Resultado de la evaluación Infija: %.2f", Interpreter.evaluateInfix(normExpression, mapped));
        System.out.println("\n--------------------------------------------------\n");
        System.out.printf("Resultado de la evaluación Postfija: %.2f", Interpreter.evaluatePostfix(postfix, mapped));
        System.out.println("\n--------------------------------------------------\n");
        System.out.printf("Resultado de la evaluación Prefija: %.2f", Interpreter.evaluatePrefix(prefix, mapped));
        System.out.println("\n--------------------------------------------------\n");

        sc.close();
    }
}