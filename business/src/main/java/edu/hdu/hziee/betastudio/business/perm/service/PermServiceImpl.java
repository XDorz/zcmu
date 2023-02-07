package edu.hdu.hziee.betastudio.business.perm.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.perm.convert.PermConvert;
import edu.hdu.hziee.betastudio.business.perm.model.PermBO;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.dao.perm.model.PermDO;
import edu.hdu.hziee.betastudio.dao.perm.model.PermUserRelationDO;
import edu.hdu.hziee.betastudio.dao.perm.repo.PermDORepo;
import edu.hdu.hziee.betastudio.dao.perm.repo.PermUserRelationDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class PermServiceImpl implements PermService{

    @Autowired
    PermDORepo permDORepo;

    @Autowired
    PermUserRelationDORepo permUserRelationDORepo;

    @Autowired
    PermConvert convert;

    @Override
    public boolean userExistPerm(UserPermRequest request) {
        PermDO permDO = permDORepo.findAllByCodeName(request.getCodeName());
        return permUserRelationDORepo.existsByUserIdAndPermId(request.getUserId(),permDO.getPermId());
    }

    @Override
    public PermBO createPerm(UserPermRequest request) {
        PermDO permDO = permDORepo.findAllByCodeName(request.getCodeName());
        AssertUtil.assertNull(permDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"已存在权限code为【"+request.getCodeName()+"】的权限");
        PermDO newPermDO = PermDO.builder()
                .permId(request.getPermId()==null?IdUtil.getSnowflakeNextId():request.getPermId())
                .permName(request.getPermName())
                .codeName(request.getCodeName())
                .deleted(false)
                .build();
        permDORepo.save(newPermDO);
        return convert.convert(newPermDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PermBO deletePerm(UserPermRequest request) {
        PermDO permDO=null;
        if(request.getCodeName()!=null){
            permDO=permDORepo.findAllByCodeName(request.getCodeName());
        }else {
            permDO=permDORepo.findAllByPermId(request.getPermId());
        }
        if(permDO==null){
            return null;
        }

        permUserRelationDORepo.deleteAllByPermId(permDO.getPermId());
        permDORepo.deletePerm(permDO.getPermId(),true);
        return convert.convert(permDO);
    }

    @Override
    public void givePerm(UserPermRequest request) {
        PermDO permDO=null;
        if(request.getCodeName()!=null){
            permDO=permDORepo.findAllByCodeName(request.getCodeName());
        }else {
            permDO=permDORepo.findAllByPermId(request.getPermId());
        }
        AssertUtil.assertNotNull(permDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"查无此权限！权限未给予");

        boolean b = permUserRelationDORepo.existsByUserIdAndPermId(request.getUserId(), request.getPermId());
        AssertUtil.assertTrue(!b,ExceptionResultCode.ILLEGAL_PARAMETERS,"该用户已有此权限，无法重复给予！");

        permUserRelationDORepo.save(
                PermUserRelationDO.builder()
                        .permId(permDO.getPermId())
                        .userId(request.getUserId())
                        .relationId(request.getRelationId()==null? IdUtil.getSnowflakeNextId(): request.getRelationId())
                        .build()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void takeBackPerm(UserPermRequest request) {
        permUserRelationDORepo.takeBackPerms(request.getUserId(),request.getPermIds());
    }

    @Override
    public List<PermBO> getAllPerm(UserPermRequest request) {
        return CollectionUtils.toStream(permDORepo.findAllByDeleted(false))
                .filter(Objects::nonNull)
                .map(convert::convert)
                .toList();
    }

    @Override
    public List<List<PermBO>> getAllUserPermInfo(UserPermRequest request) {
        List<PermUserRelationDO> relationDOS = permUserRelationDORepo.findAllByUserId(request.getUserId());
        List<List<PermBO>> result=new ArrayList<>();
        UserPermRequest permRequest = UserPermRequest.builder().build();
        //该用户拥有的权限
        List<PermBO> hasPerm=new ArrayList<>();
        permRequest.setSkipVerify(true);
        //该列表用作该用户未拥有的权限
        List<PermBO> dataPermBOS = getAllPerm(permRequest);
        //数据库查询返回的是不可变集合，无法在接下来的迭代操作中调用remove，故将集合类型改变
        List<PermBO> permBOS=new ArrayList<>(dataPermBOS);
        //迭代列表，将拥有的权限移入hasPerm链表，未拥有的权限保留，同时设置他们的havePerm值
        Iterator<PermBO> ite = permBOS.iterator();
        while (ite.hasNext()){
            PermBO permBO = ite.next();
            for (PermUserRelationDO relationDO : relationDOS) {
                if(permBO.getPermId().equals(relationDO.getPermId())){
                    ite.remove();
                    permBO.setHavePerm(true);
                    hasPerm.add(permBO);
                    break;
                }else {
                    //todo 默认查询出来permBO就是false，下面代码可注释以提高性能
                    permBO.setHavePerm(false);
                }
            }
        }
        result.add(permBOS);
        result.add(hasPerm);
        return result;
    }

    @Override
    public List<PermBO> getUserPermInfo(UserPermRequest request) {
        List<PermUserRelationDO> relationDOS = permUserRelationDORepo.findAllByUserId(request.getUserId());
        return CollectionUtils.toStream(relationDOS)
                .filter(Objects::nonNull)
                .map(relationDO -> permDORepo.findAllByPermId(relationDO.getPermId()))
                .filter(Objects::nonNull)
                .map(convert::convert)
                .toList();
    }
}
