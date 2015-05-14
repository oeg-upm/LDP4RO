/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid (http://www.oeg-upm.net/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ldp4j.apps.ldp4ro.servlets;

import com.hp.hpl.jena.rdf.model.Model;
import com.typesafe.config.Config;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ldp4j.apps.ldp4ro.RoRDFModel;
import org.ldp4j.apps.ldp4ro.elasticsearch.ESClient;
import org.ldp4j.apps.ldp4ro.listeners.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/***
 * This servlet handles the conversion of form data from the HTML frontend to RDF (using RO ontology) to be
 * submitted to an LDP server
 */
public class Form2RDFServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(Form2RDFServlet.class);

    /**
     * Upon receiving data from the RO creation form,
     * 1. parses the request to extract the form data
     * 2. converts them to RDF to generate a RO
     * 3. submit that RO to an LDP server that manages the RO
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {


        request.setCharacterEncoding("UTF-8");

        String requestURL = request.getRequestURL().toString();
        logger.debug("Received a POST request on '{}'", requestURL);

        Map<String, String[]> parameterMap = request.getParameterMap();

        RoRDFModel ro = new RoRDFModel(parameterMap);

        Model model = ro.process();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, "TURTLE");
        String roString = os.toString();

        logger.debug("Form data is converted to RDF ... \n{}", roString);


        String containerPath = ConfigManager.getAppConfig().getString("ldp4j.container-url");
        logger.debug("Container path is '{}'", containerPath);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(containerPath);

        ByteArrayEntity body = new ByteArrayEntity(roString.getBytes("UTF-8"));

        body.setContentType("text/turtle; charset=utf-8");
        body.setContentEncoding("UTF-8");
        post.setEntity(body);

        HttpResponse ldpResponse = httpclient.execute(post);

        ESClient esClient = null;
        try {
            int statusCode = ldpResponse.getStatusLine().getStatusCode();
            logger.debug("LDP server responded with {} {}", statusCode,
                    ldpResponse.getStatusLine().getReasonPhrase());

            if(statusCode == 201 && ldpResponse.getFirstHeader("Location") != null) {
                String location = ldpResponse.getFirstHeader("Location").getValue();
                logger.debug("URI of the newly created LDPR - {}", location);

                esClient = new ESClient("127.0.0.1", 9300, "elasticsearch");
                esClient.index("roindex", "ro", esClient.createIndex(ro, location));

                request.setAttribute("newURI", location);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/roCreated.jsp");
                dispatcher.forward(request, response);

            } else {
                logger.error("An error occurred while creating the RO. {} {}", statusCode,
                        ldpResponse.getStatusLine().getReasonPhrase());
            }

        } finally {
            post.releaseConnection();
            if(esClient != null){
                esClient.close();
            }
        }

    }

}
