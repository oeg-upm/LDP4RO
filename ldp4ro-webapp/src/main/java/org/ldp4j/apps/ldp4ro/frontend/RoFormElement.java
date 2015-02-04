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
package org.ldp4j.apps.ldp4ro.frontend;

public enum RoFormElement {
    ABSTRACT, TITLE, CREATOR_NAME, CREATOR_URI, EXTERNAL_URI, RESOURCE_URI, DATE, LICENSE;

    public static RoFormElement fromString(String string) {
        if ("abstract".equals(string)) {
            return RoFormElement.ABSTRACT;
        } else if ("title".equals(string)) {
            return RoFormElement.TITLE;
        } else if ("creatorName[]".equals(string)) {
            return RoFormElement.CREATOR_NAME;
        } else if ("creatorURI[]".equals(string)) {
            return RoFormElement.CREATOR_URI;
        } else if ("externalURI[]".equals(string)) {
            return RoFormElement.EXTERNAL_URI;
        } else if ("resourceURI[]".equals(string)){
            return RoFormElement.RESOURCE_URI;
        } else if ("date".equals(string)) {
            return RoFormElement.DATE;
        } else if ("license".equals(string)) {
            return RoFormElement.LICENSE;
        } else {
            throw new IllegalArgumentException("Unknown form element - '" + string + "'");
        }
    }

}
