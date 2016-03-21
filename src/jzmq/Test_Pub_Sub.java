package jzmq;

import org.junit.Test;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
/***
 * @author solq
 * 目前测试发送得太快会丢消息。。。。。。
 * TODO 未完成
 * */
public class Test_Pub_Sub extends TestCtx {
    public static void main(String[] args) throws InterruptedException {
	final int count = 300000;

	final Socket pub = ZMQ.context(1).socket(ZMQ.PUB);
	final Socket sub = ZMQ.context(1).socket(ZMQ.SUB);
	pub.bind("tcp://*:5555");
	//pub.bind("ipc://weather.ipc");
	
	sub.connect("tcp://localhost:5555");
	sub.subscribe("abc".getBytes());
	
 	Thread t = new Thread(new Runnable() {
	    public void run() {
		int _count = count;
		String body = null;
		while (_count-- > 0) {
		   // System.out.println(_count);
		    body = new String(sub.recv(0));
		}
		System.out.println(body);
		sub.close();
	    }
	});
	t.start();
	long start = System.currentTimeMillis();
	for (int i = 0; i < count; i++) {
	    boolean state = pub.send("abc Hello : " + i );
	    //TODO 
 	   System.out.format("num %d state %s  \n", i, state);
	}
	long end = System.currentTimeMillis();
	System.out.println("push time :" + (end - start));

	t.join();
	end = System.currentTimeMillis();
	System.out.println("all time :" + (end - start));
    }
    
    @Test
    public   void myImpl() throws InterruptedException {
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
