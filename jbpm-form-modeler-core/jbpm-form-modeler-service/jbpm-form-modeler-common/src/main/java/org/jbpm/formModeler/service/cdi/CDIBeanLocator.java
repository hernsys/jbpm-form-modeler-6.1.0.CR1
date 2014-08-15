/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.service.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CDIBeanLocator {

    /** The bean manager. */
    public static BeanManager beanManager;

    public static BeanManager getBeanManager() {
        if (beanManager != null) return beanManager;

        beanManager = lookupBeanManager("java:comp/BeanManager");
        if (beanManager == null) beanManager = lookupBeanManager("java:comp/env/BeanManager");
        return beanManager;
    }

    private static BeanManager lookupBeanManager(String jndiName) {
        try{
            InitialContext initialContext = new InitialContext();
            return (BeanManager) initialContext.lookup(jndiName);
        } catch (NamingException e) {
            // Ignore and return null.
            return null;
        }
    }

    public static Object getBeanByName(String name) {
        BeanManager bm = getBeanManager();
        Bean bean = bm.getBeans(name).iterator().next();
        CreationalContext ctx = bm.createCreationalContext(bean);
        Object o = bm.getReference(bean, bean.getBeanClass(), ctx);
        return o;
    }

    public static Object getBeanByType(Class type) {
        BeanManager bm = getBeanManager();
        Bean bean = bm.getBeans(type).iterator().next();
        CreationalContext ctx = bm.createCreationalContext(bean);
        Object o = bm.getReference(bean, bean.getBeanClass(), ctx);
        return o;
    }

    public static Object getBeanByType(String type) {
        try {
            Class beanClass = Class.forName(type);
            return CDIBeanLocator.getBeanByType(beanClass);
        } catch (Throwable e) {
            // Just ignore
            return null;
        }
    }

    public static Object getBeanByNameOrType(String beanName) {
        try {
            Object beanObject = CDIBeanLocator.getBeanByName(beanName);
            if (beanObject != null) return beanObject;
            return null;
        } catch (Exception e) {
            return getBeanByType(beanName);
        }
    }
}
