package qx.leizige;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author leizige
 * Echo 客户端
 * {@link io.netty.channel.ChannelHandler.Sharable} 标识一个ChannelHandler可以被多个Channel安全共享
 */
@Slf4j
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    /**
     * 在到服务器的连接已经建立之后将被调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当被通知 Channel 是活跃的时候，发 送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", Charset.defaultCharset()));
    }

    /**
     * 记录已接收 消息的转储
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("Client received : {}", msg.toString(Charset.defaultCharset()));
    }


    /**
     * 在发生异常时， 记录错误并关闭 Channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
