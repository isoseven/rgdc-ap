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

    // Mapping of shorthand names to tooth sets
    private static final Map<String, Set<Integer>> SHORTHAND_SETS = new LinkedHashMap<>();
    private static final List<String> TYPE_NAMES = Arrays.asList("Wisdom", "Molar", "Premolar", "Canine", "Incisor");

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

        // Populate shorthand mappings
        SHORTHAND_SETS.put("Upper Wisdom", UPPER_WISDOM);
        SHORTHAND_SETS.put("Lower Wisdom", LOWER_WISDOM);
        SHORTHAND_SETS.put("Upper Molar", UPPER_MOLAR);
        SHORTHAND_SETS.put("Lower Molar", LOWER_MOLAR);
        SHORTHAND_SETS.put("Upper Premolar", UPPER_PREMOLAR);
        SHORTHAND_SETS.put("Lower Premolar", LOWER_PREMOLAR);
        SHORTHAND_SETS.put("Upper Canine", UPPER_CANINE);
        SHORTHAND_SETS.put("Lower Canine", LOWER_CANINE);
        SHORTHAND_SETS.put("Upper Incisor", UPPER_INCISOR);
        SHORTHAND_SETS.put("Lower Incisor", LOWER_INCISOR);

        SHORTHAND_SETS.put("Wisdom", WISDOM);
        SHORTHAND_SETS.put("Molar", MOLAR);
        SHORTHAND_SETS.put("Premolar", PREMOLAR);
        SHORTHAND_SETS.put("Canine", CANINE);
        SHORTHAND_SETS.put("Incisor", INCISOR);
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

        Set<Integer> teeth = parseTeethNumbers(teethNumbers);
        if (teeth == null) {
            return teethNumbers;
        }

        String exact = matchExactSet(teeth);
        if (exact != null) {
            return exact;
        }

        List<String> typeMatches = collectTypeMatches(teeth);
        List<String> locationMatches = collectLocationMatches(teeth);

        String result = buildResult(locationMatches, typeMatches);
        return result.isEmpty() ? teethNumbers : result;
    }

    private static Set<Integer> parseTeethNumbers(String teethNumbers) {
        Set<Integer> teeth = new HashSet<>();
        for (String tooth : teethNumbers.split("-")) {
            try {
                teeth.add(Integer.parseInt(tooth.trim()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return teeth;
    }

    private static String matchExactSet(Set<Integer> teeth) {
        for (Map.Entry<String, Set<Integer>> entry : SHORTHAND_SETS.entrySet()) {
            if (entry.getValue().equals(teeth)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static List<String> collectTypeMatches(Set<Integer> teeth) {
        List<String> matches = new ArrayList<>();
        for (String type : TYPE_NAMES) {
            if (SHORTHAND_SETS.get(type).containsAll(teeth)) {
                matches.add(type);
            }
        }
        return matches;
    }

    private static List<String> collectLocationMatches(Set<Integer> teeth) {
        List<String> matches = new ArrayList<>();
        if (UPPER.containsAll(teeth)) {
            matches.add("Upper");
        }
        if (LOWER.containsAll(teeth)) {
            matches.add("Lower");
        }
        if (LEFT.containsAll(teeth)) {
            matches.add("Left");
        }
        if (RIGHT.containsAll(teeth)) {
            matches.add("Right");
        }
        return matches;
    }

    private static String buildResult(List<String> locationMatches, List<String> typeMatches) {
        StringBuilder result = new StringBuilder();
        if (locationMatches.contains("Upper")) {
            result.append("Upper ");
        } else if (locationMatches.contains("Lower")) {
            result.append("Lower ");
        }
        if (locationMatches.contains("Left")) {
            result.append("Left ");
        } else if (locationMatches.contains("Right")) {
            result.append("Right ");
        }

        if (!typeMatches.isEmpty()) {
            result.append(typeMatches.get(0));
            if (typeMatches.size() > 1) {
                result.append(" + ");
                result.append(String.join(" + ", typeMatches.subList(1, typeMatches.size())));
                result.append(" (");
                result.append(String.join(" + ", typeMatches));
                result.append(")");
            }
        }
        return result.toString().trim();
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

        Set<Integer> direct = SHORTHAND_SETS.get(shorthand);
        if (direct != null) {
            return setToString(direct);
        }

        Set<Integer> result = new HashSet<>();
        boolean matched = false;

        if (shorthand.contains("Upper")) {
            update(result, UPPER);
            matched = true;
        } else if (shorthand.contains("Lower")) {
            update(result, LOWER);
            matched = true;
        }

        if (shorthand.contains("Left")) {
            update(result, LEFT);
            matched = true;
        } else if (shorthand.contains("Right")) {
            update(result, RIGHT);
            matched = true;
        }

        for (String type : TYPE_NAMES) {
            if (shorthand.contains(type)) {
                update(result, SHORTHAND_SETS.get(type));
                matched = true;
            }
        }

        return matched ? setToString(result) : shorthand;
    }

    private static void update(Set<Integer> base, Set<Integer> addition) {
        if (base.isEmpty()) {
            base.addAll(addition);
        } else {
            base.retainAll(addition);
        }
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
