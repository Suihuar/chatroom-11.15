<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <meta charset="ISO-8859-1">
    <title>User Account Information</title>
    <style type="text/css">
        label {
            display: inline-block;
            width: 200px;
            margin: 5px;
            text-align: left;
        }
        input[type=text], input[type=password], select {
            width: 200px;
        }
        input[type=radio] {
            display: inline-block;
            margin-left: 45px;
        }
        input[type=checkbox] {
            display: inline-block;
            margin-right: 190px;
        }
        button {
            padding: 10px;
            margin: 10px;
        }
        .error {
            color: red;
            font-style: italic;
        }
        .success {
            color: green;
            font-style: italic;
        }
        .button {
            padding: 10px 20px;
            margin: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .button:hover {
            background-color: #45a049;
        }
        .avatar-circle {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background-color: #007bff;
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
        }
        .avatar-circle img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .avatar-initial {
            color: white;
            font-size: 40px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div align="center">
    <h2>User Account Information</h2>
    <div id="avatarContainer" class="avatar-circle">
        <!-- Avatar content will be inserted here by JavaScript -->
    </div>
    <form:form action="update" method="post" modelAttribute="user" enctype="multipart/form-data">
        <form:label path="name">Full name:</form:label>
        <form:input path="name"/>
        <form:errors path="name" cssClass="error" /><br/>

        <form:label path="email">E-mail:</form:label>
        <form:input path="email"/>
        <form:errors path="email" cssClass="error" /><br/>

        <form:label path="password">Password:</form:label>
        <form:password path="password" showPassword="true"/>
        <form:errors path="password" cssClass="error" /><br/>

        <form:label path="birthday">Birthday (yyyy-mm-dd):</form:label>
        <form:input path="birthday" type="date" />
        <%--            <input type="date" th:field="*{birthday}" />--%>
        <form:errors path="birthday" cssClass="error" /><br/>

        <form:label path="gender">Gender:</form:label>
        <form:radiobutton path="gender" value="Male"/>Male
        <form:radiobutton path="gender" value="Female"/>Female<br/>

        <form:label path="profession">Profession:</form:label>
        <form:select path="profession" items="${professionList}" /><br/>

        <form:label path="married">Married?</form:label>
        <form:checkbox path="married"/><br/>

        <form:label path="note">Note:</form:label>
        <form:textarea path="note" cols="25" rows="5"/><br/>

        <label for="profileImage">Profile Image:</label>
        <input type="file" name="profileImage" id="profileImage" accept="image/*">

        <form:button>Update Information</form:button>
    </form:form>

    <% if (request.getAttribute("updateSuccess") != null) { %>
    <div class="success">${updateSuccess}</div>
    <% } %>

    <a href="/lobby" class="button">Return to Lobby</a>
</div>
<script>
    (function() {
        var container = document.getElementById('avatarContainer');
        var pathPrefix = "/Users/rayrao11/Desktop/Term 1/IEMS5731 Software Developement/Group Project/uploads/";
        var imagePath = pathPrefix + "${user.profileImagePath}";
        var userName = "${user.name}";
        if (imagePath && imagePath !== "null" && imagePath !== "") {
            var img = document.createElement('img');
            img.src = "/uploads/" + imagePath.split('/').pop();
            img.alt = "User Avatar";
            container.appendChild(img);
        } else {
            var initial = document.createElement('span');
            initial.className = "avatar-initial";
            initial.textContent = userName.charAt(0).toUpperCase();
            container.appendChild(initial);
        }
    })();
</script>
</body>
</html>