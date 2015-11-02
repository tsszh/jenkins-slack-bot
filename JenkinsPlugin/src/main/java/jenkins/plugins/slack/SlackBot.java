package jenkins.plugins.slack;

import java.io.IOException;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

public class SlackBot implements Runnable {
	private SlackSession session;
	private String slackToken;

	// Slack Message Posted Handler
	private SlackMessagePostedListener postedHangdler = new SlackMessagePostedListener() {
		public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
			if (!slackMessage.getSender().isBot()) {
				String msg = slackMessage.getMessageContent();
				if (msg.startsWith("!jenkins")) {
					// Echoing Message Back To Slack Channel
					String res = "Hello" + slackMessage.getSender().getUserName() + " : " + msg.substring(9);
					session.sendMessage(slackMessage.getChannel(), res, null);
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
