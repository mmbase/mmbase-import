package org.mmbase.mojo;


import org.apache.maven.plugin.*;
import org.apache.maven.project.*;

import java.io.*;
import java.util.*;

/**
 * Mojo which copies the 'templates' and 'blocks' direcotory (if they exist) to the correct location
 * in the war.
 * The whole excercise is only necessary, because you can't configure in pom that it should not
 * include  a certain web-resource if the source happens to not exist.
 *
 * @phase process-resources
 * @goal install-resources
 * @requiresProject
 * @author Michiel Meeuwissen
 */
public class InstallResources extends AbstractMojo {



    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File subFile = new File(dir, name);

                // TODO, you may want to switch default excludes off,
                if (subFile.isDirectory()) {
                    if (".svn".equals(name)) {
                        return false;
                    }
                    if ("CVS".equals(name)) {
                        return false;
                    }
                }
                return true;
            }
        };

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (project.getPackaging().equals("war")) {
            try {
                File webapp = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName());
                File templates = new File(project.getBasedir(), "templates");
                if (templates.exists()) {
                    String templatesTarget = (String) project.getProperties().get("templatesTarget");
                    if(templatesTarget == null) {
                        templatesTarget = "mmbase/" + project.getArtifactId().substring("mmbase-".length());
                    }
                    templatesTarget.replaceAll("/", File.separator);
                    File target = new File(webapp, templatesTarget);
                    getLog().info("Copying " + templates + " to " + target);
                    int[] result = copyDirectory(templates, target, true);
                    getLog().info("Copied " + result[0]+ " file. " + result[1] + " files were not modified");
                }
                File blocks = new File(project.getBasedir(), "blocks");
                if (blocks.exists()) {
                    String blocksTarget = (String) project.getProperties().get("blocksTarget");
                    if (blocksTarget == null) {
                        blocksTarget = "mmbase" + File.separator + "blocks" + File.separator + project.getArtifactId().substring("mmbase-".length());
                    }
                    blocksTarget.replaceAll("/", File.separator);
                    File target = new File(webapp, blocksTarget);
                    getLog().info("Copying " + blocks + " to " + target);
                    int[] result = copyDirectory(blocks, target, true);
                    getLog().info("Copied " + result[0]+ " file. " + result[1] + " files were not modified");
                }
            } catch (IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
        }
    }


    private int[] copyDirectory(File srcPath, File dstPath, boolean exception) throws IOException{
        int[] result = {0, 0};
        if (srcPath.isDirectory()){
            dstPath.mkdirs();
            String files[] = srcPath.list(filter);
            for(int i = 0; i < files.length; i++){
                int[] r = copyDirectory(new File(srcPath, files[i]),
                                        new File(dstPath, files[i]), false);
                result[0] += r[0];
                result[1] += r[1];

            }

        } else{
            if(!srcPath.exists()){
                if (exception) {
                    throw new RuntimeException("File or directory " + srcPath + " does not exist.");
                } else {
                    getLog().warn("File or directory " + srcPath + " does not exist.");
                }
            } else {
                if (! dstPath.exists() || dstPath.lastModified() < srcPath.lastModified()) {
                    InputStream in = new FileInputStream(srcPath);
                    OutputStream out = new FileOutputStream(dstPath);
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();
                    dstPath.setLastModified(srcPath.lastModified());
                    result[0]++;
                } else {
                    result[1]++;
                }

            }

        }
        return result;
    }
}
