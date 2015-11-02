package com.songzh.jenkins_slack;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;


public class BotDemo {
	private String url = "https://fa15-cs427-082.cs.illinois.edu:8083/";
	private JenkinsServer jenkins;
	private JenkinsHttpClient conn;
	private SlackSession session;
	
	
    // Slack Message Posted Handler
	private SlackMessagePostedListener postedHangdler = new SlackMessagePostedListener() {
		public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
			System.out.println();
			if ( !slackMessage.getSender().isBot() ) {
				String msg = slackMessage.getMessageContent();
				String[] args = msg.split("[\\s]+");
				if ( args.length == 2 && args[0].equals("!jenkins")) {
					try {
						String res = conn.get(args[1]+"?pretty=true");
						session.sendMessage(slackMessage.getChannel(),res, null);
					} catch (IOException e) {
						session.sendMessage(slackMessage.getChannel(),"Cannot connect to the ", null);
						e.printStackTrace();
					}
				} else {
					session.sendMessage(slackMessage.getChannel(),"Invalid Command!", null);
				}
			}
		}
	};
	
	public BotDemo( String slackToken, String jenkinsURL ){
		this(slackToken, jenkinsURL, null, null);
	}
	public BotDemo( String slackToken, String jenkinsURL, String userName, String password ) {
		try {
			if ( userName == null ) {
				this.conn = new JenkinsHttpClient(new URI(jenkinsURL) );
			} else {
				this.conn = new JenkinsHttpClient(new URI(jenkinsURL), userName, password );
			}
			this.jenkins = new JenkinsServer(conn);
		} catch (URISyntaxException e) {
			System.out.println("Cannot connect to "+jenkinsURL);
			e.printStackTrace();
			return;
		}
		try {
			ConnectToSlack(slackToken);
		} catch (IOException e) {
			System.out.println("Cannot connect to Slack!");
			e.printStackTrace();
		}
	}
	private void ConnectToSlack ( String token ) throws IOException {
		this.session = SlackSessionFactory.createWebSocketSlackSession(token);
		this.session.addMessagePostedListener(postedHangdler);
		this.session.connect();
	}
	
	public static void main(String[] args) {
		new BotDemo("xoxb-13203396052-6U3vsnm89DThgtCw4NYsYYpp",
				"https://fa15-cs427-032.cs.illinois.edu:8083/","zsong12", "w2jkscs427");
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
