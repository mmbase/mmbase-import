<%@page import="com.finalist.tree.*,nl.leocms.authorization.forms.*,nl.leocms.util.*, java.util.*, org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<cache:flush scope="application"/>
<mm:cloud jspvar="cloud" rank="basic user">
<%
   // todo: change this hardcode implementation to one based on the styles object.

   // *** note: there is a difference between layout and style: style is always set, layout may have value parent_layout
   
   // *** new layouts should be added to /editors/rubriek/rubriek.jsp ***
   int PARENT_LAYOUT = -1;
   int DEFAULT_LAYOUT = 0;
   int SUBSITE1_LAYOUT = 1;
   int SUBSITE2_LAYOUT = 2;
   
   // *** default style should be set in top1_params.jsp and new layouts should be added to /editors/rubriek/rubriek.jsp ***
   int PARENT_STYLE = -1;
   int DEFAULT_STYLE = 7;
   String [] style1 = {"vereniging","steun" ,"nieuws","natuurin","natuurgebieden","links" ,"fun"   ,"default","zoeken","winkel","vragen","naardermeer" };
   String [] color1 = {"552500"    ,"990100","4A7934","D71920"  ,"BAC42B"        ,"9C948C","EC008C","1D1E94" ,"00AEEF","F37021","6C6B5C","F37021" }; // color + line leftnavpage
   String [] color2 = {"E4BFA3"    ,"F7D6C3","B0DF9B","FFBDB7"  ,"EEF584"        ,"EDE9E6","FABFE2","96ADD9" ,"B2E7FA","FED9B2","D6D6D1","F9B790" }; // background leftnavpage_high
   String [] color3 = {"050080"    ,"050080","050080","050080"  ,"050080"        ,"050080","050080","FFFFFF" ,"050080","050080","050080","FFFFFF"};  // footer links


   RubriekHelper rubriekHelper = new RubriekHelper(cloud);
   HashMap leocmsStyles = new HashMap();
   leocmsStyles.put("hoofdsite/themas/fun.css", "FUN");
   leocmsStyles.put("hoofdsite/themas/default.css", "HOME");
   leocmsStyles.put("hoofdsite/themas/links.css", "LINKS");
   leocmsStyles.put("hoofdsite/themas/natuurin.css", "NATUUR IN");
   leocmsStyles.put("hoofdsite/themas/natuurgebieden.css",  "NATUURGEBIEDEN");
   leocmsStyles.put("hoofdsite/themas/nieuws.css", "NIEUWS");
   leocmsStyles.put("hoofdsite/themas/winkel.css", "WINKEL");
   leocmsStyles.put("hoofdsite/themas/steun.css", "STEUN ONS");
   leocmsStyles.put("hoofdsite/themas/vragen.css", "VRAGEN?");
   leocmsStyles.put("hoofdsite/themas/zoeken.css", "ZOEKEN");
   leocmsStyles.put("hoofdsite/themas/naardermeer.css", "Naardermeer");
   
   String rubriekSubsiteNodeNumber = "";
%>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Rubrieken</title>
</head>
<body>

<logic:equal name="RubriekForm" property="node" value="">
<h1>Rubriek toevoegen</h1>
De nieuwe rubriek wordt een subrubriek van:
<bean:define id="parentNode" property="parent" name="RubriekForm" scope="request" type="java.lang.String"/>
<b>
    <%
       rubriekSubsiteNodeNumber = rubriekHelper.getSubsiteRubriek(parentNode);
       out.println(rubriekHelper.getPathToRootString(parentNode));
    %>
</b>
</logic:equal>
<logic:notEqual name="RubriekForm" property="node" value="">
<h1>Rubriek wijzigen</h1>
Rubriek:<b>
<bean:define id="nodenr" property="node" name="RubriekForm" scope="request" type="java.lang.String"/>
   <mm:import id="nodenummber"><%= nodenr %></mm:import>
   <%
       rubriekSubsiteNodeNumber = rubriekHelper.getSubsiteRubriek(nodenr);
       out.println(rubriekHelper.getPathToRootString(nodenr));
    %>
</b>
</logic:notEqual>
<mm:import id="level">2</mm:import>
<mm:present referid="nodenummber">
   <mm:node referid="nodenummber">
      <mm:remove referid="level"/>
      <mm:import id="level"><mm:field name="level"/></mm:import>
   </mm:node>
</mm:present>

<html:form action="/editors/rubrieken/RubriekAction">
<html:hidden property="node"/>
<html:hidden property="parent"/>
<html:hidden property="level"/>
<table class="formcontent">
    <tr><td class="fieldname" width='120'>Naam</td><td><html:text property="naam" size='40' maxlength='40' />
    <span class="notvalid"><html:errors bundle="LEOCMS" property="naam" /></span></td></tr>
<%-- hh
    <tr><td class="fieldname">Naam - frans</td><td><html:text property="naam_fra" size='40' maxlength='40' /></td></tr>
    <tr><td class="fieldname">Naam - engels</td><td><html:text property="naam_eng" size='40' maxlength='40' /></td></tr>
    <tr><td class="fieldname">Naam - duits</td><td><html:text property="naam_de" size='40' maxlength='40' /></td></tr>
    <tr><td class="fieldname">Url</td><td><html:text property="url" maxlength='100' />
    <span class="notvalid"><html:errors bundle="LEOCMS" property="url" /></span></td></tr>
<logic:equal name="RubriekForm" property="level" value="1">
    <tr><td class="fieldname">Url Live</td><td><html:text property="url_live" maxlength='100' /></td></tr>
</logic:equal>
    <tr><td class="fieldname">Beschikbare talen</td><td><html:checkbox property="fra_active" styleClass="check"/>Frans *</br><html:checkbox property="eng_active" styleClass="check"/>Engels *</br><html:checkbox property="de_active" styleClass="check"/>Duits *</br></td></tr>
<logic:equal name="RubriekForm" property="level" value="1">
    <tr><td class="fieldname">&nbsp;</td><td><html:checkbox property="wholesubsite" styleClass="check"/>Talen gelden voor de hele subsite</td></tr>
</logic:equal>
   <tr><td></td><td><b>* Wanneer bij een subsite een taal wordt uitgeschakeld, zal dit voor de hele subsite gelden. Hoewel het nog steeds mogelijk is binnen een subrubriek dezelfde taal aan te zetten zal dit geen effect hebben, zolang bij de subsite de taal uit staat.</b></td></tr>
--%>
    <tr><td class="fieldname">Layout</td><td>
         <html:select property="naam_fra">
             <html:option value="<%= "" + PARENT_LAYOUT %>">Layout van parent rubriek</html:option>
             <html:option value="<%= "" + DEFAULT_LAYOUT %>">Natuurmonumenten</html:option>
             <html:option value="<%= "" + SUBSITE1_LAYOUT %>">Naardermeer</html:option>
             <html:option value="<%= "" + SUBSITE2_LAYOUT %>">ING-Perspectief</html:option>
         </html:select>
      </td>
    </tr>
    <tr><td class="fieldname">Style</td>
       <td>
         <html:select property="style">
            <html:option value="parentstyle">Style van parent rubriek</html:option>
            <%
            Iterator stylesIt = leocmsStyles.keySet().iterator();
            while (stylesIt.hasNext()) {
               String key = (String) stylesIt.next();
               String value = (String) leocmsStyles.get(key);
               %>
               <html:option value="<%= key %>"><%= value %></html:option>
               <%
            }
            %>
         </html:select>
      </td>
   </tr>
   <tr><td class="fieldname">Rubriek is zichtbaar</td>
       <td>
        <html:select property="url">
          <html:option value="1">ja</html:option>
          <html:option value="0">nee</html:option>
        </html:select>
       </td>
    </tr>
</table>
<table class="formcontent">

   <tr>
      <td>
         <mm:compare referid="level" value="0" inverse="true">
            <html:submit value='Opslaan' style="width:90"/>
         </mm:compare>
         &nbsp;<html:cancel value='Annuleren' style="width:90"/>
      </td>
   </tr>
</table>
</html:form>

</body>
</html>
</mm:cloud>