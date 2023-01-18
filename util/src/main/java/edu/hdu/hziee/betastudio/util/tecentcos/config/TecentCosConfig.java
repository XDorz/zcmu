package edu.hdu.hziee.betastudio.util.tecentcos.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.region.Region;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Configuration
@PropertySource("classpath:/config/application.yml")
@ConfigurationProperties(prefix = "zcmu.tecent.cos.config")
public class TecentCosConfig {

    /**
     * bucket地区
     */
    private String region;
    /**
     * 秘钥id
     */
    private String secretId;
    /**
     * 秘钥
     */
    private String secretKey;
    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 腾讯主账号APPID
     */
    private String appId;
    private COSClient cosClient;

    @PostConstruct
    public void initConfig() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region cosRegion = new Region(region);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(cosRegion);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient client = new COSClient(cred, clientConfig);
        cosClient = client;
        boolean hasBucket = false;
        for (Bucket bucket : client.listBuckets()) {
            if (bucket.getName().equals(bucketName)) {
                hasBucket = true;
                break;
            }
        }

        if (!hasBucket) {
            //创建存储桶
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            // 设置 bucket 的权限为 Private(私有读写)、其他可选有 PublicRead（公有读私有写）、PublicReadWrite（公有读写）
            createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            try {
                Bucket bucketResult = cosClient.createBucket(createBucketRequest);
            } catch (CosServiceException serverException) {
                log.error("COS存储桶创建失败！！！请登录腾讯云检查\n【https://console.cloud.tencent.com/cos/bucket】", serverException);
                return;
            } catch (CosClientException clientException) {
                log.error("COS客户端出现问题，请检查是否有最新版本SDK！", clientException);
                return;
            }
            log.info("存储桶创建完毕，存储桶全名:" + bucketName);
        }
    }

    @Bean("cos")
    public COSClient getClient() {
        return cosClient;
    }
}
