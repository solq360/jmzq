package jzmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

/***
 * @author solq 
 * TODO
 */
public class Test_Poller extends TestCtx {
    public static void main(String[] args) throws InterruptedException {

	final Socket push = ZMQ.context(1).socket(ZMQ.PUSH);
	final Socket pull = ZMQ.context(1).socket(ZMQ.PULL);
	final Socket pull2 = ZMQ.context(1).socket(ZMQ.PULL);

	push.bind("tcp://*:5555");
	pull.connect("tcp://localhost:5555");
	pull.connect("tcp://localhost:5555");

	final ZMQ.Poller poller = new ZMQ.Poller(2); // 创建一个大小为2的poller
	poller.register(pull, ZMQ.Poller.POLLIN); // 分别将上述的pull注册到poller上，注册的事件是读
	poller.register(pull2, ZMQ.Poller.POLLIN);

	final int count = 3000000;
	Thread t = new Thread(new Runnable() {
	    public void run() {
		String actual = null;
		while (true) {
		    poller.poll();
		    int i = 0;
		    if (poller.pollin(0)) {
			while (null != pull.recv(ZMQ.NOBLOCK)) {// 这里采用了非阻塞，确保一次性将队列中的数据读取完
			    i++;
			}
		    }
		    if (poller.pollin(1)) {
			while (null != pull2.recv(ZMQ.NOBLOCK)) {
			    i++;
			}
		    }
		    if (i % 100000 == 0) {
			System.out.println(i);
		    }
		    if (i >= count) {
			break;
		    }
		}
		System.out.println(actual);
	    }
	});
	t.start();

	final String expected = "hello";
	long start = System.currentTimeMillis();
	for (int i = 0; i < count; i++) {
	    push.send(expected + "_" + i);
	}
	long end = System.currentTimeMillis();
	System.out.println("push time :" + (end - start));

	t.join();
	end = System.currentTimeMillis();
	System.out.println("all time :" + (end - start));
    }

}
