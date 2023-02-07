package edu.hdu.hziee.betastudio.business.resours.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.perm.verify.VerifyOperate;
import edu.hdu.hziee.betastudio.business.resours.convert.ResoursConvert;
import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonPassageDO;
import edu.hdu.hziee.betastudio.dao.resours.model.ResoursDO;
import edu.hdu.hziee.betastudio.dao.resours.repo.ResoursDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import edu.hdu.hziee.betastudio.util.tecentcos.CosUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ResoursServiceImpl implements ResoursService{

    @Autowired
    ResoursDORepo resoursDORepo;

    @Autowired
    ResoursConvert convert;

    @Autowired
    CosUtil cosUtil;

    @Autowired
    private void setVerify(VerifyOperate verifyOperate) {
        this.resourceVerifyOperate = verifyOperate.getInstance(this::customResourceVerify);
    }
    private VerifyOperate resourceVerifyOperate;

    @Override
    public List<ResoursBO> getListByBelongId(Long belongId) {
        return CollectionUtils.toStream(resoursDORepo.findAllByBelongIdAndDeleted(belongId,false))
                .filter(Objects::nonNull)
                .map(convert::convert)
                .toList();
    }

    @Override
    public ResoursBO createResource(ResoursRequest request) {
        Long resourceId= IdUtil.getSnowflakeNextId();
        String picUrl=null;
        if(request.getPicFile()!=null){
            picUrl=cosUtil.uploadFile(request.getPicFile());
        }
        ResoursDO resoursDO = ResoursDO.builder()
                .resourceId(resourceId)
                .info(request.getInfo())
                .name(request.getName())
                .userId(request.getUserId())
                .belongId(request.getBelongId())
                .picUrl(picUrl)
                .url(cosUtil.uploadFile(request.getSourceFile()))
                .deleted(false)
                .ext("{}")
                .build();
        resoursDORepo.save(resoursDO);
        return convert.convert(resoursDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void connectResource(ResoursRequest request) {
        if(request.getResourceList()==null) return;
        AssertUtil.assertNotNull(request.getBelongId()
                ,ExceptionResultCode.ILLEGAL_PARAMETERS,"资源所属id不可为空，本次关联未进行");
        //为确保系统稳定性，不做资源所属检查
//        AssertUtil.assertTrue(resourceVerifyOperate.verifyLevel(request.getVerifyId(),request.getResourceId())
//                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"仅资源上传者可关联资源");


        for (Long resourceId : request.getResourceList()) {
            ResoursDO resoursDO = resoursDORepo.findAllByResourceId(resourceId);
            AssertUtil.assertNotNull(resoursDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"无法查到该资源，本次批量关联未进行");
            AssertUtil.assertNull(resoursDO.getBelongId()
                    , ExceptionResultCode.ILLEGAL_PARAMETERS,"不可更改已经绑定了的资源，本次关联未进行");
            resoursDORepo.updateBelong(resourceId,request.getBelongId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteResource(ResoursRequest request) {
        AssertUtil.assertTrue(resourceVerifyOperate.verifyLevel(request.getVerifyId(),request.getResourceId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权删除此资源");

        return resoursDORepo.deleteSrc(request.getResourceId(),true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateResourceName(ResoursRequest request) {
        AssertUtil.assertTrue(resourceVerifyOperate.verifyLevel(request.getVerifyId(),request.getResourceId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权更新此资源的名称");

        return resoursDORepo.updateName(request.getResourceId(),request.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateResourceInfo(ResoursRequest request) {
        AssertUtil.assertTrue(resourceVerifyOperate.verifyLevel(request.getVerifyId(),request.getResourceId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权更新此资源的信息");

        return resoursDORepo.updateInfo(request.getResourceId(),request.getInfo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateResourcePicUrl(ResoursRequest request) {
        AssertUtil.assertTrue(resourceVerifyOperate.verifyLevel(request.getVerifyId(),request.getResourceId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权更新此资源的封面");

        String picUrl = cosUtil.uploadFile(request.getPicFile());
        return resoursDORepo.updatePic(request.getResourceId(), picUrl);
    }


    //=====================================以下是归属鉴定与鉴权================================================
    private OperateLevelEnum customResourceVerify(Long userId, Long resourceId) {
        ResoursDO resoursDO = resoursDORepo.findAllByResourceId(resourceId);
        if(resoursDO==null){
            return OperateLevelEnum.FORBIDDEN;
        }

        if(userId.equals(resoursDO.getUserId())){
            return OperateLevelEnum.TOTAL_OPERATE;
        }
        return null;
    }
}
