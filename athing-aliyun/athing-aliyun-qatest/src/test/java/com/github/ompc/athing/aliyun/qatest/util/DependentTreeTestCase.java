package com.github.ompc.athing.aliyun.qatest.util;

import com.github.ompc.athing.aliyun.thing.util.DependentTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DependentTreeTestCase {

    @Test
    public void test$dependent$success() {
        final DependentTree<String> dependentTree = new DependentTree<>();

        dependentTree.depends("D", new String[]{"E", "F"});
        dependentTree.depends("E", new String[]{"G"});
        dependentTree.depends("A", new String[]{"B", "C"});
        dependentTree.depends("F", new String[]{"A", "H"});
        dependentTree.depends("C", new String[]{"G"});

        final List<String> actual = new ArrayList<>();
        for (final String string : dependentTree) {
            actual.add(string);
        }

        Assert.assertEquals(Arrays.asList("H", "C", "B", "A", "F", "G", "E", "D"), actual);
    }

    @Test(expected = DependentTree.DependentCircularException.class)
    public void test$dependent$failure_circular() {
        final DependentTree<String> dependentTree = new DependentTree<>();

        dependentTree.depends("A", new String[]{"B", "C"});
        dependentTree.depends("C", new String[]{"D", "E"});
        dependentTree.depends("E", new String[]{"B", "C"});

        for (final String string : dependentTree) {
            System.out.println(string);
        }
    }

}
