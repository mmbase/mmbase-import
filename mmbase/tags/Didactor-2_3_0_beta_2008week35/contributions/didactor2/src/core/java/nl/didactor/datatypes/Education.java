package nl.didactor.datatypes;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.core.event.*;
import java.util.*;
import java.util.regex.*;


/**
 * If you make an 'education' typed field, it will be autoamticly filled with the current user's education.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Education.java,v 1.3 2008-06-20 10:09:50 michiel Exp $
 */
public class Education extends NodeDataType  {
    private static final Logger log = Logging.getLoggerInstance(Education.class);


    public Education(String name) {
        super(name);
    }

    @Override
    public Node getDefaultValue(Locale locale, Cloud cloud, Field field) {
        if (cloud == null) {
            log.service("no cloud, returning null");
            if (field == null) return null;
            try {
                cloud = field.getNodeManager().getCloud();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                return null;
            }
        }
        Node user =  nl.didactor.security.Authentication.getCurrentUserNode(cloud);
        Collection<Node> relatedEducations = (Collection<Node>) user.getFunctionValue("educations", null).get();
        if (relatedEducations != null && relatedEducations.size() > 0) {
            return relatedEducations.iterator().next();
        } else {
            return null;
        }
    }

}
