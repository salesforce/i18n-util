/* 
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.force.db.i18n;

import junit.framework.TestCase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Locale;

/** A generator for OracleUpperTable.java. This generator creates an OracleUpperTable for each of a number of
 * {@link UpperExpr PL/SQL expressions}, which simply tabulates the these differences, allowing them to be
 * compensated for.
 * <p>
 * May be run as a JUnit test or as a stand-alone Java application. Run the output in Oracle to generate the source for
 * OracleUpperTable.java.
 *
 * @author afield
 *
 * @see OracleUpper
 * @see OracleUpperTable
 */
public class OracleUpperTableGeneratorTest extends TestCase {

    private static final char[] charsToTest = new char[] {
        // i may be messed up for Turkic languages where it's supposed to upper-case to dotted I.
        'i',
        // Sharp s may upper-case to SS or itself, depending on the details.
        'ß',
        // Oracle removes tonos from all of these when upper-casing.
        'Ά', 'Έ', 'Ή', 'Ί', 'Ό', 'Ύ','Ώ','ά','έ','ή','ί','ό','ύ','ώ'
    };

    /**
     * Most of these were just generated from the LinguisticSort enum:
     *
     * <pre><code>
     *     public static void generateValuesFromLinguisticSort() {
     *         for (LinguisticSort s : LinguisticSort.values()) {
     *             System.out.println(String.format("%1$s(\"%2$s\", \"%3$s\"),",
     *                 s.name(), s.getUpperSqlFormatString(), s.getLocale().getLanguage()));
     *         }
     *     }
     * </code></pre>
     *
     * Each value is a PL/SQL upper case expression that may return different results than Java's String.toUpperCase
     * method for the given language.
     */
    private enum UpperExpr {
        ENGLISH("upper(%s)", "en"),
        GERMAN("nls_upper(%s, 'nls_sort=xgerman')", "de"),
        FRENCH("nls_upper(%s, 'nls_sort=xfrench')", "fr"),
        ITALIAN("nls_upper(%s, 'nls_sort=italian')", "it"),
        SPANISH("nls_upper(%s, 'nls_sort=spanish')", "es"),
        CATALAN("nls_upper(%s, 'nls_sort=catalan')", "ca"),
        DUTCH("nls_upper(%s, 'nls_sort=dutch')", "nl"),
        PORTUGUESE("nls_upper(%s, 'nls_sort=west_european')", "pt"),
        DANISH("nls_upper(%s, 'nls_sort=danish')", "da"),
        NORWEGIAN("nls_upper(%s, 'nls_sort=norwegian')", "no"),
        SWEDISH("nls_upper(%s, 'nls_sort=swedish')", "sv"),
        FINNISH("nls_upper(%s, 'nls_sort=finnish')", "fi"),
        CZECH("nls_upper(%s, 'nls_sort=xczech')", "cs"),
        POLISH("nls_upper(%s, 'nls_sort=polish')", "pl"),
        TURKISH("nls_upper(translate(%s,'i','İ'), 'nls_sort=xturkish')", "tr"),
        CHINESE_HK("nls_upper(to_single_byte(%s), 'nls_sort=tchinese_radical_m')", "zh"),
        CHINESE_TW("nls_upper(to_single_byte(%s), 'nls_sort=tchinese_radical_m')", "zh"),
        CHINESE("nls_upper(to_single_byte(%s), 'nls_sort=schinese_radical_m')", "zh"),
        JAPANESE("nls_upper(to_single_byte(%s), 'nls_sort=japanese_m')", "ja"),
        KOREAN("nls_upper(to_single_byte(%s), 'nls_sort=korean_m')", "ko"),
        RUSSIAN("nls_upper(%s, 'nls_sort=russian')", "ru"),
        BULGARIAN("nls_upper(%s, 'nls_sort=bulgarian')", "bg"),
        INDONESIAN("nls_upper(%s, 'nls_sort=indonesian')", "in"),
        ROMANIAN("nls_upper(%s, 'nls_sort=romanian')", "ro"),
        VIETNAMESE("nls_upper(%s, 'nls_sort=vietnamese')", "vi"),
        UKRANIAN("nls_upper(%s, 'nls_sort=ukrainian')", "uk"),
        HUNGARIAN("nls_upper(%s, 'nls_sort=xhungarian')", "hu"),
        GREEK("nls_upper(%s, 'nls_sort=greek')", "el"),
        HEBREW("nls_upper(%s, 'nls_sort=hebrew')", "iw"),
        SLOVAK("nls_upper(%s, 'nls_sort=slovak')", "sk"),
        SERBIAN_CYRILLIC("nls_upper(%s, 'nls_sort=generic_m')", "sr"),
        SERBIAN_LATIN("nls_upper(%s, 'nls_sort=xcroatian')", "sh"),
        BOSNIAN("nls_upper(%s, 'nls_sort=xcroatian')", "bs"),
        GEORGIAN("nls_upper(%s, 'nls_sort=binary')", "ka"),
        BASQUE("nls_upper(%s, 'nls_sort=west_european')", "eu"),
        MALTESE("nls_upper(%s, 'nls_sort=west_european')", "mt"),
        ROMANSH("nls_upper(%s, 'nls_sort=west_european')", "rm"),
        LUXEMBOURGISH("nls_upper(%s, 'nls_sort=west_european')", "lb"),
        IRISH("nls_upper(%s, 'nls_sort=west_european')", "ga"),
        SLOVENE("nls_upper(%s, 'nls_sort=xslovenian')", "sl"),
        CROATIAN("nls_upper(%s, 'nls_sort=xcroatian')", "hr"),
        MALAY("nls_upper(%s, 'nls_sort=malay')", "ms"),
        ARABIC("nls_upper(%s, 'nls_sort=arabic')", "ar"),
        ESTONIAN("nls_upper(%s, 'nls_sort=estonian')", "et"),
        ICELANDIC("nls_upper(%s, 'nls_sort=icelandic')", "is"),
        LATVIAN("nls_upper(%s, 'nls_sort=latvian')", "lv"),
        LITHUANIAN("nls_upper(%s, 'nls_sort=lithuanian')", "lt"),
        KYRGYZ("nls_upper(%s, 'nls_sort=binary')", "ky"),
        KAZAKH("nls_upper(%s, 'nls_sort=binary')", "kk"),
        TAJIK("nls_upper(%s, 'nls_sort=russian')", "tg"),
        BELARUSIAN("nls_upper(%s, 'nls_sort=russian')", "be"),
        TURKMEN("nls_upper(translate(%s,'i','İ'), 'nls_sort=xturkish')", "tk"),
        AZERBAIJANI("nls_upper(translate(%s,'i','İ'), 'nls_sort=xturkish')", "az"),
        ARMENIAN("nls_upper(%s, 'nls_sort=binary')", "hy"),
        THAI("nls_upper(%s, 'nls_sort=thai_dictionary')", "th"),
        HINDI("nls_upper(%s, 'nls_sort=binary')", "hi"),
        URDU("nls_upper(%s, 'nls_sort=arabic')", "ur"),
        BENGALI("nls_upper(%s, 'nls_sort=bengali')", "bn"),
        TAMIL("nls_upper(%s, 'nls_sort=binary')", "ta"),
        ESPERANTO("upper(%s)", "eo"),

        // for formulas
        XWEST_EUROPEAN("NLS_UPPER(%s,'NLS_SORT=xwest_european')", "en");


        private final String expr;
        private final Locale locale;

        /**
         * @param expr the PL/SQL expression with %s wildcards for the single string input.
         * @param langCode ISO code for the language to use, as in <code> str.toUpperCase(new Locale(langCode))<code>.
         */
        private UpperExpr(String expr, String langCode) {
            this.expr = expr;
            this.locale = new Locale(langCode);
        }

        private String getSql(char value) {
            return String.format(expr, "unistr('\\" + hexCodePoint(value) + "')");
        }

        private String getJava(char value) {
            return Character.toString(value).toUpperCase(locale);
        }
    }

    /**
     * This method generates some anonymous PL/SQL routines which, when run, will generate an OracleUpperTable value
     * for each {@code UpperExpr}. Each table is created by comparing the result of {@link String#toUpperCase(Locale)}
     * against a {@link UpperExpr#getSql(char) PL/SQL expression}. The table contains all deviations from Oracle for
     * each character in a {@link #charsToTest given set} that we know are fussy.
     */
    public static void generateUpperCaseExceptions(PrintWriter out) {

        out.println("set serveroutput on;");
        out.println("set define off;"); // So we don't have to escape ampersands.
        out.println("/");
        out.println("BEGIN");

        putLine(out, "/*");
        putLine(out, " * Copyright, 1999-" + Calendar.getInstance().get(Calendar.YEAR) + ", salesforce.com");
        putLine(out, " * All Rights Reserved");
        putLine(out, " * Company Confidential");
        putLine(out, " */");
        putLine(out, "package i18n;");
        putLine(out, "");
        putLine(out, "import java.util.Locale;");
        putLine(out, "import edu.umd.cs.findbugs.annotations.NonNull;");
        putLine(out, "");
        putLine(out, "/**");
        putLine(out, " * Generated by " + OracleUpperTableGeneratorTest.class.getCanonicalName());
        putLine(out, " * <p>");
        putLine(out, " * An instance of this enum codifies the difference between executing a {@link #getSqlFormatString() particular PL/SQL");
        putLine(out, " * expression} in Oracle and executing {@link String#toUpperCase(Locale)} for a {@link #getLocale() particular locale}");
        putLine(out, " * in Java. These differences (also called exceptions) are expressed by the output of {@link #getUpperCaseExceptions()}");
        putLine(out, " * and {@link #getUpperCaseExceptionMapping(char)}.");
        putLine(out, " * <p>");
        putLine(out, " * The tables are generated by testing a particular set of characters that are known to contain exceptions and");
        putLine(out, " * {@link #toUpperCase(String) may be used} to compensate for exceptions found and generate output in Java that will be");
        putLine(out, " * consistent with Oracle for the given (sql expression, locale) pair over all tested values.");
        putLine(out, " * <p>");
        putLine(out, " * Characters tested:");
        putLine(out, " * <ul>");
        for (char c : charsToTest) {
            putLine(out, " * <li>U+%1$s &#x%1$s</li>", hexCodePoint(c));
        }
        putLine(out, " * </ul>");
        putLine(out, " *");
        putLine(out, " * @see OracleUpper");
        putLine(out, " */");
        putLine(out, "public enum OracleUpperTable {");

        for (UpperExpr u : UpperExpr.values()) {
            put(out, "    %s(\"%s\", \"%s\", \"", u.name(), u.expr, u.locale.getLanguage());

            // Don't generate any exceptions for EO, it's a test value and I wanna use it as a baseline.
            if (u != UpperExpr.ESPERANTO) {
                for (char c : charsToTest) {
                   out.println(String.format("IF %1$s <> '%2$s' THEN dbms_output.put(unistr('\\%3$s')); END IF;",
                            u.getSql(c),
                            u.getJava(c),
                            hexCodePoint(c)));
                }
            }

            putLine(out, "\"),");
        }

        putLine(out, "    ;");
        putLine(out, "");
        putLine(out, "    private final String sql;");
        putLine(out, "    private final Locale locale;");
        putLine(out, "    private final char[] exceptionChars;");
        putLine(out, "");
        putLine(out, "    private OracleUpperTable(String sql, String lang, String exceptionChars) {");
        putLine(out, "        this.sql = sql;");
        putLine(out, "        this.locale = new Locale(lang);");
        putLine(out, "        this.exceptionChars = exceptionChars.toCharArray();");
        putLine(out, "    }");
        putLine(out, "");
        putLine(out, "    /**");
        putLine(out, "    * Return an array containing characters for which Java's String.toUpperCase method is known to deviate from the");
        putLine(out, "    * result of Oracle evaluating {@link #getSql(String) this expression}.");
        putLine(out, "    *");
        putLine(out, "    * @return an array containing all exceptional characters.");
        putLine(out, "    */");
        putLine(out, "    final @NonNull char[] getUpperCaseExceptions() {");
        putLine(out, "        return exceptionChars;");
        putLine(out, "    }");
        putLine(out, "");
        putLine(out, "   /**");
        putLine(out, "    * For a character, {@code exception}, contained in the String returned from {@link #getUpperCaseExceptions()}, this");
        putLine(out, "    * method returns the anticipated result of upper-casing the character in Oracle when evaluating");
        putLine(out, "    * {@link #getSql(String) this expression}.");
        putLine(out, "    *");
        putLine(out, "    * @return the upper case of {@code exception}, according to what Oracle would do.");
        putLine(out, "    * @throws IllegalArgumentException");
        putLine(out, "    *             if the character is not contained in the String returned by {@link #getUpperCaseExceptions()}.");
        putLine(out, "    */");
        putLine(out, "    final String getUpperCaseExceptionMapping(char exception) {");

        putLine(out, "        switch (exception) {");
        for (char c : charsToTest){
            putLine(out, "        case '%s':", "" + c);
            putLine(out, "            switch (this) {");
            for (UpperExpr u : UpperExpr.values()) {
                if (u == UpperExpr.ESPERANTO) {
                    continue;
                }
                out.println(String.format("IF %1$s <> '%2$s' THEN dbms_output.put_line('            case %3$s: return ' || '\"' || %1$s || '\"; // %2$s'); END IF;",
                        u.getSql(c),
                        u.getJava(c),
                        u.name()));
            }
            putLine(out, "            default: // fall out");
            putLine(out, "            }");
            putLine(out, "            break;");
        }
        putLine(out, "        }");

        putLine(out, "        throw new IllegalArgumentException(");
        putLine(out, "                \"No upper case mapping for char=\" + exception");
        putLine(out, "                + \" and this=\" + this);");
        putLine(out, "    }");
        putLine(out, "");

        putLine(out, "    public final Locale getLocale() {");
        putLine(out, "        return locale;");
        putLine(out, "    }");
        putLine(out, "");

        putLine(out, "    public String getSqlFormatString() {");
        putLine(out, "        return sql;");
        putLine(out, "    }");
        putLine(out, "");

        putLine(out, "    public String getSql(String expr) {");
        putLine(out, "        return String.format(sql, expr);");
        putLine(out, "    }");
        putLine(out, "");

        putLine(out, "    public String toUpperCase(String value) {");
        putLine(out, "        return OracleUpper.toUpperCase(this, value);");
        putLine(out, "    }");
        putLine(out, "");

        putLine(out, "    public static final OracleUpperTable forLinguisticSort(String sort) {");
        putLine(out, "        return Enum.valueOf(OracleUpperTable.class, sort);");
        putLine(out, "    }");
        putLine(out, "}");

        out.println("END;");
    }

    /** Escape single quotes by doubling them up (i.e. two single quotes in a row). */
    private static String sqlEscape(String str) {
        //return TextUtil.replaceChar(str, '\'', "''");
    	return str.replace("'", "''");
    }

    /** Return four hex digits of the character's codepoint. */
    private static String hexCodePoint(char c) {
        String cp = Integer.toHexString(c);
        while (cp.length() < 4) {
            cp = "0" + cp;
        }
        return cp;
    }

    /** Send to standard output a dbms_output.put_line call that will emit the result of
     * {@link String#format(String, Object...) formatting} {@code str} with {@code args}.
     *
     * @param str a format string
     * @param args optional format arguments.
     */
    private static void put(PrintWriter out, String str, String... args) {
        out.println("dbms_output.put('" + format(str, args) + "');");
    }

    /** Send to standard output a dbms_output.put call that will emit the result of
     * {@link #format(String, String...) formatting} {@code str} with {@code args}.
     *
     * @param str a format string
     * @param args optional format arguments.
     */
    private static void putLine(PrintWriter out, String str, String... args) {
        out.println("dbms_output.put_line('" + format(str, args) + "');");
    }

    /**
     *  Both {@code str} and {@code args} will be {@link #sqlEscape(String) sql escaped}, and then {@code str} will be
     *  {@link String#format(String, Object...) formatted} using {@code args}.
     *
     */
    private static String format(String str, String... args) {
        str = sqlEscape(str);
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                args[i] = sqlEscape(args[i]);
            }
            str = String.format(str, (Object[])args);
        }
        return str;
    }

    public static void main(String[] args) {
        generateUpperCaseExceptions(new PrintWriter(System.out));
    }

    public void testGenerateUpperCaseExceptions() {
        // Don't bother logging it, just see if there's an exception
        generateUpperCaseExceptions(new PrintWriter(new StringWriter()));
    }
}
