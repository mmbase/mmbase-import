/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.datatypes.handlers.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.datatypes.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @version $Id$
 */

public  class TextHandlerTest {

    private static NodeManager news;
    private static Field title;
    public static Cloud getCloud() {
        return MockCloudContext.getInstance().getCloud("mmbase");
    }
    @BeforeClass()
    public static void setUp() throws Exception {
        DataTypes.initialize();
        MockCloudContext.getInstance().clear();
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        news = getCloud().getNodeManager("news");
        title = news.getField("title");
    }

    @Test
    public void id() throws Exception {
        Handler handler = new TextHandler();
        Request request = new JavaRequest(false);
        assertEquals("mm_title", handler.id(request, title));

    }

    @Test
    public void inputNewNodeForm() throws Exception {
        Handler handler = new TextHandler();
        Request request = new JavaRequest(false);
        // Not entirely sure that the mm_n_-1 is needed if there is no node yet.
        assertEquals("<input type=\"text\" class=\"small mm_validate mm_dtclass_string mm_dtclass_comparable mm_dtclass_length mm_f_title mm_nm_news mm_n_-1 mm_length_13\" size=\"80\" name=\"title\" id=\"mm_title\" value=\"Article title\" />",
                     handler.input(request, null, news.getField("title"), false));

    }

    @Test
    public void inputNewNodeFormPost() throws Exception {
        Handler handler = new TextHandler();
        JavaRequest request = new JavaRequest(true);
        request.put("title", "foo");
        Node article = news.createNode();
        assertEquals("<input type=\"text\" class=\"small mm_validate mm_dtclass_string mm_dtclass_comparable mm_dtclass_length mm_f_title mm_nm_news mm_n_-1 mm_length_3\" size=\"80\" name=\"title\" id=\"mm_title\" value=\"foo\" />",
                     handler.input(request, article, title, false));
    }
    @Test
    public void set() throws Exception {
        Handler handler = new TextHandler();
        JavaRequest request = new JavaRequest(true);
        request.put("title", "foo");
        Node article = news.createNode();
        handler.set(request, article, title);
        assertEquals("foo", article.getValue("title"));
    }


}
