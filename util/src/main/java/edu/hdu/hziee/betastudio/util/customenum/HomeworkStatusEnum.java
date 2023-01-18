package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import lombok.Getter;

@Getter
public enum HomeworkStatusEnum implements KVenum<String,String> {

    UN_SUBMIT("unSubmit","作业未提交"),
    SUBMIT_NOT_SCORE("submitted","教师未批改"),
    SUBMIT_SCORED("scored","教师以打分"),
    KICK_BACK("kicked","被打回"),
    //仅观看，指未选此课程的人观看课程作业或者教师观看其自己布置的作业
    CUSTOMER("customer",""),
    ;

    private String code;
    private String desc;
    HomeworkStatusEnum(String code,String desc){
        this.code=code;
        this.desc=desc;
    }
}
