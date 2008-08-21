<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.evenementen.Evenement,org.mmbase.bridge.*" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String objectID = request.getParameter("o");
   PaginaHelper ph = new PaginaHelper(cloud);
   String subsiteID = ph.getSubsiteRubriek(cloud,objectID);
   int count = 0;
%>   
<%@include file="/editors/mailer/util/memberid_get.jsp" %>
<mm:node number="<%=objectID%>">
  <div class="headerBar" style="width:396px;">DOSSIERS</div>
  <table cellspacing="0">
<%
   NodeList nlDossiers = PoolUtil.getByPools(cloud,objectID,"pagina,posrel","dossier",
                                             "","posrel.pos",memberid);
   int maxDossiers = nlDossiers.size();
   for(int i=0; i<maxDossiers; i++) {
     Node node = nlDossiers.getNode(i);
%>
     <mm:node number="<%= node.getStringValue("dossier.number") %>">   
      <mm:field name="number" jspvar="dossier_number" vartype="String" write="false">
      <mm:field name="naam" jspvar="dossier_naam" vartype="String" write="false">
<%
        if (count%2==0 && count>1) { %><tr><td colspan="2" style="height:3px;"><div class="rule" style="margin:0px;"></div></td></tr><% }

        count++;
        if (count%2==1) { %><tr><% }
%>
        <td style="vertical-align:top;width:50%;">
          <table cellspacing="0">
          <tr>
            <td style="vertical-align:top;">
              <mm:list nodes="<%=dossier_number%>" path="dossier,posrel,images">
                <mm:node element="images"><img src="<mm:image  template="s(68)+part(0,0,68,68)" />" alt="" border="0" /></mm:node>
              </mm:list>
            </td>
            <td style="vertical-align:top;">
              <span class="colortitle">
                <% readmoreURL = ""; %>
                <mm:list nodes="<%=dossier_number%>" path="dossier,posrel,pagina,gebruikt,paginatemplate"
                        fields="paginatemplate.url,pagina.number" max="1"
                        constraints="<%= "pagina.number != '" + objectID + "'" %>">
                  <mm:field name="pagina.number" write="false" jspvar="pagina_number" vartype="String">
                  <mm:field name="paginatemplate.url" write="false" jspvar="template_url" vartype="String">
<%
                    readmoreURL = template_url + "?p=" + pagina_number + "&d=" + dossier_number;
%>
                  </mm:field>
                  </mm:field>
                </mm:list>
<%
                if(!readmoreURL.equals("")) {
%>
                  <a href="<%= readmoreURL %>" class="maincolor_link_shorty">
<%
                }
                %><span class="colortitle"><%= dossier_naam.toUpperCase() %></span><%
                if(!readmoreURL.equals("")) {
                  %></a><%
                }
%>
              </span><br/>
              <mm:list nodes="<%=dossier_number%>" path="dossier,readmore,artikel"
                      fields="artikel.number,artikel.titel" orderby="readmore.pos" max="3">
                <mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false">
                  <table style="width:100%;" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td style="text-align:left;vertical-align:middle;">
                        <a href="<%= readmoreURL + "&a=" + artikel_number %>" class="hover"><mm:field name="artikel.titel" /></a>
                      </td>
                    </tr>
                  </table>
                </mm:field>
              </mm:list>
              <mm:list nodes="<%=dossier_number%>" path="dossier,posrel,evenement"
                      fields="evenement.number" orderby="posrel.pos" max="3">
                <mm:field name="evenement.number" jspvar="evenement_number" vartype="String" write="false">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td style="text-align:left;vertical-align:middle;">
                        <a href="events.jsp?p=agenda&e=<%= Evenement.getNextOccurence(evenement_number) %>" class="hover"><mm:field name="evenement.titel" /></a>
                      </td>
                    </tr>
                  </table>
                </mm:field>
              </mm:list>
            </td>
          </tr>
          </table>
        </td>
<%
        if (count%2==0) { %></tr><% }
%>   
      </mm:field>
      </mm:field>
     </mm:node>
<%
   }
   if (count%2==1) { %><td>&nbsp;</td></tr><% }
%>
  </table>
</mm:node>
</mm:cloud>
