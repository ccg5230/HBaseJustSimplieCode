package hbase.controller;

import hbase.service.HBaseService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className ShardingController
 * @Description
 * @Author chungaochen
 * Date 2020/4/8 21:17
 * Version 1.0
 **/
@RestController
@RequestMapping("/hbse")
public class HBaseController {
    private static final Logger logger = LoggerFactory.getLogger(HBaseController.class);

    @Autowired
    private HBaseService hbaseService;

    @RequestMapping(value = "/createTable", method = RequestMethod.GET)
    public void createTable() {
        //删除表
        hbaseService.deleteTable("test_base");
        //创建表 及 列蔟
        hbaseService.createTableBySplitKeys("test_base", Arrays.asList("f","back","vf_cf"),hbaseService.getSplitKeys(null));
        //插入三条数据
        hbaseService.putData("test_base","66804_000001","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"40866","mob_3","0.9416","0.0000","12.2293","null"});
        hbaseService.putData("test_base","66804_000002","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"40866","idno_prov","0.9317","0.0000","9.8679","null"});
        hbaseService.putData("test_base","66804_000003","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"40866","education","0.8984","0.0000","25.5649","null"});
        //查询数据
        //1. 根据rowKey查询
        Map<String,String> result1 = hbaseService.getRowData("test_base","66804_000001");
        System.out.println("+++++++++++根据rowKey查询+++++++++++");
        result1.forEach((k,value) -> {
            System.out.println(k + "---" + value);
        });
        System.out.println();
        //精确查询某个单元格的数据
        String str1 = hbaseService.getColumnValue("test_base","66804_000002","f","varName");
        System.out.println("+++++++++++精确查询某个单元格的数据+++++++++++");
        System.out.println(str1);
        System.out.println();
        //2. 遍历查询
        Map<String, Map<String,String>> result2 = hbaseService.getResultScanner("test_base");
        System.out.println("+++++++++++遍历查询+++++++++++");
        result2.forEach((k,value) -> {
            System.out.println(k + "---" + value);
        });
    }

    /**
     * 测试指定startRowKey和stopRowKey的查询
     */
    @RequestMapping(value = "/selectByStartStopRowKey", method = RequestMethod.GET)
    public void selectByStartStopRowKey(){
        Map<String,Map<String,String>> result = hbaseService.getResultScanner("test_base","66804_000002","66804_000004");
        result.forEach((rowKey,columnMap) -> {
            System.out.println("rowKey:" + rowKey);
            System.out.println("+++++++++++行数据+++++++++++");
            columnMap.forEach((k,value) -> {
                System.out.println(k + "---" + value);
            });
        });
        System.out.println("-----------------------");
    }

    /**
     * 测试获取所有表名
     */
    @RequestMapping(value = "/getTableNameList", method = RequestMethod.GET)
    public void getTableNameList(){
        List<String> result = hbaseService.getAllTableNames();
        result.forEach(System.out::println);
    }

    /**
     * 测试获取指定单元格多个版本的数据
     */
    @RequestMapping(value = "/getColumnValuesByVersion", method = RequestMethod.GET)
    public void getColumnValuesByVersion(){
        hbaseService.setColumnValue("test_base","66804_000002","f","varName","aa");
        hbaseService.setColumnValue("test_base","66804_000002","f","varName","bb");
        hbaseService.setColumnValue("test_base","66804_000002","f","varName","cc");
        hbaseService.setColumnValue("test_base","66804_000002","f","varName","dd");
        hbaseService.setColumnValue("test_base","66804_000002","f","varName","ee");
        List<String> result = hbaseService.getColumnValuesByVersion("test_base","66804_000002","f","varName",4);
        result.forEach(System.out::println);
    }

    /**
     * 测试根据行键过滤器查询数据
     */
    @RequestMapping(value = "/getResultScannerPrefixFilter", method = RequestMethod.GET)
    public void getResultScannerPrefixFilter(){
        hbaseService.putData("test_base","111","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"111","111","111","111","111","null"});
        hbaseService.putData("test_base","112","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"112","112","112","112","112","null"});
        Map<String,Map<String,String>> result = hbaseService.getResultScannerPrefixFilter("test_base","11");
        result.forEach((rowKey,columnMap) -> {
            System.out.println("-----------------------");
            System.out.println("rowKey:" + rowKey);
            System.out.println("+++++++++++行数据+++++++++++");
            columnMap.forEach((k,value) -> {
                System.out.println(k + "---" + value);
            });
            System.out.println("-----------------------");
        });
    }

    /**
     * 测试根据列名过滤器查询数据
     */
    @RequestMapping(value = "/getResultScannerColumnPrefixFilter", method = RequestMethod.GET)
    public void getResultScannerColumnPrefixFilter(){
        hbaseService.putData("test_base","211","f",new String[]{"project_id"},new String[]{"11111"});
        hbaseService.putData("test_base","211","f",new String[]{"var_name1"},new String[]{"111"});
        hbaseService.putData("test_base","212","f",new String[]{"var_name2"},new String[]{"112"});
        Map<String,Map<String,String>> result = hbaseService.getResultScannerColumnPrefixFilter("test_base","var_name");
        result.forEach((rowKey,columnMap) -> {
            System.out.println("-----------------------");
            System.out.println("rowKey:" + rowKey);
            System.out.println("+++++++++++行数据+++++++++++");
            columnMap.forEach((k,value) -> {
                System.out.println(k + "---" + value);
            });
            System.out.println("-----------------------");
        });
    }

    /**
     * 测试查询行键中包含特定字符的数据
     */
    @RequestMapping(value = "/getResultScannerRowFilter", method = RequestMethod.GET)
    public void getResultScannerRowFilter(){
        hbaseService.putData("test_base","abc666666def","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"111","abc6666def","111","111","111","null"});
        hbaseService.putData("test_base","cba666666fed","f",new String[]{"project_id","varName","coefs","pvalues","tvalues","create_time"},new String[]{"112","cba6666fed","112","112","112","null"});
        Map<String,Map<String,String>> result = hbaseService.getResultScannerRowFilter("test_base","666666");
        result.forEach((rowKey,columnMap) -> {
            System.out.println("-----------------------");
            System.out.println("rowKey:" + rowKey);
            System.out.println("+++++++++++行数据+++++++++++");
            columnMap.forEach((k,value) -> {
                System.out.println(k + "---" + value);
            });
            System.out.println("-----------------------");
        });
    }

    /**
     * 查询列名中包含特定字符的数据
     */
    @RequestMapping(value = "/getResultScannerQualifierFilter", method = RequestMethod.GET)
    public Map<String,Map<String,String>> getResultScannerQualifierFilter(){
        hbaseService.putData("test_base","rowid_no1","f",new String[]{"id_special","name_special","coefs_special",
                "pvalues_special", "value_special", "create_time"},new String[]{"123","gda",
                "cdgd","aafa","afda","null"});
        hbaseService.putData("test_base","rowid_no2","f",new String[]{"id_special_p","name_special_p","coefs_special_p",
                "pvalues_special_p", "value_special_p", "create_time_p"},new String[]{"456","hh",
                "efgh","45ffd","899rvf","null"});
        Map<String,Map<String,String>> result = hbaseService.getResultScannerQualifierFilter("test_base","special");
        result.forEach((rowKey,columnMap) -> {
            System.out.println("-----------------------");
            System.out.println("rowKey:" + rowKey);
            System.out.println("+++++++++++行数据+++++++++++");
            columnMap.forEach((k,value) -> {
                System.out.println(k + "---" + value);
            });
            System.out.println("-----------------------");
        });
        return result;
    }

    /**
     * 查询列名中包含特定字符的数据
     */
    @RequestMapping(value = "/getResultScannerValueFilter", method = RequestMethod.GET)
    public Map<String,Map<String,String>>  getResultScannerValueFilter(){
        hbaseService.putData("test_base","vf1","vf_cf",new String[]{"id","name","coefs",
                "pvalues", "value", "create_time"},new String[]{"123_special","gda_special",
                "cdgd_special","aafa","afda","null"});
        hbaseService.putData("test_base","vf2","vf_cf",new String[]{"id","name","coefs",
                "pvalues", "value", "create_time"},new String[]{"456","hh",
                "efgh_special","45ffd_special","899rvf_special","null"});
        Map<String,Map<String,String>> result = hbaseService.getResultScannerValueFilter("test_base","special");
        result.forEach((rowKey,columnMap) -> {
            System.out.println("-----------------------");
            System.out.println("rowKey:" + rowKey);
            System.out.println("+++++++++++行数据+++++++++++");
            columnMap.forEach((k,value) -> {
                System.out.println(k + "---" + value);
            });
            System.out.println("-----------------------");
        });
        return result;
    }

    /**
     * 测试删除指定的列
     */
    @RequestMapping(value = "/deleteColumn", method = RequestMethod.GET)
    public void deleteColumn(){
        //新增一个测试列
        hbaseService.setColumnValue("test_base","66804_000002","f","xxx","123");
        String str = hbaseService.getColumnValue("test_base","66804_000002","f","xxx");
        System.out.println("第一次取值：" + str);
        //删除测试列
        hbaseService.deleteColumn("test_base","66804_000002","f","xxx");
        //再次取值
        String str2 = hbaseService.getColumnValue("test_base","66804_000002","f","xxx");
        System.out.println("第二次取值：" + str2);
    }

    /**
     * 测试删除指定的行
     */
    @RequestMapping(value = "/deleteRow", method = RequestMethod.GET)
    public void deleteRow(){
        //取值
        Map<String,String> result1 = hbaseService.getRowData("test_base","66804_000003");
        System.out.println("第一次取值输出：");
        result1.forEach((k,value) -> {
            System.out.println(k + "---" + value);
        });
        //删除测试行
        hbaseService.deleteRow("test_base","66804_000003");
        //再次取值
        Map<String,String> result2 = hbaseService.getRowData("test_base","66804_000003");
        System.out.println("第二次取值输出：");
        result2.forEach((k,value) -> {
            System.out.println(k + "---" + value);
        });
    }

    /**
     * 测试删除指定的列族
     */
    @RequestMapping(value = "/deleteColumnFamily", method = RequestMethod.GET)
    public void deleteColumnFamily(){
        //添加测试数据
        hbaseService.putData("test_base","777","back",new String[]{"var_name1"},new String[]{"777"});
        //取值
        Map<String,String> result1 = hbaseService.getRowData("test_base","777");
        System.out.println("第一次取值输出：");
        result1.forEach((k,value) -> {
            System.out.println(k + "---" + value);
        });
        //删除测试列族
        hbaseService.deleteColumnFamily("test_base","back");
        //再次取值
        Map<String,String> result2 = hbaseService.getRowData("test_base","777");
        System.out.println("第二次取值输出：");
        result2.forEach((k,value) -> {
            System.out.println(k + "---" + value);
        });
    }
}