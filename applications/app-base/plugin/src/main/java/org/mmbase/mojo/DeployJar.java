package org.mmbase.mojo;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;
import org.apache.maven.artifact.deployer.ArtifactDeployer;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
 import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
 import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

import org.apache.maven.model.io.xpp3.MavenXpp3Writer;


import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * This goal is associated with the deploy phase of a 'war' package, to deploy the jar as well.
 *
 * @phase package
 * @goal deploy-jar
 * @requiresProject
 * @author Michiel Meeuwissen
 * @version $Id: DeployJar.java,v 1.5 2009-02-17 19:44:13 michiel Exp $
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


            Artifact artifact = artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, packaging, null);
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
