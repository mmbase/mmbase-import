package nl.didactor.builders;
/**
 * Builder that overrides the commit() method of the MMBase 'ObjectTypes'
 * builder. The original method will try to delete the builder table and
 * re-insert the builder, and this will throw an exception if the table
 * is not empty. Our 'Dynamic Fields' system allows for dynamic addition
 * of fields, so a commit() on an object-type should not throw an error.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DFObjectTypes extends org.mmbase.module.corebuilders.ObjectTypes {
    public boolean commit(org.mmbase.module.core.MMObjectNode node) {
        boolean retval = false;
        try {
            retval = super.commit(node);        
        } catch(Exception e) {
        }
        if (retval) {
            loadBuilder(node);
        }
        return retval;
    }
}
