package ebunders.mmbase.shorthand;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import javax.net.ssl.SSLEngineResult.Status;

public class SimpleIndentWriter extends FileWriter {
    
    public SimpleIndentWriter(File file) throws IOException {
        super(file);
    }


    public static final int STATE_TAG_OPENING=0;
    public static final int STATE_INSIDE_TAG=1;
    public static final int STATE_INSIDE_CLOSING_TAG=2;
    public static final int STATE_OUTSIDE_TAG=3;
    public static final int STATE_IGNORE_LINE=4;
    public static final int STATE_NEW_LINE=5;
    
    private int indentlevel=-1;
    private int state = STATE_NEW_LINE;
    private static final Logger log = Logger.getLogger(SimpleIndentWriter.class.getName());
    private boolean lineindented = false;
    private boolean between_quotes = false; 

  
    @Override
    public void write(int c) throws IOException {
        char ch = (char)c;
        //System.out.print(ch);
        switch (ch) {
        case '\n':
            //System.out.println("[newline]");
            super.write(ch);
            state = STATE_NEW_LINE;
            lineindented = false;
            break;

        case ' ':
            if(state != STATE_NEW_LINE)
            super.write(ch);
            break;
            
        case '"':
            between_quotes = !between_quotes;
            super.write(ch);
            break;
            
        case '?':
            if(state == STATE_TAG_OPENING){
                super.write('<');
                state = STATE_IGNORE_LINE;
            }
            //System.out.print("[ignore line]");
            super.write(ch);
            break;
            
        case '!':
            if(state == STATE_TAG_OPENING){
                super.write('<');
                state = STATE_OUTSIDE_TAG;
            }
            //System.out.print("[outside tag]");
            super.write(ch);
            break;
            
        case '<':
            state = STATE_TAG_OPENING;
            //System.out.print("[tag opening]");
            break;
            
        case '/':
            if(state == STATE_TAG_OPENING){
                if(!lineindented){
                    indent();
                    lineindented = true;
                }
                
                super.write("</");
                indentlevel --;
                //System.out.print("[indent--]");
                state = STATE_INSIDE_CLOSING_TAG;
                //System.out.print("[inside closing tag]");
            }else if(state == STATE_INSIDE_TAG && !between_quotes){
                indentlevel --;
                //System.out.print("[indent--]");
                super.write(ch);
            }
            else{
                super.write(ch);
            }
            
            break;
            
        case '>':
            if(state == STATE_INSIDE_CLOSING_TAG){
                //indentlevel --;
                state = STATE_OUTSIDE_TAG;
                //System.out.print("[end of closing tag.]");
            }
            
            if(state == STATE_INSIDE_TAG){
                state = STATE_OUTSIDE_TAG;
                //System.out.print("[end of opening tag]");
            }
            super.write(ch);
            break;

        default:
            if(state == STATE_TAG_OPENING){
                state = STATE_INSIDE_TAG;
                indentlevel ++;
                //System.out.print("[indent++]");
                if(!lineindented){
                    indent();
                    lineindented = true;
                }
                super.write('<');
                //System.out.print("[inside opening tag.]");
            }
        super.write(ch);
            break;
        }
    }


    private void indent() throws IOException {
        for (int i = 0; i < indentlevel; i++) {
            //System.out.print("*");
            super.write('\t');
        }
        
    }
    
    

}
