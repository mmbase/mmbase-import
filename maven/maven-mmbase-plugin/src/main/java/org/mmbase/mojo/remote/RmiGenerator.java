/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.mojo.remote;

import java.lang.reflect.*;
import java.io.File;


/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id$
 */
public class RmiGenerator extends AbstractClassGenerator {

    String originalName;
    String interfaceName;
    String rmiName;

    public RmiGenerator(Class<?> c) {
        super(c);
    }

    public void generateHeader() {
        generateLicense();
        buffer.append("package org.mmbase.bridge.remote.rmi;\n");
        buffer.append("\n");
        buffer.append("import org.mmbase.bridge.*;\n");
        buffer.append("import org.mmbase.cache.*;\n");
        buffer.append("import org.mmbase.datatypes.*;\n");
        buffer.append("import org.mmbase.security.*;\n");
        buffer.append("import org.mmbase.storage.search.*;\n");
        buffer.append("import org.mmbase.util.functions.*;\n");
        buffer.append("import org.mmbase.util.logging.*;\n");
        buffer.append("import java.util.*;\n");
        buffer.append("import java.rmi.*;\n");
        buffer.append("import java.rmi.server.*;\n");
        buffer.append("import org.mmbase.bridge.remote.*;\n\n");
        buffer.append("import org.mmbase.bridge.remote.util.*;\n\n");

        buffer.append("/**\n");
        buffer.append(" * " + rmiName + " in a generated remote implementation of " + interfaceName + "<br />\n");
        buffer.append(" * This remote implementation is used by rmci to create a stub and skeleton for communication between remote and server.\n");
        buffer.append(" * @author generated by " + this.getClass().getName() + "\n");
        buffer.append(" */\n");
        buffer.append(" //DO NOT EDIT THIS FILE, IT IS GENERATED by " + this.getClass().getName() + "\n");
    }

    @Override
    protected void appendMethod(Method m) {
        appendMethodHeader(m, true, true);
        buffer.append(" throws RemoteException {\n");

        int paramCounter = 0;
        for (Type t:m.getGenericParameterTypes()) {
            //
            Type ct = getComponentType(t);
            // TODO: should also wrap if the result is an typevariable array?
            if (needsRemote(ct)) {
                if (((Class<?>)ct).isArray()) { // not remote!
                    indent4();
                    appendTypeInfo(ct,true,false);
                    buffer.append("[] localArg" + paramCounter + " = new ");
                    appendTypeInfo(ct,true,false);
                    buffer.append("[arg" + paramCounter + ".length];\n");
                    indent4();
                    buffer.append("for(int i = 0; i <arg" + paramCounter + ".length; i++ ) {\n");
                    indent6();
                    buffer.append("localArg" + paramCounter + "[i] = (");
                    appendTypeInfo(ct);
                    buffer.append(")StubToLocalMapper.get(arg" + paramCounter + "[i] == null ? \"\" + null : arg" + paramCounter + "[i].getMapperCode());");
                    indent4();
                    buffer.append("}\n");
                } else {
                    indent4();
                    appendTypeInfo(ct,true,false);
                    buffer.append(" localArg" + paramCounter + " = (");
                    appendTypeInfo(ct,true,false);
                    buffer.append(")StubToLocalMapper.get(arg" + paramCounter);
                    buffer.append(" == null ? \"\" + null : arg" + paramCounter + ".getMapperCode());\n");
                }
            }
            paramCounter++;
        }


        indent4();
        Type returnType = m.getGenericReturnType();
        if (!returnType.equals(Void.TYPE)) {
            buffer.append("final ");
            appendTypeInfo(returnType,true);
            buffer.append(" retval;\n");
        }
        indent4();
        buffer.append("try {\n");
        indent6();
        if (!returnType.equals(Void.TYPE)) {
            buffer.append("retval = (");
            appendTypeInfo(returnType,true);
            buffer.append(")");
        }

        boolean needToWrap = needtoWrap(returnType);
        if (needToWrap) {
            buffer.append("ObjectWrapper.localToRMIObject(getOriginalObject()." + m.getName() + "(");
        } else {
            buffer.append("getOriginalObject()." + m.getName() + "(");
        }

        paramCounter = 0;
        Type[] parameters = m.getGenericParameterTypes();
        for (Type t : parameters) {
            Type componentType = getComponentType(t);
            if (needsRemote(componentType)) {
                buffer.append("localArg" + paramCounter);
            } else if (isBasicTypeVariable(componentType) && !(t instanceof GenericArrayType)) {
                buffer.append("(" + ((TypeVariable<?>)componentType).getName() + ")ObjectWrapper.rmiObjectToLocal(arg" + paramCounter + ")");
            } else if (isBasicClass(componentType) && !((Class<?>)componentType).isArray()) {
                buffer.append("(" + ((Class<?>)componentType).getName() + ")ObjectWrapper.rmiObjectToLocal(arg" + paramCounter + ")");
            } else {
                buffer.append("arg" + paramCounter);
            }
            paramCounter++;
            if (paramCounter < parameters.length) {
                buffer.append(", ");
            }
        }
        // TODO: should also wrap if the result is an array?

        if (needToWrap) {
            buffer.append(")");
        }
        buffer.append(");\n");

        indent4();
        buffer.append("}\n");
        indent4();
        buffer.append("catch (RuntimeException rte) { throw rte; }\n");
        indent4();
        buffer.append("catch (Exception ex) { throw new RemoteException(ex.getMessage(), ex); }\n");

        if (!returnType.equals(Void.TYPE)) {
            indent4();
            buffer.append("return retval;\n");
        }

        buffer.append("  }\n\n");
    }

    @Override
    public void generate() {
        originalName = getShortName(currentClass);
        interfaceName = "Remote" + originalName;
        rmiName = interfaceName + "_Rmi";
        boolean isExtendingRemote = false;;

        generateHeader();

        buffer.append("public class " + rmiName);
        appendTypeParameters(currentClass.getTypeParameters());

        if (isListIterator(currentClass)) {
            buffer.append(" extends RemoteIterator_Rmi");
            Type[] typeParameters = getListIteratorTypeParameters(currentClass);
            appendListTypeParameters(typeParameters, true, false);
            isExtendingRemote = true;
        } else {
            if (isList(currentClass)) {
                buffer.append(" extends RemoteBridgeList_Rmi");
                Type[] typeParameters = getListTypeParameters(currentClass);
                appendListTypeParameters(typeParameters, true, false);
                isExtendingRemote = true;
            } else {
                buffer.append(" extends ServerMappedObject_Rmi<");
                buffer.append(originalName);
                buffer.append("> ");
            }
        }

        buffer.append(" implements " + interfaceName);
        appendTypeParameters(currentClass.getTypeParameters(), true);
        buffer.append(", ");

        Type[] interfaces = currentClass.getGenericInterfaces();
        resolveTypeParameters(interfaces);
        for (Type element : interfaces) {
            Type ct = getComponentType(element);
            if (needsRemote(ct)) {
                appendTypeInfo(element, true, true);
                buffer.append(", ");
            }
        }
        buffer.append("Unreferenced  {\n");

        buffer.append("   private static Logger log = Logging.getLoggerInstance(" + rmiName + ".class);\n");

        //constructor
        buffer.append("   public " + rmiName + "(" + originalName + " originalObject) throws RemoteException{\n");
        buffer.append("    super(originalObject);\n");
        buffer.append("      log.debug(\"new " + rmiName + "\");\n");
        buffer.append("   }\n");

        // methods
        appendMethods();

        if (isExtendingRemote) {
            generateOriginalObject();
        }

        buffer.append("\n");

        buffer.append("}\n");
    }

    private void generateOriginalObject() {
        buffer.append("  public ").append(originalName);
        appendTypeParameters(currentClass.getTypeParameters(), true, false);
        buffer.append(" getOriginalObject() {\n");
        buffer.append("    return ");
        buffer.append("(").append(originalName);
        appendTypeParameters(currentClass.getTypeParameters(), true, false);
        buffer.append(") ");
        buffer.append("super.getOriginalObject();\n");
        buffer.append("  }\n\n");
    }

    public void generate(File rmiDir) {
        generate();
        writeSourceFile(new File(rmiDir, rmiName + ".java"));
    }

}
