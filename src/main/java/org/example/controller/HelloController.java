package org.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String UPDATE_COUNT_SQL = "UPDATE howmany SET count = count + 1";
    private static final String SELECT_COUNT_SQL = "select count from howmany";

    @GetMapping("/hello")
    @ResponseBody
    public Map<String, Object> sayHello() {
        jdbcTemplate.update(UPDATE_COUNT_SQL);
        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_SQL, Integer.class);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, World " + count + "th visitor");
        response.put("count", count);
        return response;
    }

    @GetMapping("/search")
    public String searchChar() {
        return "hello";
    }

    @GetMapping("/getOcid")
    public String getOcid(@RequestParam("characterName") String characterName, Model model) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("x-nxopen-api-key", "test_c3c7513e49d03f4c0d14389cf14e274f5504d6b6b0f373662f022b8f07304e4b356397c41c1a3eef638d09601b2f4f18");

        // Build URL
        String apiUrl = "https://open.api.nexon.com/maplestory/v1/id?character_name=" + characterName;

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make the GET request
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        // Check if response is successful (status code 200)
        if (response.getStatusCode().is2xxSuccessful()) {
            // Extract OCID from JSON response
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("ocid")) {
                String ocid = responseBody.get("ocid").toString();

                HttpHeaders headers2 = new HttpHeaders();
                headers2.set("Accept", "application/json");
                headers2.set("x-nxopen-api-key", "test_c3c7513e49d03f4c0d14389cf14e274f5504d6b6b0f373662f022b8f07304e4b356397c41c1a3eef638d09601b2f4f18");

                // Build URL
                String apiUrl2 = "https://open.api.nexon.com/maplestory/v1/character/basic?ocid=" + ocid;

                // Create a new RestTemplate instance
                RestTemplate restTemplate2 = new RestTemplate();

                // Make the GET request
                HttpEntity<String> entity2 = new HttpEntity<>(headers);
                ResponseEntity<Map> response2 = restTemplate2.exchange(apiUrl2, HttpMethod.GET, entity2, Map.class);

                // Check if response is successful (status code 200)
                if (response2.getStatusCode().is2xxSuccessful()) {
                    // Extract OCID from JSON response
                    Map<String, Object> responseBody2 = response2.getBody();
                    if (responseBody2 != null && responseBody2.containsKey("character_class")) {
                        String character_class = responseBody2.get("character_class").toString();
                        String character_class_level = responseBody2.get("character_class_level").toString();
                        String character_level = responseBody2.get("character_level").toString();
                        model.addAttribute("character_class", character_class);
                        model.addAttribute("character_class_level", character_class_level);
                        model.addAttribute("character_level", character_level);
                    }
                } else {
                    // Handle error response
                    System.err.println("Error fetching OCID: " + response.getStatusCode());
                    // Optionally, handle error display in the HTML
                }
            }
        } else {
            // Handle error response
            System.err.println("Error fetching OCID: " + response.getStatusCode());
            // Optionally, handle error display in the HTML
        }


        // //////////////////////////////////////////////////////////////



        // Return the Thymeleaf template to render (hello.html)
        return "hello";
    }
}
