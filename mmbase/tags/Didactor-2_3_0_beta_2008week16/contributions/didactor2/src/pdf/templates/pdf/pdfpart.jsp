<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<%@page import="java.io.UnsupportedEncodingException" %>
<%@page import="java.io.StringBufferInputStream" %>
<%@page import="java.io.ByteArrayOutputStream" %>
<%@page import="java.io.PrintWriter" %>
<%@page import="org.w3c.tidy.Tidy" %>

<mm:content postprocessor="reducespace" expires="0">
<%--
    we need to get the parameters from the request by hand
    for some reason mmbase keeps the old versions if we use
    referid (even with from="parameters" !)
--%>
<mm:import id="number" jspvar="number"><%= request.getParameter("partnumber") %></mm:import>
<mm:import id="level"  jspvar="level" vartype="Integer"><%= request.getParameter("level") %></mm:import>
<mm:cloud jspvar="cloud" method="anonymous">
<%@include file="/shared/setImports.jsp" %>

<mm:import id="providerurl">geen.standaard.aanbieders.url</mm:import>

<mm:node referid="provider">
    <mm:relatednodes type="urls">
        <mm:first>
            <mm:import id="providerurl" reset="true"><mm:field name="url"/></mm:import>
        </mm:first>
    </mm:relatednodes>
</mm:node>


<mm:node number="$number">
<mm:nodeinfo type="type" id="node_type" jspvar="nodeType">
<mm:import jspvar="layout" id="layout"><mm:field name="layout"/></mm:import>

<% System.err.println("rendering node "+number+" of type "+nodeType+" at level "+level+" with layout "+layout); %>
<mm:compare referid="node_type" value="learnblocks">
    <mm:import id="display">1</mm:import>
</mm:compare>
<mm:compare referid="node_type" value="pages">
    <mm:import id="display">1</mm:import>
</mm:compare>
<mm:compare referid="node_type" value="educations">
    <mm:import id="display">1</mm:import>
</mm:compare>


<mm:present referid="display">

   <%// Here we are makeing a whole text part %>
   <mm:import jspvar="text" reset="true">
      <%// The title of page %>
      <mm:field name="showtitle">
         <mm:compare value="1">
            <mm:field name="name" jspvar="sTitle" vartype="String" write="false">
               <%= "<h"+level.toString()+" style=\"font-size: "+((5-level.intValue())*2+11)+"px\">" %><mm:field name="title"/><mm:field name="name"/><%= "</h"+level.toString()+">" %>
               <br/>
            </mm:field>
         </mm:compare>
      </mm:field>

      <mm:field name="intro" escape="none" jspvar="sRawIntro" vartype="String">
         <%= doCleaning(sRawIntro) %>
      </mm:field>
      <mm:field name="text" escape="none" jspvar="sRawText" vartype="String">
         <%= doCleaning(sRawText) %>
      </mm:field>
      
     <%
         String baseUrl = getServletContext().getInitParameter("internalUrl");
         if (baseUrl == null)
         {
            throw new ServletException("Please set 'internalUrl' in the web.xml!");
         }
      %>
      <%// Go through all paragraphs %>
         <mm:related path="posrel,paragraphs" orderby="posrel.pos" directions="UP">
            <mm:first>
               <table  border="0" cellpadding="0" cellspacing="0" width="100%">
            </mm:first>
            <mm:first inverse="true">
               <% //This is a padding for the next paragraph %>
            </mm:first>
            <mm:node element="paragraphs">
               <tr>
                  <td>
                     <mm:field name="showtitle">
                        <mm:compare value="1">
                           <%= "<h"+level.toString()+" style=\"font-size: "+((5-level.intValue())*2+11)+"px\">" %><mm:field name="title"/><mm:field name="name"/><%= "</h"+level.toString()+">" %>
                        </mm:compare>
                     </mm:field>
                     <mm:field name="body" escape="none" jspvar="sRawHTML" vartype="String">
                        <%= doCleaning(sRawHTML) %>
                     </mm:field>


                     <% // see the types/images_position at the editwizards
                           // <option id="1">rechts (oorspronkelijk formaat)</option>
                           // <option id="2">links (oorspronkelijk formaat)</option>
                           // <option id="3">rechts klein</option>
                           // <option id="4">links klein</option>
                           // <option id="5">rechts medium</option>
                           // <option id="6">links medium</option>
                           // <option id="7">volle breedte</option>
                     %>
                     <mm:related path="pos2rel,images"  max="1">
                        <mm:field name="pos2rel.pos1" jspvar="posrel_pos" vartype="Integer" write="false">
                              <mm:node element="images">
                                 <%
                                 int image_position = 3;
                                 try
                                 {
                                    image_position = posrel_pos.intValue();
                                 }
                                 catch (Exception e)
                                 {
                                 }

                                 if (image_position == 7)
                                 {
                                    %><img src="<%= baseUrl %>/img.db?<mm:field name="number"/>" border="0"/><%
                                 }
                                 else
                                 {
                                    String sImageTemplate = "";
                                    // *** medium or small image ****
                                    if((2 < image_position) && (image_position < 5))
                                    {
                                       sImageTemplate = "+s(60)";
                                    }
                                    if((4 < image_position) && (image_position<7))
                                    {
                                       sImageTemplate = "+s(180)";
                                    }
                                    String sAlign = "";
                                    if((image_position % 2) == 1)
                                    {
                                       sAlign += " align=\"right\" ";
                                    }
                                    %>
                                       <mm:import jspvar="imageUrl" reset="true"><mm:image template="<%= sImageTemplate %>"/></mm:import>
                                    <%
                                    imageUrl = baseUrl + imageUrl.substring(imageUrl.indexOf("/img.db"));
                                    %><img src="<%= imageUrl %>" border="0" <%= sAlign %> /><%
                                 }
                              %>

                           </mm:node>
                        </mm:field>
                     </mm:related>
                  </td>
               </tr>
               <tr>
                  <td>
                     <mm:import id="there_is_additional_information" reset="true">false</mm:import>
                     <mm:related path="posrel,attachments">
                        <mm:import id="there_is_additional_information" reset="true">true</mm:import>
                     </mm:related>
                     <mm:related path="posrel,urls">
                        <mm:import id="there_is_additional_information" reset="true">true</mm:import>
                     </mm:related>

                     <mm:compare referid="there_is_additional_information" value="true">
                        <br/>
                        Meer informatie

                        <table border="0" align="left" width="100%" cellpadding="0" cellspacing="0">
                           <mm:related path="posrel,urls" orderby="posrel.pos">
                              <tr>
                                 <td></td>

                                 <%
                                    String link ="";
                                 %>
                                 <mm:node element="urls">
                                    <mm:field name="url" jspvar="url" vartype="String" write="false">
                                       <%
                                          if(url.indexOf("http://") > -1)
                                          {
                                             link = url;
                                             %>
                                                <td><img src="<%= baseUrl %>/education/gfx/http_url.gif" align="right" title="" alt=""/></td>
                                                <td width="60%">&nbsp;websites:</td>
                                                <td width="100%"><%= url %></td>
                                             <%
                                          }
                                          if(url.indexOf("mailto:") > -1)
                                          {
                                             link = url;
                                             %>
                                                <mm:field name="name" jspvar="name" vartype="String" write="false">
                                                   <mm:isnotempty>
                                                   <%
                                                      link += "?subject=" + name;
                                                   %>
                                                   </mm:isnotempty>
                                                </mm:field>
                                                <td><img src="<%= baseUrl %>/education/gfx/email_url.gif" align="right" title="" alt=""/></td>
                                                <td width="60%">&nbsp;email:</td>
                                                <td width="100%"><%= url %></td>
                                             <%
                                          }
                                       %>
                                    </mm:field>
                                 </mm:node>

                              </tr>
                           </mm:related>


                           <mm:related path="posrel,attachments" orderby="posrel.pos">
                              <tr>
                                 <td></td>
                                 <td><img src="<%= baseUrl %>/education/gfx/http_url.gif" align="right" title="" alt=""/></td>
                                 <td width="60%">&nbsp;download:</td>
                                 <td width="100%">
                                    <mm:node element="attachments">
                                       <mm:field name="filename" />
                                    </mm:node>
                                 </td>
                              </tr>
                           </mm:related>
                        </table>
                     </mm:compare>
                  </td>
               </tr>
            </mm:node>
            <mm:last>
                </table>
            </mm:last>
         </mm:related>
   </mm:import>



        <mm:countrelations type="images">
            <mm:isgreaterthan value="0">
                <mm:field name="imagelayout" id="imagelayout" write="false"/>
                <mm:compare referid="layout" value="0">
                <%= text %>
                <%@include file="pdfimages.jsp"%>
                </mm:compare>
                <mm:compare referid="layout" value="1">
                <%@include file="pdfimages.jsp"%>
                <%= text %>
                </mm:compare>
                <mm:compare referid="layout" value="2">
                <table width="100%" ><tr>
                <td width="100%" valign="top"><%= text %></td><td>
                <%@include file="pdfimages.jsp"%>
                </td>
                </tr>
                </table>
                </mm:compare>
                <mm:compare referid="layout" value="3">
                <table width="100%" ><tr><td>
                <%@include file="pdfimages.jsp"%></td>
                <td width="100%" valign="top"><%= text %></td>
                </tr>
                </table>
                </mm:compare>
            </mm:isgreaterthan>
            <mm:islessthan value="1">
                <%= text %>
            </mm:islessthan>
        </mm:countrelations>


        <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos">
          <p>
            <mm:field name="showtitle">
              <mm:compare value="1">
                <b><mm:field name="title"/></b><br>
              </mm:compare>
            </mm:field>
            <i><mm:field name="description" escape="inline"/></i>
            <br>
            http://<mm:write referid="providerurl"/>/attachment.db?<mm:field name="number"/>
          </p>
          <br>
        </mm:relatednodes>

        <mm:relatednodes type="audiotapes" role="posrel" orderby="posrel.pos">
          <p>
            <mm:field name="showtitle">
              <mm:compare value="1">
                <b><mm:field name="title"/></b><br>
              </mm:compare>
            </mm:field>
            <i><mm:field name="subtitle"/></i><br>
            <mm:field name="intro" escape="inline"/>
          </p>
          <mm:field name="body" escape="p"/>
          <p>
            <mm:field name="url" />
          </p>
          <br>
        </mm:relatednodes>

        <mm:relatednodes type="videotapes" role="posrel" orderby="posrel.pos">
          <p>
            <mm:field name="showtitle">
              <mm:compare value="1">
                <b><mm:field name="title"/></b><br>
              </mm:compare>
            </mm:field>
            <i><mm:field name="subtitle"/></i><br>
            <mm:field name="intro" escape="inline"/>
          </p>
          <mm:field name="body" escape="p"/>
          <p>
            <mm:field name="url" />
          </p>
        </mm:relatednodes>

        <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos">
          <br/>
          <mm:field name="showtitle">
            <mm:compare value="1">
              <b><mm:field name="name"/></b><br>
            </mm:compare>
          </mm:field>
          <p>
            <i><mm:field name="description" escape="inline"/></i>
            <br/>
            <mm:field name="url" />
          </p>
          <br>
        </mm:relatednodes>

        <br/>

    <% if (level.intValue() < 20) { %>
        <mm:related path="posrel,learnobjects" fields="learnobjects.number" orderby="posrel.pos" searchdir="destination">
            <mm:field name="learnobjects.number" jspvar="partnumber">
                <mm:include page="pdfpart.jsp">
                    <mm:param name="partnumber"><%= partnumber %></mm:param>
                    <mm:param name="level"><%= (level.intValue()+1) %></mm:param>
                </mm:include>
            </mm:field>
        </mm:related>
    <% } %>

</mm:present>
</mm:nodeinfo>
</mm:node>
</mm:cloud>
</mm:content>



<%!
   private String doCleaning(String text)
   {
      if(text==null) { text =  ""; }

      StringBufferInputStream in;
      ByteArrayOutputStream out;
      ByteArrayOutputStream err;
      String sOut = "";

      Tidy tidy = new Tidy();

      out = new ByteArrayOutputStream();
      err = new ByteArrayOutputStream();

      tidy.setXmlOut(true);
      try {
          in = new StringBufferInputStream(text);
          tidy.setErrout(new PrintWriter(err));
          tidy.parse(in, out);
          sOut = out.toString("UTF-8");
      }
      catch ( UnsupportedEncodingException e ) {
          System.out.println( "Error in Tidy parsing: " + this.toString() + e.toString() );
      }
      return sOut;
   }
%>
