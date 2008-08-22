package nl.didactor.builders;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.Processor;


/**

 * @author Michiel Meeuwissen
 */
public class PeopleActivater extends DidactorBuilder {

    public Object process(Node node, Field field, Object value) {
        node.setBooleanValue("person_status", true);
        return value;
    }

}


