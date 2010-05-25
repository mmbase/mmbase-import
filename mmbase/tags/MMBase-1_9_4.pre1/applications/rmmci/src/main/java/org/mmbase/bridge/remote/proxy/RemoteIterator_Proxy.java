/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.remote.proxy;

import java.rmi.RemoteException;
import java.util.ListIterator;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.remote.RemoteIterator;
import org.mmbase.bridge.remote.util.ObjectWrapper;


/**
 * @javadoc
 */
public class RemoteIterator_Proxy<L, R> extends MappedObject_Proxy<RemoteIterator<R>> implements ListIterator<L> {

    ListIterator<L> local;

    public RemoteIterator_Proxy(ListIterator<L> listIterator) {
      super(null);
      this.local = listIterator;
    }

    public RemoteIterator_Proxy(RemoteIterator<R> remoteObject) {
        super(remoteObject);
    }

    public boolean hasNext() {
        if (local != null) {
            return local.hasNext();
        }
        try {
            return getWrappedObject().hasNext();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public boolean hasPrevious() {
        if (local != null) {
            return local.hasPrevious();
        }
        try {
            return getWrappedObject().hasPrevious();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public L next() {
        if (local != null) {
            return local.next();
        }
        try {
            R remote = getWrappedObject().next();
            L retval = convertToLocal(remote);
            return retval;
          } catch (RuntimeException e) {
            throw e ;
          } catch(Exception e) {
            throw new BridgeException(e.getMessage(), e);
          }
    }

    public int nextIndex() {
        if (local != null) {
            return local.nextIndex();
        }
        try {
            return getWrappedObject().nextIndex();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public L previous() {
        if (local != null) {
            return local.previous();
        }
        try {
            R remote = getWrappedObject().previous();
            L retval = convertToLocal(remote);
            return retval;
          } catch (RuntimeException e) {
            throw e ;
          } catch(Exception e) {
            throw new BridgeException(e.getMessage(), e);
          }
    }

    public int previousIndex() {
        if (local != null) {
            return local.previousIndex();
        }
        try {
            return getWrappedObject().previousIndex();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public void remove() {
        if (local != null) {
            local.remove();
        }
        try {
            getWrappedObject().remove();
          } catch (RuntimeException e) {
            throw e ;
          } catch(Exception e) {
            throw new BridgeException(e.getMessage(), e);
          }
    }

    public void set(L o) {
        if (local != null) {
            local.set(o);
        }
        try {
            R remotesSet = convertToRemote(o);
            getWrappedObject().set(remotesSet);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    public void add(L o) {
        if (local != null) {
            local.add(o);
        }
        try {
            R remotesSet = convertToRemote(o);
            getWrappedObject().add(remotesSet);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BridgeException(e.getMessage(), e);
        }
    }

    private R convertToRemote(L o) {
        R remotesSet = (R) ObjectWrapper.remoteProxyToRMIObject(o);
        return remotesSet;
    }

    private L convertToLocal(R remote) throws RemoteException {
        L retval = (L) ObjectWrapper.rmiObjectToRemoteProxy(remote);
        return retval;
    }

}
