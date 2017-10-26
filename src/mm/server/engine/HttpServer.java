package mm.server.engine;

/**
 * Created by baidu on 16/11/17.
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import mm.server.work.Properties;
import mm.server.work.USRSignalHandler;

public class HttpServer {

    private static Logger logger = Logger.getLogger(HttpServer.class);

    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpServerInboundHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, false);

            ChannelFuture future = null;
            try {
                future = bootstrap.bind(port).sync();
            } catch (InterruptedException e) {
                logger.error("bootstrap bind error", e);
            }

            try {
                if (future != null) {
                    future.channel().closeFuture().sync();
                }
            } catch (InterruptedException e) {
                logger.error("close netty server error", e);
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("server stop!");
        }
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        // USRSignalHandler.listenTo("USR1");

        int port = Integer.parseInt(Properties.getInstance().getProperty("http.port"));
        HttpServer server = new HttpServer();
        logger.info("Http Server listening on " + port + " ...");
        server.start(port);
    }
}
