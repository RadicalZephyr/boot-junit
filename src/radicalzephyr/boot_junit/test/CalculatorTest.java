package radicalzephyr.boot_junit.test;

import radicalzephyr.boot_junit.test.Calculator;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CalculatorTest {
    @Test
    public void evaluatesExpression() {
        Calculator calculator = new Calculator();
        int sum = calculator.evaluate("1+2+3");
        assertEquals(6, sum);
    }

    @Test
    public void reportsErrors() {
        assertEquals(true, false);
    }
}
