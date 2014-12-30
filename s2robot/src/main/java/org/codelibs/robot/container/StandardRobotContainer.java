package org.codelibs.robot.container;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.FieldDesc;
import org.codelibs.core.beans.MethodDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.FieldUtil;
import org.codelibs.core.lang.MethodUtil;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardRobotContainer implements RobotContainer {

    private final Logger logger = LoggerFactory
            .getLogger(StandardRobotContainer.class);

    private final Map<String, ComponentHolder<?>> singletonMap = new ConcurrentHashMap<>();

    private final Map<String, ComponentDef<?>> prototypeMap = new ConcurrentHashMap<>();

    private boolean available = true;

    @Override
    public <T> T getComponent(final String name) {
        if ("robotContainer".equals(name)) {
            @SuppressWarnings("unchecked")
            final T t = (T) this;
            return t;
        }

        final ComponentDef<?> componentDef = prototypeMap.get(name);
        if (componentDef != null) {
            @SuppressWarnings("unchecked")
            final T instance = (T) componentDef.get();
            return instance;
        }
        final ComponentHolder<?> componentHolder = singletonMap.get(name);
        if (componentHolder != null) {
            @SuppressWarnings("unchecked")
            final T instance = (T) componentHolder.get();
            return instance;
        }
        return null;
    }

    @Override
    public boolean available() {
        return available;
    }

    public void destroy() {
        available = false;
        for (final ComponentHolder<?> componentHolder : singletonMap.values()) {
            try {
                componentHolder.destroy();
            } catch (final Exception e) {
                logger.warn("Failed to destroy " + componentHolder.get(), e);
            }
        }
    }

    public <T> StandardRobotContainer prototype(final String name,
            final Class<T> cls, final Consumer<T> component) {
        prototypeMap.put(name, new ComponentDef<>(cls, component, this));
        return this;
    }

    public <T> StandardRobotContainer prototype(final String name,
            final Class<T> cls) {
        return prototype(name, cls, null);
    }

    public <T> StandardRobotContainer singleton(final String name,
            final Class<T> cls, final Consumer<T> initializer,
            final Consumer<T> destroyer) {
        final ComponentDef<T> componentDef = new ComponentDef<>(cls,
                initializer, this);
        final T instance = componentDef.get();
        singletonMap.put(name, new ComponentHolder<T>(instance, destroyer));
        return this;
    }

    public <T> StandardRobotContainer singleton(final String name,
            final Class<T> cls, final Consumer<T> initializer) {
        return singleton(name, cls, initializer, (Consumer<T>) null);
    }

    public <T> StandardRobotContainer singleton(final String name,
            final Class<T> cls) {
        return singleton(name, cls, (Consumer<T>) null, (Consumer<T>) null);
    }

    public <T> StandardRobotContainer singleton(final String name,
            final T instance, final Consumer<T> initializer,
            final Consumer<T> destroyer) {
        final ComponentDef<T> componentDef = new ComponentDef<>(instance,
                initializer, this);
        singletonMap.put(name, new ComponentHolder<T>(componentDef.get(),
                destroyer));
        return this;
    }

    public <T> StandardRobotContainer singleton(final String name,
            final T instance, final Consumer<T> initializer) {
        return singleton(name, instance, initializer, null);
    }

    public <T> StandardRobotContainer singleton(final String name,
            final T instance) {
        return singleton(name, instance, null, null);
    }

    protected static class ComponentHolder<T> {
        protected T instance;

        protected Consumer<T> destroyer;

        protected ComponentHolder(final T instance, final Consumer<T> destroyer) {
            this.instance = instance;
            this.destroyer = destroyer;
        }

        protected T get() {
            return instance;
        }

        protected void destroy() {
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(instance
                    .getClass());
            for (final String methodName : beanDesc.getMethodNames()) {
                final MethodDesc methodDesc = beanDesc
                        .getMethodDescNoException(methodName, new Class[0]);
                if (methodDesc != null) {
                    final Method method = methodDesc.getMethod();
                    final PreDestroy postConstruct = method
                            .getAnnotation(PreDestroy.class);
                    if (postConstruct != null) {
                        MethodUtil.invoke(method, instance, new Object[0]);
                    }
                }
            }

            if (destroyer != null) {
                destroyer.accept(instance);
            }
        }
    }

    protected static class ComponentDef<T> {
        protected Class<T> cls;

        protected Consumer<T> initializer;

        protected StandardRobotContainer container;

        private T instance;

        protected ComponentDef(final Class<T> cls,
                final Consumer<T> initializer,
                final StandardRobotContainer container) {
            this.cls = cls;
            this.initializer = initializer;
            this.container = container;
        }

        protected ComponentDef(final T instance, final Consumer<T> initializer,
                final StandardRobotContainer container) {
            this.instance = instance;
            this.initializer = initializer;
            this.container = container;
        }

        protected T get() {
            final T component = this.instance == null ? ClassUtil
                    .newInstance(cls) : this.instance;
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(component
                    .getClass());
            for (final FieldDesc fieldDesc : beanDesc.getFieldDescs()) {
                final Resource annotation = fieldDesc.getField().getAnnotation(
                        Resource.class);
                if (annotation != null) {
                    final Object injected = container.getComponent(fieldDesc
                            .getFieldName());
                    if (injected != null) {
                        FieldUtil
                                .set(fieldDesc.getField(), component, injected);
                    }
                }
            }

            for (final String methodName : beanDesc.getMethodNames()) {
                final MethodDesc methodDesc = beanDesc
                        .getMethodDescNoException(methodName, new Class[0]);
                if (methodDesc != null) {
                    final Method method = methodDesc.getMethod();
                    final PostConstruct postConstruct = method
                            .getAnnotation(PostConstruct.class);
                    if (postConstruct != null) {
                        MethodUtil.invoke(method, component, new Object[0]);
                    }
                }
            }
            if (initializer != null) {
                initializer.accept(component);
            }
            return component;
        }
    }
}