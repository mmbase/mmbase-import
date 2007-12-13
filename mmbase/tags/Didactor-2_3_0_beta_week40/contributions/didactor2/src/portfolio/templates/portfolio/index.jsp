<%--
  TODO: This JSP is much, much to big and complicated.
  
  This template shows the personal workspace (in dutch: persoonlijke werkruimte).
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%-- expires is set so renaming a folder does not show the old name 
--%><mm:content postprocessor="reducespace" expires="0">
<mm:cloud rank="didactor user">
  <mm:locale language="$language">
    <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="extraheader">
        <title><di:translate key="portfolio.mydocuments" /></title>
      </mm:param>
    </mm:treeinclude>
    <mm:import externid="currentfolder">-1</mm:import>
    
    <mm:import id="myuser"><mm:write referid="user"/></mm:import>
    <mm:import externid="contact">-1</mm:import>
    <mm:compare referid="contact" value="-1" inverse="true">
      <mm:import id="myuser" reset="true"><mm:write referid="contact"/></mm:import>
    </mm:compare>
    
    <%-- Determine if my documents or shared documents is started --%>
    <mm:import externid="typeof">-1</mm:import>
    
    <%-- Get the first folder if no folder selected --%>
    <mm:compare referid="typeof" value="-1" inverse="true">
      <mm:compare referid="currentfolder" value="-1">
        <mm:node referid="myuser" >
          <mm:relatednodescontainer type="portfolios">
            <mm:constraint field="type" value="${typeof}" />
            <mm:relatednodes>
              <mm:relatednodes type="folders" role="posrel" orderby="posrel.pos">
                <mm:first>
                  <mm:remove referid="currentfolder" />
                  <mm:node id="currentfolder" />
                </mm:first>
              </mm:relatednodes>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>
      </mm:compare>
    </mm:compare>
    
    <mm:import id="mayeditentries">false</mm:import>
    
    <mm:isgreaterthan referid="user" value="0">
      <di:hasrole role="teacher">
        <mm:list nodes="$user" path="people1,classes,people2,portfolios,folders" constraints="folders.number=$currentfolder" max="1">
          <mm:import id="mayeditentries" reset="true">true</mm:import>
        </mm:list>
      </di:hasrole>
      
      <di:hasrole role="teacher" inverse="true">
        <mm:compare referid="typeof" value="1" inverse="true">
          <mm:list nodes="$user" path="people,portfolios,folders" constraints="folders.number=$currentfolder" max="1">
            <mm:import id="mayeditentries" reset="true">true</mm:import>
          </mm:list>
        </mm:compare>
      </di:hasrole>
    </mm:isgreaterthan>
    
    <mm:import id="mayeditfolders">false</mm:import>
    <mm:compare referid="user" value="$myuser">
      <mm:compare referid="typeof" value="1" inverse="true">
        <mm:import id="mayeditfolders" reset="true">true</mm:import>
      </mm:compare>
    </mm:compare>




    <mm:import externid="action_delete.x" id="action_delete" from="parameters"/>
    <mm:import externid="action_move.x" id="action_move" from="parameters"/>
    <mm:import externid="ids" vartype="List"/>
    
    <mm:present referid="action_delete">
      <mm:redirect page="/portfolio/deleteitems.jsp" referids="$referids,currentfolder,ids,typeof,contact?">
        <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
      </mm:redirect>
    </mm:present>
    <mm:present referid="action_move">
      <mm:import id="currenttime"><%= System.currentTimeMillis() %></mm:import>
      <mm:redirect page="/portfolio/moveitems.jsp" referids="$referids,currentfolder,ids,typeof,currenttime,contact?">
        <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
      </mm:redirect>
    </mm:present>
    

    <div class="rows">
      
      <div class="navigationbar">
        <div class="titlebar">
          <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="portfolio.portfolio" />" alt="<di:translate key="portfolio.portfolio" />"/>
          <di:translate key="portfolio.portfolio" />
        </div>
      </div>
      
      <div class="folders">
        
        <div class="folderHeader">
          <di:translate key="portfolio.portfolio" />
        </div>

        <div class="folderBody">
          
          <mm:compare referid="mayeditfolders" value="true">
            <mm:compare referid="typeof" value="-1" inverse="true">
              <a href="<mm:treefile page="/portfolio/createfolder.jsp" objectlist="$includePath" referids="$referids,contact?">
                <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
                <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                </mm:treefile>">
                <img src="<mm:treefile page="/portfolio/gfx/map maken.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.createfolder" />" alt="<di:translate key="portfolio.createfolder" />" />
              </a>
            </mm:compare>
            
            <mm:isgreaterthan referid="currentfolder" value="0">
              <a href="<mm:treefile page="/portfolio/changefolder.jsp" objectlist="$includePath" referids="$referids,contact?">
                <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
                <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                </mm:treefile>">
                  
                <img src="<mm:treefile page="/portfolio/gfx/map hernoemen.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.renamefolder" />" alt="<di:translate key="portfolio.renamefolder" />" />
              </a>
              
              <a href="<mm:treefile page="/portfolio/deletefolder.jsp" objectlist="$includePath" referids="$referids,contact?">
                <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
                <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                </mm:treefile>">
                <img src="<mm:treefile page="/portfolio/gfx/verwijder map.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.deletefolder" />" alt="<di:translate key="portfolio.deletefolder" />" />
              </a>
            </mm:isgreaterthan>
            <br /><br /><%-- WTF --%>
                  
            <%-- folder is open --%>
            <mm:compare referid="typeof" value="-1">
              <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
            </mm:compare>
            <%-- folder is closed --%>
            <mm:compare referid="typeof" value="-1" inverse="true">
              <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
            </mm:compare>
          </mm:compare>
          
          <a href="index.jsp?contact=<mm:write referid="contact"/>">Portfolio cockpit</a><br/>
          
          
          <mm:node referid="myuser" >
            <mm:relatednodes type="portfolios" orderby="type">
              
              
              <mm:import id="currentportfolionumber"><mm:field name="number"/></mm:import>
              <mm:import id="currentportfoliotype"><mm:field name="type"/></mm:import>
              <mm:remove referid="currentportfolioisopen"/>
              
              <mm:isgreaterthan referid="user" value="0">
                <mm:compare referid="currentportfoliotype" value="0">
                  
                  <%-- folder is open --%>
                  <mm:compare referid="typeof" value="0">
                    <mm:import id="currentportfolioisopen">true</mm:import>
                    <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
                  </mm:compare>
                  <%-- folder is closed --%>
                  <mm:compare referid="typeof" value="0" inverse="true">
                    <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
                  </mm:compare>
                  <a href="<mm:treefile page="index.jsp" objectlist="$includePath" referids="$referids,contact?"><mm:param name="typeof">0</mm:param></mm:treefile>">Ontwikkelingsgericht portfolio</a><br/>
                  
                </mm:compare>
                <mm:compare referid="currentportfoliotype" value="1">
                  
                  <%-- folder is open --%>
                  <mm:compare referid="typeof" value="1">
                    <mm:import id="currentportfolioisopen">true</mm:import>
                    <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
                  </mm:compare>
                  <%-- folder is closed --%>
                  <mm:compare referid="typeof" value="1" inverse="true">
                    <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
                  </mm:compare>
                  <a href="<mm:treefile page="index.jsp" objectlist="$includePath" referids="$referids,contact?"><mm:param name="typeof">1</mm:param></mm:treefile>">Assessment portfolio</a><br/>
                  
                </mm:compare>
              </mm:isgreaterthan>
              
              <mm:compare referid="currentportfoliotype" value="2">
                
                <%-- folder is open --%>
                <mm:compare referid="typeof" value="2">
                  <mm:import id="currentportfolioisopen">true</mm:import>
                  <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
                </mm:compare>
                <%-- folder is closed --%>
                <mm:compare referid="typeof" value="2" inverse="true">
                  <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
                </mm:compare>
                <a href="<mm:treefile page="index.jsp" objectlist="$includePath" referids="$referids,contact?"><mm:param name="typeof">2</mm:param></mm:treefile>">Showcase portfolio</a><br/>
                
              </mm:compare>
              
              
              <mm:compare referid="mayeditfolders" value="true">
                <mm:field name="number" id="portfolionumber">
                  <mm:import externid="up"/>
                  <mm:present referid="up">
                    <mm:list nodes="$portfolionumber" path="portfolios,posrel,folders" constraints="folders.number=$up" max="1">
                      <mm:import id="posrel"><mm:field name="posrel.number"/></mm:import>
                      <mm:import id="pos" jspvar="pos" vartype="Integer"><mm:field name="posrel.pos"/></mm:import>
                      <mm:list nodes="$portfolionumber" path="portfolios,posrel,folders" constraints="posrel.pos =($pos - 1)" max="1">
                        <mm:import id="posrel2"><mm:field name="posrel.number"/></mm:import>
                      </mm:list>
                      <mm:node referid="posrel">
                        <mm:setfield name="pos"><%= pos.intValue() - 1 %></mm:setfield>
                      </mm:node>
                      <mm:node referid="posrel2">
                        <mm:setfield name="pos"><%= pos.intValue() %></mm:setfield>
                      </mm:node>
                    </mm:list>
                  </mm:present>
                  
                  <mm:import externid="down"/>
                  <mm:present referid="down">
                    <mm:list nodes="$portfolionumber" path="portfolios,posrel,folders" constraints="folders.number=$down" max="1">
                      <mm:import id="posrel"><mm:field name="posrel.number"/></mm:import>
                      <mm:import id="pos" jspvar="pos" vartype="Integer"><mm:field name="posrel.pos"/></mm:import>
                      <mm:list nodes="$portfolionumber" path="portfolios,posrel,folders" constraints="posrel.pos =($pos + 1)" max="1">
                        <mm:import id="posrel2"><mm:field name="posrel.number"/></mm:import>
                      </mm:list>
                      <mm:node referid="posrel">
                        <mm:setfield name="pos"><%= pos.intValue() + 1 %></mm:setfield>
                      </mm:node>
                      <mm:node referid="posrel2">
                        <mm:setfield name="pos"><%= pos.intValue() %></mm:setfield>
                      </mm:node>
                    </mm:list>
                  </mm:present>   
                </mm:field>
              </mm:compare>
              
              <mm:present referid="currentportfolioisopen">
                
                <mm:relatednodes role="posrel" type="folders" orderby="posrel.pos">
                  
                  <mm:import id="currentnumber"><mm:field name="number"/></mm:import>
                  
                  <%-- folder is open --%>
                  <mm:compare referid="currentfolder" referid2="currentnumber">
                    &nbsp;<img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
                  </mm:compare>
                  
                  <%-- folder is closed --%>
                  <mm:compare referid="currentfolder" referid2="currentnumber" inverse="true">
                    
                    &nbsp;<img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
                  </mm:compare>
                  
                  <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,contact?">
                    <mm:param name="currentfolder"><mm:field name="number" /></mm:param>
                    <mm:param name="typeof"><mm:write referid="currentportfoliotype"/></mm:param>
                    </mm:treefile>">
                    <mm:field name="name" />
                  </a>
                  <mm:compare referid="mayeditfolders" value="true">
                    <mm:first inverse="true"><a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,currentfolder,contact,typeof"/>&amp;up=<mm:write referid="currentnumber"/>">(-)</a></mm:first>
                    <mm:last inverse="true"><a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,currentfolder,contact,typeof"/>&amp;down=<mm:write referid="currentnumber"/>">(+)</a></mm:last>
                  </mm:compare>
                  <br />
                  
                </mm:relatednodes>
                
              </mm:present>
              
            </mm:relatednodes>
          </mm:node>
          
        </div>
        
      </div>
      
      <div class="mainContent">
        
        <mm:compare referid="typeof" value="-1" inverse="true">
          
          <form action="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,typeof,contact?"/>" method="POST">
          <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>">
          
          <div class="contentHeader">
            
            <mm:node number="$currentfolder" notfound="skip">
              <mm:field name="name"/>
            </mm:node>
            
          </div>
          
          <div class="contentSubHeader">
            
            <mm:isgreaterthan referid="currentfolder" value="0">
              <mm:compare referid="mayeditentries" value="true">
                <a href="<mm:treefile page="/portfolio/adddocument.jsp" objectlist="$includePath" referids="$referids,contact?">
                  <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                  <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
                  <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                  </mm:treefile>">
                  <img src="<mm:treefile page="/portfolio/gfx/document plaatsen.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.adddocument" />" alt="<di:translate key="portfolio.adddocument" />" />
                </a>
                
                <a href="<mm:treefile page="/portfolio/addurl.jsp" objectlist="$includePath" referids="$referids,contact?">
                  <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                  <mm:param name="callerpage">/portfolio/index.jsp</mm:param>
                  <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                  </mm:treefile>">
                  <img src="<mm:treefile page="/portfolio/gfx/bron plaatsen.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.addsource" />" alt="<di:translate key="portfolio.addsource" />" />
                </a>
                
                
                <input type="image" name="action_move" src="<mm:treefile page="/portfolio/gfx/verplaats geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.moveselected" />" alt="<di:translate key="portfolio.moveselected" />" />
                
                <input type="image" name="action_delete" src="<mm:treefile page="/portfolio/gfx/verwijder geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="portfolio.deleteselected" />" alt="<di:translate key="portfolio.deleteselected" />"/>
              </mm:compare>
            </mm:isgreaterthan>
            
          </div>
          
          <div class="contentBodywit">
            <mm:import id="gfx_attachment"><mm:treefile page="/portfolio/gfx/mijn documenten.gif" objectlist="$includePath" referids="$referids" /></mm:import>
            <mm:import id="gfx_url"><mm:treefile page="/portfolio/gfx/bronnen.gif" objectlist="$includePath" referids="$referids" /></mm:import>
            <mm:import id="gfx_page"><mm:treefile page="/portfolio/gfx/pagina.gif" objectlist="$includePath" referids="$referids" /></mm:import>
            <mm:import id="gfx_chatlog"><mm:treefile page="/portfolio/gfx/chatverslag.gif" objectlist="$includePath" referids="$referids" /></mm:import>
            
            <mm:node number="$currentfolder" notfound="skip">
              
              <mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>
              
              <%-- Show also the nodes below in the table --%>
              <mm:relatednodes type="attachments" id="myattachments">
                <mm:remove referid="objectnumber"/>
                <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
                <%
                linkedlist.add( objectnumber );
                %>
              </mm:relatednodes>
              <mm:relatednodes type="urls" id="myurls">
                <mm:remove referid="objectnumber"/>
                <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
                <%
                linkedlist.add( objectnumber );
                %>
              </mm:relatednodes>
              <mm:relatednodes type="pages" id="mypages">
                <mm:remove referid="objectnumber"/>
                <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
                <%
                linkedlist.add( objectnumber );
                %>
              </mm:relatednodes>
              <mm:hasnodemanager name="chatlogs">
                <mm:relatednodes type="chatlogs" id="mychatlogs">
                  <mm:remove referid="objectnumber"/>
                  <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
                  <%
                  linkedlist.add( objectnumber );
                  %>
                </mm:relatednodes>
              </mm:hasnodemanager>
              
              
              <mm:listnodescontainer type="object">
                <mm:constraint field="number" referid="linkedlist" operator="IN"/>
                
                <di:table maxitems="10">
                  
                  <di:row>
                    <mm:compare referid="mayeditentries" value="true"><di:headercell><input type="checkbox" onclick="selectAllClicked(this.form, this.checked)"/></di:headercell></mm:compare>
                    <di:headercell><di:translate key="portfolio.type" /></di:headercell>
                    <di:headercell><di:translate key="portfolio.title" /></di:headercell>
                    <di:headercell><di:translate key="portfolio.reactions" /></di:headercell>
                    <di:headercell><di:translate key="portfolio.date" /></di:headercell>
                    <di:headercell><di:translate key="portfolio.description" /></di:headercell>
                    
                  </di:row>
                  
                  <mm:listnodes>
                    <mm:import id="mayread" reset="true">false</mm:import>
                    <mm:relatednodes type="portfoliopermissions" max="1">
                      <mm:field name="readrights">
                        <mm:compare value="1">
                          <mm:isgreaterthan referid="user" value="0">
                            <mm:list nodes="$user" path="people1,classes,people2,portfolios,folders"  constraints="folders.number=$currentfolder" max="1">
                              <mm:import id="mayread" reset="true">true</mm:import>
                            </mm:list>
                          </mm:isgreaterthan>
                        </mm:compare>
                        <mm:compare value="2">
                          <mm:isgreaterthan referid="user" value="0">
                            <di:hasrole role="teacher">
                              <mm:list nodes="$user" path="people1,classes,people2,portfolios,folders"  constraints="folders.number=$currentfolder" max="1">
                                <mm:import id="mayread" reset="true">true</mm:import>
                              </mm:list>
                            </di:hasrole>
                          </mm:isgreaterthan>
                        </mm:compare>
                        <mm:compare value="3">
                          <mm:isgreaterthan referid="user" value="0">
                            <mm:import id="mayread" reset="true">true</mm:import>                           
                          </mm:isgreaterthan>
                        </mm:compare>
                        <mm:compare value="4">
                          <mm:import id="mayread" reset="true">true</mm:import>
                        </mm:compare>
                      </mm:field>
                    </mm:relatednodes>
                    <mm:isgreaterthan referid="user" value="0">
                      <mm:list nodes="$user" path="people,portfolios,folders" constraints="folders.number=$currentfolder" max="1">
                        <mm:import id="mayread" reset="true">true</mm:import>
                      </mm:list> 
                    </mm:isgreaterthan>
                    <mm:compare referid="mayread" value="true">
                      
                      <mm:field name="number" id="itemnumber">
                        <mm:listnodes type="daymarks" constraints="mark <= $itemnumber" orderby="mark" directions="down" max="1">
                          <mm:field name="daycount" jspvar="dayCount" vartype="Integer">
                            <mm:import id="itemdate" reset="true"><%= dayCount.intValue()*60*60*24 %></mm:import>
                          </mm:field>
                        </mm:listnodes>
                      </mm:field>
                      
                      <di:row>
                        <mm:compare referid="mayeditentries" value="true">
                          <di:cell><input type="checkbox" name="ids" value="<mm:field name="number"/>"></input></di:cell>
                        </mm:compare>
                        
                        <mm:remove referid="link"/>
                        <mm:import id="link"><a href="<mm:treefile page="/portfolio/showitem.jsp" objectlist="$includePath" referids="$referids">
                          <mm:param name="contact"><mm:write referid="contact"/></mm:param>
                          
                          <mm:param name="currentitem"><mm:field name="number"/></mm:param>
                          <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                          <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                          </mm:treefile>">
                        </mm:import>
                        
                        <mm:remove referid="objecttype"/>
                        <mm:import id="objecttype"><mm:nodeinfo type="type"/></mm:import>
                        <mm:compare referid="objecttype" value="attachments">
                          <di:cell><img src="<mm:write referid="gfx_attachment"/>" title="<di:translate key="portfolio.folderitemtypedocument" />" alt="<di:translate key="portfolio.folderitemtypedocument" />" /></di:cell>
                          <di:cell><mm:write referid="link" escape="none"/><mm:field name="title" /></a></di:cell>
                          <di:cell><mm:countrelations type="postings"/></di:cell>
                          <di:cell><mm:write referid="itemdate"><mm:time format="d/M/yyyy"/></mm:write></di:cell>
                          <di:cell><mm:field name="description" /></di:cell>
                        </mm:compare>
                        <mm:compare referid="objecttype" value="urls">
                          <mm:import id="urllink" jspvar="linkText"><mm:field name="url"/></mm:import>
                          <%
                          if ( linkText.indexOf( "http://" ) == -1 ) {
                          %>
                          <mm:remove referid="urllink"/>
                          <mm:import id="urllink">http://<mm:field name="url"/></mm:import>
                          <%
                          }
                          %>
                          <di:cell><img src="<mm:write referid="gfx_url"/>" title="<di:translate key="portfolio.folderitemtypeurl" />" alt="<di:translate key="portfolio.folderitemtypeurl" />" /></di:cell>
                          <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                          <di:cell><mm:countrelations type="postings"/></di:cell>
                          <di:cell><mm:write referid="itemdate"><mm:time format="d/M/yyyy"/></mm:write></di:cell>
                          <di:cell><mm:field name="description" /></di:cell>
                        </mm:compare>
                        <mm:compare referid="objecttype" value="pages">
                          <di:cell><img src="<mm:write referid="gfx_page"/>" title="<di:translate key="portfolio.folderitemtypepage" />" alt="<di:translate key="portfolio.folderitemtypepage" />" /></di:cell>
                          <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                          <di:cell><mm:countrelations type="postings"/></di:cell>
                          <di:cell><mm:write referid="itemdate"><mm:time format="d/M/yyyy"/></mm:write></di:cell>
                          <di:cell><mm:field name="text" /></di:cell>
                        </mm:compare>
                        <mm:compare referid="objecttype" value="chatlogs">
                          <di:cell><img src="<mm:write referid="gfx_chatlog"/>" title="<di:translate key="portfolio.folderitemtypechatlog" />" alt="<di:translate key="portfolio.folderitemtypechatlog" />" /></di:cell>
                          <di:cell><mm:write referid="link" escape="none"/><di:translate key="portfolio.folderitemtypechatlog" /><mm:field name="number"/></a></di:cell>
                          <di:cell><mm:countrelations type="postings"/></di:cell>
                          <di:cell><mm:field name="date"></mm:field></di:cell> <!-- TODO show correct date -->
                          <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
                        </mm:compare>
                        
                      </di:row>
                    </mm:compare>
                  </mm:listnodes>
                  
                </di:table>
                
              </mm:listnodescontainer>
              
            </mm:node>
            
          </div>
          
        </form>
        
      </mm:compare>
      
      
      <mm:compare referid="typeof" value="-1">
        
        <div class="contentBodywit">
          
          <mm:import externid="edit"/>
          <mm:compare referid="edit" value="true" inverse="true">
            
            <mm:node referid="myuser">

              <di:translate key="portfolio.myinfo" />:

              <table class="Font">
                <tr>
                  <td>
                    <table class="Font">
                      <tr>
                        <td><mm:field name="initials" write="false"><mm:fieldinfo type="guiname"/></mm:field>:</td>
                        <td><mm:field name="initials"/></td>
                      </tr>
                      <tr>
                        <td><mm:field name="firstname" write="false"><mm:fieldinfo type="guiname"/></mm:field>:</td>
                        <td><mm:field name="firstname"/></td>
                      </tr>
                      <tr>
                        <td><mm:field name="lastname" write="false"><mm:fieldinfo type="guiname"/></mm:field>:</td>
                        <td><mm:field name="lastname"/></td>
                      </tr>
                      <tr>
                        <td style="vertical-align: top"><mm:field name="remarks" write="false"><mm:fieldinfo type="guiname"/></mm:field>:</td>
                        <td><mm:field name="remarks" escape="p"/></td>
                      </tr>
                    </table>
                  </td>
                  <td>
                    <mm:node number="$myuser">
                      <mm:relatednodes type="images">
                        <img src="<mm:image template="s(200x180)"/>"/>
                      </mm:relatednodes>
                    </mm:node>
                  </td>
                  <mm:compare referid="myuser" value="$user">
                    <td>
                      <a href="index.jsp?edit=true"><img src="<mm:treefile page="/portfolio/gfx/edit.gif" objectlist="$includePath"/>" title="edit" alt="edit" border="0"></a>
                    </td>
                  </mm:compare>
                </tr>
              </table>

              
            </mm:node>


          </mm:compare>
          
          
          <mm:compare referid="edit" value="true">
            
            <mm:include page="/portfolio/edit.user.jspx" />

            <form name="cancel" method="POST" action="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath"/>">
              <input class="formbutton" type="submit" name="cancel" value="<di:translate key="portfolio.back"/>" />
            </form>

          </mm:compare>

        </mm:compare>


      </div>
    </div>
    <script>
      
      function selectAllClicked(frm, newState) {
      if (frm.elements['ids'].length) {
      for(var count =0; count < frm.elements['ids'].length; count++ ) {
      var box = frm.elements['ids'][count];
      box.checked=newState;
      }
      }
      else {
      frm.elements['ids'].checked=newState;
      }
      }
      
    </script>
    

    <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
  </mm:locale>
</mm:cloud>
</mm:content>
