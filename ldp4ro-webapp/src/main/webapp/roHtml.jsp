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
    <title>Browse existing Research Objects</title>

	
    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet"/>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script type="text/javascript">
			
			$(document).ready(function() {
				loadRO();
			});

			/**
			* Function to load the Research Objects. 
			**/
			function loadRO(rouri) {
				var ro ='<%=request.getParameter("uri")%>';//rouri;
				// An ajax request that requests the above URI and parses the response. 
				$.ajax( {
					headers: {'Accept': 'application/ld+json'},
					//jsonp :'callback',
					url :ro,
					success : function(json) {
						console.log(json);
						//console.log("Success!. Number of ROs "+json.contains.length);
						//$("#numberOfROs").html(json.contains.length);
						//loadROMetadata(json.contains);
						//iterate through the contents of the RO
						var returnedObjects= json['@graph'];
						var ro = new Object();
						var agents = [];
						var htmlOther = '';
						for(var j=0; j<returnedObjects.length; j++){
							if(returnedObjects[j]['@type'] instanceof Array && 
									(returnedObjects[j]['@type'][0]=='http://www.openarchives.org/ore/terms/Aggregation') ||
									(returnedObjects[j]['@type'][0]=='http://purl.org/wf4ever/ro#ResearchObject') ||
									(returnedObjects[j]['@type'][0]=='http://www.w3.org/ns/ldp#Container'))
									{
									var obj = returnedObjects[j];
									for(var key in obj){
										var attrName = key;
										var attrValue = obj[key];
										//console.log(attrName+','+attrValue);
										//we iterate through the variables in order to get all of them
										//we can retrieve the variables also doing ro.title = returnedObjects[j].title;
										//but that way we don't capture other properties that the RO might have
										if(key == 'abstract'){
											ro.abstract = attrValue;
										}else if(key == 'title'){
											ro.title = attrValue;
										}else if(key == 'created'){
											ro.date = attrValue;
										}else if(key == 'license'){
											ro.license = attrValue;
										}else if(key == 'creator'){
											ro.creator = attrValue;
										}else if(key == 'aggregates'){
											ro.aggregates = attrValue;
										}else if(key == '@id'){
											ro.uri = attrValue;
										}else if(key == '@type'){
											ro.type = attrValue;
											htmlOther+='<tr><td><strong>Type(s)</strong></td><td><ul>';
											for(var k=0; k<attrValue.length; k++){
												htmlOther+='<li><a href="'+attrValue[k]+'">'+attrValue[k]+'</a></li>';
											}
											htmlOther+='</ul></td></tr>';
										}else{
											htmlOther+='<tr><td><strong>'+attrName+'</strong></td><td><a href="'+attrValue+'">'+attrValue+'</a></td></tr>';
										}
									}
									/*ro.date = returnedObjects[j].created;
									ro.license = returnedObjects[j].license;
									ro.uri = returnedObjects[j]['@id'];
									ro.creator = returnedObjects[j].creator;
									ro.aggregates = returnedObjects[j].aggregates;*/
									//console.log('Found RO: '+ro.title);
								}//if it's an agent, add it as creator
								if(returnedObjects[j]['@type']=='http://purl.org/dc/terms/Agent'){									
									var ag = new Object();
									if(returnedObjects[j].homepage)ag.url = returnedObjects[j].homepage;
									if(returnedObjects[j].name)	ag.name = returnedObjects[j].name;
									//console.log(ag.url+' ');
									agents.push(ag);
								}
						}
						//now that I have all the data, create the html and update it.
						var html='<table class="col-sm-12">'+
							'<tbody>';
						html+='<tr><td><strong>RO URI </strong></td><td><a href="'+ro.uri+'">'+ro.uri+'</a></td></tr>';
						if (typeof ro.abstract !== 'undefined' && ro.abstract !== null) {
							html+='<tr><td><strong>Abstract</strong></td><td>'+ro.abstract+'</td></tr>';
						}
						if (typeof ro.date !== 'undefined' && ro.date !== null) {
							html+='<tr><td><strong>Created on</strong></td><td>'+ro.date+'</td></tr>';
						}
						if (typeof ro.license !== 'undefined' && ro.license !== null) {
							html+='<tr><td><strong>License</strong></td><td><a href="'+ro.license+'">'+ro.license+'</a></td></tr>';
						}
						if (typeof ro.aggregates !== 'undefined' && ro.aggregates !== null) {
							html+='<tr><td><strong>Aggregated resources</strong></td><td><ul>';
							for(var j=0; j<ro.aggregates.length; j++){
								html+='<li><a href="'+ro.aggregates[j]+'">'+ro.aggregates[j]+'</a></li>';
							}
							html+='</ul></td></tr>';
						}
						if (typeof agents !== 'undefined' && agents !== null) {
							html+='<tr><td><strong>Creators</strong></td><td>';
							for(var j=0; j<agents.length; j++){
								homepage = agents[j].url;
								if(typeof agents[j].url == 'undefined' || agents[j].url == null){
									homepage='';
								}
								name = agents[j].name;
								if(typeof agents[j].name == 'undefined' || agents[j].name == null){
									name='author'+j;
								}
								html+='<a href="'+homepage+'">'+name+'</a><br/>';								
							}
							html+='</td></tr>';
						}
						html+='</tbody></table>';
						$("#roList").html(html);
						var rotitleAndURI = '<a href=\"'+ro.uri+'\">'+ro.title+'</a>';
						$('#roURI').html(rotitleAndURI);
						
						//html of the second table
						var htmlOtherProperties='<table class="col-sm-12"><tbody>'+htmlOther+'</tbody></table>';
						$('#propList').html(htmlOtherProperties);
					}
				}); 
			}
			
	</script>

  </head>
   <body>

    <div class="container">
      <nav class="navbar navbar-inverse" role="navigation">
            <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <a class="navbar-brand" href="index.html">Create ROs!</a>
              <ul class="nav navbar-nav">
				  <li><a href="roVisualizer.html">Browse ROs</a></li>
                  <li><a href="about.html">About</a></li>
              </ul>
            </div>
        </div>
      </nav>
      <!--<div>        
        <h3 class="text-muted">Browsing existing Research Objects</h3>
      </div>-->
	
	<div class="panel panel-default">
	  <div class="panel-body">
			<h1><span id="roURI">Research Object</span></h1>
			<br/>
              
		<div class="panel panel-primary" >
			<div class="panel-heading">Research Object key metadata</div>
			<div class="panel-body" id="roList">
			Loading Research Object metadata...
			</div>
		</div>
		<div class="panel panel-info" >
			<div class="panel-heading">Other Research Object properties</div>
			<div class="panel-body" id="propList">
			Loading Research Object properties...
			</div>
		</div>
       </div>
	</div>

      <footer class="footer">
        <p>&copy; Ontology Engineering Group, 2015</p>
      </footer>

    </div> <!-- /container -->
    
  </body>
</html>