package com.stkych.rivergreenap.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TeethNotationUtilTest {

    @Test
    public void testToShorthandPremolars() {
        // Test with all premolars
        String allPremolars = "4-5-12-13-20-21-28-29";
        assertEquals("Premolar", TeethNotationUtil.toShorthand(allPremolars));

        // Test with a subset of premolars
        String somePremolars = "4-5-12-13";
        assertEquals("Upper Premolar", TeethNotationUtil.toShorthand(somePremolars));

        // Test with lower premolars
        String lowerPremolars = "20-21-28-29";
        assertEquals("Lower Premolar", TeethNotationUtil.toShorthand(lowerPremolars));
    }

    @Test
    public void testToShorthandMolars() {
        // Test with all molars
        String allMolars = "2-3-14-15-18-19-30-31";
        assertEquals("Molar", TeethNotationUtil.toShorthand(allMolars));

        // Test with upper molars
        String upperMolars = "2-3-14-15";
        assertEquals("Upper Molar", TeethNotationUtil.toShorthand(upperMolars));

        // Test with lower molars
        String lowerMolars = "18-19-30-31";
        assertEquals("Lower Molar", TeethNotationUtil.toShorthand(lowerMolars));
    }

    @Test
    public void testToShorthandThirdMolars() {
        // Test with all third molars
        String allThirdMolars = "1-16-17-32";
        assertEquals("Wisdom", TeethNotationUtil.toShorthand(allThirdMolars));

        // Test with upper third molars
        String upperThirdMolars = "1-16";
        assertEquals("Upper Wisdom", TeethNotationUtil.toShorthand(upperThirdMolars));

        // Test with lower third molars
        String lowerThirdMolars = "17-32";
        assertEquals("Lower Wisdom", TeethNotationUtil.toShorthand(lowerThirdMolars));
    }

    @Test
    public void testToShorthandCanines() {
        // Test with all canines
        String allCanines = "6-11-22-27";
        assertEquals("Canine", TeethNotationUtil.toShorthand(allCanines));

        // Test with upper canines
        String upperCanines = "6-11";
        assertEquals("Upper Canine", TeethNotationUtil.toShorthand(upperCanines));

        // Test with lower canines
        String lowerCanines = "22-27";
        assertEquals("Lower Canine", TeethNotationUtil.toShorthand(lowerCanines));
    }

    @Test
    public void testToShorthandIncisors() {
        // Test with all incisors
        String allIncisors = "7-8-9-10-23-24-25-26";
        assertEquals("Incisor", TeethNotationUtil.toShorthand(allIncisors));

        // Test with upper incisors
        String upperIncisors = "7-8-9-10";
        assertEquals("Upper Incisor", TeethNotationUtil.toShorthand(upperIncisors));

        // Test with lower incisors
        String lowerIncisors = "23-24-25-26";
        assertEquals("Lower Incisor", TeethNotationUtil.toShorthand(lowerIncisors));
    }

    @Test
    public void testToShorthandLeftRight() {
        // Test with all left teeth
        String allLeft = "9-10-11-12-13-14-15-16-17-18-19-20-21-22-23-24";
        assertEquals("Left", TeethNotationUtil.toShorthand(allLeft));

        // Test with upper left teeth
        String upperLeft = "9-10-11-12-13-14-15-16";
        assertEquals("Upper Left", TeethNotationUtil.toShorthand(upperLeft));

        // Test with lower left teeth
        String lowerLeft = "17-18-19-20-21-22-23-24";
        assertEquals("Lower Left", TeethNotationUtil.toShorthand(lowerLeft));

        // Test with all right teeth
        String allRight = "1-2-3-4-5-6-7-8-25-26-27-28-29-30-31-32";
        assertEquals("Right", TeethNotationUtil.toShorthand(allRight));

        // Test with upper right teeth
        String upperRight = "1-2-3-4-5-6-7-8";
        assertEquals("Upper Right", TeethNotationUtil.toShorthand(upperRight));

        // Test with lower right teeth
        String lowerRight = "25-26-27-28-29-30-31-32";
        assertEquals("Lower Right", TeethNotationUtil.toShorthand(lowerRight));
    }

    @Test
    public void testFromShorthandUpperLeftPremolar() {
        assertEquals("13-14", TeethNotationUtil.fromShorthand("Upper Left Premolar"));
    }
}
