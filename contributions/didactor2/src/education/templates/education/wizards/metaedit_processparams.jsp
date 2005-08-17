<%
//If we are not adding or delete a langstring:
//Check for all required metadefinitions
//If the metadefinition is present, we remove it from "hsetHaveToBeNotEmpty"


enumParamNames = request.getParameterNames();
ArrayList arliSizeErrors = new ArrayList();

while(enumParamNames.hasMoreElements())
{
   String sParameter = (String) enumParamNames.nextElement();
   String[] arrstrParameters = request.getParameterValues(sParameter);
   if(sParameter.charAt(0) == 'm')
   {
      String sMetadataDefinitionID = sParameter.substring(1);
      %>
         <mm:node number="<%= sMetadataDefinitionID %>">
            <mm:field name="type" jspvar="sType" vartype="String">
               <mm:field name="required" jspvar="sReq" vartype="String">
                  <%
                     if(sType.equals("1"))
                     {//Vocabulary

                        %>
                           <mm:field name="maxvalues" jspvar="max" vartype="Integer">
                              <mm:field name="minvalues" jspvar="min" vartype="Integer">
                                 <%
                                    if ((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(EMPTY_VALUE)))
                                    {
                                       if((max.intValue() < arrstrParameters.length) || (min.intValue() > arrstrParameters.length ))
                                       {
                                          bSizeOk = false;
                                          %>
                                             <mm:field name="name" jspvar="name" vartype="String">
                                                <mm:field name="number" jspvar="number" vartype="String">
                                                   <%
                                                      arliSizeErrors.add("Voor " + name + " moeten minimaal " + min + " en maximaal " + max + " waarden worden ingevuld.");
                                                   %>
                                                </mm:field>
                                             </mm:field>
                                           <%
                                        }
                                     }
                                     else
                                     {
                                        bFillOk = false;
                                     };

                                  %>

                               </mm:field>
                           </mm:field>
                        <%
                     }
                     if(sType.equals("2"))
                     {//Date
                        try
                        {
                           String sDate = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
                           SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");
                           Date date = df.parse(sDate);
                           hsetHaveToBeNotEmpty.remove(sMetadataDefinitionID);
                        }
                        catch(Exception e)
                        {
                           if(sReq.equals("1"))
                           {
                              bFillOk = false;
                           }
                        }
                     }
                     if(sType.equals("4"))
                     {//Duration
                        try
                        {
                           String sDateBegin = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
                           String sDateEnd   = arrstrParameters[5] + "-" + arrstrParameters[6] + "-" + arrstrParameters[7] + "|" + arrstrParameters[8] + ":" + arrstrParameters[9];
                           SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");
                           Date date = df.parse(sDateBegin);
                           date = df.parse(sDateEnd);
                           hsetHaveToBeNotEmpty.remove(sMetadataDefinitionID);
                        }
                        catch(Exception e)
                        {
                           if(sReq.equals("1"))
                           {
                              bFillOk = false;
                           }
                        }
                     }
                  %>
               </mm:field>
            </mm:field>
         </mm:node>

      <%

      if ((bFillOk) && (hsetHaveToBeNotEmpty.contains(sMetadataDefinitionID)))
      {
         hsetHaveToBeNotEmpty.remove(sMetadataDefinitionID);
      }

   }
}

%>
   <%@include file="metaedit_header.jsp" %>
   <br/>
<%
//Header, if error
if((hsetHaveToBeNotEmpty.size() > 0) || (arliSizeErrors.size() > 0) || (arliConstraintErrors.size() > 0))
{
   %>
      <style type="text/css">
         body{
            font-family:arial;
            font-size:12px;
         }
      </style>


      <font style="color:red; font-size:11px; font-weight:bold; text-decoration:none; letter-spacing:1px;"><font style="font-size:15px">O</font>NTBREKENDE VERPLICHTE VELDEN!</font>
      <br/>
   <%
}
//List of errors for empty nodes
if(hsetHaveToBeNotEmpty.size() > 0)
{
   %>
      U wordt verzocht de volgende onvolkomendheden te herstellen:
      <br/>
      <ul>
   <%
}
for(Iterator it = hsetHaveToBeNotEmpty.iterator(); it.hasNext();)
{
   String sIsEmpty = (String) it.next();
   %>
      <li>
         <mm:node number="<%= sIsEmpty %>">
            <mm:field name="name"/>
            <br/>
         </mm:node>
      </li>
   <%
}
//List constraint errors
for(Iterator it = arliConstraintErrors.iterator(); it.hasNext();)
{
   String sConstraintError = (String) it.next();
   %><li><%= sConstraintError %></li><%
}


//List of error for errors with "size"
for(Iterator it = arliSizeErrors.iterator(); it.hasNext();)
{
   String sSizeError = (String) it.next();
   %><li><%= sSizeError %></li><%
}
%></ul><%



//Use JS to synchronize values in tree
if((!bFillOk) || (!bSizeOk) || (!bConstraintOk) || (hsetHaveToBeNotEmpty.size() > 0))
{

   %>


      <a href="javascript:history.go(-1)">
      <font style="color:red; font-weight:bold; text-decoration:none">Terug naar het metadata formulier
      </font>
      </a>

      <script>
         try
         {
            top.frames['menu'].document.images['img_<%= sNode %>'].src='gfx/metaerror.gif';
         }
         catch(err)
         {
         }
      </script>
<%
}
else
{
   if(session.getAttribute("show_metadata_in_list") == null)
   {//We use metaeditor from content_metadata or not?

     String sParList = "";

     enumParamNames = request.getParameterNames();
     while(enumParamNames.hasMoreElements())
     {
                String sParameter = (String) enumParamNames.nextElement();
                String[] arrstrParameters = request.getParameterValues(sParameter);

                if(sParameter.charAt(0) == 'm')
              {
                            for(int i=0; i < arrstrParameters.length; i++)
                     {

                      sParList += "&" + sParameter + "=" + arrstrParameters[i] ;

                     } // end of for(int i=0; i < arrstrParameters.length; i++)

              } // end of if(sParameter.charAt(0) == 'm')

   } // end of while(enumParamNames.hasMoreElements())
%>
Metadata is opgeslagen.
<script>
     try
       {
          top.frames['menu'].document.images['img_<%= sNode %>'].src='gfx/metavalid.gif';
       }
       catch(err)
       {
       }
       window.setInterval("document.location.href='metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime()%><%=sParList%>;'", 3000);
</script>
<br/><br/>
<a href="javascript:history.go(-1)"><font style="color:red; font-weight:bold; text-decoration:none">Terug naar het metadata formulier</font></a>
<%
} // end of if(session.getAttribute("show_metadata_in_list") == null)

else   // It should be default here
{
      if ((sRequest_DoCloseMetaeditor != null) && (sRequest_DoCloseMetaeditor.equals("yes")))
      {
                     //User has selected "SAVE&CLOSE"
         response.sendRedirect((String) session.getAttribute("metalist_url"));
      }
      else
      {
        String sParList = "";

        enumParamNames = request.getParameterNames();

        while(enumParamNames.hasMoreElements())
         {
           String sParameter = (String) enumParamNames.nextElement();
           String[] arrstrParameters = request.getParameterValues(sParameter);

           if(sParameter.charAt(0) == 'm')
           {
                     for(int i=0; i < arrstrParameters.length; i++)
                       {
                          sParList += "&" + sParameter + "=" + arrstrParameters[i] ;
                       }

            }// end of if(sParameter.charAt(0) == 'm')

        } // end of while


         response.sendRedirect("metaedit.jsp?number=" + sNode + "&random=" + (new Date()).getTime()+sParList);
      }
   }
} %>