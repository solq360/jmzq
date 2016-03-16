package jzmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/***
 * down http://download.csdn.net/detail/wangyahuizc/8041175
 * */
public class ZMQ_SUB {

    public static void main(String[] args) {

	Context context = ZMQ.context(1);
	Socket subscriber = context.socket(ZMQ.SUB);
	subscriber.connect("tcp://localhost:5555");

	subscriber.subscribe("".getBytes());
	for (int i = 0; i < 100; i++) {
	    String string = new String(subscriber.recv(0));
	    System.out.println("recv 1" + string);
	}

	subscriber.close();
	context.term();
    }
}