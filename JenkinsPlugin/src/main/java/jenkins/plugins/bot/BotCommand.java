package jenkins.plugins.bot;

import java.util.Collection;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import jenkins.model.Jenkins;

public abstract class BotCommand implements ExtensionPoint {
    /**
     * Obtains the name of the command. Single commands can register multiple aliases,
     * so this method returns a collection.
     *
     * @return
     *      Can be empty but never null.
     */
    public abstract Collection<String> getCommandNames();
	
	/**
	 * Execute a command.
	 * 
	 * @param bot
     *      The bot for which this command runs. Never null.
     * @param chat the {@link JBotChat} object, may be used to send reply messages
     * @param message the original {@link JBotMessage}
     * @param sender the command JBotSender
     * @param args arguments passed to the command, where <code>args[0]</code> is the command name itself
     * @throws JBotException if anything goes wrong while communicating with the remote IM server
	 */
	public abstract void executeCommand(Bot bot, JBotChat chat, JBotMessage message,
			JBotSender sender,String[] args) throws JBotException;
	
	/**
	 * Return the command usage text.
	 * @return the command usage text
	 */
	public abstract String getHelp();

    /**
     * Returns all the registered {@link BotCommand}s.
     */
    public static ExtensionList<BotCommand> all() {
        return Jenkins.getInstance().getExtensionList(BotCommand.class);
    }    
}

