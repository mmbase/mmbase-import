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

    private FilenameFilter xml = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return  name.endsWith(".xml");
            }
        };
    private FilenameFilter dirs = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return  filter.accept(dir, name) && new File(dir, name).isDirectory();
            }
        };

    protected void createIndex(File dir, File dest) throws IOException {
        if (dir.isDirectory()) {
            File index = new File(dir, "INDEX");
            if (! index.exists()) {
                File destIndex = new File(dest, "INDEX");
                dest.mkdirs();
                getLog().info("Generating " + destIndex);
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destIndex), "UTF-8"));
                String files[] = dir.list(xml);
                for(int i = 0; i < files.length; i++) {
                    File file = new File(dir, files[i]);
                    w.write(files[i]);
                    w.newLine();
                }
                String subdirs[] = dir.list(dirs);
                for(int i = 0; i < subdirs.length; i++) {
                    w.write(subdirs[i]);
                    w.newLine();
                    createIndex(new File(dir, subdirs[i]), new File(dest, subdirs[i]));
                }
                w.close();

            }
        }
    }

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        String packaging  = project.getPackaging();
        if (packaging.equals("war") || packaging.equals("jar")) {
            try {
                File configDir = new File(project.getBasedir(), "config");
                File dest      = new File(project.getBuild().getDirectory(),
                                          "generated-resources" + File.separator + "org" + File.separator + "mmbase" + File.separator + "config");
                if (configDir.isDirectory()) {
                    createIndex(new File(configDir, "modules"),    new File(dest, "modules"));
                    createIndex(new File(configDir, "components"), new File(dest, "components"));
                    createIndex(new File(configDir, "builders"),   new File(dest, "builders"));
                }
            } catch(IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
        }
        if (packaging.equals("war")) {
            try {
                File webapp = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName());
                File templates = new File(project.getBasedir(), "templates");
                if (templates.isDirectory()) {
                    String templatesTarget = (String) project.getProperties().get("templatesTarget");
                    if(templatesTarget == null) {
                        templatesTarget = "mmbase/" + project.getArtifactId().substring("mmbase-".length());
                    }
                    templatesTarget.replaceAll("/", File.separator);
                    File target = new File(webapp, templatesTarget);
                    getLog().info("Copying " + templates + " to " + target);
                    int[] result = copyDirectory(templates, target, true);
                    getLog().info("Copied " + result[0] + " files. " + result[1] + " files were not modified");
                }
                File blocks = new File(project.getBasedir(), "blocks");
                if (blocks.isDirectory()) {
                    String blocksTarget = (String) project.getProperties().get("blocksTarget");
                    if (blocksTarget == null) {
                        blocksTarget = "mmbase" + File.separator + "blocks" + File.separator + project.getArtifactId().substring("mmbase-".length());
                    }
                    blocksTarget.replaceAll("/", File.separator);
                    File target = new File(webapp, blocksTarget);
                    getLog().info("Copying " + blocks + " to " + target);
                    int[] result = copyDirectory(blocks, target, true);
                    getLog().info("Copied " + result[0]+ " files. " + result[1] + " files were not modified");
                }
                File mmbase = new File(project.getBasedir(), "mmbase");
                if (mmbase.isDirectory()) {
                    File target = new File(webapp, "mmbase");
                    getLog().info("Copying " + blocks + " to " + target);
                    int[] result = copyDirectory(mmbase, target, true);
                    getLog().info("Copied " + result[0]+ " files. " + result[1] + " files were not modified");
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
