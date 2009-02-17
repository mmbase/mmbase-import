package org.mmbase.mojo;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;
import org.apache.maven.model.*;
import org.apache.maven.artifact.deployer.ArtifactDeployer;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
 import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
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
 * @phase package
 * @goal deploy-jar
 * @requiresProject
 */
public class DeployJar extends AbstractMojo {


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
     * @parameter expression="${component.org.apache.maven.artifact.deployer.ArtifactDeployer}"
     * @required
     * @readonly
     */
    private ArtifactDeployer deployer;

    /*
     * Server Id to map on the &lt;id&gt; under &lt;server&gt; section of settings.xml
     * In most cases, this parameter will be required for authentication.
     *
     * @parameter expression="${repositoryId}" default-value="mmbase-snapshots"
     * @required
     */
    private String repositoryId;


    /**
     * Component used to create a repository
     *
     * @component
     */
     private ArtifactRepositoryFactory repositoryFactory;



    /**
     * The type of remote repository layout to deploy to. Try <i>legacy</i> for
     * a Maven 1.x-style repository layout.
     *
     * @parameter expression="${repositoryLayout}" default-value="default"
     * @required
     */
    private String repositoryLayout;

    /**
     * Map that contains the layouts
     *
     * @component role="org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout"
     */
    private Map repositoryLayouts;





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


            Artifact artifact = artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, packaging, null);


            ArtifactMetadata metadata = null;
            Artifact pomArtifact = null;
            FileWriter fw = null;
            try {
                File tempFile = File.createTempFile( "mvninstall", ".pom" );
                tempFile.deleteOnExit();

                Model model = new Model();
                model.setModelVersion("4.0.0");
                model.setGroupId( groupId);
                model.setArtifactId( artifactId);
                model.setVersion(version);
                model.setPackaging(packaging);
                model.setDescription("POM was created from mmbase:deploy-jar" );
                fw = new FileWriter(tempFile);
                new MavenXpp3Writer().write( fw, model);
                metadata = new ProjectArtifactMetadata( artifact, tempFile);
                artifact.addMetadata(metadata);
            } catch (IOException e ) {
                throw new MojoExecutionException( "Error writing temporary pom file: " + e.getMessage(), e );
            } finally {
                try {
                    fw.close();
                } catch (IOException ioe) {
                }
            }

            File file =  new File (dir + "/" + artifactId + "-" + version + "/WEB-INF/lib/" + artifactId + "-" + version + ".jar");


            ArtifactRepositoryLayout layout = ( ArtifactRepositoryLayout ) repositoryLayouts.get( repositoryLayout );


            String url = version.endsWith("SNAPSHOT") ?
                project.getDistributionManagement().getSnapshotRepository().getUrl() :
                project.getDistributionManagement().getRepository().getUrl();

            String repositoryId = version.endsWith("SNAPSHOT") ?
                project.getDistributionManagement().getSnapshotRepository().getId() :
                project.getDistributionManagement().getRepository().getId();

            boolean uniqueVersion = version.endsWith("SNAPSHOT") ?
                project.getDistributionManagement().getSnapshotRepository().isUniqueVersion() :
                project.getDistributionManagement().getRepository().isUniqueVersion();

            ArtifactRepository deploymentRepository =
                repositoryFactory.createDeploymentArtifactRepository(repositoryId, url, layout, uniqueVersion);

            try {
                deployer.deploy( file, artifact, deploymentRepository, localRepository);
            } catch (ArtifactDeploymentException e)   {
                throw new MojoExecutionException("Error installing artifact '" + artifact.getDependencyConflictId() + "': " + e.getMessage(), e );
            }

         }

     }
}
