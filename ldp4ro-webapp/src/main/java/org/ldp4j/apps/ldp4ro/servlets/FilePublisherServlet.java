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

import org.apache.commons.io.FileUtils;
import org.ldp4j.apps.ldp4ro.listeners.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/***
 * This servlet serves the uploaded files from the file system.
 */
public class FilePublisherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FilePublisherServlet.class);

    /***
     *
     * @param request The http request. The last part of the request URI acts as the identifier of the file.
     * @param response The binary representation of the requested file.
     * @throws IOException if there is an error file reading the requested file
     */
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String requestURL = request.getRequestURL().toString();

        logger.debug("Received a GET request on '{}'", requestURL);

        String fileName = requestURL.substring(requestURL.lastIndexOf('/'), requestURL.length());
        logger.debug("Extracted the file name '{}'", fileName);

        File file = new File(ConfigManager.getFileUploadDir().getAbsolutePath() + File.separator + fileName);

        logger.debug("Resolved the absolute path '{}'", file.getAbsolutePath());

        //TODO we should try to find a way to set the content type properly

        if(file.exists()){
            FileUtils.copyFile(file, response.getOutputStream());

        } else {
            logger.debug("File '{}' does not exist.", file.getAbsolutePath());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

    }
}
