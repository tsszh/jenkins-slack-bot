package jenkins.plugins.bot;

public class JBotSender {
    
    private final String nickname;
    private String id;

    public JBotSender(String nickname) {
        this.nickname = nickname;
    }
    
    public JBotSender(String nickname, String id) {
        this.nickname = nickname;
        this.id = id;
    }

    /**
     * The nickname of the sender.
     * This string is not necessarily unique - i.e. the nick in a chatroom. 
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the unique id of the sender.
     */
    public String getId() {
        return id;
    }
    
    public String toString() {
        return this.nickname;
    }
}