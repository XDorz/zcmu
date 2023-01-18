package edu.hdu.hziee.betastudio.business.resours.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.resours.convert.ResoursConvert;
import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.dao.resours.model.ResoursDO;
import edu.hdu.hziee.betastudio.dao.resours.repo.ResoursDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
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
                //todo 检测XSS攻击
                .info(request.getInfo())
                //todo 检测XSS攻击
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
        AssertUtil.assertNotNull(request.getBelongId()
                ,ExceptionResultCode.ILLEGAL_PARAMETERS,"资源所属id不可为空，本次关联未进行");
        if(request.getResourceList()==null) return;
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
        //todo 为管理员开绿灯
        verifyOwner(request.getResourceId(),request.getUserId());
        return resoursDORepo.deleteSrc(request.getResourceId(),true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateResourceName(ResoursRequest request) {
        //todo 检测XSS攻击
        //todo 为管理员开绿灯
        verifyOwner(request.getResourceId(),request.getUserId());
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateResourceInfo(ResoursRequest request) {
        //todo 检测XSS攻击
        //todo 为管理员开绿灯
        verifyOwner(request.getResourceId(),request.getUserId());
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateResourcePicUrl(ResoursRequest request) {
        //todo 为管理员开绿灯
        verifyOwner(request.getResourceId(),request.getUserId());
        return null;
    }

    private void verifyOwner(Long resourceId,Long userId){
        ResoursDO resoursDO = resoursDORepo.findAllByResourceId(resourceId);
        AssertUtil.assertEquals(resoursDO.getUserId(),userId
                ,ExceptionResultCode.FORBIDDEN,"您不是该资源的上传者，无法更改该资源");
    }
}
