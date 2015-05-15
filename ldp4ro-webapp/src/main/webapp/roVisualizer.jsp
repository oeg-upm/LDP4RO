<!--

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

-->
<%@ page import="org.ldp4j.apps.ldp4ro.listeners.ConfigManager" %>
<!DOCTYPE html>
<html lang="en">
  <!--
  /*
 * This page was created by Daniel Garijo.
 * https://github.com/oeg-upm/LDP4RO/blame/master/ldp4ro-webapp/src/main/webapp/roVisualizer.jsp
 */
  -->
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Browse existing Research Objects</title>

	
    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet"/>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script type="text/javascript">
			
			$(document).ready(function() {
				loadROs();
			});

			/**
			* Function to load the Research Objects. 
			* Thanks to Daniel Vila for his help
			**/
			function loadROs() {
                var endpointURI ="<%=ConfigManager.getAppConfig().getString("ldp4j.container-url")%>";
				//var endpointURI ="http://localhost:8080/ldp4j/ldp-bc/";
				// An ajax request that requests the above URI and parses the response. 
				$.ajax( {
					//contentType: "application/ld+json",
					//accepts: {jsonld: "application/ld+json"},
					//dataType :'jsonp',
					headers: {'Accept': 'application/ld+json'},
					//jsonp :'callback',
					url :endpointURI,
					success : function(json) {
						//console.log(json);
						console.log("Success!. Number of ROs "+json.contains.length);
						$("#numberOfROs").html(json.contains.length);
						loadROMetadata(json.contains);
					}
				}); 
			}
			/**
			* Function that loads the metadata of the Research Objects and builds the html
			**/
			//var _researchObjects;
			var _researchObjectHtml='';//variable for storing incrementally the html of the table
			function loadROMetadata(ros) {
				//_researchObjects=[];
				for(var i = 0; i < ros.length; i++) {
					var currentRO = ros[i];
					//load title, authors, date and license
					$.ajax( {
					headers: {'Accept': 'application/ld+json'},
					url :currentRO,
					success : function(json) {
						//iterate through the objects returned.
						var returnedObjects= json["@graph"];
						var ro = new Object();
						var agents = [];
						for(var j=0; j<returnedObjects.length; j++){
							//if it is an RO, get the metadata. Otherwise push in the author array.
							//since ROs are defined with 3 types, I test any of the three
								if(returnedObjects[j]['@type'] instanceof Array && 
									(returnedObjects[j]['@type'][0]=='ore:Aggregation') ||
									(returnedObjects[j]['@type'][0]=='ro:ResearchObject') ||
									(returnedObjects[j]['@type'][0]=='ldp:Container')||
									(returnedObjects[j]['@type'][0]=='ldp:DirectContainer'))
									{
									ro.title = returnedObjects[j].title;//
									ro.date = returnedObjects[j]['dc:created']['@value'];
									if(ro.date == 'undefined' || ro.date == null){
										ro.date = returnedObjects[j]['dc:created'];
									}
									ro.license = returnedObjects[j].license;
									ro.uri = returnedObjects[j]['@id'];
									//if the agents are not individual objects, at least get their uris
									ro.creator = returnedObjects[j]['creator']
									//console.log('Found RO: '+ro.title);
								}								//if it's an agent, add it as creator
							if(returnedObjects[j]['@type']=='dc:Agent'){									
								var ag = new Object();
								if(returnedObjects[j]['foaf:homepage'])ag.url = returnedObjects[j]['homepage'];
								if(returnedObjects[j]['foaf:name'])	ag.name = returnedObjects[j]['foaf:name'];
								//console.log(ag.url+' ');
								agents.push(ag);
							}							
								//console.log(returnedObjects[1].title);														
						}
						//console.log(currentRO);
						ro.agents = agents;
						loadRow(ro);
						updateTable();
					}
				}); 
				}
				
				
			}
			
			/*
			Function that updates the html variable for the table asynchronously
			*/
			function updateTable(){
				var html='<table class="col-sm-12">'+
					'<thead>'+
					'	<tr>'+
					'		<th data-field="id" class="col-sm-4">Title</th>'+
					'		<th data-field="name" class="col-sm-2">Author</th>'+
					'		<th data-field="price" class="col-sm-3">Date</th>'+
					'		<th data-field="license" class="col-sm-3">License</th>'+
					'	</tr>'+
					'</thead>';
				html+=	'<tbody>'+_researchObjectHtml+
				'</tbody></table>';
				html+="</ul>";
				$("#roList").html(html);
			}
			
			/**
			* Function that loads a research object into a row in the html variable.
			**/
			function loadRow(ro){
				_researchObjectHtml+='<tr>';
				_researchObjectHtml+='<td><a href=\"'+ro.uri+'\">'+ro.title+'</a></td>';
				//console.log(ro.agents);
				if(ro.agents && ro.agents.length>0){
					_researchObjectHtml+='<td>'+ro.agents[0].name+'</td>';
				}else{
					if(ro.creator && (typeof ro.creator == 'string' || ro.creator instanceof String)){
						_researchObjectHtml+='<td>'+ro.creator+'</td>';
					}else{
						if(ro.creator){
							_researchObjectHtml+='<td>'+ro.creator[0]+'</td>';
						}else{
							_researchObjectHtml+='<td>Undefined</td>';
						}
					}
				}
				_researchObjectHtml+='<td>'+ro.date+'</td>';
				_researchObjectHtml+='<td><a href=\"'+ro.license+'\">'+ro.license+'</a></td>';
				_researchObjectHtml+='</tr>';
			}
			
	</script>

  </head>
   <body>

    <div class="container">
      <nav class="navbar navbar-inverse" role="navigation">
            <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <a class="navbar-brand" href="index.jsp">Create ROs!</a>
              <ul class="nav navbar-nav">
				  <li><a href="#">Browse ROs</a></li>
				  <li><a href="search.html">Search ROs</a></li>
                  <li><a href="about.html">About</a></li>
              </ul>
            </div>
        </div>
      </nav>
      <div>        
        <h3 class="text-muted">Browsing existing Research Objects</h3>
      </div>
	
	<div class="panel panel-default">
	  <div class="panel-body">
			<h3><span id="numberOfROs">0</span> Research Objects found:</h3>	
              
		<div id="roList">	
			Loading Research Objects...
		</div>
       </div>
	</div>

      <footer class="footer">
        <p>&copy; Ontology Engineering Group, 2014</p>
      </footer>

    </div> <!-- /container -->
    
  </body>
</html>