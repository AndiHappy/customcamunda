package com.zlz.customcamunda.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

/**
 * Created by zhailz on 17/7/6.
 */
public class PropertiesUtil {

  private  Properties properties = new Properties();

  public PropertiesUtil() {
    File pro = new File("properties");
//		if(!pro.exists()){
//			pro.createNewFile();
//		}
	if (pro.exists()) {
	  properties = loadseries();
	}else{
	  series(properties);
	}
  }

  private void series(Object object) {
    FileOutputStream fileOut = null;
    ObjectOutputStream  out = null;
    try {
      fileOut = new FileOutputStream("properties");
      out = new ObjectOutputStream(fileOut);
      out.writeObject(object);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      va(fileOut, out);

    }

  }

  private Properties loadseries() {
    FileInputStream fileOut = null;
    ObjectInputStream out = null;
    try {
      fileOut = new FileInputStream("properties");
      out = new ObjectInputStream(fileOut);
      Properties value = (Properties) out.readObject();
      return value;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (out != null)
        try {
          out.close();
          fileOut.close();
        } catch (IOException e) {
        }

    }
    return null;
}
  private void va(FileOutputStream fileOut, ObjectOutputStream out) {
    if (out !=null)
      try {
        out.close();
        if(fileOut!=null) fileOut.close();
      } catch (IOException e) {
      }
  }

  public String getPropertyValue(String key) {
    properties = loadseries();
    return properties.getProperty(key);
  }

  public void setPropertiesValue(String key, String value) {
    properties.setProperty(key, value);
    series(properties);
  }
}
