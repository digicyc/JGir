package org.antitech.jgir.plugins;

import org.antitech.jgir.*;
import org.antitech.jgir.plugins.events.*;

import org.jibble.pircbot.DccChat;

public class DccAdmin implements PluginInterface, PluginEventListener {

    private MyBot myBot = null;
    private Config conf = new Config();

    public boolean init() {
        myBot = MyBot.getInstance();
        PluginChain.addListener(this);
        return true;
    }


    public void exeCmd(DccChat chat) {
        try {
            // Accept all chat, whoever it's from.
            chat.accept();

            // Authenticate The DCC user.
            chat.sendLine("Username:");
            String username = chat.readLine();

            chat.sendLine("Password:");
            String password = chat.readLine();

            if (!loginFlag) {
                if(getAuthentication(username, password)) {
                    chat.sendLine("Authentication PASSED!");
                    chat.sendLine("GoodDay " + username);
                    String conv = "";
            
                    // Carry out a DCC chat Until told to close.
                    while(!conv.startsWith("=close")) {
                        conv = chat.readLine();

                        if(conv.startsWith("=hello")) {
                            chat.sendLine("Hello there");
                        }
                        else if(conv.startsWith("=hostname")) {
                            chat.sendLine("hostname: " + HOSTNAME);
                        }
                        else if(conv.startsWith("=close") ||
                                conv.startsWith("=exit")) {
                            chat.sendLine("GoodBye!");
                            chat.close();
                        }
                        else if(conv.startsWith("=quit")) {
                            chat.sendLine("Are you sure?");
                            conv = chat.readLine();

                            if(conv.startsWith("yes")) {
                                myBot.quitServer();
                                System.exit(0);
                            }
                        }
                        else if(conv.startsWith("=version")) {
                            String vers = getVersion();
                            chat.sendLine("Version: " + vers);
                        }
                        else if(conv.startsWith("=join")) {
                            myBot.joinChannel(conv);
                        }
                        else if(conv.startsWith("=part")) {
                            myBot.partChannel(conv);
                        }
                        else if(conv.startsWith("=say")) {
                            int pound = conv.indexOf("#");
                            String chan = conv.substring(pound, conv.length());
                            String msg = conv.substring(5, pound - 1);
    
                            myBot.sendMessage(chan, msg);
                        }
                    }
                }
                else {
                    chat.sendLine("Invalid Username/password");
                    chat.sendLine("Goodbye.");
                    chat.close();
                }   
            }
        } catch(IOException ioe) {}
    }


    public void handleEvent( PluginEvent event ) {
       if (event instanceof DccRequestEvent) {
            DccReuqestEvent dcc = (DccRequestEvent)event;
            DccChat chat = dcc.getChat();
            exeCmd(chat);
        } 
    }

    public boolean unload() {
        PluginChain.removeListener(this);
        return true;
    }

    /** Authenticate DCC user.  */
    public static boolean getAuthentication(String authName, String pass) {
        if (authName.startsWith(conf.readConfig("userName")) && 
                pass.startsWith(conf.readConfig("userPass"))) 
            return true;
        else 
            return false;
    }
}
