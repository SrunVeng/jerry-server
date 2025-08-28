package com.example.jerryservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class DateTimeUtil {

    /* ---------- Formatters ---------- */
    public static final DateTimeFormatter DATE_FMT =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("EEE, d MMM yyyy")
                    .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter TIME_FMT =
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("HH:mm")
                    .toFormatter(Locale.ENGLISH);

    private static final List<DateTimeFormatter> DATE_INPUTS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,                               // 2025-08-28
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d/M/uuuu").toFormatter(Locale.ENGLISH),
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("d-M-uuuu").toFormatter(Locale.ENGLISH),
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("uuuu/M/d").toFormatter(Locale.ENGLISH),
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM d, uuuu").toFormatter(Locale.ENGLISH) // Aug 28, 2025
    );

    private static final List<DateTimeFormatter> TIME_INPUTS = List.of(
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("H:mm").toFormatter(Locale.ENGLISH),     // 6:05
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("HH:mm").toFormatter(Locale.ENGLISH),    // 06:05
            new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mm a").toFormatter(Locale.ENGLISH)    // 6:05 PM
    );

    /* ---------- Safe helpers ---------- */
    public static String formatDate(LocalDate d) {
        return (d == null) ? "TBD" : d.format(DATE_FMT);
    }

    /** Accepts LocalDate or String; attempts to parse common formats. */
    public static String formatDateAny(Object raw) {
        if (raw == null) return "TBD";
        if (raw instanceof LocalDate ld) return formatDate(ld);
        if (raw instanceof CharSequence cs) {
            String s = cs.toString().trim();
            for (DateTimeFormatter f : DATE_INPUTS) {
                try { return LocalDate.parse(s, f).format(DATE_FMT); } catch (Exception ignored) {}
            }
            // last resort: try java.util.Date via ISO
            try {
                LocalDate d = java.time.LocalDate.parse(s);
                return formatDate(d);
            } catch (Exception ignored) {}
        }
        return "TBD";
    }

    /** Accepts LocalTime or String; normalizes to HH:mm (HTML-escaped if unknown). */
    public static String formatTime(Object t) {
        if (t == null) return "TBD";
        if (t instanceof LocalTime lt) return lt.format(TIME_FMT);
        if (t instanceof CharSequence cs) {
            String s = cs.toString().trim();
            for (DateTimeFormatter f : TIME_INPUTS) {
                try { return LocalTime.parse(s, f).format(TIME_FMT); } catch (Exception ignored) {}
            }
            // Fallback: try constructing a time
            try {
                LocalTime lt = LocalTime.parse(s);
                return lt.format(TIME_FMT);
            } catch (Exception ignored) {}
            return HtmlUtils.htmlEscape(s);
        }
        return HtmlUtils.htmlEscape(String.valueOf(t));
    }

    public static String nz(String v, String fb) {
        return (v == null || v.isBlank()) ? fb : v;
    }

    public static String esc(String v) {
        return HtmlUtils.htmlEscape(v == null ? "" : v);
    }

    /** Convert <br> to newlines (Telegram HTML doesn’t support <br/>). */
    public static String tgNormalize(String s) {
        if (s == null) return "";
        return s.replace("<br/>", "\n")
                .replace("<br />", "\n")
                .replace("<br>", "\n");
    }

    /* ---------- Progress bars ---------- */
    public enum ProgressStyle { GREEN, GRADIENT }


    public static String progressBar(int current, int max) {
        return progressBar(current, max, ProgressStyle.GRADIENT);
    }
    /** 10 blocks, either all green or red→yellow→green gradient. */
    public static String progressBar(int current, int max, ProgressStyle style) {
        if (max <= 0) return "0/0 (0%)";
        current = Math.max(0, Math.min(current, max));
        final int totalBlocks = 10;
        int filled = (int) Math.round((double) current / max * totalBlocks);

        StringBuilder bar = new StringBuilder(totalBlocks * 3 + 20);
        for (int i = 0; i < totalBlocks; i++) {
            if (i < filled) {
                if (style == ProgressStyle.GRADIENT) {
                    double ratio = (i + 1) / (double) totalBlocks;
                    String color = (ratio <= 0.33) ? "🟥" : (ratio <= 0.66 ? "🟨" : "🟩");
                    bar.append(color);
                } else {
                    bar.append("🟩");
                }
            } else {
                bar.append("⬜");
            }
        }
        int pct = (int) Math.round(100.0 * current / max);
        bar.append(" ").append(current).append('/').append(max).append(" (").append(pct).append("%)");
        return bar.toString();
    }

    /** Bullet list of names, escaped, limited with "+N more" (newline-separated). */
    public static String bulletListLimited(Collection<String> names, int limit) {
        if (names == null || names.isEmpty()) return "<i>No players yet</i>";
        List<String> list = names.stream()
                .filter(Objects::nonNull)
                .map(DateTimeUtil::esc)
                .collect(Collectors.toList());

        int total = list.size();
        int show = Math.max(0, Math.min(limit, total));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < show; i++) {
            if (i > 0) sb.append('\n');
            sb.append("• ").append(list.get(i));
        }
        if (total > show) {
            sb.append('\n').append("<i>+").append(total - show).append(" more</i>");
        }
        return sb.toString();
    }

    /** Safe link builder with fallback label. */
    public static String link(String url, String labelFallback) {
        String safeUrl = nz(url, "");
        if (safeUrl.isBlank()) return "<i>(no invite link)</i>";
        String label = esc(nz(labelFallback, "Open"));
        return "<a href=\"" + HtmlUtils.htmlEscape(safeUrl) + "\">" + label + "</a>";
    }
}
