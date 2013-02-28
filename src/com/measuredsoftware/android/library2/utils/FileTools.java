package com.measuredsoftware.android.library2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class FileTools
{
  public static boolean createPath(String sFolderPath)
  {
    boolean bOK = true;
    File file = new File(sFolderPath);
    try
    {
      file.mkdirs();
    }
    catch(SecurityException e)
    {
      bOK = false;
    }
    
    return bOK;
  }
  
  public static boolean createFile(String sPath, String sFile)
  {
    boolean bOK = true;
    File file = new File(sPath, sFile);
    try
    {
      file.createNewFile();
    }
    catch(IOException e)
    {
      bOK = false;
    }
    
    return bOK;
  }
  
  public static boolean deleteFile(String sPath)
  {
    boolean bSuccess = false;
    try
    {
      File file = new File(sPath);
      bSuccess = file.delete();
    }
    catch(SecurityException e)
    {
    }
    return bSuccess;
  }
  
  public static byte[] loadDataFile(String path, String file) {
    path = Environment.getExternalStorageDirectory() + path;
    if (!FileTools.createPathAndFile(path, file))
      return null;

    String s = path+file;

    File f = new File(s);
    final int size = (int)f.length();
    
    FileInputStream in;
    byte[] buffer = new byte[size];
    try {
      in = new FileInputStream(s);
      in.read(buffer);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return buffer;
  }
  
  public static boolean createPathAndFile(String sPath, String sFile)
  {
    boolean bOK = true;
    File filePath = new File(sPath);
    
    try
    {
      filePath.mkdirs();
      if (!filePath.canWrite())
        bOK = false;
    }
    catch(SecurityException e)
    {
      bOK = false;
    }
    
    if (bOK)
    {
      File file = new File(filePath, sFile);
      try
      {
        file.createNewFile();
      }
      catch(IOException e)
      {
        bOK = false;
      }
    }
    
    return bOK;    
  }
  
  public static String formatFileName(String sFileName)
  {
    return formatFilePath(sFileName);
  }
  
  public static String formatFilePath(String sFilePath)
  {
    int nLen = sFilePath.length();
    String sRet = "";
    char c;
    for(int i=0; i < nLen; i++)
    {
      c = sFilePath.charAt(i);
      if (!Character.isDigit(c) && !Character.isLetter(c) && /*!Character.isWhitespace(c) &&*/ c != '/' && c != '.')
      {
        sRet += "_";
        continue;
      }

      sRet += c;
    }
    
    return sRet;
  }
  
  public static boolean fileExists(String sFilePath)
  {
    File file = new File(sFilePath);
    return file.exists();
  }
  
  public static boolean fileSizeDiffers(String sFilePath, int nSize)
  {
    long lLength = getFileSize(sFilePath); 
    if (lLength != nSize)
      return true;
    
    return false;
  }
  
  public static long getFileSize(String sFilePath)
  {
    long lLength = -1;
    File file = new File(sFilePath);
    if (file.exists())
      lLength = file.length();
    
    return lLength;
  }
  
  public static String getExtension(String sFilePath, boolean bIncludeDot)
  {
    int nIndex = sFilePath.lastIndexOf('.');
    if (nIndex == -1)
      return "";
    
    String temp = sFilePath.substring(nIndex+1);
    if (bIncludeDot)
      temp = "." + temp;
    
    return temp;
  }
  
  
  public static boolean extIsOneOf(String sFileName, String sTestExts)
  {
    String[] saTestExts = sTestExts.split(" ");
    
    for(String sExt : saTestExts)
    {
      if (extIs(sFileName, sExt))
        return true;
    }
    
    return false;
  }
  
  public static boolean extIs(String sFileName, String sTestExt)
  {
    return (getExtension(sFileName, false).compareToIgnoreCase(sTestExt) == 0);
  }
  
  /*
   * Returns whether or not the sd card is available
   */
  public static boolean sdCardIsAvailable()
  {
    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); 
  }
  
  // path should have prefixed and postfixed /
  public static boolean dumpBitmap(Bitmap bmp, String path, String file) {
    path = Environment.getExternalStorageDirectory() + path;
    if (!FileTools.createPathAndFile(path, file))
      return false;

    String s = path+file;
    FileOutputStream of;
    try {
      of = new FileOutputStream(s);
      bmp.compress(CompressFormat.PNG, 80, of);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public static boolean dumpTextFile(String text, String path, String fileName) {
    return dumpByteArray(text.getBytes(), path, fileName);
  }
  
  public static boolean dumpByteArray(byte[] ba, String path, String fileName) {
    path = Environment.getExternalStorageDirectory() + path;
    if (!FileTools.createPathAndFile(path, fileName))
      return false;
    
    String s = path+fileName;
    FileOutputStream of;
    try {
      of = new FileOutputStream(s);
      of.write(ba);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
    
  }  
}
