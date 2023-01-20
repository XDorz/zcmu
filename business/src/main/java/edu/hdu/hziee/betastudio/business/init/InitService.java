package edu.hdu.hziee.betastudio.business.init;

import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.user.service.UserService;
import edu.hdu.hziee.betastudio.dao.perm.model.PermDO;
import edu.hdu.hziee.betastudio.dao.perm.model.PermUserRelationDO;
import edu.hdu.hziee.betastudio.dao.perm.repo.PermDORepo;
import edu.hdu.hziee.betastudio.dao.perm.repo.PermUserRelationDORepo;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import edu.hdu.hziee.betastudio.util.customenum.basic.ZCMUConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitService {

    @Autowired
    PermService permService;

    @Autowired
    PermDORepo permDORepo;

    @Autowired
    PermUserRelationDORepo relationDORepo;

    @Autowired
    UserService userService;

    //推迟初始化至常量读取后
    @Autowired
    ZCMUConstant zcmuConstant;

    @PostConstruct
    public void init(){
        //如果数据库中不存在枚举类中的权限则创建
        UserPermRequest request = UserPermRequest.builder()
            .build();
        request.setSkipVerify(true);
        for (PermEnum permEnum : PermEnum.values()) {
            if(!permDORepo.existsByCodeName(permEnum.getCode())){
                request.setCodeName(permEnum.getCode());
                request.setPermName(permEnum.getDesc());
                permService.createPerm(request);
            }
        }

        //查询是否有管理员账户，没有则创建
        PermDO permDO = permDORepo.findAllByCodeName(PermEnum.MANAGER.getCode());
        List<PermUserRelationDO> relationDOS = relationDORepo.findAllByPermId(permDO.getPermId());
        if(relationDOS==null||relationDOS.size()==0){
            userService.register(ZCMUConstant.MANAGER_ACCOUNT
                    ,ZCMUConstant.DEFAULT_PASSWORD,"System",0L,PermEnum.MANAGER);
        }
    }
}
