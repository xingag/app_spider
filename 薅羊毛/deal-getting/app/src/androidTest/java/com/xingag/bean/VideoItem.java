package com.xingag.bean;

public class VideoItem
{
    //播放时长
    private int play_time;

    //作者
    private String author;

    //内容
    private String content;


    public VideoItem(int play_time, String author, String content)
    {
        this.play_time = play_time;
        this.author = author;
        this.content = content;
    }

    public int getPlay_time()
    {
        return play_time;
    }

    public void setPlay_time(int play_time)
    {
        this.play_time = play_time;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }


    @Override
    public String toString()
    {
        return "作者:" + author + "\n内容：" + content + "\n,播放时长:" + play_time+"s";
    }
}
