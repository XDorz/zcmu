package edu.hdu.hziee.betastudio.web.controller;

import edu.hdu.hziee.betastudio.business.comment.model.CommentBO;
import edu.hdu.hziee.betastudio.business.comment.model.SimpleThemeBO;
import edu.hdu.hziee.betastudio.business.comment.model.ThemeBO;
import edu.hdu.hziee.betastudio.business.comment.request.CommentRequest;
import edu.hdu.hziee.betastudio.business.comment.service.CommentService;
import edu.hdu.hziee.betastudio.business.comment.service.ThemeService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.IpUtil;
import edu.hdu.hziee.betastudio.util.customenum.ClientTypeEnum;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.EnumUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.CommentRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    ThemeService themeService;

    @CheckLogin
    @PostMapping("/theme/create")
    public ZCMUResult<ThemeBO> create(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "创建主题帖", request, httpServletRequest, new OperateCallBack<ThemeBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeTitle(), ExceptionResultCode.ILLEGAL_PARAMETERS,"主题标题不能为空");
                AssertUtil.assertTrue(StringUtils.hasText(request.getContent()), ExceptionResultCode.ILLEGAL_PARAMETERS,"评论内容不能为空");
                AssertUtil.assertNotNull(request.getClientType(), ExceptionResultCode.ILLEGAL_PARAMETERS,"客户端类型不能为空");
                AssertUtil.assertTrue(EnumUtil.isExist(ClientTypeEnum.class,request.getClientType())
                        ,ExceptionResultCode.FORBIDDEN,"非法的客户端类型！");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<ThemeBO> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeTitle(request.getThemeTitle())
                        .content(request.getContent())
                        .userId(request.getUserId())
                        .clientType(request.getClientType())
                        .ipAddr(IpUtil.getIp(httpServletRequest))
                        .picIdList(request.getPicIdList())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(themeService.createTheme(commentRequest),"创建主题帖成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/theme/own")
    public ZCMUResult<List<SimpleThemeBO>> browserSelfTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "查看自己发布的的主题帖", request, httpServletRequest, new OperateCallBack<List<SimpleThemeBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<SimpleThemeBO>> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(themeService.getAllSelfTheme(commentRequest),"获取创建的主题帖成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/theme/themelist")
    public ZCMUResult<List<SimpleThemeBO>> getAllTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "拉取所有主题帖简要信息", request, httpServletRequest, new OperateCallBack<List<SimpleThemeBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<SimpleThemeBO>> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(themeService.getAllTheme(commentRequest),"拉取帖子列表成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/theme")
    public ZCMUResult<ThemeBO> selectTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "浏览一个主题帖", request, httpServletRequest, new OperateCallBack<ThemeBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"主题帖id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<ThemeBO> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(themeService.getTheme(commentRequest),"拉取成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/theme/hot")
    public ZCMUResult<Void> increaseThemeHot(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "提升一个主题帖的热度", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"主题帖id不能为空");
                AssertUtil.assertNotNull(request.getHot(), ExceptionResultCode.ILLEGAL_PARAMETERS,"提升的热度值不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .hot(request.getHot())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                themeService.increaseHot(commentRequest);
                return RestUtil.buildSuccessResult(null,"提升热度成功");
            }
        });
    }

    @CheckLogin
    @DeleteMapping ("/theme")
    public ZCMUResult<Void> deleteTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "删除一个帖子", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"主题帖id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                themeService.deleteTheme(commentRequest);
                return RestUtil.buildSuccessResult(null,"删除一条帖子成功");
            }
        });
    }

    @CheckLogin
    @PostMapping("/create")
    public ZCMUResult<CommentBO> createComment(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "发布一条评论", request, httpServletRequest, new OperateCallBack<CommentBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"所属主题id不能为空");
                AssertUtil.assertNotNull(request.getClientType(), ExceptionResultCode.ILLEGAL_PARAMETERS,"客户端类型不能为空");
                AssertUtil.assertTrue(StringUtils.hasText(request.getContent()), ExceptionResultCode.ILLEGAL_PARAMETERS,"评论内容不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<CommentBO> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .previousCommentId(request.getPreviousCommentId())
                        .masterId(request.getMasterId())
                        .userId(request.getUserId())
                        .ipAddr(IpUtil.getIp(httpServletRequest))
                        .clientType(request.getClientType())
                        .content(request.getContent())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(commentService.createComment(commentRequest),"评论发布成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/theme/subscribe")
    public ZCMUResult<Void> subscribeTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "订阅某个主题", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"订阅的主题id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                themeService.subscribeTheme(commentRequest);
                return RestUtil.buildSuccessResult(null,"主题订阅成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/theme/unsubscribe")
    public ZCMUResult<Void> unSubscribeTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "取消订阅某个主题", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"取消订阅的主题id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                themeService.unSubscribeTheme(commentRequest);
                return RestUtil.buildSuccessResult(null,"主题订阅取消成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/theme/subscribed")
    public ZCMUResult<List<SimpleThemeBO>> subscribedTheme(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "获取所有订阅的主题", request, httpServletRequest, new OperateCallBack<List<SimpleThemeBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<SimpleThemeBO>> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(themeService.getSubscribeThemes(commentRequest),"主题订阅获取成功");
            }
        });
    }

    /**
     * 【备用接口，其功能已被其他接口替代】
     * {@link #selectTheme}
     */
    @CheckLogin
    @GetMapping
    public ZCMUResult<CommentBO> getComment(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "获取一条评论的详细信息", request, httpServletRequest, new OperateCallBack<CommentBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getCommentId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"评论id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<CommentBO> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .commentId(request.getCommentId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(commentService.findComment(commentRequest),"评论获取成功");
            }
        });
    }

    /**
     * 【备用接口，其功能已被其他接口替代】
     * {@link #selectTheme}
     */
    @CheckLogin
    @GetMapping("/theme/comment")
    public ZCMUResult<List<CommentBO>> getThemeComments(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "获取主题贴下的所有评论", request, httpServletRequest, new OperateCallBack<List<CommentBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"查询的主题id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<CommentBO>> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(commentService.findCommentList(commentRequest),"获取主题贴下的所有评论成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/theme/title")
    public ZCMUResult<Void> updateThemeTitle(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "修改主题帖标题", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getThemeId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"要修改的主题id不能为空");
                AssertUtil.assertNotNull(request.getThemeTitle(), ExceptionResultCode.ILLEGAL_PARAMETERS,"新的主题标题不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .themeId(request.getThemeId())
                        .userId(request.getUserId())
                        .themeTitle(request.getThemeTitle())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                themeService.updateThemeName(commentRequest);
                return RestUtil.buildSuccessResult(null,"主题帖标题修改成功");
            }
        });
    }

    /**
     * 【也许不需要这个接口？】
     */
    @CheckLogin
    @PutMapping("/content")
    public ZCMUResult<Void> updateCommentContent(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户修改评论内容", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getCommentId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"评论id不能为空");
                AssertUtil.assertTrue(StringUtils.hasText(request.getContent()), ExceptionResultCode.ILLEGAL_PARAMETERS,"评论内容不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .commentId(request.getCommentId())
                        .content(request.getContent())
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                commentService.updateContent(commentRequest);
                return RestUtil.buildSuccessResult(null,"修改评论内容成功");
            }
        });
    }

    /**
     * 【删除无法撤销】
     */
    @CheckLogin
    @DeleteMapping
    public ZCMUResult<Void> deleteComment(CommentRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "删除评论", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getCommentId(), ExceptionResultCode.ILLEGAL_PARAMETERS,"删除的评论id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                CommentRequest commentRequest = CommentRequest.builder()
                        .commentId(request.getCommentId())
                        .userId(request.getUserId())
                        .build();
                commentRequest.setVerifyId(request.getUserId());
                commentService.deleteComment(commentRequest);
                return RestUtil.buildSuccessResult(null,"评论删除成功");
            }
        });
    }
}
