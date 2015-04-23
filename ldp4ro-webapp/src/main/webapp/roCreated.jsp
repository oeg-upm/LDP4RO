<<<<<<< HEAD
=======
<%@ page import="java.net.URLEncoder" %>
<%--

    Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid (http://www.oeg-upm.net/)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
>>>>>>> origin/master
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <!--
  /*
 * Copyright 2012-2013 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * This page was created by Daniel Garijo.
 */
  -->
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>RO created!</title>

    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
    

  </head>
   <body>

    <div class="container">
      <nav class="navbar navbar-inverse" role="navigation">
            <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <a class="navbar-brand" href="index.html">Create ROs!</a>
              <ul class="nav navbar-nav">
				  <li><a href="#">Browse ROs</a></li>
                  <li><a href="about.html">About</a></li>
              </ul>
            </div>
        </div>
      </nav>
      <div>        
        <h3 class="text-muted">Success</h3>
      </div>
	  <div class="alert alert-success" role="alert">
		Your Research Object was created successfully
	  </div>
	
	<div class="panel panel-default">
	  <div class="panel-body">	  
              <p>
			  The URI for your Research Object is:
			  </p>
               <h4><%= request.getAttribute("newURI") %></h4>
			   <h4>View as <a href="<%= request.getAttribute("newURI")%>">RDF</a> or <a href="./roHtml.jsp?uri=<%=URLEncoder.encode(request.getAttribute("newURI").toString())%>">HTML</a></h4>
			  <p>Now you can</p>
			  <ul>
                              <li><a href="roHtml">Navigate through the contents of the Research Object</a></li>
                              <li><a href="roVisualizer.html">Browse the full list of available Research Objects</a></li>
			  </ul>
			  
      </div>
	</div>

      <footer class="footer">
        <p>&copy; Ontology Engineering Group, 2014</p>
      </footer>

    </div> <!-- /container -->
    
  </body>
</html>