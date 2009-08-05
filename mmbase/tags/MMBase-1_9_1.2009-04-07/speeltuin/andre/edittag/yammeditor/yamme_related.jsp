<mm:compare referid="paths" value="" inverse="true">
<h4>Related nodes</h4>
<table border="0" width="100%" cellspacing="0" cellpadding="0">

<%-- changes to relations --%>
<mm:import externid="rel" />
<mm:import externid="changerel" />
<mm:import externid="deleterel" />
<mm:present referid="rel">
<tr>
  <td colspan="3" class="rel">
  <mm:node number="$rel"><p class="message">
  <mm:present referid="changerel">
    <mm:fieldlist type="edit" fields="owner"><mm:fieldinfo type="useinput" /></mm:fieldlist>
    The relation is changed. 
  </mm:present>
  <mm:present referid="deleterel">
    <mm:deletenode number="$rel" /> The relation is removed.
  </mm:present>
  </mm:node></p>
  </td>
</tr>
</mm:present>
<%-- /changes to relations --%>
<%!
  /**
  * Get the with a nodenr associated set with paths from pmap
  * @param map Map with associated sets with nodenrs and paths
  * @param map Map with associated sets with nodenrs and fields
  * @param str String being a nodenumber
  * @return Set with paths
  */
  public static List listRelatedNodeTypes(String nodenr, String pstr, Map fldmap) {
	String path = pstr;		// a path with this nodenr as startnode
	List l = new ArrayList();
    Set fs = null;
	  
	if (fldmap.containsKey(nodenr)) fs = (Set)fldmap.get(nodenr);
	for (Iterator j = fs.iterator(); j.hasNext();) {
	  String fld = (String)j.next();
	  String nt = fld.substring(0, fld.indexOf("."));    // news
	  
	  int pos = path.indexOf(nt);
	  if (pos > 0 && nt != null && !l.contains(nt)) {
		l.add(nt);
	  }
	}
	return l;
  }
  
  /*
  This method should return a list
  */
  public static List listRelatedNodes(String nodenr, String nodes) {
    List l = new ArrayList();
   
	if (nodes != null && !nodes.equals("")) {
	  String[] strArray = nodes.split(";");
	  for (int k = 0; k < strArray.length; k++) {
		int p = strArray[k].indexOf("_");
		String key = strArray[k].substring(0, p);
		String value = strArray[k].substring(p + 1);
		
		if (key.equals(nodenr) && !l.contains(nodenr)) l.add(value);
	  }
	}
    return l;
  }
%>
<%-- relations --%>
<mm:node number="$nr" notfound="skip">
<% String nodeman = ""; %>
<mm:nodeinfo type="type" jspvar="nodem" vartype="String" write="false"><% nodeman = nodem; %></mm:nodeinfo>
<%
if (pmap.containsKey(nr)) {
  Set ps = (Set)pmap.get(nr);
  for (Iterator i = ps.iterator(); i.hasNext();) {          // iterate over the paths
    String path = (String)i.next();
    Map ntmap = new TreeMap();          // make sure all nt's are put in correct order!
    
    Set fs = (Set)fmap.get(nr);			// get a set with fields, the same nr from fmap
    for (Iterator j = fs.iterator(); j.hasNext();) {
      String fld = (String)j.next();                        // news.title
      String nt = fld.substring(0, fld.indexOf("."));       // news

      int nt_pos = path.indexOf(nt);
      if (path.indexOf(nt) > 0) {                           // when path contains this nodetype
        if (!ntmap.containsValue(nt)) { 
          ntmap.put(Integer.toString(nt_pos), nt);
        }
      }
    }
    
    List ntlist = listRelatedNodeTypes(nr, path, fmap);
    %>
    <tr>
      <td class="path" colspan="3">
      <span class="fltleft"><%= path %></span>
      <%
      // buggy part.. (there can be > 1 nodetypes (and thus relations!) in a path)
      String ant = path.substring(path.lastIndexOf(",") + 1, path.length());
      String arole = path.substring(path.indexOf(",") + 1,path.lastIndexOf(","));
      if (arole.indexOf(",") > -1) {		// delete parts before komma
      	arole = arole.substring(arole.lastIndexOf(",") + 1, arole.length());
      }
      if (arole.equals("insrel")) arole = "related";
      %>
      <span class="fltright">
       <a href="javascript:OpenWindow('<mm:url page="yamme_search.jsp" referids="nr">
		 <mm:param name="path"><%= path %></mm:param>
		 <mm:param name="type"><%= ant %></mm:param>
		 <mm:param name="role"><%= arole %></mm:param>
	   </mm:url>','Search','height=480,width=540, scrollbars=YES, menubar=0, toolbar=0, status=0, directories=0, resizable=1')""
       ><img src="/mmbase/edit/my_editors/img/mmbase-search.gif" alt="search" width="21" height="20" /></a>
       <a href="javascript:OpenWindow('<mm:url page="yamme_create.jsp" referids="nr">
		 <mm:param name="path"><%= path %></mm:param>
		 <mm:param name="type"><%= ant %></mm:param>
		 <mm:param name="role"><%= arole %></mm:param>
	   </mm:url>','New','height=480,width=540, scrollbars=NO, menubar=0, toolbar=0, status=0, directories=0, resizable=1')""
       ><img src="/mmbase/edit/my_editors/img/mmbase-new.gif" alt="create" width="21" height="20" /></a>
       </span>
      </td>
    </tr>
    <%-- list related nodes --%>
    
    <mm:list nodes="<%= nr %>" path="<%= path %>">
      <% 
      // iterate the nodetypes in this path
      for (Iterator nti = ntlist.iterator(); nti.hasNext();) { 
      	String nt = (String)nti.next();
      	String rnr = "";
      %>
      <%-- related node --%>
      <tr>
        <td colspan="3" class="rel0">
        <mm:node element="<%= nt %>">
          <span class="reltxt">
          <mm:maywrite><a title="edit node" href="<mm:url 
            referids="nrs,nodes,fields,paths">
            <mm:param name="nr"><mm:field name="number" /></mm:param>
          </mm:url>"><img class="buttonedit" src="/mmbase/edit/my_editors/img/mmbase-edit.gif" alt="edit" width="21" height="20" /></a></mm:maywrite>
          <strong><mm:function name="gui" jspvar="gui" vartype="String" write="false">
              <% 
              if ((gui.indexOf("<a href") < 0 || gui.indexOf("<img src") < 0) && gui.length() > 28) {
                gui = gui.substring(0,28) + "..."; 
              }
              out.println(gui);
              %>
          </mm:function></strong>
          <mm:field name="number" write="false" jspvar="nmbr" vartype="String"><% rnr = nmbr; %></mm:field>
          <%= nt %><br />
		  </span>
		</mm:node>
		<%-- edit relation: the this nt preceding nodetype is the relation --%>
		<%
		String stukje = path.substring( 0, path.indexOf(nt) - 1 );
		String relnode = stukje.substring( (stukje.lastIndexOf(",") + 1), stukje.length());
		// out.println("relnode: " + relnode);
		%>
		<mm:context>
		<mm:node element="<%= relnode %>" id="rel">
		  <a title="edit relation" href="#" onclick="toggle('edit_<mm:write referid="rel" />');return false;" ><img class="buttonrelate" src="/mmbase/edit/my_editors/img/mmbase-relation-right.gif" alt="edit-relation" width="21" height="20" /></a>
		  <div class="editrelation" style="display: none;" id="edit_<mm:write referid="rel" />">
		  <form method="post" action="<mm:url referids="nrs,nodes,fields,paths,nr,rel" />">
		  <mm:maywrite><mm:import reset="true" id="formtype">input</mm:import></mm:maywrite>
		  <mm:maywrite inverse="true"><mm:import id="formtype">guivalue</mm:import></mm:maywrite>
		  <mm:fieldlist type="edit" fields="owner">
			<strong><mm:fieldinfo type="guiname" /></strong> <mm:fieldinfo type="name" />
			<mm:fieldinfo type="$formtype" /><br />
		  </mm:fieldlist>
		  <mm:maydelete><input type="submit" name="deleterel" value="Delete" /></mm:maydelete>
		  <mm:maywrite><input type="submit" name="changerel" value="Change" /></mm:maywrite>
		  </form>
		 </div>
		</mm:node>
		</mm:context>
		
		<%-- related node(s) of this related node :-) --%>
		<%
		List list = listRelatedNodes(rnr, nodes);
		for (Iterator li = list.iterator(); li.hasNext();) {
		  String yannr = (String)li.next();
		  %>
		  <div class="relnode">&nbsp;&nbsp;
		  <mm:node number="<%= yannr %>" notfound="skip">
			related <mm:nodeinfo type="type" /> :
			<mm:maywrite><a href="<mm:url referids="nrs,nodes,fields,paths">
			  <mm:param name="nr" value="<%= yannr %>"/>
			</mm:url>"></mm:maywrite><mm:function name="gui" /><mm:maywrite></a></mm:maywrite>
			
		  </mm:node>
		  </div>
		  <%
		}
		%>
        
        
        </td>
      </tr>
      
      <% } %>
    </mm:list>
    <%    
  } // end pmap iterator
} // end if pmap.containsKey(nr)
// #################################
%>
</mm:node>
</table>
</mm:compare><%-- /referid="paths" value="" inverse="true" --%>
