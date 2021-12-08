package qx.leizige.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * netty服务监听器
 */
@Slf4j
@Component
public class NettyServer implements ApplicationRunner {

    /**
     * bossGroup : 主线程组
     * workGroup : 工作线程组
     * bootstrap : 服务端启动对象
     * bootstrap#channel : 服务端通道实现类型
     * bootstrap:option : 线程队列连接数
     */
    public void start(InetSocketAddress socketAddress) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(200);

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ServerChannelInitializer());

        try {
            ChannelFuture future = bootstrap.bind(socketAddress).sync();
            log.info("服务器启动开始监听端口: {}", socketAddress.getPort());
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Value("${netty.server.hostname}")
    private String hostname;

    @Value("${netty.server.port}")
    private Integer serverPort;

    @Override
    public void run(ApplicationArguments arguments) {
        log.info("netty server start ...");
        this.start(new InetSocketAddress(hostname, serverPort));
    }
}
