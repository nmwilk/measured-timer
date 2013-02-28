package com.measuredsoftware.android.library2.utils;

import org.w3c.dom.NodeList;

public class XMLUtils
{
  public static String extractContentFromNode(NodeList nodeList)
  {
    String sContent = "";
    
    if (nodeList != null)
    {
      int size = nodeList.getLength();
      for(int i=0; i < size; i++)
      {
        sContent += nodeList.item(i).getNodeValue();
      }
    }
    
    return sContent;
  }
  
  public static String decodeHTML(String sHTML)
  {
    return sHTML.replaceAll("&amp;", "&");
  }
}
