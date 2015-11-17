/*
 * Created on Apr 22, 2007
 */
package jenkins.plugins.bot;

import java.util.Arrays;
import java.util.Collection;

import hudson.Extension;
import hudson.model.Queue;
import hudson.model.Queue.Item;
import jenkins.model.Jenkins;

/**
 * Queue command for the jabber bot.
 * @author Pascal Bleser
 */
@Extension
public class QueueCommand extends JBotCommand {
	
	private static final String HELP = " - show the state of the build queue";

    @Override
    public Collection<String> getCommandNames() {
        return Arrays.asList("queue","q");
    }

    public void executeCommand(Bot bot, JBotChat chat, JBotMessage message,
                               JBotSender sender, String[] args) throws JBotException {
		Queue queue = Jenkins.getInstance().getQueue();
		Item[] items = queue.getItems();
		String reply;
		if (items.length > 0) {
			StringBuffer msg = new StringBuffer();
			msg.append("Build queue:");
			for (Item item : queue.getItems()) {
				msg.append("\n- ")
				.append(item.task.getFullDisplayName())
				.append(": ").append(item.getWhy());
			}
			reply = msg.toString();
		} else {
			reply = "build queue is empty";
		}
		
		chat.sendMessage(reply);
	}

	public String getHelp() {
		return HELP;
	}

}
