package org.mmbase.bridge;

import org.mmbase.util.functions.*;
/**
 *
 */
public class ThisNodeFunction extends  NodeFunction<Node> {

    public ThisNodeFunction() {
        super("THISNODE");
    }

    protected  Node getFunctionValue(Node node, Parameters parameters) {
        return node;
    }

}
