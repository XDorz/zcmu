package edu.hdu.hziee.betastudio.business.user.convert;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.model.UserBO;
import edu.hdu.hziee.betastudio.business.user.model.UserInfoBO;
import edu.hdu.hziee.betastudio.dao.user.model.UserDO;
import edu.hdu.hziee.betastudio.dao.user.model.UserInfoDO;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.ZCMUConstant;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * BO<=>DO 转换器
 */
@Component
public class UserConvert {

    public UserBO convert(UserDO userDO) {
        AssertUtil.assertNotNull(userDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return UserBO.builder()
                .userId(userDO.getUserId())
                .account(userDO.getAccount())
                .lastLoginIp(userDO.getLastLoginIp())
                .lastLoginDate(userDO.getLastLoginDate())
                .password(userDO.getPassword())
                .build();
    }

    public UserDO convert(UserBO userBO) {
        AssertUtil.assertNotNull(userBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return UserDO.builder()
                .userId(userBO.getUserId() == null ? IdUtil.getSnowflakeNextId() : userBO.getUserId())
                .account(userBO.getAccount())
                .lastLoginIp(userBO.getLastLoginIp())
                .lastLoginDate(userBO.getLastLoginDate() == null ? new Date() : userBO.getLastLoginDate())
                .password(userBO.getPassword())
                .build();
    }

    public UserInfoDO convert(UserInfoBO userInfoBO) {
        AssertUtil.assertNotNull(userInfoBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return UserInfoDO.builder()
                .userId(userInfoBO.getUserId() == null ? IdUtil.getSnowflakeNextId() : userInfoBO.getUserId())
                .grade(userInfoBO.getGrade())
                .major(userInfoBO.getMajor())
                .collage(userInfoBO.getCollage())
                .patch(userInfoBO.isPatch())
                .email(userInfoBO.getEmail())
                .deleted(userInfoBO.isDeleted())
                .sex(userInfoBO.getSex())
                .stuId(userInfoBO.getStuId())
                .picUrl(userInfoBO.getPicUrl() == null ? ZCMUConstant.USER_PIC_URL : userInfoBO.getPicUrl())
                .repair(userInfoBO.isRepair())
                .stuMark(userInfoBO.getStuMark())
                .userClass(userInfoBO.getUserClass())
                .userName(userInfoBO.getUserName())
                .realName(userInfoBO.getRealName())
                .minorMark(userInfoBO.getMinorMark())
                .selfStudy(userInfoBO.isSelfStudy())
                .ext(JSONObject.toJSONString(userInfoBO.getExt()))
                .build();
    }

    public UserInfoBO convert(UserInfoDO userInfoDO) {
        AssertUtil.assertNotNull(userInfoDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return UserInfoBO.builder()
                .userId(userInfoDO.getUserId())
                .grade(userInfoDO.getGrade())
                .major(userInfoDO.getMajor())
                .collage(userInfoDO.getCollage())
                .patch(userInfoDO.isPatch())
                .email(userInfoDO.getEmail())
                .deleted(userInfoDO.isDeleted())
                .sex(userInfoDO.getSex())
                .stuId(userInfoDO.getStuId())
                .picUrl(userInfoDO.getPicUrl())
                .repair(userInfoDO.isRepair())
                .stuMark(userInfoDO.getStuMark())
                .userClass(userInfoDO.getUserClass())
                .userName(userInfoDO.getUserName())
                .realName(userInfoDO.getRealName())
                .minorMark(userInfoDO.getMinorMark())
                .selfStudy(userInfoDO.isSelfStudy())
                .ext(JSONObject.parseObject(userInfoDO.getExt()).toJavaObject(new TypeReference<Map<String, String>>() {
                }))
                .build();
    }

    public AppUserInfoBO convertApp(UserInfoDO userInfoDO) {
        AssertUtil.assertNotNull(userInfoDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return AppUserInfoBO.builder()
                .userId(userInfoDO.getUserId())
                .userName(userInfoDO.getUserName())
                .picUrl(userInfoDO.getPicUrl())
                .sex(userInfoDO.getSex())
                .ext(JSONObject.parseObject(userInfoDO.getExt()).toJavaObject(new TypeReference<Map<String, String>>() {
                }))
                .stuId(userInfoDO.getStuId())
                .build();
    }
}
