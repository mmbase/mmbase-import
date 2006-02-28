<%@page import="java.util.*,nl.leocms.util.*,nl.mmatch.HtmlCleaner" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache"%>

<%@taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>
<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%-- hh 
<%@taglib uri="/WEB-INF/tld/struts-template.tld" prefix="template"%>
<%@taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>
<%@taglib uri="/WEB-INF/tld/error.tld" prefix="error"%>
<%@taglib uri="/WEB-INF/tld/oneclickedit.tld" prefix="oce"%>
<%@taglib uri="/WEB-INF/tld/oneclickedit-staging.tld" prefix="oce_staging"%>
<%@taglib uri="/WEB-INF/tld/leocms-utils.tld" prefix="lutils"%>
--%>

<%

String editwizard_location = "/mmbase/edit/wizard";

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
%>