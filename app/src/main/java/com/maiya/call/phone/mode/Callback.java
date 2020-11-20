package com.maiya.call.phone.mode;

/**
 * @ClassName: {@link Callback}
 * @Description:
 *
 * Created by admin at 2020-05-09
 * @Email xiaosw0802@163.com
 */
public interface Callback<R> {

    /**
     * 成功
     * @param response
     */
    void onSuccess(R response);

    /**
     * 失败
     * @param code
     * @param message
     */
    void onFailed(int code, String message);

}
