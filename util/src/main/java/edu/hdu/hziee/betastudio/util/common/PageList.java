package edu.hdu.hziee.betastudio.util.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 将数据库中取出的spring分页数据进一步解析与转化
 *
 * @param <E> page数据转换后的类型
 */
@Data
public class PageList<E> {

    /**
     * 总数据
     */
    private Long totalElements;

    /**
     * 总页码
     */
    private Integer totalPages;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 当前页
     */
    private Integer number;

    /**
     * 当前页条数
     */
    private Integer numberOfElements;

    /**
     * 是否是首页
     */
    private Boolean first;

    /**
     * 是否是尾页
     */
    private Boolean end;

    /**
     * 数据去重
     */
    private List<E> content;


    /**
     * 将分页转换成pageList并转换数据内容
     *
     * @param page      spring提供的分页
     * @param convert   由T至于E的转化方法
     * @param <T>       转换后的数据
     */
    public <T> PageList(Page<T> page, Function<T,E> convert){
        init(page,convert);
    }

    /**
     * 对pageList做一次数据内容转换
     *
     * @param pageList
     * @param convert
     * @param <T>
     */
    public <T> PageList(PageList<T> pageList,Function<T,E> convert){
        init(pageList,convert);
    }

    /**
     * 普通地将page转为pageList
     *
     * @param page
     */
    public PageList(Page<E> page){
        init(page);
    }

    public <T> void init(Page<T> page, Function<T,E> convert){
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();
        this.end = page.isLast();
        this.content =CollectionUtils.toStream(page.getContent())
                .filter(Objects::nonNull)
                .map(convert)
                .collect(Collectors.toList());
    }

    private void init(Page<E> page) {
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();
        this.end = page.isLast();
        this.content =page.getContent();
    }

    private <T> void init(PageList<T> pageList, Function<T,E> convert) {
        this.totalElements = pageList.getTotalElements();
        this.totalPages = pageList.getTotalPages();
        this.size = pageList.getSize();
        this.number = pageList.getNumber();
        this.numberOfElements = pageList.getNumberOfElements();
        this.first = pageList.getFirst();
        this.end = pageList.getEnd();
        this.content =CollectionUtils.toStream(pageList.getContent())
                .filter(Objects::nonNull)
                .map(convert)
                .collect(Collectors.toList());
    }
}
