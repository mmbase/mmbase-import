<%@page import="java.util.Enumeration"%>
<%!
    public Enumeration convertToEnumeration(String value) {
        int i = 0;
        int j = 0;
        int k = value.indexOf(',');
        Vector v = new Vector();
        while (k != -1) {
            v.add(value.substring(j, k));
            i++;
            j = k + 1;
            k = value.indexOf(',', j);
        }
        v.add(value.substring(j));
        return v.elements();
    }
%>
