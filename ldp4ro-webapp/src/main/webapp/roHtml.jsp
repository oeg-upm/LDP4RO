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
	<style>
	#myMenu a:link {
		color: #ffffff;
	}
	/* visited link grey */
	#myMenu a:visited {
		color: #ffffff;
	}
	/* mouse over link blue */
	#myMenuu a:hover {
		color: #ffffff;
	}
	</style>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	
    <script type="text/javascript">
			
			$(document).ready(function() {
				loadRO();
			});

			/**
			* Function to load the Research Objects. 
			**/
			var htmlOther;//used in getROMetadata
			function loadRO(rouri) {
				var ro ='<%=request.getParameter("uri")%>';//rouri;
				// An ajax request that requests the above URI and parses the response. 
				$.ajax( {
					headers: {'Accept': 'application/ld+json'},
					//jsonp :'callback',
					url :ro,
					success : function(json) {
						console.log(json);						
						//console.log("Success!. Number of ROs "+json.length);
						//$("#numberOfROs").html(json.contains.length);
						//loadROMetadata(json.contains);
						//iterate through the contents of the RO
						var returnedObjects= json['@graph'];
						var ro;
						var agents = [];
						htmlOther = '';
						for(var j=0; j<returnedObjects.length; j++){
								if(returnedObjects[j]['@type'] instanceof Array && 
									(returnedObjects[j]['@type'][0]=='ore:Aggregation') ||
									(returnedObjects[j]['@type'][0]=='ro:ResearchObject') ||
									(returnedObjects[j]['@type'][0]=='ldp:Container')){
										var obj = returnedObjects[j];
										ro = getROMetadata(obj);
									}
							//if it's an agent, add it as creator
							if(returnedObjects[j]['@type']=='dc:Agent'){									
								var ag = new Object();
								if(returnedObjects[j]['homepage'])ag.url = returnedObjects[j]['homepage'];
								if(returnedObjects[j]['foaf:name'])ag.name = returnedObjects[j]['foaf:name'];
								//console.log(ag.url+' ');
								agents.push(ag);
							}
						}
						//now that I have all the data, create the html and update it.
						var html='<table class="col-sm-12">'+
							'<tbody>';
						html+='<tr><td><strong>RO URI </strong></td><td><a href="'+ro.uri+'">'+ro.uri+'</a></td></tr>';
						if (ro.abstract) {
							html+='<tr><td><strong>Abstract</strong></td><td>'+ro.abstract+'</td></tr>';
						}
						if (ro.date) {
							html+='<tr><td><strong>Created on</strong></td><td>'+ro.date+'</td></tr>';
						}
						if (ro.license) {
							html+='<tr><td><strong>License</strong></td><td><a href="'+ro.license+'">'+ro.license+'</a></td></tr>';
						}
						if (ro.aggregates) {
							html+='<tr><td><strong>Aggregated resources</strong></td><td><ul>';
							if(ro.aggregates instanceof Array){
								for(var j=0; j<ro.aggregates.length; j++){
									html+='<li><a href="'+ro.aggregates[j]+'">'+ro.aggregates[j]+'</a></li>';
								}
							}else{
								html+='<li><a href="'+ro.aggregates+'">'+ro.aggregates+'</a></li>';
							}
							html+='</ul></td></tr>';
						}
						if (agents && agents.length>0) {
							html+='<tr><td><strong>Creators</strong></td><td>';
							for(var j=0; j<agents.length; j++){
								homepage = agents[j].url;
								if(typeof agents[j].url == 'undefined' || agents[j].url == null){
									homepage='';
								}
								name = agents[j].name;
								if(typeof agents[j].name == 'undefined' || agents[j].name == null){
									name='author '+j;
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
			
			function getROMetadata(obj){
				ro = new Object();
				for(var key in obj){
					var attrName = key;
					var attrValue = obj[key];
					//console.log(attrName+','+attrValue);
					//we iterate through the variables in order to get all of them
					if(key == 'abstract'){
						ro.abstract = attrValue;
					}else if(key == 'title'){
						ro.title = attrValue;
					}else if(key == 'dc:created'){
						ro.date = attrValue['@value'];
						if(ro.date == 'undefined' || ro.date == null){
							ro.date = attrValue;
						}
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
							//quick fix for expanding prefixes (the types are always the same).
							//console.log(aux);
							var aux = attrValue[k];
							if((typeof aux == 'string' || aux instanceof String) && aux.indexOf('ldp:')>-1){
								aux = aux.replace('ldp:','http://www.w3.org/ns/ldp#');
							}
							if((typeof aux == 'string' || aux instanceof String) && aux.indexOf('ro:')>-1){
								aux = aux.replace('ro:','http://purl.org/wf4ever/ro#');
							}
							if((typeof aux == 'string' || aux instanceof String) && aux.indexOf('ore:')>-1){
								aux = aux.replace('ore:','http://www.openarchives.org/ore/terms/');
							}
							htmlOther+='<li><a href="'+aux+'">'+aux+'</a></li>';
						}
						htmlOther+='</ul></td></tr>';
					}else{
						var aux = attrValue;
						//console.log(aux);
						if((typeof aux == 'string' || aux instanceof String) && aux.indexOf('ore:')>-1){
							aux = aux.replace('ore:','http://www.openarchives.org/ore/terms/');
						}
						if(aux instanceof Array){
							htmlOther+='<tr><td><strong>'+attrName+'</strong></td><td><ul>';
							for(var m=0; m<aux.length; m++){
								var aux2 = aux[m];
								if(typeof aux2 == 'string' || aux2 instanceof String){
									htmlOther+='<li>'+aux2+'</li>';
								}else{
									htmlOther+='<li>'+aux2['@id']+'</li>';//filter to remove [object] (schema:creator is special)
								}
							}
							htmlOther+='</ul></td></tr>';
						}else{
							if(typeof aux == 'string' || aux instanceof String){
								htmlOther+='<tr><td><strong>'+attrName+'</strong></td><td><a href="'+aux+'">'+aux+'</a></td></tr>';
							}							
						}
						
					}
				}
				return ro;
			}
			function getRDF(){
				//alert('you should be getting the rdf');
				var rdf = $.get('<%=request.getParameter("uri")%>'
					,"text/turtle").fail(function() {
							alert( "Error while downloading the RDF" );
					  }).done(function(data) {
						//console.log( data );
						var newWindow = window.open("", "RDF Download");
						newWindow.document.write(data);
					  });
							/*console.log (rdf['responseText']);
						  var newWindow = window.open("", "RDF Download");
						  newWindow.document.write(rdf['responseText']);*/
						//});
				console.log(rdf);
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
				  <li><a href="roVisualizer.jsp">Browse ROs</a></li>
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
			<div class="panel-heading" id="myMenu">Research Object key metadata <a href="#" onclick="getRDF()" class="pull-right">Download RDF</a></div><!--<img src="rdf.gif" height="33" width="33"></img></a></div>-- class="pull-right" -->
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