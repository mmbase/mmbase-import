
package org.mmbase.util.swing;

import java.util.*;
import java.io.*;
import javax.swing.text.*;
import org.mmbase.util.swing.xml.*;
import org.mmbase.util.swing.xml.parse.*;
import org.mmbase.util.swing.mmxf.*;

public class Test extends DefaultStyledDocument  {

    public Test(StyleContext s) {
        super(s);
    }


    public Responder getResponder(Reader in, int pos) {
        return null;
    }
    public void doTest() {
        AbstractDocument.BranchElement p = (AbstractDocument.BranchElement) createBranchElement(null, null);
        AbstractDocument.BranchElement e = (AbstractDocument.BranchElement) createBranchElement(p, null);
        Element [] es = {e};
        p.replace(0,0, es);
        System.out.println("children:" + p.children());
        System.out.println("end");       
    }

    public static void main (String[] argv) {
        /*
        {
            Test doc = new Test(new MMXFStyle());
            XMLBranchElement p = (XMLBranchElement) doc.createBranchElement(null, null);
            XMLBranchElement e = (XMLBranchElement) doc.createBranchElement(p, null);
            System.out.println("children:" + p.children());
            System.out.println("end");
        }
        */
        {
            Test doc = new Test(new MMXFStyle());
            doc.doTest();
        }

        return;
    }
}
