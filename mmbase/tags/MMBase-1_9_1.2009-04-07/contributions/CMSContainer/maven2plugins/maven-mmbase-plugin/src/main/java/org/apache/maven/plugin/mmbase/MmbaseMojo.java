package org.apache.maven.plugin.mmbase;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which download and unpack mmbase-module file for package.
 * 
 * @goal mmbase
 * 
 * @phase process-test-resources
 */
public class MmbaseMojo extends AbstractMojo {
    /**
     * The maven project
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * mmbaseOverlays
     * 
     * @parameter
     */
    private List<MmbaseOverlay> mmbaseOverlays = new ArrayList<MmbaseOverlay>();

    /**
     * @return the project
     */
    public MavenProject getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public void setProject(MavenProject project) {
        this.project = project;
    }

    /**
     * @return the mmbaseOverlays
     */
    public List<MmbaseOverlay> getMmbaseOverlays() {
        return mmbaseOverlays;
    }

    /**
     * @param mmbaseOverlays
     *            the mmbaseOverlays to set
     */
    public void setMmbaseOverlays(List<MmbaseOverlay> mmbaseOverlays) {
        this.mmbaseOverlays = mmbaseOverlays;
    }

    /**
     * void
     */
    public void addMmbaseOverlay(MmbaseOverlay mmbaseOverlay) {
        this.mmbaseOverlays.add(mmbaseOverlay);
    }

    public void execute() throws MojoExecutionException {
        File target = new File(project.getBuild().getDirectory());

        if (!target.exists()) {
            target.mkdirs();
        }

        File webapp = new File(target, project.getArtifactId() + "-" + project.getVersion());
        if (!webapp.exists()) {
            webapp.mkdirs();
        }
        
        File mmbaseTemp = new File(target, "mmbaseTemp");
        if(!mmbaseTemp.exists()){
            mmbaseTemp.mkdirs();
        }
        
        List<Artifact> mmbaseArtifacts = getMmbaseArtifacts();

        List<MmbaseOverlay> mmbaseOverlays = convertMmbaseOverlays(mmbaseArtifacts);
        for(MmbaseOverlay mmbaseOverlay: mmbaseOverlays){
            boolean isConfigBundle = true;   //the default is true
            boolean isExampleBundle = false;  //the default is false
            boolean isTemplateBundle = true;  //the default is true
            String templatePath = "";
            if(mmbaseOverlay.getMyProperties().containsKey("config.bundle") && mmbaseOverlay.getMyProperties().getProperty("config.bundle").equals("false")){
                isConfigBundle = false;
            }
            if(mmbaseOverlay.getMyProperties().containsKey("examples.bundle") && mmbaseOverlay.getMyProperties().getProperty("examples.bundle").equals("true")){
                isExampleBundle = true;
            }
            if(mmbaseOverlay.getMyProperties().containsKey("templates.bundle") && mmbaseOverlay.getMyProperties().getProperty("examples.bundle").equals("false")){
                isTemplateBundle = false;
            }
            if(mmbaseOverlay.getMyProperties().containsKey("templates.path") && isTemplateBundle){
                templatePath = mmbaseOverlay.getMyProperties().getProperty("templates.path");                
            }
            File tempDir = getOverlayTempDirectory(mmbaseTemp, mmbaseOverlay);
            getLog().info("[ unpack and the mmbase-module of "+mmbaseOverlay.getArtifact().getArtifactId()+" to "+tempDir.getPath()+" ]");
            MmbaseUtils.unzip(mmbaseOverlay.getArtifact().getFile().getPath(), tempDir.getPath());
            getLog().info("[ copy the resources from "+tempDir.getPath()+" to "+webapp.getPath()+" ]" );
            copyToWebapp(webapp, tempDir, isConfigBundle, isExampleBundle, isTemplateBundle, templatePath);
        }
    }

    private List<MmbaseOverlay> convertMmbaseOverlays(List<Artifact> mmbaseArtifacts) {
        List<MmbaseOverlay> mmbaseOverlays = getMmbaseOverlays();

        List<MmbaseOverlay> distOverlays = new ArrayList<MmbaseOverlay>();
        Set<Artifact> overlayArtifacts = new HashSet<Artifact>();
        for (Artifact artifact : mmbaseArtifacts) {
            for (MmbaseOverlay mmbaseOverlay : mmbaseOverlays) {
                if (artifact.getGroupId().equals(mmbaseOverlay.getGroupId())
                        && artifact.getArtifactId().equals(mmbaseOverlay.getArtifactId())
                        && artifact.getVersion().equals(mmbaseOverlay.getVersion())) {
                        mmbaseOverlay.setArtifact(artifact);
                        overlayArtifacts.add(artifact);
                        distOverlays.add(mmbaseOverlay);
                }
            }
        }
        for(Artifact artifact2 : mmbaseArtifacts){
            if(!overlayArtifacts.contains(artifact2)){
                MmbaseOverlay nOverlay = new MmbaseOverlay();
                nOverlay.setArtifact(artifact2);
                distOverlays.add(nOverlay);
            }
        }
        return distOverlays;
    }

    @SuppressWarnings("unchecked")
    private List<Artifact> getMmbaseArtifacts() {
        final Set artifacts = project.getDependencyArtifacts();
        final Iterator it = artifacts.iterator();
        List<Artifact> mmbaseArtifacts = new ArrayList<Artifact>();
        while (it.hasNext()) {
            Artifact artifact = (Artifact) it.next();
            if ("mmbase-module".equals(artifact.getType())) {
                mmbaseArtifacts.add(artifact);
            }
        }
        return mmbaseArtifacts;
    }
    
    private File getOverlayTempDirectory( File mmbaseTemp,  MmbaseOverlay mmbaseOverlay)
    {
        final File groupIdDir = new File( mmbaseTemp, mmbaseOverlay.getArtifact().getGroupId() );
        if ( !groupIdDir.exists() )
        {
            groupIdDir.mkdir();
        }
        String directoryName = mmbaseOverlay.getArtifact().getArtifactId();
        final File result = new File( groupIdDir, directoryName );
        if ( !result.exists() )
        {
            result.mkdirs();
        }
        return result;
    }
    
    private void copyToWebapp(File webapp, File tempDir, boolean isConfigBundle, boolean isExampleBundle, boolean isTemplateBundle, String templatePath){
        File templateDir = new File(webapp,templatePath);
        File[] files = tempDir.listFiles();
        for(int i=0; i < files.length; i++){
            if(isTemplateBundle && "templates".equals(files[i].getName())){
                MmbaseUtils.copyMmSubDir(files[i],templateDir);
            }
            if(isConfigBundle && "config".equals(files[i].getName())){
                File configFile = new File(webapp,"WEB-INF\\config");
                if(!configFile.exists()){
                    configFile.mkdirs();
                }
                MmbaseUtils.copyMmSubDir(files[i],configFile);
            }
            if("WEB-INF".equals(files[i].getName())){
                File webFile = new File(webapp,"WEB-INF");
                if(!webFile.exists()){
                    webFile.mkdirs();
                }
                MmbaseUtils.copyMmSubDir(files[i],webFile);
            }
            if(isExampleBundle && "examples".equals(files[i].getName())){
                File examFile = new File(webapp,"examples");
                if(!examFile.exists()){
                    examFile.mkdirs();
                }
                MmbaseUtils.copyMmSubDir(files[i],examFile);
            }
        }
    }
}
