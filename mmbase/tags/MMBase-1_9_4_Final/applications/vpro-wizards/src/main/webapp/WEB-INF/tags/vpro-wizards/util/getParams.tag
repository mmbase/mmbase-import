<%--
    use this tag to get a url formatted string of the params set in this request with the addParam tags.

--%>
<%@ tag import="java.util.*"  %>
<%@ attribute name="exclude" description="comma separated list of params to exclude"  %>
<%@ attribute name="var" required="true" rtexprvalue="false" description="the value of this attribute will be the name of the varible that holds the params" %>
<%@ variable scope="AT_END" name-from-attribute="var" alias="a" %>
<%
    Map params = (Map)jspContext.findAttribute("___params");
    if(params != null){
        String exclude = (String)jspContext.getAttribute("exclude");
        Set excSet = new HashSet();
        if(exclude != null && ! exclude.trim().equals("")){
            StringTokenizer st = new StringTokenizer(exclude,",");
            while(st.hasMoreTokens()){
                excSet.add(st.nextToken().trim());
            }
        }
        String result = "";
        Set keys = params.keySet();
        boolean first = true;
        for(Iterator i = keys.iterator(); i.hasNext(); ){
            String key = (String)i.next();
            if(! excSet.contains(key)){
                if(!first){
                    result = result + "&";
                }
                first = false;
                result = result + key + "=" + (String) params.get(key);
            }
        }
        jspContext.setAttribute("a", result);
    }
%>

