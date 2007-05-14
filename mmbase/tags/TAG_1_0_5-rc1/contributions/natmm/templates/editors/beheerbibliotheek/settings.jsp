<%
   Logger log = Logging.getLoggerInstance("beheerbibliotheek/index.jsp");
   ContentHelper contentHelper = new ContentHelper(cloud);
   ArrayList cTypes = (new ApplicationHelper(cloud)).getContentTypes(false);
   
   /**
    * Page settings.
    */
   int AMOUNT_OF_RESULTS = 25;  //default  amount of results per page
   String pagSize = PropertiesUtil.getProperty("beheerbibliotheek.elementen_per_pagina");
   if (pagSize != null && !"".equals(pagSize)) {
      try {
         AMOUNT_OF_RESULTS = Integer.parseInt(pagSize);
      }
      catch (NumberFormatException e) {
         // ignore but log
         log.warn("Property beheerbibliotheek.elementen_per_pagina in property builder should be an integer! Taking default value of 25.");
      }
   }
   int EXTRA_PAGES = 5;  //amount of pages 'under' and 'above' the current displayed page
   String extraPagsSize = PropertiesUtil.getProperty("beheerbibliotheek.aantal_pagineer_paginas");
   if (extraPagsSize != null && !"".equals(extraPagsSize)) {
      try {
         EXTRA_PAGES = Integer.parseInt(extraPagsSize);
      }
      catch (NumberFormatException e) {
         // ignore but log
         log.warn("Property beheerbibliotheek.extraPagsSize in property builder should be an integer! Taking default value 5.");
      }
   }
%><%-- hh
   String language = request.getParameter("language");
   if (language != null) {
%>
   <lutils:locale language="<%= language %>"/>
<%      
   }

   Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
   language = (locale != null) ? locale.getLanguage() : "nl";
--%><%

   String language = "nl";
   
   long nu = (long) System.currentTimeMillis()/1000;
   nu = (nu/(60*15))*(60*15); // help the query cache by rounding to quarter of an hour
   String OPTION_ALLE = "- alle -";

   // This variable actually means that the bieb is used in the one-click-edit
   boolean popup = (request.getParameter("popup") != null && "true".equals(request.getParameter("popup")) )  ? true : false ;
   String refreshFrame = request.getParameter("refreshFrame");


   boolean disableNewContent = (request.getParameter("disableNewContent") != null && "true".equals(request.getParameter("disableNewContent")) )  ? true : false ;
   boolean disableTypesModus = (request.getParameter("disableTypesModus") != null && "true".equals(request.getParameter("disableTypesModus")) )  ? true : false ;

   // ** Defaults
   String modus = "simple";
   boolean searchIsOn = false;
   boolean allTypesSelected = false;
   String selectedTypes = "";
   String rubriek = OPTION_ALLE;
   int age = 0;
   int changeAge = 0;
   String auteur = OPTION_ALLE;
   String metatag = "";
   String titel = "";
   String paginanaam = "- alle pagina's -";
   String orderColumn = "contentelement.otype";
   int pageNo = 0;
   int curPage = 1;
   
   // ** If available replace defaults by cookie setting
   Cookie[] cookies = request.getCookies();
   if(cookies!=null){ 
   for (int c = 0; c < cookies.length; c++) {
      String thisName = cookies[c].getName();
      String thisValue = cookies[c].getValue();
      if (thisName!=null&&thisValue!=null) {
      if(thisName.equals("modus")) {  modus = thisValue; }
      if(thisName.equals("searchIsOn")) { searchIsOn = thisValue.equals("true"); }
      if(thisName.equals("selectedTypes")) { selectedTypes = thisValue; }
      if(thisName.equals("rubriek")) { rubriek = thisValue; }
      if(thisName.equals("age")) { age = Integer.parseInt(thisValue); }
      if(thisName.equals("changeAge")) { changeAge = Integer.parseInt(thisValue); }
      if(thisName.equals("auteur")) { auteur = thisValue; }
      if(thisName.equals("metatag")) { metatag = thisValue; }
      if(thisName.equals("titel")) { titel = thisValue; }
      if(thisName.equals("paginanaam")) { paginanaam = thisValue; }
      if(thisName.equals("orderColumn")) { orderColumn = thisValue; }
      if(thisName.equals("pageno")) { pageNo = Integer.parseInt(thisValue); }
      if(thisName.equals("curPage")) { curPage = Integer.parseInt(thisValue); }
   	}
   }

   // ** If available replace defaults / cookie setting by request paramaters
   allTypesSelected = "all".equals(request.getParameter("allTypesSelected"));
   if (request.getParameter("modus") != null) { modus = request.getParameter("modus"); }
   if (request.getParameter("searchIsOn") != null) { searchIsOn = request.getParameter("searchIsOn").equals("true"); }
   if (request.getParameter("selectedTypes") != null) {  selectedTypes = request.getParameter("selectedTypes"); }
   if (request.getParameter("rubriek") != null) { rubriek = request.getParameter("rubriek"); }
   if (request.getParameter("age") != null) { age = Integer.parseInt(request.getParameter("age")); }
   if (request.getParameter("changeAge") != null) { changeAge = Integer.parseInt(request.getParameter("changeAge")); }
   if (request.getParameter("auteur") != null) { auteur = request.getParameter("auteur"); }
   if (request.getParameter("metatag") != null) { metatag = request.getParameter("metatag"); }
   if (request.getParameter("titel") != null) { titel = request.getParameter("titel"); }
   if (request.getParameter("paginanaam") != null) { paginanaam = request.getParameter("paginanaam"); } 
   if (request.getParameter("orderColumn") != null) { orderColumn = request.getParameter("orderColumn"); }
   if (request.getParameter("pageno") != null) { pageNo = Integer.parseInt(request.getParameter("pageno"));  if(paginanaam.equals("")) { pageNo = 0; }}
   if (request.getParameter("curPage") != null) { curPage = Integer.parseInt(request.getParameter("curPage")); }

   // ** set cookies
   int maxAge = 60 * 60 * 24 * 365;
   Cookie thisCookie = null;
   thisCookie = new Cookie("modus", modus); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("searchIsOn", "" + searchIsOn); thisCookie.setMaxAge(60); response.addCookie(thisCookie); 
   thisCookie = new Cookie("selectedTypes", selectedTypes); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("rubriek", rubriek); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("age", "" + age); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("changeAge", "" + changeAge); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("auteur", auteur); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("metatag", metatag); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("titel", titel); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("paginanaam", paginanaam); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("orderColumn", orderColumn); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("pageno", "" + pageNo ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
   thisCookie = new Cookie("curPage", "" + curPage ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
}
%>

