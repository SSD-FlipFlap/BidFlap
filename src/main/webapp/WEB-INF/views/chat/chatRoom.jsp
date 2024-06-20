<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% String loggedInMemberNickname = (String) session.getAttribute("loggedInMember");%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>BidFlap</title>
	<link rel="stylesheet" type="text/css"
		  href="/resources/css/chat/chatRoom.css">
</head>
<body>
<div class="chat-container">
	<div class="info-container">
		<c:choose>
			<c:when test="${message == '채팅가능'}">
				<div class="info imgForm product">
					<c:choose>
						<c:when test="${seller.profile != null && !seller.profile.isEmpty()}">
							<c:if test="${crType=='product'}">
								<img class="chatImg" src="${product.productImageList.get(0).url}" alt="Seller Profile"/>
							</c:if>
							<c:if test="${crType=='afterService'}">
								<img class="chatImg" src="${seller.profile}" alt="Seller Profile" />
							</c:if>
						</c:when>
						<c:otherwise>
							<img class="chatImg" src="/resources/img/Profile.png" alt="Default Profile"/>
						</c:otherwise>
					</c:choose>
					<b>${product.price}원</b>
				</div>
				<c:choose>
					<c:when test="${crType == 'product'}">
						<div class="info">
							<h1>${product.title}</h1>
							<p>${seller.nickname} 거래내역 ${soldCounts}회</p>
						</div>
					</c:when>
					<c:when test="${crType == 'afterService'}">
						<div class="info">
							<h1>${seller.nickname}</h1>
							<p>${seller.nickname} 문의 채팅방</p>
						</div>
					</c:when>
				</c:choose>
			</c:when>
			<c:otherwise>
				<div class="info imgForm product">
					<img class="chatImg" src="/resources/img/Profile.png"/>
					<b>거래 불가능</b>
				</div>
				<div class="info">
					<h1>알 수 없는 사용자</h1>
					<p>채팅을 보낼 수 없습니다.</p>
				</div>
			</c:otherwise>
		</c:choose>
		<c:if test="${crType=='product'}">
			<div class="button-container">
				<form action="/product/purchase" method="post">
					<input type="hidden" name="id" value="${product.id}" />
					<button type="submit">구매하기</button>
				</form>
			</div>
		</c:if>
	</div>
	<div id="chat-scroll">
		<div id="chat-history">
			<c:set var="prevDate" value="" />
			<c:forEach var="chatMessage" items="${chatMessages}">
				<c:set var="currentDate" value="${fn:substring(chatMessage.createdAt, 0, 10)}" />
				<c:if test="${!currentDate.equals(prevDate)}">
					<div class="chatDate">
						<p>${currentDate}</p>
					</div>
					<c:set var="prevDate" value="${currentDate}" />
				</c:if>
				<c:choose>
					<c:when test="${chatMessage.member.nickname eq sender.nickname}">
						<div class="fileSet member1">
							<c:if test="${chatMessage.attachmentUrl !=null}">
								<img src="${chatMessage.attachmentUrl}" class="fileImg member1">
							</c:if>
							<c:if test="${chatMessage.message.trim() != '' && chatMessage.message!=null}">
								<div class="messageSet member1">
									<div class="message">${chatMessage.message}</div>
									<c:choose>
										<c:when test="${sender.profile != null && not empty sender.profile}">
											<img src="${sender.profile}" class="chat-profile">
										</c:when>
										<c:otherwise>
											<img src="/resources/img/Profile.png" class="chat-profile">
										</c:otherwise>
									</c:choose>
								</div>
							</c:if>
						</div>
					</c:when>
					<c:otherwise>
						<div class="fileSet member2">
							<c:if test="${chatMessage.attachmentUrl !=null}">
								<img src="${chatMessage.attachmentUrl}" class="fileImg member2">
							</c:if>
							<c:if test="${chatMessage.message.trim() != '' && chatMessage.message!=null}">
								<div class="messageSet member2">
									<c:choose>
										<c:when test="${chatMessage.member.profile != null && not empty chatMessage.member.profile}">
											<img src="${chatMessage.member.profile}" class="chat-profile">
										</c:when>
										<c:otherwise>
											<img src="/resources/img/Profile.png" class="chat-profile">
										</c:otherwise>
									</c:choose>
									<div class="message">${chatMessage.message}</div>
								</div>
							</c:if>
						</div>
					</c:otherwise>
				</c:choose>
			</c:forEach>

		</div>
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
	let stompClient = null;
	let loggedInMemberNickname = "<%= loggedInMemberNickname %>";

	let roomId = "${chatRoom.id}";

	function connect() {
		var socket = new SockJS("/ws-stomp");
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function (frame) {
			setConnected(true);
			//console.log('연결됨: ' + frame);
			moveChatScrollToBottom();

			// 채팅 룸 구독
			stompClient.subscribe('/room/' + roomId, function (chatMessage) {
				showChat(JSON.parse(chatMessage.body));
			});
		}, function (error) {
			console.error('Connection error:', error);
			setTimeout(connect, 1000); // 5초 후에 재연결 시도
		});
	}

	function sendMessage(roomId, member, message, attachment) {
		if (!stompClient) {
			console.error("WebSocket 연결이 설정되지 않았습니다.");
			return;
		}
		var formData = new FormData();
		formData.append('roomId', roomId);
		formData.append('member', JSON.stringify(member));
		formData.append('message', message);
		if (attachment) {
			formData.append('attachment', attachment);
		}

		fetch('/chat/sendMessageWithAttachment', {
			method: 'POST',
			body: formData
		}).then(response => response.json())
				.then(data => {
					const chatMessage = {
						message: message,
						attachmentUrl: data.attachmentUrl,
						member: member
					};
					stompClient.send("/send/" + roomId, {}, JSON.stringify(chatMessage));
					formData = new FormData();
				})
				.catch(error => console.error('Error:', error));
	}


	function setConnected(connected) {
		if (connected) console.log("연결 성공");
		else {
			console.log("연결 실패");
			window.onload();
		}
	}

	function makeProfileDiv(parent, type) {
		const profileImage = document.createElement('img');
		const imgSrc = "/resources/img/Profile.png";

		let profile = null;

		if(${product.member != sender})
			profile = "${product.member.profile}";	//afterService, product 모두 model명이 product
		else
			profile = "${chatRoom.member.profile}";

		if (type === 'member1' && ${sender.profile.trim() != ""} && ${sender.profile != null})
			profileImage.src = "${sender.profile}";
		else if (type === 'member2' && profile.trim() != "" && profile != null)
			profileImage.src = profile;
		else
			profileImage.src = imgSrc;

		profileImage.classList.add('chat-profile');
		parent.appendChild(profileImage);
	}

	function makeMessageDiv(parent, message) {
		const chatMessage = document.createElement('div');
		chatMessage.innerHTML = message;
		chatMessage.classList.add('message');
		parent.appendChild(chatMessage);
	}

	function makeAttachmentDiv(parent, attachmentUrl) {
		const attachmentImage = document.createElement('img');
		attachmentImage.src = attachmentUrl;
		attachmentImage.classList.add('fileImg');
		parent.appendChild(attachmentImage);
	}

	window.onload = function () {
		connect();
	};

	document.getElementById('imageUpload').addEventListener('click', function () {
		document.getElementById('attachment').click();
	});

	document.getElementById('imagePreview').addEventListener('click', function () {
		const imagePreview = document.getElementById('imagePreview');
		imagePreview.src = "";
		imagePreview.style.display = 'none';
		document.getElementById('attachment').value = '';
	})

	document.getElementById('attachment').addEventListener('change', function (event) {
		const file = event.target.files[0];

		if (file) {
			// 파일 확장자 검사
			const allowedExtensions = ['png', 'jpg', 'jpeg'];
			const fileExtension = file.name.split('.').pop().toLowerCase();
			if (!allowedExtensions.includes(fileExtension)) {
				alert("png, jpg, jpeg 파일만 가능합니다.");
				event.target.value = ''; // 파일 선택 창 비우기
				return;
			}

			const reader = new FileReader();
			reader.onload = function (e) {
				const imagePreview = document.getElementById('imagePreview');
				imagePreview.src = e.target.result;
				imagePreview.style.display = 'block';
			};
			reader.readAsDataURL(file);
		}
	});

	document.getElementById('sendIcon').addEventListener('click', function (event) {
		event.stopPropagation();
		if ("${sender.nickname!= '알수없음'}" && "${message == '채팅가능'}") {
			let member = {
				id: ${sender.id},
				account: "${sender.account}",
				bank: "${sender.bank}",
				email: "${sender.email}",
				memberRole: "${sender.memberRole}",
				nickname: "${sender.nickname}",
				password: "${sender.password}",
				profile: "${sender.profile}"
			};
			let message = document.getElementById('message').value.trim();
			let attachmentInput = document.getElementById('attachment');
			let attachment = attachmentInput.files[0];

			if (message != "" || attachment) {
				sendMessage(roomId, member, message, attachment);
				attachmentInput.value = "";
				const imagePreview = document.getElementById('imagePreview');
				imagePreview.src = "";
				imagePreview.style.display = 'none';
			} else {
				alert("메시지를 입력해주세요");
				document.getElementById('message').value = "";
			}
		} else {
			alert("판매자가 탈퇴했거나 삭제한 경우 채팅을 보낼 수 없습니다.");
		}
	});

	function showChat(messageJson) {
		const messageInput = messageJson.message;
		const attachmentUrl = messageJson.attachmentUrl;
		//console.log("url", attachmentUrl);
		//보낸 채팅
		const chatHistoryDiv = document.getElementById('chat-history');

		const fileSet = document.createElement('div');
		fileSet.classList.add('fileSet');
		const messageSet = document.createElement('div');
		messageSet.classList.add('messageSet');

		type = messageJson.member.nickname == loggedInMemberNickname ? "member1" : "member2";

		if (attachmentUrl) {
			//convertImageToWebP(attachmentUrl);
			makeAttachmentDiv(fileSet, attachmentUrl);
		}
		if (type == "member1") {
			if (messageInput != "") {
				makeMessageDiv(messageSet, messageInput);
				makeProfileDiv(messageSet, "member1");
			}
		} else {
			if (messageInput != "") {
				makeProfileDiv(messageSet, "member2");
				makeMessageDiv(messageSet, messageInput);
			}
		}

		messageSet.classList.add(type);
		fileSet.appendChild(messageSet);
		fileSet.classList.add(type);
		chatHistoryDiv.appendChild(fileSet);
		document.getElementById("message").value = "";

		moveChatScrollToBottom();
	}

	function moveChatScrollToBottom() {
		const chatDiv = document.getElementById('chat-scroll');
		chatDiv.scrollTop = chatDiv.scrollHeight;
	}
</script>
</body>
</html>