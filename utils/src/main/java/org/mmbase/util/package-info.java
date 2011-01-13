/**
 * This package contains all kind of generic utilities. It is a bit like {@link java.util.package-summary java.util}. Collections
 * related stuff like {@link org.mmbase.util.BijectiveMap} or related to IO like {@link IOUtil}.
 *
 * Noticable in the package are {@link org.mmbase.util.MMBaseContext} which maintains some information in a few static variables which are
 * sometimes needed. E.g. the {@link javax.servlet.ServletContext} with {@link
 * org.mmbase.util.MMBaseContext#getServletContext} and the data directory with {@link
 * org.mmbase.util.MMBaseContext#getDataDir}.
 *
 * Other importants ones are {@link org.mmbase.util.Casting}, which implements all kind of casting between types, and
 * {@link org.mmbase.util.ResourceLoader}, which is used to open resources using a fall back mechanism. There are
 * used in many places in MMBase.
 *
 *
 */
package org.mmbase.util;