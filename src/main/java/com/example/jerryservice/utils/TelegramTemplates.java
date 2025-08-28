package com.example.jerryservice.utils;

import com.example.jerryservice.entity.MatchEntity;
import com.example.jerryservice.entity.PlayerEntity;
import com.example.jerryservice.entity.UserEntity;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.jerryservice.utils.DateTimeUtil.*;

public final class TelegramTemplates {
    private TelegramTemplates() {}

    /* ====== Language toggle ====== */
    public enum Lang { EN, KH }   // English, Khmer
    private static final String TEAM_NAME = "Jerry FC";

    /* ====== i18n strings ====== */
    private record Strings(
            String newMatch, String summary, String summaryUpdated, String cancelled,
            String playerJoined, String playerLeft,
            String fixture, String opponent, String player, String date, String kickoff,
            String location, String pitch, String maxSlots, String players, String invitation,
            String cancelNote, String openLabel
    ) {}

    private static Strings S(Lang lang) {
        if (lang == Lang.KH) {
            return new Strings(
                    "ការប្រកួតថ្មីបានបង្កើត", "សង្ខេបការប្រកួត", "បានកែប្រែការប្រកួត", "ការប្រកួតត្រូវបានបោះបង់",
                    "អ្នកលេងបានចូលរួម", "អ្នកលេងបានចាកចេញ",
                    "ការប្រកួត", "គូប្រកួត", "អ្នកលេង", "កាលបរិច្ឆេទ", "ពេលចាប់ផ្តើម",
                    "ទីតាំង", "ទីលាន", "ចំនួនកន្លែងអតិបរមា", "អ្នកលេង", "អាចចូលរួមតាមរយៈ",
                    "ការប្រកួតនេះត្រូវបានបោះបង់។", "បើក"
            );
        }
        // Default EN
        return new Strings(
                "New Match Created", "Match Summary", "Match Updated", "Match Cancelled",
                "Player Joined", "Player Left",
                "Fixture", "Opponent", "Player", "Date", "Kick-off",
                "Location", "Pitch", "Max slots", "Players", "Invitation",
                "This match has been cancelled.", "Open"
        );
    }

    /* ====== Local helpers (no changes needed to DateTimeUtil) ====== */

    /** Try to format various date inputs to "EEE, d MMM yyyy"; fall back to raw string (escaped). */
    private static String formatDateAny(Object raw) {
        if (raw == null) return "TBD";
        if (raw instanceof LocalDate ld) return formatDate(ld);

        // Try a few common patterns quickly; you can add more if needed
        String s = String.valueOf(raw).trim();
        DateTimeFormatter[] candidates = new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE,                 // 2025-08-28
                DateTimeFormatter.ofPattern("d/M/uuuu"),          // 28/8/2025
                DateTimeFormatter.ofPattern("uuuu/M/d"),          // 2025/8/28
                DateTimeFormatter.ofPattern("d-M-uuuu"),          // 28-08-2025
                DateTimeFormatter.ofPattern("MMM d, uuuu")        // Aug 28, 2025
        };
        for (DateTimeFormatter f : candidates) {
            try { return formatDate(LocalDate.parse(s, f)); } catch (Exception ignored) {}
        }
        // Could not parse → return escaped original
        return esc(nz(s, "TBD"));
    }

    /** Names of players (unescaped here; escaping handled by list builder). */
    private static List<String> playerNames(MatchEntity m) {
        if (m == null || m.getPlayers() == null) return List.of();
        return m.getPlayers().stream()
                .map(PlayerEntity::getUser)
                .filter(Objects::nonNull)
                .map(u -> nz(u.getDisplayName(), "(unknown)"))
                .collect(Collectors.toList());
    }

    /** Pitch number via reflection (supports getPitchNumber / getPitchNo); else "TBD". */
    private static String pitchNumber(MatchEntity m) {
        if (m == null) return "TBD";
        try {
            Method mm = m.getClass().getMethod("getPitchNumber");
            Object v = mm.invoke(m);
            return esc(v == null ? "TBD" : String.valueOf(v));
        } catch (Exception ignored) {}
        try {
            Method mm = m.getClass().getMethod("getPitchNo");
            Object v = mm.invoke(m);
            return esc(v == null ? "TBD" : String.valueOf(v));
        } catch (Exception ignored) {}
        return "TBD";
    }

    /** Build invite link using entity's getInviteUrl() if present; fallback to provided URL. */
    private static String inviteLink(MatchEntity m, String inviteUrlOverride, String label) {
        String url = null;
        try {
            Method mm = m.getClass().getMethod("getInviteUrl");
            Object v = mm.invoke(m);
            if (v != null && !String.valueOf(v).isBlank()) url = String.valueOf(v);
        } catch (Exception ignored) {}
        if (url == null || url.isBlank()) url = inviteUrlOverride;
        return link(url, nz(label, "Open"));
    }

    /** Progress bar (uses your DateTimeUtil.progressBar). */
    private static String progressBarLine(int joined, int max) {
        return esc(DateTimeUtil.progressBar(joined, Math.max(max, 1)));
    }

    /* ====== Public builders (defaults to EN) ====== */

    public static String buildMatchCreatedTemplate(MatchEntity m, String inviteUrl) {
        return buildMatchCreatedTemplate(m, inviteUrl, Lang.EN);
    }
    public static String buildSummaryTemplate(MatchEntity m, String inviteUrl) {
        return buildSummaryTemplate(m, inviteUrl, Lang.EN);
    }
    public static String buildSummaryTemplateUpdate(MatchEntity m, String inviteUrl) {
        return buildSummaryTemplateUpdate(m, inviteUrl, Lang.EN);
    }
    public static String buildMatchCancelledTemplate(MatchEntity m) {
        return buildMatchCancelledTemplate(m, Lang.EN);
    }
    public static String buildPlayerJoinedTemplate(UserEntity player, MatchEntity m, String inviteUrl) {
        return buildPlayerJoinedTemplate(player, m, inviteUrl, Lang.EN);
    }
    public static String buildPlayerLeftTemplate(UserEntity player, MatchEntity m, String inviteUrl) {
        return buildPlayerLeftTemplate(player, m, inviteUrl, Lang.EN);
    }

    /* ====== Language-aware builders ====== */

    public static String buildMatchCreatedTemplate(MatchEntity m, String inviteUrl, Lang lang) {
        Strings s = S(lang);

        String date = formatDateAny(m != null ? m.getMatchDate() : null);
        String time = formatTime(m != null ? m.getKickOffTime() : null);
        String opp  = esc(nz(m != null ? m.getOpponentName() : null, "TBD"));
        String loc  = esc(nz(m != null ? m.getLocation() : null, "TBD"));
        String pitch= pitchNumber(m);
        String max  = (m != null && m.getMaxPlayers() != null) ? String.valueOf(m.getMaxPlayers()) : "TBD";
        String link = inviteLink(m, inviteUrl, s.openLabel());

        String playersList = bulletListLimited(playerNames(m), 12);

        String body = """
            <b>🏟 %s</b>
            ─────────────────
            • <b>%s</b>: <b>%s vs %s</b>
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s

            <b>%s</b>
            %s

            • <b>%s</b>: %s
            """.formatted(
                s.newMatch(),
                s.fixture(), esc(TEAM_NAME), opp,
                s.date(), date,
                s.kickoff(), time,
                s.location(), loc,
                s.pitch(), pitch,
                s.maxSlots(), esc(max),
                s.players(), playersList,
                s.invitation(), link
        );

        return tgNormalize(body);
    }

    public static String buildSummaryTemplate(MatchEntity m, String inviteUrl, Lang lang) {
        Strings s = S(lang);

        String date = formatDateAny(m != null ? m.getMatchDate() : null);
        String time = formatTime(m != null ? m.getKickOffTime() : null);
        int joined  = (m == null || m.getPlayers() == null) ? 0 : m.getPlayers().size();
        int max     = (m == null || m.getMaxPlayers() == null) ? 0 : m.getMaxPlayers();
        String bar  = progressBarLine(joined, max);
        String opp  = esc(nz(m != null ? m.getOpponentName() : null, "TBD"));
        String loc  = esc(nz(m != null ? m.getLocation() : null, "TBD"));
        String pitch= pitchNumber(m);
        String link = inviteLink(m, inviteUrl, s.openLabel());

        String playersList = bulletListLimited(playerNames(m), 12);

        String body = """
            <b>📊 %s</b>
            ──────────────────
            • <b>%s</b>: <b>%s vs %s</b>
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s

            <b>%s</b>
            %s

            • <b>%s</b>: %s
            """.formatted(
                s.summary(),
                s.fixture(), esc(TEAM_NAME), opp,
                s.date(), date,
                s.kickoff(), time,
                s.location(), loc,
                s.pitch(), pitch,
                s.players(), bar,
                s.players(), playersList,
                s.invitation(), link
        );

        return tgNormalize(body);
    }

    public static String buildSummaryTemplateUpdate(MatchEntity m, String inviteUrl, Lang lang) {
        Strings s = S(lang);

        String date = formatDateAny(m != null ? m.getMatchDate() : null);
        String time = formatTime(m != null ? m.getKickOffTime() : null);
        int joined  = (m == null || m.getPlayers() == null) ? 0 : m.getPlayers().size();
        int max     = (m == null || m.getMaxPlayers() == null) ? 0 : m.getMaxPlayers();
        String bar  = progressBarLine(joined, max);
        String opp  = esc(nz(m != null ? m.getOpponentName() : null, "TBD"));
        String loc  = esc(nz(m != null ? m.getLocation() : null, "TBD"));
        String pitch= pitchNumber(m);
        String link = inviteLink(m, inviteUrl, s.openLabel());

        String playersList = bulletListLimited(playerNames(m), 12);

        String body = """
            <b>✏️ %s</b>
            ──────────────────
            • <b>%s</b>: <b>%s vs %s</b>
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s

            <b>%s</b>
            %s

            • <b>%s</b>: %s
            """.formatted(
                s.summaryUpdated(),
                s.fixture(), esc(TEAM_NAME), opp,
                s.date(), date,
                s.kickoff(), time,
                s.location(), loc,
                s.pitch(), pitch,
                s.players(), bar,
                s.players(), playersList,
                s.invitation(), link
        );

        return tgNormalize(body);
    }

    public static String buildMatchCancelledTemplate(MatchEntity m, Lang lang) {
        Strings s = S(lang);

        String opp  = esc(nz(m != null ? m.getOpponentName() : null, "TBD"));
        String date = formatDateAny(m != null ? m.getMatchDate() : null);
        String time = formatTime(m != null ? m.getKickOffTime() : null);
        String loc  = esc(nz(m != null ? m.getLocation() : null, "TBD"));
        String pitch= pitchNumber(m);

        String body = """
            <b>🚫 %s</b>
            ──────────────────
            • <b>%s</b>: <b>%s vs %s</b>
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s
            • <b>%s</b>: %s

            <i>%s</i>
            """.formatted(
                s.cancelled(),
                s.fixture(), esc(TEAM_NAME), opp,
                s.date(), date,
                s.kickoff(), time,
                s.location(), loc,
                s.pitch(), pitch,
                s.cancelNote()
        );

        return tgNormalize(body);
    }

    public static String buildPlayerJoinedTemplate(UserEntity player, MatchEntity m, String inviteUrl, Lang lang) {
        Strings s = S(lang);

        String name = esc(nz(player != null ? player.getDisplayName() : null, "Unknown"));
        String opp  = esc(nz(m != null ? m.getOpponentName() : null, "TBD"));

        String header = """
            <b>✅ %s</b>
            ──────────────────
            • <b>%s</b>: <b>%s</b>
            • <b>%s</b>: %s vs %s
            """.formatted(s.playerJoined(), s.player(), name, s.fixture(), esc(TEAM_NAME), opp);

        return tgNormalize(header + "\n\n" + buildSummaryTemplate(m, inviteUrl, lang));
    }

    public static String buildPlayerLeftTemplate(UserEntity player, MatchEntity m, String inviteUrl, Lang lang) {
        Strings s = S(lang);

        String name = esc(nz(player != null ? player.getDisplayName() : null, "Unknown"));
        String opp  = esc(nz(m != null ? m.getOpponentName() : null, "TBD"));

        String header = """
            <b>❌ %s</b>
            ────────────────
            • <b>%s</b>: <b>%s</b>
            • <b>%s</b>: %s vs %s
            """.formatted(s.playerLeft(), s.player(), name, s.fixture(), esc(TEAM_NAME), opp);

        return tgNormalize(header + "\n\n" + buildSummaryTemplate(m, inviteUrl, lang));
    }
}
