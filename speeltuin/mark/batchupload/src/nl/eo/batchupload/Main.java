package nl.eo.batchupload;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Main class. When invocated, it looks if all necessary libraries are located
 * in the 'batchlib' subdirectory. If not, it tries to unpack all jar's in the current
 * archive to that subdirectory. 
 * After unpacking, the batchupload utility is started.
 * @author Johannes Verelst <johannes@verelst.net>
 * @version $ID$
 */
public class Main {

    /**
     * Main method
     * @param args Array of Strings containing the command line arguments
     */
    public static void main(String[] args) {
        nl.eo.batchupload.gui.BatchUploadMain.main(args);
    }
    
    /**
     * Unpack all jar files contained in this archive in case the 'batchlib' directory
     * does not exists. If it does exist, return immediately.
     **/
}
