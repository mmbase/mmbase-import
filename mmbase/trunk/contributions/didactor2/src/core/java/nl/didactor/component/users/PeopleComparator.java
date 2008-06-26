package nl.didactor.component.users;

import java.util.Comparator;
import org.mmbase.bridge.Node;


/**
 * lastname,firstname comparator for people
 * @author azemskov
 * @javadoc
 */
public class PeopleComparator implements Comparator<Node> {

    public int compare(Node ob1, Node ob2)    {

        int iResult = ob1.getStringValue("firstname").compareTo(ob2.getStringValue("firstname"));
        if (iResult != 0) {
            return iResult;
        } else {
            return ob1.getStringValue("lastname").compareTo(ob2.getStringValue("lastname"));
        }
    }
}
