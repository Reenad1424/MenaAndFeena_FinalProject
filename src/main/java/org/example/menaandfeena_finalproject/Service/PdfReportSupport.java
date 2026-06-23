package org.example.menaandfeena_finalproject.Service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * أدوات مشتركة لتوليد تقارير PDF بمظهر رسمي (بلدية):
 * - تنظيف رموز الماركداون من نص الذكاء الاصطناعي.
 * - تحويل نص الذكاء الاصطناعي إلى أقسام HTML نظيفة.
 * - CSS موحّد متوافق مع OpenHTMLToPDF (بدون Grid/Flex — يعتمد على الجداول).
 */
public final class PdfReportSupport {

    private PdfReportSupport() {}

    // تعليمات تُضاف لكل برومبت ذكاء اصطناعي حتى يرجع نصاً عربياً نظيفاً بلا ماركداون.
    public static final String AI_PLAIN_ARABIC =
            "\n\nاكتب بالعربية الفصحى فقط كنص عادي. ممنوع استخدام الماركداون: لا تستخدم ** ولا ## ولا ### "
            + "ولا الشرطات أو النجوم كنقاط، ولا أكواد. اكتب عنوان كل قسم في سطر مستقل ينتهي بنقطتين، "
            + "ثم فقرة قصيرة تحته.";

    private static final List<String> KNOWN_HEADINGS = List.of(
            "ملخص تنفيذي", "أبرز النتائج", "أخطر المشكلات", "أكبر مشكلة", "أكثر منطقة متأثرة",
            "التوصيات", "التوصيات العاجلة", "اقتراحات تحسين", "تحسينات مستقبلية", "الخلاصة", "النتائج");

    /** يزيل رموز الماركداون الشائعة من نص الذكاء الاصطناعي. */
    public static String stripMarkdown(String text) {
        if (text == null) {
            return "";
        }
        String t = text;
        t = t.replace("```", " ");
        t = t.replace("**", "");
        t = t.replace("###", "");
        t = t.replace("##", "");
        t = t.replace("`", "");
        // نحوّل بدايات النقاط (-, *, •) في أول السطر إلى لا شيء
        t = t.replaceAll("(?m)^\\s*[\\*\\-•]\\s+", "");
        // نزيل النجوم المتبقية
        t = t.replace("*", "");
        // نزيل رقم القسم في أول السطر مثل "1." أو "2)"
        t = t.replaceAll("(?m)^\\s*\\d+[\\.)]\\s*", "");
        return t.trim();
    }

    /** يحوّل نص الذكاء الاصطناعي إلى أقسام (عناوين + فقرات) HTML نظيفة. */
    public static String aiHtml(String raw) {
        String text = stripMarkdown(raw);
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\\r?\\n")) {
            String s = line.trim();
            if (s.isEmpty()) {
                continue;
            }
            boolean heading = s.length() <= 45
                    && (s.endsWith(":") || s.endsWith("：")
                        || KNOWN_HEADINGS.stream().anyMatch(s::startsWith));
            if (heading) {
                String h = s.replaceAll("[:：]\\s*$", "");
                sb.append("<h4>").append(esc(h)).append("</h4>");
            } else {
                sb.append("<p>").append(esc(s)).append("</p>");
            }
        }
        if (sb.length() == 0) {
            sb.append("<p>").append(esc(text)).append("</p>");
        }
        return sb.toString();
    }

    public static String esc(Object value) {
        if (value == null) {
            return "—";
        }
        String s = value.toString();
        if (s.isBlank()) {
            return "—";
        }
        return HtmlUtils.htmlEscape(s, StandardCharsets.UTF_8.name());
    }

    /** شعار المنصة بصيغة base64 (best-effort: يرجع فارغاً إن لم يوجد). */
    public static String logoBase64() {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(
                    new ClassPathResource("static/images/logo.jpeg").getInputStream());
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "";
        }
    }

    /** اللون حسب النسبة (للبطاقات والأشرطة). */
    public static String percentColor(double v) {
        if (v < 30) return "#dc2626";
        if (v < 60) return "#d97706";
        return "#16a34a";
    }

    /** CSS موحّد لكل التقارير — متوافق مع OpenHTMLToPDF (جداول بدل Grid/Flex). */
    public static String css() {
        return """
@page {
    size: A4;
    margin: 16mm 14mm 18mm 14mm;
    @top-center {
        content: "منصة منا وفينا — تقرير رسمي";
        font-family: "Noto Naskh Arabic";
        font-size: 9px;
        color: #9ca3af;
    }
    @bottom-center {
        content: "صفحة " counter(page);
        font-family: "Noto Naskh Arabic";
        font-size: 9px;
        color: #6b7280;
    }
}
@page cover {
    margin: 0;
    @top-center { content: ""; }
    @bottom-center { content: ""; }
}

* { font-family: "Noto Naskh Arabic"; box-sizing: border-box; }
html, body { margin: 0; padding: 0; }
body {
    color: #1f2937;
    font-size: 12px;
    line-height: 1.7;
    direction: rtl;
    text-align: right;
    background: #FAF7F0;
}

.page-break { page-break-before: always; }

/* ===== Cover ===== */
.cover {
    page: cover;
    text-align: center;
    padding: 150px 40px 0 40px;
    background: #FAF7F0;
}
.cover .logo { width: 120px; height: 120px; border-radius: 28px; }
.cover .platform { color: #2e7d32; font-size: 22px; font-weight: bold; margin-top: 18px; }
.cover .bar { width: 90px; height: 4px; background: #2e7d32; margin: 26px auto; border-radius: 4px; }
.cover .rtitle { color: #14532d; font-size: 38px; font-weight: bold; margin-top: 40px; }
.cover .rsub { color: #4b5563; font-size: 18px; margin-top: 14px; }
.cover .rmeta { color: #6b7280; font-size: 14px; margin-top: 60px; line-height: 2.2; }

/* ===== Section heading ===== */
.section-title {
    color: #14532d;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 2px solid #2e7d32;
    padding-bottom: 8px;
    margin: 0 0 16px 0;
}
.section { margin-bottom: 22px; }

/* ===== Dashboard stat cards (table for equal sizes) ===== */
.cards { width: 100%; border-collapse: separate; border-spacing: 10px; table-layout: fixed; }
.cards td { vertical-align: top; }
.card {
    background: #FFFFFF;
    border: 1px solid #E5E7EB;
    border-radius: 14px;
    padding: 16px 10px;
    text-align: center;
    min-height: 96px;
}
.card .num { font-size: 36px; font-weight: bold; color: #14532d; }
.card .lbl { font-size: 12px; color: #6b7280; margin-top: 8px; }
.card.urgent .num { color: #b91c1c; }
.card.green .num { color: #16a34a; }
.card.amber .num { color: #d97706; }

/* ===== Metric cards with progress bars (performance) ===== */
.metrics { width: 100%; border-collapse: separate; border-spacing: 10px; table-layout: fixed; }
.metrics td { vertical-align: top; }
.metric {
    background: #FFFFFF;
    border: 1px solid #E5E7EB;
    border-radius: 14px;
    padding: 14px;
    min-height: 110px;
}
.metric .mt { font-size: 12px; color: #374151; font-weight: bold; }
.metric .mv { font-size: 26px; font-weight: bold; margin: 8px 0; }
.bar { width: 100%; height: 9px; background: #eef2f7; border-radius: 8px; }
.fill { height: 9px; border-radius: 8px; }

.overall {
    background: #FFFFFF;
    border: 1px solid #E5E7EB;
    border-radius: 16px;
    padding: 22px;
    text-align: center;
    margin-bottom: 22px;
}
.overall .ot { color: #14532d; font-size: 16px; font-weight: bold; }
.overall .ov { font-size: 48px; font-weight: bold; margin-top: 6px; }
.overall .ob { display: inline-block; margin-top: 8px; padding: 4px 16px; border-radius: 20px;
    background: #e8f5e9; color: #166534; font-size: 13px; font-weight: bold; }

/* ===== Data tables ===== */
table.data {
    width: 100%;
    border-collapse: collapse;
    background: #FFFFFF;
    border: 1px solid #E5E7EB;
}
table.data thead th {
    background: #2e7d32;
    color: #FFFFFF;
    font-weight: bold;
    padding: 10px 8px;
    text-align: right;
    font-size: 12px;
}
table.data tbody td {
    padding: 9px 8px;
    border-bottom: 1px solid #E5E7EB;
    text-align: right;
    color: #374151;
    font-size: 11px;
    vertical-align: top;
}
table.data tbody tr { page-break-inside: avoid; }
table.data tbody tr:nth-child(even) td { background: #F9FAFB; }

/* small two-column facts table */
table.facts { width: 100%; border-collapse: collapse; background: #FFFFFF; border: 1px solid #E5E7EB; }
table.facts th { background: #f0fdf4; color: #166534; text-align: right; font-weight: bold;
    padding: 10px; border-bottom: 1px solid #E5E7EB; width: 45%; }
table.facts td { padding: 10px; text-align: right; color: #374151; border-bottom: 1px solid #E5E7EB; }

/* ===== AI section ===== */
.ai {
    background: #FFFFFF;
    border: 1px solid #E5E7EB;
    border-radius: 16px;
    padding: 24px;
}
.ai h4 { color: #14532d; font-size: 14px; font-weight: bold; margin: 16px 0 6px 0; }
.ai h4:first-child { margin-top: 0; }
.ai p { margin: 0 0 10px 0; line-height: 1.95; color: #374151; }
""";
    }
}
