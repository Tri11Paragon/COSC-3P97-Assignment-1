package com.mouseboy.assignment1.parser;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mouseboy.assignment1.R;
import com.mouseboy.assignment1.helpers.Number;
import com.mouseboy.assignment1.helpers.Operator;
import com.mouseboy.assignment1.helpers.Token;
import com.mouseboy.assignment1.helpers.Utils;

import java.util.ArrayList;

// yes this could've been done in a more OOP way but I miss C++ and don't want to Java :3
public class StateFarm {

    // none of this state needs to be stored as it is only ever generated when public methods are called / is reset between calls
    private StringBuilder currentData = new StringBuilder();
    private final AppCompatActivity parent;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int currentToken = 0;

    public StateFarm(AppCompatActivity parent, String restore) {
        currentData.append(restore);
        this.parent = parent;
    }

    public String getString() {
        return currentData.toString();
    }

    public void updateDisplay() {
        ((TextView) parent.findViewById(R.id.output)).setText(currentData.toString());
    }

    /**
     * Backspace function
     */
    public void clearCurrent() {
        checkAndClearExceptions();
        if (currentData.length() == 0)
            return;
        currentData.deleteCharAt(currentData.length() - 1);
        updateDisplay();
    }

    /**
     * Clears the entire string
     */
    public void clearAll() {
        currentData = new StringBuilder();
        updateDisplay();
    }

    /**
     * Adds a number to the formula. Should be a single character
     */
    public void addNumber(String number) {
        tokenize();
        // adds a multiply for you if you add a number after a paren express ()
        if (!tokens.isEmpty() && last() == Operator.ParenRight)
            currentData.append("*");
        currentData.append(number);
        updateDisplay();
    }

    /**
     * Adds an operator to the formula. Should be a single character
     */
    public void addOperator(String operator) {
        tokenize();
        // don't add operators if there is no operands
        if (!tokens.isEmpty()) {
            // Remove unused opening braces
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

    /**
     * Adds a dot to the current number in the formula. Does nothing if it is already a decimal
     */
    public void dot() {
        tokenize();
        // don't do anything if the number already has a dot in it
        if (!tokens.isEmpty() && last() instanceof Number) {
            Number v = (Number) last();
            if (!v.value.contains(".")) {
                currentData.append('.');
                updateDisplay();
            }
        }
    }

    /**
     * Negates the current number or parenthesis expression. removes the negative if it already exists.
     */
    public void neg() {
        tokenize();
        if (tokens.isEmpty())
            return;
        if (last() instanceof Number) {
            // find beginning of number. Should store begins/end inside tokens but meh it's Java
            int v = currentData.length() - 1;
            while (v > 0 &&
                    (Character.isDigit(currentData.charAt(v)) || currentData.charAt(v) == '.')) {
                --v;
            }
            handleNegativeWithSubtract(v, false);
            updateDisplay();
        }
        if (last() == Operator.ParenRight) {
            // same thing as if it is a number but we need to find the matching opening brace to the closing brace we are on
            int outstanding = 0;
            int v = currentData.length() - 1;
            do {
                if (currentData.charAt(v) == ')')
                    outstanding++;
                else if (currentData.charAt(v) == '(')
                    outstanding--;
                v--;
            } while (v > 0 && outstanding != 0);
            handleNegativeWithSubtract(v, outstanding == 0);
            updateDisplay();
        }
    }

    /**
     * Evaluates the current equation, removes the formula and replaces with the result
     */
    public void equals() {
        StringBuilder newData = new StringBuilder();
        try {
            double value = parse();
            newData.append(Utils.formatDecimal(value));
        } catch (Exception e) {
            newData.append(e.getMessage());
            // used as a tombstone to identify if the formula entry has an error message in it
            // plus:
            // https://en.wikipedia.org/wiki/Tilde#Other_uses
            // In modern internet slang, the tilde can be used to signify endearment or love,
            // i.e. "Hello master~". It is commonly used in the furry and femboy communities
            // and can also be used as a diminutive, akin to adding the "ee" sound to the end of a word.[citation needed]
            newData.append("~");
        }
        currentData = newData;
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
        // when determining what kind of parenthesis to add we need to know the current outstanding count.
        // outstanding being the ones which do not have a matching closing bracket
        int outstanding_paren = 0;
        for (Token op : tokens) {
            if (op == Operator.ParenLeft)
                outstanding_paren++;
            else if (op == Operator.ParenRight)
                outstanding_paren--;
        }
        if (last() instanceof Number) {
            // numbers have a special case where we will infer number(expr) means number*(expr)
            // which is fairly standard math and a nice QOL feature
            if (outstanding_paren == 0)
                currentData.append("*(");
            else
                currentData.append(")");
        } else {
            switch ((Operator) last()) {
                // operators can be all the same
                case Plus:
                case Minus:
                case Mul:
                case Div:
                case ParenLeft:
                    // we just want to append a new opening brace
                    currentData.append("(");
                    break;
                case ParenRight:
                    // but if all paren are closed we can assume multiplying a new expression
                    // otherwise we should continue to close the parenthesis
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

    /**
     * Starts the parser
     * @return the evaluated value using precedence of the current formula
     */
    public double parse() {
        tokenize();
        if (currentData.length() == 0)
            throw new RuntimeException("Expression Required");
        return parsePres1();
    }

    /**
     * Evaluate precedence of level 1 (lowest level of precedence)
     */
    private double parsePres1() {
        // get the left hand side of the equation by parsing down a level in the tree.
        // This will be a value an expression
        double v = parsePres2();
        // if after getting the value for the lhs we find a plus or minus, apply the operator
        if (peek() == Operator.Plus) {
            next();
            v += parsePres2();
        } else if (peek() == Operator.Minus) {
            next();
            v -= parsePres2();
        }
        return v;
    }

    /**
     * Evaluate precedence of level 2 (second lowest precedence)
     */
    private double parsePres2() {
        // same as above, lvl 3 contains the terminals though
        double v = parsePres3();
        // same as above; BEDMAS. Divide goes first.
        if (peek() == Operator.Div) {
            next();
            double d = parsePres3();
            if (d == 0)
                throw new ArithmeticException("Cannot divide by zero");
            v /= d;
        } else if (peek() == Operator.Mul) {
            next();
            v *= parsePres3();
        }
        return v;
    }

    /**
     * Evaluate precedence of level 3 (highest precedence)
     */
    private double parsePres3() {
        // look for either an expression, a number, or a minus sign
        if (peek() == Operator.ParenLeft) {
            next(); // consume (
            double d = parsePres1(); // consume expression
            next(); // consume )
            return d;
        } else if (peek() == Operator.Minus) {
            // negating a value
            next();
            return -parsePres3();
        } else if (peek() instanceof Number) {
            double value = Double.parseDouble(((Number) peek()).value);
            next();
            return value;
        } else {
            throw new RuntimeException("Invalid Expression");
        }
    }

    /**
     * Handles adding or removing the negative sign on numbers or (parenthesis groups)
     * @param v one past the start of your value
     * @param matching true if the two open paren are matched, otherwise false.
     */
    private void handleNegativeWithSubtract(int v, boolean matching) {
        // im not able to explain mess of edge cases. just accept it works like i did and move on :3
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

    /**
     * @return next token without moving the parser forward
     */
    private Token peek() {
        // returning null if there is none will result in all equals failing,
        // which will throw an invalid expression at some point.
        if (!hasNext())
            return null;
        return tokens.get(currentToken);
    }

    /**
     * Moves the parser forward one in the token stream
     */
    private void next() {
        currentToken++;
    }

    /**
     * Returns true if there is a next value in the steam
     */
    private boolean hasNext() {
        return currentToken < tokens.size();
    }

    /**
     * @return token at the top of the token stream. useful for input validation.
     */
    private Token last() {
        return tokens.get(tokens.size() - 1);
    }

    /**
     * Checks for an exception then clears it from the screen.
     */
    private void checkAndClearExceptions() {
        // this is why i used the ~. Was the simplest way.
        if (currentData.toString().contains("~")) {
            currentData = new StringBuilder();
            updateDisplay();
        }
    }

    /**
     * Function which handles all the tokenization of the current input expression string.
     */
    private void tokenize() {
        checkAndClearExceptions();
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
                    // find extends of the number then add it as a token.
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

    /**
     * Unused helper functions below
     * -----------------------------
     */
    private void printAllTokens() {
        for (Token t : tokens)
            printToken(t);
    }

    private void printToken(Token t) {
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

}
