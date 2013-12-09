package com.dianping.tavern.config;

/**
 * @author code4crafter@gmail.com
 */
public class ApplicationConfig {

    /**
     * 应用名
     */
    private String name;

    /**
     * 包名，作为判断当前上线文的依据<br>
     * 约定不同app不可使用相同的包名
     */
    private String packageBase;

    /**
     * 父类，用于属性继承
     */
    private String parent;

    /**
     * spring配置路径
     */
    private String contextPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageBase() {
        return packageBase;
    }

    public void setPackageBase(String packageBase) {
        this.packageBase = packageBase;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
