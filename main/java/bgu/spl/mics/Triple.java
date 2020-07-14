package bgu.spl.mics;
/*
class for represent a Object containing a MicroService with :
    * Queue of messages waiting,
    * Vector of type of message the MicroService subscribe to
 */
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class Triple {
    private MicroService micro;
    private LinkedBlockingQueue<Message> first;
    private Vector<Class<? extends Message>> second;

    public Triple(MicroService micro, LinkedBlockingQueue<Message> first, Vector<Class<? extends Message>> second) {
        this.micro = micro;
        this.first = first;
        this.second = second;
    }

    public MicroService getMicro() {
        return micro;
    }

    public LinkedBlockingQueue<Message> getFirst() {
        return first;
    }

    public Vector<Class<? extends Message>> getSecond() {
        return second;
    }
}
