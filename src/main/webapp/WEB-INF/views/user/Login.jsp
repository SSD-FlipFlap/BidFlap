<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/resources/css/auth/Login.css">
    <title>로그인</title>
</head>
<body>
<%@ include file="../Header2.jsp" %>

<div class="login-container">
    <h2>Bid Flap</h2><br/>
    <form action="/login" method="post">
        <div class="form-group">
            <label for="email">이메일 주소</label><br/>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="form-group">
            <label for="password">비밀번호</label><br/>
            <input type="password" id="password" name="password" required>
        </div>
        <br/>
        <button type="submit">로그인</button>
    </form>
    <div class="link-container">
        <a href="#" class="link">회원가입</a>
        <a href="#" class="link">이메일 찾기</a>
        <a href="#" class="link">비밀번호 찾기</a>
    </div>
</div>
</body>
</html>
