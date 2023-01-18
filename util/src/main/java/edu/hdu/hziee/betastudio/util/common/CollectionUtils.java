package edu.hdu.hziee.betastudio.util.common;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * 流操作工具
 */
public class CollectionUtils {

    /**
     * 转换stream 如果为空 返回空流 避免npe
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> Stream<T> toStream(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        return collection.stream();
    }
}
