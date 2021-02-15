package com.github.ompc.athing.aliyun.qatest.util;

import com.github.ompc.athing.aliyun.thing.util.DependentSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DependentSetTestCase {

    private <E> void assertIsBehind(List<E> list, E target, E[] elements) {

        for (final E element : elements) {
            Assert.assertTrue(list.indexOf(element) < list.indexOf(target));
        }

    }

    @Test
    public void test$dependent$success() {
        final DependentSet<String> dependents = new DependentSet<>();

        dependents.depends("D", new String[]{"E", "F"});
        dependents.depends("E", new String[]{"G"});
        dependents.depends("A", new String[]{"C"});
        dependents.depends("F", new String[]{"A", "H"});
        dependents.depends("C", new String[]{"G"});
        dependents.depends("A", new String[]{"B"});

        final List<String> list = new ArrayList<>(dependents);

        assertIsBehind(list, "A", new String[]{"B", "C", "G"});
        assertIsBehind(list, "C", new String[]{"G"});
        assertIsBehind(list, "D", new String[]{"E", "F", "H", "A", "B", "C", "G"});
        assertIsBehind(list, "E", new String[]{"G"});
        assertIsBehind(list, "F", new String[]{"H"});


    }

    @Test(expected = DependentSet.DependentCircularException.class)
    public void test$dependent$failure_circular() {
        final DependentSet<String> dependentSet = new DependentSet<>();

        dependentSet.depends("A", new String[]{"B", "C"});
        dependentSet.depends("C", new String[]{"D", "E"});
        dependentSet.depends("E", new String[]{"B", "C"});

    }


}
