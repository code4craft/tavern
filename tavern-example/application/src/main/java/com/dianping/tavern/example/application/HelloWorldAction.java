package com.dianping.tavern.example.application;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author code4crafter@gmail.com
 */
public class HelloWorldAction extends ActionSupport{

    @Autowired
    private TestBean testBean;

    private String msg;

    @Override
    public String execute() throws Exception {
        testBean.helloWorld();
        return SUCCESS;
    }

    public String getMsg() {
        return msg;
    }
}
