package edu.hdu.hziee.betastudio.business.perm.verify;

import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonPassageDO;
import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class VerifyOperate {

    public VerifyOperateModelEnum model=VerifyOperateModelEnum.OWNER_HIGHER_MODEL;

    private static PermService permService;
    private VerifyFunction verifyMethod;

    @Autowired
    private void setPermService(PermService permService){
        this.permService=permService;
    }

    public static VerifyOperate getInstance(VerifyFunction verifyMethod){
        VerifyOperate verifyOperate = new VerifyOperate();
        verifyOperate.setVerifyMethod(verifyMethod);
        return verifyOperate;
    }

    public static VerifyOperate getInstance(VerifyFunction verifyMethod,VerifyOperateModelEnum model){
        VerifyOperate instance = getInstance(verifyMethod);
        instance.setModel(model);
        return instance;
    }

    public OperateLevelEnum verifyLevel(Long userId, Long operateObjId, VerifyOperateModelEnum model){
        if (userId == null || operateObjId == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        if(model==VerifyOperateModelEnum.OWNER_HIGHER_MODEL){
            OperateLevelEnum levelEnum = verifyMethod.accept(userId, operateObjId);
            if(levelEnum!=null){
                return levelEnum;
            }

            UserPermRequest userPermRequest = UserPermRequest.builder()
                    .userId(userId)
                    .codeName(PermEnum.MANAGER.getCode())
                    .build();
            userPermRequest.setSkipVerify(true);
            if(permService.userExistPerm(userPermRequest)){
                return OperateLevelEnum.MEDIUM_OPERATE;
            }
        }else if(model==VerifyOperateModelEnum.MANAGE_HIGHER_MODEL){
            UserPermRequest userPermRequest = UserPermRequest.builder()
                    .userId(userId)
                    .codeName(PermEnum.MANAGER.getCode())
                    .build();
            userPermRequest.setSkipVerify(true);
            if(permService.userExistPerm(userPermRequest)){
                return OperateLevelEnum.TOTAL_OPERATE;
            }

            OperateLevelEnum levelEnum = verifyMethod.accept(userId, operateObjId);
            if(levelEnum!=null){
                return levelEnum;
            }
        }
        return OperateLevelEnum.FORBIDDEN;
    }

    public OperateLevelEnum verifyLevel(Long userId, Long operateObjId){
        return verifyLevel(userId,operateObjId,model);
    }
}
