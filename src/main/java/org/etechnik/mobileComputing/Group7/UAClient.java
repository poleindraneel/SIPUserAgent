package org.etechnik.mobileComputing.Group7;

import java.text.ParseException;
import java.util.HashMap;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

public class UAClient {

	private UA ua = null;
	public static UAClient uaClient = null;
	
	public UAClient(UA ua) {
		uaClient = this;
		this.ua = ua;
	}
	
	public static UAClient getInstance(){
		return uaClient;
	}
	
	
	
	
	// Might need to do some Changes based on dialog ... for CSEQ Header ... for now every CSEQ Header is 1
	public void sendMESSAGE(String message, String toUser, String toIP, String toPort){
		
		if((toIP == null) || toIP.isEmpty()){
			toIP = UserProperties.realm;
		}
		
		
		try {
								
				SipURI requestURI = ua.addressFactory.createSipURI(toUser, toIP);
				CallIdHeader callIdHeader = ua.sipProvider.getNewCallId();
				CSeqHeader cSeqHeader = ua.headerFactory.createCSeqHeader(1L, Request.MESSAGE);

				Request request = ua.messageFactory.createRequest(requestURI, 
								Request.MESSAGE,
								callIdHeader,
								cSeqHeader,
								ua.createFromHeader(),
								ua.createToHeader(toUser, toIP, toPort),
								ua.createViaHeader(),
								ua.createMaxForwardsHeader(70));
			
				request.addHeader(ua.createContactHeader());	
				request.setContent(message, ua.createContentTypeHeader("text", "plain"));
									
				ClientTransaction msg = ua.sipProvider.getNewClientTransaction(request);
				msg.sendRequest();

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	@SuppressWarnings("deprecation")
	public void sendREGISTER(int expires){
		try{
			UserProperties.cSeqRegister++;
			
			Request request = ua.messageFactory.createRequest("REGISTER sip:"+ UserProperties.realm + " SIP/2.0 \r\n\r\n");
			request.addHeader(ua.sipProvider.getNewCallId());
			request.addHeader(ua.headerFactory.createCSeqHeader(UserProperties.cSeqRegister, "REGISTER"));
			request.addHeader(ua.createToHeader(UserProperties.name, UserProperties.realm, null));
			request.addHeader(ua.createFromHeader());
			request.addHeader(ua.createMaxForwardsHeader(70));
			request.addHeader(ua.createContactHeader());
			request.addHeader(ua.headerFactory.createExpiresHeader(expires));
			request.addHeader(ua.headerFactory.createViaHeader(ua.ipAddress, ua.port, ua.protocol, null));
			
			ClientTransaction ct = (ClientTransaction) ua.sipProvider.getNewClientTransaction(request);
			ct.sendRequest();
			
			if(expires == 0) {
				UserProperties.realm = "";
				UserProperties.cSeqRegister = 0;
			}

			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();

	}
		
		
	}

	
	
	public void sendBYEtoCurrentCall(){
		try {
			if(!(ua.callDialog == null)){
				Request byeRequest = ua.callDialog.createRequest(Request.BYE);
			
				ClientTransaction ct = ua.sipProvider.getNewClientTransaction(byeRequest);
				ua.callDialog.sendRequest(ct);
				RestClient.stopGStreamer();
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("type", "GStreamer");
				map.put("status", "stopped");
				new WebsocketMessageHandler().sendMessage(map);
				ua.callDialog = null;
			} else System.out.println("no current call activ");
		
			
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void sendINVITE(String toUser, String toIP){
		
		if((toIP == null || toIP.isEmpty())){
			toIP = UserProperties.realm;
		}
		
		try {
			Request request = ua.messageFactory.createRequest(ua.addressFactory.createSipURI(toUser, toIP),
					Request.INVITE, 
					ua.sipProvider.getNewCallId(),
					ua.headerFactory.createCSeqHeader(1L, Request.INVITE),
					ua.createFromHeader(),
					ua.createToHeader(toUser, toIP, null),
					ua.createViaHeader(),
					ua.createMaxForwardsHeader(70),
					ua.createContentTypeHeader("application", "sdp"),
					new SDP().createSDPOffer(ua.fromName, ua.ipAddress, ua.audioPort, ua.videoPort));
			
			request.addHeader(ua.createContactHeader());
			
			ClientTransaction clientTransaction = (ClientTransaction) ua.sipProvider.getNewClientTransaction(request);
			clientTransaction.sendRequest();
			ua.callClientTransaction = clientTransaction;
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		} catch (TransactionUnavailableException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		}
	}
	
}
