package org.ldp4j.apps.ldp4ro.elasticsearch;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.ldp4j.apps.ldp4ro.RoRDFModel;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ESClient {

    private Client client;

    public ESClient(String ip, int port, String cluster){

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", cluster).build();

        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(ip, port));

    }


    public String index(String index, String type, XContentBuilder content) {

        IndexResponse response = client.prepareIndex(index, type)
                .setSource(content)
                .execute()
                .actionGet();

        return response.getId();

    }

    public XContentBuilder createIndex(RoRDFModel model, String uri) throws IOException {

        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("uri", uri)
                .field("title", model.getTitle())
                .field("abstract", model.getAbstract());

        String[] authorNames = model.getAuthorNames();
        if (authorNames.length > 0) {
            builder.array("authors", authorNames);
        }

        return builder.endObject();

    }

    public boolean close(){
        if(client != null) {
            client.close();
            return true;
        } else {
            return false;
        }
    }
}
