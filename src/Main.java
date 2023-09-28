import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String infixExpression, normExpression;
        do {
            System.out.print("Give me an expression: ");
            infixExpression = sc.nextLine();
            normExpression = infixExpression.toLowerCase();
        } while (!Interpreter.isValid(normExpression));

        System.out.println("Expresión infija: " + infixExpression);

        String postfix = Interpreter.infixToPostfix(normExpression);
        System.out.println("Expresión posfija: " + postfix);

        String prefix = Interpreter.infixToPrefix(normExpression);
        System.out.println("Expresión prefija: " + prefix);

        System.out.println("Evaluando...");
//        Interpreter.evaluateInfix(normExpression);

        var mapped = new HashMap<Character, Integer>();

        for (Character a : Interpreter.yeah(normExpression)) {
            mapped.put(a, Interpreter.ensureInt());
        }

        System.out.println(Interpreter.evaluatePostfix(postfix, mapped));

        sc.close();
    }
}