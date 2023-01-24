package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import lombok.Getter;

@Getter
public enum ClientTypeEnum implements KVenum<String,String> {

    VX_CLIENT("vx_client","微信小程序端"),
    WEB_CLIENT("web_client","网页端"),
    ;
    String code;
    String desc;

    ClientTypeEnum(String code,String desc){
        this.code=code;
        this.desc=desc;
    }
}
