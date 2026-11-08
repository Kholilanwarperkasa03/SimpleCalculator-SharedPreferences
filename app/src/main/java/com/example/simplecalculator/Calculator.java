package com.example.simplecalculator;

public class Calculator {

    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Tidak bisa membagi dengan nol");
        } else {
            return a / b;
        }
    }

    public double calculate(double a, double b, String operator) {
        double result;
        switch (operator) {
            case "+":
                result = add(a, b);
                break;
            case "-":
            case "−":
                result = subtract(a, b);
                break;
            case "×":
                result = multiply(a, b);
                break;
            case "÷":
                result = divide(a, b);
                break;
            default:
                throw new IllegalArgumentException("Operator tidak dikenali: " + operator);
        }
        return result;
    }
}