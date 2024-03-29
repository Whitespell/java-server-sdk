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
package whitespell.net.websockets.socketio.handler;

import whitespell.net.websockets.socketio.PacketListener;
import whitespell.net.websockets.socketio.messages.PacketsMessage;
import whitespell.net.websockets.socketio.namespace.Namespace;
import whitespell.net.websockets.socketio.namespace.NamespacesHub;
import whitespell.net.websockets.socketio.parser.Decoder;
import whitespell.net.websockets.socketio.parser.Packet;
import whitespell.net.websockets.socketio.transport.BaseClient;
import whitespell.net.websockets.socketio.transport.NamespaceClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class PacketHandler extends SimpleChannelInboundHandler<PacketsMessage> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PacketListener packetListener;
    private final Decoder decoder;
    private final NamespacesHub namespacesHub;

    public PacketHandler(PacketListener packetListener, Decoder decoder, NamespacesHub namespacesHub) {
        super();
        this.packetListener = packetListener;
        this.decoder = decoder;
        this.namespacesHub = namespacesHub;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketsMessage message)
                throws Exception {
        ByteBuf content = message.getContent();
        BaseClient client = message.getClient();

        if (log.isTraceEnabled()) {
            log.trace("In message: {} sessionId: {}", content.toString(CharsetUtil.UTF_8), client.getSessionId());
        }
        while (content.isReadable()) {
            try {
                Packet packet = decoder.decodePackets(content, client.getSessionId());
                Namespace ns = namespacesHub.get(packet.getEndpoint());

                NamespaceClient nClient = (NamespaceClient) client.getChildClient(ns);
                packetListener.onPacket(packet, nClient);
            } catch (Exception ex) {
                String c = content.toString(CharsetUtil.UTF_8);
                log.error("Error during data processing. Client sessionId: " + client.getSessionId() + ", data: " + c, ex);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        log.error("Exception occurs", e);
    }

}
