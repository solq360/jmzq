package jzmq;

import org.junit.Test;
import org.zeromq.ZAuth;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

/***
 * @author solq {@linkplain http://hintjens.com/blog:49}
 */
public class Test_Plain_Auth extends TestCtx {
    @Test
    public void pushPullTest() throws InterruptedException {
	ZContext zContext = new ZContext();
	ZAuth zAuth = new ZAuth(zContext);
	zAuth.allow("*");
	// zAuth.configurePlain(domain, filename);
	final Socket push = zContext.createSocket(ZMQ.PUSH);
	final Socket pull = zContext.createSocket(ZMQ.PULL);
	push.setPlainServer(true);
	push.setPlainUsername("solq".getBytes());
	push.setPlainPassword("hello".getBytes());
	push.bind("tcp://*:5555");

	// pull.setIdentity("xrlserjlsekjrlsejr".getBytes());
	pull.setPlainUsername("solq".getBytes());
	pull.setPlainPassword("hello".getBytes());
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
