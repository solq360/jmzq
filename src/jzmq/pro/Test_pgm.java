package jzmq.pro;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import jzmq.TestCtx;

/***
 * @author solq 
 * pgm 要安装lib 没有效果
 */
public class Test_pgm extends TestCtx {
    public static void main(String[] args) throws InterruptedException {
	final Socket push = ZMQ.context(1).socket(ZMQ.PUSH);
	final Socket pull = ZMQ.context(1).socket(ZMQ.PULL);

	push.bind("epgm://*:5555");
	pull.connect("pgm://localhost:5555");

	final int count = 30000;
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
