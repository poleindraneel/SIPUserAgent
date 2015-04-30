package org.etechnik.mobileComputing.Group7;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class WebsocketMessageHandler {
/*
	private static WebsocketMessageHandler instance;
	//public static UA ua;
	//public static UAClient uaClient;
	
	public void WebsocketMessageHandler()
	{
		instance = this;
	}
	
	public static WebsocketMessageHandler getInstance()
	{
		return instance;
	}*/
	
	public void analyze(String jsonMessage) {
		//uaClient =  uaClient.getInstance();
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonMessage);
		
			sortMessage(jsonObject);
			
		} 
		
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sortMessage(JSONObject jsonObject)
	{
		//TODO.. if else condition to processMessages
		if(jsonObject.containsKey("call"))
		{
			if(jsonObject.containsValue("accepted"))
			{
				System.out.println(jsonObject.get("status"));
				
			}
			else if(jsonObject.containsValue("end"))
			{
				//String toIP = (String)jsonObject.get("Domain");
				//String toUser = (String)jsonObject.get("uri");
				//System.out.println(jsonObject.get("status"));
				UAClient.getInstance().sendBYEtoCurrentCall();
			}
			else if(jsonObject.containsValue("dialed"))
			{
				String toUser = (String)jsonObject.get("uri");
				String toIP = (String)jsonObject.get("Domain");
				UAClient.getInstance().sendINVITE(toUser, toIP);
			}
			else if(jsonObject.containsValue("declined"))
			{
				//uaClient.sendCancel();
			}
		}
		else if(jsonObject.containsKey("message"))
		{
			if(jsonObject.containsValue("sent"))
			{
				System.out.println(jsonObject.get("body"));
				String body = (String)jsonObject.get("body");
				String toUser = (String)jsonObject.get("uri");
				String toIP = (String)jsonObject.get("Domain");
				UAClient.getInstance().sendMESSAGE(body, toUser, toIP, null);
			}
		}
		else if(jsonObject.containsKey("register"))
		{
			if(jsonObject.containsValue("start"))
			{
				System.out.println("Registering on realm" + jsonObject.get("realm"));
				String realm = (String) jsonObject.get("realm");
				UserProperties.realm = realm;
				new Register().startRegister();
				//TODO call register method here
			}
			if(jsonObject.containsValue("stop"))
			{
				System.out.println("DeRegistering on realm" + jsonObject.get("realm"));
				String realm = (String) jsonObject.get("realm");
				//TODO call deregister method here
				Register.getInstance().deRegister();
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void sendMessage(HashMap<String, String> map){
	
		JSONObject json = new JSONObject();
		json.putAll(map);
		System.out.println("this will be sent:   " + json.toString());
		WebsocketHandler.getInstance().sendMessage(json.toString());
		
	}

}






























/*               GREGORS VERSION
public class WebsocketMessageHandler {

	public void analyze(String jsonMessage) {
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonMessage);
			
			if(jsonObject.containsKey("Test")){
				System.out.println(jsonObject.get("Test"));
				System.out.println(jsonObject.get("anotherTest"));
				// Do something with the keyvalue here - call method and so on
				
				
				
				
				// Simply sending a message to the client (JavaScript/HTML-Page)
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("name", "gregor");
				map.put("reason", "this is awesome");
				sendMessage(map);
				
				
			} else System.out.println("Error with incoming JSON String");
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void sendMessage(HashMap<String, String> map){
	
		JSONObject json = new JSONObject();
		json.putAll(map);
		System.out.println("this will be send:   " + json.toString());
		WebsocketHandler.getInstance().sendMessage(json.toString());
		
	}

}
*/