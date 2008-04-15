package com.finalist.cmsc.tools.generator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneratorReport extends BatchOperation {

    private static final Log log = LogFactory.getLog(GeneratorReport.class);

    public void generatorReport() {
        List<VCSConfig> configs = getConfigs();
        FileOutputStream out = null;
        try {
            // TODO test if file existed ,content should not be added to the
            // tail of the file.
            out = new FileOutputStream(targetReportFileLocation
                    + File.separator + "projectreport.xml", true);
            String head = "<?xml version='1.0' encoding='utf-8' ?><?xml-stylesheet type='text/xsl' href='report.xsl'?>";
            append(out, head);
            append(out, "<cmscprojects>");
            for (VCSConfig vcsConfig : configs) {
                String path = this.dest + File.separator
                        + vcsConfig.getModule() + File.separator;
                String url = vcsConfig.getUrl();
                searchNormolProject(out, path, url);
            }
            append(out, "</cmscprojects>");
            out.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNnormalProjectPath(FileOutputStream out,
                                       String normolProjectPath, String url) throws IOException {

        append(out, "<cmscproject>");
        append(out, String.format("<name>%s</name>\n",
                getNormalName(normolProjectPath)));
        append(out, String.format("<source>%s</source>\n", url));

        String pathOfProjectFile = normolProjectPath + File.separator
                + "maven-base" + File.separator + "project.xml";
        String pathOfPropertiesFile = normolProjectPath + File.separator
                + "maven-base" + File.separator + "project.properties";
        String projectXmlFile = getProjectXmlFile(pathOfProjectFile,
                pathOfPropertiesFile);
        append(out, String.format("<maven>%s</maven>\n", projectXmlFile));

        String projectVersion = getNormalProjectVersions(projectXmlFile);
        append(out, String.format("<versions>%s</versions>\n", projectVersion));
        iteratorSubProject(out, normolProjectPath, pathOfPropertiesFile);

        append(out, "</cmscproject>\n");
    }

    // get subproject
    private void iteratorSubProject(FileOutputStream out, String parentLocation,
                                    String pathOfPropertiesFile) {

        File[] filesOrDirs = new File(parentLocation).listFiles();

        for (File filesOrDir : filesOrDirs) {
            if (filesOrDir.isFile())
                continue;
            File projectFile = new File(filesOrDir.getPath() + File.separator
                    + "project.xml");
            String subProjectPropertiesFile = filesOrDir.getPath()
                    + File.separator + "project.properties";
            if (projectFile.exists()
                    && !filesOrDir.getPath().contains("maven-base")) {

                String pathOfProjectFile = projectFile.getPath();
                String subProjectXmlFile = getProjectXmlFile(pathOfProjectFile,
                        subProjectPropertiesFile, pathOfPropertiesFile);
                String readme = getReadMe(filesOrDir.getPath());

                append(out, "<subproject>");
                append(out, String.format("<name>%s</name>\n", filesOrDir
                        .getName()));
                append(out, String.format("<readme>%s</readme>\n", readme));
                append(out, String.format("<maven>%s</maven>\n",
                        subProjectXmlFile));
                append(out, "</subproject>");
            } else if (!projectFile.exists()) {
                iteratorSubProject(out, filesOrDir.getPath(),
                        pathOfPropertiesFile);
            }
        }
    }

    // get the project.xml file
    private String getProjectXmlFile(String pathOfProjectFile,
                                     String pathOfPropertiesFile) {
        StringBuffer sb = new StringBuffer();

        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    pathOfProjectFile));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("<?xml"))
                    continue;
                sb.append(changeProperties(pathOfPropertiesFile, line));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "No project.xml,or read project.xml read error";
        }
        return sb.toString();
    }

    private String getProjectXmlFile(String pathOfProjectFile,
                                     String subFilePath, String pathOfPropertiesFile) {
        StringBuffer sb = new StringBuffer();

        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    pathOfProjectFile));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("<?xml"))
                    continue;
                sb.append(changeProperties(pathOfPropertiesFile, subFilePath,
                        line));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "No project.xml,or read project.xml read error";
        }
        return sb.toString();
    }

    // get the content of normal project versions
    private String getNormalProjectVersions(String prxml) {
        return prxml.substring(prxml.indexOf("<currentVersion>")
                + "<currentVersion>".length(), prxml
                .indexOf("</currentVersion>"));
    }

    // get the content of readme
    private String getReadMe(String folderLocation) {

        String readmeFileLocation = folderLocation + File.separator
                + "readme.txt";
        File readmeFile = new File(readmeFileLocation);
        StringBuffer sb = new StringBuffer();

        if (readmeFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(
                        readmeFile));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void append(FileOutputStream out, String content) {
        try {
            content += "\n";
            out.write(content.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error When write to report file");
        }
    }

    private String readProperties(String filePath, String key)
            throws IOException {
        Properties props = new Properties();
        String Property = null;
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        props.load(in);
        if (props.getProperty(key) != null)
            Property = props.getProperty(key);
        return Property;
    }

    private String readProperties(String filePath, String subFilePath, String key)
            throws IOException {
        Properties props = new Properties();
        Properties subProps = new Properties();
        String Property = null;
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        File subPropertiesFile = new File(subFilePath);
        if (subPropertiesFile.exists()) {
            InputStream subIn = new BufferedInputStream(new FileInputStream(
                    subFilePath));
            subProps.load(subIn);
        }
        props.load(in);
        if (subProps != null && subProps.getProperty(key) != null) {
            Property = subProps.getProperty(key);
        } else if (props.getProperty(key) != null) {
            Property = props.getProperty(key);
        }
        return Property;
    }

    private String changeProperties(String filePath, String line)
            throws IOException {
        String regEx = "\\$\\{(\\w*\\.\\w*)*\\}";
        Pattern pattern = Pattern.compile(regEx);
        String out = line;
        Matcher matcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            String keyWord = matcher.group();
            String key = keyWord.substring(keyWord.indexOf("${")
                    + "${".length(), keyWord.indexOf("}"));
            String value = readProperties(filePath, key);
            if (value != null) {
                String newResult = matcher.replaceAll(value);
                sb.append(newResult);
            } else {
                sb.append(out);
            }
        } else {
            sb.append(out);
        }
        return sb.toString();
    }

    private String changeProperties(String filePath, String subFilePath,
                                    String line) throws IOException {
        String regEx = "\\$\\{(\\w*\\.\\w*)*\\}";
        Pattern pattern = Pattern.compile(regEx);
        String out = line;
        Matcher matcher = pattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            String keyWord = matcher.group();
            String key = keyWord.substring(keyWord.indexOf("${")
                    + "${".length(), keyWord.indexOf("}"));
            String value = readProperties(filePath, subFilePath, key);
            if (value != null) {
                String newResult = matcher.replaceAll(value);
                sb.append(newResult);
            } else {
                sb.append(out);
            }
        } else {
            sb.append(out);
        }
        return sb.toString();
    }

    private void searchNormolProject(FileOutputStream out, String path,
                                     String url) throws IOException {
        File normalroot = new File(path);
        File[] filesOrDirs = normalroot.listFiles();
        for (File filesOrDir : filesOrDirs) {
            if (filesOrDir.isFile())
                continue;
            if ("maven-base".equals(filesOrDir.getName())) {
                addNnormalProjectPath(out, filesOrDir.getParent(), url);
            } else {
                searchNormolProject(out, filesOrDir.getAbsolutePath(), url);
            }
        }
    }

    private String getNormalName(String normolProjectPath) {
        StringTokenizer tokens = new StringTokenizer(normolProjectPath,
                File.separator);
        String normalName = null;
        while (tokens.hasMoreElements()) {
            normalName = tokens.nextElement().toString();
        }
        return normalName;
    }

    public static void main(String[] args) {

        switch (args.length) {
            case 0:
                printUsage();
            case 1:
                comeOn(args[0], ".", ".");
                break;
            case 2:
                comeOn(args[0], args[1], ".");
                break;
            case 3:
                comeOn(args[0], args[1], args[2]);
                break;
        }
    }

    public static void printUsage() {
        System.out.println("Need Path of config file");
        System.out.println("usage: GeneratorReport configfile [workingfolder] [reportfile]");
    }

    public static void comeOn(String configfile, String dest, String report) {

        CheckOutSourceCode bo = new CheckOutSourceCode();
        bo.setConfigfile(configfile);
        bo.setDest(dest);
        bo.checkout();

        GeneratorReport gr = new GeneratorReport();
        gr.setConfigfile(configfile);
        gr.setDest(dest);
        gr.setTargetReportFileLocation(report);
        gr.generatorReport();
    }
}
