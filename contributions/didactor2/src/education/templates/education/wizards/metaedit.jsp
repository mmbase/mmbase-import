<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import = "java.util.Date" %>
<%@page import = "java.util.HashSet" %>
<%@page import = "java.util.ArrayList" %>
<%@page import = "java.util.Enumeration" %>
<%@page import = "java.util.Iterator" %>
<%@page import = "java.text.SimpleDateFormat" %>

<html>
   <head>
   <head>

   <body style="padding-left:10px">

      <%
         String sNode = request.getParameter("number");
         String EMPTY_VALUE = "...";

        
         String sRequest_Submitted = request.getParameter("submitted");
         String sRequest_DoCloseMetaeditor = request.getParameter("close");

         if (sRequest_Submitted == null)
         {//Empty form
            if (request.getParameter("set_defaults") != null)
            {

               
               %>
                  <jsp:include page="metaedit_form.jsp">
                     <jsp:param name="node" value="<%= sNode %>" />
                     <jsp:param name="set_defaults" value="true" />
                  </jsp:include>
               <%
            }
            else
            {

		  
               
               %>
                  <jsp:include page="metaedit_form.jsp">
                     <jsp:param name="node" value="<%= sNode %>" />
                  </jsp:include>
               <%
            }
         }
         else
         {   
            //Submit has been pressed
            //------------------------------- Check form -------------------------------
            HashSet hsetPassedNodes = new HashSet();
            HashSet hsetHaveToBeNotEmpty = new HashSet();
            HashSet hsetAssignedMetadefinitions = new HashSet();
	      HashSet hsetAssignedVocabularies = new HashSet();

	      Enumeration enumParamNames;
            ArrayList arlistParVals = new ArrayList();
            ArrayList arliConstraintErrors = new ArrayList();

            boolean bFillOk = true;
            boolean bSizeOk = true;
            boolean bConstraintOk = true;
            
            %>
               <mm:content postprocessor="reducespace">
                  <mm:cloud>

                     <mm:listnodes type="metastandard" orderby="name">
                     <mm:field name="name" jspvar="sMetastandardName" vartype="String">

                     <%
                         
				 enumParamNames = request.getParameterNames();

            	       while(enumParamNames.hasMoreElements())
        	             {
	                       String sParameter = (String) enumParamNames.nextElement(); 
	  	        	     String[] arrstrParameters = request.getParameterValues(sParameter);
          	                 if(sParameter.charAt(0) == 'm')
          	                   {
                                
		        		    String sMetadataDefinitionID = sParameter.substring(1);
                               			
		                      for (int i=0 ; i < arrstrParameters.length ; i++)
		                         {
                   
                         		    if(arrstrParameters[i]!= null && 
                                          !arrstrParameters[i].equals(EMPTY_VALUE) && 
                                          !arrstrParameters[i].equals(""))
                                        {
                            			// Put metadefinition in the list
                                          hsetAssignedMetadefinitions.add(sMetadataDefinitionID);
		            
                                          // Let's check this metadefinition metavocabularies
                     %>
                      

                     <mm:list nodes="<%=sMetadataDefinitionID %>"  path="metadefinition,related,metavocabulary" 
                                     searchdir="destination" fields="metavocabulary.number,metavocabulary.value" >
                        <mm:field name="metavocabulary.value" jspvar="sVocValue" vartype="String">
                           <mm:field name="metavocabulary.number" jspvar="sVocNumber" vartype="String">
                           
                              <%                                   
                        
                                 if(sVocValue.equals(arrstrParameters[i]))
                                    hsetAssignedVocabularies.add(sVocNumber);
                                                                                            
                              %>

                           </mm:field>
                        </mm:field>
                     </mm:list>

                     <%
                                        } // end of if

		                         } // end of for (int i=0 ; i < arrstrParameters.length ; i++)

                                 } // end of if if(sParameter.charAt(0) == 'm')

                           } // end of while

                      %>

                      <mm:relatednodes type="metadefinition" role="posrel">
                         <mm:field name="required" jspvar="sRequired" vartype="String">
                            <mm:field name="number" jspvar="sNumber"  vartype="String">
                                <mm:field name="name" jspvar="sName"    vartype="String">
                                    <mm:field name="type" jspvar="sType"  vartype="String">

                                     <%
                                       if(sRequired.equals("1"))
                                       {
                                          hsetHaveToBeNotEmpty.add(sNumber);
                                       }
				             %>

                                     <!-- Now we have to check whether we fill this values or not according type2 relations  -->

			                   <mm:list nodes="<%=sNumber %>"  path="metadefinition,posrel,metadefinition2" 
                                             searchdir="source" fields="metadefinition.number,metadefinition.name,posrel.pos" >

           		                      <mm:field name="metadefinition2.number" jspvar="rMd" vartype="String">
  				                   <mm:field name="posrel.pos" jspvar="rPos" vartype="String">

               			             <%			                  
                                              if(rPos.equals("1") && hsetAssignedMetadefinitions.contains(rMd))
                                               {
                                                  hsetHaveToBeNotEmpty.add(sNumber);

                                               } // end of if
                                                                                 
			                         %>

			                         </mm:field> <%-- posrel.pos --%>
                                        </mm:field> <%-- metadefinition2.number --%>
       			             </mm:list> <!-- path="metadefinition,posrel,metadefinition2" -->

			                   <mm:list nodes="<%=sNumber %>"  path="metadefinition,constraints,metavocabulary" searchdir="source"
                                            fields="metavocabulary.number,metavocabulary.value,constraints.type,constraints.maxvalues,constraints.minvalues" >

			                      <mm:field name="metavocabulary.number" jspvar="rMv"   vartype="String">
                                           <mm:field name="metavocabulary.value"  jspvar="rMval" vartype="String">
 				                      <mm:field name="constraints.type" jspvar="vType"      vartype="String">
                                                 <mm:field name="constraints.maxvalues" jspvar="iMaxvalues"    vartype="Integer">
					                      <mm:field name="constraints.minvalues" jspvar="iMinvalues"    vartype="Integer">

               				                <%	

                                                    String sParam = "m"+sNumber;
                                                    String[] arrstrParams = request.getParameterValues(sParam);
                                          
                                                    if(arrstrParams == null)
                                                    {
                                                       arrstrParams = new String[0];
                                                    }                                              
							
                                         		                  
                                                    if(vType.equals("1") && hsetAssignedVocabularies.contains(rMv))
                                                    {
                                                       hsetHaveToBeNotEmpty.add(sNumber);								
                                                    }
                                                                                      
                                                    if(vType.equals("2") && 
                                                       hsetAssignedVocabularies.contains(rMv) &&                                             
                                                       sType.equals("1") &&
                                                       (arrstrParams.length < iMinvalues.intValue() || 
							             arrstrParams.length > iMaxvalues.intValue() ))
                                                     {
							              bConstraintOk = false;
                                                        arliConstraintErrors.add("Metavocabulary "+
                                                                        rMval+
                                                                        " requires more or less values from metadefinition "+
                                                                        sName);
                                                     }
                                           

                                                   if(vType.equals("3") && 
                                                      hsetAssignedVocabularies.contains(rMv) && 
							            hsetAssignedMetadefinitions.contains(sNumber)) 
                                                      {
								         bConstraintOk = false;
                                                         arliConstraintErrors.add("Metavocabulary "+rMval+" forbids metadefinition "+sName);
                                                      }

                                          
                                                    %>
 
 			                                  </mm:field>
					                   </mm:field>
					                </mm:field>
                                           </mm:field>
                                       </mm:field>
                        
			                  </mm:list> <%-- path="metadefinition,constraints,metavocabulary"  --%>
                                   
                                 </mm:field>
                              </mm:field>
                            </mm:field>
                          </mm:field>
                        </mm:relatednodes>
                      </mm:field>     <%-- matastandard name --%>
                   </mm:listnodes> <%-- metastandards     --%>

                   <%
                        
                     if ((!sRequest_Submitted.equals("add")) && (!sRequest_Submitted.equals("remove")) && 
                          (request.getParameter("set_defaults") == null))
                       {
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
                                                                }
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
                                                      if(sReq.equals("1")) {
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
                                                      if(sReq.equals("1")) {
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
                                    response.sendRedirect("metaedit.jsp?number=" + sNode + "&random=" + (new Date()).getTime());
                                 }
                              }
                           }

                        }

                        //If we set only defaults values, always redirect
                        if((request.getParameter("set_defaults") != null) && (!sRequest_Submitted.equals("add")) && (!sRequest_Submitted.equals("remove")))
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

                        %>

                                 <%@include file="metaedit_header.jsp" %>
                                 <br/>
                                 Standaard metadata waarden zijn opgeslagen.
                                 <script>
                                    window.setInterval("document.location.href='metaedit.jsp?number=<%= sNode %>&random=<%= (new Date()).getTime()%>&set_defaults=true<%=sParList%>;'", 3000);
                                 </script>
                                 <br/><br/>
                                 <a href="javascript:history.go(-1)"><font style="color:red; font-weight:bold; text-decoration:none">Terug naar het metadata formulier</font></a>

                      <%
                       } // end of if((request.getParameter("set_defaults") != null) 

                        //---------------- Process parameters and store values ---------------
                        enumParamNames = request.getParameterNames();
                        while(enumParamNames.hasMoreElements())
                        {//Go throw all parameters from http-request
                           String sParameter = (String) enumParamNames.nextElement();


                           if((sParameter.equals("add")) && (sRequest_Submitted.equals("remove")))
                           {
                             //we have got "remove lang string" command
                           }


                           if(sParameter.charAt(0) == 'm')
                           {
                              String sMetadataDefinitionID = sParameter.substring(1);

                              //It creates a new or gets exist metadata
                              %>
                                 <jsp:include page="metaedit_metaget.jsp" flush="true">
                                    <jsp:param name="node" value="<%= sNode %>" />
                                    <jsp:param name="metadata_definition" value="<%= sMetadataDefinitionID%>" />
                                 </jsp:include>
                              <%
                              String sMetadataID = (String) session.getAttribute("metadata_id");


                              //Add this node to the "passed" list
                              //We shouldn't erase values from it in future
                              hsetPassedNodes.add(sMetadataID);

                              String sMetadataDefinitionType = "";
                              //Type of metadata
                              %>
                                 <mm:node number="<%= sMetadataDefinitionID%>">
                                    <mm:field name="type" jspvar="sType" vartype="String" write="false">
                                       <%
                                          sMetadataDefinitionType = sType;
                                       %>
                                    </mm:field>
 							 
                                 </mm:node>
                              <%

                              if(sMetadataDefinitionType.equals("1"))
                              {//Vocabulary

                              %>
                                  <mm:node number="<%= sMetadataID %>">
                                     <mm:relatednodes type="metavocabulary">
                                         <mm:deletenode deleterelations="true"/>
                                      </mm:relatednodes>
                                   </mm:node>
                              <%
                                 String[] arrstrParameters = request.getParameterValues(sParameter);
                                 if ((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(EMPTY_VALUE)))
                                 {
                                    for(int f = 0; f < arrstrParameters.length; f++)
                                    {
                                       %>
                                          <mm:remove referid="vocabulary_id" />
                                          <mm:remove referid="metadata_id" />
                                          <mm:createnode type="metavocabulary" id="vocabulary_id">
                                             <mm:setfield name="value"><%= arrstrParameters[f] %></mm:setfield>
                                          </mm:createnode>
                                          <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                             <mm:createrelation source="metadata_id" destination="vocabulary_id" role="posrel" />
                                          </mm:node>
                                       <%
                                    }
                                 }
                              }
                              if(sMetadataDefinitionType.equals("2"))
                              {//Date
                                 %>
                                    <mm:node number="<%= sMetadataID %>">
                                       <mm:relatednodes type="metadate">
                                          <mm:deletenode deleterelations="true"/>
                                       </mm:relatednodes>
                                    </mm:node>
                                 <%
                                 String[] arrstrParameters = request.getParameterValues(sParameter);
                                 try
                                 {
                                    String sDate = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
                                    SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");
                                    Date date = df.parse(sDate);
                                    %>
                                       <mm:remove referid="date_id" />
                                       <mm:remove referid="metadata_id" />
                                       <mm:createnode type="metadate" id="date_id">
                                          <mm:setfield name="value"><%= date.getTime() / 1000 %></mm:setfield>
                                       </mm:createnode>
                                       <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                          <mm:createrelation source="metadata_id" destination="date_id" role="posrel" />
                                       </mm:node>
                                    <%
                                 }
                                 catch(Exception e)
                                 {
                                    %>
                                       <mm:node number="<%= sMetadataID %>">
                                          <mm:relatednodes type="metadate">
                                             <mm:deletenode deleterelations="true"/>
                                          </mm:relatednodes>
                                       </mm:node>
                                    <%
                                 }
                              }

                              if(sMetadataDefinitionType.equals("3"))
                              {//Strings with langs code
                                 boolean bNoData = true;
                                 %>
                                    <mm:node number="<%= sMetadataID %>">
                                       <mm:relatednodes type="metalangstring">
                                          <mm:deletenode deleterelations="true"/>
                                          <%
                                             bNoData = false;
                                          %>
                                       </mm:relatednodes>
                                    </mm:node>
                                 <%
                                 String[] arrstrParameters = request.getParameterValues(sParameter);
                                 for(int f = 0; f < arrstrParameters.length ; f += 2)
                                 {// in cycle we are getting all values from request
                                    if(sRequest_Submitted.equals("remove"))
                                    {// if we have got "remove" command, we should skip the
                                       String[] sTarget = request.getParameter("add").split("\\,");
                                       if (sMetadataDefinitionID.equals(sTarget[0]))
                                       {
                                          if (sTarget[1].equals("" + f/2)) continue;
                                       }
                                    }

                                    String sLang = arrstrParameters[f];
                                    String sCode = arrstrParameters[f + 1];
                                    if ((sCode.equals("")) && (arrstrParameters.length == 2) && (bNoData))
                                    {// if we have got only one paramter and it is empty, and there are no existing nodes in db then we shouldn't store this lang string
                                       break;
                                    }

                                    %>
                                       <mm:remove referid="lang_id" />
                                       <mm:remove referid="metadata_id" />
                                       <mm:createnode type="metalangstring" id="lang_id">
                                          <mm:setfield name="language"><%= sLang %></mm:setfield>
                                          <mm:setfield name="value"><%= sCode %></mm:setfield>
                                       </mm:createnode>
                                       <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                          <mm:createrelation source="metadata_id" destination="lang_id" role="posrel">
                                             <mm:setfield name="pos"><%= f + 1 %></mm:setfield>
                                          </mm:createrelation>
                                       </mm:node>
                                    <%
                                 }
                              }
                              if(sMetadataDefinitionType.equals("4"))
                              {//Duration
                                 boolean bNotEmpty = false;
                                 %>
                                    <mm:node number="<%= sMetadataID %>">
                                       <mm:relatednodes type="metadate">
                                          <%
                                             bNotEmpty = true;
                                          %>
                                          <mm:deletenode deleterelations="true"/>
                                       </mm:relatednodes>
                                    </mm:node>
                                 <%
                                 String[] arrstrParameters = request.getParameterValues(sParameter);
                                 try
                                 {
                                    String sDateBegin = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
                                    String sDateEnd   = arrstrParameters[5] + "-" + arrstrParameters[6] + "-" + arrstrParameters[7] + "|" + arrstrParameters[8] + ":" + arrstrParameters[9];
                                    SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");

                                    Date dateBegin = df.parse(sDateBegin);
                                    Date dateEnd   = df.parse(sDateEnd);
                                    %>
                                       <mm:remove referid="date_id" />
                                       <mm:remove referid="metadata_id" />
                                       <mm:createnode type="metadate" id="date_id">
                                          <mm:setfield name="value"><%= dateBegin.getTime() / 1000 %></mm:setfield>
                                       </mm:createnode>
                                       <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                          <mm:createrelation source="metadata_id" destination="date_id" role="posrel">
                                             <mm:setfield name="pos">1</mm:setfield>
                                          </mm:createrelation>
                                       </mm:node>

                                       <mm:remove referid="date_id" />
                                       <mm:remove referid="metadata_id" />
                                       <mm:createnode type="metadate" id="date_id">
                                          <mm:setfield name="value"><%= dateEnd.getTime() / 1000 %></mm:setfield>
                                       </mm:createnode>
                                       <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                          <mm:createrelation source="metadata_id" destination="date_id" role="posrel">
                                             <mm:setfield name="pos">2</mm:setfield>
                                          </mm:createrelation>
                                       </mm:node>
                                    <%
                                 }
                                 catch(Exception e)
                                 {
                                    %>
                                       <mm:node number="<%= sMetadataID %>">
                                          <mm:relatednodes type="metadate">
                                             <mm:deletenode deleterelations="true"/>
                                          </mm:relatednodes>
                                       </mm:node>
                                    <%
                                 }
                              }
                           }
                        }

                        if(sRequest_Submitted.equals("add"))
                        {//we have got "add lang string" command
                         //It creates a new or gets exist metadata
                           %>
                              <jsp:include page="metaedit_metaget.jsp" flush="true">
                                 <jsp:param name="node" value="<%= sNode %>" />
                                 <jsp:param name="metadata_definition" value="<%= request.getParameter("add") %>" />
                              </jsp:include>
                           <%
                           String sMetadataID = (String) session.getAttribute("metadata_id");

                           //add a new field
                           %>
                              <mm:remove referid="lang_id" />
                              <mm:remove referid="metadata_id" />
                              <mm:createnode type="metalangstring" id="lang_id"/>

                              <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                 <mm:createrelation source="metadata_id" destination="lang_id" role="posrel">
                                    <mm:setfield name="pos"><%= 1000000 %></mm:setfield>
                                 </mm:createrelation>
                              </mm:node>
                           <%
                        }
                     %>
                     <mm:node number="<%= sNode %>">
                        <mm:relatednodes type="metadata">
                           <mm:field name="number" jspvar="sID" vartype="String">
                              <%
                                 if(!hsetPassedNodes.contains(sID))
                                 {// ------------ Remove old values ---------------
                                    %>
                                       <mm:relatednodes type="metavocabulary">
                                          <mm:deletenode deleterelations="true"/>
                                       </mm:relatednodes>
                                       <mm:relatednodes type="metadate">
                                          <mm:deletenode deleterelations="true"/>
                                       </mm:relatednodes>
                                       <mm:relatednodes type="metalangstring">
                                          <mm:deletenode deleterelations="true"/>
                                       </mm:relatednodes>
                                    <%
                                 }
                              %>
                           </mm:field>
                        </mm:relatednodes>
                     </mm:node>
                     <%
                        if((sRequest_Submitted.equals("add")) || (sRequest_Submitted.equals("remove")))
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
 
                                       }// end of for
					
                               } // ind of if

 	        	       }// end of while

                     %>
                     <jsp:include page="metaedit_form.jsp?node=<%= sNode %><%= sParList %>" flush="true" />
                                                               
                     <%
                      }  // end of if
                     
                     // We have to update metadate.value field (it is handled by metadata builder) %>
                     <mm:node number="<%= sNode %>">
                        <mm:relatednodes type="metadata">
                           <mm:setfield name="value">-</mm:setfield>
                        </mm:relatednodes>
                     </mm:node>
                  </mm:cloud>
               </mm:content>
            <%
         } // end of Submit has been pressed
      %>
   </body>
</html>

