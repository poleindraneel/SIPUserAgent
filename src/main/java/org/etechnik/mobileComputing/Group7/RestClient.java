package org.etechnik.mobileComputing.Group7;

import java.util.HashMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	// Post to GStreamer for starting stream
	public static void startGStreamer(HashMap<String,String> map){
		System.out.println("HIER!!!!");
		String url = "http://localhost:8080/moco/gstreamer/startStreams/";
		url += map.get("callerIP")+"/";
		url += map.get("callerAudioPort")+"/";
		url += map.get("callerAudioCodec")+"/";
		if(!map.get("callerVideoPort").isEmpty()){
			url += map.get("callerVideoPort")+"/";
			url += map.get("callerVideoCodec");
		}
		try {
			 System.out.println(url);
			Client client = Client.create();
	 
			WebResource webResource = client.resource(url);

			
			//type("application/json")
			ClientResponse response = webResource.get(ClientResponse.class);
	 
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}
	 
			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);
	 
		  } catch (Exception e) {
	 
			e.printStackTrace();
	 
		  }
	 
		}
		
	
	public static void stopGStreamer(){
		
		try {
			 
			Client client = Client.create();
	 
			WebResource webResource = client
			   .resource("http://localhost:8080/moco/gstreamer/stopStreams");
			
			ClientResponse response = webResource.get(ClientResponse.class);
	 
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}
	 
			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);
	 
		  } catch (Exception e) {
	 
			e.printStackTrace();
	 
		  }
	 
		
		
		
		
	}
	
	
	}
	

