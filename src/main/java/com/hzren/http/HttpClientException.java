package com.hzren.http;

/**
 * @author chenwei
 * @version $Id: HttpClientException.java, v 0.1 2015年12月5日 下午11:10:07 chenwei Exp $
 */
public class HttpClientException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HttpClientException() {
        super();
    }

    public HttpClientException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(Throwable cause) {
        super(cause);
    }

}
