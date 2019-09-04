package com.xingag.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Juergen Punz
 * Injects InputEvents to Android-Device
 * Needs root-access to Device and App needs superuser-rights (you will be asked for that at execution-time).
 */
public class Injector
{

    /**
     * Injects Swipe-Event from right to left
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean swipeRightLeft() throws IOException, InterruptedException
    {
        return executeCommand("input swipe 300 500 50 500 100");
    }

    /**
     * Injects Swipe-Event from left to right
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean swipeLeftRight() throws IOException, InterruptedException
    {
        return executeCommand("input swipe 50 500 300 500 100");
    }

    /**
     * Injects Touch-Event at x- and y-coordinates of screen
     *
     * @param x x-coordinate of screen
     * @param y y-coordinate of screen
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean touch(int x, int y) throws IOException, InterruptedException
    {
        return executeCommand(String.format(Locale.getDefault(), "input tap %d %d", x, y));
    }

    /**
     * Injects Unlock-Event for unlocking the device's screen
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean unlockDevice() throws IOException, InterruptedException
    {
        return executeCommand("input keyevent 82");
    }

    /**
     * Injects Powerbutton-Event for locking or activate the device's screen
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean pressPowerButton() throws IOException, InterruptedException
    {
        return executeCommand("input keyevent 26");
    }

    /**
     * Injects Homebutton-Event
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean pressHomeButton() throws IOException, InterruptedException
    {
        return executeCommand("input keyevent 3");
    }

    /**
     * Injects Backbutton-Event
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean pressBackButton() throws IOException, InterruptedException
    {
        return executeCommand("input keyevent 4");
    }

    /**
     * Injects Swipe-Event (up to down) for opening the Notificationcenter
     *
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean showNotificationCenter() throws IOException, InterruptedException
    {
        return executeCommand("input swipe 10 10 10 1000");
    }

    /**
     * Runs given command in shell as superuser
     *
     * @param command Command to execute
     * @return If execution of shell-command was successful or not
     * @throws IOException
     * @throws InterruptedException
     */
    private static boolean executeCommand(String command) throws IOException, InterruptedException
    {
        Process suShell = Runtime.getRuntime().exec("su");
        DataOutputStream commandLine = new DataOutputStream(suShell.getOutputStream());

        commandLine.writeBytes(command + '\n');
        commandLine.flush();
        commandLine.writeBytes("exit\n");
        commandLine.flush();

        return suShell.waitFor() == 0;
    }
}
