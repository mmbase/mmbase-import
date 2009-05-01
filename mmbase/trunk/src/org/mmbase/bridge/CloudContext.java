/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.Map;
import org.mmbase.security.AuthenticationData;
import org.mmbase.security.ActionRepository;

/**
 * The collection of clouds and modules within a Java Virtual Machine.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id$
 */
public interface CloudContext {


    /**
     * Returns all modules available in this context.
     *
     * @return all available modules
     */
    public ModuleList getModules();

    /**
     * Returns the module with the specified name.
     *
     * @param name                the name of the module to be returned
     * @return                    the requested module
     * @throws NotFoundException  if the specified module could not be found
     */
    public Module getModule(String name) throws NotFoundException;

    /**
     * Returns whether the module with the specified name is available.
     *
     * @param name                the name of the module
     * @return                    <code>true</code> if the module is available
     */
    public boolean hasModule(String name);

    /**

     * Returns the cloud with the specified name.
     *
     * @param name                The name of the cloud to be returned, this is always "mmbase".
     * @return                    The requested cloud
     * @throws NotFoundException  if the specified cloud could not be found
     * @throws SecurityException       if no anonymous user can be created
     */
    public Cloud getCloud(String name);

    /**
     * Returns the cloud with the specified name, with authentication
     *
     * @param name                The name of the cloud to be returned, always "mmbase".
     * @param authenticationType  The type of authentication, which should be
     *                            used by the authentication implementation.
     * @param loginInfo           the user related login information.
     * @return                    the requested cloud
     * @throws NotFoundException  if the specified cloud could not be found
     */
    public Cloud getCloud(String name, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException;

    /**
     * Returns the cloud with the specified name, based on an existing User object (e.g. of another {@link Cloud#getUser}
     * @param name                The name of the cloud to be returned, always "mmbase".
     * @param user                The user object for which this cloud object must be created.
     * @return                    the requested cloud
     * @throws NotFoundException thrown when cloud not found
     * @since MMBase-1.8
     */
    public Cloud getCloud(String name, org.mmbase.security.UserContext user) throws NotFoundException;

    /**
     * Returns the names of all the clouds known to the system
     *
     * @return  A StringList of all clouds names known to our Context
     */
    public StringList getCloudNames();

    /**
     * Returns the default character encoding, which can be used as a default.
     *
     * @return  A string with the character encoding
     * @since   MMBase-1.6
     *
     */
    public String getDefaultCharacterEncoding();


    /**
     * Returns the default locale setting.
     *
     * @return  A Locale object
     * @since   MMBase-1.6
     */
    public java.util.Locale getDefaultLocale();


    /**
     * Returns the default time zone.
     * @return the default time zone
     * @since MMBase-1.8
     */
    public java.util.TimeZone getDefaultTimeZone();

    /**
     * Returns a new, empty field list
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public FieldList createFieldList();

    /**
     * Returns a new, empty node list.
     * Note that it is generally better to use {@link Cloud#createNodeList} or {@link NodeManager#createNodeList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public NodeList createNodeList();

    /**
     * Returns a new, empty relation list
     * Note that it is generally better to use {@link Cloud#createRelationList} or {@link NodeManager#createRelationList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public RelationList createRelationList();

    /**
     * Returns a new, empty node manager list
     * Note that it is generally better to use {@link Cloud#createNodeManagerList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public NodeManagerList createNodeManagerList();

    /**
     * Returns a new, empty relation manager list
     * Note that it is generally better to use {@link Cloud#createRelationManagerList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public RelationManagerList createRelationManagerList();

    /**
     * Returns a new, empty module list
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public ModuleList createModuleList();

    /**
     * Returns a new, empty string list
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    public StringList createStringList();


    /**
     * Acquired information about the currently configuration Authentication implementation.
     * @return current Authentication information
     * @since MMBase-1.8
     */
    public AuthenticationData getAuthentication();

    /**
     * Returns the Repository with actions
     * @return Repository with actions
     * @since MMBase-1.9
     */
    public ActionRepository getActionRepository();


    /**
     * Returns whether MMbase is up and running
     * @return <code>true</code> when mmbase is running
     * @since MMBase-1.8
     */
    public boolean isUp();

    /**
     * Assert whether MMbase is up and running. This will wait until it is.
     * @since MMBase-1.8
     */
    public CloudContext assertUp();

    /**
     * The String which could be used to acquire this cloud context using {@link ContextProvider#getCloudContext(String)}.
     * @since MMBase-1.9
     */
    public String getUri();
 }
