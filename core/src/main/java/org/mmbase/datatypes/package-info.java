/**
 * This package defines and implements {@link org.mmbase.datatypes.DataType}s, which are a way to apply restrictions to and
 * annotate certain data. DataTypes are most commonly associated with {@link
 * org.mmbase.bridge.Field}s and function {@link org.mmbase.util.functions.Parameter}s.
 *
 * DataTypes are usually defined via a piece of XML. DataTypes can extend other datatypes. A set of
 * basic datatypes which can be used or extend from are defined in <a
 * href="https://scm.mmbase.org/mmbase/trunk/core/src/main/config/datatypes.xml">datatypes.xml</a>
 * (of wich multiple can exist simultanously).
 *
 * Whether a certain value is a valid value for a certain datatype can also be checked via
 * <a
 * href="https://scm.mmbase.org/mmbase/trunk/base-webapp/src/main/webapp/mmbase/validation/validation.js.jsp">javascript
 * </a>. Further information about that is also available in the DataTypes documentation.
 *
 *
 * @since MMBase-1.8
 */
package org.mmbase.datatypes;