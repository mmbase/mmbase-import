<%@ include file="settings.jsp"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"><%@ page errorPage="exception.jsp"
%><%
    // start a popup wizard.
    String wizardname = request.getParameter("wizard");
    String objectnumber = request.getParameter("objectnumber");

    if (wizardname!=null && objectnumber!=null && !wizardname.equals("") && !objectnumber.equals("") && wizardname.indexOf("|")==-1) {
        String sessionkey = wizardname + "|" + new java.util.Date().getTime();
        response.sendRedirect("wizard.jsp?wizard="+wizardname+"&sessionkey="+sessionkey+"&objectnumber="+objectnumber+"&popup=true&referrer="+ewconfig.backPage);
    } else {
        throw new WizardException("Could not start a popup wizard because no wizardname and objectnumber are applied."+
                                  " Please make sure you define a wizardname and objectnumber in the wizard schema.");
    }
%>
</mm:cloud>
