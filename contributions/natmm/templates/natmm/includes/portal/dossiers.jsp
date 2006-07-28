<%@page import="nl.leocms.evenementen.Evenement,nl.leocms.util.PaginaHelper" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String objectID = request.getParameter("o");
   PaginaHelper ph = new PaginaHelper(cloud);
   String rootID = ph.getRootRubriek(cloud,objectID);
   int count = 0;
%>
<mm:node number="<%=objectID%>">
  <div class="headerBar" style="width:100%;">DOSSIERS</div>
  <table cellspacing="0">
    <mm:related path="posrel,dossier" fields="dossier.number,dossier.naam" orderby="posrel.pos">
      <mm:field name="dossier.number" jspvar="dossier_number" vartype="String" write="false">
      <mm:field name="dossier.naam" jspvar="dossier_naam" vartype="String" write="false">
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
                  <% String relatedPage = null; %>
                  <mm:list nodes="<%= artikel_number %>" path="artikel,contentrel,pagina" fields="pagina.number">
                    <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
<%
                      if(rootID.equals(ph.getRootRubriek(cloud,pagina_number))) { relatedPage = pagina_number; } 
%>
                    </mm:field>
                  </mm:list>
                  <table style="width:100%;" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td style="text-align:left;vertical-align:middle;"><a 
                         href="<%= (relatedPage!=null ? ph.createItemUrl(artikel_number,relatedPage,"d="+dossier_number, request.getContextPath()) : "") %>"
                         class="hover"><mm:field name="artikel.titel" /></a></td>
                    </tr>
                  </table>
                </mm:field>
              </mm:list>
              <mm:list nodes="<%=dossier_number%>" path="dossier,posrel,evenement"
                      fields="evenement.number" orderby="posrel.pos" max="3">
                <mm:field name="evenement.number" jspvar="evenement_number" vartype="String" write="false">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td style="text-align:left;vertical-align:middle;"><a href="events.jsp?p=agenda&e=<%= Evenement.getNextOccurence(evenement_number) %>" class="hover"><mm:field name="evenement.titel" /></a></td>
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
    </mm:related>
<%
    if (count%2==1) { %><td>&nbsp;</td></tr><% }
%>
  </table>
</mm:node>
</mm:cloud>
