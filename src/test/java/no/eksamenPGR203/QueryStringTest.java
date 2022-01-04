package no.eksamenPGR203;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryStringTest {

    @Test
    public void doesItWork() throws UnsupportedEncodingException {
        String test = "status=400&id=2050";
        HashMap<String, String> testingOnly = QueryString.queryString(test);
        assertTrue(testingOnly.get("status").equals("400"));
        assertTrue(testingOnly.get("id").equals("2050"));
    }
}
