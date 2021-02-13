package com.github.ompc.athing.aliyun.thing.container;

import com.github.ompc.athing.aliyun.thing.util.DependentTree;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.boot.ThDepend;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组件依赖注入器
 */
public class ThDependInjector {

    private final String productId;
    private final String thingId;
    private final Map<String, ThingComStub> stubs;
    private final DependentTree<String> tree;

    public ThDependInjector(String productId, String thingId, Map<String, ThingComStub> stubs, DependentTree<String> tree) {
        this.productId = productId;
        this.thingId = thingId;
        this.stubs = stubs;
        this.tree = tree;
    }

    public void inject(ThingComStub stub, Field field) throws ThingException {

        // 寻找注入器并注入
        try {
            for (final Injector injector : injectors) {
                if (injector.inject(stub, field, stubs, tree)) {
                    return;
                }
            }
        } catch (InjectException cause) {
            throw new ThingException(productId, thingId,
                    String.format("component: %s inject on %s.%s occur error!",
                            stub.getThingComId(),
                            field.getDeclaringClass().getName(),
                            field.getName()
                    ),
                    cause
            );
        }

        // 如果没有找到对应的注入器，则说明是不支持的注入类型
        throw new ThingException(productId, thingId,
                String.format("component: %s inject on %s.%s, but unsupported type: %s",
                        stub.getThingComId(),
                        field.getDeclaringClass().getName(),
                        field.getName(),
                        field.getType().getName()
                )
        );

    }

    /**
     * 依赖组件注入失败
     */
    private static class InjectException extends Exception {

        public InjectException(String message) {
            super(message);
        }

        public InjectException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 注入器
     */
    private interface Injector {

        /**
         * 注入依赖
         *
         * @param stub  目标存根
         * @param field 目标属性
         * @param stubs 设备组件存根集合
         * @param tree  设备组件依赖树
         * @return true:注入成功 | false:放弃注入
         * @throws InjectException 注入失败
         */
        boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree) throws InjectException;

        /**
         * 校验类型是否匹配
         *
         * @param expect 期待类型
         * @param actual 实际类型
         * @throws InjectException 注入失败
         */
        default void verifyType(Class<?> expect, Class<?> actual) throws InjectException {
            if (actual.isAssignableFrom(expect)) {
                throw new InjectException(String.format("type not match, expect: %s, actual: %s",
                        expect.getName(),
                        actual.getName()
                ));
            }
        }

        /**
         * 属性赋值
         *
         * @param field    属性
         * @param thingCom 目标对象
         * @param value    属性值
         * @throws InjectException 注入失败
         */
        default void assignField(Field field, ThingCom thingCom, Object value) throws InjectException {
            final boolean isAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                field.set(thingCom, value);
            } catch (Throwable cause) {
                throw new InjectException("assign occur error!", cause);
            } finally {
                field.setAccessible(isAccessible);
            }
        }

    }

    /**
     * 依赖Id注入器
     */
    private interface ByIdInjector extends Injector {

        @Override
        default boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree) throws InjectException {
            final ThDepend anThDepend = field.getDeclaredAnnotation(ThDepend.class);
            if (anThDepend.value().isEmpty()) {
                return false;
            }

            final String dependId = anThDepend.value();
            final ThingComStub depend = stubs.get(dependId);

            // 指定的依赖ID不存在
            if (null == depend) {
                throw new InjectException(String.format("depend: %s not found!", dependId));
            }

            return inject(stub, field, stubs, tree, depend);
        }

        /**
         * 注入指定ID依赖
         *
         * @param stub   目标存根
         * @param field  目标属性
         * @param stubs  设备组件存根集合
         * @param tree   设备组件依赖树
         * @param depend 指定ID依赖
         * @return true:注入成功 | false:放弃注入
         * @throws InjectException 注入失败
         */
        boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree, ThingComStub depend) throws InjectException;

    }

    /**
     * 依赖类型注入器
     */
    private interface ByTypeInjector extends Injector {

        @Override
        default boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree) throws InjectException {

            // 如果依赖ID有值，说明不是根据类型匹配
            final ThDepend anThDepend = field.getDeclaredAnnotation(ThDepend.class);
            if (!anThDepend.value().isEmpty()) {
                return false;
            }

            // 依赖的类型如果不是ThingCom，则不给予支持
            final Class<?> expect = getExpectType(field);
            if (null == expect || !expect.isAssignableFrom(ThingCom.class)) {
                return false;
            }

            // 根据依赖类型找到匹配的存根
            final Map<String, ThingComStub> depends = stubs.entrySet().stream()
                    .filter(entry -> entry.getValue().getThComMeta().getThingComType().isAssignableFrom(expect))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            // 指定的依赖类型不存在
            if (depends.isEmpty()) {
                throw new InjectException(String.format("depend-type: %s not found!",
                        expect.getName()
                ));
            }

            return inject(stub, field, stubs, tree, expect, depends);
        }

        /**
         * 获取匹配类型
         *
         * @param field 目标属性
         * @return 匹配类型
         */
        Class<?> getExpectType(Field field);

        boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree, Class<?> expect, Map<String, ThingComStub> depends) throws InjectException;

    }


    private final static Injector[] injectors = new Injector[]{

            // 根据ID注入ThingCom
            new ByIdInjector() {

                @Override
                public boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree, ThingComStub depend) throws InjectException {

                    // 如果不是ThingCom类型则立即返回
                    if (!field.getType().isAssignableFrom(ThingCom.class)) {
                        return false;
                    }

                    // 校验找到的组件类型是否匹配期待
                    verifyType(field.getType(), depend.getThComMeta().getThingComType());

                    // 注入依赖
                    assignField(field, stub.getThingCom(), depend.getThingCom());

                    // 更新依赖树
                    tree.depends(stub.getThingComId(), new String[]{depend.getThingComId()});

                    return true;
                }

            },

            // 根据ID注入ThingCom[]
            new ByIdInjector() {

                @Override
                public boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree, ThingComStub depend) throws InjectException {

                    // 如果不是ThingCom[]类型则立即返回
                    if (!field.getType().isArray() || !field.getType().getComponentType().isAssignableFrom(ThingCom.class)) {
                        return false;
                    }

                    // 校验找到的组件类型是否匹配期待
                    final Class<?> componentType = field.getType().getComponentType();
                    verifyType(componentType, depend.getThComMeta().getThingComType());

                    // 注入依赖
                    final Object array = Array.newInstance(componentType, 1);
                    Array.set(array, 0, depend.getThingCom());
                    assignField(field, stub.getThingCom(), array);

                    // 更新依赖树
                    tree.depends(stub.getThingComId(), new String[]{depend.getThingComId()});

                    return true;
                }
            },

            // 根据类型注入ThingCom
            new ByTypeInjector() {

                @Override
                public Class<?> getExpectType(Field field) {
                    return field.getType();
                }

                @Override
                public boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree, Class<?> expect, Map<String, ThingComStub> depends) throws InjectException {

                    // 指定的依赖类型存在多个
                    if (depends.size() > 1) {
                        throw new InjectException(String.format("depend-type: %s not unique, expect: 1, actual: %d, found: %s",
                                expect.getName(),
                                depends.size(),
                                depends.keySet()
                        ));
                    }

                    final ThingComStub depend = depends.values().iterator().next();

                    // 注入依赖
                    assignField(field, stub.getThingCom(), depend.getThingCom());

                    // 更新依赖树
                    tree.depends(stub.getThingComId(), new String[]{depend.getThingComId()});

                    return true;
                }

            },

            // 根据类型注入ThingCom[]
            new ByTypeInjector() {

                @Override
                public Class<?> getExpectType(Field field) {
                    return field.getType().isArray()
                            ? field.getType().getComponentType()
                            : null;
                }

                @Override
                public boolean inject(ThingComStub stub, Field field, Map<String, ThingComStub> stubs, DependentTree<String> tree, Class<?> expect, Map<String, ThingComStub> depends) throws InjectException {

                    // 注入依赖
                    final Object array = Array.newInstance(expect, depends.size());
                    int index = 0;
                    for (final ThingComStub depend : depends.values()) {
                        Array.set(array, index++, depend.getThingCom());
                    }
                    assignField(field, stub.getThingCom(), array);

                    // 更新依赖树
                    tree.depends(stub.getThingComId(), depends.keySet().toArray(new String[0]));

                    return true;
                }
            }

    };

}
