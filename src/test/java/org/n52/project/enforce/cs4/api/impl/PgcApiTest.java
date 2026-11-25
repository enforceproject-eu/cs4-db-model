package org.n52.project.enforce.cs4.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PgcApiTest {

    public PgcApiTest() {

        try {
            File file = new File("C:\\Users\\bpros\\Documents\\dev\\enforce\\git\\playas-geocoding-api\\src\\test\\resources\\org\\n52\\project\\enforce\\api\\playas\\geocoding\\util\\Playas-test.xlsx");

            InputStream responseInputStream =
                    requestPlayasGeocodingApi(new FileInputStream(file));

            FileOutputStream fileOutputStream =
                    new FileOutputStream(new File("C:\\Users\\bpros\\Documents\\tmp\\response.xlsx"));

            IOUtils.copy(responseInputStream, fileOutputStream);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PgcApiTest();
    }

    private InputStream requestPlayasGeocodingApi(InputStream originalInputStream) throws IOException {
        String playasGeocodingApiUrl = "http://localhost:8081/playas-geocoding-api/processes/geocode-playas/execution";
        // String playasGeocodingApiUrl =
        // environment.getProperty("playas.geocoding.api.url") +
        // "processes/geocode-playas/execution";
        
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode executeRequestNode = objectMapper.createObjectNode();
        ObjectNode inputs = objectMapper.createObjectNode();
        ObjectNode playasInput = objectMapper.createObjectNode();

        String base64EncodedContent = Base64.getEncoder().encodeToString(originalInputStream.readAllBytes());

        playasInput.put("value", base64EncodedContent);
        playasInput.put("encoding", "base64");
        playasInput.put("mediaType", "application/enforce-cs4-playas-data");

        inputs.set("playasInput", playasInput);

        executeRequestNode.set("inputs", inputs);

        HttpPost httpPost = new HttpPost(playasGeocodingApiUrl);

        httpPost.addHeader("Content-Type", "application/json");
        String executeRequestString = objectMapper.writeValueAsString(executeRequestNode);
//        System.out.println(executeRequestString);
        HttpEntity httpEntity = new StringEntity(executeRequestString);
        httpPost.setEntity(httpEntity);
        CredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope scope = new AuthScope("localhost", 8081);
        provider.setCredentials(scope , new UsernamePasswordCredentials("enforce", "bsdb3231&#25F"));
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider ).build();
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        return responseEntity.getContent();

    }

}
