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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class ResourceHandler extends ChannelOutboundHandlerAdapter {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    private final Map<String, File> resources = new HashMap<String, File>();

    public ResourceHandler(String context) {
        addResource(context + "/single/flashsocket/WebSocketMain.swf",
                        "/single/flashsocket/WebSocketMain.swf");
        addResource(context + "/single/flashsocket/WebSocketMainInsecure.swf",
                        "/single/flashsocket/WebSocketMainInsecure.swf");
    }

    private void addResource(String pathPart, String resourcePath) {
        URL resource = getClass().getResource(resourcePath);
        // in case of usage exclude-swf-files profile
        if (resource != null) {
            File file = new File(resource.getFile());
            resources.put(pathPart, file);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            QueryStringDecoder queryDecoder = new QueryStringDecoder(req.getUri());
            File resource = resources.get(queryDecoder.path());
            if (resource != null) {
                HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);

                if (isNotModified(req, resource)) {
                    sendNotModified(ctx);
                    req.release();
                    return;
                }

                req.release();

                RandomAccessFile raf;
                try {
                    raf = new RandomAccessFile(resource, "r");
                } catch (FileNotFoundException fnfe) {
                    sendError(ctx, NOT_FOUND);
                    return;
                }
                long fileLength = raf.length();

                setContentLength(res, fileLength);
                setContentTypeHeader(res, resource);
                setDateAndCacheHeaders(res, resource);
                writeContent(raf, fileLength, ctx.channel());
                return;
            }
        }
        // TODO Auto-generated method stub
        super.write(ctx, msg, promise);
    }

    private boolean isNotModified(HttpRequest request, File file) throws ParseException {
        String ifModifiedSince = request.headers().get(HttpHeaders.Names.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.equals("")) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client does
            // not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            return ifModifiedSinceDateSeconds == fileLastModifiedSeconds;
        }
        return false;
    }

    private void sendNotModified(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.channel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    private void setDateHeader(HttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        HttpHeaders.setHeader(response, HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));
    }

    private void writeContent(RandomAccessFile raf, long fileLength, Channel ch) throws IOException {
        ChannelFuture writeFuture;
        if (ch.pipeline().get(SslHandler.class) != null) {
            // Cannot use zero-copy with HTTPS.
            writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
        } else {
            // No encryption - use zero-copy.
            final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
            writeFuture = ch.write(region);
            writeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    region.release();
                }
            });
        }

        writeFuture.addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        HttpHeaders.setHeader(response, CONTENT_TYPE, "text/plain; charset=UTF-8");
        ByteBuf content = Unpooled.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8);

        ctx.channel().write(response);

        // Close the connection as soon as the error message is sent.
        ctx.channel().write(content).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param fileToCache
     *            file to extract content type
     */
    private void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        HttpHeaders.setHeader(response, HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        HttpHeaders.setHeader(response, HttpHeaders.Names.EXPIRES, dateFormatter.format(time.getTime()));
        HttpHeaders.setHeader(response, HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        HttpHeaders.setHeader(response,
                HttpHeaders.Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    private void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}
