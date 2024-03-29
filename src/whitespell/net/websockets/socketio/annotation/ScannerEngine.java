/**
 * Copyright 2012 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package whitespell.net.websockets.socketio.annotation;

import whitespell.net.websockets.socketio.namespace.Namespace;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ScannerEngine {

    private static final List<? extends AnnotationScanner> annotations =
                    Arrays.asList(new OnConnectScanner(), new OnDisconnectScanner(),
                            new OnEventScanner(), new OnJsonObjectScanner(), new OnMessageScanner());

    public void scan(Namespace namespace, Object object, Class<?> clazz)
            throws IllegalArgumentException {

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            for (AnnotationScanner annotationScanner : annotations) {
                if (method.isAnnotationPresent(annotationScanner.getScanAnnotation())) {
                    annotationScanner.validate(method, clazz);
                    makeAccessible(method);
                    annotationScanner.addListener(namespace, object, clazz, method);
                }
            }
        }

        if (clazz.getSuperclass() != null) {
            scan(namespace, object, clazz.getSuperclass());
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                scan(namespace, object, superIfc);
            }
        }
    }

    private void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

}
