<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,
      org.mmbase.module.Module,
      org.mmbase.bridge.Cloud,
      org.mmbase.bridge.Node,
      org.mmbase.bridge.NodeList,
      nl.leocms.evenementen.Evenement,
      nl.leocms.util.PaginaHelper,
      nl.leocms.util.tools.SearchUtil,
		net.sf.mmapps.modules.lucenesearch.LuceneModule,
      org.apache.lucene.index.IndexReader,
      org.apache.lucene.analysis.*,
      org.apache.lucene.search.*,
		net.sf.mmapps.modules.lucenesearch.util.*,
      org.apache.lucene.queryParser.QueryParser,
      org.apache.lucene.document.Document" %>

<mm:import jspvar="offsetID" externid="offset" id="offset">1</mm:import>

<mm:cloud jspvar="cloud">
   <%   
      boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);
   
      String subsiteID = request.getParameter("root");
      String sQuery = request.getParameter("query");

      String pContentType = request.getParameter("pcontentype"); 
      if (pContentType == null) pContentType = "0";
      int contentType = new Integer(pContentType).intValue();
      
      HashSet hsetPagesNodes = new HashSet();
   
      PaginaHelper ph = new PaginaHelper(cloud);
      String templateUrl = "";
   %>
   <form name="eventForm" action="">
      <input type="hidden" name="offset" value="0"/>
      
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
         <tr>
            <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;">&nbsp;ZOEKTERM&nbsp;</td>
            <td class="maincolor" style="width:210px;padding:0px;padding-right:1px;vertical-align:top;">
               <input name="query_frm" type="text" value="<%= ( sQuery == null ? "" : sQuery ) %>" style="width:100%;border:0">
            </td>
         </tr>
         <tr>
            <td colspan="2" style="padding-left:80px;line-height:0.85em;height:7px;"></td>
         </tr>
         <tr>
            <td class="maincolor" style="width:177px;padding:5px;line-height:0.85em;">&nbsp;ZOEK NAAR&nbsp;</td>         
            <td class="maincolor" style="width:210px;padding:0px;vertical-align:top;">
               <select name="pcontentype" style="width:100%;border:0;font-size:12px;" />
               <%
                  Map contentTypes = new TreeMap();
                  contentTypes.put("0","Alles");
                  contentTypes.put("1","Artikelen");
                  contentTypes.put("2","Producten uit de webwinkel");
                  contentTypes.put("3","Activiteiten");
                  contentTypes.put("4","Natuurgebieden");

                  Iterator it1 = contentTypes.entrySet().iterator();              
                  while(it1.hasNext()) {
                     Map.Entry mapentry = (Map.Entry) it1.next();
                     
                     // see if the option is selected priviously
                     String isSelected = (pContentType.equals(mapentry.getKey())) ? "selected " : "";
                     out.println("<option " + isSelected + "value=\"" + mapentry.getKey() + "\">" + mapentry.getValue() + "</option>");
                 }
               %>              
               </select>
            </td>
               
         </tr>
         <tr>
            <td colspan="2" style="padding-left:80px;line-height:0.85em;height:7px;"></td>
         </tr>
         <tr>
            <td></td>
            <td><input type="submit" value="OPNIEUW ZOEKEN" class="submit_image" style="width:100%;"/></td>
         </tr>
     </table>
   </form>
   <br/>
   
   <table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table>
   <%   
   
      LuceneModule mod = (LuceneModule) Module.getModule("lucenemodule"); 
      if (mod != null && !sQuery.equals("")) {   
         
         %><%@include file="../../includes/time.jsp" %><%
         %><!-- searching on <%= sQuery %> --><% 

         net.sf.mmapps.modules.lucenesearch.LuceneManager lm  = mod.getLuceneManager();
         net.sf.mmapps.modules.lucenesearch.SearchConfig cf = lm.getConfig();

         SearchUtil su = new SearchUtil();
         String qStr = su.queryString(sQuery);
         
         int pageSize = 10;
         int thisOffset = Integer.parseInt(offsetID);  
         List searchResults = new ArrayList();
         
         boolean searchAllNodes = false;
         
         switch (contentType) {
            case 0:
               searchAllNodes = true;
            case 1:
               %><%@include file="results/articles_nodes.jsp" %><%
               if (!searchAllNodes) break;
            case 2:
               %><%@include file="results/items_nodes.jsp" %><%
               if (!searchAllNodes) break;
            case 3:  
               %><%@include file="results/evenement_nodes.jsp" %><%
               if (!searchAllNodes) break;
            case 4:
               %><%@include file="results/natuurgebieden_nodes.jsp" %><%
               if (!searchAllNodes) break;
         }
         
         int listSize = searchResults.size();       

         if(listSize > pageSize) {
            int firstEvent = pageSize * thisOffset+1;
            int lastEvent = pageSize * (thisOffset+1);
            
            if (lastEvent > listSize) lastEvent = listSize;
            %>
               Gevonden <%= listSize %> resultaten; getoond <%= firstEvent %><% if(firstEvent != lastEvent) { %> - <%= lastEvent %><% } %><br/>
               <%@include file="offsetlinks.jsp" %>
               <table class="dotline"><tr><td height="3"></td></tr></table><br/>
            <% 
         } else if (listSize == 0) {
            %>Er zijn geen resultaten gevonden, die voldoen aan uw zoekopdracht.<%
         } else {
            %>Gevonden <%= listSize %> resultaten
            <table class="dotline"><tr><td height="3"></td></tr></table><%
         } 
         
         for (int i = (thisOffset * pageSize); i < ((thisOffset+1) * pageSize); i++) {           
            if (i == searchResults.size()) break;
            out.println(searchResults.get(i));   
         }
         
         %><table width="100%" background="media/dotline.gif"><tr><td height="3"></td></tr></table><%
         
         if(listSize > pageSize) {
            %><%@include file="offsetlinks.jsp" %><% 
         }          
      }  
   %>  
</mm:cloud>




