package radicalzephyr.boot_junit.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Ignore;

import radicalzephyr.boot_junit.test.Calculator;

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

    @Ignore
    @Test
    public void ignoredTest() {
        assertEquals(true, true);
    }
}
