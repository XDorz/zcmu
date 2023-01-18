package edu.hdu.hziee.betastudio.util.common;

import lombok.Getter;
import org.springframework.data.domain.Pageable;

/**
 * 为分页参数提供的Pageable的封装
 * {@link PageRequest}
 */
@Getter
public class PageRequest {

    Integer page=0;

    Integer size=10;

    public void setPage(Integer page) {
        if(page!=null && page>0) this.page=page;
    }

    public void setSize(Integer size) {
        if(size!=null && size>0) this.size=size;
    }

    public Pageable getPageable(){
        return org.springframework.data.domain.PageRequest.of(page,size);
    }

    public void setPageable(Integer page,Integer size){
        this.page=page==null?0:page;
        this.size=size==null?10:size;
    }
}
