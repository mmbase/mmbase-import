package nl.didactor.builders;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.*;
import org.mmbase.util.*;
import java.util.Date;
import java.util.List;

/**
 * This class handles objects of type 'ProActiveMailTemplate'.
 * @author Goran Kostadinov     (Levi9 Balkan Global Sourcing)
 */
public class ProActiveMailTemplateBuilder extends MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(ProActiveMailTemplateBuilder.class.getName());

    public boolean init() {
        return super.init();
    }

    public int insert(String owner, MMObjectNode node) {
        int nr = super.insert(owner, node);

        String username = "system";
        String admin = "admin";
        String templateName = node.getStringValue("name");

        // add new entry in batches
        try {
            MMObjectBuilder batchesbuilder = mmb.getBuilder("proactivemailbatches");
            MMObjectNode batNode = batchesbuilder.getNewNode(username);
            batNode.setValue("name", templateName);
            batNode.setValue("start_time", System.currentTimeMillis()/1000);
            batNode.setValue("end_time", System.currentTimeMillis()/1000);
            batNode.setValue("sent_messages", 0);
            batchesbuilder.insert(username, batNode);
        }  catch (Exception e) {
            log.error("Can't add new row in proactivemailbatches table with name '"+templateName+".\r\n"+
                    "Template will not work as expected!\r\n    "+e.toString());
        }
        return nr;
    }
}

