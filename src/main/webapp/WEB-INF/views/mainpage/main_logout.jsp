<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/resources/css/main/main.css">
    <title>메인페이지-로그아웃</title>
</head>
<body>
<%@ include file="../Header2.jsp" %>

<div class="main-container">
    <div class="main-frame">
        <img src="resources/img/main_frame.png" alt="Product 1">
    </div>
    <div class="search-container">
        <input type="text" class="search-input" placeholder="원하는 제품을 검색해보세요">
    </div>

    <div class="recommendations-container">요즘 뜨는 상품</div>
    <div class="recommendations-category">전체 | 휴대폰</div>
    <div class="product_images">
        <div class="image_row">
            <!-- 이미지 1 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
            <!-- 이미지 2 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
            <!-- 이미지 3 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
            <!-- 이미지 4 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
        </div>
    </div>
    <div class="product_images">
        <div class="image_row">
            <!-- 이미지 1 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
            <!-- 이미지 2 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
            <!-- 이미지 3 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
            <!-- 이미지 4 -->
            <div class="image_container">
                <div class="image_and_heart_container">
                    <div class="image_placeholder"></div>
                    <img src="resources/img/product_example.png" alt="Product 1" class="product_img">
                    <img src="resources/img/heart.png" alt="heart 1" class="heart_icon">
                    <div class="heart"></div>
                </div>
                <p>100,000원</p>
            </div>
        </div>
    </div>
    <!-- 오른쪽 빈 공간 -->
    <div class="empty_space"></div>
    <!-- 선택 창 -->
    <div class="selection-box">

        <a href=""> 내 경매</a><br/><br/><br/>
        <a href=""> 좋아요한 목록</a><br/><br/><br/>
        <a href=""> AS 서비스</a> <br/><br/><br/>
        <a href=""> 판매글 목록</a><br/><br/><br/>
    </div>
</div>

</body>
</html>
