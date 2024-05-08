<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link href="https://fonts.googleapis.com/css?family=Inter&display=swap" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css?family=sans-serif&display=swap" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css?family=Archivo+Black&display=swap" rel="stylesheet" />
    <link href="resources/css/Header.css" rel="stylesheet" />
    <title>Document</title>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            let isLoggedIn = false; // 서버에서 로그인 상태 받아오기

            if (isLoggedIn) {
                document.querySelectorAll('.logged-in').forEach(function(item) {
                    item.style.display = 'inline'; // 로그인 시 보여짐
                });
                document.querySelectorAll('.logged-out').forEach(function(item) {
                    item.style.display = 'none'; // 로그인 시 숨김
                });
            } else {
                document.querySelectorAll('.logged-in').forEach(function(item) {
                    item.style.display = 'none'; // 로그아웃 시 숨김
                });
                document.querySelectorAll('.logged-out').forEach(function(item) {
                    item.style.display = 'inline'; // 로그아웃 시 보여짐
                });
            }
        });
    </script>
</head>
<body>
<header>
    <div class="container">
        <div class="logo">
            <a href="#">
                <img src="resources/img/Logo.png" alt="BID FLAP">
            </a>
        </div>
        <div class="menu">
            <ul>
                <li class="logged-out"><a href="#">회원가입</a></li>
                <li class="logged-in" style="display:none;"><a href="#">마이페이지</a></li>
                <li class="logged-in" style="display:none;">|</li>
                <li class="logged-in" style="display:none;"><a href="#">알림</a></li>
                <li class="logged-out"><a href="#">로그인</a></li>
                <li class="logged-in" style="display:none;"><a href="#">로그아웃</a></li>
            </ul>
        </div>
    </div>
    <div class="category">
        <ul>
            <li><a href="#">전체</a></li>
            <li><a href="#">휴대폰</a></li>
            <li><a href="#">태블릿</a></li>
            <li><a href="#">웨어러블</a></li>
            <li><a href="#">PC/노트북</a></li>
            <li><a href="#">카메라</a></li>
            <li><a href="#">PC 부품</a></li>
            <li><a href="#">악세사리</a></li>
            <li><a href="#">기타</a></li>
        </ul>
    </div>
</header>
</body>
</html>