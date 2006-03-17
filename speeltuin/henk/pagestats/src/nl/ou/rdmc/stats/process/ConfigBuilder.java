/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is the
 * Ruud de Moor Centrum of the Open University.
 */
package nl.ou.rdmc.stats.process;

import javax.xml.parsers.*;
import javax.xml.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.util.*;

import java.util.regex.*;

import org.mmbase.util.logging.*;

//import nl.ou.rdmc.stats.ILogger;
//import nl.ou.rdmc.stats.SystemLogger;

public class ConfigBuilder {

  private static final Logger log = Logging.getLoggerInstance(ConfigBuilder.class);
  //private static final ILogger log = new SystemLogger();

  private static final String STATSCONFIG_NODE = "statsconfig";
  private static final String STATSCONFIG_NAME_ATTR = "name";

  private static final String FILEEXT_NODE = "extension";
  private static final String LOGDIR_NODE = "logdir";

  private static final String DELIMITER_NODE = "delimiter";

  private static final String TAGS_NODE = "tags";
  private static final String TAG_NODE = "tag";

  private static final String SESSIONSTART_NODE = "sessionstart";
  private static final String SESSIONSTART_TAG_ATTR ="tag";
  private static final String SESSIONSTART_VALUE_ATTR ="value";


  private static final String SUBTRACES_NODE = "subtraces";
  private static final String USETAG_NODE = "usetag";
  private static final String SUBTRACE_NODE = "subtrace";
  private static final String SUBTRACE_NAME_ATTR = "name";
  private static final String VALUE_NODE = "value";

  private Config conf;

  private Stack stack;
  private String currentNode;
  private String currentTest;

  private Map buildsMap;


  public ConfigBuilder(String conf_file_name) {
    conf = new Config();
    stack = new Stack();
    buildsMap = new HashMap();
    configure(conf_file_name);
    //conf.printConfig();
  }

  public Config getConfig() {
    return conf;
  }

  private void configure(String conf_file_name) {
    try {
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dbuilder = dfactory.newDocumentBuilder();
      File file = new File(conf_file_name);
      Document doc = dbuilder.parse(file);
      build(0, conf, doc);
    } catch (ParserConfigurationException pce) {
      log.error(pce.getMessage());
    } catch (SAXException se) {
      log.error(se.getMessage());
    } catch (IOException ie) {
      log.error(ie.getMessage());
    }

  }

  private void openNode(int key, String name) {
    //Integer k = new Integer(key);
    //buildsMap.put(k, name);
    currentNode = name;
    //stack.push(k);
  }

  private String currentNode() {
    //Integer k = (Integer)stack.peek();
    //return (String)buildsMap.get(k);
    return currentNode;
  }

  private void closeNode(int key) {
    /*Integer k = new Integer(key);
    if (buildsMap.containsKey(k)) {
      buildsMap.remove(new Integer(key));
      stack.pop();
    }*/
  }

  private void addAttribute(String name, String value) {
    if (currentNode()==null) return;
    //System.out.println("Current="+currentNode()+" Attr name='"+name+"' value='"+value+"'");


    if (currentNode().equals(STATSCONFIG_NODE)) {
      if (name.equals(STATSCONFIG_NAME_ATTR)) {
        conf.setFileNamePrefix(value);
      }
    }


    if (currentNode().equals(SESSIONSTART_NODE)) {
      if (name.equals(SESSIONSTART_TAG_ATTR)) {
        conf.setSessionStartTag(value);
      }
      if (name.equals(SESSIONSTART_VALUE_ATTR)) {
        conf.setSessionStartValue(value);
      }
    }

    if (currentNode().equals(SUBTRACE_NODE)) {
      if (name.equals(SUBTRACE_NAME_ATTR)) {
        conf.addSubtrace(value);
        currentTest = value;
      }

    }
  }


  private void addText(String text) {
    if (currentNode()==null) return;

    Pattern p = Pattern.compile("[ \\s]*");
    Matcher m = p.matcher(text);
    if (m.matches()) {
      //System.out.println("Current="+currentNode()+" text= EMPTY");
      return;
    }
    //System.out.println("Current="+currentNode()+" text='"+text+"'");

    if (currentNode().equals(FILEEXT_NODE)) {
      conf.setFileExt(text);
    }

    if (currentNode().equals(LOGDIR_NODE)) {
      conf.setLogDir(text);
    }

    if (currentNode().equals(DELIMITER_NODE)) {
      conf.setDelimiter(text);
    }

    if (currentNode().equals(USETAG_NODE)) {
      conf.setTag(currentTest, text);
    }


    if (currentNode().equals(TAG_NODE)) {
      conf.addLogTag(text);
    }

    if (currentNode().equals(VALUE_NODE)) {
      conf.addSubtraceValue(currentTest, text);
    }
  }

  private void build(int objectKey, Config conf, Node node)  {

  if (node == null)  return;
  int type = node.getNodeType();
  int ownKey = node.hashCode();

  String nodeName = node.getNodeName();
  //System.out.println("P:"+objectKey+" O:"+ownKey+" Node="+nodeName+" type="+type);
  String content = node.getNodeValue();
  //System.out.println("content="+content);

  switch (type) {
        case Node.DOCUMENT_NODE: {
          build(ownKey,conf,((Document)node).getDocumentElement());
          break;
        }
        case Node.ELEMENT_NODE: {

          openNode(ownKey, nodeName);

          NamedNodeMap attrs = node.getAttributes();
          Node attr;

          for (int i = 0; i < attrs.getLength(); i++) {
                attr = attrs.item(i);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                //System.out.println("Attr name="+attrName+" value="+attrValue);

                addAttribute(attrName, attrValue);

          }

          NodeList children = node.getChildNodes();
          if (children != null) {
                int len = children.getLength();
                for (int i = 0; i < len; i++)  build(ownKey, conf, children.item(i));
          }
          break;
        }
        case Node.TEXT_NODE: {

          addText(content);
          closeNode(objectKey);

          break;
        }

      }
    }



}
