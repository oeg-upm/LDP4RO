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

import com.typesafe.config.Config;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ldp4j.apps.ldp4ro.listeners.ConfigManager;
import org.ldp4j.apps.ldp4ro.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import static org.ldp4j.apps.ldp4ro.listeners.ConfigManager.ConfigParam.*;

public class FileUploaderServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FileUploaderServlet.class);


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        logger.debug("FileUploaderServlet is initialized");
    }

    /**
     * Upon receiving file upload submission, parses the request to read
     * upload data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        logger.debug("Received a POST request on '{}'", request.getRequestURL());

        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, send an error message
            sendMessage("Not a multipart request", response);
            return;
        }

        // Configures upload settings
        ServletFileUpload upload = getFileItemFactory();

        // parses the request's content to extract file data
        @SuppressWarnings("unchecked")
        List<FileItem> formItems = null;
        try {
            formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                String fileName = null;
                File storeFile = null;

                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();

                        logger.trace("Processing the file upload '{}'", fileName);

                        String filePath = ConfigManager.getFileUploadDir().getAbsolutePath() + File.separator + fileName;
                        storeFile = new File(filePath);

                        // We don't want to override existing files. If there is a file with the same name already,
                        // we will just prefix the file name with a number
                        int counter = 0;
                        String origFileName = fileName;
                        while(storeFile.exists()) {
                            fileName=  counter++ + origFileName;
                            logger.trace("File with the name  '{}' already exists. Trying out the new name '{}'", storeFile.getAbsolutePath(), fileName);
                            filePath = ConfigManager.getFileUploadDir().getAbsolutePath() + File.separator + fileName;
                            storeFile = new File(filePath);
                        }
                        item.write(storeFile);
                        logger.trace("Successfully written the file at '{}'", storeFile.getAbsolutePath());
                    }


                }

                if (fileName == null || storeFile == null)  {
                    sendMessage("Error occurred while uploading the file ...", response);
                    return;
                }

                URL url = URLUtils.generateFileURL(fileName);

                // Preparing the response
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setHeader("Location", url.toExternalForm());
                sendMessage(url.toExternalForm(), response);

            }

        } catch (FileUploadException e) {
            throw new ServletException("Error parsing upload request ...", e);
        } catch (Exception e) {
            throw new ServletException("Error occurred while uploading the file ...", e);
        }
    }


    private void sendMessage(String msg, HttpServletResponse response) throws IOException {

        PrintWriter writer = response.getWriter();
        writer.println(msg);
        writer.flush();

    }

    private ServletFileUpload getFileItemFactory(){

        Config config = ConfigManager.getAppConfig();

        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(config.getInt(MEMORY_THRESHOULD));

        // sets temporary location to store files
        File tempUploadDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tempUploadDir.exists()) {
            tempUploadDir.mkdir();
        }
        factory.setRepository(tempUploadDir);

        ServletFileUpload upload = new ServletFileUpload(factory);

        // sets maximum size of upload file
        upload.setFileSizeMax(config.getLong(MAX_FILE_SIZE));

        // sets maximum size of request (include file + form data)
        upload.setSizeMax(config.getLong(MAX_REQUEST_SIZE));

        return upload;

    }
}
