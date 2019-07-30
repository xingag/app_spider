package com.xingag.bean;

public class Contact
{
    private String userName;
    private String alias;
    private String nickName;

    public Contact(String userName, String alias, String nickName)
    {
        this.userName = userName;
        this.alias = alias;
        this.nickName = nickName;
    }

    public Contact(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
}
