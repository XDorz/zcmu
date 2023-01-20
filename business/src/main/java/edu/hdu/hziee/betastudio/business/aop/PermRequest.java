package edu.hdu.hziee.betastudio.business.aop;

import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;

/**
 * 鉴权接口，需鉴权的接口需要有实现此接口的入参
 */
public abstract class PermRequest {

    private Long verifyId = null;

    private boolean skipVerify = false;

    public void setVerifyId(Long verifyId) {
        this.verifyId=verifyId;
    }

    public Long getVerifyId() {
        AssertUtil.assertNotNull(verifyId, ExceptionResultCode.ILLEGAL_PARAMETERS, "鉴权接口传入空的id或未设置id");
        return verifyId;
    }

    public boolean isSkipVerify() {
        return skipVerify;
    }

    public void setSkipVerify(boolean skipVerify) {
        this.skipVerify = skipVerify;
    }
}
