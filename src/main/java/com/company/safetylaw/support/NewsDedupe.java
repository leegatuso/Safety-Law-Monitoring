package com.company.safetylaw.support;

import static com.company.safetylaw.support.Rows.*;

import com.company.safetylaw.domain.NewsItem;

import java.time.*;
import java.util.*;

public final class NewsDedupe {
    private NewsDedupe() {}

    static String core(String title) {
        int publisher = title.lastIndexOf(" - ");
        return (publisher < 0 ? title : title.substring(0, publisher)).toLowerCase(Locale.ROOT);
    }

    static Set<String> bigrams(String title) {
        var result = new HashSet<String>();
        for (String token : core(title).split("[^0-9a-z가-힣]+"))
            for (int i = 0; i < token.length() - 1; i++) result.add(token.substring(i, i + 2));
        return result;
    }

    public static Instant time(NewsItem row) {
        String date = java.util.Objects.toString(row.getPublishedAt(), "");
        if (date.isBlank()) date = java.util.Objects.toString(row.getFetchedAt(), "");
        return date.isBlank() ? Instant.now() : Instant.parse(date);
    }

    // Same heuristic as the original: IDF-weighted bigrams, 0.35 similarity, three-day window.
    // ponytail: O(n²) within each time window; replace with a candidate index if news volume grows.
    public static List<List<NewsItem>> groups(List<NewsItem> rows) {
        int n = rows.size();
        var features = rows.stream().map(r -> bigrams(r.getTitle())).toList();
        var frequencies = new HashMap<String, Integer>();
        features.forEach(f -> f.forEach(b -> frequencies.merge(b, 1, Integer::sum)));
        var weights = new HashMap<String, Double>();
        frequencies.forEach(
                (b, count) -> weights.put(b, Math.pow(Math.log((n + 1.0) / (count + 1.0)), 2)));
        double[] norms = new double[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            norms[i] = features.get(i).stream().mapToDouble(weights::get).sum();
        }
        var order = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) order.add(i);
        order.sort(Comparator.comparing(i -> time(rows.get(i))));
        for (int a = 0; a < n; a++)
            for (int b = a + 1; b < n; b++) {
                int i = order.get(a), j = order.get(b);
                if (Duration.between(time(rows.get(i)), time(rows.get(j)))
                                .compareTo(Duration.ofDays(3))
                        > 0) break;
                boolean sameTitle =
                        core(rows.get(i).getTitle()).equals(core(rows.get(j).getTitle()));
                double dot =
                        features.get(i).stream()
                                .filter(features.get(j)::contains)
                                .mapToDouble(weights::get)
                                .sum();
                if (sameTitle
                        || norms[i] > 0
                                && norms[j] > 0
                                && dot / Math.sqrt(norms[i] * norms[j]) >= .35)
                    parent[find(parent, j)] = find(parent, i);
            }
        var groups = new LinkedHashMap<Integer, List<NewsItem>>();
        for (int i = 0; i < n; i++)
            groups.computeIfAbsent(find(parent, i), key -> new ArrayList<>()).add(rows.get(i));
        return new ArrayList<>(groups.values());
    }

    private static int find(int[] parent, int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]];
            i = parent[i];
        }
        return i;
    }
}
