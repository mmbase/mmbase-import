package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The EditTag can create an url to an editor with nodenrs, fields and paths.
 * FieldTags register these with the EditTag. This way the linked editor
 * 'knows' which nodes and fields are available in the page.
 *
 * @author Andre van Toly
 * @version $Id: EditTag.java,v 1.3 2005-03-18 13:19:26 andre Exp $
 */
public interface EditTag {

    /**
     * Location of an editor 
     *
     * @param editor     String with link to the editor
     */ 
    public void setEditor(String editor);      // editor : link to the editor
    
    /**
     * Location of an icon 
     *
     * @param icon     String with link to icon
     */ 
    public void setIcon(String icon);          // icon : link to the edit icon
    
    
    /**
     * Here is were the FieldTag registers its fields and some associated 
     * and maybe usefull information with the EditTag.
     *
     * @param query     SearchQuery that delivered the field
     * @param nodenr    Nodenumber of the node the field belongs to
     * @param field     Name of the field
     */ 
    public void registerField(Query query, int nodenr, String field);
        
}
