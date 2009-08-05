package org.apache.maven.plugin.mmbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MmbaseUtils {
    /**
     * Unzip the specify mmbase-module file
     *
     * @param zipFileName             the mmbase-module file
     * @param outputDirectory         the temp outputDirectory
     */
    public static void unzip(String zipFileName, String outputDirectory) {
        try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(zipFileName));
            ZipEntry z = in.getNextEntry();
            while (z != null) {
                File f = new File(outputDirectory);
                f.mkdir();
                if (z.isDirectory()) {
                    String name = z.getName();
                    name = name.substring(0, name.length() - 1);
                    f = new File(outputDirectory + File.separator + name);
                    f.mkdir();
                }
                else {
                    f = new File(outputDirectory + File.separator + z.getName());
                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    byte[] buf = new byte[1024*1024];

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    
                    out.close();
                }
                z = in.getNextEntry();
            }
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Copy the temp mmbase-module directory
     *
     * @param srcPath         the source path file
     * @param dstPath         the destination path file
     */
    public static void copyMmSubDir(File srcPath, File dstPath) {
        if (srcPath.isDirectory()) {
            if (!dstPath.exists()) {
                dstPath.mkdirs();
            }
            String files[] = srcPath.list();
            for (int i = 0; i < files.length; i++) {                
                    copyMmSubDir(new File(srcPath, files[i]), new File(dstPath, files[i]));                
            }
        } else {
            if (!srcPath.exists()) {
                System.out.println("File or directory does not exist.");
                System.exit(0);
            } else {
                try {
                    InputStream in = new FileInputStream(srcPath);
                    OutputStream out = new FileOutputStream(dstPath);
                    byte[] buf = new byte[1024*1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();                    
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        }
    }
}
