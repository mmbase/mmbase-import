<%@page import="com.lowagie.text.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp"%>
<%@include file="/education/wizards/roles_defs.jsp"%>
<mm:import id="editcontextname" reset="true">docent schermen</mm:import>
<%@include file="/education/wizards/roles_chk.jsp"%>
<%
	// SET VARIABLES HOLDING TODAY'S DATE
	java.util.Calendar currentDate=java.util.Calendar.getInstance();
	      
	int currentYear=currentDate.get(java.util.Calendar.YEAR);
	int currentMonth=currentDate.get(java.util.Calendar.MONTH)+1;
	int currentDay=currentDate.get(java.util.Calendar.DAY_OF_MONTH);
	
	String months[] = {"Januari","Februari","Maart","April","Mei","Juni","Juli","Augustus","September","October","November","December"};
	
	java.util.TreeMap pdfDocumentElements = new java.util.TreeMap();

	session.setAttribute( "pdf_document", pdfDocumentElements );
	
  Font font_title = FontFactory.getFont(FontFactory.HELVETICA,(float)12.0, Font.BOLD, new java.awt.Color( 0xED, 0x6F, 0x2C ));
  Font font_table_header = FontFactory.getFont(FontFactory.HELVETICA,(float)10.0, Font.BOLD );
  Font font = FontFactory.getFont(FontFactory.HELVETICA,(float)10.0);
%>

<script type="text/javascript">
<!--
	
	function changeURL( obj )	{
		var selectedClass = obj.options[obj.selectedIndex].value;
		var url = location + "";
		var ind = url.indexOf( "class=" );
		if( ind != -1 )
		{
			url = url.substring( 0, ind ) + "class=" + selectedClass;
		}
		else
		{
			url = url + "&class=" + selectedClass;
		}
		location.href=url;
	}
	
	function checkDateEntered() {
	  var f = null;
	  if (document.getElementById) { 
	    f = document.getElementById("dateform");
	  } else if (window.dateform) { 
	    f = window.dateform;
	  }

	  var startDate = new Date( f.start_year.value,f.start_month.value - 1,f.start_day.value );
	  var endDate = new Date( f.end_year.value,f.end_month.value - 1,f.end_day.value );
		if ((startDate.getTime() >= endDate.getTime())){
		    message = "The end date must be later than the start date";
		    alert(message);	    
		}
		else{
			location.href="reports.html?page=<%= nl.didactor.reports.util.ReportsPages.DOCUMENT_REPORTS %>&startdate=" + startDate.getTime() + "&enddate=" + endDate.getTime();
		}
	}	
//-->
</script>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader"><title><di:translate key="reports.reports" /></title></mm:param>
</mm:treeinclude>

<mm:import id="imageurl" jspvar="imageurl" vartype="String"><mm:treefile page="/pdf/headerimage.png" objectlist="$includePath" referids="$referids"/></mm:import>
<%
    String imgurl = request.getRequestURL().substring(0, request.getRequestURL().indexOf("//")+2 )+ request.getServerName() +":" + request.getServerPort()+imageurl;
    try {
      Image imgLogo = Image.getInstance(new java.net.URL( imgurl ));
      imgLogo.setAlignment(Image.ALIGN_RIGHT);
      pdfDocumentElements.put( "element0", imgLogo );
    } catch (Exception excImg) {}
  %> 

<mm:import externid="columnsNumber" jspvar="columnsNumber" vartype="Integer"/>
<mm:notpresent referid="columnsNumber">
  <mm:import externid="columnsNumber" jspvar="columnsNumber" reset="true" vartype="Integer">4</mm:import>
</mm:notpresent>

<%
	int COLUMNS_PER_PAGE = columnsNumber.intValue();
%>
<mm:import externid="offset" jspvar="offset" vartype="Integer"/>
<mm:notpresent referid="offset">
  <mm:import externid="offset" jspvar="offset" reset="true" vartype="Integer">1</mm:import>
</mm:notpresent>

<mm:import externid="classnumber" jspvar="classnumber"><%= request.getParameter( "class" ) %></mm:import>

<mm:compare referid="classnumber" value="null">
	<mm:listnodes path="classes">
		<mm:first>
			<mm:remove referid="classnumber" />
			<mm:import externid="classnumber" jspvar="classnumber"><mm:field name="number" write="true"/></mm:import>
		</mm:first>
	</mm:listnodes>
</mm:compare>

<div class="rows">
  <div class="navigationbar">
    <div class="titlebar"><di:translate key="reports.reports" /></div>
  </div>

  <div class="folders">
    <div>
    
    <form name="selectclassform" action="index.jsp">
			<input type="hidden" name="classnumber" value="0">
    	&nbsp;&nbsp;<b><di:translate key="reports.class" /></b><br/>
			<select onChange="javascript:changeURL( this )" style="margin-left:5px;margin-top:5px; width:19%">
				<mm:listnodes path="classes">
					<mm:import id="cn" jspvar="cn"><mm:field name="number"/></mm:import>
      		<option value="<%= cn %>" <%= classnumber.equals(cn)?"selected":"" %>><mm:field name="name" /></option>
      	</mm:listnodes>
      </select>    
    </form>
    </div>
    <div class="folderBody">
    
			<a href="<mm:url page="/reports/reports.html"><mm:param name="page" value="<%= nl.didactor.reports.util.ReportsPages.LOGIN_REPORTS %>" /><mm:param name="class" value="$classnumber"/></mm:url>">
				<di:translate key="reports.login" />
			</a><br>
			<a href="<mm:url page="/reports/reports.html"><mm:param name="page" value="<%= nl.didactor.reports.util.ReportsPages.EDUCATION_REPORTS %>" /><mm:param name="class" value="$classnumber"/></mm:url>">
				<di:translate key="reports.education" />
			</a><br>
			<a href="<mm:url page="/reports/reports.html"><mm:param name="page" value="<%= nl.didactor.reports.util.ReportsPages.LEARNOBJECT_REPORTS %>" /><mm:param name="class" value="$classnumber"/></mm:url>">
				<di:translate key="reports.page" />
			</a><br>
			<a href="<mm:url page="/reports/reports.html"><mm:param name="page" value="<%= nl.didactor.reports.util.ReportsPages.TEST_REPORTS %>" /><mm:param name="class" value="$classnumber"/></mm:url>">
				<di:translate key="reports.test" />
			</a><br>
			<a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page" value="<%= nl.didactor.reports.util.ReportsPages.DOCUMENT_REPORTS %>" /></mm:treefile>" >
				<di:translate key="reports.document" />
			</a><br>
    
    </div>
  </div>

  <div class="mainContent">
    <div class="contentHeader">
    </div>

    <div class="contentBodywit">
    
      <mm:import externid="showDocument" />
      <mm:present referid="showDocument">
        <mm:import externid="documentId" jspvar="documentId"/>
          
        <mm:listnodes path="attachments" constraints="attachments.number=$documentId">
          <mm:import id="time" jspvar="time" vartype="Long"><mm:field name="date" /></mm:import>
          <%
            java.util.Date date = new java.util.Date( time.longValue()*1000 );
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
              "EEE, MMM d, yyyy");
            String dateAsString = sdf.format( date );
          %>
          <%= dateAsString %>    
      
          <h1><mm:field name="title" /></h1>  
          <p><mm:field name="description" /></p>
          <mm:import id="attach"><mm:field name="filename" /></mm:import>
          <mm:compare referid="attach" value="unknown" inverse="true">
            <a href="../attachment.db?<%= documentId %>"><mm:field name="filename" /></a>
          </mm:compare>
        </mm:listnodes>
          
          <br><br>
          <form>
             <input type="button" class="formbutton" id="goback" value="<di:translate key="core.back" />" onClick="history.back()"/><br/>
          </form>
      </mm:present>
      
      <mm:present referid="showDocument" inverse="true">
  			<mm:import id="reports_page" externid="page"/>
  			<mm:import id="student" jspvar="student"><di:translate key="reports.student" /></mm:import>
  			<% 
  				java.util.HashMap map = ( java.util.HashMap )request.getAttribute( "map" );
          if( map != null )
              session.setAttribute( "map", map );
          if( map == null )
              map = ( java.util.HashMap )session.getAttribute( "map" );
  			%>
  			<!-- 
  				LOGIN REPORTS
  			 -->
         
  			<mm:compare referid="reports_page" value="<%= nl.didactor.reports.util.ReportsPages.LOGIN_REPORTS %>">
  
  				<h1><di:translate key="reports.login" /></h1>
  				<div align="right">
  	        <a href="<mm:url page="/reports/pdf_reports.html"></mm:url>" target="_new">
  	         <di:translate key="reports.pdf" />
  	        </a><br/><br/>
  	      </div>
  				<mm:import id="logintitle" jspvar="logintitle"><di:translate key="reports.login" /></mm:import>
          
  				<%
  				  Paragraph p1 = new Paragraph( logintitle, font_title );
				  p1.setAlignment(Image.ALIGN_LEFT);
            	  pdfDocumentElements.put( "element1", p1 ); 
          %> 
  
  				<%
  					Table table1 = new Table( 2 );
            		table1.setAlignment(Table.ALIGN_LEFT);
  					table1.setPadding(2);
  					table1.setAlignment( Element.ALIGN_LEFT );
  					table1.setWidth( 60 );
  					table1.setBorder( com.lowagie.text.Rectangle.NO_BORDER );
  					table1.setSpacing(0);				
  				%>
  				<table cellpadding="0" cellspacing="0" border="0">
  					<tr>
  						<td class="listItem">
  							<di:translate key="reports.loggedstudents" />
  							<mm:import id="loggedstudents" jspvar="loggedstudents"><di:translate key="reports.loggedstudents" /></mm:import>
  							<% 
  								Cell c1 = new Cell( new Phrase( loggedstudents, font ) );
  								c1.setBorder( com.lowagie.text.Rectangle.NO_BORDER );
  								table1.addCell( c1 ); 
  							%>
  						</td>
  						<td class="listItem">
  							<%=
  								( (java.util.ArrayList )application.getAttribute( "active_users" ) ).size()
  							%>
  							<%  
  								Cell c2 = new Cell( new Phrase( ( (java.util.ArrayList )application.getAttribute( "active_users" ) ).size() + "", font ) );
  								c2.setBorder( com.lowagie.text.Rectangle.NO_BORDER );
  								table1.addCell( c2 ); 
  							%>							
  						</td>
  					</tr>
  					<tr>
  						<td class="listItem">
  							<di:translate key="reports.registeredaccounts" />
  							<mm:import id="registeredaccounts" jspvar="registeredaccounts"><di:translate key="reports.registeredaccounts" /></mm:import>
  							<%  
  								Cell c3 = new Cell( new Phrase( registeredaccounts, font ) );
  								c3.setBorder( com.lowagie.text.Rectangle.NO_BORDER );
  								table1.addCell( c3 ); 
  							%>							
  						</td>
  						<td class="listItem">
  							<mm:listnodescontainer type="people">
  								<mm:constraint operator="null" field="username" inverse="true"/>
  								<mm:listnodes>
  									<mm:first>
  										<mm:import id="registered_accounts" jspvar="registered_accounts"><mm:size /></mm:import>
  										<mm:size write="true" />
  										<%  
  											Cell c4 = new Cell( new Phrase( registered_accounts + "", font ) );
  											c4.setBorder( com.lowagie.text.Rectangle.NO_BORDER );
  											table1.addCell( c4 ); 
  										%>							
  										
  									</mm:first>
  								</mm:listnodes>
  							</mm:listnodescontainer>						
  						</td>
  					</tr>
  				</table>
  				<br>
  
  				<% pdfDocumentElements.put( "element2", table1 ); %> 
  				
  				
  				<%
  					 Table table2 = new Table( 2 );
             		 table2.setAlignment(Table.ALIGN_LEFT);
  					 table2.setPadding(3);
  				%>
  				
  				<table class="listTable">
  					<tr>
  						<th class="listHeader"><di:translate key="reports.student" /></th>
  						<th class="listHeader"><di:translate key="reports.loggedtime" /></th>
  						<mm:import id="loggedtime" jspvar="loggedtime"><di:translate key="reports.loggedtime" /></mm:import>
  						
  						<%
  						 	Cell cell1= new Cell( new Phrase( student, font_table_header ) );
  					    cell1.setHorizontalAlignment( Element.ALIGN_CENTER );
  					    cell1.setVerticalAlignment( Element.ALIGN_MIDDLE );
  					    cell1.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  							table2.addCell(cell1);
  
  							Cell cell2= new Cell( new Phrase( loggedtime, font_table_header ) );
  					    cell2.setHorizontalAlignment( Element.ALIGN_CENTER );
  					    cell2.setVerticalAlignment( Element.ALIGN_MIDDLE );
  					    cell2.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  							table2.addCell(cell2);
  						%>
  					</tr>
  
            <mm:listnodes path="people,classes" constraints="classes.number=$classnumber">
  						<mm:import id="person"><mm:field name="number" /></mm:import>
							<di:hasrole referid="person" role="systemadministrator" inverse="true">
								<di:hasrole referid="person" role="student">
									<mm:import id="user_name" jspvar="user_name"><mm:field name="username" write="true" /></mm:import>
									<tr>
										<td class="listItem">
											<mm:import id="student_name" jspvar="student_name"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
											<mm:field name="firstname" write="true" /> <mm:field name="lastname" write="true" />
											<% table2.addCell( new Phrase( student_name, font ) ); %>
										</td>
										
										<td class="listItem" align="center">
											<% 
												String value = ( map.get( user_name ) != null ) ? nl.didactor.reports.util.TimeUtil.milisecondsToHHMMSS ( Long.decode( (map.get( user_name )).toString() ).longValue()) : "0";
											%>
											<%= value %>
											<% 
												Cell cell = new Cell( new Phrase( value, font ) );
					 					    cell.setHorizontalAlignment( Element.ALIGN_CENTER );
												table2.addCell( cell ); 
											%>
										</td>
									</tr>
								</di:hasrole>
							</di:hasrole>
  					</mm:listnodes>
  				</table>
  				<% pdfDocumentElements.put( "element3", table2 ); %> 
  								
  			</mm:compare>
  			
  			<!-- 
  				EDUCATION REPORTS
  			 -->
  			<mm:compare referid="reports_page" value="<%= nl.didactor.reports.util.ReportsPages.EDUCATION_REPORTS %>">
  			
  				<h1><di:translate key="reports.education" /></h1>
  				<div align="right">
  	        <a href="<mm:url page="/reports/pdf_reports.html"></mm:url>" target="_new">
  	         <di:translate key="reports.pdf" />
  	        </a><br/><br/>
  	      </div>
  				<mm:import id="educationtitle" jspvar="educationtitle"><di:translate key="reports.education" /></mm:import>
  				<% 
  				Paragraph p3 = new Paragraph( educationtitle, font_title );
  	      		p3.setAlignment(Paragraph.ALIGN_LEFT);
  				pdfDocumentElements.put( "element1", p3 ); %> 
  				<mm:import id="size" jspvar="size" vartype="Integer">0</mm:import>
  				<mm:listnodes path="educations,classes" constraints="classes.number=$classnumber">
  					<mm:import id="size" jspvar="size" reset="true" vartype="Integer"><mm:size /></mm:import>
  				</mm:listnodes>
  
  				<%
  					 Table table = new Table( size.intValue() + 1 );
             		 table.setAlignment(Table.ALIGN_LEFT);
  				 	 table.setWidth( 100 );
  					 table.setPadding( 3 );
  				 	 Cell cell1= new Cell( new Phrase( student, font_table_header ) );
  				   cell1.setHorizontalAlignment( Element.ALIGN_CENTER );
  				   cell1.setVerticalAlignment( Element.ALIGN_MIDDLE );
  				   cell1.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  				   table.addCell( cell1 );
  				%>
  				
  				<table class="listTable">
  					<tr>
  						<th class="listHeader"><di:translate key="reports.student" /></th>
  						<mm:listnodes path="educations,classes" constraints="classes.number=$classnumber">
  							<th class="listHeader"><mm:field name="name" write="true" /></th>
  							<mm:import id="education_name" jspvar="education_name"><mm:field name="name" /></mm:import>
  							<%
  							  Cell cell= new Cell( new Phrase( education_name, font_table_header ) );
  	 					    cell.setHorizontalAlignment( Element.ALIGN_CENTER );
  	 					    cell.setVerticalAlignment( Element.ALIGN_MIDDLE );
  						    cell.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
   						    table.addCell( cell );
  							%>							
  						</mm:listnodes>
  					</tr>
  				   
  					<mm:list path="people,classrel,classes" constraints="classes.number=$classnumber">
  						<mm:node element="people">
  							<mm:import id="person"><mm:field name="number" /></mm:import>
  							<di:hasrole referid="person" role="systemadministrator" inverse="true">
  								<di:hasrole referid="person" role="student">
  									<mm:import id="user_name" jspvar="user_name"><mm:field name="username" write="true" /></mm:import>
  									<tr>
  										<td class="listItem">
  											<mm:import id="student_name" jspvar="student_name"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
  											<mm:field name="firstname" write="true" /> <mm:field name="lastname" write="true" />
  											<% table.addCell( new Phrase( student_name, font ) ); %>
  										</td>
  
  										<mm:listnodes path="educations,classes" constraints="classes.number=$classnumber">
  											<mm:import id="education_number" jspvar="education_number"><mm:field name="number" /></mm:import>
  											<td class="listItem" align="center">
  											
  												<%
  													String value = ( map.get( user_name + "." + education_number ) != null ) ? nl.didactor.reports.util.TimeUtil.milisecondsToHHMMSS ( Long.decode( (map.get( user_name + "." + education_number ).toString()) ).longValue()) : "0";
  												%>
  												<%= value %>
  												<% 
  													Cell cell = new Cell( new Phrase( value, font ) );
  						 					    cell.setHorizontalAlignment( Element.ALIGN_CENTER );
  													table.addCell( cell ); 
  												%>
  
  											</td>
  										</mm:listnodes>
  									</tr>
  								</di:hasrole>
  							</di:hasrole>
  						</mm:node>
  					</mm:list>
  
  				</table>					
  				<% pdfDocumentElements.put( "element2", table ); %> 
  								
  			</mm:compare>	
  			
  			<!-- 
  				LEARNOBJECT REPORTS
  			 -->
  			<mm:compare referid="reports_page" value="<%= nl.didactor.reports.util.ReportsPages.LEARNOBJECT_REPORTS %>">
  
  				<h1><di:translate key="reports.page" /></h1>
  				<div align="right">
  	        <a href="<mm:url page="/reports/pdf_reports.html"></mm:url>" target="_new">
  	         <di:translate key="reports.pdf" />
  	        </a><br/><br/>
  	      </div>
  				<mm:import id="pagetitle" jspvar="pagetitle"><di:translate key="reports.page" /></mm:import>
  				<% 
        		Paragraph p4 = new Paragraph( pagetitle, font_title );
          		p4.setAlignment(Paragraph.ALIGN_LEFT);
          		pdfDocumentElements.put( "element1", p4); %> 
  
          <% 
            java.util.ArrayList pages = new java.util.ArrayList(); // list with titles of all pages
            java.util.ArrayList pageIDs = new java.util.ArrayList(); // list with id's of all pages
          %>
  				<mm:import id="size" jspvar="size" vartype="Integer">0</mm:import>
  				<mm:listnodes path="pages,learnblocks,educations,classes" constraints="classes.number=$classnumber">
            <mm:import id="pageName" jspvar="pageName"><mm:field name="name"/></mm:import>
            <mm:import id="pageId" jspvar="pageId"><mm:field name="number"/></mm:import>
            <% 
              pages.add( pageName ); 
              pageIDs.add( pageId );
            %>
  
  					<mm:import id="size" jspvar="size" reset="true" vartype="Integer"><mm:size /></mm:import>
            <mm:remove referid="pageName" />
            <mm:remove referid="pageId" />
  				</mm:listnodes>
  
  				<%
             int pageNumber = ( size.intValue() - 1 )/COLUMNS_PER_PAGE + 1; // number if pages in paging links
             int columnNum; // number of columns in the table ( without student's name )
  
             if( size.intValue() <= COLUMNS_PER_PAGE )
                 columnNum = size.intValue();
             else if( pageNumber > offset.intValue() )
                 columnNum = COLUMNS_PER_PAGE;
             else
                 columnNum = size.intValue() - (offset.intValue() - 1)*COLUMNS_PER_PAGE;
             
  					 Table table = new Table( columnNum + 1 );
             		 table.setAlignment(Table.ALIGN_LEFT);
             		 table.setWidth( 100 );
  					 table.setPadding(3);
  				 	 Cell cell1= new Cell( new Phrase( student, font_table_header ) );
  				   cell1.setHorizontalAlignment( Element.ALIGN_CENTER );
  				   cell1.setVerticalAlignment( Element.ALIGN_MIDDLE );
  				   cell1.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  				   table.addCell( cell1 );
  				%>
  
  				<table class="listTable">
  					<tr>
  						<th class="listHeader"><di:translate key="reports.student" /></th>
              <%
                for( int i = COLUMNS_PER_PAGE*(offset.intValue()-1); i < COLUMNS_PER_PAGE*(offset.intValue()-1) + columnNum; i++ )
                {
                  Cell cell= new Cell( new Phrase( pages.get( i ) + "", font_table_header ) );
                  cell.setHorizontalAlignment( Element.ALIGN_CENTER );
                  cell.setVerticalAlignment( Element.ALIGN_MIDDLE );
                  cell.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
                  table.addCell( cell );
                    
              %>
              <th class="listHeader"><%= pages.get( i ) %></th>
              <%      
                }
              %>
  					</tr>
            
            <mm:list path="people,classrel,classes" constraints="classes.number=$classnumber">
    					<mm:node element="people">
    						<mm:import id="person"><mm:field name="number" /></mm:import>
    						<di:hasrole referid="person" role="systemadministrator" inverse="true">
    							<di:hasrole referid="person" role="student">
    								<mm:import id="user_name" jspvar="user_name"><mm:field name="username" write="true" /></mm:import>
    								<tr>
    									<td class="listItem">
    										<mm:import id="student_name" jspvar="student_name"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
    										<mm:field name="firstname" write="true" /> <mm:field name="lastname" write="true" />
    										<% table.addCell( new Phrase( student_name, font ) ); %>
    									</td>
    
                      <%
                        for( int i = COLUMNS_PER_PAGE*(offset.intValue()-1); i < COLUMNS_PER_PAGE*(offset.intValue()-1) + columnNum; i++ )
                        {
                          String value = ( map.get( user_name + "." + pageIDs.get( i ) ) != null ) ? map.get( user_name + "." + pageIDs.get( i ) ).toString() : "0";
                          Cell cell = new Cell( new Phrase( value, font ) );
                          cell.setHorizontalAlignment( Element.ALIGN_CENTER );
                          table.addCell( cell ); 
                            
                      %>
                      <td class="listItem" align="center"><%= value %></td>
                      <%      
                        }
                      %>
    
    								</tr>
    							</di:hasrole>
    						</di:hasrole>
    					</mm:node>
            </mm:list>
  				</table>
          
          <!--  Paging  -->        
  				<%if( pageNumber > 1 ){ %>
            <% if( offset.intValue() > 1 ){ %><a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page">learnobjects_reports</mm:param><mm:param name="offset"><%= offset.intValue()-1 %></mm:param><mm:param name="columnsNumber"><%= columnsNumber %></mm:param></mm:treefile>"/><%}%>
  		        &lt;
  					<% if( offset.intValue() > 1 ){ %></a><%}%>
  	        <%
  	          for( int i = 1; i <= pageNumber; i++ )
  	          {
  	        %>
  	            <a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page">learnobjects_reports</mm:param><mm:param name="offset"><%= i %></mm:param><mm:param name="columnsNumber"><%= columnsNumber %></mm:param></mm:treefile>"/>
  	              <% if( offset.intValue() == i ){ %><b><%}%>
  	              <%= i %> 
  	              <% if( offset.intValue() == i ){ %></b><%}%>
  	            </a>
  	        <%}%>
            <% if( offset.intValue() < pageNumber ){ %><a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page">learnobjects_reports</mm:param><mm:param name="offset"><%= offset.intValue()+1 %></mm:param><mm:param name="columnsNumber"><%= columnsNumber %></mm:param></mm:treefile>"/><%}%>
  		        &gt;
  					<% if( offset.intValue() < pageNumber ){ %></a><%}%>
          <%}%>
          <div align="right">
  			    <form action="index.jsp">
  			    	<input type="hidden" name="offset" value="1">
  			    	<input type="hidden" name="class" value="<%= classnumber %>">
  			    	<input type="hidden" name="page" value="learnobjects_reports">
  			    	<di:translate key="reports.columnsnumber" />
  						<input type="text" name="columnsNumber" value="<%= columnsNumber %>" size="1" >
  						<input type="submit" value="OK">
  			    </form>
  				</div>        
  				<% 
            pdfDocumentElements.put( "element2", table ); 
          %> 
  			</mm:compare>						
  			
  			<!-- 
  				TEST REPORTS
  			 -->
  			<mm:compare referid="reports_page" value="<%= nl.didactor.reports.util.ReportsPages.TEST_REPORTS %>">
  
  				<h1><di:translate key="reports.test" /></h1>
  				<div align="right">
  	        <a href="<mm:url page="/reports/pdf_reports.html"></mm:url>" target="_new">
  	         <di:translate key="reports.pdf" />
  	        </a><br/><br/>
  				</div>
  				<mm:import id="testtitle" jspvar="testtitle"><di:translate key="reports.test" /></mm:import>
  				<% 
          		Paragraph p5 = new Paragraph( testtitle, font_title );
          		p5.setAlignment(Paragraph.ALIGN_LEFT);
  				pdfDocumentElements.put( "element1", p5 ); %> 
  
          <% 
            java.util.ArrayList tests = new java.util.ArrayList(); // list with titles of all tests
            java.util.ArrayList testIDs = new java.util.ArrayList(); // list with id's of all tests
          %>
  				<mm:import id="size" jspvar="size" vartype="Integer">0</mm:import>
  				<mm:listnodes path="tests,learnblocks,educations,classes" constraints="classes.number=$classnumber">
            <mm:import id="testName" jspvar="testName"><mm:field name="name"/></mm:import>
            <mm:import id="testId" jspvar="testId"><mm:field name="number"/></mm:import>
            
            <% 
              tests.add( testName ); 
  	          testIDs.add( testId );
            %>
  
  					<mm:import id="size" jspvar="size" reset="true" vartype="Integer"><mm:size /></mm:import>
            <mm:remove referid="testName" />
            <mm:remove referid="testId" />
  				</mm:listnodes>
  
  				<%
             int pageNumber = ( size.intValue() - 1 )/COLUMNS_PER_PAGE + 1; // number if pages in paging links
             int columnNum; // number of columns in the table ( without student's name and percentage )
  
             if( size.intValue() <= COLUMNS_PER_PAGE )
                 columnNum = size.intValue();
             else if( pageNumber > offset.intValue() )
                 columnNum = COLUMNS_PER_PAGE;
             else
                 columnNum = size.intValue() - (offset.intValue() - 1)*COLUMNS_PER_PAGE;				
  						
             int extraColumns = 2;
  					 if( pageNumber > 1 )
  						 extraColumns++;
             		 Table table = new Table( columnNum + extraColumns );
             		 table.setAlignment(Table.ALIGN_LEFT);
  					 table.setWidth(100);
  					 table.setPadding(3);
  				 	 Cell cell1= new Cell(new Phrase( student, font_table_header ) );
  				   cell1.setHorizontalAlignment( Element.ALIGN_CENTER );
  				   cell1.setVerticalAlignment( Element.ALIGN_MIDDLE );
  				   cell1.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  				   table.addCell( cell1 );
  				%>
  
  				<table class="listTable">
  					<tr>
  						<th class="listHeader"><di:translate key="reports.student" /></th>
  						
              <%
                for( int i = COLUMNS_PER_PAGE*(offset.intValue()-1); i < COLUMNS_PER_PAGE*(offset.intValue()-1) + columnNum; i++ )
                {
                  Cell cell= new Cell( new Phrase( tests.get( i ) + "", font_table_header ) );
                  cell.setHorizontalAlignment( Element.ALIGN_CENTER );
                  cell.setVerticalAlignment( Element.ALIGN_MIDDLE );
                  cell.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
                  table.addCell( cell );
                    
              %>
              <th class="listHeader"><%= tests.get( i ) %></th>
              <%      
                }
              %>
  						<%if( pageNumber > 1 ){
                  Cell c= new Cell( new Phrase( "...", font_table_header ) );
                  c.setHorizontalAlignment( Element.ALIGN_CENTER );
                  c.setVerticalAlignment( Element.ALIGN_MIDDLE );
                  c.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
                  table.addCell( c );
              %>
                <th class="listHeader">...</th>
              <%}%>
  						<mm:import id="percentage" jspvar="percentage"><di:translate key="reports.percentage" /></mm:import>
  						<th><di:translate key="reports.percentage" /></th>
  						<%
  		 				 	Cell cell2= new Cell( new Phrase( percentage, font_table_header ) );
  			    		cell2.setHorizontalAlignment( Element.ALIGN_CENTER );
  			    		cell2.setVerticalAlignment( Element.ALIGN_MIDDLE );
  			    		cell2.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  							table.addCell( cell2 );
  					  %>
  					<tr>
  					
  					<mm:list path="people,classrel,classes" constraints="classes.number=$classnumber">
  					<% int i = COLUMNS_PER_PAGE*(offset.intValue()-1); %>
  						<mm:node element="people">
  						<%
  						   int nof_tests= 0;
  						   int nof_tests_passed= 0;
  						%>			
  						<mm:remove referid="class"/>
  						<mm:import id="class"><mm:write referid="classnumber"/></mm:import>
  							<%@include file="find_copybook.jsp"%>
  														
  							<mm:import id="person"><mm:field name="number" /></mm:import>
  							<di:hasrole referid="person" role="systemadministrator" inverse="true">
  								<di:hasrole referid="person" role="student">
  									<mm:import id="user_name" jspvar="user_name"><mm:field name="username" write="true" /></mm:import>
  									<tr>
  										<td class="listItem">
  											<mm:import id="student_name" jspvar="student_name"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
  											<mm:field name="firstname" write="true" /> <mm:field name="lastname" write="true" />
  											<% table.addCell( new Phrase( student_name, font ) ); %>
  										</td>
  
  										<mm:listnodes path="tests,learnblocks,educations,classes" constraints="classes.number=$classnumber">
  					          	<mm:import id="testNo" reset="true"><mm:field name="number" write="true"/></mm:import>
  											<%
  											   nof_tests++;
  											%>					          	
  											<mm:relatednodescontainer path="madetests,copybooks" element="madetests">
  												<mm:constraint field="copybooks.number" referid="copybookNo" />
  												<mm:relatednodes>
  													<mm:field id="madetestNo" name="number" write="false" />
  												</mm:relatednodes>
  											</mm:relatednodescontainer>
  				          	
  											<%@include file="teststatus.jsp"%>
  											
  											<mm:compare referid="teststatus" value="passed">
  												<% nof_tests_passed++; %>
  											</mm:compare>
  											
  					          	<% if( i < COLUMNS_PER_PAGE*(offset.intValue()-1) + columnNum ){ %>
  												<td class="listItem" align="center">
  													<mm:compare referid="teststatus" value="passed">
  													<mm:import id="yes" jspvar="yes"><di:translate key="reports.yes" /></mm:import>
  	                          <a href="<mm:treefile page="/progress/student.jsp" objectlist="$includePath" referids="$referids" ><mm:param name="madetest"><mm:write referid="madetestNo"/></mm:param><mm:param name="tests"><mm:write referid="testNo"/></mm:param><mm:param name="showfeedback">true</mm:param><mm:param name="reports">true</mm:param></mm:treefile>"/>
  	                             <di:translate key="reports.yes" />
  	                          </a>
  	
  														<% 
  															Cell c = new Cell( new Phrase( yes, font ) );
  															c.setHorizontalAlignment( Element.ALIGN_CENTER );
  															table.addCell( c ); 
  														%>
  													</mm:compare>
  													<mm:compare referid="teststatus" value="passed" inverse="true">
  													<mm:import id="no" jspvar="no"><di:translate key="reports.no" /></mm:import>
  														<di:translate key="reports.no" />
  														<% 
  															Cell c = new Cell( new Phrase( no, font ) );
  															c.setHorizontalAlignment( Element.ALIGN_CENTER );
  															table.addCell( c ); 
  														%>
  													</mm:compare>
  												</td>	
  											<%}
  												i++;
  											%>
  										</mm:listnodes>
  										<%if( pageNumber > 1 ){
                          Cell c1= new Cell( new Phrase( "...", font ) );
                          c1.setHorizontalAlignment( Element.ALIGN_CENTER );
                          c1.setVerticalAlignment( Element.ALIGN_MIDDLE );
                          table.addCell( c1 );
                      %>
                        <td class="listItem" align="center">...</td>
                      <%}%>
  										<td class="listItem" align="center">
  							      <%
  							         double progress= (double)nof_tests_passed / (double)nof_tests;
  							      	 int value = (int) (progress * 100.0);
  							      %>
  								      <%=value%>%
  											<% 
  												Cell cell = new Cell( new Phrase( value + "%", font ) );
  					 					    cell.setHorizontalAlignment( Element.ALIGN_CENTER );
  												table.addCell( cell ); 
  											%>
  										</td>
  									</tr>
  								</di:hasrole>
  							</di:hasrole>
  						</mm:node>
  					</mm:list>			
  				</table>
  				
          <!--  Paging  -->        
  				<%if( pageNumber > 1 ){ %>
            <% if( offset.intValue() > 1 ){ %><a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page">test_reports</mm:param><mm:param name="offset"><%= offset.intValue()-1 %></mm:param><mm:param name="columnsNumber"><%= columnsNumber %></mm:param></mm:treefile>"/><%}%>
  		        &lt;
  					<% if( offset.intValue() > 1 ){ %></a><%}%>
  	        <%
  	          for( int i = 1; i <= pageNumber; i++ )
  	          {
  	        %>
  	            <a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page">test_reports</mm:param><mm:param name="offset"><%= i %></mm:param><mm:param name="columnsNumber"><%= columnsNumber %></mm:param></mm:treefile>"/>
  	              <% if( offset.intValue() == i ){ %><b><%}%>
  	              <%= i %> 
  	              <% if( offset.intValue() == i ){ %></b><%}%>
  	            </a>
  	        <%}%>
            <% if( offset.intValue() < pageNumber ){ %><a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="page">test_reports</mm:param><mm:param name="offset"><%= offset.intValue()+1 %></mm:param><mm:param name="columnsNumber"><%= columnsNumber %></mm:param></mm:treefile>"/><%}%>
  		        &gt;
  					<% if( offset.intValue() < pageNumber ){ %></a><%}%>
  				<%}%>   
  				
          <div align="right">
  			    <form action="index.jsp">
  			    	<input type="hidden" name="offset" value="1">
  			    	<input type="hidden" name="class" value="<%= classnumber %>">
  			    	<input type="hidden" name="page" value="test_reports">
  			    	<di:translate key="reports.columnsnumber" />
  						<input type="text" name="columnsNumber" value="<%= columnsNumber %>" size="1" >
  						<input type="submit" value="OK">
  			    </form>
  				</div>        
  				     				
  				<% pdfDocumentElements.put( "element2", table ); %> 
  			</mm:compare>
  
  			<!-- 
  				DOCUMENT REPORTS
  			 -->
  			<mm:compare referid="reports_page" value="<%= nl.didactor.reports.util.ReportsPages.DOCUMENT_REPORTS %>">
  				<% if( request.getParameter( "startdate" ) == null ) {%>
  				  <form action="" id="dateform" name="dateform">
  					<h1><di:translate key="reports.document" /> - <di:translate key="reports.selectperiod" /></h1>
  					  <input type="hidden" name="start_date" value="" >
  					  <input type="hidden" name="end_date" value="" >
  		
  						<h3><di:translate key="reports.startdate" /></h3>
  		
  					  <di:translate key="reports.day" />
  					    <%-- day select box --%>
  					    <select name="start_day">
  					    <%
  					      for (int i=1;i<=31;++i) {
  					       out.write("<option value=\""+i+"\"");
  					       if (i==currentDay)
  					         out.write(" selected=\"selected\"");
  					        out.write(">"+i);
  					        out.write("</option>\n");
  					      }
  					    %>
  					    </select>
  					  <di:translate key="reports.month" />
  					    <%-- month select box --%>
  					    <select name="start_month">
  					    <%
  					      for (int i=1;i<=12;++i) {
  					        out.write("<option value=\""+i+"\"");
  					       if (i==currentMonth)
  					         out.write(" selected=\"selected\"");
  					        out.write(">"+months[i-1]);
  					        out.write("</option>\n");
  					      }
  					    %>
  					    </select>
  						<di:translate key="reports.year" />
  					    <%-- year select box --%>
  					    <select name="start_year">
  					    <%
  					      for (int i=2005;i<=2010;++i) {
  					        out.write("<option value=\""+i+"\"");
  					       if (i==currentYear)
  					         out.write(" selected=\"selected\"");
  					        out.write(">"+i);
  					        out.write("</option>\n");
  					      }
  					    %>
  					    </select>
  					    <br/><br/>
  					    
  						<h3><di:translate key="reports.enddate" /></h3>
  					  <di:translate key="reports.day" />
  					    <%-- day select box --%>
  					    <select name="end_day">
  					    <%
  					      for (int i=1;i<=31;++i) {
  					       out.write("<option value=\""+i+"\"");
  					       if (i==currentDay)
  					         out.write(" selected=\"selected\"");
  					        out.write(">"+i);
  					        out.write("</option>\n");
  					      }
  					    %>
  					    </select>
  					  <di:translate key="reports.month" />
  					    <%-- month select box --%>
  					    <select name="end_month">
  					    <%
  					      for (int i=1;i<=12;++i) {
  					        out.write("<option value=\""+i+"\"");
  					       if (i==currentMonth)
  					         out.write(" selected=\"selected\"");
  					        out.write(">"+months[i-1]);
  					        out.write("</option>\n");
  					      }
  					    %>
  					    </select>
  						<di:translate key="reports.year" />
  					    <%-- year select box --%>
  					    <select name="end_year">
  					    <%
  					      for (int i=2005;i<=2010;++i) {
  					        out.write("<option value=\""+i+"\"");
  					       if (i==currentYear)
  					         out.write(" selected=\"selected\"");
  					        out.write(">"+i);
  					        out.write("</option>\n");
  					      }
  					    %>
  					    </select>
  					    <br/><br/>		    
  					    <input type="hidden" name="setdatebutton" value="true" >
  					    <input type="button" onclick="javascript:checkDateEntered()" value="OK">
  					  </form>
  					<%}else{
  						long startTime = Long.decode( request.getParameter( "startdate" ) ).longValue();
  						long endTime = Long.decode( request.getParameter( "enddate" ) ).longValue();
  					%>
  
  						<h1><di:translate key="reports.document" /> - <di:translate key="reports.statisticforperiod" />&nbsp;&nbsp;&nbsp;<%= new java.util.Date( startTime ) %>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<%= new java.util.Date( endTime ) %></h1>
  						<div align="right">
  			        <a href="<mm:url page="/reports/pdf_reports.html"></mm:url>" target="_new">
  			         <di:translate key="reports.pdf" />
  			        </a><br/><br/>
  						</div>
  						<mm:import id="statisticforperiod" jspvar="statisticforperiod"><di:translate key="reports.document" /></mm:import>
  					<%
  						pdfDocumentElements.put( "element1", new Phrase( statisticforperiod, font_title ) ); 
 		          		Paragraph p6 = new Paragraph( "\r\n", font_title );
 		          		p6.setAlignment(Paragraph.ALIGN_LEFT);
              			pdfDocumentElements.put( "element11", new Phrase( "\r\n\r\n", font_title ) ); 
  						pdfDocumentElements.put( "element2", new Phrase( new java.util.Date( startTime ) + "   -   ", font_title ) ); 
  						pdfDocumentElements.put( "element3", new Phrase( new java.util.Date( endTime ) + "", font_title ) ); 
  					%> 
  
  
  					<%
  						 Table table = new Table( 3 );
               			 table.setAlignment(Table.ALIGN_LEFT);
  						 table.setPadding(3);
  					%>
  					
  
  						<table class="listTable">
  							<tr>
  								<th class="listHeader" width="30%"><di:translate key="reports.student" /></th>
  								<th class="listHeader" width="20%"><di:translate key="reports.addeddocsnumber" /></th>
                  <th class="listHeader" width="50%"><di:translate key="reports.addeddocs" /></th>
  								<mm:import id="addeddocsnumber" jspvar="addeddocsnumber"><di:translate key="reports.addeddocsnumber" /></mm:import>
                  <mm:import id="addeddocs" jspvar="addeddocs"><di:translate key="reports.addeddocs" /></mm:import>
  								<%
  								 	Cell cell1= new Cell( new Phrase( student, font_table_header ) );
  						    	cell1.setHorizontalAlignment( Element.ALIGN_CENTER );
  						    	cell1.setVerticalAlignment( Element.ALIGN_MIDDLE );
  						    	cell1.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  								  table.addCell(cell1);
  								
  								 	Cell cell2= new Cell( new Phrase( addeddocsnumber, font_table_header ) );
  						    	cell2.setHorizontalAlignment( Element.ALIGN_CENTER );
  						    	cell2.setVerticalAlignment( Element.ALIGN_MIDDLE );
  						    	cell2.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
  								  table.addCell(cell2);
  
  								  Cell cell3= new Cell( new Phrase( addeddocs, font_table_header ) );
                    cell3.setHorizontalAlignment( Element.ALIGN_CENTER );
                    cell3.setVerticalAlignment( Element.ALIGN_MIDDLE );
                    cell3.setBackgroundColor( new java.awt.Color( 0xED, 0x6F, 0x2C ) );
                    table.addCell(cell3);
  								%>
  								
  							</tr>
		            <mm:listnodes path="people,classes" constraints="classes.number=$classnumber">
  								<mm:import id="person"><mm:field name="number" /></mm:import>
  									<di:hasrole referid="person" role="systemadministrator" inverse="true">
  										<di:hasrole referid="person" role="student">
  											<mm:import id="user_name" jspvar="user_name"><mm:field name="username" write="true" /></mm:import>
  											<tr>
  												<td class="listItem">
  													<mm:import id="student_name" jspvar="student_name"><mm:field name="firstname" /> <mm:field name="lastname" /></mm:import>
  													<mm:field name="firstname" write="true" /> <mm:field name="lastname" write="true" />
  													<% table.addCell( new Phrase( student_name, font ) ); %>
  												</td>
                          
                          <%
                            java.util.ArrayList list = (java.util.ArrayList)map.get( user_name );
                            
                            // no added documents
                            if( list == null ){
                          %>                              
                              <td class="listItem" align="center">0
                              </td>    
                              <td class="listItem">
                              </td>    
                          <% 
  						                Cell cell4 = new Cell( new Phrase( "0", font ) );
                 					    cell4.setHorizontalAlignment( Element.ALIGN_CENTER );
  							              table.addCell( cell4 ); 
  
                              Cell cell5 = new Cell( new Phrase( "", font ) );
                              cell5.setHorizontalAlignment( Element.ALIGN_CENTER );
                              table.addCell( cell5 ); 
                            }
                            
                            // added documents
                            else{
                              int numOfDocs = list.size();                              
  
                              Cell cell4 = new Cell( new Phrase( numOfDocs + "", font ) );
                              cell4.setHorizontalAlignment( Element.ALIGN_CENTER );
                              table.addCell( cell4 ); 
                              
                          %>
                              <td class="listItem" align="center"><%= numOfDocs %>
                              </td>
  
                              <td class="listItem">
                            <%
  
                              Cell cell5 = new Cell();
                              cell5.setHorizontalAlignment( Element.ALIGN_LEFT );
                              
                              for( int i = 0; i < numOfDocs; i++ )
                              {
                            %>
                              <mm:import id="docId" jspvar="docId" ><%= list.get( i ) %></mm:import>
                              <!-- show added documents  --> 
                              <mm:listnodes path="attachments" constraints="attachments.number=$docId">
                                <mm:import id="docTitle" jspvar="docTitle"><mm:field name="title" /></mm:import>
                                <% cell5.add( new Phrase( docTitle + "\n", font ) ); %>
  
                                <a href="<mm:treefile page="/reports/index.jsp" objectlist="$includePath" referids="$referids" ><mm:param name="documentId"><mm:write referid="docId"/></mm:param><mm:param name="showDocument">true</mm:param></mm:treefile>"/>
                                   <mm:field name="title" write="true"/>
                                </a>
                                 
                              </mm:listnodes>
                              <br>
                              <mm:remove referid="docId" />
                              <mm:remove referid="docTitle" />
                            <%}
                              table.addCell( cell5 ); 
                            %>    
                              </td>    
                          <%}%>
  
  												</td>
  											</tr>
  										</di:hasrole>
  									</di:hasrole>
	  							</mm:listnodes>
  						</table>								
  					<% pdfDocumentElements.put( "element4", table ); %> 
  					<%}%>
  				</mm:compare>
        </mm:present>
			</div>
		</div>
	</div>
	
	<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
	    
</mm:cloud>
</mm:content>

