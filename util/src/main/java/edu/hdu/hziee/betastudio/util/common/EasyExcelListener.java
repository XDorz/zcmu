package edu.hdu.hziee.betastudio.util.common;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;

import java.util.List;
import java.util.function.Consumer;

public class EasyExcelListener<T> implements ReadListener<T> {

    /**
     * 最大缓存数据条数
     */
    private static final int BATCH_COUNT = 100;

    /**
     * listener内部缓存
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 自定义处理方法
     */
    private Consumer<List<T>> custom;

    public EasyExcelListener(List<T> saveList){
        this(saveList::addAll);
    }

    public EasyExcelListener(Consumer<List<T>> custom){
        this.custom=custom;
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        cachedDataList.add(t);
        if (cachedDataList.size() >= BATCH_COUNT) {
            custom.accept(cachedDataList);
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * excel读取完毕，调用此方法将剩余部分刷入
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        custom.accept(cachedDataList);
        cachedDataList=ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }
}
