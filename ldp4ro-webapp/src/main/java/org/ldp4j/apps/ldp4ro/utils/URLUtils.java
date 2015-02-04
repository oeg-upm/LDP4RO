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
package org.ldp4j.apps.ldp4ro.utils;

import org.ldp4j.apps.ldp4ro.listeners.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {

    private static final Logger logger = LoggerFactory.getLogger(URLUtils.class);

    private static final String PUBLISHER_PATH = "files";

    private URLUtils(){
        // Utility class
    }

    public static URL generateFileURL(String fileName) throws MalformedURLException {

        String base = ConfigManager.getBaseURL();

        String urlString = base + PUBLISHER_PATH + "/" + fileName;

        return new URL(urlString);

    }


}
