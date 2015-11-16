package jenkins.plugins.bot;

public interface JBotChat {
    public void sendMessage(String message) throws JBotException;
    public boolean isCommandsAccepted();
}
