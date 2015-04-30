package org.etechnik.mobileComputing.Group7;

import java.io.Console;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.*;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.address.Address;

public class UAServer {

	public UA ua = null;
	
	public UAServer(UA ua){
		this.ua = ua;
	}
	
	
	public void processInvite(RequestEvent requestEvent,
			ServerTransaction serverTransactionID)  {
		
		SipProvider sipProvider = (SipProvider)requestEvent.getSource();
		Request request = requestEvent.getRequest();
		


		try {
			// add unavailable response here later - when its finished
			Response resTrying = ua.messageFactory.createResponse(Response.TRYING, request);
	        Response resRinging = ua.messageFactory.createResponse(Response.RINGING, request);
	        if (serverTransactionID == null) {
	            serverTransactionID = sipProvider.getNewServerTransaction(request);
	        }

	        serverTransactionID.sendResponse(resTrying);
			serverTransactionID.sendResponse(resRinging);
			
			Response resOk = ua.messageFactory.createResponse(Response.OK, request);
			ContentTypeHeader contentType = ua.headerFactory.createContentTypeHeader("application", "sdp");

			resOk.addHeader(ua.createContactHeader());
			
			// Read out SDP from INVITE-Request
			byte[] callerSDPContent = request.getRawContent();
			String callerSDP = new String(callerSDPContent);
			SdpFactory callerSDPFactory = SdpFactory.getInstance();
			SessionDescription callerSessionDescription = callerSDPFactory.createSessionDescription(callerSDP);
			
			SDP sdp = new SDP();
			resOk.setContent(sdp.createSDPAnswer(ua.fromName, ua.audioPort, ua.videoPort, ua.ipAddress, callerSessionDescription), contentType);
			serverTransactionID.sendResponse(resOk);
			
			RestClient.startGStreamer(sdp.getCallInformations());
					
			Dialog dialog = serverTransactionID.getDialog();
			dialog.setApplicationData(serverTransactionID);
			
			ua.callDialog = dialog;

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SdpParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public void processAck(RequestEvent requestEvent,
			ServerTransaction serverTransactionID) {
				System.out.println("Request ACK recieved - maybe do something with the DIALOG of the request? dont know");
				ua.callServerTransaction = serverTransactionID;
				ua.callProvider = (SipProvider) requestEvent.getSource();
				ua.callDialog = serverTransactionID.getDialog();
		
		//RestClient.startGStreamer(map);
	}

	public void processBye(RequestEvent requestEvent,
			ServerTransaction serverTransactionID) {

			Request request = requestEvent.getRequest();
		
		try
		{
			Response okResponse = ua.messageFactory.createResponse(200, request);
			serverTransactionID.sendResponse(okResponse);
			RestClient.stopGStreamer();
			ua.callDialog = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		// Stop GSTREAMER here and clear callDialog (=null)
	}
	
	public void processCancel(RequestEvent requestEvent,
			ServerTransaction serverTransactionID) {
		
		Request request = requestEvent.getRequest();
		
		try
		{
			//Create and send 200 Ok response.
			Response okResponse = ua.messageFactory.createResponse(200, request);
			serverTransactionID.sendResponse(okResponse);
			// break gstreamer here and clear callDialog if it is (=null)
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	// Reading out msg from SIP-MESSAGE-Request
	public void processMessage(RequestEvent requestEvent,
			ServerTransaction serverTransactionID) {
		
		SipProvider sipProvider = (SipProvider)requestEvent.getSource();
		Request request = requestEvent.getRequest();
		try{
			String msg = new String(request.getRawContent());
			String from = getFrom(request);
			System.out.println("Received Message:   " + msg + "   from:   " + from);

			Response okResponse = ua.messageFactory.createResponse(200, request);
					
			serverTransactionID = sipProvider.getNewServerTransaction(request);
	        serverTransactionID.sendResponse(okResponse);
	        
	        
	        HashMap<String,String> map = new HashMap<String,String>();
	        map.put("type", "Message");
	        map.put("msg", msg);
	        map.put("from", from);
	        new WebsocketMessageHandler().sendMessage(map);
	        
	        
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
		
	// Read out FROM-Header from each 
	public String getFrom(Request request){
		FromHeader from = (FromHeader) request.getHeader("From");
		return from.getAddress().toString();
	}


	public void processInfo(RequestEvent requestEvent,
			ServerTransaction serverTransactionID) {
		
		SipProvider sipProvider = (SipProvider) requestEvent.getSource();
		Request request = requestEvent.getRequest();

		try {
			// OK-Response erstellen und versenden
			Response response = ua.messageFactory.createResponse(200, request);
			serverTransactionID.sendResponse(response);

			String content = new String(request.getRawContent());
			System.out.println(content);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}


	public void processOptions(RequestEvent requestEvent,
			ServerTransaction serverTransactionID) {
		
		Request request = requestEvent.getRequest();
		
		try {
			
			Response ok = ua.messageFactory.createResponse(Response.OK, request);
			AllowHeader allowHeader = ua.headerFactory.createAllowHeader("INVITE, ACK, OPTIONS, CANCEL, BYE, MESSAGE");
			ok.addHeader(allowHeader);
			ServerTransaction st = requestEvent.getServerTransaction();
			st.sendResponse(ok);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
