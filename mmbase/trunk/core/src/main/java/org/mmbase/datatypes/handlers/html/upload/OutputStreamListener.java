/* Licence:
*   Use this however/wherever you like, just don't blame me if it breaks anything.
*
* Credit:
*   If you're nice, you'll leave this bit:
*
*   Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
*   email : plosson@users.sourceforge.net
*/
package org.mmbase.datatypes.handlers.html.upload;

/**
 *
 * @author Pierre-Alexandre Losson
 * @author Michiel Meeuwissen (adapted for MMBase)
 * @version $Id$
 * @since MMBase-1.9.2
 */
public interface OutputStreamListener {
    void start();
    void bytesRead(int bytesRead);
    void error(String message);
    void done();
}
