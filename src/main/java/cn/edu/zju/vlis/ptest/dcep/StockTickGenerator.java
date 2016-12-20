package cn.edu.zju.vlis.ptest.dcep;

import cn.edu.zju.vlis.eventhub.EventBusKafkaProducer;
import cn.edu.zju.vlis.eventhub.EventData;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by wangxiaoyi on 2016/12/20.
 */
public class StockTickGenerator {

    private static EventBusKafkaProducer producer;
    private static Logger LOG = Logger.getLogger(StockTickGenerator.class.getSimpleName());
    private String connString = "";
    private int partitionNum = 0;


    public StockTickGenerator(String conn, int partitionNum){
        this.connString = conn;
        this.partitionNum = partitionNum;
    }

    public static void main(String []args){
        new StockTickGenerator("cn8:9092", 1).run();
    }

    public void init(){
        Properties props = new Properties();
        props.put("bootstrap.servers", connString);
        props.put("client.id", "EventProducer");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        producer = new EventBusKafkaProducer(props, partitionNum);
    }

    public void run(){
        init();

        String [] names = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int count = 0;
        int maxCount = 10 * 10000;
        int index = 0;
        while (count < maxCount){
            index = (index + 1) % names.length;
            double price = 0.0;
            double priceChangeRatio = 0.0;
            double priceChange = 0.0;
            double amount = 0.0;
            int volume = 0;
            EventData stockTick = EventGenerator.getStockTick(names[index],index,
                    "1", price, priceChangeRatio, priceChange, volume, amount,
                    "SELL", System.currentTimeMillis());
            producer.send(stockTick, stockTick.getEventSchemaName());
            LOG.info(stockTick);
            count ++;
        }
    }
}
