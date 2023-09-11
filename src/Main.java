import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println(Interpreter.isValid("a + a + b - c + a + b") ? "Expression is valid!" : "Expression is not valid!");
        System.out.println(Interpreter.isValid("A + B * (A + (A / C)) + ()") ? "Expression is valid!" : "Expression is not valid!");
        System.out.println(Interpreter.isValid(")A + B * ((((A + (A / C)) + ()") ? "Expression is valid!" : "Expression is not valid!");

        Stack<Integer> stack = new Stack<>();
        stack.push(12);
        stack.push(24);
        stack.push(36);

//        Interpreter.evaluateInfix("A + B * (C - D)");

        String infija = "a+b*c-(d/e+f)^g*h";
        String posfija = Interpreter.infijaToPostfija(infija);
        System.out.println("Expresión infija: " + infija);
        System.out.println("Expresión posfija: " + posfija);

        System.out.println(stack);
        sc.close();
    }
}