/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.Map;
import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.security.AuthenticationData;
import org.mmbase.security.ActionRepository;

/**
 * A CloudContext is the general 'factory' for  a MMBase bridge implementation. A CloudContext object can
 * be aquired by the static methods of {@link ContextProvider}.
 *
 * The methods which are used mostly are {@link #getCloud(String)} to have an 'anonymous' {@link Cloud}
 * which normally suffices for readonly acces and {@link #getCloud(String, String, Map)}
 * returns an {@link org.mmbase.security.Authentication}ed Cloud.
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
    ModuleList getModules();

    /**
     * Returns the module with the specified name.
     *
     * @param name                the name of the module to be returned
     * @return                    the requested module
     * @throws NotFoundException  if the specified module could not be found
     */
    Module getModule(String name) throws NotFoundException;

    /**
     * Returns whether the module with the specified name is available.
     *
     * @param name                the name of the module
     * @return                    <code>true</code> if the module is available
     */
    boolean hasModule(String name);

    /**
     *
     * Returns the cloud with the specified name. The available cloud names are returned by {@link #getCloudNames}.
     *
     * @param name                The name of the cloud to be returned, this is very often "mmbase".
     * @return                    The requested cloud
     * @throws NotFoundException  if the specified cloud could not be found
     * @throws SecurityException       if no anonymous user can be created
     */
    Cloud getCloud(String name);

    /**
     * Returns the cloud with the specified name, with authentication
     *
     * @param name                The name of the cloud to be returned. See {@link #getCloudNames}
     * @param authenticationType  The type of authentication, which should be
     *                             used by the authentication implementation.. This is one of the
     *                             strings returned by {@link #getAuthentication}.{@link
     *                             AuthenticationData#getTypes}.
     *                             Typically, 'anonymous', 'class' and 'name/password' are
     *                             supported.
     *
     *
     * @param loginInfo           The required user information and credentials. What exactly is
     *                            required, depends on the {@link org.mmbase.security.Authentication} implementation,
     *                            and the value of the authenticationType parameter.
     *                            If no credentials are needed <code>null</code> can be specified
     *                            (This for example is the case with authenticationType 'class').
     *                            It is possible to create this Map using {@link
     *                            #getAuthentcation}.{@link
     *                            AuthenticationData#createParameters(String) }.{@link
     *                            org.mmbase.util.functions.Parameters#toMap}.
     *                            That e.g. makes it possible to know beforehand what keys can be
     *                            used in the loginInfo Map, given a certain authenticationType.
     *
     * @return                    the requested cloud
     * @throws NotFoundException  if the specified cloud could not be found
     */
    Cloud getCloud(String name, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException;

    /**
     * Returns the cloud with the specified name, based on an existing User object.
     *
     * This user object may originate from a call to {@link Cloud#getUser} or directly from {@link org.mmbase.security.Authentication#login}).
     * The security implementation <em>may</em> depend on implementation details of this
     * UserContext.
     *
     * @param name                The name of the cloud to be returned. See {@link #getCloudNames}.
     * @param user                The user object for which this cloud object must be created. Many
     *                            security implementation require this object to be created by
     *                            themselves.
     *
     * @return                    the requested cloud
     * @throws NotFoundException thrown when cloud not found
     * @since MMBase-1.8
     */
    Cloud getCloud(String name, org.mmbase.security.UserContext user) throws NotFoundException;

    /**
     * Returns the names of all the clouds known to the system. Most bridge implementations return a
     * list with one entry 'mmbase'.
     *
     * @return  A StringList of all clouds names known to our Context
     */
    StringList getCloudNames();

    /**
     * Returns the default character encoding, which can be used as a default. E.g. 'UTF-8'.
     * This is a setting in MMBase which you may want to access. It is nowadays of little
     * importance, and you can ignore it and simply always use UTF-8 or so.
     *
     * @return  A string with the character encoding
     * @since   MMBase-1.6
     *
     */
    String getDefaultCharacterEncoding();


    /**
     * Returns the default locale setting. This may correspond to the JVM default, but it also may
     * not. The point is that the JVM may be shared by other applications. It corresponds to a
     * property of the mmbase module (in 'mmbaseroot.xml').
     *
     * @return  A Locale object
     * @since   MMBase-1.6
     */
    java.util.Locale getDefaultLocale();


    /**
     * Returns the default time zone. This may or may not correspond to the JVM default. It
     * corresponds to a property of the mmbase module (in 'mmbaseroot.xml').
     *
     * @return the default time zone
     * @since MMBase-1.8
     */
    java.util.TimeZone getDefaultTimeZone();

    /**
     * Returns a new, empty field list
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    FieldList createFieldList();

    /**
     * Returns a new, empty node list.
     * Note that it is generally better to use {@link Cloud#createNodeList} or {@link
     * NodeManager#createNodeList}, because then the object can be properly associated with a {@link Cloud}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    NodeList createNodeList();

    /**
     * Returns a new, empty relation list
     * Note that it is generally better to use {@link Cloud#createRelationList} or {@link NodeManager#createRelationList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    RelationList createRelationList();

    /**
     * Returns a new, empty node manager list
     * Note that it is generally better to use {@link Cloud#createNodeManagerList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    NodeManagerList createNodeManagerList();

    /**
     * Returns a new, empty relation manager list
     * Note that it is generally better to use {@link Cloud#createRelationManagerList}.
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    RelationManagerList createRelationManagerList();

    /**
     * Returns a new, empty module list
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    ModuleList createModuleList();

    /**
     * Returns a new, empty string list
     *
     * @return  The empty list
     * @since   MMBase-1.6
     */
    StringList createStringList();


    /**
     * Acquires information about the currently configuration Authentication implementation.
     * @return current Authentication information
     * @since MMBase-1.8
     */
    AuthenticationData getAuthentication();

    /**
     * Returns the Repository with actions
     * @return Repository with actions
     * @since MMBase-1.9
     */
    ActionRepository getActionRepository();


    /**
     * Returns whether MMbase is up and running
     * @return <code>true</code> when mmbase is running
     * @since MMBase-1.8
     */
    boolean isUp();

    /**
     * Assert whether MMbase is up and running. This will wait until it is.
     * @since MMBase-1.8
     */
    CloudContext assertUp();

    /**
     * The String which was usesd, and could be used again to acquire this cloud context using {@link ContextProvider#getCloudContext(String)}.
     * @since MMBase-1.9
     */
    String getUri();

    /**
     * @since MMBase-2.0
     */
    SearchQueryHandler getSearchQueryHandler();
 }
