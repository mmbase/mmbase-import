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
 * Disables a list of values. For the rest, this is the same as a StringDataType.
 * These disallowed names are stored in a specialized builder for that, named 'disallowedusernames'.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UserName.java,v 1.4 2008-02-01 14:13:01 michiel Exp $
 */
public class UserName extends StringDataType implements NodeEventListener {
    private static final Logger log = Logging.getLoggerInstance(UserName.class);

    protected static final LocalizedString notAcceptable = new LocalizedString("Notacceptable");
    static {
        notAcceptable.setBundle("nl.didactor.datatypes.resources.username");
    }

    final Set<Pattern> disallowed = new HashSet<Pattern>();
    boolean filled = false;
    public UserName(String name) {
        super(name);
        EventManager.getInstance().addEventListener(this);
    }

    protected void fillSet() {
        disallowed.clear();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        NodeIterator i = cloud.getNodeManager("disallowedusernames").getList(null).nodeIterator();
        while (i.hasNext()) {
            Node n = i.nextNode();
            disallowed.add(Pattern.compile(n.getStringValue("username")));
        }
        filled = true;

    }
    public void notify(NodeEvent event) {
        if (event.getBuilderName().equals("disallowedusernames")) {
            fillSet();
        }
    }


    protected Collection validateCastValue(Collection errors, Object castValue, Object value, Node node, Field field) {
        if (! filled) fillSet();
        log.debug("Validating " + castValue);
        errors = super.validateCastValue(errors, castValue, value,  node, field);
        if (errors == VALID) errors = new ArrayList();
        for (Pattern p : disallowed) {
            if (p.matcher("" + castValue).matches()) {
                errors.add(new LocalizedString("Dit is geen acceptabele " + field.getGUIName()));
                break;
            }
        }
        return errors;
    }
}
