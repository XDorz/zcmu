package edu.hdu.hziee.betastudio.business.perm.verify;

import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;

@FunctionalInterface
public interface VerifyFunction {

    OperateLevelEnum accept(Long userId,Long operateObjId);
}
