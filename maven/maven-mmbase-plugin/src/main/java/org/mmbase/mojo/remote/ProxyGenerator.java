/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.mojo.remote;

import java.lang.reflect.*;
import java.util.*;
import java.io.File;


/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id$
 */
public class ProxyGenerator extends AbstractClassGenerator {

    String originalName;
    String interfaceName;
    String proxyName;

    public ProxyGenerator(Class<?> c) {
        super(c);
    }

    public void generateHeader() {
        generateLicense();
        //create the default imports for the interface
        buffer.append("package org.mmbase.bridge.remote.proxy;\n");
        buffer.append("\n");
        buffer.append("import org.mmbase.bridge.*;\n");
        buffer.append("import org.mmbase.cache.*;\n");
        buffer.append("import org.mmbase.datatypes.*;\n");
        buffer.append("import org.mmbase.storage.search.*;\n");
        buffer.append("import org.mmbase.util.functions.*;\n");
        buffer.append("import org.mmbase.bridge.remote.*;\n");
        buffer.append("import org.mmbase.security.*;\n\n");
        buffer.append("import org.mmbase.bridge.remote.util.*;\n\n");
        buffer.append("/**\n");
        buffer.append(" * " + proxyName + " in a generated smart proxy of " + originalName + "<br />\n");
        buffer.append(" * This smart proxy is used by a local class when the MMCI is called remotely\n");
        buffer.append(" * @author generated by " + this.getClass().getName() + "\n");
        buffer.append(" */\n");
        buffer.append(" //DO NOT EDIT THIS FILE, IT IS GENERATED by " + this.getClass().getName() + "\n");
    }

    boolean abstractListMethod(Method m) {
        String name = m.getName();
        return name.equals("toArray") || name.equals("iterator") || name.equals("listIterator");
    }

    @Override
    protected void appendMethod(Method m) {
        if (!abstractListMethod(m)) {
            appendMethodHeader(m, false, false);
            buffer.append(" {\n");

            boolean unsupported = false;
            for (Class<?> t:m.getParameterTypes()) {
                if (t.equals(Comparator.class)) {
                    unsupported = true;
                    break;
                }
            }

            if (unsupported) {
                indent4();
                buffer.append("throw new UnsupportedOperationException(\" Method not supported in remote bridge \");\n");
            }
            else {
                buffer.append("    try {\n");
                String name = m.getName();
                String remoteName = name;
                if (isBasicMethod(m) || isCloneMethod(m)) {
                    remoteName = "wrapped_" + name;
                }
                int paramCounter = 0;
                for (Type t : m.getGenericParameterTypes()) {
                    //
                    Type ct = getComponentType(t);
                    if (ct instanceof TypeVariable) {
                        if (((TypeVariable<?>)ct).getBounds().length > 0) {
                            buffer.append("/* ").append(ct).append(((TypeVariable<?>)ct).getBounds()[0]).append(" */ ");
                        }
                    }
                    // TODO: should also wrap if the result is an typevariable array?
                    if (needsRemote(ct)) {
                        if (((Class<?>)ct).isArray()) { // not remote!
                            indent6();
                            appendTypeInfo(ct,true,true);
                            buffer.append("[] remoteArg").append(paramCounter).append(" = new ");
                            appendTypeInfo(ct,true,true);
                            buffer.append("[arg").append(paramCounter).append(".length];\n");
                            indent6();
                            buffer.append("for(int i = 0; i <arg").append(paramCounter).append(".length; i++ ) {\n");
                            indent8();
                            buffer.append("localArg").append(paramCounter).append("[i] = (");
                            appendTypeInfo(ct);
                            buffer.append(")( arg").append(paramCounter).append("[i] == null ? null : ");
                            buffer.append("((MappedObject) arg").append(paramCounter).append("[i]).getWrappedObject());\n");
                            indent6();
                            buffer.append("}\n");
                        } else {
                            indent6();
                            appendTypeInfo(ct,true,true);
                            buffer.append(" remoteArg").append(paramCounter).append(" = ");
                            if (!ct.equals(Object.class) && !ct.equals(String.class)
                                    && !ct.equals(Integer.TYPE) && !ct.equals(Boolean.TYPE) ) {
                                buffer.append("(");
                                appendTypeInfo(ct,true,true);
                                buffer.append(")");
                            }
                            buffer.append("(arg").append(paramCounter).append(" == null ? null : ");
                            buffer.append("((MappedObject) arg").append(paramCounter).append(").getWrappedObject());\n");
                        }
                    }
                    paramCounter++;
                }

                indent6();
                Type returnType = m.getGenericReturnType();
                boolean needToWrap = needtoWrap(returnType);

                if (!returnType.equals(Void.TYPE)) {
                    appendTypeInfo(returnType,true,false);
                    buffer.append(" retval = ");
                    if (needToWrap && !returnType.equals(Object.class)) {
                        buffer.append("(");
                        appendTypeInfo(returnType,true,false);
                        buffer.append(")");
                    }
                }

                if (needToWrap) {
                    buffer.append("ObjectWrapper.rmiObjectToRemoteProxy(getWrappedObject().")
                          .append(remoteName).append("(");
                } else {
                    buffer.append("getWrappedObject().").append(remoteName).append("(");
                }

                paramCounter = 0;
                Type[] parameters = m.getGenericParameterTypes();
                for (Type t : parameters) {
                    Type componentType = getComponentType(t);
                    if (needsRemote(componentType)) {
                        buffer.append("remoteArg").append(paramCounter);
                    } else if (isBasicTypeVariable(componentType) && !(t instanceof GenericArrayType)) {
                        buffer.append("(").append(((TypeVariable<?>)componentType).getName()).append(")");
                        buffer.append("ObjectWrapper.remoteProxyToRMIObject(arg")
                              .append(paramCounter).append(")");
                    } else if (isBasicClass(componentType) && !((Class<?>)componentType).isArray()) {
                        if (!componentType.equals(Object.class) && !componentType.equals(String.class)
                                && !componentType.equals(Integer.TYPE) && !componentType.equals(Boolean.TYPE) ) {
                            buffer.append("(").append(((Class<?>)componentType).getName()).append(")");
                        }
                        buffer.append("ObjectWrapper.remoteProxyToRMIObject(arg")
                              .append(paramCounter).append(")");
                    } else {
                        buffer.append("arg").append(paramCounter);
                    }
                    paramCounter++;
                    if (paramCounter < parameters.length) {
                        buffer.append(", ");
                    }
                }
                if (needToWrap) {
                    buffer.append(")");
                }
                buffer.append(");\n");

                // TODO: should also wrap if the result is an array?

                if (!returnType.equals(Void.TYPE)) {
                    indent6();
                    buffer.append("return retval;\n");
                }

                buffer.append("    } catch (RuntimeException e) {\n");
                buffer.append("      throw e ;\n");
                buffer.append("    } catch(Exception e) {\n");
                buffer.append("      throw new BridgeException(e.getMessage(), e);\n");
                buffer.append("    }\n");
            }
            buffer.append("  }\n\n");
        }
    }

    @Override
    public void generate() {
        originalName = getShortName(currentClass);
        interfaceName = "Remote" + originalName;
        proxyName = interfaceName + "_Proxy";
        boolean isExtendingRemote = false;;

        generateHeader();

        buffer.append("public class " + proxyName);
        appendTypeParameters(currentClass.getTypeParameters(), false, false);
        if (isListIterator(currentClass)) {
            buffer.append(" extends RemoteIterator_Proxy");
            Type[] typeParameters = getListIteratorTypeParameters(currentClass);
            appendListTypeParameters(typeParameters, true, true);
            isExtendingRemote = true;
        }
        else {
            if (isList(currentClass)) {
                buffer.append(" extends RemoteBridgeList_Proxy");
                Type[] typeParameters = getListTypeParameters(currentClass);
                appendListTypeParameters(typeParameters, true, true);
                isExtendingRemote = true;
            }
            else {
              buffer.append(" extends MappedObject_Proxy<");
              buffer.append(interfaceName);
              appendTypeParameters(currentClass.getTypeParameters(), true);
              buffer.append("> ");
            }
        }

        generateImplements();
        buffer.append(" {\n\n");

        //constructor
        generateConstructor();

        // methods
        appendMethods();

        generateWrappedObject(isExtendingRemote);

        buffer.append("}\n");
    }

    private void generateImplements() {
        buffer.append(" implements " + originalName);
        appendTypeParameters(currentClass.getTypeParameters(), true);

        Type[] interfaces = currentClass.getGenericInterfaces();
        resolveTypeParameters(interfaces);
    }

    private void generateWrappedObject(boolean isExtendingRemote) {
        buffer.append("  public ").append(interfaceName);
        appendTypeParameters(currentClass.getTypeParameters(), true);
        buffer.append(" getWrappedObject() {\n");
        buffer.append("    return ");
        if (isExtendingRemote) {
            buffer.append("(").append(interfaceName);
            appendTypeParameters(currentClass.getTypeParameters(), true);
            buffer.append(") ");
        }
        buffer.append("super.getWrappedObject();\n");
        buffer.append("  }\n\n");
    }

    private void generateConstructor() {
        buffer.append("  public ").append(proxyName).append("(").append(interfaceName);
        appendTypeParameters(currentClass.getTypeParameters(), true);

        buffer.append(" remoteObject) {\n");
        buffer.append("    super(remoteObject);\n");
        buffer.append("  }\n\n");
    }

    public void generate(File proxyDir) {
        generate();
        writeSourceFile(new File(proxyDir, proxyName + ".java"));
    }

}
