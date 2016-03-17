package jzmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Event;
import org.zeromq.ZMQ.Socket;

/***
 * @author solq
 * 
 */
public class Test_Monitor extends TestCtx {
    public static void main(String[] args) throws InterruptedException {

	final Socket push = ZMQ.context(1).socket(ZMQ.PUSH);
	final Socket pull = ZMQ.context(1).socket(ZMQ.PULL);

	push.bind("tcp://*:5555");
	pull.connect("tcp://localhost:5555");

	// 监控
	pull.monitor("inproc://reqmoniter", ZMQ.EVENT_CONNECTED | ZMQ.EVENT_DISCONNECTED); // 这段代码会创建一个pair类型的socket，专门来接收当前socket发生的事件
	final ZMQ.Socket moniter = ZMQ.context(1).socket(ZMQ.PAIR);
	moniter.connect("inproc://reqmoniter"); // 连接当前socket的监听

	new Thread(new Runnable() {

	    public void run() {
		while (true) {
		    Event event = Event.recv(moniter); // 从当前moniter里面读取event
		    System.out.println(event.getEvent() + "  " + event.getAddress());
		}
	    }

	}).start();

	final int count = 3000000;
	Thread t = new Thread(new Runnable() {
	    public void run() {
		int v = 0;
		String actual = null;
		int _count = count;
		while (_count-- > 0) {
		    actual = new String(pull.recv());
		    v++;
		}
		pull.close();
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
