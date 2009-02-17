package org.mmbase.mojo;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

import org.apache.maven.model.io.xpp3.MavenXpp3Writer;


import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * This goal is associated with the install phase of a 'war' package, to install the jar as well.
 *
 * @phase install
 * @goal install-jar
 * @requiresProject
 * @author Michiel Meeuwissen
 * @version $Id: InstallJar.java,v 1.5 2009-02-17 19:44:13 michiel Exp $
 */
public class InstallJar extends AbstractMojo {


     /**
      * Used to create artifacts
      *
      * @component
      */
     private ArtifactFactory artifactFactory;


    /**
     * The API towards the local Maven2 repository
     *
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;



    /**
     * @parameter expression="${component.org.apache.maven.artifact.installer.ArtifactInstaller}"
     * @required
     * @readonly
     */
    protected ArtifactInstaller installer;



    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;


    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (project.getPackaging().equals("war")) {
            String packaging = "jar";
            String dir = project.getBuild().getDirectory();
            String artifactId = project.getArtifactId();
            String version    = project.getVersion();
            String groupId    = project.getGroupId();


            Artifact artifact = artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, packaging, null);
            File file =  new File (dir + "/" + artifactId + "-" + version + "/WEB-INF/lib/" + artifactId + "-" + version + ".jar");
            try {
                installer.install(file, artifact, localRepository);
                //installCheckSum(file, artifact, false);
                //installer.install(pomFile, pomArtifact, localRepository);
                //installCheckSum( pomFile, pomArtifact, false );
            } catch (ArtifactInstallationException e)   {
                throw new MojoExecutionException("Error installing artifact '" + artifact.getDependencyConflictId() + "': " + e.getMessage(), e );
            }

        }

     }

}
