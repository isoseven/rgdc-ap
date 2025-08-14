import com.stkych.rivergreenap.util.DentalCodeUtil;
import java.util.List;

class TestMixedCodes {
    public static void main(String[] args) {
        System.out.println("Testing mixed D/N dental codes functionality...");
        
        // Test the exact format from the previous issue
        String testInput = "D1000-1999; N2300-2500";
        System.out.println("Input: '" + testInput + "'");
        
        try {
            List<String> expanded = DentalCodeUtil.expandDentalCodes(testInput);
            System.out.println("Expanded codes count: " + expanded.size());
            
            if (expanded.size() > 0) {
                System.out.println("First 5 codes: " + expanded.subList(0, Math.min(5, expanded.size())));
                System.out.println("Last 5 codes: " + expanded.subList(Math.max(0, expanded.size() - 5), expanded.size()));
                
                // Test compression back
                String compressed = DentalCodeUtil.compressDentalCodes(expanded);
                System.out.println("Compressed back to: '" + compressed + "'");
                
                // Check if we have both D and N codes
                boolean hasDCodes = expanded.stream().anyMatch(c -> c.startsWith("D"));
                boolean hasNCodes = expanded.stream().anyMatch(c -> c.startsWith("N"));
                System.out.println("Has D codes: " + hasDCodes);
                System.out.println("Has N codes: " + hasNCodes);
                
                if (!hasDCodes || !hasNCodes) {
                    System.out.println("ERROR: Missing D or N codes in expansion!");
                }
            }
            
            // Test individual components
            System.out.println("\nTesting individual components:");
            
            String dOnly = "D1000-1005";
            List<String> dExpanded = DentalCodeUtil.expandDentalCodes(dOnly);
            System.out.println("D only input: '" + dOnly + "' -> " + dExpanded);
            
            String nOnly = "N2300-2305";
            List<String> nExpanded = DentalCodeUtil.expandDentalCodes(nOnly);
            System.out.println("N only input: '" + nOnly + "' -> " + nExpanded);
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nTest completed!");
    }
}