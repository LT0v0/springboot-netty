package qx.leizige.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientServer implements ApplicationRunner {

    @Value("${netty.client.host}")
    private String inetHost;

    @Value("${netty.client.port}")
    private Integer inetPort;


    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());

        try {
            ChannelFuture future = bootstrap.connect(inetHost, inetPort).sync();
            log.info("客户端成功....");
            //发送消息
            future.channel().writeAndFlush("netty client send message one ...");
            future.channel().writeAndFlush("netty client send message two ...");
            // 等待连接被关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.start();
    }
}
