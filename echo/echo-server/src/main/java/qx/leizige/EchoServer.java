package qx.leizige;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author leizige
 * 引导服务器
 */
@Slf4j
@AllArgsConstructor
public class EchoServer {

    private final int port;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            log.error("Usage: " + EchoServer.class.getSimpleName() + " <port>");
            return;
        }
        log.info("echo server port:{}", args[0]);
        new EchoServer(Integer.parseInt(args[0])).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建ServerBootStrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)  //指定所使用的Nio传输Channel
                    .localAddress(new InetSocketAddress(port))  //使用指定端口设置套接字地址
                    .option(ChannelOption.SO_BACKLOG, 128)  //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //添加一个EventHandler到子ChannelHandler的ChannelPipeline
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            //异步绑定服务器,调用sync方法阻塞等待直到绑定完成
            ChannelFuture channelFuture = bootstrap.bind().sync();
            log.info(EchoServer.class.getName() + " started and listening for connections on " + channelFuture.channel().localAddress());
            //获取Channel的closeFuture,并且阻塞当前线程直到完成
            channelFuture.channel().closeFuture().sync();
        } finally {
            //关闭 EventLoopGroup,释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

}
