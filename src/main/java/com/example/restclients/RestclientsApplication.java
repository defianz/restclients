package com.example.restclients;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Map;

@SpringBootApplication
public class RestclientsApplication {

    @Bean
    ApplicationRunner init(ErApi api) {
        return args -> {
            // https://open.er-api.com/v6/latest
            RestTemplate rt = new RestTemplate();
            Map<String, Map<String, Double>> res = rt.getForObject("https://open.er-api.com/v6/latest", Map.class);
            System.out.println(res.get("rates").get("KRW"));

            WebClient client = WebClient.create("https://open.er-api.com");
            Map<String, Map<String, Double>> res2 = client.get().uri("/v6/latest").retrieve().bodyToMono(Map.class).block();
            System.out.println(res2.get("rates").get("KRW"));

            WebClientAdapter adapter = WebClientAdapter.create(client);
            HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

            ErApi erApi = httpServiceProxyFactory.createClient(ErApi.class);
            Map<String, Map<String, Double>> res3 = erApi.getLatest();
            System.out.println(res3.get("rates").get("KRW"));

            Map<String, Map<String, Double>> res4 = api.getLatest();
            System.out.println(res4.get("rates").get("KRW"));

            RestClient restClient = RestClient.create("https://open.er-api.com");
            Map<String, Map<String, Double>> res5 = restClient.get().uri("/v6/latest").retrieve().body(Map.class);
            System.out.println(res5.get("rates").get("KRW"));

        };
    }


    @Bean
    ErApi erApi() {
        RestClient restClient = RestClient.builder().baseUrl("https://open.er-api.com").build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return httpServiceProxyFactory.createClient(ErApi.class);
    }


    interface ErApi {
        @GetExchange("/v6/latest")
        Map getLatest();
    }

    public static void main(String[] args) {
        SpringApplication.run(RestclientsApplication.class, args);
    }

}
