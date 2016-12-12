package cn.edu.zju.vlis.eventhub;

import org.apache.kafka.common.TopicPartition;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wangxiaoyi on 2016/12/12.
 */
public class EventBusSubscriber implements Serializable{

    private String eventName;
    private List<TopicPartition> topicPartitions;
    private Properties props;
    private List<EventPartitionSubscriber> subscribers;
    private List<BlockingQueue<EventData>> eventQueues;

    private ExecutorService executor;
    private PriorityQueue<EventData> priorityQueue;

    private Lock lock = new ReentrantLock();

    private volatile boolean stopBusSubscriber = false;
    private volatile boolean startted = false;

    private EventBusSubscriber(){

    }

    public EventBusSubscriber(int maxPartition, String eventName, Properties props) {
        this.eventName = eventName;
        this.topicPartitions = new LinkedList<>();
        this.props = props;
        this.eventQueues = new ArrayList<>();
        for (int i = 0; i < maxPartition; ++i) {
            topicPartitions.add(new TopicPartition(eventName, i));
            eventQueues.add(new ArrayBlockingQueue<>(1000));
        }
        subscribers = new LinkedList<>();
    }

    public void initSubscribers() {
        for (TopicPartition tp : topicPartitions) {
            EventPartitionSubscriber subscriber = new EventPartitionSubscriber(tp, props);
            subscriber.connect();
            subscribers.add(subscriber);
        }
    }

    private void start(){
        initSubscribers();
        executor = Executors.newCachedThreadPool();
        priorityQueue = new PriorityQueue<>();
        startted = true;
        for(int i = 0; i < subscribers.size(); ++ i){
            executor.submit(new Worker(subscribers.get(i), eventQueues.get(i)));
        }
        executor.submit(new EventFetcher());
    }

    public void stop(){
        if(executor != null) executor.shutdown();
        stopBusSubscriber = true;
        for (EventPartitionSubscriber subscriber: subscribers){
            subscriber.close();
        }
    }

    public List<EventData> pullEvents(){
        if(!startted) start();
        List<EventData> rs = new LinkedList<>();
        if (!priorityQueue.isEmpty()){
            lock.lock();
            try {
                while (!priorityQueue.isEmpty()) rs.add(priorityQueue.poll());
            }finally {
                lock.unlock();
            }
        }
        return rs;
    }

    private void mergeEvents(){
        //System.out.println("merge ...");
        lock.lock();
        try {
            for (int i = 0; i < eventQueues.size(); ++i) {
                BlockingQueue<EventData> eventQueue = eventQueues.get(i);
                if (!eventQueue.isEmpty()) {
                    eventQueue.drainTo(priorityQueue, 500);
                }
            }
        }finally {
            lock.unlock();
        }
    }

    class EventFetcher implements Runnable{
        public void run(){
            try {
                while (!stopBusSubscriber) {
                    mergeEvents();
                    Thread.sleep(50);
                }
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    class Worker implements Runnable {
        private EventPartitionSubscriber subscriber;
        private BlockingQueue<EventData> eventQueue;

        public Worker(EventPartitionSubscriber subscriber, BlockingQueue<EventData> eventQueue) {
            this.subscriber = subscriber;
            this.eventQueue = eventQueue;
        }
        @Override
        public void run() {
            try {
                while (!stopBusSubscriber) {
                    List<EventData> events = subscriber.pollEvents();
                    for (EventData event : events) {
                        eventQueue.put(event);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}