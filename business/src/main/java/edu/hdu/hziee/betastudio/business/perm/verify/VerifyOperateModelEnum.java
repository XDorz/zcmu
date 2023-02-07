package edu.hdu.hziee.betastudio.business.perm.verify;

public enum VerifyOperateModelEnum {
    OWNER_HIGHER_MODEL("所有者权限最高"),
    MANAGE_HIGHER_MODEL("管理员权限最高"),
    ;
    String desc;
    VerifyOperateModelEnum(String desc){
        this.desc=desc;
    }
}
