package jzmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

/***
 * @author solq 不建议使用 这种模式，每次接收数据时必须要响应
 */
public class Test_Request_Reply extends TestCtx {

    public static void main(String[] args) throws InterruptedException {
	final int count = 30000;

	final Socket response = ZMQ.context(1).socket(ZMQ.REP);
	final Socket request = ZMQ.context(1).socket(ZMQ.REQ);
	response.bind("tcp://*:5555");
	request.connect("tcp://localhost:5555");

	Thread t = new Thread(new Runnable() {
	    public void run() {
		int checkValue = count;
		// 如果没有返回影响 他妈的接收时会抛异常
		while (checkValue-- > 0) {
		    try {
			String body = new String(response.recv());
			// System.out.println(body);
			// if (checkValue % 200 != 0)
			{
 			    response.send(body);
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	});
	t.start();

	long start = System.currentTimeMillis();
	for (int i = 0; i < count; i++) {
	    boolean state = request.send("Hello" + i);
	    request.recv();
	    // String body = new String(request.recv());
	    // System.out.format("num %d state %s body %s \n", i, state, body);
	}
	long end = System.currentTimeMillis();
	System.out.println("push time :" + (end - start));
	t.join();
	end = System.currentTimeMillis();
	System.out.println("all time :" + (end - start));
    }

}
