import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println(Interpreter.isValid("a + a + b - c + a + b") ? "Expression is valid!" : "Expression is not valid!");
        System.out.println(Interpreter.isValid("A + B * (A + (A / C)) + ()") ? "Expression is valid!" : "Expression is not valid!");
        System.out.println(Interpreter.isValid("A + B * ((((A + (A / C)) + ()") ? "Expression is valid!" : "Expression is not valid!");

        Interpreter.evaluateInfix("A + B * (C - D)");

        String infix = "a+b*c-(d/e+f)^g*h";
        String postfix = Interpreter.infijaToPostfija(infix);
        System.out.println("Expresión infija: " + infix);
        System.out.println("Expresión posfija: " + postfix);

        sc.close();
    }
}