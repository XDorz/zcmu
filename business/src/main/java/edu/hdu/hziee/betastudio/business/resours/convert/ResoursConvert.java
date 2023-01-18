package edu.hdu.hziee.betastudio.business.resours.convert;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.dao.resours.model.ResoursDO;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResoursConvert {

    public ResoursBO convert(ResoursDO resoursDO){
        AssertUtil.assertNotNull(resoursDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return ResoursBO.builder()
                .belongId(resoursDO.getBelongId())
                .userId(resoursDO.getUserId())
                .url(resoursDO.getUrl())
                .picUrl(resoursDO.getPicUrl())
                .info(resoursDO.getInfo())
                .name(resoursDO.getName())
                .resourceId(resoursDO.getResourceId())
                .ext(JSONObject.parseObject(resoursDO.getExt()).toJavaObject(new TypeReference<Map<String, String>>() {
                })).build();
    }

    public ResoursDO convert(ResoursBO resoursBO){
        AssertUtil.assertNotNull(resoursBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return ResoursDO.builder()
                .belongId(resoursBO.getBelongId())
                .userId(resoursBO.getUserId())
                .url(resoursBO.getUrl())
                .picUrl(resoursBO.getPicUrl())
                .info(resoursBO.getInfo())
                .name(resoursBO.getName())
                .resourceId(resoursBO.getResourceId()==null? IdUtil.getSnowflakeNextId():resoursBO.getResourceId())
                .deleted(false)
                .ext(JSONObject.toJSONString(resoursBO.getExt()))
                .build();
    }
}
