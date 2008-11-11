<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@page import="java.text.*,java.io.*,org.mmbase.bridge.*" %>
<mm:import jspvar="ID" externid="id">-1</mm:import>
<mm:import jspvar="rubriekId" externid="r">-1</mm:import>
<mm:import jspvar="paginaID" externid="p">-1</mm:import>
<mm:import jspvar="articleId" externid="article">-1</mm:import>
<mm:import jspvar="shop_itemId" externid="u">-1</mm:import>
<mm:import jspvar="employeeId" externid="employee">-1</mm:import>
<%

ApplicationHelper ap = new ApplicationHelper(cloud);
PaginaHelper ph = new PaginaHelper(cloud);
String path = ph.getTemplate(request);

// Id finding for the following request parameters (only the object types in NMIntraConfig can be used)

HashMap ids = new HashMap();
ids.put("object", ID);
ids.put("rubriek", rubriekId);
ids.put("pagina", paginaID);
ids.put("artikel", articleId);
ids.put("items", shop_itemId);
ids.put("medewerkers", employeeId);

ids = ph.findIDs(ids, path, "thuispagina");

ID = (String) ids.get("object");
rubriekId = (String) ids.get("rubriek");
paginaID = (String) ids.get("pagina");
articleId = (String) ids.get("artikel");
shop_itemId = (String) ids.get("items");
employeeId = (String) ids.get("medewerkers");

String refererId = request.getParameter("referer"); if(refererId==null){ refererId = "-1"; }

// *** find the root rubriek the present page is related to
Vector breadcrumbs = ph.getBreadCrumbs(cloud, paginaID);
String subsiteID = (String) breadcrumbs.get(breadcrumbs.size()-2);

int iRubriekStyle = NMIntraConfig.PARENT_STYLE;
int iRubriekLayout = NMIntraConfig.PARENT_LAYOUT;

String styleSheet = "hoofdsite/themas/default.css"; 

// *** determine the rubriek specific setting: style & rubriek image
String rbLogoID = "-1";
for(int r=0; r<breadcrumbs.size(); r++) {
	%><mm:node number="<%= (String) breadcrumbs.get(r) %>" jspvar="thisRubriek"><%

    if(iRubriekLayout==NMIntraConfig.PARENT_LAYOUT) {
       try { iRubriekLayout = thisRubriek.getIntValue("naam_fra"); } catch (Exception e) {}
    }
    
		if(iRubriekStyle==NMIntraConfig.PARENT_STYLE){
			styleSheet = thisRubriek.getStringValue("style");
			for(int s = 0; s< NMIntraConfig.style1.length; s++) {
				if(styleSheet.indexOf(NMIntraConfig.style1[s])>-1) { iRubriekStyle = s; } 
			}
		} %>
      <mm:related path="contentrel,images" max="1"
          ><mm:field name="images.number" jspvar="images_number" vartype="String" write="false"><%
              rbLogoID = images_number;
          %></mm:field
       ></mm:related>
  
	</mm:node><%
}

if(iRubriekLayout==NMIntraConfig.PARENT_LAYOUT) { iRubriekLayout = NMIntraConfig.DEFAULT_LAYOUT; }
if(iRubriekStyle==NMIntraConfig.PARENT_STYLE) { iRubriekStyle = NMIntraConfig.DEFAULT_STYLE; }

// smoelenboek (and searchbar)
String nameEntry = "voor- en/of achternaam";
String nameId = request.getParameter("name"); if(nameId==null) { nameId=""; }
String departmentId = request.getParameter("department"); if(departmentId==null){ departmentId="default"; }
String programId = request.getParameter("program"); if(programId==null){ programId="default"; }
String locationId = request.getParameter("location"); if(locationId==null){ locationId="default"; }

//String visitorGroup = request.getParameter("vg"); if(visitorGroup==null){ visitorGroup = ""; }

// IntraShop
String imageId = request.getParameter("i"); 
String actionId = request.getParameter("t"); if (actionId==null) {actionId=""; }
String totalitemsId = (String) session.getAttribute("totalitems");
if(totalitemsId==null) totalitemsId = "0";
String shop_itemHref = "";
String extendedHref = "";
NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
nf.setMaximumFractionDigits(2);
nf.setMinimumFractionDigits(2);
boolean bShowPrices = true;
boolean bMemberDiscount = false;
boolean bExtraCosts = false;

// IntraProject
String projectId = request.getParameter("project"); if(projectId==null){ projectId = ""; }
String typeId = request.getParameter("type"); if(typeId==null) { typeId="-1"; }
String groupId = request.getParameter("group"); if(groupId==null) { groupId="-1"; }
String projectNameId = request.getParameter("projectname"); if(projectNameId==null) { projectNameId=""; }
String abcId = request.getParameter("abc"); if(abcId==null) { abcId=""; }
String termSearchId = request.getParameter("termsearch"); if(termSearchId==null) { termSearchId=""; }

// item and info
String offsetId = request.getParameter("offset"); if(offsetId==null){ offsetId=""; }
String periodId = request.getParameter("d"); if(periodId==null){ periodId =""; }

// info & producttypes
String poolId = request.getParameter("pool"); if(poolId==null){ poolId=""; }
String productId = request.getParameter("product"); if(productId==null){ productId=""; }

// searchresults
String searchId = request.getParameter("search"); if(searchId==null) { searchId=""; }
String categoryId = request.getParameter("category"); if(categoryId==null){ categoryId = ""; }
String defaultSearchText = "ik zoek op ...";

// formulier
String postingStr = request.getParameter("pst"); if(postingStr==null) { postingStr=""; }

//IntraEducations
String educationId = request.getParameter("e"); if(educationId==null){ educationId = ""; }
String keywordId = request.getParameter("k"); if(keywordId==null){ keywordId = ""; }
String providerId = request.getParameter("pr"); if(providerId==null){ providerId = ""; }
String competenceId = request.getParameter("c"); if(competenceId==null){ competenceId = ""; }
String educationNameId = request.getParameter("en"); if(educationNameId==null) { educationNameId = ""; }

//IntraEvents
String eventId = request.getParameter("ev"); if(eventId==null){ eventId = ""; }
String eTypeId = request.getParameter("evt"); if(eTypeId==null){ eTypeId = ""; }
String pCategorieId = request.getParameter("pc"); if(pCategorieId==null){ pCategorieId = ""; }
String pAgeId = request.getParameter("pa"); if(pAgeId==null){ pAgeId = ""; }
String nReserveId = request.getParameter("nr"); if(nReserveId==null){ nReserveId = ""; }
String eDistanceId = request.getParameter("evl"); if(eDistanceId==null){ eDistanceId = ""; }
String eDurationId = request.getParameter("evd"); if(eDurationId==null){ eDurationId = ""; }

// globals
String infopageClass = "infopage";
boolean printPage = postingStr.equals("|action=print");
if(printPage) { infopageClass = "ipage"; }

String templateQueryString = "";
if(!paginaID.equals("-1")){ templateQueryString += "?p=" + paginaID; } 
if(!articleId.equals("-1")){ templateQueryString += "&article=" + articleId; }
if(!categoryId.equals("")){ templateQueryString += "&category=" + categoryId; }
if(!projectId.equals("")){ templateQueryString += "&project=" + projectId; }
if(!educationId.equals("")){ templateQueryString += "&e=" + educationId; }

String imageTemplate = "";
 
String sPageRef = (String) session.getAttribute("pageref");
if(sPageRef!=null&&!sPageRef.equals(paginaID)) { // set pagerefminone to sPagRef, set pageref to paginaID
	session.setAttribute("pagerefminone",sPageRef);
}
session.setAttribute("pageref",paginaID);

%><%@include file="screensize.jsp" %>
