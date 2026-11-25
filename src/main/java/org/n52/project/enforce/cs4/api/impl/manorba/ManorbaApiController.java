package org.n52.project.enforce.cs4.api.impl.manorba;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.n52.project.enforce.cs4.api.BaseController;
import org.n52.project.enforce.cs4.api.ManorbaApi;
import org.n52.project.enforce.cs4.api.impl.minka.Cs4MinkaDataRepository;
import org.n52.project.enforce.cs4.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2025-12-04T07:49:15.855249900+01:00[Europe/Berlin]",
        comments = "Generator version: 7.13.0")
@Controller
@SecurityRequirement(
        name = "basicAuth")
@RequestMapping("${openapi.eNFORCEDataAccess.base-path:}")
public class ManorbaApiController extends BaseController implements ManorbaApi {

    @Autowired
    public ManorbaApiController(Cs4MinkaDataRepository dataRepository, Utils utils,
            CS4PlayasDataRepository cs4PlayasDataRepository, Environment environment) {
        super(dataRepository, utils, cs4PlayasDataRepository, environment);
    }

    @Override
    public ResponseEntity<Serializable> addCs4ManorbaDataAsBody(@Valid Boolean sanitizeLocations,
            @Valid Resource body) {

        try {
            InputStream inputStream = body.getInputStream();

            if (sanitizeLocations != null && sanitizeLocations) {
                inputStream = requestPlayasGeocodingApi(inputStream);
            }
            utils.readExcelFile(inputStream);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Serializable> getCs4ManorbaData(@Valid BigDecimal limit) {
        try {
            return ResponseEntity.ok(cs4PlayasDataRepository.getGeoJson());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private InputStream requestPlayasGeocodingApi(InputStream originalInputStream) throws IOException {
        String playasGeocodingApiUrl =
                environment.getProperty("playas.geocoding.api.url") + "processes/geocode-playas/execution";

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
        HttpEntity httpEntity = new StringEntity(executeRequestString);
        httpPost.setEntity(httpEntity);
        CredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope scope = new AuthScope("localhost", 8081);
        provider.setCredentials(scope, new UsernamePasswordCredentials("enforce", "bsdb3231&#25F"));
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        return responseEntity.getContent();

    }

}
