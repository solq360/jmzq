package jzmq;

import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class Test_Router_Dealer extends TestCtx {

    // 应答模式：queue XREP/XREQ
    // 订阅模式：forwarder SUB/PUB
    // 分包模式：streamer PULL/PUSH
    public static void main(String args[]) {
	final int count = 3000000;
	final ZMQ.Context context = ZMQ.context(1);
	ZMQ.Socket router = context.socket(ZMQ.PULL);
	ZMQ.Socket dealer = context.socket(ZMQ.PUSH);
	router.bind("tcp://*:5555");
	dealer.bind("tcp://*:5566");// worker

	// final ZMQ.Poller poller = new ZMQ.Poller(2); // 创建一个大小为2的poller
	// poller.register(router, ZMQ.Poller.POLLIN); //
	// 分别将上述的pull注册到poller上，注册的事件是读
	// poller.register(dealer, ZMQ.Poller.POLLIN);
	for (int i = 0; i < 2; i++) {
	    new Thread(new Runnable() {
		public void run() {
		    ZMQ.Socket pub = context.socket(ZMQ.PULL);
		    pub.connect("tcp://localhost:5566");
		    int value = count;
		    String str = null;
		    long start = System.currentTimeMillis();

		    while (value-- > 0) {
			str = new String(pub.recv());
			if (value % 100000 == 0) {
			    System.out.println(Thread.currentThread().getName() + ":" + str);
			}
		    }

		    long end = System.currentTimeMillis();
		    System.out.println("all time :" + (end - start));
		    pub.close();
		}

	    }).start();
	}
	for (int i = 0; i < 1; i++) {
	    new Thread(new Runnable() {
		public void run() {
		    ZMQ.Socket push = context.socket(ZMQ.PUSH);
		    push.connect("tcp://localhost:5555");
		    int value = count;
		    while (value-- > 0) {
			push.send("hello :" + value);
		    }
		    push.close();
		}

	    }).start();
	}

	ZMQ.proxy(dealer, router, null);

	// String actual = null;
	// while (true) {
	// poller.poll();
	// if (poller.pollin(0)) {
	//
	// }
	// if (poller.pollin(1)) {
	//
	// }
	// }

	// router.close();
	// dealer.close();
	// context.term();
    }

    @Test
    public void testBase() {
	final ZMQ.Context context = ZMQ.context(1);
	ZMQ.Socket router = context.socket(ZMQ.ROUTER);
	ZMQ.Socket dealer = context.socket(ZMQ.DEALER);
	router.bind("tcp://*:5555");

	dealer.setIdentity("A".getBytes());
	dealer.connect("tcp://localhost:5555");// worker

	dealer.send("dealer send");
	System.out.println("router recv :" + new String(router.recv()));
	System.out.println("router recv :" + new String(router.recv()));

	router.send("A", ZMQ.SNDMORE);
	router.send("END");
	System.out.println("dealer recv :" + new String(dealer.recv()));
    }

    @Test
    public void testProxyReq_Rep() throws Exception {
	final ZMQ.Context context = ZMQ.context(1);

	Thread thread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		Socket requesterEndpoint = context.socket(ZMQ.ROUTER);
		Socket workerEndpoint = context.socket(ZMQ.DEALER);
		requesterEndpoint.bind("inproc://requests");
		workerEndpoint.bind("inproc://work");
		ZMQ.proxy(workerEndpoint, requesterEndpoint, null);
	    }
	});
	thread.start();
	Thread.sleep(100L);

	Socket client = context.socket(ZMQ.REQ);
	Socket worker = context.socket(ZMQ.REP);
	client.connect("inproc://requests");
	worker.connect("inproc://work");

	client.send("hello");
	byte[] response = worker.recv();
	Assert.assertArrayEquals("hello".getBytes(), response);

	worker.send("goodbye");
	byte[] response2 = client.recv();
	Assert.assertArrayEquals("goodbye".getBytes(), response2);

    }
}