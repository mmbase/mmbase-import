/*

OSI Certified is a certification mark of the Open Source Initiative.
This software is OSI Certified Open Source Software.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.person;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Person (actually User) entity (loosely based on the P3P 1.0 specification, see http://www.w3.org/TR/P3P/ )
 *
 * Not implemented:
 *   HomeInfo (User's Home Contact Information)
 *   BusinessInfo (User's Business Contact Information)
 *
 * @author Remco Bos
 */

@Entity
@Table(name = "people")
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private Long authenticationId; // his/her credentials (usually his e-mail adress and password)

    /* User Name */
    private String prefix;         // Name Prefix
    private String given;          // Given Name (First Name)
    private String family;         // Family Name (Last Name)
    private String middle;         // Middle Name
    private String suffix;         // Name Suffix
    private String nickname;       // Nickname

    /* User Details */
    private Date bdate;            // User's Birth Date
    private String login;          // User's Login Information
    private String cert;           // User's Identity Certificate
    private String gender;         // User's Gender (Male or Female)
    private String employer;       // User's Employer
    private String department;     // Department or Division of Organization where User is Employed
    private String jobtitle;       // User's Job Title

    /* Online information */
    private String email;          // Email Address
    private String uri;            // Home Page Address

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getBdate() {
        return bdate;
    }

    public void setBdate(Date bdate) {
        this.bdate = bdate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(Long authenticationId) {
        this.authenticationId = authenticationId;
    }

    public void setProperties(Map<String, Serializable> properties) {
//        for (String key : properties.keySet()) {
//            if (key.equalsIgnoreCase("firstName")) {
//                String firstName = (String)properties.get(key);
//            }
//        }
        if (properties.get("firstName") != null) setGiven((String)properties.get("firstName"));
    }
    private boolean isEmpty(String stringValue) {
        return stringValue == null || stringValue.equals("");
    }
}
