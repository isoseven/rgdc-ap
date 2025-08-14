import com.stkych.rivergreenap.util.DentalCodeUtil;
import java.util.List;

class TestDentalCodes {
    public static void main(String[] args) {
        System.out.println("Testing mixed D/N dental codes...");
        
        // Test the exact format requested in the issue
        String testInput = "D1000-1999; N2300-2500";
        System.out.println("Input: " + testInput);
        
        List<String> expanded = DentalCodeUtil.expandDentalCodes(testInput);
        System.out.println("Expanded codes count: " + expanded.size());
        System.out.println("First few D codes: " + expanded.subList(0, Math.min(5, expanded.size())));
        System.out.println("Last few codes (should be N): " + expanded.subList(Math.max(0, expanded.size() - 5), expanded.size()));
        
        // Test compression back
        String compressed = DentalCodeUtil.compressDentalCodes(expanded);
        System.out.println("Compressed back to: " + compressed);
        
        // Test individual components
        System.out.println("\nTesting individual components:");
        String dRange = "D1000-1999";
        List<String> dExpanded = DentalCodeUtil.expandDentalCodes(dRange);
        System.out.println("D range expanded count: " + dExpanded.size());
        System.out.println("D range sample: " + dExpanded.subList(0, 3) + " ... " + dExpanded.subList(dExpanded.size()-3, dExpanded.size()));
        
        String nRange = "N2300-2500";
        List<String> nExpanded = DentalCodeUtil.expandDentalCodes(nRange);
        System.out.println("N range expanded count: " + nExpanded.size());
        System.out.println("N range sample: " + nExpanded.subList(0, 3) + " ... " + nExpanded.subList(nExpanded.size()-3, nExpanded.size()));
        
        System.out.println("\nTest completed successfully!");
    }
}