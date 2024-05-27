<%@ page import="com.ssd.bidflap.domain.ChatMessage" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String loggedInMemberNickname = (String) session.getAttribute("loggedInMember");
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>BidFlap</title>
	<link rel="stylesheet" type="text/css"
		  href="/resources/css/chat/chatRoom.css">
</head>
<body>
<%@ include file="../Header2.jsp" %>
<div class="chat-container">
	<div class="info-container">
		<div id="product" class="info imgForm">
			<img src="/resources/img/productImg.png"/>
			<b>000,000원</b>
		</div>
		<div class="info">
			<h1>무선이어폰</h1>
			<p>판매자 거래내역 ${5}회</p>
		</div>
		<button onclick="window.location.href='/deliveryInfo'">결제하기</button>
	</div>
	<div id="chat-scroll">
		<div id="chat-history"></div>
	</div>
	<div class="send-container">
		<img src="/resources/img/fileIcon.png" id="imageUpload">

		<img src="" id="imagePreview" style="display: none; max-width: 100px; max-height: 100px;">
		<input type="file" id="attachment" style="display: none;">
		<input type="text" id="message" placeholder="Type your message...">
		<img src="/resources/img/sendIcon.png" id="sendIcon">
	</div>
</div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script>
	var stompClient = null;
	var loggedInMemberNickname = <%= loggedInMemberNickname %>;
	if(loggedInMemberNickname == null)
		loggedInMemberNickname = "rkfka";

	var roomId = ${chatRoom.id} != null ? ${chatRoom.id} : 4;

	var chatHistory = [
		<c:forEach var="chatMessage" items="${chatMessages}">
		{
			message: "<c:out value="${chatMessage.message}"/>",
			date: "<c:out value="${chatMessage.createdAt}"/>",
			type: "<c:out value="${chatMessage.member.nickname != loggedInMemberNickname ? 'receiver' : 'sender'}"/>"
		},
		</c:forEach>
	];

	function connect() {
		var socket = new SockJS("/ws-stomp");
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function (frame) {
			setConnected(true);
			console.log('연결됨: ' + frame);
			displayChatHistory();   // loadChat

			// 채팅 룸 구독
			stompClient.subscribe('/room/'+roomId, function (chatMessage) {
				showChat(JSON.parse(chatMessage.body));
			});
		});
	}

	function sendMessage(roomId, member, message) {
		if (!stompClient) {
			console.error("WebSocket 연결이 설정되지 않았습니다.");
			return;
		}
		var messageToSend = {
			roomId: roomId,
			member: member,
			message: message
		};
		stompClient.send("/send/" + roomId, {}, JSON.stringify(messageToSend));
	}

	function setConnected(connected) {
		if(connected) console.log("연결 성공");
		else console.log("연결 실패");
	}

	function makeProfileDiv(parent, type) {
		const profileImage = document.createElement('img');
		if(type==='receiver')
			profileImage.src = "/resources/img/Profile.png";
		else
			profileImage.src = "/resources/img/Profile.png";

		profileImage.classList.add('chat-profile');
		profileImage.classList.add(type);
		parent.appendChild(profileImage);
	}

	function makeMessageDiv(parent, message, type) {
		const chatMessage = document.createElement('div');
		chatMessage.innerHTML = message;
		chatMessage.classList.add('message');
		chatMessage.classList.add(type);
		parent.appendChild(chatMessage);
	}

	// 이전 채팅 기록
	function displayChatHistory() {
		const chatHistoryDiv = document.getElementById('chat-history');
		let currentDate = null;

		chatHistory.forEach(item => {
			const messageSet = document.createElement('div');
			messageSet.classList.add('messageSet');

			var messageDate = item.date;
			messageDate = messageDate.substring(0, 10);

			const message = item.message;
			const type = item.type;

			if (messageDate !== currentDate) {
				const dateDiv = document.createElement('div');
				dateDiv.textContent = messageDate;
				dateDiv.classList.add('chatDate');
				chatHistoryDiv.appendChild(dateDiv);
				currentDate = messageDate;
			}

			if (type === "sender")
				makeProfileDiv(messageSet, type);

			makeMessageDiv(messageSet, message, type);

			if (type === "receiver")
				makeProfileDiv(messageSet, type);

			messageSet.classList.add(type);

			chatHistoryDiv.appendChild(messageSet);
		});

		const chatDiv = document.getElementById('chat-scroll');
		chatDiv.scrollTop = chatDiv.scrollHeight;
	}

	//window.onload = displayChatHistory;
	window.onload = function() {
		connect();
	};

	document.getElementById('imageUpload').addEventListener('click', function () {
		document.getElementById('attachment').click();
	});

	document.getElementById('sendIcon').addEventListener('click', function () {
		//${receiver} - 멤버 객체를 model에 추가 필요
		//var member = { id: 61, account: "00000", bank:"신한", email:"river2523@naver.com", member_role:"USER", nickname:"rkfka",password:"$2a$10$y/k.htfvre3tkUnsSTOEd.DRS9G/STn5TCLkyijIeEF8nGEYl11nq", profile:null }; // 예시로 사용자 정보를 하드코딩되었다고 가정합니다.
		var member = ${sender};
		var message = document.getElementById('message').value;

		sendMessage(roomId, member, message);
	});

	function showChat(messageJson) {
		console.log("send message!");
		//var attachmentFile = document.getElementById("attachment").files[0];
		const messageInput = messageJson.message;
		//보낸 채팅
		const chatHistoryDiv = document.getElementById('chat-history');
		const messageSet = document.createElement('div');
		messageSet.classList.add('messageSet');
		makeMessageDiv(messageSet, messageInput, "receiver");
		makeProfileDiv(messageSet, "receiver");
		messageSet.classList.add("receiver");
		chatHistoryDiv.appendChild(messageSet);
		document.getElementById("message").value = "";

		//스크롤 아래로
		const chatDiv = document.getElementById('chat-scroll');
		chatDiv.scrollTop = chatDiv.scrollHeight;
	}
</script>
</body>
</html>