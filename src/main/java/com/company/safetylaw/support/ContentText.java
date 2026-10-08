package com.company.safetylaw.support;

import static com.company.safetylaw.support.Rows.row;

import com.company.safetylaw.client.RemoteClient;
import com.company.safetylaw.domain.LawContent;

public final class ContentText {
    private ContentText() {}

    public static String snippet(String text, String query) {
        int i = text.indexOf(query);
        return i < 0
                ? ""
                : text.substring(
                        Math.max(0, i - 40), Math.min(text.length(), i + query.length() + 80));
    }

    public static String articleLink(LawContent law, String query) {
        String body = Checks.text(law.getContent());
        int index = body.indexOf(query),
                length = law.getArticleContentLen() == null ? 0 : law.getArticleContentLen();
        if (index < 0 || index >= length || !"law".equals(law.getSourceType())) return null;
        var matcher =
                java.util.regex.Pattern.compile("(?:^|\n)제(\\d+)조(?:의(\\d+))?")
                        .matcher(body.substring(0, index + 1));
        String article = null;
        while (matcher.find())
            article =
                    String.format(
                            "%04d%02d000",
                            Integer.parseInt(matcher.group(1)),
                            matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2)));
        return article == null
                ? null
                : RemoteClient.query(
                        "https://www.law.go.kr/LSW/LsiJoLinkP.do",
                        row(
                                "docType",
                                "JO",
                                "lsNm",
                                law.getName(),
                                "joNo",
                                article,
                                "languageType",
                                "KO",
                                "paras",
                                "1"));
    }
}
