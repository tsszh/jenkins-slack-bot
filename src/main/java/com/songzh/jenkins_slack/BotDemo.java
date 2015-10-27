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
	String url = "https://fa15-cs427-082.cs.illinois.edu:8083/";
	static JenkinsServer jenkins;
	static JenkinsHttpClient conn;
	public static void main(String[] args) {
		
		try {
			conn = new JenkinsHttpClient(new URI("https://fa15-cs427-032.cs.illinois.edu:8083/"), "zsong12", "w2jkscs427");
			jenkins = new JenkinsServer(conn);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-13203396052-6U3vsnm89DThgtCw4NYsYYpp");
		session.addMessagePostedListener(new SlackMessagePostedListener() {
			public void onEvent(SlackMessagePosted slackMessage, SlackSession session) {
				if ( !slackMessage.getSender().isBot() ) {
					String msg = slackMessage.getMessageContent();
					String[] args = msg.split("[\\s]+");
					if ( args.length == 2 && args[0].equals("!jenkins")) {
						try {
							String res = conn.get(args[1]+"?pretty=true");
							session.sendMessage(slackMessage.getChannel(),res, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						session.sendMessage(slackMessage.getChannel(),"Invalid Command!", null);
					}
				}
			}
		});
		try {
			session.connect();
		} catch (IOException e) {
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
