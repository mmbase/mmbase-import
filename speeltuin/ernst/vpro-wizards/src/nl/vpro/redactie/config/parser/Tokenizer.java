package nl.vpro.redactie.config.parser;

import java.util.*;

/**
 * @author ebunders The simple config works like this:
 *         <h3>create an instance of a bean in the map, as 'een'</h3>
 *         <p>
 *         nl.Class.id=een
 *         </p>
 * 
 * <h3>set a siple (string, boolean or integer) value on an object</h3>
 * <p>
 * een.value='somevalue' (literal)
 * </p>
 * <p>
 * nl.Class.id=een.value='disco' (create the object in the map and set the value)
 * </p>
 * 
 * <h3>set a complex value on an object</h3>
 * <p>
 * een.value=somevalue (somevalue shoul be in the object map.)
 * </p>
 * 
 * <h3>set a list of beans as parameter</h3>
 * <p>
 * een.list=[a,b] (a and b should be in the map, and both of the proper type (assignable from)
 * </p>
 * <p>
 * een.list=[nl.SomeClass.id=a.value="een",nl.SomeClass.id=b.value="twee"] (create the objects in the map, set values
 * and put them to the fist object as list)
 * </p>
 * een.list=[nl.SomeClass.value="drie"] (create the instance anonimously and put it to the first object as a list)
 * <p>
 * </p>
 * 
 * <h3></h3>
 */
public class Tokenizer {
    private List<String> tokens = new ArrayList<String>();
    
    private static final int READ = 0;
    private static final int INSIDE_LIST = 1;
    private static final int ESCAPE_ON = 2;
    private int index = 0;
    
    
    
    public Tokenizer (String line){
        char[] chars = line.toCharArray();
        int status = READ;
        String word = "";
        int nestedLists = 0;
        
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            switch (c) {
            
            case '\\':
                if(status != ESCAPE_ON){
                    status = ESCAPE_ON;
                    break;
                }
                
            case '[':
                if(status != ESCAPE_ON && status != INSIDE_LIST){
                    status = INSIDE_LIST;
                    if(!"".equals(word)){
                        tokens.add(word);
                    }
                    tokens.add(""+c);
                    word = "";
                    break;
                }else{
                    //we are inside a list already
                    nestedLists ++;
                }
                
            case ']':
                if(status == INSIDE_LIST ){
                    if (nestedLists == 0) {
                        status = READ;
                        if (!"".equals(word)) {
                            tokens.add(word);
                        }
                        tokens.add("" + c);
                        word = "";
                        break;
                    }else{
                        nestedLists --;
                    }
                }
                
            case '.':
                if(status == READ){
                    if(!"".equals(word)){
                        tokens.add(word);
                    }
                    word = "";
                    break;
                }else{
                    //escape is on or inside list
                    word = word + c;
                }
                
            
            default:
                word = word + c;
                if(status == ESCAPE_ON){
                    status = READ;
                }
                break;
            }
        }
        if(!"".equals(word)){
            tokens.add(word);
        }
        
    }
    
    public String nextToken(boolean updateIndex){
        if(hasMoreTokens()){
            String t = tokens.get(index+1);
            if(updateIndex) index ++;
            return t;
        }
        return null;
    }
    
    public String nextToken(){
        return nextToken(false);
    }
    
    public String currentToken(boolean updateIndex){
        String t = tokens.get(index);
        if(updateIndex && hasMoreTokens()){
            index ++;
        }
        return t;
    }
    
    public String currentToken(){
        return currentToken(false);
    }
    
    public boolean hasMoreTokens(){
        return index < (tokens.size());
    }
    
    public void reset(){
        index = 0;
    }
    
    public int size(){
        return tokens.size();
    }
    
    public void forward(){
        if(hasMoreTokens()){
            index ++;
        }
    }
    public String toString(){
        StringBuilder sb = new StringBuilder("#");
        for (String s : tokens){
            sb.append(s + "#");
        }
        return sb.toString();
    }

}
