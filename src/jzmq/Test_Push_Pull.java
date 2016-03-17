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

	final Socket push = ZMQ.context(1).socket(ZMQ.PUSH);
	final Socket pull = ZMQ.context(1).socket(ZMQ.PULL);

	push.bind("tcp://*:5555");
	pull.connect("tcp://localhost:5555");

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
