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
package whitespell.net.websockets.socketio.namespace;

import whitespell.net.websockets.socketio.SocketIOClient;
import whitespell.net.websockets.socketio.misc.CompositeIterable;
import whitespell.net.websockets.socketio.parser.JsonSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NamespacesHub {

    private final ConcurrentMap<String, Namespace> namespaces = new ConcurrentHashMap<String, Namespace>();
    private final JsonSupport jsonSupport;

    public NamespacesHub(JsonSupport jsonSupport) {
        this.jsonSupport = jsonSupport;
    }

    public Namespace create(String name) {
        Namespace namespace = namespaces.get(name);
        if (namespace == null) {
            namespace = new Namespace(name, jsonSupport);
            Namespace oldNamespace = namespaces.putIfAbsent(name, namespace);
            if (oldNamespace != null) {
                namespace = oldNamespace;
            }
        }
        return namespace;
    }

    public <T> Iterable<SocketIOClient> getRoomClients(T roomKey) {
        List<Iterable<SocketIOClient>> allClients = new ArrayList<Iterable<SocketIOClient>>();
        for (Namespace namespace : namespaces.values()) {
            Iterable<SocketIOClient> clients = namespace.getRoomClients(roomKey);
            allClients.add(clients);
        }
        return new CompositeIterable<SocketIOClient>(allClients);
    }

    public Namespace get(String name) {
        return namespaces.get(name);
    }

    public void remove(String name) {
        Namespace namespace = namespaces.remove(name);
        namespace.getBroadcastOperations().disconnect();
    }

}
