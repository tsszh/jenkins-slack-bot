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

/**
 * 
 * SlackBot class handles the interactions with Slack channel.
 * 
 * 1. Listen for Slack Channel
 * 2. Parse Message to retrieve commands
 * 3. Pass the commands to Jenkins bot package
 * 4. Jenkins bot is responsible for interaction with Jenkins
 * 
 * @author Zehao Song
 *
 */

public class SlackBot implements Runnable {
	/** Logger for SlackBot */
	private final static Logger logger = Logger.getLogger(SlackBot.class.getName());
	/**  Represents a slack server */
	private SlackSession session;
	/**  Global Unique Slack Bot Identification */
	private String slackToken;
	/**  Jenkins User Name - Access Control */
	private String jenkinsUserID;
	/**  Jenkins Bot - Handles the interaction with Jenkins */
	private Bot bot = null;

	/**
	 * 
	 * @param slackToken  Identifier provided by Slack Bot
	 * @param userID      Login Name For Jenkins
	 */
	public SlackBot(String slackToken,String userID) {
		this.slackToken = slackToken;
		this.jenkinsUserID = userID;
	}
	/**
	 * Empty SlackBot
	 */
	public SlackBot() {
		this.slackToken = "";
		this.jenkinsUserID = "";
	}
	
	/** 
	 * Slack Message Posted Handler
	 * 
	 */
	private SlackMessagePostedListener postedHangdler = new SlackMessagePostedListener() {
		public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
			if (!slackMessage.getSender().isBot()) {
				String msg = slackMessage.getMessageContent();
				if (msg.startsWith("!jenkins")) {
					JBotChat chat = new JBotChatImpl(slackMessage, session);
					// Create Jenkins Bot
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
					// The remaining part is handled by Jenkins bot package
					bot.onMessage(chat);
				}
			}
		}
	};
	
	/**
	 * Update the SlackBot and Establish the connection to slack bot
	 *  
	 * @param slackToken  Identifier provided by Slack Bot
	 * @param userID      Login Name For Jenkins
	 */
	void setSlackBot(String slackToken, String userID) {
		if ( !this.slackToken.equals(slackToken) ) {
			this.slackToken = slackToken;
			disconnectToSlack();
			connectToSlack(slackToken);
		}
		if ( !this.jenkinsUserID.equals(userID) ) {
			this.jenkinsUserID = userID;
			bot = null;
		}
		
	}
	
	/**
	 * Connect to Slack Bot identified by token string
	 * 
	 * @param token  Global Unique Identifier for Slack Bot
	 */
	void connectToSlack(String token) {
		this.session = SlackSessionFactory.createWebSocketSlackSession(token);
		this.session.addMessagePostedListener(postedHangdler);
		logger.info("Connecting to Slack Server ("+token+")...");;
		try {
			this.session.connect();
			logger.info("Connection to SLack: Success - "+token);
		} catch (IOException e) {
			logger.info("Connection to SLack: Failed  - "+token);
		}
	}
	
	/**
	 * Disconnect with Slack Bot
	 * 
	 */
	void disconnectToSlack() {
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

	/**
	 * Thread for server.
	 * 
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * 
 * Adapter for JBotChat interface in bot package.
 * 
 * JBotChat has two responsibility:
 * 1. Send the Reply Messages Back to Channel
 * 2. Provide All Information Required by bot package
 * 
 * @author Zehao Song
 *
 */
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

	@Override
	public void sendMessage(String message) throws JBotException {
		this.session.sendMessage(this.channel, message, null);

	}

	@Override
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
