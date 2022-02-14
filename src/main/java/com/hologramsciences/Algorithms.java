package com.hologramsciences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Algorithms {
    /**
     *
     *  Compute the cartesian product of a list of lists of any type T
     *  the result is a list of lists of type T, where each element comes
     *  each successive element of the each list.
     *
     *  https://en.wikipedia.org/wiki/Cartesian_product
     *
     *  For this problem order matters.
     *
     *  Example:
     *
     *   listOfLists = Arrays.asList(
     *                         Arrays.asList("A", "B"),
     *                         Arrays.asList("K", "L")
     *                 )
     *
     *   returns:
     *
     *   Arrays.asList(
     *         Arrays.asList("A", "K"),
     *         Arrays.asList("A", "L"),
     *         Arrays.asList("B", "K"),
     *         Arrays.asList("B", "L")
     *   )
     *
     *
     *
     */
    public static final <T> List<List<T>> cartesianProductForLists(final List<List<T>> listOfLists) {
        int size = listOfLists.size();
        List<List<T>> cartesian = new ArrayList<List<T>>();

        // Getting the 1st list
        List<T> list1 = listOfLists.get(0);

        // Handling the trivial case
        if (size <= 1) {
            // Breaking down the single list in multiple single-item lists
            list1.forEach(item -> cartesian.add(Arrays.asList(item)));
            return cartesian;
        }
        
        // Using recursion to get the cartesian product of 2nd list to last list
        List<List<T>> subCartesian = cartesianProductForLists(listOfLists.subList(1, size));
        
        // Obtaining cartesian product of 1st list against the subcartesian of 2nd-to-last
        for (int l1 = 0; l1 < list1.size(); l1++) {
            List<T> l1List = Arrays.asList(list1.get(l1));
            for (int l2 = 0; l2 < subCartesian.size(); l2++) {
                // Joining l1List with subCartesian sublist l1                
                List<T> product = Stream.concat(l1List.stream(), subCartesian.get(l2).stream())
                        .collect(Collectors.toList());
                // Alternative equivalent code:
                // List<T> product = new LinkedList<T>(subCartesian.get(l2));
                // product.add(0, list1.get(l1));
                
                // Adding to cartesian product
                cartesian.add(product);
            }
        }
        return cartesian;
    }

    /**
     *
     *  In the United States there are six coins:
     *  1¢ 5¢ 10¢ 25¢ 50¢ 100¢
     *  Assuming you have an unlimited supply of each coin,
     *  implement a method which returns the number of distinct ways to make totalCents
     */
    public static final long countNumWaysMakeChange(final int totalCents) {
        long[] count = new long[totalCents+1]; // Ways to give the change for zero cents to total cents
        Arrays.fill(count, 1); // There is just one way of giving the change with only 1 cent coins
        int[] coinValues = new int[] {5, 10, 25, 50, 100}; // The remaining coin types
        
        // Iterating over the coins, gradually increasing the coin value
        for (int coinValue : coinValues) {
            // Gradually increasing the change total cents
            for (int cents = coinValue; cents <= totalCents; cents++) {
                // System.out.format("count[%d] += count[%d - %d] (which is %d)%n",
                //     cents, cents, coinValue, count[cents - coinValue]);

                // Axiom: given any new coin value, the additional number of ways to give a
                // change of [total cents] must be incremented with the number of ways to give
                // a change for [totalCents minus the coin value], with that new coin already included
                count[cents] += count[cents - coinValue];
            }
        }
        return count[totalCents];
    }
}
