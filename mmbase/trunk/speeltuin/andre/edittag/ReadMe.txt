About the EditTag
=================

André van Toly
andre@toly.nl
24 december 2004

The EditTag is a simple MMBase JSP tag that can be used to make the data which
is displayed with field tags accessible. It builds on the principle that the
data in MMBase nodes is stored in fields and in every case you will need a field
tag of some kind to display it. Whether you use the simple <mm:node> tag, or
more complicated <mm:list>, <mm:relatednodes> or <mm:tree> tags, you will always
use <mm:field>.

A most simple example of the EditTag is as follows, presuming the MMBase MyNews
example is installed:

<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
  <mm:edit editor="/yammeditor/yammeditor.jsp">
  <mm:node number="default.mags">
    <mm:field name="title" />
  </mm:node>
  </mm:edit>
</mm:cloud>

In which case you will find to a link below the title of the example MyNews
magazine to a generic JSP editor YAMMeditor.

Installation
------------
1. Copy the contents of the directory 'yammeditor' to the root of your 
MMBase 1.8 installation.
2. Move to your local cvs directory:
'mmbase/applications/taglib/src/org/mmbase/bridge/jsp/taglib'. Copy the files
'FieldTag.java' and 'mmbase-taglib.xml' to a save location and replace them with
the new Java files in 'src'.
3. Move some directories up to 'mmbase/applications/taglib' and build a new
'mmbase-taglib.jar' with the command 'ant jar'.
4. Copy the 'mmbase-taglib.jar' (in: 'mmbase/applications/taglib/build') to
'WEB-INF/lib' in your MMBase 1.8 installation. Restart your web application.

Notice: 
- Sometimes you'll need to do a 'ant clean' before a build.
- And it can be necessary to clean the 'work' directory of your web application
server. The new tag may not be recognised.

How-to
------
Use <mm:edit> preferably around <mm:node> and other nodeprovider or 
clusternodeprovider tags. At the moment it has two attributes:
- editor : to provide a link to an external editor, f.e. 
"/yammeditor/yammeditor.jsp".
- icon : to replace the text link, f.e. 
"/mmbase/edit/my_editors/img/mmbase-edit.gif".
In the directory 'yammeditor' i have made some examples.