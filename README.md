#并发编程高性能开发框架
### 1、Disruptor与BlockQueue压力测试性能对比

### 2、Disruptor 核心原理与使用 （无锁并行框架）
#### 2.1、Disruptor-QuickStart-基础元素工厂类 (事件驱动模型)
##### 背景：
###### 1、Martin Fowler在自己网站上写了一篇LMAX架构的文章
###### 2、在文章中他介绍了LMAX是一种新型零售金融交易平台
###### 3、它能够以很低的延迟生产大量交易，也就是它能承载非常高的并发
###### 4、这个系统是简历在JVM平台上，其核心是一个业务逻辑处理器
###### 5、它能够在一个线程里每秒处理6百万订单
###### 6、业务逻辑处理器完全是运行在内存中，使用时间源驱动方式
###### 7、业务逻辑处理的核心就是Disruptor
##### 快速使用
###### 1、建立一个工厂Event类，用于创建Event类实例对象，这里的Event类就类似于一条message消息
###### 2、需要一个监听事件类，用于处理数据(Event类),这里相当于写了一个消费端去处理数据
###### 3、实例化Disruptor实例，配置一系列参数，编写Disruptor核心组件
###### 4、编写生产者组件，向Disruptor容器中去投递数据
#### 2.2、Disruptor核心原理
##### 1、初看Disruptor，给人的印象就是RingBuffer是其核心，生产者向RingBuffer中写入元素，消费者从RingBuffer中消费元素

    生产者  ----->  RingBuffer  ----->  消费者
    
##### 2、RingBuffer 正如名字所说一样， 他是一个环 (首尾相接的环)，它用做在不同上下文(线程)间传递数据的buffer 
###### RingBuffer拥有一个序号， 这个序号指向数组中下一个可用元素
##### 3、扔芝麻与捡芝麻的小故事
###### Disruptor说的是生产者和消费者的故事：有一个数组： 生产者往里面扔芝麻，消费者从里面捡芝麻，但是扔芝麻和捡芝麻也是有一个速度的问题
###### 1、消费者捡的比扔的块，那么消费者要停下来，生产者扔了新的芝麻，然后消费者继续 ---------（消费者消费的比生产者快）
###### 2、数组的长度是有限的，生产者到末尾的时候会再从数组的开始位置继续投递芝麻，这个时候可能会追上消费者，消费者还没从哪个地方捡走芝麻，这个时候生产者要等待消费者捡走芝麻，然后继续 ------ （生产者生产的比消费者快）
##### 4、RingBuffer数据结构深入探究
###### 要找到数据中当前序号指向的元素，可以通过mod操作：sequence mod array length = array index (取模操作) 以上面的RingBuffer为例 （java的mod语法） ： 12 % 10 = 2、
###### RingBuffer 的 size 最好是 2的N次方，更有利于二进制的计算机进行计算 
#### 2.3、 Disruptor核心 - RingBuffer
##### 1、RingBuffer ：基于数组的缓存实现，也是创建sequencer与定义WaitStrategy的入口
###### sequencer 是序号， WaitStrategy是等待的策略
##### 2、Disruptor ： 持有RingBuffer、消费者线程池Executor、消费者集合ConsumerRepository等引用
#### 2.4、 Disruptor核心 - Sequence
##### 通过顺序递增的序号来编号，管理进行交换的数据(事件)
##### 对数据(事件)的处理过程总是沿着序号逐个递增处理
##### 有个Sequence 用于更正标示某个特定的事件处理者(RingBuffer/Producer/Consumer)的处理进度
##### Sequence可以看成是一个AtomicLong用于标示进度(功能要比AtomicLong要强大)
##### 还有另外一个目的就是防止不同Sequence之间CPU缓存伪共享(Flase Sharing)的问题
#### 2.5、Disruptor核心 - Sequencer
##### Sequencer 是 Disruptor 的真正核心 (它里边包含Sequence)
##### 此接口有俩个实现类
###### SingleProducerSequencer
###### MultiProducerSequencer
##### 主要实现生产和消费者之间快速、正确地长度数据的并发算法
#### 2.6、Disruptor核心 - Sequence Barrier(序号栅栏)
##### 用于保持对RingBuffer的 Main Published Sequence(Producer) 和 Consumer 之间的平衡关系； Sequence Barrier 还定义了决定 Consumer 是否还有可处理的时间的逻辑 
#### 2.7、Disruptor核心 - WaitStrategy (等待策略)
##### 决定一个消费者将如何等待生产者将数据(Event)置入Disruptor
##### 主要的策略有：（常用）
###### BlockingWaitStratagy：阻塞的方式
###### SleepingWaitStrategy：休眠的方式
###### YieldingWaitStrategy：线程之间互相切换竞争的方式
##### BlockingWaitStrategy ：是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中提供更加一致的性能表现
###### 需要java中的锁
##### SleepingWaitStrategy ：性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的营销最小，适合用于异步日志类似的场景
##### YieldWaitStrategy (无锁并行等待策略)：性能是最好的，适合用于低延迟的系统。在要求极高性能且时间处理线程数小于CPU逻辑核心数的场景中，推荐使用此策略；例如、CPU开启超线程的特性
#### 2.8、Disruptor核心 - Event
##### Event ：从生产者到消费者过程中所处理的数据单元
##### Disruptor中没有代码表示Event，因为它完全是有用户定义的
#### 2.9、Disruptor核心 - EventProcessor(接口，继承Runnable接口)
##### EventProcessor ：主要事件循环，处理Disruptor中的Event，拥有消费者的Sequence
##### 它有一个实现类是BatchEventProcessor，包含了event loop(轮询)有效的实现，并且将回调到一个EventHandler接口的实现对象
    BatchEventProcessor 是 Disruptor 中核心的一部分，它就是通过run方法不断的去轮询获取我们实际的Event数据对象，
    利用消费者等待策略，将数据交给消费者去处理, 
    由于它底层是通过实现了Runnable来run运行的，所以在实例化Disruptor的时候，监听器的个数有几个，线程池的个数就得是几个，否则会出现异常
#### 2.10、Disruptor核心 - EventHandler
##### EventHandler ：由用户实现并且代表了Disruptor中的一个消费者的接口，也就是我们的消费者逻辑都需要写在这里，

#### 2.11、Disruptor - WorkProcessor
##### WorkProcessor ：确保每个Sequence只被一个processor消费，在用一个WorkPool中处理多个WorkProcessor不会消费同样的Sequence


### 3、Disruptor高级应用
#### 3.1、Disruptor核心链路应用场景讲解
##### 核心链路特点 ：至关重要且业务复杂，那么代码应该如何实现？
##### 实现方式一 ：传统的完全解耦模式
##### 实现方式二 ：模板模式 
##### 都不合理解决手段
##### 1、领域模型的高度抽象
##### 2、寻找更好的框架帮助我们进行编码(大中台服务)
##### 使用框架：
###### 1、优先状态机框架，例如Spring-StateMachine 
###### 2、使用Disruptor

#### 3.2、并行计算-串、并行操作
###### EventHandlerGroup < T > handleEventsWith(final EventHandler< ? super T > ... handlers)
##### 串行操作 ：使用链式调用的方式
##### 并行彩妆 ：使用单独调用的方式


#### 3.3、并行计算 - 多边型高端操作
##### Disruptor可以实现串并行同时编码： 
##### 注意：在模拟单消费者的情况下，如果包含多个EventHandler监听，那么在构造Disruptor容器的时候就需要将线程池中的线程数量定为相应的个数

#### 3.4、Disruptor - 多生产者模型讲解
##### 依赖Disruptor配置实现多生产者
    create(ProducerType producerType, 
        EventFactory<E> factory,
        int bufferSize,
        WaitStrategy waitStrategy)
###### 指定ProducerType 为  MULTI

#### 3.5、Disruptor - 多消费者模型讲解
##### 依赖WorkerPool实现多消费者：
    WorkerPool(
            final RingBuffer<T> ringBuffer,
            final SequenceBarrier sequenceBarrier,
            final ExceptionHandler<? super T> exceptionHandler,
            final WorkHandler<? super T>... workHandlers)
 
 
 ### 4、Disruptor深度提升 - 并发编程回顾
 #### 4.1、并发容器类
 ##### ConcurrentMap (接口)
 ###### 实现类：
    ConcurrentHashMap       ---> HashMap
    ConcurrentSkipListMap   ---> TreeMap
###### ConcurrentHashMap 底层主要采用分段锁，分段锁把一个大容器分成了16个小块，然后对于每一段小的 Segment 容器, 它都自己加锁了，也就是并发的时候如果操作的是不同的Segment段的时候，它就能够实现并发的读写

##### CopyOnWrite (接口) ： 写时复制，采用读写分离的思想，它在写的时候，会把原容器Copy一份，这样就产生一个副本了，当有现成要对这个容器进行操作的时候，只会操作它的副本。原容器此时支持读操作
###### 不太适合写特别多的场景，在写的时候使用的是ReentrantLock重入锁
###### 实现类：
    CopyOnWriteArrayList
    CopyOnWriteArraySet
    
##### ArrayBlockQueue、LinkedBlockQueue
##### SynchronousQueue、PriorityBlockQueue
##### DelayQueue带有延迟时间的队列

#### 4.2、并发核心点
##### Volatile
###### 作用一：多线程间的可见性
###### 作用二：阻止JVM指令重排序， 指令重排序其实也就是Happens-Before语义

#### 4.3、Atomic系列类 & Unsafe类
##### Atomic系列类提供了原子性操作，保障多线程下的安全
##### Atomic底层也是使用了Unsafe类，Unsafe类是能直接访问操作系统底层的，java方法是没发访问操作系统底层的，Unsafe类底层是用 C语言 写的
##### Unsafe类的四大作用：
###### 1、对内存级别的操作
###### 2、字段的定位与修改
###### 3、对于线程的挂起和恢复  ----> LockSport 基于线程级别的阻塞和唤醒
###### 4、CAS操作（乐观锁） - CompareAndSwap 包含： 内存位置、预期的值、新的值三个操作数

#### 4.4、J.U.C工具类 
##### CountDownLatch & CyclicBarrier
###### CountDownLatch ： 它是一次的阻塞，可以唤醒多次，假如定了临界点为 3 的时候，当前线程调用了await()方法之后，需要有3次线程调用(包含自身) countDown() 方法，当前线程才能继续执行
###### CyclicBarrier ： 它也可以设置阈值（临界点）。比如设置了临界点为 5 ，当 5 个线程都调用了 await() 方法，已经全部准备就绪之后，一起往下进行执行

##### Future模式与Caller接口
###### Future模式用于异步的提交一个工作，做一个等待，等到异步回调的那个线程真正执行完之后，给我们通过，调用get()方法就能获取数据了，

##### Exchanger线程数据交换器
###### 它可以实现俩个线程数据的交换。 比如A线程和B线程执行完了都有一个结果，他们之间想要交换结果，可以用这个；通常用于一些对账， 比如说A B俩个线程，同时接收同样的数据，进行磁盘IO的读写，这个时候都读完之后，对比一下结果是否一致，

##### ForkJoin并行计算框架
###### 核心原则就是将一个大的任务拆分成若干个小任务，然后去进行并行的Fork拆分，最后在Join整合。它的核心原理就是递归，需要一个临界值

##### Semaphore 信号量
###### 信号量底层也是AQS的体现，并发访问的时候可以用它来控制，同一时间点允许并发访问的线程数由我们自己定义，超出的线程就会在AQS队列中进行等待，
###### AQS其实是一个队列， 它可以实现非公平和公平这俩种竞争的方案，

#### 4.5、AQS锁
##### ReentrantLock重入锁
###### 为什么要叫重入锁呢？ 因为这是基于AQS机制，AQS里边非两大块，一个是共享资源state，一个是共享队列 AbstractQueueSynchronizor, 重入的概念就是一个线程可以反复的或者和ReentrantLock，它里边的共享变量state是要累加的，

##### ReentrantReadWriteLock 读写锁
###### 它能够实现读写分离，如果完全是读的时候可以不上锁，如果有读有写或者全是写，就要加锁 ----- 口诀 : 读读共享、读写互斥、写写互斥
##### Condition 条件判断
###### 配合前边俩个一起用
##### LockSupport 基于线程的锁 操作系统底层的类 基于线程的锁, 
##### LockSupport应用特别灵活， 不用去考虑线程阻塞和唤醒的顺序， 因为他太底层了，是基于线程的锁(同一个线程) 
###### LockSupport底层的唤醒和阻塞也是基于AQS的

#### 4.6 线程池核心
##### Executors工厂类
###### 尽量不要使用Executors工厂类中任何创建线程的工厂方法，因为他都是没有限制的，会存在隐患，要用就用自定义线程池， TheadPoolExecutor
#####  ThreadPoolExecutor 自定义线程池
##### 计算机密集型与IO密集型（线程池的考量）
###### 计算机密集型 ： -----  可以理解为 CPU 密集型   和CPU个数差不多
    公式为 ： CPU核数 + 1 或者  CPU核 * 2
###### IO密集型 ： -----  实际进行网络通信读写的     一般为CPU个数的 5 -10 倍
    公式为 ： CPU核数 / (1 - 阻塞系数)   ---- 阻塞系数一般为 0.8 - 0.9
###### 例如 ：
    如果当前的业务的业务场景是完全的内存级别的计算或者是复杂的运算，不涉及到任何IO操作，这个时候就选择 --- 计算机密集型
    对数据进行存储、对数据从网络中读取，将数据保存到文件中、数据库操作都是IO操作，IO操作相对慢一些， 所以可以把线程池中线程数调的大一点
##### 如何正确的使用线程池
###### 1、线程池大小和线程池数量，设置界限
###### 2、在某一线程执行之前和之后要做一些日志，用ES等日志收集框架来收集起来
###### 3、一定要进行优雅的关闭线程池 

#### 4.7、AQS架构(AbstractQueueSynchronizor)
###### 核心 - 资源 （state）， 它是volatile关键字修饰的一个int类型的变量
###### 核心 - CLH队列 （FIFO），一个先进先出的队列，包含一个head节点、一个tail节点，当前第一个标杆节点拿到这个共享资源state之后，其他的节点就得排队等待(公平锁的概念),标杆节点处理完之后，会通知下一个节点来处理任务，也就下一个节点成为标杆节点，如果有新加入的节点，就维护到队列的尾部
##### AQS维护了一个volatile int state(代表共享资源) 和一个FIFO线程等待队列(多线程争用资源被阻塞是会进入此队列) 
##### AQS定义俩种资源共享方式 ： Exclusive(独占)， Share(共享)
###### Exclusive(独占资源方式)： ReentrantLock
    因为加锁了之后，其他线程进不来，只能等着
###### Share(共享资源方式) ：Semaphore
    多个线程可以同时去执行，执行完之后可以去释放
##### isHeldExclusively方法 ：该线程是否正在独占资源
##### tryAcquire / tryRelease ：独占的方式尝试获取和释放资源
    成功返回 ：true
    失败返回 ：false
##### tryAcquireShared / tryReleaseShared ：共享方式尝试获取和释放资源
    如果是负数 ：获取失败
    如果是0    ：没有可用的资源
    如果是正数 ：返回可用的资源的个数
    
#### 4.8、AQS - 底层代码分析
##### 以ReentrantLock重入锁为例，state初始化为0，表示未锁定状态
##### A线程lock()时， 会调用tryAcquire()去获取许可，独占该锁并将state + 1
##### 此后，其他的线程再去tryAcquire()时就会失败，直到A线程unlock()到 state=0 (即释放锁)为止，其它线程才有机会获取该锁
##### 当然，释放锁之前，A线程自己是可以重复获取此锁的(state会累加)， 这就是可重入的概念
##### 但要注意，获取多少次就要释放多少次，这样才能保证state是能回到零态的

##### ———————————— 
##### 以CountDownLatch为例， 任务分为N个子线程去执行， state也初始为N （注意N要与线程个数一致）
##### 这N个子线程是并行执行的，每个子线程执行完后 countDown() 一次， state 会CAS减1
##### 等到所有子线程都执行完后(即 state = 0)，会unpark()调用线程 ,使线程恢复
##### 然后主调用线程就会从await() 函数返回，继续后余动作


### 5、Disruptor深度提升 - 源代码分析
#### 5.1、Disruptor为何底层性能如此牛？
##### 1、数据结构层面：使用环形结构、数组、内存预加载
###### RingBuffer使用数组Object[] entries作为存储元素，先把RingBuffer中的每个event对象new出来，达到内存预加载的机制，

##### 2、使用单线程写方式，内存屏障

##### 3、消除伪共享（填充缓存行）
##### 4、序号栅栏和序号配合使用来消除JDK的锁和CAS

#### 5.2、高性能之道 - 内核 - 使用单线程写
##### Disruptor的RingBuffer， 之所以可以做到完全无锁，也是因为 "单线程写"， 这是所有 "前提的前提"
##### 离了这个前提条件，没有任何技术可以做到完全无锁
##### Redis、Netty 等等高性能技术框架的设计都是这个核心思想  ---- 单线程写

#### 5.3、高性能之道 - 系统内存优化 - 内存屏障
##### 要正确的实现无锁，还需要另外一个关键技术 : 内存屏障。
##### 对应到Java语言，就是volatile变量与happens before语义
##### 内存屏障 - Linux的smp-wmb()/smp_rmb()
##### 内存屏障可以理解为一条CPU指令，这条指令的作用有：
###### 阻止指令的重排序，插入一个内存屏障之后，屏障之后的代码不会被重排序到屏障之前
###### 写屏障和读屏障
##### 系统内核：拿Linux的kfifo来举例：smp_wmb(), 无论是底层的读写都是使用了Linux的smp_wmb
#### 5.4、高性能之道 - 算法优化 - 序号栅栏机制 
##### 我们在生产者进行投递Event的时候，总是会使用：long sequence = ringBuffer.next();
##### Disruptor3.0中，序号栅栏SequenceBarrier和序号Sequence搭配使用：协调和管理消费者与生产者的工作节奏，避免了锁和CAS的使用
##### 在Disruptor3.0中，各个消费者和生产者持有自己的序号，这些序号的变化必须满足如下的基本条件
###### 1、消费者的序号数值必须小于生产者序号数值
###### 2、消费者序号数值必须小于其前置 (依赖关系) 消费者的序号数值 
###### 3、生产者序号的数值不能大于消费者中最小的序号数值，以避免生产者速度过快，将还未来得及消费的消息覆盖

#### 5.5、WaitStrategy等待策略深度分析
##### 1、Disruptor之所以说是高性能，其实也有一部分原因取决于他等待策略的实现： WaitStrategy接口


#### 5.6、EventProcessor 核心机制深度分析
##### EventProcessor实现类： BatchEventProcessor代码核心分析(单消费者时使用)

##### WorkProcessor实现类： WorkEventProcessor代码核心分析(多消费者时使用)


### 6、Disruptor 实战应用业务场景架构设计与分析
#### 6.1、实战应用业务场景 - Disruptor与Netty实现百万级场链接接入
##### 与Netty网络通信框架整合提升性能
###### 在使用Netty进行接收处理数据的时候，我们尽量都不要在工作线程上全编写自己的代码逻辑
###### 我们需要利用异步的机制，比如使用线程池异步处理，如果使用线程池就意味着使用阻塞队列，这里可以替换为Disruptor提高性能
#### 6.2、实战应用业务场景 - 分布式统一ID生成策略抗压
##### 对于ID的生成， 在我们日常开发里应该是一个最基本的问题，我们先来说说最基础的ID的生成策略：
###### 1、最简单的就是利用java.util.UUID工具类进行生成，ID没有排序策略，这种方式额问题就是比如我要查询一批数据，进行入口时间做数据排序的时候，只能够自己在表里设置一个create_time，给这个字段添加索引然后进行排序
###### 2、(雪花算法/数据库sequence序列、自增ID等)
##### 顺序ID生成方式：
###### 我们通过代码，KeyUtil里生成的ID是有时间先后顺序的，我们可以使用ID天然进行排序，这做法比较好的就是没必要浪费一个索引字段了，从数据库的角度来讲，一般能尽量减少索引，就减少索引。因为索引虽然可以提升查询性能，但也是需要占用空间的，并且一张表的最好索引不要超过3个，所以在做表优化的时候，往往也是要根据业务进行考量。

##### 业务ID生成方式
###### 最实用带有业务含义的ID生成策略，这种方式也在传统应用系统、特定的场景下非常的好用。比如我们现在有一张商品货架表，这张表的数据维度是这样的，比如是按照城市和区域来划分的
###### 比如北京我们按照1000000为基本维度数据，100010为北京的一个区域，100020则为另一个区域，以此类推，200000可能是另一个城市，200010则为另一个城市的区域。那么我们在生成货架信息ID的时候，可以按照前六位为城市和区域的方式进行组织，后面可以拼接一个简单的32位的UUID字符集


