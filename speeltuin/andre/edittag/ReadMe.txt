About the EditTag
=================

André van Toly
andre@toly.nl
31 october 2005

The EditTag is a simple MMBase JSP tag that can make the data being displayed by 
<mm:field> tags accessible. It builds on the principle that data in MMBase nodes
is stored in fields, which in all cases you can only get with a field tag. 
Whether you use the simple <mm:node> tag, or more complicated <mm:list>, 
<mm:relatednodes> or <mm:tree> tags, you will always use <mm:field>.

A simple example of the EditTag is as follows, presuming the MMBase MyNews
example is installed:

<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
  <mm:edit type="yammeditor">
  <mm:param name="icon">/mmbase/edit/my_editors/img/mmbase-edit.gif</mm:param>
  <mm:param name="url">/yammeditor/yammeditor.jsp</mm:param>
  <mm:node number="default.mags">
    <strong><mm:field name="title" /></strong>
    <mm:related path="posrel,news"
	  fields="news.title">
	  <mm:field name="news.title" /><br />
	</mm:related>
  </mm:node>
  </mm:edit>
</mm:cloud>

This example prints the title of the MyNews magazine example, the news articles
that are related to it, and below it a link to a generic editor YAMMEditor. In 
the editor you can edit the nodes, their related nodes and relations.

In the directory 'yammeditor' you will find the editor and some more examples 
based on a 'news' node with the alias 'artikel' (no data provided, create in 
one of the other editors).

Currently this principle has a problem: when a node or a field does not contain 
any data, it is not displayed on the page, and will thus not be found by the 
EditTag. You can not 'fill' a page using YAMMeditor.


How-to
------
Use <mm:edit> preferably around <mm:node> and other nodeprovider or 
clusternodeprovider tags. At the moment it has two attributes:
- editor : to provide a link to an external editor, f.e. 
"/yammeditor/yammeditor.jsp".
- icon : to replace the text link, f.e. 
"/mmbase/edit/my_editors/img/mmbase-edit.gif".
In the directory 'yammeditor' i have made some examples.
