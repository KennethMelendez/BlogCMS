<%-- 
    Document   : navigationBar
    Created on : Jun 13, 2018, 9:11:29 AM
    Author     : dannylantigua
--%>

<nav class="navbar navbar-expand-lg navbar-light bg-light" >

    <a class="navbar-brand" href="${pageContext.request.contextPath}/homepage">Welcome</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav mr-auto">

            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/allBlogs">All Blog Posts</a>
            </li>
            
