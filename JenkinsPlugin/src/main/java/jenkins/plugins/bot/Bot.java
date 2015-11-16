package jenkins.plugins.bot;

import hudson.Extension;
import hudson.security.ACL;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import jenkins.plugins.bot.SetAliasCommand.AliasCommand;
import jenkins.security.NotReallyRoleSensitiveCallable;


public class Bot {

	private static final Logger LOGGER = Logger.getLogger(Bot.class.getName());

    @Extension
	public static class HelpCommand extends BotCommand {
        @Override
        public Collection<String> getCommandNames() {
            return Collections.singleton("help");
        }

        public void executeCommand(Bot bot, JBotChat groupChat, JBotMessage message,
                                   JBotSender sender, String[] args) throws JBotException {
			if (bot.helpCache == null) {
				final StringBuilder msg = new StringBuilder(64);
				msg.append("Available commands:");
				for (final Entry<String, BotCommand> item : bot.cmdsAndAliases.entrySet()) {
					// skip myself
					if ((item.getValue() != this)
							&& (item.getValue().getHelp() != null)) {
						msg.append("\n");
						msg.append(item.getKey());
						msg.append(item.getValue().getHelp());
					}
				}
				bot.helpCache = msg.toString();
			}
			groupChat.sendMessage(bot.helpCache);
		}

		public String getHelp() {
			return null;
		}
	}

	private final SortedMap<String, BotCommand> cmdsAndAliases = new TreeMap<String, BotCommand>();

	private final JBotChat chat;
	private final String nick;
	private final String imServer;
	private final String commandPrefix;
	private boolean commandsAccepted;
	private String helpCache;

	private final AuthenticationHolder authentication;

	public Bot(JBotChat chat, String nick, String imServer,
			String commandPrefix, AuthenticationHolder authentication
			) {
		this.chat = chat;
		this.nick = nick;
		this.imServer = imServer;
		this.commandPrefix = commandPrefix;
		this.authentication = authentication;
        this.commandsAccepted = true;

        for (BotCommand cmd : BotCommand.all()) {
            for (String name : cmd.getCommandNames())
                this.cmdsAndAliases.put(name,cmd);
        }
	}
	public Bot(){
		this.chat = null;
		this.nick = "nick";
		this.imServer = "Server";
		this.commandPrefix = "!jenkins";
		this.authentication = null;
        this.commandsAccepted = true;
        this.cmdsAndAliases.put("test",new SnackCommand());
	}
    /**
     * Returns an identifier describing the Im account used to send the build command.
     *   E.g. the Jabber ID of the Bot.
     */
    public String getImId() {
        return this.nick + "@" + this.imServer;
    }

    public void onMessage( final JBotChat chat ) {
    	String payload = chat.getMsg();
    	final JBotMessage msg = new JBotMessage("From","To",payload);
    	if ( payload != null ) {
    		// split words
            final String[] args = MessageHelper.extractCommandLine(payload);
            final JBotSender s = new JBotSender(chat.getSender());
            if (args.length > 0) {
                // first word is the command name
                String cmd = args[0];
                try {
                    System.out.println(cmd);
                	final BotCommand command = this.cmdsAndAliases.get(cmd);
                    if (command != null) {
                    	command.executeCommand(Bot.this, chat, msg, s, args);
                    	if (isAuthenticationNeeded()) {
                    		ACL.impersonate(this.authentication.getAuthentication(), new NotReallyRoleSensitiveCallable<Void, JBotException>() {
								private static final long serialVersionUID = 1L;

								public Void call() throws JBotException {
									command.executeCommand(Bot.this, chat, msg, s, args);
									return null;
								}
							});
                    	} else {
                    		command.executeCommand(Bot.this, chat, msg, s, args);
                    	}
                    } else {
                        chat.sendMessage(s.getNickname() + " did you mean me? Unknown command " + cmd
                                + "'\nUse '" + this.commandPrefix + " help' to get help!");
                    }
                } catch (Exception e) {
                    LOGGER.warning(ExceptionHelper.dump(e));
                }
            }
    	}
    }
    
    public void onMessage(final JBotMessage msg) {
        // is it a command for me ? (returns null if not, the payload if so)
        String payload = retrieveMessagePayLoad(msg.getBody());
        if (payload != null) {
            final JBotSender s = getSender(msg);
        	
        	try {
            	if (!this.commandsAccepted) {
            	    this.chat.sendMessage(s.getNickname() + " you may not issue bot commands in this chat!");
            	    return;
            	} else if (!msg.isAuthorized()) {
    				this.chat.sendMessage(s.getNickname() + " you're not a buddy of me. I won't take any commands from you!");
    				return;
            	}
        	} catch (JBotException e) {
                LOGGER.warning(ExceptionHelper.dump(e));
                return;
            }
        	
            // split words
            final String[] args = MessageHelper.extractCommandLine(payload);
            if (args.length > 0) {
                // first word is the command name
                String cmd = args[0];
                try {
                    System.out.println(cmd);
                	final BotCommand command = this.cmdsAndAliases.get(cmd);
                    if (command != null) {
                    	command.executeCommand(Bot.this, chat, msg, s, args);
                    	if (isAuthenticationNeeded()) {
                    		ACL.impersonate(this.authentication.getAuthentication(), new NotReallyRoleSensitiveCallable<Void, JBotException>() {
								private static final long serialVersionUID = 1L;

								public Void call() throws JBotException {
									command.executeCommand(Bot.this, chat, msg, s, args);
									return null;
								}
							});
                    	} else {
                    		command.executeCommand(Bot.this, chat, msg, s, args);
                    	}
                    } else {
                        this.chat.sendMessage(s.getNickname() + " did you mean me? Unknown command " + cmd
                                + "'\nUse '" + this.commandPrefix + " help' to get help!");
                    }
                } catch (Exception e) {
                    LOGGER.warning(ExceptionHelper.dump(e));
                }
            }
        }
	}
    
	private boolean isAuthenticationNeeded() {
    	return this.authentication != null && Jenkins.getInstance().isUseSecurity();
    }

	private JBotSender getSender(JBotMessage msg) {
	    String sender = msg.getFrom();     
        final JBotSender s = new JBotSender(sender);
        return s;
    }

    private static boolean isNickSeparator(final String candidate) {
		return ":".equals(candidate) || ",".equals(candidate);
	}

	private String retrieveMessagePayLoad(final String body) {
		if (body == null) {
			return null;
		}

		if (body.startsWith(this.commandPrefix)) {
			return body.substring(this.commandPrefix.length()).trim();
		}

		if (body.startsWith(this.nick)
				&& isNickSeparator(body.substring(this.nick.length(), this.nick
						.length() + 1))) {
			return body.substring(this.nick.length() + 1).trim();
		}

		return null;
	}
	
	/**
	 * Returns the command or alias associated with the given name
	 * or <code>null</code>.
	 */
	BotCommand getCommand(String name) {
		return this.cmdsAndAliases.get(name);
	}
	
	/**
	 * Registers a new alias.
	 * 
	 * @return the alias previously registered under this name or <code>null</code>
	 * if no alias was registered by that name previously
	 * @throws IllegalArgumentException when trying to override a built-in command
	 */
	BotCommand addAlias(String name, BotCommand alias) {
		BotCommand old = this.cmdsAndAliases.get(name);
		if (old != null && ! (old instanceof AliasCommand)) {
			throw new IllegalArgumentException("Won't override built-in command: '" + name + "'!");
		}
		
		this.cmdsAndAliases.put(name, alias);
		this.helpCache = null;
		return old;
	}
	
	/**
	 * Removes an existing alias.
	 *
	 * @param name The name of the alias
	 * @return the removed alias or <code>null</code> if no alias by that name is registered
	 */
	AliasCommand removeAlias(String name) {
		BotCommand alias = this.cmdsAndAliases.get(name);
		if (alias instanceof AliasCommand) {
			this.cmdsAndAliases.remove(name);
			return (AliasCommand) alias;
		} else if (alias != null) {
			throw new IllegalArgumentException("Won't remove built-in command: '" + name + "'!");
		}
		return null;
	}
	
	/**
	 * Returns a map of all currently defined aliases.
	 * The map is sorted by the alias name.
	 */
	SortedMap<String, AliasCommand> getAliases() {
		SortedMap<String, AliasCommand> result = new TreeMap<String, AliasCommand>();
		for (Map.Entry<String, BotCommand> entry : this.cmdsAndAliases.entrySet()) {
			if (entry.getValue() instanceof AliasCommand) {
				result.put(entry.getKey(), (AliasCommand) entry.getValue());
			}
		}
		return result;
	}
}
