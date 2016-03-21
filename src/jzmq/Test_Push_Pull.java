package jzmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

/***
 * @author solq<br>
 *         300w 测试 push pull模式 <br>
 *         push time :1005<br>
 *         hello_2999999<br>
 *         all time :1006<br>
 */
public class Test_Push_Pull extends TestCtx {
    public static void main(String[] args) throws InterruptedException {
	final int count = 3000000;

	final Socket push = ZMQ.context(1).socket(ZMQ.PUSH);
	final Socket pull = ZMQ.context(1).socket(ZMQ.PULL);

	push.bind("tcp://*:5555");
	pull.connect("tcp://localhost:5555");

	Thread t = new Thread(new Task(count, pull));
	t.start();

 	long start = System.currentTimeMillis();
	for (int i = 0; i < count; i++) {
	    push.send("hello" + "_" + i);
	}
	long end = System.currentTimeMillis();
	System.out.println("push time :" + (end - start));

	t.join();
	end = System.currentTimeMillis();
	System.out.println("all time :" + (end - start));
    }

    // @Test
    // public void mutil() throws InterruptedException {
    // final Socket push = ZMQ.context(1).socket(ZMQ.PUSH);
    // final Socket pull = ZMQ.context(1).socket(ZMQ.PULL);
    // final Socket pull2 = ZMQ.context(1).socket(ZMQ.PULL);
    //
    // push.bind("tcp://*:5555");
    // pull.connect("tcp://localhost:5555");
    // pull2.connect("tcp://localhost:5555");
    //
    // final int count = 3000000;
    // Thread t = new Thread(new Task(count, pull));
    // t.start();
    // Thread t2 = new Thread(new Task(count, pull2));
    // t2.start();
    //
    // final String expected = "hello";
    // long start = System.currentTimeMillis();
    // for (int i = 0; i < count; i++) {
    // push.send(expected + "_" + i);
    // }
    // long end = System.currentTimeMillis();
    // System.out.println("push time :" + (end - start));
    //
    // t.join();
    // t2.join();
    // end = System.currentTimeMillis();
    // System.out.println("all time :" + (end - start));
    // }

    static class Task implements Runnable {
	private int count;
	private Socket pull;

	public Task(int count, Socket pull) {
	    this.count = count;
	    this.pull = pull;
	}

	public void run() {
	    int v = 0;
	    String actual = null;
	    int _count = count;
	    while (_count-- > 0) {
		actual = new String(pull.recv());
		if (_count % 10000 == 0) {
		    System.out.println(Thread.currentThread().getName() + ":" + actual);
		}
		v++;
	    }
	    pull.close();
	    System.out.println(actual);
	}
    };
}
