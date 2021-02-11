package com.wemeet.dating.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericUtils {

    public static List<BigInteger> combineAndStrip(List<BigInteger> a, List<BigInteger> b) {

        List<BigInteger> meshed = new ArrayList<>();


        int aSize = a.size();
        int bSize = b.size();

        int i = 0, j = 0;

        // Traverse both array
        while (i < bSize && j < aSize) {
            meshed.add(b.get(i++));
            meshed.add(a.get(j++));

        }

        // Store remaining elements of first array
        while (i < bSize) {
            meshed.add(b.get(i++));
        }

        // Store remaining elements of second array
        while (j < aSize) {
            meshed.add(a.get(j++));
        }
        meshed = meshed.stream().distinct().collect(Collectors.toList());

        return meshed;


    }
}
