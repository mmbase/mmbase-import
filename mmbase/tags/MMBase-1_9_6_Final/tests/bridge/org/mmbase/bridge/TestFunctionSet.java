package org.mmbase.bridge;

import org.mmbase.util.functions.Name;

/**
 * Used to call all functions for the SetFunctions tests. XXX work in progress
 *
 * @author Simon Groenewolt (simon@submarine.nl)
 */
public class TestFunctionSet {

    public static boolean testboolean(boolean b) {
        return b;
    }

    public static Boolean testBoolean(Boolean b) {
        return b;
    }

    public static int testint(int i) {
        return i;
    }

    public static Integer testInteger(Integer i) {
        return i;
    }

    public static Node thisNode(@Name("node") final Node node) { // a Node typed parameter with 'node' makes the function useable as a NodeFunction.
        return node;
    }

}
