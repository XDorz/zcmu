package edu.hdu.hziee.betastudio.util.tecentcos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import edu.hdu.hziee.betastudio.util.common.ZCMUException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

import static com.qcloud.cos.demo.BucketRefererDemo.cosClient;

@Data
@Slf4j
@Component
@PropertySource("classpath:/config/application.yml")
@ConfigurationProperties(prefix = "zcmu.tecent.cos")
public class CosUtil {

    @Autowired
    COSClient client;

    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 存储前缀
     */
    private String fileHost;
    /**
     * 访问域名
     */
    private String endpoint;

    public String uploadFile(String saveName, InputStream fileStream){
        //todo 修改为高级传输
        //将文件名修改为随机名
        saveName= UUID.randomUUID()+getFileSuffix(saveName);
        ObjectMetadata metadata = new ObjectMetadata();
        try {
            metadata.setContentLength(fileStream.available());
        }catch (IOException e){
            log.error("获取stream流大小失败",e);
            throw new ZCMUException("获取stream流大小失败");
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileHost+saveName,fileStream,metadata);
        client.putObject(putObjectRequest);
        return getFileUrl(saveName);
    }

    public String uploadFile(String saveName, File file){
        try {
            return uploadFile(saveName,new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error("从文件中获取输入流失败！！！");
            throw new ZCMUException("从文件中获取输入流失败！！！");
        }
    }

    public String uploadFile(MultipartFile file){
        try {
            return uploadFile(file.getOriginalFilename(),file.getInputStream());
        } catch (IOException e) {
            log.error("从上传文件打开文件流失败！",e);
            throw new ZCMUException("从上传文件打开文件流失败！");
        }
    }

    /**
     * 从文件名获取文件url
     */
    private String getFileUrl(String fileName){
        return endpoint+fileHost+fileName;
    }

    /**
     * 获取文件后缀
     */
    private String getFileSuffix(String fileName){
        int i=-1;
        if((i=fileName.lastIndexOf('.'))!=-1){
            return "."+fileName.substring(i+1);
        }
        return "."+fileName;
    }
}
