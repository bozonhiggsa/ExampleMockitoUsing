package com.application.exampleMockito;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Example of a test with use of Mockito
 */
@RunWith(MockitoJUnitRunner.class)
public class Test_Mockito {

    @Mock
    ICalculator mcalc;

    //ICalculator mcalc = mock(ICalculator.class);

    @InjectMocks
    Calculator calc = new Calculator(mcalc);

    @Test
    public void testCalcAdd() {

        when(calc.add(10.0, 20.0)).thenReturn(30.0);

        assertEquals(calc.add(10, 20), 30.0, 0);
        verify(mcalc).add(10.0, 20.0);

        doReturn(15.0).when(mcalc).add(10.0, 5.0);

        assertEquals(calc.add(10.0, 5.0), 15.0, 0);
        verify(mcalc).add(10.0, 5.0);
    }

    @Test
    public void testCallMethod() {
        when(mcalc.subtract(15.0, 25.0)).thenReturn(10.0);
        when(mcalc.subtract(35.0, 25.0)).thenReturn(-10.0);

        assertEquals (calc.subtract(15.0, 25.0), 10, 0);
        assertEquals (calc.subtract(15.0, 25.0), 10, 0);

        assertEquals (calc.subtract(35.0, 25.0), -10, 0);

        verify(mcalc, atLeastOnce()).subtract(35.0, 25.0);
        verify(mcalc, atLeast(2)).subtract(15.0, 25.0);

        verify(mcalc, times(2)).subtract(15.0, 25.0);
        verify(mcalc, never()).divide(10.0, 20.0);

        // verify(mcalc, atLeast(2)).subtract(35.0, 25.0);

        // verify(mcalc, atMost(1)).subtract(15.0, 25.0);
    }

    @Test(expected = RuntimeException.class)
    public void testDevide() {
        when(mcalc.divide(15.0, 3)).thenReturn(5.0);

        assertEquals(calc.divide(15.0, 3), 5.0, 0);
        verify(mcalc).divide(15.0, 3);

        RuntimeException exception = new RuntimeException ("Division by zero");
        doThrow(exception).when(mcalc).divide(15.0, 0);
        //when(calc.divide(15.0, 0)).thenThrow(exception);

        assertEquals(mcalc.divide(15.0, 0), 0.0, 0);
        verify(mcalc).divide(15.0, 0);
    }

    private Answer<Double> answer = new Answer<Double>() {
        public Double answer(InvocationOnMock invocation) throws Throwable {
            Object mock = invocation.getMock();
            System.out.println ("mock object : " + mock.toString());

            Object[] args = invocation.getArguments();
            double d1 = (Double) args[0];
            double d2 = (Double) args[1];
            double d3 = d1 + d2;
            System.out.println ("" + d1 + " + " + d2);

            return d3;
        }
    };

    @Test
    public void testThenAnswer()
    {
        when(calc.add(11.0, 12.0)).thenAnswer(answer);
        assertEquals(calc.add(11.0,12.0), 23.0, 0);
    }

    @Test
    public void testSpy()
    {
        Calculator scalc = spy(new Calculator());
        when(scalc.double15()).thenReturn(23.0);

        scalc.double15();
        verify(scalc).double15();

        assertEquals(23.0, scalc.double15(), 0);
        verify(scalc, atLeast(2)).double15();
    }

    @Test
    public void testTimout()
    {
        when(mcalc.add(11.0, 12.0)).thenReturn(23.0);
        assertEquals(calc.add(11.0,12.0), 23.0, 0);

        verify(mcalc, timeout(100)).add(11.0, 12.0);
    }

    @Test
    public void testJavaClasses()
    {
        Iterator<String> mis = mock(Iterator.class);
        when(mis.next()).thenReturn("Привет").thenReturn("Mockito");
        String result = mis.next() + ", " + mis.next();
        // проверяем
        assertEquals("Привет, Mockito", result);

        Comparable<String> mcs = mock(Comparable.class);
        when(mcs.compareTo("Mockito")).thenReturn(1);
        assertEquals(1, mcs.compareTo("Mockito"));

        Comparable<Integer> mci = mock(Comparable.class);
        when(mci.compareTo(anyInt())).thenReturn(1);
        assertEquals(1, mci.compareTo(5));
    }
}
