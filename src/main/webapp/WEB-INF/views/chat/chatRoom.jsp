<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BidFlap</title>
<link rel="stylesheet" type="text/css"
	href="/resources/css/chat/chatRoom.css">
</head>
<body>
	<%@ include file="../Header2.jsp"%>
	<div class="chat-container">
		<div class="info-container">
			<div id="product" class="info imgForm">
				<img src="/resources/img/productImg.png">
				<div class="heartIcon">
					<img id="heart" src="">
					<script>
               			document.getElementById('heart').src=heart_info();
               			
               			function heart_info() {
                            var img_src = '/resources/img/heartIcon.png';
                            
                            //if(좋아요 클릭한 상품이면) img_src = /resources/img/heartIcon_Fill.png;
                            return img_src;
                        }
               		</script>
				</div>
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
		<form id="sendNewChat" action="/send" method="post">
			<div class="send-container">
				<img src="/resources/img/fileIcon.png" id="imageUpload">
				
        		<img src="" id="imagePreview" style="display: none; max-width: 100px; max-height: 100px;">
        <input type="file" id="attachment" style="display: none;">
					<input type="text" id="message" placeholder="Type your message...">
				<img src="/resources/img/sendIcon.png" id="sendMessage">
				<%-- <button id="sendBtn" type="submit" style="display:none;" /> --%>
			</div>
		</form>
	</div>

	<script>
      const chatHistory = [
    	    { message: "이전 채팅 메시지 1", date: "2024-05-08", type:"sender" },
    	    { message: "이전 채팅 메시지 2", date: "2024-05-08", type:"sender" },
    	    { message: "이전 채팅 메시지 3", date: "2024-05-09",type:"receiver" },
    	    { message: "이전 채팅 메시지 2", date: "2024-05-09", type:"sender" },
    	    { message: "이전 채팅 메시지 3", date: "2024-05-09",type:"receiver" },
    	    { message: "이전 채팅 메시지 2", date: "2024-05-10", type:"sender" },
    	    { message: "이전 채팅 메시지 3", date: "2024-05-10",type:"receiver" },
    	    { message: "이전 채팅 메시지 2", date: "2024-05-10", type:"sender" },
    	    { message: "이전 채팅 메시지 3", date: "2024-05-10",type:"receiver" },
    	    { message: "이전 채팅 메시지 2", date: "2024-05-10", type:"sender" },
    	    { message: "이전 채팅 메시지 3", date: "2024-05-10",type:"receiver" },
    	    { message: "이전 채팅 메시지 2", date: "2024-05-10", type:"sender" },
    	    { message: "이전 채팅 메시지 3", date: "2024-05-10",type:"receiver" },
    	];
	      	
	      function makeProfileDiv(parent, type) {
	    	    const profileImage = document.createElement('img');
	    	    profileImage.src = "/resources/img/Profile.png";
	    	    profileImage.classList.add('chat-profile');
	    	    profileImage.classList.add(type);
	    	    parent.appendChild(profileImage);
	    	}
	      
	      function makeMessageDiv(parent, message, type){
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
		         
		         const messageDate = item.date;
		         const message = item.message;
	             const type = item.type;
	             
		         if (messageDate !== currentDate) {
		             const dateDiv = document.createElement('div');
		             dateDiv.textContent = messageDate;
		             dateDiv.classList.add('chatDate');
		             chatHistoryDiv.appendChild(dateDiv);
		             currentDate = messageDate;
		         }
		         
	             if(type==="receiver")
	             	makeProfileDiv(messageSet,type);
		         
	             makeMessageDiv(messageSet, message,type);
				
	             if(type==="sender")
	        	 	makeProfileDiv(messageSet,type);
	             
	             messageSet.classList.add(type);
	             
	             chatHistoryDiv.appendChild(messageSet);
	          });
	          
	          const chatDiv = document.getElementById('chat-scroll');
              chatDiv.scrollTop = chatDiv.scrollHeight;
	       }
	       
	     //window.onload = displayChatHistory;
	     displayChatHistory();
	     
         document.getElementById('imageUpload').addEventListener('click', function() {
              document.getElementById('attachment').click();
          });
         
         document.getElementById('sendMessage').addEventListener('click', function() {
             sendMessage();
         });
         
         document.getElementById('attachment').addEventListener('change', function() {
        	    const file = this.files[0];
        	    if (file) {
        	        const reader = new FileReader();
        	        reader.onload = function(e) {
        	            const preview = document.getElementById('imagePreview');
        	            preview.src = e.target.result;
        	            preview.style.display = 'block';
        	        }
        	        reader.readAsDataURL(file);
        	    }
        	});
         
          function sendMessage() {
        	  console.log("send message!");
        	  const messageInput = document.getElementById("message").value;
        	  var attachmentFile = document.getElementById("attachment").files[0];
        	  
              // 여기에 새로운 메시지를 처리하고 전송하는 코드를 추가
              // 이 함수가 실행될 때마다 새로운 채팅 메시지를 chatHistory 배열에 추가
              // 새로운 채팅 메시지
		      const chatHistoryDiv = document.getElementById('chat-history');
              

		     const messageSet = document.createElement('div');
		     messageSet.classList.add('messageSet');
		     
		     makeMessageDiv(messageSet, messageInput,"sender");
		     makeProfileDiv(messageSet,"sender");
		     messageSet.classList.add("sender");
		     chatHistoryDiv.appendChild(messageSet);

		     document.getElementById("message").value = "";
              
             const chatDiv = document.getElementById('chat-scroll');
             chatDiv.scrollTop = chatDiv.scrollHeight;
             
             var formData = new FormData();
             formData.append("message", messageInput);
             formData.append("attachment", attachmentFile);

             var xhr = new XMLHttpRequest();
             xhr.open("POST", "/send", true);
             xhr.onreadystatechange = function() {
                 if (xhr.readyState === 4 && xhr.status === 200) {
                     console.log("Message sent successfully");
                 }
             };
             xhr.send(formData);
          }
      </script>
</body>
</html>