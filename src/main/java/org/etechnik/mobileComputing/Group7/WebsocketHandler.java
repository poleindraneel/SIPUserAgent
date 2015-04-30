package org.etechnik.mobileComputing.Group7;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket
public class WebsocketHandler {

	
	private static WebsocketHandler instance = null;
	private Session session = null;
	
	// Constructor - Saving for later use?!?
	public WebsocketHandler()
	{
		instance = this; 
	}
	
	// Getting instance for later use?!?
	public static WebsocketHandler getInstance(){
		return instance;
	}
	
	// User leaving/disconnecting Homepage
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        session = null;
    }

    
    // Error
    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    
    
    
    // User connecting to IP/PORT
    @OnWebSocketConnect
    public void onConnect(Session session) {
    	// Saves actuall Session
    	this.session = session;
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {
            session.getRemote().sendString("Hello Webbrowser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        new WebsocketMessageHandler().analyze(message);
        
    }
    
    
    
    public void sendMessage(String x){
    	if(this.session != null){	
    	   	try {
    	      		this.session.getRemote().sendString(x);
    	   	} catch (IOException e) {
    	   		e.printStackTrace();
    	   	}
    	}
    }
    
}