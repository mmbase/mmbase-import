<%-- Functie die checkt of een e-mail adres een punt en een apestaart bevat --%>

<%! public boolean isValidEmailAddr(String emailAddrString) {
        boolean isValid = false;
        if (emailAddrString != null &&
                emailAddrString.indexOf("@") > 1 &&
                emailAddrString.lastIndexOf(".") > emailAddrString.indexOf("@")) {
                isValid = true;
        }
        return isValid;
}
%>
