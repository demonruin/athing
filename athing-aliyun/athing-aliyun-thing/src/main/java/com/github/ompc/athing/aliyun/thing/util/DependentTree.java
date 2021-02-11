package com.github.ompc.athing.aliyun.thing.util;

import java.util.*;

/**
 * 依赖树
 *
 * @param <E> 元素
 */
public class DependentTree<E> implements Iterable<E> {

    private final Set<Node> nodes = new HashSet<>();

    /**
     * 添加依赖
     * <p>
     * {@code target}依赖了{@code depends}元素
     * </p>
     *
     * @param target  目标元素
     * @param depends 依赖元素
     */
    public final void depends(E target, E[] depends) {
        final Node targetNode = wrap(target);
        if (null != depends && depends.length > 0) {
            for (final E depend : depends) {
                targetNode.depends.add(wrap(depend));
            }
        }
    }

    // 封装元素为节点
    private Node wrap(E element) {
        for (final Node node : nodes) {
            if (Objects.equals(node.element, element)) {
                return node;
            }
        }
        final Node node = new Node(element);
        nodes.add(node);
        return node;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private final Stack<Node> stack = init(new Stack<>(), new Stack<>(), nodes);

            private Stack<Node> init(Stack<Node> stack, Stack<E> track, Collection<Node> nodes) {
                nodes.forEach(node -> {
                    checkCircularDependency(node.element, track);
                    if (!stack.contains(node)) {
                        stack.push(node);
                    }
                    track.push(node.element);
                    init(stack, track, node.depends);
                    track.pop();
                });
                return stack;
            }

            private void checkCircularDependency(E target, Stack<E> track) {
                if (track.contains(target)) {
                    final StringBuilder pathSB = new StringBuilder();
                    track.forEach(element -> {
                        if (Objects.equals(element, target)) {
                            pathSB.append("[").append(element).append("]").append("->");
                        } else {
                            pathSB.append(element).append("->");
                        }

                    });
                    pathSB.append("[").append(target).append("]");
                    throw new DependentCircularException(String.format("%s is circular reference: %s",
                            target,
                            pathSB.toString()
                    ));
                }
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public E next() {
                return stack.pop().element;
            }
        };
    }


    /**
     * 节点
     */
    private class Node {

        private final E element;
        private final Collection<Node> depends = new LinkedList<>();

        Node(E element) {
            this.element = element;
        }

    }

    /**
     * 依赖异常
     */
    public static class DependentException extends RuntimeException {

        DependentException(String message) {
            super(message);
        }

    }

    /**
     * 循环依赖异常
     */
    public static class DependentCircularException extends DependentException {

        DependentCircularException(String message) {
            super(message);
        }

    }

}
