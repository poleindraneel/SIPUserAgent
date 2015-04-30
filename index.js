
var webSocket = new WebSocket("ws://192.168.50.57:8082/");



webSocket.onopen = function() {
    alert("Opened!");
    var userJoined_Jason = { "user" : "joined" };
    webSocket.send(JSON.stringify(userJoined_Jason));
};

webSocket.onmessage = function (evt) {
	
	
	var message = JSON.parse(evt.data);
	if(message.id == "incomingCall")
		{
			var des = confirm(message.body);
			if(des == false)
				{
					var can = {"call" : "declined"};
					webSocket.send(JSON.stringify(can));
				}
		}
	else if(message.type == "Message")
		{
			var des = message.body;
			var from = message.from;
			/*var items = document.getElementById("messageList");
			 var item = document.createElement("li");
		        item.innerHTML = des;
		        items.appendChild(item);*/
			document.getElementById("readMessage").value = message.msg;
			document.getElementById("readFrom").value = message.from;
			var replArray = from.split("@");
			var reply = replArray[0];
			var nameArr = reply.split(":");
			var name = nameArr[1];
			var ip = replArray[1];
			var ipNew = ip.replace(">","");
			document.getElementById("calledUA").value = name;
			document.getElementById("calledDomain").value = ipNew;
		}
	else if(message.type == "Register")
		{
			document.getElementById("registerStatus").value = message.status;
			document.getElementById("registerRealm").value = message.realm;
			document.getElementById("registerCSeq").value = message.cseq;
		}
	else if(message.type == "GStreamer")
		{
			document.getElementById("gstreamerStat").value = message.status;
			if(message.status == "running")
				{
					document.getElementById("audioPort").value = message.callerAudioPort;
					document.getElementById("audioCodec").value = message.callerAudioCodec;
					document.getElementById("videoPort").value = message.callerVideoPort;
					document.getElementById("videoCodec").value = message.callerVideoCodec;
				}
		}
	
};

webSocket.onclose = function() {
	var userLeft_Jason = { "user" : "left" };
  	webSocket.send(JSON.stringify(userLeft_Jason));
   	alert("Closed!");
};

webSocket.onerror = function(err) {
    alert("Error: " + err);
};


function acceptCall(){
	var acceptCall_Json = { "call" : "accepted",
			"status" : "user has picked up call"};
	webSocket.send(JSON.stringify(acceptCall_Json));
}

function declineCall(){
	var declineCall_Json = { "call" : "declined",
			"status":"user has declined call"};
	webSocket.send(JSON.stringify(declineCall_Json));
}

function callUA(){

	var UaUri = document.getElementById("calledUA").value;
	var Domain = document.getElementById("calledDomain").value;
	var dialCall_Json = 
	
		{	
			"call" : "dialed",
			"uri" :	UaUri,
			"Domain" : Domain
		};
	
	webSocket.send(JSON.stringify(dialCall_Json));
	alert("calling");	//webSocket.send(JSON.stringify(UaUri));
	//webSocket.send(JSON.stringify(Domain));
}

function endCall(){
	//var UaUri = document.getElementById("calledUA").value;
	//var Domain = document.getElementById("calledDomain").value;
	var endCall_Json = {"call" : "end"};
	webSocket.send(JSON.stringify(endCall_Json));
}

function registerUser(){
	var Realm = document.getElementById("realm").value;
	var Realm_JSON = {"register" : "start", "realm" : Realm};
	webSocket.send(JSON.stringify(Realm_JSON));
}

function deregisterUser(){
	var Realm = document.getElementById("realm").value;
	var Realm_JSON = {"register" : "stop", "realm" : Realm};
	webSocket.send(JSON.stringify(Realm_JSON));
}

function sendMessage(){
	var body = document.getElementById('MessageText').value;
	var UaUri = document.getElementById("calledUA").value;
	var Domain = document.getElementById("calledDomain").value;
	var endCall_Json = {"message" : "sent",
			"uri" : UaUri,
			"Domain" :	Domain,
		"body":body};
    alert('Message is sent');
	webSocket.send(JSON.stringify(endCall_Json));
};
var counter = 0
function isNumberKey(evt){
	
	var charCode = (evt.which) ? evt.which : event.keyCode
    
	alert(evt.keyCode);
	if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
return true;    
}    
