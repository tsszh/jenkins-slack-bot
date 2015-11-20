package jenkins.plugins.slack;

import java.io.IOException;
import java.util.logging.Logger;

import org.acegisecurity.Authentication;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import hudson.model.User;
import jenkins.plugins.bot.Bot;
import jenkins.plugins.bot.JBotChat;
import jenkins.plugins.bot.JBotException;

public class SlackBot implements Runnable {
	private final static Logger logger = Logger.getLogger(SlackBot.class.getName());
	
	private SlackSession session;
	private String slackToken;
	private String jenkinsUserID;

	private Bot bot = null;

	// Slack Message Posted Handler
	private SlackMessagePostedListener postedHangdler = new SlackMessagePostedListener() {
		public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
			if (!slackMessage.getSender().isBot()) {
				String msg = slackMessage.getMessageContent();
				if (msg.startsWith("!jenkins")) {
					JBotChat chat = new JBotChatImpl(slackMessage, session);
					if (bot == null) {
						if ( jenkinsUserID.equals("") ) {
							bot = new Bot();
						} else {
							User u = User.get(jenkinsUserID);
							Authentication authentication = u.impersonate();
							bot = new Bot(authentication);
						}
						logger.info("Slack Bot is now logged in as "+jenkinsUserID);
					}
					bot.onMessage(chat);
				}
			}
		}
	};

	public SlackBot(String slackToken,String userID) {
		this.slackToken = slackToken;
		this.jenkinsUserID = userID;
	}
	
	public SlackBot() {
		this.slackToken = "";
		this.jenkinsUserID = "";
	}
	
	public void setSlackBot(String slackToken, String userID) {
		if ( !this.slackToken.equals(slackToken) ) {
			this.slackToken = slackToken;
			DisconnectToSlack();
			ConnectToSlack(slackToken);
		}
		if ( !this.jenkinsUserID.equals(userID) ) {
			this.jenkinsUserID = userID;
			bot = null;
		}
		
	}
	
	private void ConnectToSlack(String token) {
		this.session = SlackSessionFactory.createWebSocketSlackSession(token);
		this.session.addMessagePostedListener(postedHangdler);
		try {
			this.session.connect();
			logger.info("Connection to SLack: Success - "+token);
		} catch (IOException e) {
			logger.info("Connection to SLack: Failed  - "+token);
		}
	}
	
	private void DisconnectToSlack() {
		if ( session != null ) {
			try {
				session.disconnect();
				logger.info("Disconnection to Slack : Success");
			} catch (IOException e) {
				logger.info("Disconnection to Slack : Failed");
			} finally {
				session = null;
			}
		}
	}

	public void run() {
		if ( !this.slackToken.equals("") ) {
			ConnectToSlack(this.slackToken);
		}
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class JBotChatImpl implements JBotChat {
	private SlackSession session;
	private SlackChannel channel;
	private String sender;
	private String msg;

	public JBotChatImpl(SlackMessagePosted slackMessage, SlackSession session) {
		this.session = session;
		this.channel = slackMessage.getChannel();
		this.sender = slackMessage.getSender().getUserName();
		this.msg = slackMessage.getMessageContent().replace("!jenkins", "");
	}

	public void sendMessage(String message) throws JBotException {
		// logger.info("Sending Message...");
		this.session.sendMessage(this.channel, message, null);

	}

	public boolean isCommandsAccepted() {
		return true;
	}

	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public String getMsg() {
		return msg;
	}
}
