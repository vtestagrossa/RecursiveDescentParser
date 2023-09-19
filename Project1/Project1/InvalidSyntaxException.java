/*
 * Vincent Testagrossa
 * Project 1: Recursive Descent Parser
 * 11SEP2022
 * 
 * Requirements: None
 * 
 * Custom exception for handling Parsing errors. The message is preceded with "Invalid Syntax: ".
 */
package Project1;

public class InvalidSyntaxException extends Exception {
    public InvalidSyntaxException(String message){
        super("Invalid Syntax: " + message);
    }
}
