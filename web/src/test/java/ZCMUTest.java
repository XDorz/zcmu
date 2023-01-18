import com.qcloud.cos.model.PutObjectResult;
import edu.hdu.hziee.betastudio.ZCMU_Application;
import edu.hdu.hziee.betastudio.util.tecentcos.CosUtil;
import edu.hdu.hziee.betastudio.util.tecentcos.config.TecentCosConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ZCMU_Application.class, TecentCosConfig.class})
public class ZCMUTest {

    @Autowired
    CosUtil cosUtil;

    @Test
    public void TestCos() throws IOException {
        File file=new File(System.getProperty("user.home")+"/desktop/caoshen.png");
//        PutObjectResult result = cosUtil.uploadFile("caoshen3.png", new FileInputStream(file));
//        System.out.println(cosUtil.getFileUrl("caoshen3.png"));
    }
}
