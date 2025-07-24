package com.stkych.rivergreenap.util;

import java.util.*;

/**
 * Utility class for handling teeth notation conversions.
 * Provides methods to convert between teeth numbers and shorthand notations.
 */
public class TeethNotationUtil {

    // Type sets - using exact names from shorthand.txt
    private static final Set<Integer> WISDOM = new HashSet<>(Arrays.asList(1, 16, 17, 32));
    private static final Set<Integer> MOLAR = new HashSet<>(Arrays.asList(2, 3, 14, 15, 18, 19, 30, 31));
    private static final Set<Integer> PREMOLAR = new HashSet<>(Arrays.asList(3, 4, 13, 14, 20, 21, 28, 29));
    private static final Set<Integer> CANINE = new HashSet<>(Arrays.asList(6, 11, 22, 27));
    private static final Set<Integer> INCISOR = new HashSet<>(Arrays.asList(7, 8, 9, 10, 23, 24, 25, 26));

    // Combined sets from shorthand.txt
    private static final Set<Integer> UPPER_WISDOM = new HashSet<>(Arrays.asList(1, 16));
    private static final Set<Integer> LOWER_WISDOM = new HashSet<>(Arrays.asList(17, 32));
    private static final Set<Integer> UPPER_MOLAR = new HashSet<>(Arrays.asList(2, 3, 14, 15));
    private static final Set<Integer> LOWER_MOLAR = new HashSet<>(Arrays.asList(18, 19, 30, 31));
    private static final Set<Integer> UPPER_PREMOLAR = new HashSet<>(Arrays.asList(3, 4, 13, 14));
    private static final Set<Integer> LOWER_PREMOLAR = new HashSet<>(Arrays.asList(20, 21, 28, 29));
    private static final Set<Integer> UPPER_CANINE = new HashSet<>(Arrays.asList(6, 11));
    private static final Set<Integer> LOWER_CANINE = new HashSet<>(Arrays.asList(22, 27));
    private static final Set<Integer> UPPER_INCISOR = new HashSet<>(Arrays.asList(7, 8, 9, 10));
    private static final Set<Integer> LOWER_INCISOR = new HashSet<>(Arrays.asList(23, 24, 25, 26));

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

        // Check for exact matches with combined sets from shorthand.txt
        if (teeth.equals(UPPER_WISDOM)) {
            return "Upper Wisdom";
        }
        if (teeth.equals(LOWER_WISDOM)) {
            return "Lower Wisdom";
        }
        if (teeth.equals(UPPER_MOLAR)) {
            return "Upper Molar";
        }
        if (teeth.equals(LOWER_MOLAR)) {
            return "Lower Molar";
        }
        if (teeth.equals(UPPER_PREMOLAR)) {
            return "Upper Premolar";
        }
        if (teeth.equals(LOWER_PREMOLAR)) {
            return "Lower Premolar";
        }
        if (teeth.equals(UPPER_CANINE)) {
            return "Upper Canine";
        }
        if (teeth.equals(LOWER_CANINE)) {
            return "Lower Canine";
        }
        if (teeth.equals(UPPER_INCISOR)) {
            return "Upper Incisor";
        }
        if (teeth.equals(LOWER_INCISOR)) {
            return "Lower Incisor";
        }

        // Check for type sets
        List<String> typeMatches = new ArrayList<>();
        if (teeth.equals(WISDOM)) {
            return "Wisdom";
        }
        if (teeth.equals(MOLAR)) {
            return "Molar";
        }
        if (teeth.equals(PREMOLAR)) {
            return "Premolar";
        }
        if (teeth.equals(CANINE)) {
            return "Canine";
        }
        if (teeth.equals(INCISOR)) {
            return "Incisor";
        }

        // Check for partial type matches
        if (WISDOM.containsAll(teeth)) {
            typeMatches.add("Wisdom");
        }
        if (MOLAR.containsAll(teeth)) {
            typeMatches.add("Molar");
        }
        if (PREMOLAR.containsAll(teeth)) {
            typeMatches.add("Premolar");
        }
        if (CANINE.containsAll(teeth)) {
            typeMatches.add("Canine");
        }
        if (INCISOR.containsAll(teeth)) {
            typeMatches.add("Incisor");
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

        // Check for exact matches with combined sets from shorthand.txt
        if (shorthand.equals("Upper Wisdom")) {
            return setToString(UPPER_WISDOM);
        }
        if (shorthand.equals("Lower Wisdom")) {
            return setToString(LOWER_WISDOM);
        }
        if (shorthand.equals("Upper Molar")) {
            return setToString(UPPER_MOLAR);
        }
        if (shorthand.equals("Lower Molar")) {
            return setToString(LOWER_MOLAR);
        }
        if (shorthand.equals("Upper Premolar")) {
            return setToString(UPPER_PREMOLAR);
        }
        if (shorthand.equals("Lower Premolar")) {
            return setToString(LOWER_PREMOLAR);
        }
        if (shorthand.equals("Upper Canine")) {
            return setToString(UPPER_CANINE);
        }
        if (shorthand.equals("Lower Canine")) {
            return setToString(LOWER_CANINE);
        }
        if (shorthand.equals("Upper Incisor")) {
            return setToString(UPPER_INCISOR);
        }
        if (shorthand.equals("Lower Incisor")) {
            return setToString(LOWER_INCISOR);
        }

        // Check for type sets
        if (shorthand.equals("Wisdom")) {
            return setToString(WISDOM);
        }
        if (shorthand.equals("Molar")) {
            return setToString(MOLAR);
        }
        if (shorthand.equals("Premolar")) {
            return setToString(PREMOLAR);
        }
        if (shorthand.equals("Canine")) {
            return setToString(CANINE);
        }
        if (shorthand.equals("Incisor")) {
            return setToString(INCISOR);
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
        if (shorthand.contains("Wisdom")) {
            if (hasLocation) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(WISDOM);
                result = intersection;
            } else {
                result.addAll(WISDOM);
            }
            hasType = true;
        }
        if (shorthand.contains("Molar")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(MOLAR);
                result = intersection;
            } else {
                result.addAll(MOLAR);
            }
            hasType = true;
        }
        if (shorthand.contains("Premolar")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(PREMOLAR);
                result = intersection;
            } else {
                result.addAll(PREMOLAR);
            }
            hasType = true;
        }
        if (shorthand.contains("Canine")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(CANINE);
                result = intersection;
            } else {
                result.addAll(CANINE);
            }
            hasType = true;
        }
        if (shorthand.contains("Incisor")) {
            if (hasLocation || hasType) {
                // Intersect with existing result
                Set<Integer> intersection = new HashSet<>(result);
                intersection.retainAll(INCISOR);
                result = intersection;
            } else {
                result.addAll(INCISOR);
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
