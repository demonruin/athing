package com.github.ompc.athing.aliyun.thing.container;

import com.github.ompc.athing.aliyun.thing.util.DependentSet;
import com.github.ompc.athing.aliyun.thing.util.ThingComInjectUtils;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.boot.ThDepend;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 组件依赖注入器
 */
class ThDependInjector {

    private final static Injector[] injectors = new Injector[]{

            // 注入ThingCom
            new Injector() {
                @Override
                public boolean inject(DependentSet<ThingCom> dependents, ThingCom target, Field field) throws InjectException {

                    final ThDepend anThDepend = field.getAnnotation(ThDepend.class);
                    final Class<?> type = field.getType();

                    // 如果不是ThingCom类型则立即返回
                    if (!ThingCom.class.isAssignableFrom(type)) {
                        return false;
                    }

                    // 找到匹配的设备组件
                    final Set<ThingCom> founds = dependents.stream()
                            .filter(type::isInstance)
                            .collect(Collectors.toSet());

                    // 找到多于1个
                    if (founds.size() > 1) {
                        throw new InjectException(String.format("not unique! expect: 1, actual: %d",
                                founds.size()
                        ));
                    }

                    // 如果依赖是必须且没找到，则报错
                    if (anThDepend.isRequired() && founds.isEmpty()) {
                        throw new InjectException("not found! expect: 1, actual: 0");
                    }

                    // 只有非空的时候才需要赋值
                    if (!founds.isEmpty()) {
                        // 赋值
                        assignField(field, target, founds.iterator().next());

                        // 更新依赖关系
                        dependents.depends(target, founds.toArray(new ThingCom[0]));
                    }


                    return true;
                }
            },

            // 注入ThingCom[]
            new Injector() {
                @Override
                public boolean inject(DependentSet<ThingCom> dependents, ThingCom target, Field field) throws InjectException {

                    // 如果不是ThingCom[]类型则立即返回
                    if (!field.getType().isArray() || !ThingCom.class.isAssignableFrom(field.getType().getComponentType())) {
                        return false;
                    }

                    final ThDepend anThDepend = field.getAnnotation(ThDepend.class);
                    final Class<?> type = field.getType().getComponentType();

                    // 找到匹配的设备组件
                    final Set<ThingCom> founds = dependents.stream()
                            .filter(type::isInstance)
                            .collect(Collectors.toSet());

                    // 如果依赖是必须且没找到，则报错
                    if (anThDepend.isRequired() && founds.isEmpty()) {
                        throw new InjectException("not found! expect: 1, actual: 0");
                    }

                    // 注入依赖
                    final Object array = Array.newInstance(type, founds.size());
                    int index = 0;
                    for (final ThingCom thingComponent : founds) {
                        Array.set(array, index++, thingComponent);
                    }
                    assignField(field, target, array);

                    // 更新依赖关系
                    dependents.depends(target, founds.toArray(new ThingCom[0]));

                    return true;
                }
            },

    };

    public void inject(String productId, String thingId, DependentSet<ThingCom> dependents) throws ThingException {
        for (final ThingCom thingComponent : dependents) {
            for (final Field field : ThingComInjectUtils.getThDependFields(thingComponent.getClass())) {
                try {
                    injectField(dependents, thingComponent, field);
                } catch (InjectException cause) {
                    throw new ThingException(productId, thingId,
                            String.format("component instance: %s inject filed on %s.%s occur error!",
                                    thingComponent,
                                    field.getDeclaringClass().getName(),
                                    field.getName()
                            ),
                            cause
                    );
                }
            }
        }
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

    private void injectField(DependentSet<ThingCom> dependents, ThingCom target, Field field) throws InjectException {
        // 寻找匹配的注入器注入依赖
        for (final Injector injector : injectors) {
            if (injector.inject(dependents, target, field)) {
                return;
            }
        }
        // 如果没有找到对应的注入器，则说明是不支持的注入类型
        throw new InjectException(String.format("unsupported type: %s",
                field.getType().getName()
        ));
    }


    /**
     * 注入器
     */
    private interface Injector {

        /**
         * 给目标设备组件实例注入依赖属性值
         * thingDependent
         *
         * @param dependents 依赖关系
         * @param target     目标设备组件
         * @param field      目标属性
         * @return true: 注入成功 | false: 放弃注入
         * @throws InjectException 注入失败
         */
        boolean inject(DependentSet<ThingCom> dependents, ThingCom target, Field field) throws InjectException;

        /**
         * 属性赋值
         *
         * @param field  属性
         * @param target 目标对象
         * @param value  属性值
         * @throws InjectException 注入失败
         */
        default void assignField(Field field, ThingCom target, Object value) throws InjectException {
            final boolean isAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                field.set(target, value);
            } catch (Throwable cause) {
                throw new InjectException("assign occur error!", cause);
            } finally {
                field.setAccessible(isAccessible);
            }
        }

    }

}
