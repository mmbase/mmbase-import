<% response.setContentType("text/xml"); %>
<lists>
  <optionlist name="formats">
  <%
      java.util.ResourceBundle formatsBundle = java.util.ResourceBundle.getBundle(org.mmbase.module.builders.media.MediaSources.FORMATS_RESOURCE);
      java.util.Enumeration e = formatsBundle.getKeys();
      while(e.hasMoreElements()) {
       String key = (String) e.nextElement();
  %>
     <option id="<%=key%>" ><%=formatsBundle.getString(key)%></option>
  <% } %>
   </optionlist>
</lists>
