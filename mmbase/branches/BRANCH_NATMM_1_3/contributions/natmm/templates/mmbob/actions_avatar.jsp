<%-- hh
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "xhtml1-strict.dtd">
--%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">

<mm:import externid="selectedavatar"/>
<mm:import externid="pathtype">poster_index</mm:import>
<mm:import externid="avatarset">otherset</mm:import>

<%-- imports when request is multipart post --%>
<mm:notpresent referid="selectedavatar">
  <mm:import externid="addavatar" from="multipart"/>
  <mm:import externid="selectavatar" from="multipart"/>
  <mm:import externid="otheravatarset" from="multipart"/>
  <mm:import externid="avatarsets" from="multipart"/>
  <mm:import externid="_handle_name" from="multipart"/>
</mm:notpresent>

<%-- imports when request is not multipart post --%>
<mm:present referid="selectedavatar">
  <mm:import externid="addavatar"/>
  <mm:import externid="selectavatar"/>
  <mm:import externid="selectedavatarnumber"/>
  <mm:import externid="avatarsets" />
</mm:present>

<mm:import externid="forumid"/>
<mm:import externid="referrer" jspvar="referrer"/>
<mm:import externid="postareaid" />
<mm:import externid="posterid" id="profileid" />
<mm:import externid="profile"/>

<%-- login part --%>
<%@ include file="getposterid.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%-- end login part --%>

  <mm:present referid="addavatar">
    <mm:transaction name="avatartrans">
      <mm:node id="posternode" number="$posterid">
      <mm:relatednodes type="avatarsets">
        <mm:first><mm:import id="userset"><mm:field name="number"/></mm:import></mm:first>
      </mm:relatednodes>
      <mm:related path="rolerel,images"
                  fields="rolerel.role,rolerel.number"
                  constraints="rolerel.role='avatar'">
        <mm:first><mm:import id="presentavatar"><mm:field name="rolerel.number"/></mm:import></mm:first>
      </mm:related>
      
      </mm:node>
      <mm:createnode id="avatarnode" type="images">
        <mm:setfield name="title">Uploaded avatar (<mm:write referid="_handle_name"/>)</mm:setfield>
        <mm:fieldlist fields="handle">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
      </mm:createnode>

      <mm:present referid="presentavatar">
        <mm:node referid="presentavatar">
          <mm:deletenode/>
        </mm:node>
      </mm:present>  

      <mm:createrelation source="posternode" destination="avatarnode" role="rolerel">
        <mm:setfield name="role">avatar</mm:setfield>
      </mm:createrelation>
      
      <mm:notpresent referid="userset">
        <mm:createnode id="userset" type="avatarsets">
          <mm:setfield name="name"><mm:node referid="posterid"><mm:field name="account"/></mm:node> 's set</mm:setfield>
        </mm:createnode>
        <mm:createrelation source="posternode" destination="userset" role="related" />
      </mm:notpresent>     
 
      <mm:createrelation source="userset" destination="avatarnode" role="posrel" />
    </mm:transaction>
  </mm:present>

  <mm:present referid="selectedavatarnumber">
    <mm:transaction name="avatartrans">
      <mm:node id="posternode" number="$posterid">
      <mm:relatednodes type="avatarsets">
        <mm:first><mm:import id="userset"><mm:field name="number"/></mm:import></mm:first>
      </mm:relatednodes>
      <mm:related path="rolerel,images"
                  fields="rolerel.role,rolerel.number"
                  constraints="rolerel.role='avatar'">
        <mm:first><mm:import id="presentavatar"><mm:field name="rolerel.number"/></mm:import></mm:first>
      </mm:related>
      
      </mm:node>
      <mm:present referid="presentavatar">
        <mm:node referid="presentavatar">
          <mm:deletenode/>
        </mm:node>
      </mm:present>

      <mm:notpresent referid="userset">
        <mm:createnode id="userset" type="avatarsets">
          <mm:setfield name="name"><mm:node referid="posterid"><mm:field name="account"/></mm:node> 's set</mm:setfield>
        </mm:createnode>
        <mm:createrelation source="posternode" destination="userset" role="related" />
      </mm:notpresent>  
   
      <mm:node id="avatarnode" referid="selectedavatarnumber"/>

      <mm:createrelation source="userset" destination="avatarnode" role="posrel" />

      <mm:createrelation source="posternode" destination="avatarnode" role="rolerel">
        <mm:setfield name="role">avatar</mm:setfield>
      </mm:createrelation>
    </mm:transaction>
  </mm:present>

<mm:present referid="addavatar">
  <%@ include file="includes/profile_updated.jsp" %>
</mm:present>

<mm:present referid="selectedavatarnumber">
  <%@ include file="includes/profile_updated.jsp" %>
</mm:present>

<mm:notpresent referid="selectedavatarnumber">
<mm:notpresent referid="addavatar">
<html <%-- hh xmlns="http://www.w3.org/1999/xhtml" --%>>
  <head>
    <title>MMBase Forum Profile</title>
    <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
  </head>
  <body>
    <mm:include page="path.jsp?type=$pathtype" />

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                        <tr><th colspan="2" align="left">
                                        
            </th>
            </tr>

</table>


    <form enctype="multipart/form-data" action="<mm:url page="actions_avatar.jsp">
      <mm:param name="forumid" value="$forumid" />
      <mm:param name="postareaid" value="$postareaid" />
      <mm:param name="posterid" value="$posterid" />
      <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
      <mm:param name="profile" value="$profile" />
      <mm:param name="referrer" value="profile.jsp" />
      </mm:url>" method="post"> 

    <div id="profileb">
      <div id="tabs">
      <ul>
        <mm:compare value="ownset" referid="avatarset">
        <li class="selected">
        </mm:compare>
        <mm:compare value="ownset" referid="avatarset" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="avatarset" value="ownset"  />
        <mm:param name="selectedavatar" value="true" />
        <mm:param name="profile" value="$profile" />
        </mm:url>">eigen avatars</a>
        </li>
        <mm:compare value="otherset" referid="avatarset">
        <li class="selected">
        </mm:compare>
        <mm:compare value="otherset" referid="avatarset" inverse="true">
        <li>
        </mm:compare>
        <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$profileid" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="avatarset" value="otherset" />
        <mm:param name="selectedavatar" value="true " />
        <mm:param name="profile" value="$profile" />
        </mm:url>">overige avatars</a>
        </li>
      </ul>
    </div>

    <mm:compare value="otherset" referid="avatarset">
    <div id="profile">
      <div class="row">
        <mm:node referid="forumid">
          <mm:relatednodescontainer type="avatarsets">
            
            <mm:relatednodes>
              <mm:first>
                <mm:notpresent referid="avatarsets">
                  <mm:import externid="avatarsets" vartype="Integer">
                    <mm:field name="number" />
                  </mm:import>
                </mm:notpresent>
                <mm:import id="headdisplayed">true</mm:import>
                <span class="label">Selecteer categorie:</span>
                <span class="formw">
                  <select name="avatarsets">
              </mm:first>
              <option <mm:field name="number"><mm:compare referid2="avatarsets">selected="true"</mm:compare> value="<mm:write/>"></mm:field><mm:field name="name"/></option>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>

        <mm:present referid="postareaid">
          <mm:compare referid="postareaid" value="" inverse="true">
            <mm:node referid="postareaid">
              <mm:relatednodescontainer type="avatarsets">
                
                <mm:relatednodes>
                  <mm:first>
                    <mm:notpresent referid="avatarsets">
                      <mm:import externid="avatarsets" vartype="Integer">
                        <mm:field name="number" />
                      </mm:import>
                    </mm:notpresent>
                    <mm:notpresent referid="headdisplayed">
                      <mm:import id="headdisplayed">true</mm:import>
                      <span class="label">Selecteer categorie:</span>
                      <span class="formw">
                        <select name="avatarsets">
                    </mm:notpresent>
                  </mm:first>
                  <option <mm:field name="number"><mm:compare referid2="avatarsets">selected="true"</mm:compare> value="<mm:write/>"></mm:field><mm:field name="name"/></option>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:node>
          </mm:compare>
        </mm:present>
        <mm:present referid="headdisplayed">
        </select>
          <input type="submit" name="otheravatarset" value="OK" />
        </span>
        </mm:present>
      

     <mm:notpresent referid="headdisplayed">
       <span class="label">Geen avatars geinstalleerd</span>
       
     </mm:notpresent>

</div>
     
      <div class="row">
      <mm:notpresent referid="avatarsets">
        <%--<mm:listnodes type="avatarsets" max="1">
          <mm:import externid="avatarsets"><mm:field name="number"/></mm:import>
        </mm:listnodes>--%>
        &nbsp;
      </mm:notpresent>
      <mm:present referid="avatarsets">
        <mm:node referid="avatarsets">
          <mm:relatednodes type="images">
            <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:param name="avatarsets" value="$avatarsets" />
        <mm:field id="avatarnumber" name="number"/>
        <mm:param name="selectedavatarnumber" value="$avatarnumber" />
       
        <mm:param name="selectedavatar" value="true" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        </mm:url>">
            <img src="<mm:image template="s(80x80)" />" width="80" border="0"></a>
          </mm:relatednodes>
        </mm:node>
      </mm:present>

      </div>
      </mm:compare>
      
    <mm:compare value="ownset" referid="avatarset">
    <div id="profile">
     
      <div class="row">
      <mm:node number="$profileid">
        <mm:related path="avatarsets,images" fields="images.number">
          <a href="<mm:url page="actions_avatar.jsp">
        <mm:param name="forumid" value="$forumid" />
        <mm:param name="postareaid" value="$postareaid" />
        <mm:param name="posterid" value="$posterid" />
        <mm:param name="avatarsets" value="$avatarsets" />
        <mm:field id="avatarnumber" name="images.number"/>
        <mm:param name="selectedavatarnumber" value="$avatarnumber" />
       
        <mm:param name="selectedavatar" value="true" />
        <mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
        <mm:param name="profile" value="$profile" />
        </mm:url>">
<mm:node element="images">
            <img src="<mm:image template="s(80x80)" />" width="80" border="0">
</mm:node>
</a>
          </mm:related>
        </mm:node>
     

      </div>
      </mm:compare>


    <div class="spacer">&nbsp;</div>

    </div> 
    </div>
  </form>
</body>
</html>
</mm:notpresent></mm:notpresent>
</mm:cloud>
