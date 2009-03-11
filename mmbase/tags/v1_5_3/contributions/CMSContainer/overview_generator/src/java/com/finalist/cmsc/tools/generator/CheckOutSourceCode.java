package com.finalist.cmsc.tools.generator;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.ice.cvsc.CVSArgumentVector;
import com.ice.cvsc.CVSCUtilities;
import com.ice.cvsc.CVSClient;
import com.ice.cvsc.CVSEntryVector;
import com.ice.cvsc.CVSProject;
import com.ice.cvsc.CVSProjectDef;
import com.ice.cvsc.CVSRequest;
import com.ice.cvsc.CVSResponse;
import com.ice.cvsc.CVSScramble;

public class CheckOutSourceCode extends BatchOperation {

    private static final Log log = LogFactory.getLog(CheckOutSourceCode.class.getName());

    public void checkout() {
        List<VCSConfig> configs = getConfigs();

        for (VCSConfig vcsConfig : configs) {
            File path = new File(vcsConfig.getWorkingfolder());
            if ("svn".equals(vcsConfig.getType())) {
                checkOutFromSVN(vcsConfig, path);
            }
            if ("cvs".equals(vcsConfig.getType())) {
                checkOutFromCVS(vcsConfig, path);
            }
        }
    }

    private static void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /**
     * check out from svn
     *
     * @param vcsConfig
     * @param path
     */
    private void checkOutFromSVN(VCSConfig vcsConfig, File path) {
        try {
            setupLibrary();
            SVNURL url = SVNURL.parseURIDecoded(vcsConfig.getUrl());
            SVNRevision revision = SVNRevision.parse("HEAD");
            SVNClientManager manager = SVNClientManager.newInstance();
            ISVNAuthenticationManager authManager = new BasicAuthenticationManager(
                    vcsConfig.getUsername(), vcsConfig.getPassword());
            manager.setAuthenticationManager(authManager);
            manager.getUpdateClient().doCheckout(url, path, revision, revision,
                    true);
        } catch (SVNException e) {
            log.warn("Check out from svn error");
            System.out.println("-->timeout ,connect the server failure server = [" + vcsConfig.getUrl() + "] user= [" + vcsConfig.getUsername() + "] pwd=["+vcsConfig.getPassword()+"]");
        }
    }

    /**
     * check out from cvs
     *
     * @param vcsConfig
     * @param path
     */
    private void checkOutFromCVS(VCSConfig vcsConfig, File path) {
        boolean listingModules = false;

        String userName = vcsConfig.getUsername();
        String passWord = vcsConfig.getPassword();
        String url = vcsConfig.getUrl();
        String temp = url.substring(url.indexOf("@")+1);
        String hostName = temp.substring(0, temp.indexOf(":"));
        String module = vcsConfig.getModule();
        String rootDirectory = temp.substring(temp.indexOf("/"));
		
        String localDirectory = CVSCUtilities.stripFinalSeparator(path.getPath());

        boolean isPServer = true;
        int connMethod = CVSRequest.METHOD_INETD;
        int cvsPort = CVSClient.DEFAULT_CVS_PORT;
        CVSArgumentVector arguments = CVSArgumentVector.parseArgumentString("");

        File localRootDir = new File(localDirectory);
        if (!localRootDir.exists() && !listingModules) {
            if (!localRootDir.mkdirs()) {
                log.error("Could not create local directory '" + localRootDir.getPath() + "'");
                return;
            }
        }
        CVSRequest request = new CVSRequest();
        String checkOutCommand = ":co:N:ANP:deou:";
        if (!request.parseControlString(checkOutCommand)) {
            log.error("Could not parse command specification '" + checkOutCommand + "'");
            return;
        }

        CVSEntryVector entries = new CVSEntryVector();
        // append the module name onto the argument list to tell the server which module to checkout
        arguments.appendArgument(module);
        // Create the client that will connect to the server.
        CVSClient client = new CVSClient();
        client.setHostName(hostName);
        client.setPort(cvsPort);
        // Create the CVSProject that will handle the checkout
        CVSProject project = new CVSProject(client);
        // CVSProjects are defined by a CVSProjectDef
        CVSProjectDef projectDef = new CVSProjectDef(connMethod, isPServer,
                false, hostName, userName, rootDirectory, module);
        // Now establish information required by CVSProject
        project.setProjectDef(projectDef);
        project.setUserName(userName);

        project.setRepository(module);
        project.setRootDirectory(rootDirectory);
        project.setLocalRootDirectory(localDirectory);
        project.setPServer(isPServer);
        project.setConnectionPort(cvsPort);
        project.setConnectionMethod(connMethod);
        project.setServerCommand("cvs server");
        // CVS uses a simple password scramble to avoid clear passwords.
        String scrambled = CVSScramble.scramblePassword(passWord, 'A');
        project.setPassword(scrambled);
        project.establishRootEntry(rootDirectory);
        request.setPServer(isPServer);
        request.setUserName(userName);
        request.setPassword(project.getPassword());
        request.setConnectionMethod(connMethod);
        request.setServerCommand(project.getServerCommand());
        request.setRshProcess(project.getRshProcess());
        request.setPort(cvsPort);
        request.setHostName(client.getHostName());
        request.setRepository(module);
        request.setRootDirectory(rootDirectory);
        request.setRootRepository(rootDirectory);
        request.setLocalDirectory(localRootDir.getPath());
        request.setSetVariables(project.getSetVariables());
        request.responseHandler = project;
        request.setEntries(entries);
        request.appendArguments(arguments);
        request.setUserInterface(new JCVSUI());
        CVSResponse response = new CVSResponse();
        // initiate the communication and processing
        client.processCVSRequest(request, response);
        project.writeAdminFiles();
    }

    public static void main(String[] args) {
        CheckOutSourceCode bo = new CheckOutSourceCode();
        bo.setConfigfile(args[0]);
        bo.setDest(args[1]);
        bo.checkout();
	}
}
