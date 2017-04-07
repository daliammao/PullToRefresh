package com.daliammao.pulltorefresh.bean;

import java.util.List;

/**
 * @author: zhoupengwei
 * @time:15/12/4-下午5:44
 * @Email: 496946423@qq.com
 * @desc:
 */
public class Data {
    private String time;
    private List<Img> list;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Img> getList() {
        return list;
    }

    public void setList(List<Img> list) {
        this.list = list;
    }
}
