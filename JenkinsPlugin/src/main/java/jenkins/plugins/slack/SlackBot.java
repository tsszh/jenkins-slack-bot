package jenkins.plugins.slack;

import java.io.IOException;
import java.util.List;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import hudson.model.Hudson;
import hudson.model.View;
import jenkins.model.Jenkins;
import jenkins.plugins.bot.Bot;
import jenkins.plugins.bot.JBotChat;
import jenkins.plugins.bot.JBotCommand;
import jenkins.plugins.bot.JBotException;

public class SlackBot implements Runnable {
	private SlackSession session;
	private String slackToken;
	
	private Bot bot = null;
	
	// Slack Message Posted Handler
	private SlackMessagePostedListener postedHangdler = new SlackMessagePostedListener() {
		public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
			if (!slackMessage.getSender().isBot()) {
				String msg = slackMessage.getMessageContent();
				if (msg.startsWith("!jenkins")) {
					JBotChat chat = new JBotChatImpl(slackMessage,session);
					if ( bot == null ) {
						bot = new Bot();
					}
					bot.onMessage(chat);
				}
			}
		}
	};
	
	public SlackBot(String slackToken) {
		this.slackToken = slackToken;
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
}

class JBotChatImpl implements JBotChat {
	private SlackSession session;
	private SlackChannel channel;
	private String sender;
	private String msg;
	public JBotChatImpl( SlackMessagePosted slackMessage, SlackSession session ) {
		this.session = session;
		this.channel = slackMessage.getChannel();
		this.sender = slackMessage.getSender().getUserName();
		this.msg = slackMessage.getMessageContent().replace("!jenkins", "");
	}
	public void sendMessage(String message) throws JBotException {
//		System.out.println("Sending Message...");
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
