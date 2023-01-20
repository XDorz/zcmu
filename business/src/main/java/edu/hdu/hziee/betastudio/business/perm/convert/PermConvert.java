package edu.hdu.hziee.betastudio.business.perm.convert;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.perm.model.PermBO;
import edu.hdu.hziee.betastudio.dao.perm.model.PermDO;
import edu.hdu.hziee.betastudio.dao.perm.repo.PermUserRelationDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermConvert {

    @Autowired
    PermUserRelationDORepo relationDORepo;

    public PermBO convert(PermDO permDO){
        AssertUtil.assertNotNull(permDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return PermBO.builder()
                .permId(permDO.getPermId())
                .permName(permDO.getPermName())
                .codeName(permDO.getCodeName())
                .build();
    }

    public PermBO convert(PermDO permDO,Long userId){
        PermBO permBO = convert(permDO);
        permBO.setHavePerm(relationDORepo.existsByUserIdAndPermId(userId,permBO.getPermId()));
        return permBO;
    }

    public PermDO convert(PermBO permBO){
        AssertUtil.assertNotNull(permBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return PermDO.builder()
                .permId(permBO.getPermId()==null? IdUtil.getSnowflakeNextId(): permBO.getPermId())
                .permName(permBO.getPermName())
                .codeName(permBO.getCodeName())
                .build();
    }
}
