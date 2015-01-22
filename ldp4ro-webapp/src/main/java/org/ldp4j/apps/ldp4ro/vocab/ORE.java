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

package org.ldp4j.apps.ldp4ro.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ORE {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();

    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.openarchives.org/ore/terms/";

    /** <p>A set of related resources (Aggregated Resources), grouped together such that the set can be treated as a single resource. This is the entity described within the ORE interoperability framework by a Resource Map. </p> */
    public static final Resource Aggregation = m_model.createResource( "http://www.openarchives.org/ore/terms/Aggregation" );

    /** <p> A description of an Aggregation according to the OAI-ORE data model. Resource Maps are RDF graphs, and are serialised to a machine readable format according to the implementation guidelines. </p> */
    public static final Resource ResourceMap = m_model.createResource( "http://www.openarchives.org/ore/terms/ResourceMap" );

    public static final Property aggregates = m_model.createProperty( "http://www.openarchives.org/ore/terms/aggregates" );

    public static final Property isDescribedBy = m_model.createProperty( "http://www.openarchives.org/ore/terms/isDescribedBy" );


}
