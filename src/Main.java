import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        String infixExpression, normExpression;
        do {
            System.out.print("Give me an expression: ");
            infixExpression = sc.nextLine();
            normExpression = infixExpression.toLowerCase();
            System.out.println("Do something!");
        } while (!Interpreter.isValid(infixExpression));

        System.out.println("Expresión infija: " + infixExpression);
        Interpreter.evaluateInfix(normExpression);

        String postfix = Interpreter.infixToPostfix(normExpression);
        System.out.println("Expresión posfija: " + postfix);

        sc.close();
    }
}