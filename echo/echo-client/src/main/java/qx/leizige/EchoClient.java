package qx.leizige;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author leizige
 * 引导客户端
 */
@Slf4j
@AllArgsConstructor
public class EchoClient {


    private final String host;
    private final int port;


    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            log.error("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        log.info("echo client host:{},port:{}", host, port);
        new EchoClient(host, port).start();
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();  //创建 Bootstrap
            bootstrap.group(group)  //指定 EventLoopGroup 以 处理客户端事件;需要适 用于 NIO 的实现
                    .channel(NioSocketChannel.class)    //适用于 NIO 传输的 Channel 类型
                    .remoteAddress(new InetSocketAddress(host, port))   //设置服务器的InetSocketAddress
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //在创建 Channel 时 向 ChannelPipeline 中添加一个 Echo- ClientHandler 实例
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();   //连接到远程节点，阻塞等待直到连接完成
            channelFuture.channel().closeFuture().sync();   //阻塞，直到 Channel 关闭
        } finally {
            group.shutdownGracefully().sync();
        }
    }


}
