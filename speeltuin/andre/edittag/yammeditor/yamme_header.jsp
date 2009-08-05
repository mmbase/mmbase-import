<%!
 /**
  * Get a path from pmap assoicated
  * @param map Map with associated sets with nodenrs and paths
  * @param str String being a nodenumber
  * @return String path from our Map
  */
  public static String relatedPath(Map map, String nodenr) {
    String path = "";
	if (map.containsKey(nodenr)) {
	  Set ps = (Set)map.get(nodenr);
	  for (Iterator i = ps.iterator(); i.hasNext();) {
		path = (String)i.next();
	  }
	}
	return path;
  }

public static Map createMap(String paths) {
  Map map = new HashMap();
  if (paths != null && !paths.equals("")) {
    String[] strArray = paths.split(";");				// 53.mags0,posrel,news
    for (int i = 0; i < strArray.length; i++) {
      int p = strArray[i].indexOf("_");
      String key = strArray[i].substring(0, p);			// 53
      String value = strArray[i].substring(p + 1);		// mags0,posrel,news
      
      if (!map.containsKey(key)) {
        Set s = new HashSet();		// first time, new set f.e. set 53
        s.add(value);				// add path to set 53, f.e. mags0,posrel,news
        map.put(key, s);			// put the set in the map
      } else {
        HashSet hs = (HashSet)map.remove(key);
        hs.add(value);				// add path to set
        map.put(key, hs);			// put the set back in the map
      }
    }
  }
  return map;
}
%>
<%
// startnodes: 676 (startnode)
// paths: 676_news,posrel,images (startnode.path)
// nodes: 676_345 (startnode_nodenr)
// fields: 676_news.number (startnode_nodetype.field)
Map nmap = createMap(nodes);		// Map with nodes: key = nodenr, value = startnode
Map pmap = createMap(paths);
Map fmap = createMap(fields);

// split the startnodes
String[] str_nodes = nrs.split(";");
%>
<div id="header">
  <div id="titles"><img class="yammbutton" src="/mmbase/edit/my_editors/img/mmbase-edit-40.gif" alt="edit" width="41" height="40" />
  <strong>yammeditor using the edit tag</strong></div>
  <div id="startnodes">startnodes: 
    <%
    String sn = "";
    for (int i = 0; i < str_nodes.length; i++) {
        sn = str_nodes[i];
    %>
        <a href="<mm:url referids="nrs,fields,paths"><mm:param name="sn" value="<%= sn %>" /><mm:param name="nr" value="<%= sn %>" /></mm:url>"><%= sn %></a> |
    <% 
    }
    // get the first object!
    sn = str_nodes[0];
    %>
    <mm:import id="nr" externid="nr" jspvar="nr" vartype="String"><%= sn %></mm:import>
  </div>
</div>
