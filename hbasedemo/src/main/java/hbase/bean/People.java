package hbase.bean;

import lombok.Data;

/**
 *@className City
 *@Description
 *@Author chungaochen
 *Date 2020/4/20 23:03
 *Version 1.0
 **/
@Data
public class People {


    /**
     * 省份年龄
     */
    private int age;

    /**
     * 城市名称
     */
    private String name;

}