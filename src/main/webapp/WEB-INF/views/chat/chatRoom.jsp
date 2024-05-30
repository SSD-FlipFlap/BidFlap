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
			<b>${product.price}원</b>
		</div>
		<div class="info">
			<h1>${product.title}</h1>
			<p>${seller.nickname} 거래내역 ${5}회</p>
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
<script type="text/javascript">
	var stompClient = null;
	var loggedInMemberNickname = "<%= loggedInMemberNickname %>";
	if(loggedInMemberNickname == null)
		loggedInMemberNickname = "rkfka";

	var roomId = ${chatRoom.id} != null ? ${chatRoom.id} : 4;

	var chatHistory = [
		<c:forEach var="chatMessage" items="${chatMessages}">
		{
			message: "<c:out value="${chatMessage.message}"/>",
			date: "<c:out value="${chatMessage.createdAt}"/>",
			type: "<c:out value="${chatMessage.member.nickname != loggedInMemberNickname ? 'member1' : 'member2'}"/>"
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
		var imgSrc = "/resources/img/Profile.png";
		if(type==='member1' && ${send.profile!=null})
			profileImage.src = "${sender.profile}";
		else if(type==='member2' && ${send.profile!=null})	//판매자 프로필로 변경 필요
			profileImage.src = "${sender.profile}";
		else
			profileImage.src = imgSrc;

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

			if (type === "member2")
				makeProfileDiv(messageSet, type);

			makeMessageDiv(messageSet, message, type);

			if (type === "member1")
				makeProfileDiv(messageSet, type);

			messageSet.classList.add(type);

			chatHistoryDiv.appendChild(messageSet);
		});

		const chatDiv = document.getElementById('chat-scroll');
		chatDiv.scrollTop = chatDiv.scrollHeight;
	}

	window.onload = function() {
		connect();
	};

	document.getElementById('imageUpload').addEventListener('click', function () {
		document.getElementById('attachment').click();
	});

	document.getElementById('sendIcon').addEventListener('click', function () {
		var member = {
			id: ${sender.id},
			account: "${sender.account}",
			bank: "${sender.bank}",
			email: "${sender.email}",
			member_role: "${sender.memberRole}",
			nickname: "${sender.nickname}",
			password: "${sender.password}",
			profile: "${sender.profile}"
		};
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
		makeMessageDiv(messageSet, messageInput, "member1");
		makeProfileDiv(messageSet, "member1");
		messageSet.classList.add("member1");
		chatHistoryDiv.appendChild(messageSet);
		document.getElementById("message").value = "";

		//스크롤 아래로
		const chatDiv = document.getElementById('chat-scroll');
		chatDiv.scrollTop = chatDiv.scrollHeight;
	}
</script>
</body>
</html>