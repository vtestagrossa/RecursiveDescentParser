/*
 * Vincent Testagrossa
 * Project 1: Recursive Descent Parser
 * 11SEP2022
 * 
 * Requirements: Parser object, and an input file with a format that matches the grammar.
 * 
 * This class creates a JFileChooser and reads a file in to construct the Parser object. 
 */
package Project1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

public class Main {
    static File f = new File(System.getProperty("user.dir")); //default directory is current working directory for the project.
    static JFileChooser file = new JFileChooser(f); //set the JFileChooser to use the previous directory as the default.
    static String inputStr; //inputStr for the file that's read-in.
    public static void main(String args[]){
        inputStr = readFile();
        try{
            Parser parser = new Parser(inputStr);
            parser.parse();
        }
        catch (InvalidSyntaxException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    /*
     * Builds a string from file using a StringBuilder object, to be passed to the parser, which will pass it to the lexer.
     */
    public static String readFile(){
        /*
         * Builds a string from file input selected from the JFileChooser 'file' and outputs a string for use in the Parser class.
         */
        StringBuilder outputStr = new StringBuilder();

        //open the file chooser in the default directory.
        int ret = file.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION){
            f = file.getSelectedFile();
        }

        try{
            //Builds the outputStr as a string from a file.
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            while ((line = reader.readLine()) != null){
                outputStr.append(line);
            }
            reader.close();

        }
        catch(FileNotFoundException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return outputStr.toString();
    }
}
