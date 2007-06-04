/*
 * Copyright (c) 2006 Levi9 Global Sourcing. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Levi9 Global Sourcing. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you
 * entered into with Levi9 Global Sourcing.
 * Levi9 Global Sourcing makes no representations or warranties about the
 * suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability,
 * fitness for a particular purpose, or non-infringement. Levi9 Global Sourcing
 * shall not be liable for any damages suffered by licensee as a
 * result of using, modifying or distributing this software or its
 * derivatives.
 */

package nl.didactor.reports.data;

import java.sql.Timestamp;

/**
 * @author p.becic
 */
public class EventLog implements java.io.Serializable, Cloneable {

    private Long id;

    private Timestamp timestamp;

    private String session;

    private Integer provider;

    private Integer education;

    private Integer classNumber;

    private String username;

    private Integer eventtype;

    private Long value;

    private String note;

    /**
     * @return Returns the classNumber.
     */
    public Integer getClassNumber() {
        return classNumber;
    }

    /**
     * @param classNumber
     *            The classNumber to set.
     */
    public void setClassNumber(Integer classNumber) {
        this.classNumber = classNumber;
    }

    /**
     * @return Returns the education.
     */
    public Integer getEducation() {
        return education;
    }

    /**
     * @param education
     *            The education to set.
     */
    public void setEducation(Integer education) {
        this.education = education;
    }

    /**
     * @return Returns the eventtype.
     */
    public Integer getEventtype() {
        return eventtype;
    }

    /**
     * @param eventtype
     *            The eventtype to set.
     */
    public void setEventtype(Integer eventtype) {
        this.eventtype = eventtype;
    }

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the note.
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note
     *            The note to set.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return Returns the provider.
     */
    public Integer getProvider() {
        return provider;
    }

    /**
     * @param provider
     *            The provider to set.
     */
    public void setProvider(Integer provider) {
        this.provider = provider;
    }

    /**
     * @return Returns the session.
     */
    public String getSession() {
        return session;
    }

    /**
     * @param session
     *            The session to set.
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * @return Returns the timestamp.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            The timestamp to set.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Returns the value.
     */
    public Long getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(Long value) {
        this.value = value;
    }

}
