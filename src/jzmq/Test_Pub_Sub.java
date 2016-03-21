package jzmq;

import org.junit.Test;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

/***
 * @author solq <br>
 *         目前测试发送得太快会丢消息。。。。。。解决办法 发送一定频率sleep
 */
public class Test_Pub_Sub extends TestCtx {
    public static void main(String[] args) throws InterruptedException {
	final int count = 300000;
	final Socket pub = ZMQ.context(1).socket(ZMQ.PUB);
	pub.bind("tcp://*:5555");
	// pub.setSndHWM(count);
	// System.out.println(pub.getSndHWM());
	Thread t = new Thread(new Runnable() {
	    public void run() {
		final Socket sub = ZMQ.context(1).socket(ZMQ.SUB);
		sub.connect("tcp://localhost:5555");
		sub.subscribe("abc".getBytes());
		int _count = count;
		String body = null;
		while (--_count > 0) {
		    body = new String(sub.recv(0));
		    System.out.println(_count);
		}
		System.out.println(body);
		sub.close();
	    }
	});
	t.start();
	Thread.sleep(500);
	long start = System.currentTimeMillis();
	for (int i = 0; i < count; i++) {
	    // TODO
	    pub.send("abc hello" + "_" + i);
	    if (i % 1000 == 0)
		Thread.sleep(10);
	}
	long end = System.currentTimeMillis();
	System.out.println("push time :" + (end - start));

	t.join();
	end = System.currentTimeMillis();
	System.out.println("all time :" + (end - start));
    }

    @Test
    public void mutilSub() throws InterruptedException {
	final int count = 100000;
	int threadCount = Runtime.getRuntime().availableProcessors() * 50;
	int client = threadCount;
	final Socket pub = ZMQ.context(1).socket(ZMQ.PUB);
	pub.bind("tcp://*:5555");
	Thread[] threads = new Thread[threadCount];

	for (int i = 0; i < threadCount; i++) {
	    threads[i] = new Thread(new Runnable() {
		public void run() {
		    final Socket sub = ZMQ.context(1).socket(ZMQ.SUB);
		    sub.connect("tcp://localhost:5555");
		    sub.subscribe("abc".getBytes());
		    int _count = count;
		    String body = null;
		    long start = System.currentTimeMillis();
		    boolean first = true;
		    while (--_count > 0) {
			body = new String(sub.recv(0));
			if (first) {
			    first = false;
			    System.out.println(Thread.currentThread().getName() + ":" + body);
			}
		    }
		    long end = System.currentTimeMillis();
		    System.out.println(Thread.currentThread().getName() + ":" + (end - start));

		    sub.close();
		}
	    });
	    threads[i].setDaemon(true);
	    threads[i].start();
	}
	Thread.sleep(5000);
	// 8个客户端 等侍15毫秒
	int time = client / 8 * 15;
	time = Math.max(15, time);
	System.out.println("wait time : " + time);

	long start = System.currentTimeMillis();

	for (int i = 0; i < count; i++) {
	    // TODO
	    pub.send("abc hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello" + "_" + i);
	    if (i % 1000 == 0) {
		Thread.sleep(time);
	    }
	}
	long end = System.currentTimeMillis();
	System.out.println("push time :" + (end - start));

	for (int i = 0; i < threadCount; i++) {
	    threads[i].join();
	}
    }

}
