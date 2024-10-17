package com.mouseboy.assignment1.parser;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mouseboy.assignment1.R;
import com.mouseboy.assignment1.helpers.Number;
import com.mouseboy.assignment1.helpers.Operator;
import com.mouseboy.assignment1.helpers.Token;

import java.util.ArrayList;

public class StateFarm {

    private StringBuilder currentData = new StringBuilder();
    private final AppCompatActivity parent;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int currentToken = 0;

    public StateFarm(AppCompatActivity parent, String restore) {
        currentData.append(restore);
        this.parent = parent;
    }

    public void tokenize() {
        currentToken = 0;
        tokens.clear();
        for (int i = 0; i < currentData.length(); i++) {
            char c = currentData.charAt(i);
            switch (c) {
                case '*':
                    tokens.add(Operator.Mul);
                    break;
                case '+':
                    tokens.add(Operator.Plus);
                    break;
                case '-':
                    tokens.add(Operator.Minus);
                    break;
                case '÷':
                    tokens.add(Operator.Div);
                    break;
                case '(':
                    tokens.add(Operator.ParenLeft);
                    break;
                case ')':
                    tokens.add(Operator.ParenRight);
                    break;
                default:
                    StringBuilder data = new StringBuilder();
                    while (Character.isDigit(c) || c == '.') {
                        data.append(c);
                        if (++i >= currentData.length())
                            break;
                        c = currentData.charAt(i);
                    }
                    --i;
                    tokens.add(new Number(data.toString()));
                    break;
            }
        }
    }

    public Token peek() {
        if (!hasNext())
            return null;
        return tokens.get(currentToken);
    }

    public void next() {
        currentToken++;
    }

    public boolean hasNext() {
        return currentToken < tokens.size();
    }

    public Token last() {
        return tokens.get(tokens.size() - 1);
    }

    public String getString() {
        return currentData.toString();
    }

    public void clearCurrent() {
        if (currentData.length() == 0)
            return;
        currentData.deleteCharAt(currentData.length() - 1);
        updateDisplay();
    }

    public void clearAll() {
        currentData = new StringBuilder();
        updateDisplay();
    }

    public void addNumber(String number) {
        tokenize();
        // adds a multiply for you if you add a number after a paren express ()
        if (!tokens.isEmpty() && last() == Operator.ParenRight)
            currentData.append("*");
        currentData.append(number);
        updateDisplay();
    }

    public void addOperator(String operator) {
        tokenize();
        if (!tokens.isEmpty()) {
            // changes operators if you haven't typed an expression
            while (last() == Operator.ParenLeft) {
                currentData.deleteCharAt(currentData.length() - 1);
                tokens.remove(tokens.size() - 1);
            }
            // decimal needs to be completed (we will remove it for you :3)
            if (last() instanceof Number) {
                if (((Number) last()).value.endsWith("."))
                    currentData.deleteCharAt(currentData.length() - 1);
                // changes operators if you haven't typed an expression
            } else if (last() == Operator.Div || last() == Operator.Mul
                    || last() == Operator.Minus || last() == Operator.Plus) {
                currentData.deleteCharAt(currentData.length() - 1);
            }
            currentData.append(operator);
            updateDisplay();
        }
    }

    public void dot() {
        tokenize();
        if (!tokens.isEmpty() && last() instanceof Number) {
            Number v = (Number) last();
            if (!v.value.contains(".")) {
                currentData.append('.');
                updateDisplay();
            }
        }
    }

    private void handleNegativeWithSubtract(int v, boolean matching) {
        if (currentData.charAt(v) == '-') {
            if (v == 0)
                currentData.deleteCharAt(0);
            else {
                char c = currentData.charAt(v - 1);
                if (!(Character.isDigit(c) || c == ')'))
                    currentData.deleteCharAt(v);
                else
                    currentData.insert(v, '-');
            }
        } else {
            char c = currentData.charAt(v);
            if (matching && c == '(' && currentData.charAt(v + 1) == '(') {
                currentData.insert(v + 1, '-');
            } else {
                if (!(Character.isDigit(c) || c == '('))
                    currentData.insert(v + 1, '-');
                else
                    currentData.insert(v, '-');
            }
        }
    }

    public void neg() {
        tokenize();
        if (tokens.isEmpty())
            return;
        if (last() instanceof Number) {
            int v = currentData.length() - 1;
            while (v > 0 &&
                    (Character.isDigit(currentData.charAt(v)) || currentData.charAt(v) == '.')) {
                --v;
            }
            handleNegativeWithSubtract(v, false);
            updateDisplay();
        }
        if (last() == Operator.ParenRight) {
            int outstanding = 0;
            int v = currentData.length() - 1;
            do {
                if (currentData.charAt(v) == ')')
                    outstanding++;
                else if (currentData.charAt(v) == '(')
                    outstanding--;
                v--;
            } while (v > 0 && outstanding != 0);
            for (int i = v; i < currentData.length(); i++)
                System.out.println(currentData.charAt(i));
            handleNegativeWithSubtract(v, outstanding == 0);
            updateDisplay();
        }
    }

    public void equals() {
        double value = parse();
        currentData = new StringBuilder();
        currentData.append(value);
        updateDisplay();
    }

    public void paren() {
        // edge case for no input
        if (currentData.length() == 0) {
            currentData.append("(");
            updateDisplay();
            return;
        }
        tokenize();
        // decimal needs to be completed ( we will remove it for you :3 )
        if (last() instanceof Number) {
            if (((Number) last()).value.endsWith(".")) {
                currentData.deleteCharAt(currentData.length() - 1);
                tokenize();
            }
        }
        int outstanding_paren = 0;
        for (Token op : tokens) {
            if (op == Operator.ParenLeft)
                outstanding_paren++;
            else if (op == Operator.ParenRight)
                outstanding_paren--;
        }
        if (last() instanceof Number) {
            if (outstanding_paren == 0)
                currentData.append("*(");
            else
                currentData.append(")");
        } else {
            switch ((Operator) last()) {
                case Plus:
                case Minus:
                case Mul:
                case Div:
                case ParenLeft:
                    currentData.append("(");
                    break;
                case ParenRight:
                    if (outstanding_paren == 0)
                        currentData.append("*(");
                    else
                        currentData.append(")");
                    break;
            }
        }
        updateDisplay();
    }

    // BRING ON THE 2P05!
    // Pseudo EBNF from what i can remember:
    // Expr := Pres2 '+' Pres2 | Pres2 '-' Pres2
    // Pres2 := Pres3 '*' Pres3 | Pres3 '/' Pres3
    // Pres3 := '-'Pres3 | Number | '('Expr')'
    //
    // This implements operator precedence because the tree is expanding such that expressions
    // are parsed from the bottom up.
    // So Pres3 gets evaluated first followed by Pres2 followed by Expr (Pres1).
    // I hope that makes sense? I wanted to evaluate this as an iterative stack machine
    // (similar to my how GP library handles things https://github.com/Tri11Paragon/blt-gp)
    // (https://github.com/Tri11Paragon/blt-gp/blob/094fa76b5823f81f653ef8b4065cd15d7501cfec/include/blt/gp/program.h#L143C1-L153C22)
    // (yes i do realize this code ^ makes zero sense without a complex understanding of the library internals)

    public double parse() {
        tokenize();
        return parse_pres_1();
    }

    private double parse_pres_1() {
        double v = parse_pres_2();
        if (peek() == Operator.Plus) {
            next();
            v += parse_pres_2();
        } else if (peek() == Operator.Minus) {
            next();
            v -= parse_pres_2();
        }
        return v;
    }

    private double parse_pres_2() {
        double v = parse_pres_3();
        if (peek() == Operator.Mul) {
            next();
            v *= parse_pres_3();
        } else if (peek() == Operator.Div) {
            next();
            double d = parse_pres_3();
            if (d == 0)
                throw new ArithmeticException("Cannot divide by zero!");
            v /= d;
        }
        return v;
    }

    private double parse_pres_3() {
        if (peek() == Operator.ParenLeft) {
            next(); // consume (
            double d = parse_pres_1(); // consume expression
            next(); // consume )
            return d;
        } else if (peek() == Operator.Minus) {
            // negating a value
            next();
            return -parse_pres_3();
        } else if (peek() instanceof Number) {
            double value = Double.parseDouble(((Number) peek()).value);
            next();
            return value;
        } else {
            throw new RuntimeException("There was an error parsing the expression!");
        }
    }

    public void printAllTokens() {
        for (Token t : tokens)
            printToken(t);
    }

    public void printToken(Token t) {
        if (t instanceof Number)
            System.out.println(((Number) t).value);
        else {
            switch ((Operator) t) {
                case Plus:
                    System.out.println("+");
                    break;
                case Minus:
                    System.out.println("-");
                    break;
                case Mul:
                    System.out.println("*");
                    break;
                case Div:
                    System.out.println("/");
                    break;
                case ParenLeft:
                    System.out.println("(");
                    break;
                case ParenRight:
                    System.out.println(")");
                    break;
            }
        }
    }

    public void updateDisplay() {
        ((TextView) parent.findViewById(R.id.output)).setText(currentData.toString());
    }

}
