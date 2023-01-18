package edu.hdu.hziee.betastudio.business.resours.service;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;

import java.util.List;

public interface ResoursService {

    List<ResoursBO> getListByBelongId(Long belongId);

    ResoursBO createResource(ResoursRequest request);

    void connectResource(ResoursRequest request);

    Integer deleteResource(ResoursRequest request);

    Integer updateResourceName(ResoursRequest request);

    Integer updateResourceInfo(ResoursRequest request);

    Integer updateResourcePicUrl(ResoursRequest request);
}
