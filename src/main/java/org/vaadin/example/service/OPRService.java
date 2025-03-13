package org.vaadin.example.service;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OPRService {
    RestTemplate restTemplate = new RestTemplate();

    @SneakyThrows
    public Map<String, String> getArmies() {
        String uri = "https://wiki.onepagerules.com/index.php/Category:Grimdark_Future_Miniatures"; // or any other uri

        Document doc = Jsoup.connect(uri).get();
        return doc
            .select("div[class=mw-category-group]")
            .select("a[href*=Miniatures]").stream()
            .collect(Collectors.toMap(Element::text, element -> element.attr("href")));
    }

    @SneakyThrows
    public String getUnits(String fractionUrl) {
        String uri = "https://wiki.onepagerules.com" + fractionUrl; // or any other uri

        Document doc = Jsoup.connect(uri).get();
        return doc.toString();
    }

    private byte[] getImage(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }
}
