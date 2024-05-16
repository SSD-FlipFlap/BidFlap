<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/resources/css/bidder/bidder.css">
    <title>경매페이지-로그인</title>
</head>
<body>
<%@ include file="../Header.jsp" %>

<div class="bidder-container">
    <div class="image-placeholder"></div>
    <div class="left-container">
        <div class="image-frame">
            <img class="product-image" src="https://via.placeholder.com/420x420" />
            <img class="rotate-image" src="https://via.placeholder.com/32x32" />
        </div>
        <div class="profile">
            <img class="profile-image" src="https://via.placeholder.com/110x110" />
                <span class="profile-name">가을이 님</span>
                <br/>
                <span class="profile-start-time">2024년 4월 11일 9시 경매 시작하셨습니다.</span>

        </div>
    </div>
    <div class="right-container">
        <div class="product-container">
            <div class="hashtag">#웨어러블</div>
            <div class="product-name">에어팟 프로맥스 실버</div>
            <div class="product-price">
                <a class="highest-bid">최고입찰가</a>
                <div class="price-value">300,000원</div>
            </div>
            <div class="product-description">설명설명~~~~~~~~ 사용할 제품입니다. 어쩌고저쩌고.. </div>
        </div>
        <div class="button-container">
            <div class="button-left">
                <div class="unavailable">구매불가능</div>
            </div>
            <div class="button-right">
                <button class="participate-button">경매참여</button>
            </div>
        </div>
        <div class="like-unavailable">좋아요 불가능</div>
        <div class="auction-info">경매 시작 가능 | 10명 이상 좋아요</div>
    </div>
</div>
</body>
</html>
