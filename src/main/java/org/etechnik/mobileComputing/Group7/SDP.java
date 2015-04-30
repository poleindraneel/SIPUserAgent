package org.etechnik.mobileComputing.Group7;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.sdp.Connection;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.sdp.Time;
import javax.sdp.Version;




public class SDP {

	private ArrayList<String> videoCodecOfSystem = new ArrayList<String>(Arrays.asList( 
													"102", "rtpmap", "102 H264/90000")); //,		// H264-MediaDescription
													//"98", "rtpmap", "98 H263-1998/90000",	// H263-1998-MediaDescription
													//"34", " ", " "	));						// H263 (MediaDescription not needed - Wellknown)
	
	private ArrayList<String> audioCodecOfSystem = new ArrayList<String>(Arrays.asList(
													"0" , " ", " ", 						// PCMU Media Description (not needed - Wellknown)
													"8", " ", " ",							// PCMA Media Description (not needed - Wellknown)
													"3", " ", " ",
													"107", "rtpmap", "99 OPUS/48000/2"));		// OPUS Media Description (support needs to be testet)
	
	
	private ArrayList<String> videoCodecPrio 		= new ArrayList<String>(Arrays.asList( "102"));//,"34", "98" ));		
	private ArrayList<String> videoCodecWellKnown	= new ArrayList<String>(Arrays.asList( " " ));
	private ArrayList<String> audioCodecPrio 		= new ArrayList<String>(Arrays.asList( "0", "8", "3", "107" ));		
	private ArrayList<String> audioCodecWellKnown 	= new ArrayList<String>(Arrays.asList( "0", "8", "3" ));
	
	
	// Variables of Caller/Called - needed for GStreamer-Pipelines
	private String callerMediaIP = "";
	private String callerAudioPort = "";
	private String callerAudioCodec = "";
	private String callerVideoPort = "";
	private String callerVideoCodec = "";
	
	private SdpFactory mySdpFactory;
	
	public SDP(){
		this.mySdpFactory = SdpFactory.getInstance();	
	}
	
	public SessionDescription createSDPAnswer(String systemName, int systemAudioPort, int systemVideoPort, String systemIP, SessionDescription callerSessionDescription) {
		
		try {
			
			Vector<MediaDescription> callerMediaDescription = callerSessionDescription.getMediaDescriptions(false);
			
			// Read-out IP of Caller
			this.callerMediaIP = String.valueOf(callerSessionDescription.getConnection().getAddress());
			
			// Getting SessionDescription with mandatory SDP-Lines
			SessionDescription mySdp = createSDPLines(systemName, systemIP, callerSessionDescription, false);
			
			Vector<Serializable> myMediaDescriptionVector = new Vector<Serializable>();
			
			for(MediaDescription m : callerMediaDescription){
				CodecNegotiation codecNegotiation = new CodecNegotiation();
				
				ArrayList<String> codecWellKnown = null, codecOfSystem = null, codecPrio = null, codecs = null;
				String media = null;
				int port = 0;
				
				// Setting parameters for processing
				if(m.getMedia().getMediaType().equals("audio")){
					this.callerAudioPort = String.valueOf(m.getMedia().getMediaPort());
					media = "audio";
					codecWellKnown = audioCodecWellKnown;
					codecOfSystem = audioCodecOfSystem;
					codecPrio = audioCodecPrio;
					port = systemAudioPort;
				} else if(m.getMedia().getMediaType().equals("video")){
					this.callerVideoPort = String.valueOf(m.getMedia().getMediaPort());
					media = "video";
					codecWellKnown = videoCodecWellKnown;
					codecOfSystem = videoCodecOfSystem;
					codecPrio = videoCodecPrio;
					port = systemVideoPort;
					
				}
				
				codecs = codecNegotiation.codecNegotiation(m.getMedia().toString(), m.getAttributes(false).toString(), codecWellKnown, codecOfSystem, codecPrio);
				System.out.println("codecs nach der analyse:   " + codecs);
				
				
				myMediaDescriptionVector.add(mySdpFactory.createMediaDescription(media, port, 1, "RTP/AVP", (String[]) codecs.toArray(new String[codecs.size()])));
				for(String s : codecs){
					if(!((codecWellKnown).contains(s))){
						int x = codecOfSystem.indexOf(s);
						myMediaDescriptionVector.add(mySdpFactory.createAttribute(codecOfSystem.get(x+1), codecOfSystem.get(x+2))); 
					}
				}
				
				if(media.equals("audio")) this.callerAudioCodec = codecs.get(0);
				else if(media.equals("video")) this.callerVideoCodec = codecs.get(0);
				
				
				
			}			
				System.out.println("media description created");
				mySdp.setMediaDescriptions(myMediaDescriptionVector);
				return mySdp;	
			
		} catch (SdpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	// Creating SDP Offer
	public SessionDescription createSDPOffer(String systemName, String systemIP, int systemAudioPort, int systemVideoPort){
		SessionDescription mySdp = createSDPLines(systemName, systemIP, null, true);
		Vector<Serializable> myMediaDescriptionVector = new Vector<Serializable>();
		
		// Adding AUDIO-Codec
		MediaDescription myAudioDescription = mySdpFactory.createMediaDescription("audio", systemAudioPort, 1, "RTP/AVP", (String[]) audioCodecPrio.toArray(new String[audioCodecPrio.size()]));
		myMediaDescriptionVector.add(myAudioDescription);
		
		// Adding Audio-Codec-Descriptions based on current priority
		for(String s : audioCodecPrio){
			int index = audioCodecOfSystem.indexOf(s);
			if(!(audioCodecOfSystem.get(index+1).equals(" "))) {
				myMediaDescriptionVector.add(mySdpFactory.createAttribute(audioCodecOfSystem.get(index+1), audioCodecOfSystem.get(index+2)));
			}
		}
				
		// Adding Video-Codec-Descriptions based on current priority
		MediaDescription myVideoDescription = mySdpFactory.createMediaDescription("video", systemVideoPort, 1, "RTP/AVP", (String[]) videoCodecPrio.toArray(new String[videoCodecPrio.size()]));
		myMediaDescriptionVector.add(myVideoDescription);
		for(String s : videoCodecPrio){
			int index = videoCodecOfSystem.indexOf(s);
			if(!(videoCodecOfSystem.get(index+1).equals(" "))){
				myMediaDescriptionVector.add(mySdpFactory.createAttribute(videoCodecOfSystem.get(index+1), videoCodecOfSystem.get(index+2)));
			}
		}
		
		try {
			mySdp.setMediaDescriptions(myMediaDescriptionVector);
			return mySdp;
		} catch (SdpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void inspectSDPAnswer(SessionDescription callerSessionDescription){
		try {
			Vector<MediaDescription> mediaDescriptionUser = callerSessionDescription.getMediaDescriptions(false);
			this.callerMediaIP = String.valueOf(callerSessionDescription.getConnection().getAddress());
			
			
			
			// Reading out needed Content from MediaDescriptions
			for(MediaDescription m : mediaDescriptionUser){
				CodecNegotiation codecNegotiation = new CodecNegotiation();
				
				ArrayList<String> codecWellKnown = null, codecOfSystem = null, codecPrio = null, codecs = null;
				String media = "";

				boolean video = false;
				// Setting parameters for processing
				if(m.getMedia().getMediaType().equals("audio")){
					this.callerAudioPort = String.valueOf(m.getMedia().getMediaPort());
					media = "audio";
					codecWellKnown = audioCodecWellKnown;
					codecOfSystem = audioCodecOfSystem;
					codecPrio = audioCodecPrio;
					codecs = codecNegotiation.codecNegotiation(m.getMedia().toString(), m.getAttributes(false).toString(), codecWellKnown, codecOfSystem, codecPrio);
					System.out.println("Finished CodecNegotiation for:   " + m.getMedia().getMediaType());
				} else if(m.getMedia().getMediaType().equals("video") && !(String.valueOf(m.getMedia().getMediaPort()).equals("0"))){
					video = true;
					System.out.println("VIDEO!!!!!!");
					this.callerVideoPort = String.valueOf(m.getMedia().getMediaPort());
					media = "video";
					codecWellKnown = videoCodecWellKnown;
					codecOfSystem = videoCodecOfSystem;
					codecPrio = videoCodecPrio;
					codecs = codecNegotiation.codecNegotiation(m.getMedia().toString(), m.getAttributes(false).toString(), codecWellKnown, codecOfSystem, codecPrio);
					System.out.println("Finished CodecNegotiation for:   " + m.getMedia().getMediaType());
				}
				

				
				if(media.equals("audio") && !(codecs.isEmpty())){
					this.callerAudioCodec = codecs.get(0);
					System.out.println("INSPECT AUDIO:    "    + this.callerAudioCodec);
				} else if(media.equals("video") && codecs.size() > 0 && video){
					this.callerVideoCodec = codecs.get(0);
					System.out.println("INSPECT VIDEO:    "    + this.callerVideoCodec);
				}
				
				System.out.println("Inspect finished");
			}
			
			
			
		
		} catch (SdpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// Creating the mandatory SDP-Lines (needed in Offer & Answer)
	private SessionDescription createSDPLines(String systemName, String systemIP, SessionDescription callerSessionDescription, boolean offer){	
		try {
			// ******************* CREATING SDP-LINES ***************** 
			// SDP-Version-Line(v):
			Version myVersion = mySdpFactory.createVersion(0);
			
			// Origin-Line (o)
			long ss = SdpFactory.getNtpTime(new Date());
			Origin myOrigin = mySdpFactory.createOrigin(systemName, ss, ss, "IN", "IP4", systemIP);
			
			// SessionName-Line (s)
			SessionName mySessionName;
			if(!offer) mySessionName = callerSessionDescription.getSessionName(); // Session Name already exists
			else mySessionName = mySdpFactory.createSessionName("Session with Group7"); // Session Name needs to be created
			
			// Connection-Line (c)
			Connection myConnection = mySdpFactory.createConnection("IN", "IP4", systemIP);
					
			// Time-Description-Line (o)
			Time myTime = mySdpFactory.createTime();
			Vector<Time> myTimeVector = new Vector<Time>();
			myTimeVector.add(myTime);			
			
			
			// Adding everything to SessionDescription
			SessionDescription mySdp = mySdpFactory.createSessionDescription();
			mySdp.setVersion(myVersion);
			mySdp.setOrigin(myOrigin);
			mySdp.setSessionName(mySessionName);
			mySdp.setConnection(myConnection);
			mySdp.setTimeDescriptions(myTimeVector);
			
			return mySdp;
			
		} catch (SdpParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SdpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	
	public HashMap<String,String> getCallInformations(){
		if(callerAudioCodec.equals("0")) this.callerAudioCodec = "PCMU";
		else if(callerAudioCodec.equals("8")) this.callerAudioCodec = "PCMA";
		else if(callerAudioCodec.equals("3")) this.callerAudioCodec = "GSM";
		else if(callerAudioCodec.equals("107")) this.callerAudioCodec = "OPUS";
		
		if(callerVideoCodec.equals("34")) this.callerVideoCodec = "H263";
		else if(callerVideoCodec.equals("98")) this.callerVideoCodec = "H263-1998";
		else if(callerVideoCodec.equals("102")) this.callerVideoCodec = "H264";
		
		HashMap<String,String> callInformations = new HashMap<String,String>();
		callInformations.put("callerIP", this.callerMediaIP);
		callInformations.put("callerAudioPort", this.callerAudioPort);
		callInformations.put("callerVideoPort", this.callerVideoPort);
		callInformations.put("callerAudioCodec", this.callerAudioCodec);
		callInformations.put("callerVideoCodec", this.callerVideoCodec);
		
		System.out.println("CallerInformations:    " + callInformations);
		
		return callInformations;
	}

}

