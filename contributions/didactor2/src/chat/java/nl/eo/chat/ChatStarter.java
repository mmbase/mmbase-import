package nl.eo.chat;
/**
 * Small wrapper that starts the (protected by interface) Chatserver
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ChatStarter extends Thread {
    String[] args;

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void run() {
        nl.eo.chat.Server.main(args);
    }
}
