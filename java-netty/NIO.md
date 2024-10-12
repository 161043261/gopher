# NIO

### Non-blocking IO 非阻塞 IO

> channel 数据读写的双向通道

- stream 数据读写的单向通道
- channel 数据读写的双向通道

常见的 channel

- java.nio.channels.FileChannel
- java.nio.channels.DatagramChannel
- java.nio.channels.SocketChannel
- java.nio.channels.ServerSocketChannel

> buffer 数据缓冲区

常见的 buffer

- java.nio.ByteBuffer
    - java.nio.MappedByteBuffer
    - java.nio.DirectByteBuffer（堆外内存）
    - java.nio.HeapByteBuffer（jvm 堆内存）
- java.nio.ShortBuffer
- java.nio.IntBuffer
- java.nio.LongBuffer
- java.nio.FloatBuffer
- java.nio.DoubleBuffer
- java.nio.CharBuffer

**Selector 服务器**：一个线程处理一个 socket 连接

### ByteBuffer

ByteBuffer 属性

- capacity 容量（ByteBuffer 不能动态扩容）
- position 读/写指针
- limit 读/写限制 (position <= limit)

- 读前 flip: 写模式 -> 读模式
- 写前 clear: 丢弃脏数据
- 写前 compact: 不丢弃脏数据
