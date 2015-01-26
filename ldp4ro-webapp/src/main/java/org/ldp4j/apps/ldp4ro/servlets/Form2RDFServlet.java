/*
 * Copyright 2014 Ontology Engineering Group, Universidad Politécnica de Madrid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.ldp4j.apps.ldp4ro.servlets;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ldp4j.apps.ldp4ro.RoRDFModel;
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

        String requestURL = request.getRequestURL().toString();
        logger.debug("Received a POST request on '{}'", requestURL);

        Map<String, String[]> parameterMap = request.getParameterMap();

        RoRDFModel ro = new RoRDFModel(parameterMap);

        Model model = ro.process();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, "TURTLE");
        String roString = os.toString();

        logger.debug("Form data is converted to RDF ... \n{}", roString);


        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://localhost:8080/ldp4j/ldp-bc/");
        //HttpPost post = new HttpPost("http://linkeddata4.dia.fi.upm.es:8088/ldp4j/ldp-bc/");

        StringEntity body = new StringEntity(roString);
        body.setContentType("text/turtle");
        post.setEntity(body);

        HttpResponse ldpResponse = httpclient.execute(post);

        try {
            int statusCode = ldpResponse.getStatusLine().getStatusCode();
            logger.debug("LDP server responded with {} {}", statusCode,
                    ldpResponse.getStatusLine().getReasonPhrase());

            if(statusCode == 201 && ldpResponse.getFirstHeader("Location") != null) {
                String location = ldpResponse.getFirstHeader("Location").getValue();
                logger.debug("URI of the newly created LDPR - {}", location);

                request.setAttribute("newURI", location);

                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/roCreated.jsp");
                dispatcher.forward(request, response);

            } else {
                logger.error("An error occurred while creating the RO. {} {}", statusCode,
                        ldpResponse.getStatusLine().getReasonPhrase());
            }

        } finally {
            post.releaseConnection();
        }

    }

}
