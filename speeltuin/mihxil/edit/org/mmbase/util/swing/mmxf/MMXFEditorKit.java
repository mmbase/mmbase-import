package org.mmbase.util.swing.mmxf;
import org.mmbase.util.swing.xml.XMLEditorKit;
import javax.swing.text.Document;

public class MMXFEditorKit extends XMLEditorKit {

    public String   getContentType()        { return "text/mmxf"; }

    public Document createDefaultDocument() { return new MMXFDocument(); }

}
