/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.mojo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.*;
import org.mmbase.mojo.remote.*;

/**
 * Generate interfaces and classes for the remote bridge
 *
 * @goal generate-remote
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class RemoteGenerator extends AbstractMojo {

   /**
    * The current Maven project.
    *
    * @parameter default-value="${project}"
    * @readonly
    * @required
    */
   private MavenProject project;

   /**
    * Directory containing the classes and resource files that should be packaged into the JAR.
    * The directory will be registered as a compile source root of the project such that the
    * generated files will participate in later build phases like compiling and packaging.
    * @parameter expression="${outputDirectory}" default-value="${project.build.directory}/generated-sources"
    */
   private File generatedSources;

   /**
    * A set of Ant-like inclusion patterns used to select files from the source directory for processing. By default,
    * the patterns <code>**&#47;*</code> is used to select files.
    *
    * @parameter
    */
   private String[] includes;

   /**
    * A set of Ant-like exclusion patterns used to prevent certain files from being processed. By default, this set is
    * empty such that no files are excluded.
    *
    * @parameter
    */
   private String[] excludes;


   public void execute() throws MojoExecutionException {
      if (!generatedSources.exists()) {
         generatedSources.mkdirs();
      }
      File remoteDir =  new File(generatedSources + File.separator + "org" + File.separator + "mmbase" + File.separator + "bridge" + File.separator + "remote");
      remoteDir.mkdirs();
      File rmiDir = new File(remoteDir, "rmi");
      rmiDir.mkdirs();
      File proxyDir = new File(remoteDir, "proxy");
      proxyDir.mkdirs();

      List<Class<?>> objectsToWrap = new ArrayList<Class<?>>();

      Set<Artifact> arts = project.getDependencyArtifacts();
      if (arts != null) {
         ClassLoader loader = getClassLoader(arts);
         for (Artifact artifact : arts) {
            if (artifact.getGroupId().equals("org.mmbase")) {
               generateBridgeClasses(loader, artifact.getFile(), objectsToWrap, remoteDir, rmiDir, proxyDir);
            }
         }
      }

      generateObjectWrapper(objectsToWrap, remoteDir);

      addSourceRoot(generatedSources);
   }

   public ClassLoader getClassLoader(Set<Artifact> artifacts) throws MojoExecutionException {
      try {
         ClassWorld world = new ClassWorld();
         ClassRealm realm = world.newRealm("plugin.mmbase.remote.generator", Thread.currentThread()
               .getContextClassLoader());
         ClassRealm remoteGenRealm = realm.createChildRealm("mmbaseRemoteGenerator");
         Iterator<Artifact> itor = artifacts.iterator();
         getLog().info("Remote Gen Realm " + remoteGenRealm);
         while (itor.hasNext()) {
             Artifact artifact = itor.next();
             File f = artifact.getFile();
             if (f != null) {
                 getLog().info("Adding constituent " + f);
                 remoteGenRealm.addConstituent(f.toURL());
             } else {
                 getLog().error("Artifact " +  artifact + " has no file");
             }
         }
         return remoteGenRealm.getClassLoader();
      } catch (DuplicateRealmException e) {
        throw new MojoExecutionException(e.getMessage(), e);
      } catch (MalformedURLException e) {
         throw new MojoExecutionException(e.getMessage(), e);
      }
   }

   private void generateBridgeClasses(ClassLoader loader, File jarPath, List<Class<?>> objectsToWrap, File remoteDir,
         File rmiDir, File proxyDir) {

      try {
         ArchiveScanner scanner = new ArchiveScanner();
         JarFile jarfile2 = new JarFile(jarPath);
         scanner.setJarfile(jarfile2);
         if (includes != null) {
            scanner.setIncludes(includes);
         }
         if (excludes != null) {
            scanner.setExcludes(excludes);
         }
         scanner.scan();

         String[] classes = scanner.getIncludedFiles();
         for (String clazz : classes) {
            try {
               clazz = clazz.replace('/', '.');
               clazz = clazz.substring(0, clazz.length() - ".class".length());
               Class<?> c = loader.loadClass(clazz);
               generate(c, objectsToWrap, remoteDir, rmiDir, proxyDir);
            }
            catch (ClassNotFoundException e) {
               getLog().error(e);
            }
         }
      }
      catch (IOException e) {
         getLog().error(e);
      }
   }

   public void generate(Class<?> c, List<Class<?>> objectsToWrap, File remoteDir, File rmiDir, File proxyDir) {
      if (AbstractGenerator.needsRemote(c)) {
          objectsToWrap.add(c);
          new InterfaceGenerator(c).generate(remoteDir);
          new RmiGenerator(c).generate(rmiDir);
          new ProxyGenerator(c).generate(proxyDir);
      }
   }

   public void generateObjectWrapper(List<Class<?>> objectsToWrap, File remoteDir) {
       new ObjectWrapperGenerator(objectsToWrap).generate(remoteDir);
   }

   /**
    * Registers the specified directory as a compile source root for the current project.
    *
    * @param directory The absolute path to the source root, must not be <code>null</code>.
    */
   private void addSourceRoot(File directory) {
      if (this.project != null) {
         getLog().debug("Adding compile source root: " + directory);
         this.project.addCompileSourceRoot(directory.getAbsolutePath());
      }
   }
}
