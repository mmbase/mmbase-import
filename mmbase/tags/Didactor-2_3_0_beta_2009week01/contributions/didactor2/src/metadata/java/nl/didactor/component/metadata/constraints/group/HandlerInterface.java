package nl.didactor.component.metadata.constraints.group;

import java.util.ArrayList;

import org.mmbase.bridge.*;

public interface HandlerInterface {

    public ArrayList check(Node nodeGroupConstraint, Node nodeObject, String sLocale);

}
