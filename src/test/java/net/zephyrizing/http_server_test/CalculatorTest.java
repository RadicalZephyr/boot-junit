package net.zephyrizing.http_server_test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import net.zephyrizing.http_server.Calculator;

public class CalculatorTest {
    @Test
    public void evaluatesExpression() {
        Calculator calculator = new Calculator();
        int sum = calculator.evaluate("1+2+3");
        assertEquals(6, 5);
    }
}
