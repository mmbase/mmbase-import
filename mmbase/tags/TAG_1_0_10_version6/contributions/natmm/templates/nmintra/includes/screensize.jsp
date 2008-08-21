<%
int [] thisScreen = {0, 0};
Cookie[] cookies = request.getCookies(); 
if(cookies!=null){
    for(int i=0; i<cookies.length; i++) {
        Cookie cookie = cookies[i];
        if (cookie.getName().equals("screenWidth")){
            try {
                thisScreen[0] = (new Integer(cookie.getValue())).intValue();
            } catch(Exception e) { }
        }
        if (cookie.getName().equals("screenHeight")){
            try {
                thisScreen[1] = (new Integer(cookie.getValue())).intValue();
            } catch(Exception e) { }
        }
    }
}
if(thisScreen[0]>0&&thisScreen[1]>0) {
    TreeMap screenSizes = (TreeMap) application.getAttribute("visitorscreens");
    if(screenSizes==null){
        screenSizes = new TreeMap();
        application.setAttribute("visitorscreens",screenSizes);
    }
    screenSizes.put(request.getRemoteAddr(),"<td>" + thisScreen[0] 
			+ "</td><td>" + thisScreen [1] + "</td>"); 
}
%>
