package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;

public enum PermTypeEnum implements KVenum<String,String> {

    ;
    String code;
    String desc;
    PermTypeEnum(String code,String desc){
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
