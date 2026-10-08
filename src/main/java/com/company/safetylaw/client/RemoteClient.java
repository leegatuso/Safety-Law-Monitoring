package com.company.safetylaw.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.*;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

@Component
public class RemoteClient {
    private final HttpClient http =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public static URI validateUrl(String url) {
        try {
            URI uri = URI.create(url);
            if (!Set.of("http", "https").contains(uri.getScheme())
                    || uri.getHost() == null
                    || uri.getUserInfo() != null) throw new IllegalArgumentException();
            for (InetAddress address : InetAddress.getAllByName(uri.getHost())) {
                byte[] bytes = address.getAddress();
                if (address.isAnyLocalAddress()
                        || address.isLoopbackAddress()
                        || address.isLinkLocalAddress()
                        || address.isSiteLocalAddress()
                        || address.isMulticastAddress()
                        || (bytes.length == 16 && (bytes[0] & 0xfe) == 0xfc))
                    throw new IllegalArgumentException();
            }
            return uri;
        } catch (Exception ex) {
            throw new IllegalArgumentException("외부 공개 HTTP(S) 주소를 입력하세요.");
        }
    }

    public byte[] bytes(String url) {
        try {
            URI uri = validateUrl(url);
            for (int redirect = 0; redirect < 5; redirect++) {
                var response =
                        http.send(
                                HttpRequest.newBuilder(uri)
                                        .timeout(Duration.ofSeconds(25))
                                        .header("User-Agent", "SafetyLawMonitoring/1.0")
                                        .GET()
                                        .build(),
                                HttpResponse.BodyHandlers.ofInputStream());
                try (var input = response.body()) {
                    if (response.statusCode() >= 300 && response.statusCode() < 400) {
                        uri =
                                validateUrl(
                                        uri.resolve(
                                                        response.headers()
                                                                .firstValue("location")
                                                                .orElseThrow())
                                                .toString());
                        continue;
                    }
                    if (response.statusCode() != 200)
                        throw new IOException("HTTP " + response.statusCode());
                    byte[] bytes = input.readNBytes(20 * 1024 * 1024 + 1);
                    if (bytes.length > 20 * 1024 * 1024) throw new IOException("응답 크기 초과");
                    return bytes;
                }
            }
            throw new IOException("리디렉션 횟수 초과");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "외부 조회가 중단되었습니다.");
        } catch (Exception ex) {
            // Query strings may contain API keys. Do not expose exception messages or URLs.
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "외부 서비스 조회 실패: 인증키, 주소 및 네트워크를 확인하세요.");
        }
    }

    public static String query(String url, Map<String, ?> params) {
        return url
                + (url.contains("?") ? "&" : "?")
                + params.entrySet().stream()
                        .map(
                                e ->
                                        URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                                                + "="
                                                + URLEncoder.encode(
                                                        Objects.toString(e.getValue(), ""),
                                                        StandardCharsets.UTF_8))
                        .collect(java.util.stream.Collectors.joining("&"));
    }

    public static Document xml(byte[] bytes) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(bytes));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "외부 XML 응답을 해석하지 못했습니다.");
        }
    }

    public static String text(Element root, String... tags) {
        for (String tag : tags) {
            var found = root.getElementsByTagName(tag);
            if (found.getLength() > 0 && !found.item(0).getTextContent().isBlank())
                return found.item(0).getTextContent().strip();
        }
        return "";
    }

    public static List<Element> elements(Element root, String tag) {
        var nodes = root.getElementsByTagName(tag);
        var result = new ArrayList<Element>();
        for (int i = 0; i < nodes.getLength(); i++) result.add((Element) nodes.item(i));
        return result;
    }
}
