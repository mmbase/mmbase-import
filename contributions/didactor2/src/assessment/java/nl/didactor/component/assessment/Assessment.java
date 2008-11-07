package nl.didactor.component.assessment;

import org.mmbase.module.core.*;
import org.mmbase.core.*;
import org.mmbase.bridge.Field;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.util.logging.*;

public class Assessment extends org.mmbase.framework.BasicComponent {
    private static final Logger log = Logging.getLoggerInstance(Assessment.class);

    public Assessment(String name) {
        super(name);
    }

    @Override protected void init() {
        MMObjectBuilder learnblocks = MMBase.getMMBase().getBuilder("learnblocks");

        CoreField assessment= Fields.createField("assessment", Field.TYPE_BOOLEAN, Field.STATE_VIRTUAL, DataTypes.getDataType("didactor_assessment_field"));
        assessment.setParent(learnblocks);
        assessment.setStoragePosition(100);

        learnblocks.addField(assessment);
        log.info("Added virtual field " + assessment + " to " + learnblocks + " -> " + learnblocks.getFields());



    }

}
