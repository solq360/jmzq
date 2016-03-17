package jzmq.pro;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import jzmq.TestCtx;

/***
 * @author solq 
 * 测试进程交互 同一上下文
 */
public class Test_inproc extends TestCtx {
    public static void main(String[] args) throws InterruptedException {
	Context ctx=ZMQ.context(1);
	final Socket push = ctx.socket(ZMQ.PUSH);
	final Socket pull = ctx.socket(ZMQ.PULL);

	push.bind("inproc://abc");
	pull.connect("inproc://abc");

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
