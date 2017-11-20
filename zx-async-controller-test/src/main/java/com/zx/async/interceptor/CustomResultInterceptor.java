package com.zx.async.interceptor;

import com.zx.async.handler.CustomResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 12:51
 *
 * 实现DeferredResultProcessingInterceptor接口的异步拦截器,
 * 也可以直接继承DeferredResultProcessingInterceptorAdapter类来使用,
 * 如果使用适配器类,可以不重写不想要的方法,其他都是一样的
 *
 * 此外,有一个{@link org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor}类,
 * 只是重写了超时方法,setErrorResult了一个异常进去,可以重写该类方法
 */
@Component
@Slf4j
public class CustomResultInterceptor implements DeferredResultProcessingInterceptor {
    //自定义的返回值处理器
    @Autowired
    private CustomResultHandler customResultHandler;

    /**
     * 在controller层返回result之后,执行这个方法,
     * 但并不确保一定在deferredResult对象处理前进入,因为处理方法也是异步的;
     *
     * 但是源码中是说该方法是在异步处理前调用的
     * @param request 当前请求
     * @param deferredResult 当前请求的延期返回对象
     * @param <T>
     * @throws Exception
     */
    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        log.info("result拦截器,beforeConcurrentHandling方法");

    }

    /**
     * 对于DeferredResult的异步处理开始时,就立即调用该方法
     * @param request
     * @param deferredResult
     * @param <T>
     * @throws Exception
     */
    @Override
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        log.info("result拦截器,preProcess方法");

    }

    /**
     * 在调用{@link DeferredResult#setResult(Object)}或{@link DeferredResult#setErrorResult(Object)}后立即调用,
     * 也会在触发延期返回对象的超时方法时立即调用
     * @param request
     * @param deferredResult
     * @param concurrentResult deferredResult的返回结果
     * @param <T>
     * @throws Exception
     */
    @Override
    public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult) throws Exception {
        log.info("result拦截器,postProcess方法");
        /**
         * 设置返回值的处理器
         * 看了下源码,这个方法必须在DeferredResult没有超时,并且返回值不是默认值,(当然,返回值也不能已经被返回到浏览器了)
         * 也就是调用了setResult或setErrorResult方法(没调用之前,该对象有一个默认的空的返回值)的时候调用;
         *
         * 如果返回值是默认值,该方法将被赋值给DeferredResult的resultHandler属性,理论上也会在最后set值的时候被调用,
         * 但我试了很多次,发现调用的都不是自己设置的这个resultHandler,最后才发现,这个属性在中间被
         * {@link org.springframework.web.context.request.async.WebAsyncManager#startDeferredResultProcessing}方法替换过,
         * 替换成了将返回值传递给...这些拦截器链这样的一个handler...也就是这个handler把返回值传递到了这个postProcess方法...
         *
         *
         * 所以,这个方法,就是在这个阶段(拦截器的该方法)调用合适,可以更改返回值
         */
        deferredResult.setResultHandler(customResultHandler);

    }

    /**
     * 超时时调用,
     * 具体的实现中,可以继续调用{@link DeferredResult#setResult(Object)}或{@link DeferredResult#setErrorResult(Object)}让请求继续;
     *
     * 注意:源码中没有声明的是,如果已经给某个DeferredResult对象声明了onTimeout()方法,则该result对象超时时不会触发本方法
     * @param request
     * @param deferredResult
     * @param <T>
     * @return 返回true,则后续的拦截器会继续;返回false,则后续的拦截器不会触发
     * @throws Exception
     */
    @Override
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        log.info("result拦截器,handleTimeout方法");
        return true;
    }

    /**
     * 异步请求完成后调用.
     * 该方法、postProcess方法、DeferredResult.onTimeout方法、onCompletion方法,
     * 都会另起一个容器(tomcat?)线程(处理controller请求的线程,应该是tomcat中的)进行处理
     *
     * 该方法和onCompletion方法如果同时设置,都会被调用
     * @param request
     * @param deferredResult
     * @param <T>
     * @throws Exception
     */
    @Override
    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        log.info("result拦截器,afterCompletion方法");
    }
}
