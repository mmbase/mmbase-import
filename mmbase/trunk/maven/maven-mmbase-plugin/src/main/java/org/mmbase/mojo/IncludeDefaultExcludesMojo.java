/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.mojo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import org.apache.maven.plugin.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * The war-plugin of maven sucks a bit, because it lacks a useDefaultExcludes property.
 * This only does that, it includes the default excludes of the webapp dir. This makes development
 * of JSP templates easier, because you don't have to copy the damn thing every time, but can simply
 * change it in place and check it in from there.
 *
 * @phase process-resources
 * @goal include-default-excludes
 * @requiresProject
 * @author Michiel Meeuwissen
 */

public class IncludeDefaultExcludesMojo extends AbstractMojo {

   /**
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;

    /**
     * Single directory for extra files to include in the WAR.
     *
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File warSourceDirectory;

    /**
     * The directory where the webapp is built.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     */
    private File webappDirectory;

    public void execute() throws MojoExecutionException {
        String packaging = project.getPackaging();
        if (packaging.equals("war")) {
            try {
                File source = warSourceDirectory;
                File dest   = webappDirectory;
                int count = copyExcludes(source, dest);
                getLog().info("Copied " + count + " versioning files from " + source + " to " + dest);
            } catch (IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
        }
    }

    private Pattern DEFAULTEXCLUDES = Pattern.compile("\\.svn|CVS");

    private int copyExcludes(File src, File dst) throws IOException {
        int tot = 0;
        if (src == null) {
            getLog().info("Source dir is null");
        } else {
            for (File sub : src.listFiles()) {
                if (sub.isDirectory()) {
                    File subDest = new File(dst, sub.getName());
                    if (DEFAULTEXCLUDES.matcher(sub.getName()).matches()) {
                        tot += copyDirectory(sub, subDest);
                    } else {
                        tot += copyExcludes(sub, subDest);
                    }
                } else {
                    //ignore files
                }
            }
        }
        return tot;
    }

    private int copyDirectory(File src, File dst) throws IOException {
        int tot = 0;
        if (dst.mkdirs()) tot++;
        for (File sub : src.listFiles()) {
            File subDest = new File(dst, sub.getName());
            if (sub.isDirectory()) {
                tot += copyDirectory(sub, subDest);
            } else {
                if (FileUtils.copyFileIfModified(sub, subDest)) tot++;
            }
        }
        return tot;
    }




}
