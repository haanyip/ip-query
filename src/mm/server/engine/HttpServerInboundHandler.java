package mm.server.engine;

/**
 * Created by baidu on 16/11/17.
 */

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.channel.ChannelFutureListener;

import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;

import mm.server.work.IpRequestDTO;
import mm.server.work.Properties;

public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = Logger.getLogger(HttpServerInboundHandler.class);
    private static Map<String, Reader> readerMap = new HashMap<String, Reader>();

    private static Reader getDbHandler(String dbName) throws IOException {
        File database;
        Reader reader;
        String dbFile;

        dbFile = Properties.getInstance().getProperty("file." + dbName);
        if ( dbFile == null) {
            dbFile = Properties.getInstance().getProperty("file.city");
        }

        reader = readerMap.get(dbFile);

        if (reader == null) {
            database = new File("dbFile");
            reader = new Reader(database, new CHMCache());
            readerMap.put(dbFile, reader);
        }

        return reader;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ( msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            IpRequestDTO ipRequest = IpRequestDTO.parseUri(request.getUri());

            String res = "error input ip address";
            HttpResponseStatus code = BAD_REQUEST;
            if (ipRequest != null) {
                try {
                    Reader reader = getDbHandler(ipRequest.getDbName());
                    InetAddress address = InetAddress.getByName(ipRequest.getIp());
                    JsonNode location = reader.get(address);
                    res = location.toString();
                    code = OK;
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    logger.error("error:" + sw.toString());

                    code = INTERNAL_SERVER_ERROR;
                }
            }

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, code,
                    Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH,
                    response.content().readableBytes());

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        if ( msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            buf.release();
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage());

        String res = "server error";
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1, INTERNAL_SERVER_ERROR,
                    Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH,
                    response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch ( UnsupportedEncodingException e) {
            // ignore
        }

    }

}
