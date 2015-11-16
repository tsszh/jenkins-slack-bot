package jenkins.plugins.slack;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import hudson.security.ACL;
import jenkins.plugins.bot.Bot;
import jenkins.plugins.bot.BotCommand;
import jenkins.plugins.bot.JBotChat;
import jenkins.plugins.bot.JBotException;
import jenkins.security.NotReallyRoleSensitiveCallable;

public class SlackBot implements Runnable {
	private SlackSession session;
	private String slackToken;
	
	private final SortedMap<String, BotCommand> cmdsAndAliases = new TreeMap<String, BotCommand>();
	
	// Slack Message Posted Handler
	private SlackMessagePostedListener postedHangdler = new SlackMessagePostedListener() {
		public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
			if (!slackMessage.getSender().isBot()) {
				String msg = slackMessage.getMessageContent();
				if (msg.startsWith("!jenkins")) {
					// Echoing Message Back To Slack Channel
					String res = "Hello" + slackMessage.getSender().getUserName() + " : " + msg.substring(9);
					session.sendMessage(slackMessage.getChannel(), res, null);
					
					String[] temp = msg.split("[\\s]+");
					String[] args = Arrays.copyOfRange(temp,1,temp.length);
					
					final BotCommand command = SlackBot.this.cmdsAndAliases.get(args[0]);
					
					if (command != null) {
                    	command.executeCommand(Bot.this, chat, msg, s, args);
                    } else {
                        this.chat.sendMessage(s.getNickname() + " did you mean me? Unknown command " + cmd
                                + "'\nUse '" + this.commandPrefix + " help' to get help!");
                    }
				}
			}
		}
	};
	
	public SlackBot(String slackToken) {
		this.slackToken = slackToken;
		
		for (BotCommand cmd : BotCommand.all()) {
            for (String name : cmd.getCommandNames())
                this.cmdsAndAliases.put(name,cmd);
        }
	}

	private void ConnectToSlack(String token) throws IOException {
		this.session = SlackSessionFactory.createWebSocketSlackSession(token);
		this.session.addMessagePostedListener(postedHangdler);
		this.session.connect();
		System.out.println("Connection to SLack: Success");

	}

	public static void main(String[] args) {
		SlackBot bot = new SlackBot("xoxb-13203396052-6U3vsnm89DThgtCw4NYsYYpp");
		(new Thread(bot)).start();
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			ConnectToSlack(this.slackToken);
		} catch (IOException e) {
			System.out.println("Cannot connect to Slack!");
			e.printStackTrace();
		}
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class JBotChatImpl implements JBotChat {
		private SlackSession session;
		private SlackChannel channel;
		public JBotChatImpl( SlackSession session ) {
			this( session, null );
		}
		public JBotChatImpl( SlackSession session, SlackChannel channel ) {
			this.session = session;
			this.channel= channel;
		}
		public void sendMessage(String message) throws JBotException {
			// TODO Auto-generated method stub
			this.session.sendMessage(this.channel, message, null);
			
		}
		public boolean isCommandsAccepted() {
			// TODO Auto-generated method stub
			return true;
		}
	}
}
