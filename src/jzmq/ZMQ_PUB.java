package jzmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/***
 * {@linkplain http://blog.csdn.net/sinat_19351993/article/details/45481087}
 * 
 * */
public class ZMQ_PUB {  
   
   public static void main(String[] args) throws InterruptedException {  
       //System.loadLibrary("libzmq");
       System.loadLibrary("libzmq-v120-mt-4_0_4");
       
        Context context = ZMQ.context(1);   
       Socket publisher = context.socket(ZMQ.PUB);  
       publisher.bind("tcp://*:5555");  
        Thread.sleep(1000);  
       for (int i = 0; i < 100; i++) {  
           publisher.send(("admin " + i).getBytes(), ZMQ.NOBLOCK);  
           System.out.println("pub msg " + i);  
           Thread.sleep(1000);  
       }  
         
        publisher.close();  
       context.term();  
    }  
}  