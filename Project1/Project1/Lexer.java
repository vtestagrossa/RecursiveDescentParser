/*
 * Vincent Testagrossa
 * Project 1: Recursive Descent Parser
 * 11SEP2022
 * 
 * Requirements: Input string with the correct grammar.
 * 
 * Provides the user with tokens, or token types. Uses a linked list for representation of the tokens, which provides pop() and peek()
 * methods, instead of using a listIterator which only has a next() method and no peek(). enum Token represents all the possible tokens
 * in the grammar, and provides an easy way to check a token type. Public methods exist to get the current position, check the type,
 * get the next token, check if the lexer has more tokens, and peek.
 */
package Project1;

import java.util.LinkedList;

public class Lexer {
    private LinkedList<String> tokens = new LinkedList<String>();
    private String delims = "(?<=[(),\".;: ])|(?=[(),\".;: ])"; //Delimiters to split the input string into tokens
    private String current, next; //store the current and next tokens.
    private int position = 0;
    enum Token {
        WINDOW, QUOTE, OPEN_PAREN, COMMA, CLOSE_PAREN, END, PERIOD, COLON,
        LAYOUT, LAYOUT_TYPE_FLOW, LAYOUT_TYPE_GRID, WIDGET_BUTTON, SEMICOLON,
        WIDGET_GROUP,WIDGET_LABEL, WIDGET_PANEL, WIDGET_TEXTFIELD, RADIO, STRING, NUMBER
    }

    public Lexer(String input){
        tokens = tokenize(input);
    }
    private LinkedList<String> tokenize(String input){
        //builds the linked list of tokens from the string.
        LinkedList<String> rtnList = new LinkedList<String>();
        for (String token : input.split(delims)){
            if ((!token.strip().equals(""))){    //Strips the space characters as a post tokenization step so delims works correctly.
                rtnList.add(token.strip());
            }
        }
        return rtnList;
    }
    public Boolean hasMoreTokens(){
        //Didn't use this. Wound up handling errors in the Parser.
        return (tokens.peek() != null);
    }
    public String getNextToken(){
        //updates the position and current token
        current = tokens.pop();
        position++;
        return current;
    }
    public String peek(){
        //didn't wind up using this
        next = tokens.peek();
        return next;
    }
    public int getPosition(){
        //returns current token position.
        return position;
    }
    public Token getTokenType(String input){
        /*
         * Returns the token type of the provided token.
         */
        if (input.equals("Window")){
            return Token.WINDOW;
        }
        else if (input.equals("\"")){
            return Token.QUOTE;
        }
        else if (input.equals("(")){
            return Token.OPEN_PAREN;
        }
        else if (isNum(input)){
            return Token.NUMBER;
        }
        else if (input.equals(",")){
            return Token.COMMA;
        }
        else if (input.equals(")")){
            return Token.CLOSE_PAREN;
        }
        else if (input.equals("Layout")){
            return Token.LAYOUT;
        }
        else if (input.equals("Flow")){
            return Token.LAYOUT_TYPE_FLOW;
        }
        else if (input.equals("Grid")){
            return Token.LAYOUT_TYPE_GRID;
        }
        else if (input.equals("Button")){
            return Token.WIDGET_BUTTON;
        }
        else if (input.equals("Group")){
            return Token.WIDGET_GROUP;
        }
        else if (input.equals("Label")){
            return Token.WIDGET_LABEL;
        }
        else if (input.equals("Textfield")){
            return Token.WIDGET_TEXTFIELD;
        }
        else if (input.equals("Radio")){
            return Token.RADIO;
        }
        else if (input.equals(":")){
            return Token.COLON;
        }
        else if (input.equals(";")){
            return Token.SEMICOLON;
        }
        else if (input.equals("Panel")){
            return Token.WIDGET_PANEL;
        }
        else if (input.equals("End")){
            return Token.END;
        }
        else if (input.equals(".")){
            return Token.PERIOD;
        }
        return Token.STRING;
    }
    private boolean isNum(String input){
        //Just a method to check if a string is a number.
        try{
            Integer.parseInt(input);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
