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
package org.ldp4j.apps.ldp4ro.listeners;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

import static org.ldp4j.apps.ldp4ro.listeners.ConfigManager.ConfigParam.UPLOAD_DIR;

public class ConfigManager implements ServletContextListener  {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private static String baseURL;

    private static Config config;

    private static File uploadDir;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        logger.debug("ConfigManager is initializing ...");

        logger.debug("Loading the configuration ...");

        config = ConfigFactory.load();

        //logger.trace("Application Config: \n {}", config.root().render());

        //Set the encoding to UTF-8 for handling international characters
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("javax.servlet.request.encoding", "UTF-8");

        Config appConfig = ConfigManager.getAppConfig();
        uploadDir = new File(appConfig.getString(UPLOAD_DIR));

        logger.debug("File upload directory is set to '{}'", uploadDir.getAbsolutePath());

        if (!uploadDir.exists()) {
            logger.debug("File upload directory does not exist.");
            uploadDir.mkdir();
            logger.debug("File upload directory is created at '{}'", uploadDir.getAbsolutePath());
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    /***
     * Returns the application configuration.
     * @return application configuration
     */
    public static Config getAppConfig() {
        if(config == null) {
            throw new IllegalStateException("Application configuration is not correctly initialized");
        }
        return config;
    }

    public static File getFileUploadDir() {
        return uploadDir;
    }

    /***
     * Class that holds the names of the configuration parameters of the application configuration file
     */
    public class ConfigParam {

        public static final String MEMORY_THRESHOULD = "FileUpload.memory-threshold";

        public static final String MAX_FILE_SIZE = "FileUpload.max-file-size";

        public static final String MAX_REQUEST_SIZE = "FileUpload.max-request-size";

        public static final String UPLOAD_DIR = "FileUpload.upload-dir";

    }

    public static void setBaseURL(String url){
        baseURL = url;
    }

    /***
     * Returns the base URL of the application.
     * @return baseURL of the web application
     * @throws java.lang.IllegalStateException if the base URL is not set at the time
     */
    public static String getBaseURL(){
        if (baseURL ==null) {
            throw new IllegalStateException("Base URL of the application is not set.");
        }
        return baseURL;
    }

    /***
     * Checks whether the base URL is set or not.
     * @return true if the base URL is available and falls otherwise.
     */
    public static boolean isBaseURLSet(){
        return baseURL != null;
    }
}
