<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Bid Flap</title>
<link rel="stylesheet" type="text/css"
	href="/resources/css/afterService/asInfo.css">
</head>
<body>
<%@ include file="../Header.jsp" %>
<div class="asInfo-container">
	<div class="asProfile">
		<img id="profile" src="/resources/img/asProfile.png">
	</div>
	<div class="line"></div>
	<div class="textInfo">
		<div class="mainText">
			<p class="name">몽자</p>
			<p class="cate">#웨어러블 #카메라</p>
		</div>
		<div>
			<p>안녕하세요! 서울시 성동구에서 수리 업체 서비스를 10년... 째 운영 중인</p>
			<p>편하게 문의 주세요</p>

			<button>AS 서비스 문의하기</button>
		</div>
	</div>
</div>

<script>
	function adjustLineHeight() {
	    var textInfoHeight = document.querySelector('.textInfo').offsetHeight;
	    document.querySelector('.line').style.height = textInfoHeight + 'px';
	}
	
	window.addEventListener('DOMContentLoaded', adjustLineHeight);
	window.addEventListener('resize', adjustLineHeight);
</script>
</body>
</html>