package com.kamarkaka.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import org.brotli.dec.BrotliInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamarkaka.commons.exceptions.FetchErrorException;
import com.kamarkaka.commons.exceptions.ParseErrorException;

import kotlin.Pair;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** a custom http client */
public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    /** config keys */
    private static final String CONFIG_CONNECTION_TIMEOUT = "http.connection.timeout.sec";
    private static final String CONFIG_READ_TIMEOUT = "http.read.timeout.sec";
    private static final String CONFIG_WRITE_TIMEOUT = "http.write.timeout.sec";

    /** http client */
    private final OkHttpClient client;

    /** constructor, sets up an OkHttpClient with configs */
    public HttpClient() {
        // maximum number of seconds to establish a connection to server
        int connectionTimeout = KProperties.getInt(CONFIG_CONNECTION_TIMEOUT);

        // maximum number of seconds to download data from server
        int readTimeout = KProperties.getInt(CONFIG_READ_TIMEOUT);

        // maximum number of seconds to upload data to server
        int writeTimeout = KProperties.getInt(CONFIG_WRITE_TIMEOUT);

        this.client = new OkHttpClient().newBuilder()
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .build();
    }

    /** build a GET request without headers */
    public Request buildGetRequest(String endpoint) {
        return buildGetRequest(endpoint, new ArrayList<>());
    }

    /** build a GET request with custom headers */
    public Request buildGetRequest(String endpoint, List<Pair<String, String>> headers) {
        Request.Builder requestBuilder = initRequestBuilder(endpoint);

        if (headers != null && !headers.isEmpty()) {
            requestBuilder = addHeaders(requestBuilder, headers);
        }

        return requestBuilder.get().build();
    }

    /** build a GET request with custom headers */
    public Request buildGetRequest(String endpoint, Headers headers) {
        Request.Builder requestBuilder = initRequestBuilder(endpoint);
        requestBuilder.headers(headers);
        return requestBuilder.get().build();
    }

    /** build a POST request with custom headers and a JSON body */
    public Request buildJsonPostRequest(String endpoint, List<Pair<String, String>> headers, String jsonString) {
        Request.Builder requestBuilder = initRequestBuilder(endpoint);
        requestBuilder = addHeaders(requestBuilder, headers);
        return buildJsonRequest(requestBuilder, jsonString);
    }

    /** build a POST request with custom headers and a x-www-form body */
    public Request buildFormPostRequest(String endpoint, List<Pair<String, String>> headers, List<Pair<String, String>> formData) {
        Request.Builder requestBuilder = initRequestBuilder(endpoint);
        requestBuilder = addHeaders(requestBuilder, headers);
        return buildFormRequest(requestBuilder, formData);
    }

    /** send request and read response data */
    public ResponseData execute(Request request) throws FetchErrorException {
        logger.debug("Sending {} request to {} ...", request.method(), request.url());

        try (Response response = this.client.newCall(request).execute()) {
            logger.debug("Successfully received response (code: {}, type: {}, length: {})", response.code(), response.header("Content-Type"), response.header("Content-Length", "0"));

            if (!response.isSuccessful()) {
                throw new FetchErrorException("Error receiving response, error code " + response.code());
            }

            Headers headers = response.headers();
            String contentEncodingHeader = headers.get("Content-Encoding");
            InputStream inputStream = response.body().byteStream();

            if (contentEncodingHeader != null) {
                if (contentEncodingHeader.toLowerCase().indexOf("gzip") >= 0) {
                    logger.debug("Response has gzip encoding...");
                    inputStream = new GZIPInputStream(inputStream);
                } else if (contentEncodingHeader.toLowerCase().indexOf("br") >= 0) {
                    logger.debug("Response has br encoding...");
                    inputStream = new BrotliInputStream(inputStream);
                } else if (contentEncodingHeader.toLowerCase().indexOf("deflate") >= 0) {
                    logger.debug("Response has deflate encoding...");
                    inputStream = new DeflaterInputStream(inputStream);
                }
            }

            String responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            logger.debug("Successfully read all {} bytes from response", responseBody.length());

            return new ResponseData(response.code(), headers, responseBody);
        } catch(IOException ex) {
            throw new FetchErrorException("Error receiving response from " + request.url(), ex);
        }
    }

    public JsonNode toJsonNode(String responseJsonString) throws ParseErrorException {
        try {
            return new ObjectMapper().readTree(responseJsonString);
        } catch (JsonProcessingException ex) {
            throw new ParseErrorException("Error parsing JSON", ex);
        }
    }

    /** send request and download content as file */
    public File downloadFile(Request request, Path path) throws FetchErrorException {
        logger.debug("Downloading {} to {} ...", request.url(), path);

        try (Response response = this.client.newCall(request).execute()) {
            logger.debug("Successfully received response (code: {}, type: {}, length: {})", response.code(), response.header("Content-Type"), response.header("Content-Length", "0"));

            if (!response.isSuccessful()) {
                throw new FetchErrorException("Error receiving response, error code " + response.code());
            }

            try (InputStream in = response.body().byteStream()) {
                String fileName = getFileName(request, response);
                File file = Paths.get(path.toString(), fileName).toFile();
                logger.debug("Saving response contents to file {} ...", file);

                try (OutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                logger.debug("Successfully downloaded to {}", file);
                return file;
            }
        } catch(IOException ex) {
            throw new FetchErrorException("Error downloading from " + request.url(), ex);
        }
    }

    /** initialize a new request builder */
    private Request.Builder initRequestBuilder(String endpoint) {
        return new Request.Builder().url(endpoint);
    }

    /** add headers to request builder */
    private Request.Builder addHeaders(Request.Builder builder, List<Pair<String, String>> headers) {
        if (headers == null) {
            headers = new ArrayList<>();
        }

        for (Pair<String, String> header : headers) {
            builder = builder.addHeader(header.getFirst(), header.getSecond());
        }
        return builder;
    }

    /** build a json request */
    private Request buildJsonRequest(Request.Builder builder, String jsonString) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(jsonString, mediaType);

        return builder
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build();
    }

    /** build a x-www-form request */
    private Request buildFormRequest(Request.Builder builder, List<Pair<String, String>> formData) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Pair<String, String> pair : formData) {
            formBodyBuilder = formBodyBuilder.add(pair.getFirst(), pair.getSecond());
        }
        RequestBody requestBody = formBodyBuilder.build();

        return builder
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build();
    }

    private String getFileName(Request request, Response response) {
        String fileName = null;
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        requests.add(response.request());

        for (Request req : requests) {
                            String url = req.url().toString();
                            int beginIndex = url.lastIndexOf("/");
                            int endIndex = url.indexOf("?", beginIndex);

            if (endIndex > beginIndex) {
                                fileName = url.substring(beginIndex, endIndex);
                            } else {
                                fileName = url.substring(beginIndex);
                            }

                            if (fileName.endsWith(".exe") ||
                                fileName.endsWith(".msi") ||
                                fileName.endsWith(".zip") ||
                                fileName.endsWith(".deb") ||
                                fileName.endsWith(".7z")) {
                                return fileName;
                            }
                        }

        String content = response.header("Content-Disposition");
        if (content  != null) {
            Pattern pattern = Pattern.compile("filename=\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return fileName;
    }
}
