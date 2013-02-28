package com.measuredsoftware.android.library2.utils;

public class DeviceTools {

  public static boolean isSEX10MiniOrPro(String devicename) {
    if (devicename == null)
      return false;
    
    if (devicename.contains("e10a") || devicename.contains("e10i") || devicename.contains("u20a") || devicename.contains("u20i") || devicename.contains("x10 mini"))
      return true;

    return false;
  }

  /**
   * is it a device that has the quick switch problem (lifting then quickly tapping other 
   * side with other thumb counts as a multitouch - so pocket racing doesn't see the lift 
   * and car keeps turning) 
   * @param devicename
   * @return
   */
  public static boolean isGalaxySTouchScreenFix(String devicename) {
    if (devicename.contains("sph-d700")   || 
        devicename.contains("gt-9000")    || 
        devicename.contains("gt-i9000")   || 
        devicename.contains("gt-i9020")   || 
        devicename.contains("sgh-t959")   || 
        devicename.contains("sc-02b")     || // added 2011-01-15 - av was 1.94
        devicename.contains("gt-p1000")   ||
        devicename.contains("sph-p100")   ||
        //devicename.contains("shw-m110s") || removed cos play av was 0.31
        devicename.contains("gt-i5503")   || // added cos play av was 0.52
        devicename.contains("shw-m130l")  || 
        devicename.contains("nexus s")    || 
        devicename.contains("eris") ||       // added after email from Chris Stanton
        devicename.contains("sgh-i897")) {
      return true;
    }    
    
    return false;
  }
  
  public static boolean supportsHaptics(String devicename) {
    if (devicename.contains("sph-d700")   || 
        devicename.contains("gt-9000")    || 
        devicename.contains("gt-i9000")   || 
        devicename.contains("gt-i9020")   || 
        devicename.contains("sgh-t959")   || 
        devicename.contains("sc-02b")     || // added 2011-01-15 - av was 1.94
        devicename.contains("gt-p1000")   ||
        devicename.contains("sph-p100")   ||
        devicename.contains("shw-m130l")  || 
        devicename.contains("sgh-i897")) {
      return true;
    }    
    
    return false;
  }

  public static boolean canHandle60fps(String devicename) {
    if (devicename.contains("sph-d700")   || 
        devicename.contains("gt-9000")    || 
        devicename.contains("gt-i9000")   || 
        devicename.contains("gt-i9020")   || 
        devicename.contains("sgh-t959")   || 
        devicename.contains("sc-02b")     || 
        devicename.contains("gt-p1000")   ||
        devicename.contains("shw-m110s")  || 
        devicename.contains("gt-i5503")   || 
        devicename.contains("shw-m130l")  || 
        devicename.contains("nexus s")    || 
        devicename.contains("sgh-i897")) {
      return true;
    }    
    
    return false;
  }
  
  public static boolean crapMultitouch(String devicemodel) {
    return (devicemodel.contains("htc") || devicemodel.contains("nexus one"));
  }

  public static boolean hasSelectButton(String devicemodel) {
    if (devicemodel.contains("htc desire")   || 
        devicemodel.contains("hero")    || 
        devicemodel.contains("nexus one")   || 
        devicemodel.contains("adr6300")   || 
        devicemodel.contains("gt-i5700")   || 
        devicemodel.contains("gt-i5700")   || 
        devicemodel.contains("gt-i5500")   || 
        devicemodel.contains("gt-i5503")   || 
        devicemodel.contains("sph-m910")   || 
        devicemodel.contains("mytouch 3g slide")   || 
        devicemodel.contains("sph-m900")   || 
        devicemodel.contains("t-mobile g1")   || 
        devicemodel.contains("t-mobile g2")   || 
        devicemodel.contains("eris")   || 
        devicemodel.contains("magic")   || 
        devicemodel.contains("motorola_i1")   || 
        devicemodel.contains("sph-m910")   || 
        devicemodel.contains("u8230")   || 
        devicemodel.contains("mb501")   || 
        devicemodel.contains("liberty")   || 
        devicemodel.contains("legend")) {
      return true;
    }    
    
    return false;
  }

  public static boolean is1GhzHTC(String devicemodel) {
    if (devicemodel.contains("desire")  ||
        devicemodel.contains("nexus one")   || 
        devicemodel.contains("desire hd")   || 
        devicemodel.contains("adr6300")) {
      return true;
    }
    
    return false;
  }
}
