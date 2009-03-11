package com.finalist.cmsc.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;

/**
 * Utilities to map Node fields to POJO's by using Apache Commons BeanUtils
 * 
 * @author Wouter Heijke
 */
public class MMBaseNodeMapper {
	private static Log log = LogFactory.getLog(MMBaseNodeMapper.class);

	/**
	 * Maps fields from a MMBase node to a Java Object
	 * @param <T> Type of object to copy node to
	 * 
	 * @param node MMBase Node to use
	 * @param nodeClass Java object to place values from source Node fields into
	 * @return The initialized Java object
	 */
	public static <T> T copyNode(Node node, T nodeClass) {

		if (node != null && nodeClass != null) {
			NodeManager nodeManager = node.getNodeManager();

			FieldList fl = nodeManager.getFields();
			FieldIterator fli = fl.fieldIterator();
			while (fli.hasNext()) {
				Field f = fli.nextField();
				String mmname = f.getName();
				String pojoname = mmname;
				
            if (mmname.equalsIgnoreCase("number")) {
               pojoname = "id";
            } else if (mmname.equalsIgnoreCase("otype")) {
               pojoname = "nodeType";
            }

            if (pojoname != null) {
					if (PropertyUtils.isWriteable(nodeClass, pojoname)) {
					   Object v = null; 
                  if (mmname.equalsIgnoreCase("otype")) {
                     v = node.getObjectValue(mmname);
                     v = getOType(v);
                  } else {
   					   if (!mmname.equalsIgnoreCase("number") && f.getType() == Field.TYPE_NODE) {
   					      v = node.getNodeValue(mmname);
      					} else {
   					      v = node.getObjectValue(mmname);
   					   }
   					}
					   
					   if (v != null) {
   					   try {
                        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(nodeClass, pojoname);

                        if (descriptor.getPropertyType().isEnum()) {
                           Enum a = (Enum) PropertyUtils.getProperty(nodeClass, pojoname);
                           v = Enum.valueOf(a.getClass(), (String) v);
                        }

   							PropertyUtils.setProperty(nodeClass, pojoname, v);
                     } catch (IllegalArgumentException e) {
                         log.error("IllegalArgumentException for Node '" + node.getNumber()
                                 + "' on Class '" + nodeClass.getClass().getName()
                                 + "' on fieldname '" + mmname + "'"); 
                         throw e;
   						} catch (IllegalAccessException e) {
   							log.error("IllegalAccessException for Node '" + node.getNumber()
                                       + "' on Class '" + nodeClass.getClass().getName()
                                       + "' on fieldname '" + mmname + "'");
   						} catch (InvocationTargetException e) {
   							log.error("InvocationTargetException for Node '" + node.getNumber()
                                       + "' on fieldname '" + mmname + "'");
   						} catch (NoSuchMethodException e) {
   							log.error("NoSuchMethodException for Node '" + node.getNumber()
                                       + "' on fieldname '" + mmname + "'");
   						}
					   }
					} else {
						//log.warn("Property '" + fname + "' doesn't exist or not writable");
					}
				}
			}
		}

		return nodeClass;
	}

	private static Object getOType(Object v) {
	    if (v instanceof NodeManager) {
	        return ((NodeManager) v).getName();
	    }
        return null;
    }

    /**
	 * Maps fields from a MMBase node to a Java Class
	 * 
	 * @param <T> Type of object to copy node to
	 * 
	 * @param node MMBase Node to use
	 * @param clazz Java class to place values from source Node fields into
	 * @return Instance of the Java Class initialized with the fields from the
	 *         Node
	 */
	public static <T> T copyNode(Node node, Class<T> clazz) {
		T nodeClass = null;

		if (node != null && clazz != null) {
			try {
				nodeClass = clazz.newInstance();
				nodeClass = copyNode(node, nodeClass);
			} catch (InstantiationException e) {
				log.error("InstantiationException for Node '" + node.getNumber() + "' on Class '"
						+ clazz.getName() + "'");
			} catch (IllegalAccessException e) {
				log.error("IllegalAccessException for Node '" + node.getNumber() + "' on Class '"
						+ clazz.getName() + "'");
			}
		}
		return nodeClass;
	}
    
    public static <T> List<T> convertList(List<Node> l, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (int i = 0; i < l.size(); i++) {
            T object = copyNode(l.get(i), clazz);
            result.add(object);
        }
        return result;
    }

	public static void copyBean(Object bean, Node node) {
		if (node != null && bean != null) {
			NodeManager nodeManager = node.getNodeManager();

			FieldList fl = nodeManager.getFields();
			FieldIterator fli = fl.fieldIterator();
			while (fli.hasNext()) {
				Field f = fli.nextField();
				String fname = f.getName();

				if (fname != null) {
					log.debug("field='" + fname + "'");

					if (fname.equalsIgnoreCase("number") || fname.equalsIgnoreCase("otype") || fname.equalsIgnoreCase("owner")) {
						log.debug("ignore:" + fname);
					} else {
						if (PropertyUtils.isReadable(bean, fname)) {
							try {
								Object value = PropertyUtils.getProperty(bean, fname);

								int fieldType = f.getType();
								switch (fieldType) {
								case Field.TYPE_DOUBLE:
									node.setDoubleValue(fname, ((Double) (value)).doubleValue());
									break;
								case Field.TYPE_FLOAT:
									node.setFloatValue(fname, ((Float) (value)).floatValue());
									break;
								case Field.TYPE_INTEGER:
									node.setIntValue(fname, ((Integer) (value)).intValue());
									break;
								case Field.TYPE_LONG:
									node.setLongValue(fname, ((Long) (value)).longValue());
									break;
								case Field.TYPE_NODE:
									break;
								case Field.TYPE_STRING:
									node.setStringValue(fname, (String) value);
									break;
                                case Field.TYPE_BOOLEAN:
                                    boolean bool = false;
                                    if (value instanceof Boolean) {
                                        bool = ((Boolean) value).booleanValue();
                                    }
                                    if (value instanceof String) {
                                        bool = Boolean.valueOf((String) value).booleanValue();
                                    }
                                    node.setBooleanValue(fname, bool);
                                    break;
                                case Field.TYPE_DATETIME:
                                    node.setDateValue(fname, (Date) value);
                                    break;
								default:
									node.setObjectValue(fname, value);
								}

							} catch (IllegalArgumentException e) {
								log.error("IllegalArgumentException for Node '" + node.getNumber() + "' on Class '"
										+ bean.getClass().getName() + "' on fieldname '" + fname + "'");
								throw e;
							} catch (IllegalAccessException e) {
								log.error("IllegalAccessException for Node '" + node.getNumber() + "' on Class '"
										+ bean.getClass().getName() + "' on fieldname '" + fname + "'");
							} catch (InvocationTargetException e) {
								log.error("InvocationTargetException for Node '" + node.getNumber() + "' on fieldname '" + fname
										+ "'");
							} catch (NoSuchMethodException e) {
								log.error("NoSuchMethodException for Node '" + node.getNumber() + "' on fieldname '" + fname
										+ "'");
							}
						} else {
						//	log.warn("Property '" + fname + "' doesn't exist or not readable");
						}

					}
				}
			}
		}
	}

}
