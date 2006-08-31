package org.mmbase.www;

import java.util.*;
import org.apache.commons.lang.util.Validate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.EmailValidator;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @javadoc
 * Created by IntelliJ IDEA.
 * User: mdemare
 * Date: 11-Dec-2003
 * Time: 11:51:49
 * To change this template use Options | File Templates.
 * @version $Id: AccountHandler.java,v 1.3 2004-05-11 17:06:48 michiel Exp $
 */
public class AccountHandler {
   /**
    * Expects email attribute in params. Looks up mail in user builder,
    * if there's exactly result, mail username + password to mail.
    * @param cloud
    * @param request
    * @throws IllegalArgumentException with message if anything goes wrong.
    */
    public static void forgotten(Cloud cloud, HttpServletRequest request) throws MessagingException {
        String mail = request.getParameter("email");
        Validate.notEmpty(mail, "You have not specified an email address");
        Validate.isTrue(EmailValidator.getInstance().isValid(mail), "Not a valid email address.");
        String constraint = "email='" + mail + "'";
        Node node = getUserNode(cloud, constraint);
        SimpleEmail email = new SimpleEmail();
        email.setFrom("mmweb@mmbase.org").setHostName("localhost");
        email.setSubject("your password reminder");
        email.addTo(mail);
        email.setMsg("Your username is: " + node.getStringValue("account") + "\nYour password is: " + node.getStringValue("password"));
        email.send();
    }

    /**
     * @javadoc
     */
    private static Node getUserNode(Cloud cloud, String constraint) {
        NodeList list = cloud.getNodeManager("users").getList(constraint, null, null);
        Validate.isTrue(!list.isEmpty(), "No users found.");
        Validate.isTrue(list.size() == 1, "More than one user found.");
        Node node = list.getNode(0);
        return node;
    }

    /**
     * @javadoc
     */
    private static String getUserId(HttpServletRequest request) {
        Cookie cookie[] = request.getCookies();
        Validate.notNull(cookie, "You are not logged in, or have blocked cookies.");
        for (int i = 0; i < cookie.length; i++) {
            Cookie c = cookie[i];
            if("ca".equals(c.getName())) {
                return c.getValue();
            }
        }
        throw new IllegalArgumentException("Cookie with username not found.");
    }

    /**
     * @javadoc
     */
    private static String getPassword(HttpServletRequest request) {
        Cookie cookie[] = request.getCookies();
        Validate.notNull(cookie, "You are not logged in, or have blocked cookies.");
        for (int i = 0; i < cookie.length; i++) {
            Cookie c = cookie[i];
            if("cw".equals(c.getName())) {
                return c.getValue();
            }
        }
        throw new IllegalArgumentException("Cookie with password not found.");
    }

    /**
     * Assumes user if logged in and has the ca and cw cookies in request,
     * and a new_email param. Updates the user object with the new email address.
     * @param cloud
     * @param request
     */
    public static void changeEmail(Cloud cloud, HttpServletRequest request) {
        String email = request.getParameter("new_email");
        Validate.notEmpty(email, "You must fill in an email address.");
        Validate.isTrue(EmailValidator.getInstance().isValid(email), "Not a valid email address.");
        Node userNode = getUserNode(cloud, "account='" + getUserId(request) + "'");
        userNode.setStringValue("email", email);
        userNode.commit();
    }

    /**
     * Assumes user if logged in and has the ca and cw cookies in request,
     * and passwordold, passwordnew1 and passwordnew2 params. Updates the
     * user object with the new password. Sets a new cookie.
     * @param cloud
     * @param request
     * @param response
     */
    public static void changePassword(Cloud cloud, HttpServletRequest request, HttpServletResponse response) {
        String p_old = request.getParameter("passwordold");
        String p_new1 = request.getParameter("passwordnew1");
        String p_new2 = request.getParameter("passwordnew2");
        Validate.notEmpty(p_old, "You must specify your old password.");
        Validate.notEmpty(p_new1, "You must specify your new password twice.");
        Validate.notEmpty(p_new2, "You must specify your new password twice.");
        Validate.isTrue(p_new2.length() >= 5, "Passwords should have at least 5 characters.");
        Validate.isTrue(p_new1.equals(p_new2), "New passwords are not identical.");
        Validate.isTrue(p_old.equals(getPassword(request)), "Your original password is wrong.");
        Node userNode = getUserNode(cloud, "account='" + getUserId(request) + "'");
        userNode.setStringValue("password", p_new1);
        userNode.commit();
        cookieLogin(getUserId(request), response, p_new1);
    }

    /**
     * Expects username and password. Validates user against user builder,
     * and sets cookie if validation is successful.
     * @param cloud
     * @param request
     * @param response
     */
    public static void login(Cloud cloud, HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Validate.notEmpty(username, "No username specified.");
        Validate.notEmpty(password, "No username specified.");
        Node node = getUserNode(cloud, "account='" + username + "'");
        Validate.isTrue(password.equals(node.getStringValue("password")), "Password is incorrect");
        cookieLogin(username, response, password);
        request.getSession().setAttribute("cloud_mmbase", null);
        Integer userNumber = node == null ? null : new Integer(node.getNumber());
        request.getSession().setAttribute("user", userNumber);
        Node person = getPerson(node);
        request.getSession().setAttribute("person", new Integer(person.getNumber()));
        String fullName = person.getStringValue("firstname");
        String suffix = person.getStringValue("suffix");
        suffix = StringUtils.isEmpty(suffix) ? " " : " " + suffix + " ";
        fullName = fullName + suffix + person.getStringValue("lastname");
        request.getSession().setAttribute("user_node_name", fullName);
        request.getSession().setAttribute("organisations", getOrganisations(person));

    }

    /**
     * @javadoc
     */
    private static Map getOrganisations(Node person) {
        HashMap map = new HashMap();
        if(person != null) {
            NodeList relatedNodes = person.getRelatedNodes("organisation");
            for (Iterator i = relatedNodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
                String name = node.getStringValue("name");
                map.put(new Integer(node.getNumber()), name);
            }
        }
        return map;
    }

    /**
     * @javadoc
     */
    private static Node getPerson(Node user) {
        NodeList relatedNodes = user.getRelatedNodes("persons");
        if(relatedNodes.size() != 1) {
            throw new IllegalArgumentException("This user is not related to a person.");
        }
        return relatedNodes.getNode(0);
    }

    /**
     * @javadoc
     */
    private static void cookieLogin(String username, HttpServletResponse response, String password) {
        Cookie c = new Cookie("ca", username);
        response.addCookie(c);
        c = new Cookie("cw", password);
        response.addCookie(c);
   }
}
