package edu.hdu.hziee.betastudio.business.user.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.excel.EasyExcel;
import edu.hdu.hziee.betastudio.business.aop.VerifyPerm;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.user.convert.UserConvert;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.model.UserInfoBO;
import edu.hdu.hziee.betastudio.business.user.request.UserRequest;
import edu.hdu.hziee.betastudio.dao.perm.repo.PermUserRelationDORepo;
import edu.hdu.hziee.betastudio.dao.user.model.UserDO;
import edu.hdu.hziee.betastudio.dao.user.model.UserInfoDO;
import edu.hdu.hziee.betastudio.dao.user.repo.UserDORepo;
import edu.hdu.hziee.betastudio.dao.user.repo.UserInfoDORepo;
import edu.hdu.hziee.betastudio.util.common.*;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import edu.hdu.hziee.betastudio.util.customenum.basic.ZCMUConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JwtTokenUtil tokenUtil;

    @Autowired
    UserInfoDORepo userInfoDORepo;

    @Autowired
    UserDORepo userDORepo;

    @Autowired
    UserConvert convert;

    @Autowired
    PermUserRelationDORepo permUserRelationDORepo;

    @Autowired
    PermService permService;

    @Override
    public String register(String account, String password, String realName, long stuId, PermEnum... perms) {
        long userId = IdUtil.getSnowflakeNextId();
        //??????????????????
        realName = realName == null ? "??????" : realName;
        UserInfoDO userInfoDO = UserInfoDO.builder()
                .userName(realName)
                .realName(realName)
                .userId(userId)
                .picUrl(ZCMUConstant.USER_PIC_URL)
                .stuId(stuId)
                .sex("???")
                .ext("{}")
                .build();
        userInfoDORepo.save(userInfoDO);

        String salt = UUID.randomUUID().toString();
        password = password == null ? ZCMUConstant.DEFAULT_PASSWORD : password;

        //??????????????????
        UserDO userDO = UserDO.builder()
                .lastLoginIp("127.0.0.1")
                .lastLoginDate(new Date())
                .userId(userId)
                .account(account)
                .salt(salt)
                .password(DigestUtil.md5Hex(password + salt, StandardCharsets.UTF_8))
                .build();
        userDORepo.save(userDO);

        //??????????????????
        UserPermRequest userPermRequest = UserPermRequest.builder()
                .userId(userId)
                .build();
        userPermRequest.setSkipVerify(true);
        for (PermEnum perm : perms) {
            userPermRequest.setCodeName(perm.getCode());
            permService.givePerm(userPermRequest);
        }
        return tokenUtil.getToken(userId);
    }

    /**
     * ??????????????????--??????
     */
    @Override
    @VerifyPerm(perms = {PermEnum.MANAGER})
    @Transactional(rollbackFor = Exception.class)
    public String register(UserRequest request,PermEnum... perms) {
        List<UserInfoBO> userInfoBOS=new ArrayList<>();
        InputStream excelStream = null;
        try {
            excelStream = request.getUserExcelFile().getInputStream();
        } catch (IOException e) {
            log.error("?????????excel??????????????????",e);
            throw new ZCMUException("?????????excel??????????????????");
        }
        EasyExcel.read(excelStream, UserInfoBO.class,new EasyExcelListener<UserInfoBO>(userInfoBOS)).sheet().doRead();

        //?????????????????????????????????
        List<UserInfoDO> all = userInfoDORepo.findAll();
        List<Long> stuIds = CollectionUtils.toStream(all)
                .filter(Objects::nonNull)
                .map(UserInfoDO::getStuId)
                .toList();
        int i = 2;
        StringBuilder sb = new StringBuilder();
        //????????????list???????????????
        Set<Long> savedIds = new HashSet<>();
        for (UserInfoBO userInfoBO : userInfoBOS) {
            Long stuId=userInfoBO.getStuId();
            if(stuId==null){
                stuId=userInfoBO.getWorkId();
            }
            AssertUtil.assertNotNull(stuId,ExceptionResultCode.ILLEGAL_PARAMETERS,"?????????????????????????????????????????????/??????????????????????????????"
                    +i+"????????????????????????????????????");
            userInfoBO.setStuId(stuId);
            if (stuIds.contains(stuId) || savedIds.contains(stuId)) {
                sb.append("???").append(i).append("???")
                        .append("????????????").append(userInfoBO.getRealName()).append("????????????????????????????????????????????????\n");
            }
            savedIds.add(stuId);
            i++;
        }
        if (!sb.isEmpty()) {
            return sb.toString();
        }

        //????????????
        for (UserInfoBO userInfoBO : userInfoBOS) {
            //??????????????????
            long userId = IdUtil.getSnowflakeNextId();
            UserInfoDO userInfoDO = UserInfoDO.builder()
                    .userName(userInfoBO.getRealName())
                    .realName(userInfoBO.getRealName())
                    .userId(userId)
                    .picUrl(ZCMUConstant.USER_PIC_URL)
                    .stuId(userInfoBO.getStuId())
                    .userClass(userInfoBO.getUserClass())
                    .major(userInfoBO.getMajor())
                    .sex(userInfoBO.getSex())
                    .email(userInfoBO.getEmail())
                    .patch(userInfoBO.isPatch())
                    .grade(userInfoBO.getGrade())
                    .collage(userInfoBO.getCollage())
                    .stuMark(userInfoBO.getStuMark())
                    .repair(userInfoBO.isRepair())
                    .minorMark(userInfoBO.getMinorMark())
                    .selfStudy(userInfoBO.isSelfStudy())
                    .ext("{}")
                    .deleted(false)
                    .build();
            userInfoDORepo.save(userInfoDO);

            //??????????????????
            String salt = UUID.randomUUID().toString();
            String password = ZCMUConstant.DEFAULT_PASSWORD == null ? userInfoBO.getStuId().toString() : ZCMUConstant.DEFAULT_PASSWORD;
            UserDO userDO = UserDO.builder()
                    .lastLoginIp("127.0.0.1")
                    .lastLoginDate(new Date())
                    .userId(userId)
                    .account(userInfoBO.getStuId().toString())
                    .salt(salt)
                    .password(DigestUtil.md5Hex(password + salt, StandardCharsets.UTF_8))
                    .build();
            userDORepo.save(userDO);

            //??????????????????
            UserPermRequest userPermRequest = UserPermRequest.builder()
                    .userId(userId)
                    .build();
            userPermRequest.setSkipVerify(true);
            for (PermEnum perm : perms) {
                userPermRequest.setCodeName(perm.getCode());
                permService.givePerm(userPermRequest);
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String login(UserRequest request) {
        UserDO userDO = userDORepo.findAllByAccount(request.getAccount());
        AssertUtil.assertNotNull(userDO, ExceptionResultCode.ILLEGAL_PARAMETERS, "??????????????????");
        String password = DigestUtil.md5Hex(request.getPassword() + userDO.getSalt(), StandardCharsets.UTF_8);
        AssertUtil.assertEquals(password, userDO.getPassword(), ExceptionResultCode.ILLEGAL_PARAMETERS, "???????????????????????????");
        UserInfoDO userInfoDO = userInfoDORepo.findAllByUserId(userDO.getUserId());
        AssertUtil.assertTrue(!userInfoDO.isDeleted(), ExceptionResultCode.ILLEGAL_PARAMETERS, "??????????????????????????????");
        userDORepo.login(userDO.getUserId(), new Date(), request.getLastLoginIp());
        return tokenUtil.getToken(userDO.getUserId());
    }

    @Override
    public boolean verifyLogin(String token) {
        boolean b = tokenUtil.verifyWithoutThrow(token);
        if (b) {
            String userId = tokenUtil.getUserTokenKey(token);
            UserInfoDO userInfoDO = userInfoDORepo.findAllByUserId(Long.valueOf(userId));
            if (userInfoDO.isDeleted()) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void logout(Long userId) {
        tokenUtil.delete(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updatePassword(UserRequest request) {
        String salt = UUID.randomUUID().toString();
        return userDORepo.updatePassword(request.getUserId()
                , DigestUtil.md5Hex(request.getPassword() + salt, StandardCharsets.UTF_8), salt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(UserRequest request) {
        //???????????????????????????
        userInfoDORepo.updateUserName(request.getUserId(),"??????????????????");
        userInfoDORepo.deleteUser(request.getUserId());
        userInfoDORepo.updatePic(request.getUserId(),ZCMUConstant.DELETED_PIC_URL);
        //todo ??????????????????unique???

        //????????????????????????
        permUserRelationDORepo.deleteAllByUserId(request.getUserId());
    }
}
