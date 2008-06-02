package nl.vpro.redactie.config.parser;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;

public class Parser {
    private Stack<Object> stack = new Stack<Object>();
    private ElementParser currentParser;
    private Map<String, Object> objects = new HashMap<String, Object>();
    Tokenizer tokenizer;
    private static final String DEFAULT_PATH = "nl.vpro.redactie.config.";
    private static Logger log = Logger.getAnonymousLogger();

    /**
     * create the parser, and the parsing is done straight away.
     * @param lines
     * @throws ParseException
     */
    public Parser(String lines) throws ParseException {
        parse(lines);
    }

    /**
     * with this constructor you can insert an existing object map
     * @param lines
     * @param objects
     * @throws ParseException
     */
    public Parser(String lines, Map<String, Object> objects) throws ParseException {
        this.objects = objects;
        parse(lines);
    }

    private void parse(String lines) throws ParseException {
        try {
            BufferedReader br = new BufferedReader(new StringReader(lines));
            String line = br.readLine();
            log.info("parsing first line: " + line);
            while (line != null) {
                // at the beginning of the line: set the current parser to find object parser
                currentParser = new FindObjectParser();

                // skip comment and empty lines
                if (!line.trim().startsWith("#") && !"".equals(line.trim())) {
                    tokenizer = new Tokenizer(line.trim());
                    while (tokenizer.hasMoreTokens()) {
                        log.info("parsing with parser: " + currentParser);
                        currentParser.parse();
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getObjects() {
        return objects;
    }

    /**
     * create a list of objects from the configuration
     * @return
     * @throws ParseException
     */
    static List<Object> deriveList(Tokenizer tokenizer, Map<String, Object> objects) throws ParseException {
        // next three tokens: [ , <the list>, ]
        tokenizer.forward();
        String s = tokenizer.currentToken();
        log.info("list body" + s);
        tokenizer.forward();

        String[] listItems = s.split(",");
        StringBuilder sb = new StringBuilder();
        for (String line : listItems) {
            sb.append(line);
            sb.append("\n");
        }

        // now parse the list items
        String config = sb.toString();
        Parser listParser = new Parser(config, objects);
        List<Object> result = new ArrayList<Object>();
        for (Object object : listParser.getObjectStack()) {
            result.add(object);
        }
        return result;
    }

    /**
     * when the first character is uppercase, it is a class name (as opposed to a package name)
     * @param s
     * @return
     */
    static boolean isClass(String s) {
        return s.substring(0, 1).equals(s.substring(0, 1).toUpperCase());
    }

    static String stripQuotes(String s) {
        return s.substring(1, s.length() - 1);
    }

    /**
     * This method will try to derive an object from the tokenizer. trying this:
     * <ul>
     * <li>when the token is '[' a listparser is created to derive the list, and return the resulting List
     * <li>when the token is a key in the map, return the object in the map
     * <li>when the token is a litera (starts and ends with '), return a string
     * <li>when the toekn can be converted into an Integer, return this.
     * <li>when the token is [true|false] return a Boolean.
     * <li>when the token is a class in the default package, instantiate and return this
     * <li>when the token is part of a package name, complete the classname, instantiate it and return the object.
     * </ul>
     * When there is an id='..' section behind the object token(s), the new object will be put on the map.
     * @param objects
     * @param tokenizer
     * @return
     * @throws ParseException
     */
    static Object deriveObject(Map<String, Object> objects, Tokenizer tokenizer) throws ParseException {
        Object newObject = null;
        Class clazz = null;
        String clazzName = "";
        log.info("derive object was called with tokenizer: " + tokenizer);
        boolean fromMap = false;
        String token = tokenizer.currentToken();

        if (token.equals("[")) {
            newObject = deriveList(tokenizer, objects);
        } else {
            tokenizer.forward();
            if (objects.get(token) != null) {
                newObject = objects.get(token);
                fromMap = true;
            } else if (token.startsWith("'") && token.endsWith("'")) {
                newObject = stripQuotes(token);
            } else if (token.matches("[0-9]+")) {
                newObject = new Integer(token);
            } else if ("true".equals(token.toLowerCase()) || "false".equals(token.toLowerCase())) {
                newObject = new Boolean(token);
            } else {
                // let's try to find a class
             
                if (isClass(token)) {
                    // it must be a class name
                    try {
                        clazz = Class.forName(DEFAULT_PATH + token);
                    } catch (ClassNotFoundException e) {
                        throw new ParseException("could not create an instance from class " + clazzName + ". reason:" + e.getMessage());
                    }
                } else {
                    // not a class in the default package...
                    // it must be a package name
                    // see if we can construct a path
                    Package p;

                    while (!isClass(tokenizer.currentToken())) {
                        token = token + "." + tokenizer.currentToken(true);
                    }

                    if (Package.getPackage(token) == null) {
                        throw new ParseException("could not find package " + token);
                    }

                    // the next one must be a class
                    clazzName = token + "." + tokenizer.currentToken(true);

                    try {
                        clazz = Class.forName(clazzName);
                    } catch (Exception e1) {
                        throw new ParseException("could not load the class " + clazzName + ". reason:" + e1.getMessage());
                    }
                }
            
                // now we should have the class name
                log.info("** class name: " + clazzName);
                try {
                    newObject = clazz.newInstance();
                } catch (Exception e) {
                    throw new ParseException("could not create an instance from " + clazzName + ". reason:" + e.getMessage());
                }
            }
        }
        

        // should we put it in the map?
        if (tokenizer.hasMoreTokens() && tokenizer.currentToken().startsWith("id=")) {
            if (fromMap) {
                throw new ParseException("you should not put an id on a reference to an object in the map");
            }
            String[] s1 = tokenizer.currentToken(true).split("=");
            log.info("set object [" + newObject + "] in map at key: " + s1[1]);
            objects.put(s1[1], newObject);
        }

        return newObject;
    }

    /**
     * watch out! this is not thread safe.
     * @return
     */
    public Iterable getObjectStack() {
        return stack;
    }

    interface ElementParser {
        public void parse() throws ParseException;
    }

    /**
     * This class will find an object or throw an exception if it can't. if the object is a new intance and an id is
     * given the object is put on the stack. An object can be found in three ways:
     * <ul>
     * <li> the next element on the tokenizer is an id in the object map.
     * <li> the next element on the tokenizer is a class in the nl.vpro.redactie.config package.
     * <li> the next element on the tokenizer is a (peace of) a (path to) a fully qualified class name.
     * </ul>
     * All objects should have a no-arg constructor.
     * @author ebunders
     */
    class FindObjectParser implements ElementParser {

        public void parse() throws ParseException {
            Object newObject = deriveObject(objects, tokenizer);
            log.info("** created object: " + newObject.getClass());

            // now we have the object. put it in the stack
            stack.push(newObject);

            // now, if there is still another token, we must assume it is to set attributes.
            currentParser = new SetAttributeParser();
        }
    }

    /**
     * This class will try to find a setter method that matches the present element on the tokenizer. It will then try
     * derive the value to be set by evaluating the string right of the '='. this can be a literal: 'string', true, 123
     * (booleans and integers will be converted0, or it can be a reference to an object in the object map or a declared
     * object, or it can be a list containing a combination of map references or declared objects. . For syntax examples
     * look at the Tokenizer class.
     * @author ebunders
     * 
     */
    class SetAttributeParser implements ElementParser {

        public void parse() throws ParseException {
            String t = tokenizer.currentToken(true);
            String[] s = t.split("=");

            if (s.length == 0) {
                throw new ParseException("can not derive setter method from token: " + t);
            }
            String setter = s[0];
            setter = "set" + setter.substring(0, 1).toUpperCase() + setter.substring(1);

            // find the setter in the current object in the stack
            Object currentObject = stack.peek();
            Method method = findMethod(currentObject, setter);
            if (method == null) {
                throw new ParseException("no method with name " + setter + " found on object " + currentObject);
            }

            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new ParseException("method " + setter + " takes more or less than one parameter");
            }

            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                throw new ParseException("method " + setter + " is not public");
            }
            Class parameterType = parameterTypes[0];
            try {

                // now we must find the value to be set.
                if (s.length == 2) {
                    // we have a single value
                    String valueString = s[1];

                    if (parameterType == String.class) {
                        method.invoke(currentObject, new Object[] { valueString });
                    } else if (parameterType == Boolean.class || parameterType == boolean.class) {
                        if (valueString.toLowerCase().equals("true") || !valueString.toLowerCase().equals("false")) {
                            method.invoke(currentObject, new Object[] { new Boolean(valueString) });
                        } else {
                            throw new ParseException("method " + setter + " takes a boolean, but value string [" + valueString
                                    + "] could not be converted into one");
                        }
                    } else if (parameterType == Integer.class || parameterType == int.class) {
                        try {
                            new Integer(valueString);
                        } catch (NumberFormatException e) {
                            throw new ParseException("method " + setter + " takes an  integer, but value string [" + valueString
                                    + "] could not be converted into one");
                        }
                        method.invoke(currentObject, new Object[] { new Integer(valueString) });
                    } else {
                        if (objects.get(valueString) != null) {
                            Object valueObject = objects.get(valueString);
                            if (parameterType == valueObject.getClass()) {
                                method.invoke(currentObject, new Object[] { valueObject });
                            } else {
                                throw new ParseException("value string [" + valueString + "] ponts to an object of a type ("
                                        + valueObject.getClass() + ") that is incompatible with the type that the attribute of method "
                                        + setter + " should be (" + parameterType + ")");
                            }
                        } else {
                            throw new ParseException("setter method " + setter
                                    + " does not take a String, integer or boolean as attribute, and the value string dous not"
                                    + "point to an object in the map that the method can take as an attribute");
                        }
                    }

                } else {
                    // we must have a list
                    if (!tokenizer.nextToken().equals("[")) {
                        throw new ParseException("value string for method " + setter + " is not a list and not a single identifier");
                    }

                    if (!parameterType.isAssignableFrom(List.class)) {
                        throw new ParseException("value string for method " + setter
                                + " is a list but the method does not take a list for an attribute");
                    }
                    List<Object> list = deriveList(tokenizer, objects);
                    if (list == null) {
                        throw new ParseException("trying to construct the list that method " + setter
                                + " takes for an argument resulted into null!");
                    }
                    method.invoke(currentObject, new Object[] { list });
                }
            } catch (Exception e) {
                throw new ParseException("something went wrong invoking method: " + setter + " on object " + currentObject + ". reason: "
                        + e.getMessage());
            }
        }

        private Method findMethod(Object o, String methodName) {
            Method[] methods = o.getClass().getMethods();
            for (Method someMethod : methods) {
                String someMethodName = someMethod.getName();
                if (someMethodName.equals(methodName)) {
                    return someMethod;
                }
            }
            return null;
        }
    }

    /**
     * @author ebunders this class will create a number of objects on the stack that are configured as a list: [<Classname>,<id>
     */
    class ListParser implements ElementParser {

        public void parse() {
        // TODO Auto-generated method stub

        }
    }
}
