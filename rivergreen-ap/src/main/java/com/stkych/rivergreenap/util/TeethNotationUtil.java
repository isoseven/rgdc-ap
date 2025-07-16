package com.stkych.rivergreenap.util;

import java.util.*;

/**
 * Utility class for handling teeth notation conversions.
 * Provides methods to convert between teeth numbers and shorthand notations.
 */
public class TeethNotationUtil {

    // Type sets
    private static final Set<Integer> THIRD_MOLARS = new HashSet<>(Arrays.asList(1, 16, 17, 32));
    private static final Set<Integer> MOLARS = new HashSet<>(Arrays.asList(2, 3, 14, 15, 18, 19, 30, 31));
    private static final Set<Integer> PREMOLARS = new HashSet<>(Arrays.asList(4, 5, 12, 13, 20, 21, 28, 29));
    private static final Set<Integer> CANINES = new HashSet<>(Arrays.asList(6, 11, 22, 27));
    private static final Set<Integer> INCISORS = new HashSet<>(Arrays.asList(7, 8, 9, 10, 23, 24, 25, 26));

    // Location sets
    private static final Set<Integer> UPPER = new HashSet<>();
    private static final Set<Integer> LOWER = new HashSet<>();
    private static final Set<Integer> LEFT = new HashSet<>();
    private static final Set<Integer> RIGHT = new HashSet<>();

    static {
        // Initialize UPPER (1-16)
        for (int i = 1; i <= 16; i++) {
            UPPER.add(i);
        }

        // Initialize LOWER (17-32)
        for (int i = 17; i <= 32; i++) {
            LOWER.add(i);
        }

        // Initialize LEFT (9-24)
        for (int i = 9; i <= 24; i++) {
            LEFT.add(i);
        }

        // Initialize RIGHT (1-8, 25-32)
        for (int i = 1; i <= 8; i++) {
            RIGHT.add(i);
        }
        for (int i = 25; i <= 32; i++) {
            RIGHT.add(i);
        }
    }

    /**
     * Converts a hyphen-separated list of teeth numbers to a shorthand notation if possible.
     *
     * @param teethNumbers A hyphen-separated list of teeth numbers
     * @return The shorthand notation if available, otherwise the original list
     */
    public static String toShorthand(String teethNumbers) {
        if (teethNumbers == null || teethNumbers.isEmpty()) {
            return "";
        }

        // Parse the teeth numbers
        Set<Integer> teeth = new HashSet<>();
        for (String tooth : teethNumbers.split("-")) {
            try {
                teeth.add(Integer.parseInt(tooth.trim()));
            } catch (NumberFormatException e) {
                // If any part is not a number, return the original string
                return teethNumbers;
            }
        }

        // Check for type sets
        List<String> typeMatches = new ArrayList<>();
        if (teeth.equals(THIRD_MOLARS)) {
            return "Third Molars";
        }
        if (teeth.equals(MOLARS)) {
            return "Molars";
        }
        if (teeth.equals(PREMOLARS)) {
            return "Premolars";
        }
        if (teeth.equals(CANINES)) {
            return "Canines";
        }
        if (teeth.equals(INCISORS)) {
            return "Incisors";
        }

        // Check for partial type matches
        if (THIRD_MOLARS.containsAll(teeth)) {
            typeMatches.add("Third Molars");
        }
        if (MOLARS.containsAll(teeth)) {
            typeMatches.add("Molars");
        }
        if (PREMOLARS.containsAll(teeth)) {
            typeMatches.add("Premolars");
        }
        if (CANINES.containsAll(teeth)) {
            typeMatches.add("Canines");
        }
        if (INCISORS.containsAll(teeth)) {
            typeMatches.add("Incisors");
        }

        // Check for location sets
        List<String> locationMatches = new ArrayList<>();
        boolean isUpper = true;
        boolean isLower = true;
        boolean isLeft = true;
        boolean isRight = true;

        for (Integer tooth : teeth) {
            if (!UPPER.contains(tooth)) {
                isUpper = false;
            }
            if (!LOWER.contains(tooth)) {
                isLower = false;
            }
            if (!LEFT.contains(tooth)) {
                isLeft = false;
            }
            if (!RIGHT.contains(tooth)) {
                isRight = false;
            }
        }

        if (isUpper) {
            locationMatches.add("Upper");
        }
        if (isLower) {
            locationMatches.add("Lower");
        }
        if (isLeft) {
            locationMatches.add("Left");
        }
        if (isRight) {
            locationMatches.add("Right");
        }

        // Combine location and type matches
        StringBuilder result = new StringBuilder();

        // Add location matches (Upper/Lower goes before Left/Right)
        if (isUpper) {
            result.append("Upper ");
        } else if (isLower) {
            result.append("Lower ");
        }

        if (isLeft) {
            result.append("Left ");
        } else if (isRight) {
            result.append("Right ");
        }

        // Add type matches
        if (!typeMatches.isEmpty()) {
            if (result.length() > 0) {
                result.append(typeMatches.get(0));
            } else {
                result.append(typeMatches.get(0));
            }

            // If there are multiple type matches, combine them
            if (typeMatches.size() > 1) {
                result.append(" + ");
                result.append(String.join(" + ", typeMatches.subList(1, typeMatches.size())));
                result.append(" (");
                result.append(String.join(" + ", typeMatches));
                result.append(")");
            }
        }

        // If we have a shorthand notation, return it, otherwise return the original list
        return result.length() > 0 ? result.toString().trim() : teethNumbers;
    }

    /**
     * Converts a shorthand notation to a hyphen-separated list of teeth numbers.
     *
     * @param shorthand The shorthand notation
     * @return A hyphen-separated list of teeth numbers
     */
    public static String fromShorthand(String shorthand) {
        if (shorthand == null || shorthand.isEmpty()) {
            return "";
        }

        // Check for type sets
        if (shorthand.equals("Third Molars")) {
            return setToString(THIRD_MOLARS);
        }
        if (shorthand.equals("Molars")) {
            return setToString(MOLARS);
        }
        if (shorthand.equals("Premolars")) {
            return setToString(PREMOLARS);
        }
        if (shorthand.equals("Canines")) {
            return setToString(CANINES);
        }
        if (shorthand.equals("Incisors")) {
            return setToString(INCISORS);
        }

        // Check for combined location and type
        Set<Integer> result = new HashSet<>();
        boolean hasLocation = false;
        boolean hasType = false;

        // Check for location
        if (shorthand.contains("Upper")) {
            result.addAll(UPPER);
            hasLocation = true;
        } else if (shorthand.contains("Lower")) {
            result.addAll(LOWER);
            hasLocation = true;
        }

        if (shorthand.contains("Left")) {
            if (hasLocation) {
                // Intersect with existing result
                result.retainAll(LEFT);
            } else {
                result.addAll(LEFT);
                hasLocation = true;
            }
        } else if (shorthand.contains("Right")) {
            if (hasLocation) {
                // Intersect with existing result
                result.retainAll(RIGHT);
            } else {
                result.addAll(RIGHT);
                hasLocation = true;
            }
        }

        // Check for type
        if (shorthand.contains("Third Molars")) {
            if (hasLocation) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(THIRD_MOLARS);
                result = intersection;
            } else {
                result.addAll(THIRD_MOLARS);
            }
            hasType = true;
        }
        if (shorthand.contains("Molars")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(MOLARS);
                result = intersection;
            } else {
                result.addAll(MOLARS);
            }
            hasType = true;
        }
        if (shorthand.contains("Premolars")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(PREMOLARS);
                result = intersection;
            } else {
                result.addAll(PREMOLARS);
            }
            hasType = true;
        }
        if (shorthand.contains("Canines")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(CANINES);
                result = intersection;
            } else {
                result.addAll(CANINES);
            }
            hasType = true;
        }
        if (shorthand.contains("Incisors")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(INCISORS);
                result = intersection;
            } else {
                result.addAll(INCISORS);
            }
            hasType = true;
        }

        // If we have a result, return it, otherwise assume it's already a hyphen-separated list
        return hasLocation || hasType ? setToString(result) : shorthand;
    }

    /**
     * Converts a set of integers to a hyphen-separated string.
     *
     * @param set The set of integers
     * @return A hyphen-separated string
     */
    private static String setToString(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }

        List<Integer> sortedList = new ArrayList<>(set);
        Collections.sort(sortedList);

        StringBuilder sb = new StringBuilder();
        for (Integer i : sortedList) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(i);
        }
        return sb.toString();
    }
}
