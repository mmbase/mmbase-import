<%// Global language code %>
<%
{
   String sDefaultLanguage = "nl";
   String[] sSupportedLanguages = {"en", "nl"};
   String sLangCode = request.getLocale().getLanguage();

   boolean bLangCodeIsCorrect = false;
   for(int f = 0; f < sSupportedLanguages.length; f ++)
   {
      if(sSupportedLanguages[f].equals(sLangCode))
      {
         bLangCodeIsCorrect = true;
         break;
      }
   }
   if(!bLangCodeIsCorrect)
   {
      sLangCode = sDefaultLanguage;
   }

   %>
      <mm:import id="lang_code"><%= sLangCode %></mm:import>
   <%
}
%> 