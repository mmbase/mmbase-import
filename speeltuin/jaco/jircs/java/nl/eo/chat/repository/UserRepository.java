/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository;

import java.net.Socket;
import java.util.Date;
import java.util.Set;

/**
 * The user repository holds connected sockets and related user objects.
 *
 * @author Jaco de Groot
 */
public interface UserRepository {
    final static int MODE_IRC = 100;
    final static int MODE_STRICT = 101;
    
    final static int REGISTER_OK = 200;
    final static int REGISTER_NEED_PASS = 201;
    final static int REGISTER_NEED_NICK = 202;
    final static int REGISTER_NEED_HOSTNAME = 203;
    final static int REGISTER_NEED_USERNAME = 204;
    final static int REGISTER_NEED_REALNAME = 205;
    final static int REGISTER_INCORRECT_PASSWORD = 206;
    final static int REGISTER_BANNED = 207;
    final static int REGISTER_AS_OPERATOR_OK = 208;
    final static int REGISTER_AS_OPERATOR_HOST_NOT_ALLOWED = 209;
    final static int REGISTER_AS_OPERATOR_INCORRECT_PASSWORD = 210;
    final static int REGISTER_AS_ADMINISTRATOR_OK = 211;

    /**
     * Register a new connection with a client. Will only be called once for a
     * socket. It is also the first method used with a new socket.
     */
    public void connect(Socket socket, String hostname);

    /**
     * Will only be called for unregistered users.
     *
     * @return the hostname belonging to the socket or null if no unregistered
     *         user with this socket is found.
     */
    public String getHostname(Socket socket);
    
    /**
     * Will only be called for unregistered users.
     */
    public void setPass(Socket socket, String pass);

    /**
     * In strict mode this method will not be called after succesfull
     * registration.
     *
     * @return  false if nick already in use.
     */
    public boolean setNick(Socket socket, String nick);

    /**
     * Will only be called for for registered users.
     */
    public void setUsername(Socket socket, String username);
    
    /**
     * Will only be called for for registered users.
     */
    public void setRealname(Socket socket, String realname);

    /**
     * Will only be called for unregistered users.
     */
    public int register(Socket socket);

    /**
     * Checks if the user is allowed to have operator or adminitrator status.
     * An administrator has operator status and is also allowed to do some
     * additional actions.
     *
     * @return  REGISTER_AS_OPERATOR_OK if the user is allowed to have operator
     *          status. REGISTER_AS_ADMINISTRATOR_OK if the user is allowed to
     *          have administrator status. REGISTER_AS_OPERATOR_HOST_NOT_ALLOWED
     *          if the user is not allowed to have operator status when logged
     *          in from the host he is connecting from.
     *          REGISTER_AS_OPERATOR_INCORRECT_PASSWORD if the user is not
     *          allowed to have operator status.
     */
    public int registerAsOperator(User user, String username, String password);

    /**
     * Checks if the specified connection is registered.
     */
    public boolean isRegistered(Socket socket);
    
    /**
     * This method should only return registered users.
     */
    public User getUser(String nick);

    /**
     * This method should only return registered users.
     */
    public User getUser(Socket socket);

    /**
     * Returns the nick of a user who isn't registered yet. This method will
     * only be called for unregistered users.
     *
     * @return  the nick of a user who has set his nick but isn't registered
     *          yet or null of the user hasn't used the nick command yet.
     */
    public String getUnregisteredNick(Socket socket);

    /**
     * Removes a client connection from the list of known connections. This is
     * the last method called for a socket.
     */
    public void disconnect(Socket socket);
    
    /**
     * Returns the number of users connected to the server that have passed
     * the registation fase.
     */
    public int numberOfRegisteredUsers();

    /**
     * Returns the number of users connected to the server that have not passed
     * the registation fase yet.
     */
    public int numberOfUnregisteredUsers();
    
    /**
     * Returns the number of users that have registered as an operator.
     */
    public int numberOfOperators();

    public Date getLastCommandRecieved(Socket socket);

    /**
     * Removes the socket from the waiting for pong list.
     */
    public void setLastCommandRecieved(Socket socket, Date date);

    /** 
     * Removes the socket from the last command recieved list.
     */
    public void setLastPingSend(Socket socket, Date date);
    
    /**
     * Returns a socket that has a last active time smaller than the since
     * parameter. A socket from wich we expect a pong message is returned if
     * waitingForPong is true or a socket we don't expect a pong message from
     * is returned if waitingForPong is false.
     */
    public Socket getInactiveSocket(boolean waitingForPong, Date since);
    
    /**
     * Returns a set of all registered sockets.
     */
    public Set getRegisteredSockets();

    /**
     * Returns a set of all unregistered sockets.
     */
    public Set getUnregisteredSockets();

    /**
     * Determines if a nick has a valid syntax. This is especially important
     * if the server is configured to accept any nick without a user database
     * to check with. If the server is configured to validate against a user
     * database this method may always return true because in a later stadium,
     * when validating against a user database, it will reject the nick if it's
     * syntax is not valid.
     *
     * @return  true if nick is valid, false otherwise.
     */
    public boolean isValidNick(String nick);
    
}

