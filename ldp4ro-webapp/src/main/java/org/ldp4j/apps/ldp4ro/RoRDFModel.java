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
package org.ldp4j.apps.ldp4ro;

import com.google.common.base.Joiner;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.ldp4j.apps.ldp4ro.frontend.RoFormElement;
import org.ldp4j.apps.ldp4ro.vocab.LDP;
import org.ldp4j.apps.ldp4ro.vocab.ORE;
import org.ldp4j.apps.ldp4ro.vocab.RO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RoRDFModel {

    private static final Logger logger = LoggerFactory.getLogger(RoRDFModel.class);

    Model model;

    Resource ro;

    Map<String, String[]> parameterMap;

    String[] authorNames;

    String[] authorURIs;

    public RoRDFModel(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
        createBaseModel();
    }

    public Model process(){

        for (String key : parameterMap.keySet()) {

            RoFormElement element;
            try {
                element = RoFormElement.fromString(key);
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown form element - '{}'", key);
                continue;
            }


            String[] values = parameterMap.get(key);

/*            //Fixing the encoding issue reported at https://github.com/oeg-upm/LDP4RO/issues/29
            TODO fix the encoding problem properly
            String[] rawValues = parameterMap.get(key);
            String[] values = new String[rawValues.length];

            logger.trace("Processing the form parameters ...");

            for (int i = 0; i < rawValues.length; i++) {
                try {
                    values[i] = new String(rawValues[i].getBytes("ISO-8859-1"), "UTF-8");
                    logger.trace("Original:" + key + "=" + rawValues[i]);
                    logger.trace("Decoded:" +key + "=" + values[i]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    //for this parameter and use the original value.
                    values[i] = rawValues[i];
                }
            }*/

            switch (element) {
                case TITLE:
                    generateTitle(values);
                    break;
                case ABSTRACT:
                    generateAbstract(values);
                case CREATOR_NAME:
                    authorNames = values;
                    break;
                case CREATOR_URI:
                    authorURIs = values;
                    break;
                case DATE:
                    generateDate(values);
                    break;
                case EXTERNAL_URI:
                    generateExternalURIs(values);
                    break;
                case RESOURCE_URI:
                    generateExternalURIs(values);
                    break;
                case LICENSE:
                    generateLicense(values);
                    break;

            }

        }

        generateAuthors();

        return model;

    }


    private void createBaseModel() {

        model = ModelFactory.createDefaultModel();
        model.setNsPrefix("dc", "http://purl.org/dc/terms/");
        model.setNsPrefix("ore", "http://www.openarchives.org/ore/terms/");
        model.setNsPrefix("ro", "http://purl.org/wf4ever/ro#");
        model.setNsPrefix("xhv", "http://www.w3.org/1999/xhtml/vocab#");
        model.setNsPrefix("ldp", LDP.NS);
        model.setNsPrefix("foaf", FOAF.NS);


        Resource rm = model.createResource("");
        rm.addProperty(RDF.type, ORE.ResourceMap);

        ro = model.createResource("#agr");
        ro.addProperty(RDF.type, RO.ResearchObject);
        ro.addProperty(RDF.type, ORE.Aggregation);
        ro.addProperty(RDF.type, LDP.Container);
        ro.addProperty(RDF.type, LDP.DirectContainer);
        ro.addProperty(LDP.membershipResource, ro);
        ro.addProperty(LDP.hasMemberRelation, ORE.aggregates);
        ro.addProperty(ORE.isDescribedBy, rm);


        logger.trace("Created the basic model and the RO resource");

    }

    private void generateTitle(String[] title) {

        if (title.length == 0) {
            throw new IllegalArgumentException("There must exactly one title. None found.");
        } else if (title.length > 1) {
            throw new IllegalArgumentException("There must exactly one title. Multiple titles found.");
        }

        ro.addProperty(DCTerms.title, title[0]);

    }

    private void generateAbstract(String[] abstract_) {
        if (abstract_.length > 0) {
            ro.addProperty(DCTerms.abstract_, abstract_[0]);
        }
    }

    private void generateAuthors(){

        if(authorNames == null || authorURIs == null || authorNames.length != authorURIs.length){
            throw new IllegalArgumentException("Author names and author URIs do not match. " +
                    Joiner.on(",").join(authorNames) + Joiner.on(",").join(authorURIs));
        }

        for (int i = 0; i < authorNames.length; i++) {
            Resource author = model.createResource();
            author.addProperty(RDF.type, DCTerms.Agent);
            author.addLiteral(FOAF.name, authorNames[i]);
            author.addProperty(FOAF.homepage,  model.createResource(authorURIs[i]));
            ro.addProperty(DCTerms.creator, author);
        }

    }

    private void generateExternalURIs(String[] externalURIs) {
        for (String uri : externalURIs) {
            if (uri != null && !"".equals(uri)) {
                ro.addProperty(ORE.aggregates, uri);
            }
        }
    }

    private void generateLicense(String[] licenses){
        Property license = model.createProperty("http://www.w3.org/1999/xhtml/vocab#license");
        for (String uri : licenses) {
            if (uri != null && !"".equals(uri)) {
                ro.addProperty(license, uri);
            }
        }

    }

    private void generateDate(String[] date){
        if (date.length == 1) {
            ro.addLiteral(DCTerms.created, ResourceFactory.createTypedLiteral(date[0], XSDDatatype.XSDdate));
        } else if (date.length > 0) {
            throw new IllegalArgumentException("There must zero or one date. Multiple dates found.");
        }
    }


}
