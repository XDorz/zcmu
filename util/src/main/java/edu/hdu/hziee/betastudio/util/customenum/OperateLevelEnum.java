package edu.hdu.hziee.betastudio.util.customenum;

import edu.hdu.hziee.betastudio.util.customenum.basic.KVenum;
import lombok.Getter;

/**
 * 用于表示权限高度的枚举
 */
@Getter
public enum OperateLevelEnum implements KVenum<Integer,String> {
    TOTAL_OPERATE(3,"完全操作权限"),
    MEDIUM_OPERATE(2,"部分操作权限"),
    FORBIDDEN(1,"无权操作"),
    ;
    Integer code;
    String desc;

    OperateLevelEnum(Integer code,String desc){
        this.code=code;
        this.desc=desc;
    }

    public boolean hasPerm(OperateLevelEnum requiredLevel){
        return this.code>=requiredLevel.code;
    }
}
