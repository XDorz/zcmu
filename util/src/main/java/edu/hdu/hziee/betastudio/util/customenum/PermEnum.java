package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;

/**
 * 权限枚举类
 */
public enum PermEnum implements KVenum<String,String> {
    MANAGER("zcmu_manager","管理员，最高权限"),
    TEACHER("zcmu_teacher","教师权限"),
    STUDENT("zcmu_student","学生权限"),
    ;
    String code;
    String desc;

    PermEnum(String code,String desc){
        this.code=code;
        this.desc=desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
