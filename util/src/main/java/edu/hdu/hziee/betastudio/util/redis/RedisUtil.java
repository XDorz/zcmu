package edu.hdu.hziee.betastudio.util.redis;

import edu.hdu.hziee.betastudio.util.redis.children.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisUtil {

    @Autowired
    private HashUtil hashUtil;

    @Autowired
    private KeyUtil keyUtil;

    @Autowired
    private ListUtil listUtil;

    @Autowired
    private SetUtil setUtil;

    @Autowired
    private StringUtil stringUtil;

    @Autowired
    private ZSetUtil zsetUtil;
}
