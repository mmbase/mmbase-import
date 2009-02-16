package org.mmbase.mojo;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;
import org.apache.maven.model.*;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;

import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepository;
 import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

import org.apache.maven.model.io.xpp3.MavenXpp3Writer;


import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Would have likes to extends from FileInstallMojo. But that is imposible. Many private members. It
 * makes me quit tired, but well. This class therefore much more complicated than what would be sane
 * for the small thing that it tries to do.
 *
 * @phase install
 * @goal install-jar
 * @requiresProject
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
            String groupId    = project.getGroupId() + ".jars";


            Artifact artifact = artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, packaging, null);


            ArtifactMetadata metadata = null;
            Artifact pomArtifact = null;
            FileWriter fw = null;
            try {
                File tempFile = File.createTempFile( "mvninstall", ".pom" );
                tempFile.deleteOnExit();

                Model model = new Model();
                model.setModelVersion( "4.0.0" );
                model.setGroupId( groupId );
                model.setArtifactId( artifactId );
                model.setVersion( version );
                model.setPackaging( packaging );
                model.setDescription( "POM was created from mmbase:install-jar" );
                fw = new FileWriter( tempFile );
                tempFile.deleteOnExit();
                new MavenXpp3Writer().write( fw, model );
                metadata = new ProjectArtifactMetadata( artifact, tempFile );
                artifact.addMetadata( metadata );
            } catch (IOException e ) {
                throw new MojoExecutionException( "Error writing temporary pom file: " + e.getMessage(), e );
            } finally {
                try {
                    fw.close();
                } catch (IOException ioe) {
                }
            }

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
