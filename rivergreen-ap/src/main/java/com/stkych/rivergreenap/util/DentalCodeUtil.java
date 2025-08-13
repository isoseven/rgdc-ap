package com.stkych.rivergreenap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for handling dental code conversions.
 * Provides methods to expand ranges and compress lists of dental codes.
 */
public class DentalCodeUtil {

    /**
     * Expands a dental code string that may contain ranges to a list of individual codes.
     * The string may use commas as delimiters and hyphens to denote ranges.
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

        String[] parts = dentalCodes.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }
            if (part.contains("-")) {
                String[] range = part.split("-");
                if (range.length == 2) {
                    try {
                        // Remove 'D' prefix if present for parsing
                        String startStr = range[0].trim();
                        String endStr = range[1].trim();
                        if (startStr.startsWith("D")) {
                            startStr = startStr.substring(1);
                        }
                        if (endStr.startsWith("D")) {
                            endStr = endStr.substring(1);
                        }
                        
                        int start = Integer.parseInt(startStr);
                        int end = Integer.parseInt(endStr);
                        if (start <= end) {
                            for (int i = start; i <= end; i++) {
                                result.add("D" + i);
                            }
                        } else {
                            // Handle reverse range (unlikely for dental codes, but included for completeness)
                            for (int i = start; i >= end; i--) {
                                result.add("D" + i);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // If we can't parse the range, treat it as a single code
                        String code = part;
                        if (!code.startsWith("D")) {
                            code = "D" + code;
                        }
                        result.add(code);
                    }
                } else {
                    // If the range format is invalid, treat it as a single code
                    String code = part;
                    if (!code.startsWith("D")) {
                        code = "D" + code;
                    }
                    result.add(code);
                }
            } else {
                // Not a range, just a single code
                String code = part;
                if (!code.startsWith("D")) {
                    code = "D" + code;
                }
                result.add(code);
            }
        }
        return result;
    }

    /**
     * Compresses a list of dental codes into a compact representation using ranges.
     * Example: [D3000, D3001, D3002, ..., D3999] becomes "D3000-D3999".
     *
     * @param codes The list of dental codes to compress
     * @return A compact representation of the dental codes using ranges
     */
    public static String compressDentalCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return "";
        }
        
        // Sort the codes and remove the 'D' prefix for easier processing
        List<Integer> codeNumbers = new ArrayList<>();
        for (String code : codes) {
            if (code.startsWith("D")) {
                try {
                    codeNumbers.add(Integer.parseInt(code.substring(1)));
                } catch (NumberFormatException e) {
                    // If we can't parse the code, just skip it
                }
            }
        }
        
        if (codeNumbers.isEmpty()) {
            return String.join(",", codes);
        }
        
        // Sort the code numbers
        Collections.sort(codeNumbers);
        
        // Build the compressed representation
        StringBuilder result = new StringBuilder();
        int start = codeNumbers.get(0);
        int prev = start;
        
        for (int i = 1; i < codeNumbers.size(); i++) {
            int current = codeNumbers.get(i);
            
            // If not consecutive, end the current range and start a new one
            if (current != prev + 1) {
                // Add the completed range
                if (start == prev) {
                    result.append("D").append(start);
                } else {
                    result.append("D").append(start).append("-D").append(prev);
                }
                
                // Start a new range
                result.append(",");
                start = current;
            }
            
            prev = current;
        }
        
        // Add the last range
        if (start == prev) {
            result.append("D").append(start);
        } else {
            result.append("D").append(start).append("-D").append(prev);
        }
        
        return result.toString();
    }

    /**
     * Compresses a comma-separated string of dental codes into a compact representation using ranges.
     * Example: "D3000,D3001,D3002,...,D3999" becomes "D3000-D3999".
     *
     * @param codesString The comma-separated string of dental codes to compress
     * @return A compact representation of the dental codes using ranges
     */
    public static String compressDentalCodes(String codesString) {
        if (codesString == null || codesString.isEmpty()) {
            return "";
        }
        
        // Split the string into individual codes
        String[] codesArray = codesString.split(",");
        List<String> codes = new ArrayList<>();
        for (String code : codesArray) {
            codes.add(code.trim());
        }
        
        return compressDentalCodes(codes);
    }
}