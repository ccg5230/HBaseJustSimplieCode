package hbase.config;

import hbase.service.HBaseService;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *@className HBaseConfiguration
 *@Description
 *@Author chungaochen
 *Date 2020/4/21 11:21
 *Version 1.0
 **/
@Configuration
public class HBaseConfig {
    @Value("${hbase.zookeeper.quorum}")
    private String quorum;

    @Value("${hbase.maxsize}")
    private String maxsize;

    @Value("${hbase.rootdir}")
    private String rootdir;

    @Value("${hbase.zookeeper.property.dataDir}")
    private String dataDir;

    @Bean
    public HBaseService getHbaseService(){
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum",quorum );
        conf.set("hbase.zookeeper.property.clientPort","2181"); //端口号
        conf.set("hbase.client.keyvalue.maxsize",maxsize);
        conf.set("hbase.rootdir",rootdir );
        conf.set("hbase.zookeeper.property.dataDir",dataDir );

        return new HBaseService(conf);
    }
}