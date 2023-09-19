/*
 * Vincent Testagrossa
 * Project 1: Recursive Descent Parser
 * 11SEP2022
 * 
 * Requirements: Input string with the correct grammar and a Lexer object to provide the lexemes/tokens.
 * 
 * Uses the lexer to provide it with lexemes and tokens to build a GUI with selected objects and widgets. Each production in the grammar
 * has it's own method, and recursive productions are called recursively.
 */
package Project1;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import java.awt.*;
import java.util.NoSuchElementException;

import Project1.Lexer.Token;

public class Parser{
    private String errorMsg = "", currentToken = ""; //references to the current token and error message.
    private Token currentType, expectedType; //Current token type and which type is expected.
    private Lexer lexer; //provides the lexemes/tokens and some other functionality, like the token type.
    JFrame window = new JFrame(); //frame for the main window
    JPanel main = new JPanel(); //top level panel to attach all the components to
    public Parser(String input) throws InvalidSyntaxException{
        /*
         * Accepts a string to pass to the Lexer. The lexer tokenizes the input and returns individual tokens to the
         * parser, which handles the production logic.
         */
        lexer = new Lexer(input);
    }

    public void parse()throws InvalidSyntaxException{
        /*
         * Checks if the main parse logic was successful. If it fails, formats the error message and reports the current token,
         * expected token, and token position. Successful parsing will set the window to visible.
         */
        if (!parseWindow()){
            errorMsg = "[" + currentToken +"]" + " not valid. [" + expectedType + "] expected at position " + lexer.getPosition(); 
            throw new InvalidSyntaxException(errorMsg);
        }
        else{
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
        }
    }
    private void getNext()throws InvalidSyntaxException{
        /*
         * Mostly just to update the current token and type after a terminal symbol has been processed. Reduces the amount of
         * code repetition through the project, while allowing some fine control over when and where the tokens are updated.
         * 
         * Detects when the file is empty, or too short.
         */
        try{
            currentToken = lexer.getNextToken();
            currentType = lexer.getTokenType(currentToken);
        }
        catch (NullPointerException ex){
            errorMsg = "Reached end of file at [" + currentToken +"]" + ". [" + expectedType + "] expected. " + lexer.getPosition();
            throw new InvalidSyntaxException(errorMsg);
        }
        catch (NoSuchElementException ex){
            errorMsg = "Reached end of file at [" + currentToken +"]" + ". [" + expectedType + "] expected. " + lexer.getPosition();
            throw new InvalidSyntaxException(errorMsg);
        }
        
    }
    private boolean checkToken(Token type){
        //checks the current type of token and sets the expected type.
        if (lexer.getTokenType(currentToken) == setExpectedType(type)){
            return true;
        }
        return false;
    }
    private Token setExpectedType(Token type){
        //sets the expected token type
        expectedType = type;
        return expectedType;
    }
    private boolean parseWindow() throws InvalidSyntaxException{
        /*
         * Handles the main program logic for the Window production. Sets the height and width of the window and then 
         * traverses the control logic until another production is reached, which is called through it's own method.
         */
        int width = 0, height = 0;
        getNext();
        if (checkToken(Token.WINDOW)){
            getNext();
            if (checkToken(Token.QUOTE)){
                getNext();
                //Token was a string
                if (checkToken(Token.STRING) || 
                    checkToken(Token.NUMBER)){
                    window.setTitle(currentToken);
                    getNext();
                    //End of string
                    if(checkToken(Token.QUOTE)){
                        getNext();
                        //Begin Window Size
                        if(checkToken(Token.OPEN_PAREN)){
                            getNext();
                            if(checkToken(Token.NUMBER)){
                                width = Integer.parseInt(currentToken);
                                getNext();
                                if(checkToken(Token.COMMA)){
                                    getNext();
                                    if(checkToken(Token.NUMBER)){
                                        height = Integer.parseInt(currentToken);
                                        window.setSize(width, height);
                                        main.setSize(width, height);
                                        window.add(main);
                                        getNext();
                                        //End of window size
                                        if(checkToken(Token.CLOSE_PAREN)){
                                            getNext();
                                            //First production Layout
                                            if (parseLayout(main)){
                                                getNext();
                                                //Next Production Widgets
                                                if (parseWidgets(main)){
                                                    if(checkToken(Token.END)){
                                                        getNext();
                                                        //Reached the end of the input file.
                                                        if(checkToken(Token.PERIOD)){
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                //token was an empty string
                }
                else if (checkToken(Token.QUOTE)){
                    getNext();
                    //Begin Window Size
                    if(checkToken(Token.OPEN_PAREN)){
                        getNext();
                        if(checkToken(Token.NUMBER)){
                            width = Integer.parseInt(currentToken);                                  
                            getNext();
                            if(checkToken(Token.COMMA)){
                                getNext();
                                if(checkToken(Token.NUMBER)){
                                    height = Integer.parseInt(currentToken);
                                    window.setSize(width, height);
                                    main.setSize(width, height);
                                    window.add(main);
                                    getNext();
                                    //End Window Size
                                    if(checkToken(Token.CLOSE_PAREN)){
                                        getNext();
                                        //First Production Layout
                                        if (parseLayout(main)){
                                            getNext();
                                            //Next Production Widgets
                                            if (parseWidgets(main)){
                                                getNext();
                                                if(checkToken(Token.END)){
                                                    getNext();
                                                    //Reached the end of the file
                                                    if(checkToken(Token.PERIOD)){
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean parseLayout(Container parent) throws InvalidSyntaxException{
        //First production for the Layout which calls the Layout Type, then looks for a colon.
        if(parseLayout_Type(parent)){
            getNext();
            if (checkToken(Token.COLON)){
                return true;
            }
        }        
        return false;
    }
    private boolean parseLayout_Type(Container parent) throws InvalidSyntaxException{
        //Checks which layout type and if Flow, sets the layout to the parent Container object.
        //If it's a grid, determines the sizing, or the sizing and spacing and sets the layout to the parent Container object.
        int rows = 1, cols = 1, hgap = 1, vgap = 1;
        if (checkToken(Token.LAYOUT)){
            getNext();
            if (checkToken(Token.LAYOUT_TYPE_FLOW)){
                parent.setLayout(new FlowLayout());
                return true;
            }
            else if (checkToken(Token.LAYOUT_TYPE_GRID)){
                getNext();
                if (checkToken(Token.OPEN_PAREN)){
                    getNext();
                    if (checkToken(Token.NUMBER)){
                        rows = Integer.parseInt(currentToken);
                        getNext();
                        if (checkToken(Token.COMMA)){
                            getNext();
                            if (checkToken(Token.NUMBER)){
                                cols = Integer.parseInt(currentToken);
                                getNext();
                                if (checkToken(Token.COMMA)){
                                    getNext();
                                    if (checkToken(Token.NUMBER)){
                                        hgap = Integer.parseInt(currentToken);
                                        getNext();
                                        if (checkToken(Token.COMMA)){
                                            getNext();
                                            if (checkToken(Token.NUMBER)){
                                                vgap = Integer.parseInt(currentToken);
                                                getNext();
                                                if (checkToken(Token.CLOSE_PAREN)){
                                                    parent.setLayout(new GridLayout(rows, cols, hgap, vgap));
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (checkToken(Token.CLOSE_PAREN)){
                                    parent.setLayout(new GridLayout(rows, cols));
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean parseWidgets(Container parent) throws InvalidSyntaxException{
        /*
         * Calls the method for the Widget production, then checks the type for another widget. Calls itself
         * recursively if another widget is detected.
         */
        if (parseWidget(parent)){
            getNext();
            if (verifyNextWidget(currentType)){
                if (parseWidgets(parent) || checkToken(Token.END)){
                    //allows for the end of a nested panel or group to be detected.
                    return true;
                }
            } else if (checkToken(Token.END)){
                //Need this for additional nesting
                return true;
            }
        }
        return false;
    }
    private boolean parseWidget(Container parent) throws InvalidSyntaxException{
        /*
         * Determines which widget needs to be processed and processes each.
         */
        if (checkToken(Token.WIDGET_BUTTON)){
            /*
             * Creates a new button and adds the appropriate label.
             */
            getNext();
            if (checkToken(Token.QUOTE)){
                getNext();
                if (checkToken(Token.STRING) || checkToken(Token.NUMBER)){
                    parent.add(new JButton(currentToken));
                    getNext();
                    if (checkToken(Token.QUOTE)){
                        getNext();
                        if (checkToken(Token.SEMICOLON)){
                            return true;
                        }
                    }
                }
                else if (checkToken(Token.QUOTE)){
                    parent.add(new JButton());
                    getNext();
                    if (checkToken(Token.SEMICOLON)){
                        return true;
                    }
                }
            }
        }
        else if (checkToken(Token.WIDGET_GROUP)){
            /*
             * Creates a buttongroup and passes it to the radio buttons production method. Similar to the Panel
             * processing without nesting.
             */
            ButtonGroup group = new ButtonGroup();
            getNext();
            if (parseRadio_Buttons(parent, group)){
                if (checkToken(Token.END)){
                    getNext();
                    if (checkToken(Token.SEMICOLON)){
                        return true;
                    }
                }
            }
        }
        else if (checkToken(Token.WIDGET_LABEL)){
            /*
             * Creates a new label and adds it to the parent container with the string provided.
             */
            getNext();
            if (checkToken(Token.QUOTE)){
                getNext();
                if (checkToken(Token.STRING) ||
                    checkToken(Token.NUMBER)){
                        parent.add(new JLabel(currentToken));
                        getNext();
                        if (checkToken(Token.QUOTE)){
                            getNext();
                            if (checkToken(Token.SEMICOLON)){
                                return true;
                            }
                        }
                    }
                    else if (checkToken(Token.QUOTE)){
                        parent.add(new JLabel(""));
                        getNext();
                        if (checkToken(Token.SEMICOLON)){
                            return true;
                        }
                    }
            }
        }
        else if (checkToken(Token.WIDGET_PANEL)){
            /*
             * Creates a new JPanel object and then passes it to each production method, including the parseWidgets method which recursively
             * builds and adds objects to the panel. Panels within panels work correctly.
             */
            JPanel panel = new JPanel();
            parent.add(panel);
            getNext();
            if (parseLayout(panel)){
                getNext();
                if (parseWidgets(panel)){
                    if (checkToken(Token.END)){
                        getNext();
                        if (checkToken(Token.SEMICOLON)){
                            return true;
                        }
                    }
                }
            }
        }
        else if (checkToken(Token.WIDGET_TEXTFIELD)){
            /*
             * creates a new textfield and sizes it, then adds it to the parent container.
             */
            getNext();
            if (checkToken(Token.NUMBER)){
                parent.add(new JTextField(Integer.parseInt(currentToken)));
                getNext();
                if (checkToken(Token.SEMICOLON)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean parseRadio_Buttons(Container parent, ButtonGroup group) throws InvalidSyntaxException{
        /*
         * Similar to the parseWidgets method, but doesn't require nesting. Passes the parent container and the
         * ButtonGroup to the next production, which will create and add each radio button to the group and parent
         * container.
         */
        if (parseRadio_Button(parent, group)){
            getNext();
            if (checkToken(Token.RADIO)){
                if (parseRadio_Buttons(parent, group) || checkToken(Token.END)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean parseRadio_Button(Container parent, ButtonGroup group) throws InvalidSyntaxException{
        //Adds a radio button to the parent container and buttongroup.
        if (checkToken(Token.RADIO)){
            getNext();
            if (checkToken(Token.QUOTE)){
                getNext();
                if (checkToken(Token.STRING) ||
                    checkToken(Token.NUMBER)){
                    JRadioButton button = new JRadioButton(currentToken);
                    group.add(button);
                    parent.add(button);
                    getNext();
                    if (checkToken(Token.QUOTE)){
                        getNext();
                        if (checkToken(Token.SEMICOLON)){
                            return true;
                        }
                    }
                }
                else if(checkToken(Token.QUOTE)){
                    JRadioButton button = new JRadioButton("");
                    group.add(button);
                    parent.add(button);
                    getNext();
                    if (checkToken(Token.SEMICOLON)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean verifyNextWidget(Token type){
        //Determines if the type of the object is a widget.
        if (type == Token.WIDGET_BUTTON || 
        type == Token.WIDGET_GROUP ||
        type == Token.WIDGET_LABEL ||
        type == Token.WIDGET_PANEL ||
        type == Token.WIDGET_TEXTFIELD){
            return true;
        }
        return false;
    }
}
