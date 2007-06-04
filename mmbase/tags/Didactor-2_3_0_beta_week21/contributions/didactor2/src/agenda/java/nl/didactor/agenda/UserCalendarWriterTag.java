package nl.didactor.agenda;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import nl.eo.calendar.*;
import org.mmbase.bridge.*;
import org.mmbase.security.UserContext;
import org.mmbase.bridge.jsp.taglib.*;

/**
 * This tag can be placed within a &lt;di:calendar&gt; tag to give the
 * calendar the calendaritems for the currently logged-on user.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class UserCalendarWriterTag extends CloudReferrerTag {
    String username;
    protected CalendarWriter writer;
        
    public int doStartTag() throws JspTagException {
        Cloud cloud = getCloudVar();
        if (cloud == null) {
            throw new JspTagException("No parent <mm:cloud> tag found");
        }
        UserContext user = cloud.getUser();
        if (user == null) {
            throw new JspTagException("Cloud has no user linked to it!");
        }

        String username = user.getIdentifier();

        if (username == null) {
            throw new JspTagException("Username not found; make sure you are using this tag within a <mm:cloud>");
        }
        writer = new UserCalendarWriter(cloud, username);

        Tag parent = getParent();
        if (parent != null && parent instanceof CalendarTag) {
            CalendarTag calendarTag = (CalendarTag)parent;
            calendarTag.addWriter(writer);
        } else {
            throw new JspTagException("Could not find parent calendar tag.");
        }

        return SKIP_BODY;
    }

    public int doAfterBody() {
        writer = null;
        return SKIP_BODY;
    }
}
