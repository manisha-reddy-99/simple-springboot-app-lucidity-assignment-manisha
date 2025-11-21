package com.springboot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import com.springboot.controller.SegmentResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CartOfferTestUtils {

    // Static URLs for easy reuse
    public static final String BASE_URL = "http://localhost:9001/api/v1";
    public static final String SEGMENT_URL = "http://localhost:1080/api/v1/user_segment";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean addOffer(OfferRequest offerRequest) throws Exception {
        URL url = new URL(BASE_URL + "/offer");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        String POST_PARAMS = mapper.writeValueAsString(offerRequest);

        try (OutputStream os = con.getOutputStream()) {
            os.write(POST_PARAMS.getBytes());
            os.flush();
        }

        return con.getResponseCode() == HttpURLConnection.HTTP_OK;
    }


    public static int applyOffer(int cartValue, int restaurantId, int userId) throws Exception {
        URL url = new URL(BASE_URL + "/cart/apply_offer");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        // Prepare request JSON
        String requestJson = String.format(
                "{\"cart_value\": %d, \"restaurant_id\": %d, \"user_id\": %d}",
                cartValue, restaurantId, userId
        );

        try (OutputStream os = con.getOutputStream()) {
            os.write(requestJson.getBytes());
            os.flush();
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String response = reader.readLine();
        reader.close();

        // Map to response class
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, ApplyOfferResponse.class).getCart_value();
    }


    public static Optional<String> getUserSegment(int userId) throws Exception {
        URL url = new URL(SEGMENT_URL + "?user_id=" + userId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? con.getErrorStream() : con.getInputStream()
        ));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        if (responseCode == 200) {
            SegmentResponse segResp = mapper.readValue(response.toString(), SegmentResponse.class);
            if (segResp.getSegment() == null || segResp.getSegment().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(segResp.getSegment());
        } else if (responseCode == 404) {
            return Optional.empty(); // user not found
        } else {
            throw new RuntimeException("Failed to fetch user segment for user " + userId + " | HTTP code: " + responseCode);
        }
    }

    public static List<String> getSegmentsOrDefault(int userId) throws Exception {
        Optional<String> segmentOpt = getUserSegment(userId);
        return segmentOpt.map(s -> Arrays.asList(s.split(","))).orElse(new ArrayList<>());
    }

    public static boolean addOfferWithoutAuth(OfferRequest offerRequest) throws Exception {
         URL url = new URL(BASE_URL + "/offer");
         HttpURLConnection con = (HttpURLConnection) url.openConnection(); con.setDoOutput(true); // intentionally no auth headers
         String POST_PARAMS = mapper.writeValueAsString(offerRequest);
         try (OutputStream os = con.getOutputStream()) {
             os.write(POST_PARAMS.getBytes()); os.flush();
         } return con.getResponseCode() == HttpURLConnection.HTTP_OK;
    }
}
