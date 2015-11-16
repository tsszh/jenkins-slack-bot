package jenkins.plugins.bot;

public interface JBotChat {
	public String getSender();
	public String getMsg();
    public void sendMessage(String message) throws JBotException;
    public boolean isCommandsAccepted();
}
