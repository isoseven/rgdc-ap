package com.stkych.rivergreenap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling dental code conversions.
 * Provides methods to expand ranges and compress lists of dental codes.
 */
public class DentalCodeUtil {

    /**
     * Normalizes a single dental code input to the canonical format L#### (four digits), where L is D or N.
     * Accepts values like "D140", "0140", "N0140", returns letter+4 digits. Defaults to D if no letter.
     */
    public static String normalizeToCode4(String code) {
        if (code == null) return "";
        String c = code.trim();
        if (c.isEmpty()) return "";
        char letter = 0;
        if (c.length() > 0) {
            char first = Character.toUpperCase(c.charAt(0));
            if (first == 'D' || first == 'N') {
                letter = first;
                c = c.substring(1).trim();
            }
        }
        // Remove any non-digit characters
        String digits = c.replaceAll("[^0-9]", "");
        try {
            int n = Integer.parseInt(digits);
            if (letter == 0) letter = 'D'; // backward compatibility
            return String.format("%c%04d", letter, n);
        } catch (NumberFormatException e) {
            // Fallback: ensure it starts with a letter (default D)
            if (letter == 0) {
                // Try to detect from original input if it started with D/N
                String trimmed = code.trim();
                if (trimmed.startsWith("D") || trimmed.startsWith("N") || trimmed.startsWith("d") || trimmed.startsWith("n")) {
                    return trimmed.toUpperCase();
                }
                return "D" + trimmed;
            }
            return (Character.toString(letter) + c);
        }
    }

    /**
     * Expands a dental code string that may contain ranges to a list of individual codes.
     * The string may use commas or semicolons as delimiters and hyphens to denote ranges.
     * Example: {@code 3000-3999} becomes a list containing D3000, D3001, ..., D3999.
     *
     * @param dentalCodes The dental codes string
     * @return A list of individual dental codes, or an empty list if parsing fails
     */
    public static List<String> expandDentalCodes(String dentalCodes) {
        List<String> result = new ArrayList<>();
        if (dentalCodes == null || dentalCodes.isEmpty()) {
            return result;
        }

        String[] parts = dentalCodes.split("[,;]"); // Split by both comma and semicolon for backward compatibility
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }
            // Determine leading letter if present (D or N). Default to D for backward compatibility
            char letter = 0;
            String token = part;
            if (token.length() > 0) {
                char first = Character.toUpperCase(token.charAt(0));
                if (first == 'D' || first == 'N') {
                    letter = first;
                    token = token.substring(1).trim();
                }
            }

            if (token.contains("-")) {
                String[] range = token.split("-");
                if (range.length == 2) {
                    try {
                        String startStr = range[0].trim();
                        String endStr = range[1].trim();
                        // End may optionally include letter; if so, override letter for end if provided
                        char endLetter = letter;
                        if (!endStr.isEmpty()) {
                            char endFirst = Character.toUpperCase(endStr.charAt(0));
                            if (endFirst == 'D' || endFirst == 'N') {
                                endLetter = endFirst;
                                endStr = endStr.substring(1).trim();
                            }
                        }
                        if (letter == 0) letter = 'D';
                        if (endLetter == 0) endLetter = letter;
                        int start = Integer.parseInt(startStr);
                        int end = Integer.parseInt(endStr);
                        // If letters differ between start and end, treat as two separate singles (fallback)
                        if (endLetter != letter) {
                            // add single normalized codes
                            result.add(String.format("%c%04d", letter, start));
                            result.add(String.format("%c%04d", endLetter, end));
                        } else {
                            if (start <= end) {
                                for (int i = start; i <= end; i++) {
                                    result.add(String.format("%c%04d", letter, i));
                                }
                            } else {
                                for (int i = start; i >= end; i--) {
                                    result.add(String.format("%c%04d", letter, i));
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // If we can't parse the range, treat it as a single code preserving letter/default
                        String codeDigits = token;
                        if (letter == 0) letter = 'D';
                        try {
                            int n = Integer.parseInt(codeDigits.replaceAll("[^0-9]", ""));
                            result.add(String.format("%c%04d", letter, n));
                        } catch (NumberFormatException ex) {
                            result.add(normalizeToCode4(part));
                        }
                    }
                } else {
                    // Invalid range format, treat as single
                    result.add(normalizeToCode4(part));
                }
            } else {
                // Not a range, just a single code
                if (letter == 0) {
                    // Try to detect from token itself (if it starts with letter), else default D
                    result.add(normalizeToCode4(part));
                } else {
                    try {
                        int n = Integer.parseInt(token.replaceAll("[^0-9]", ""));
                        result.add(String.format("%c%04d", letter, n));
                    } catch (NumberFormatException e) {
                        result.add(normalizeToCode4(part));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Compresses a list of dental codes into a compact representation using ranges, per prefix.
     * Example: [D3000, D3001, D3002] -> "D3000-3002"; [N2300, N2301] -> "N2300-2301".
     * Uses semicolons as delimiters between ranges and preserves the code letter (D/N).
     *
     * @param codes The list of dental codes to compress
     * @return A compact representation of the dental codes using ranges
     */
    public static String compressDentalCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return "";
        }
        // Group numbers by letter (D/N)
        Map<Character, List<Integer>> groups = new HashMap<>();
        for (String code : codes) {
            if (code == null || code.isBlank()) continue;
            String c = code.trim().toUpperCase();
            char letter = (c.charAt(0) == 'D' || c.charAt(0) == 'N') ? c.charAt(0) : 'D';
            String digits = c.substring(letter == 'D' || letter == 'N' ? 1 : 0).replaceAll("[^0-9]", "");
            try {
                int n = Integer.parseInt(digits);
                groups.computeIfAbsent(letter, k -> new ArrayList<>()).add(n);
            } catch (NumberFormatException ignore) {
                // skip unparseable
            }
        }
        // Build output in deterministic order: D then N
        StringBuilder out = new StringBuilder();
        for (char letter : new char[]{'D', 'N'}) {
            List<Integer> nums = groups.get(letter);
            if (nums == null || nums.isEmpty()) continue;
            Collections.sort(nums);
            int start = nums.get(0);
            int prev = start;
            for (int i = 1; i < nums.size(); i++) {
                int cur = nums.get(i);
                if (cur != prev + 1) {
                    // emit range
                    if (out.length() > 0) out.append(";");
                    if (start == prev) {
                        out.append(String.format("%c%04d", letter, start));
                    } else {
                        out.append(String.format("%c%04d-%04d", letter, start, prev));
                    }
                    start = cur;
                }
                prev = cur;
            }
            // emit last
            if (out.length() > 0) out.append(";");
            if (start == prev) {
                out.append(String.format("%c%04d", letter, start));
            } else {
                out.append(String.format("%c%04d-%04d", letter, start, prev));
            }
        }
        return out.toString();
    }

    /**
     * Compresses a comma or semicolon-separated string of dental codes into a compact representation using ranges.
     * Example: "D3000;D3001;D3002;...;D3999" becomes "D3000-D3999".
     * Uses semicolons as delimiters between ranges in the output.
     *
     * @param codesString The comma or semicolon-separated string of dental codes to compress
     * @return A compact representation of the dental codes using ranges
     */
    public static String compressDentalCodes(String codesString) {
        if (codesString == null || codesString.isEmpty()) {
            return "";
        }
        // First expand to individual codes (handles ranges and letters), then compress
        List<String> expanded = expandDentalCodes(codesString);
        return compressDentalCodes(expanded);
    }
}
