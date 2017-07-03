package android.support.transition;

import android.view.View;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TransitionValues
{
  public final Map<String, Object> values = new HashMap();
  public View view;
  
  public TransitionValues() {}
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof TransitionValues)) && (this.view == ((TransitionValues)paramObject).view) && (this.values.equals(((TransitionValues)paramObject).values));
  }
  
  public int hashCode()
  {
    return 31 * this.view.hashCode() + this.values.hashCode();
  }
  
  public String toString()
  {
    String str1 = "TransitionValues@" + Integer.toHexString(hashCode()) + ":\n";
    String str2 = str1 + "    view = " + this.view + "\n";
    String str3 = str2 + "    values:";
    Iterator localIterator = this.values.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str4 = (String)localIterator.next();
      str3 = str3 + "    " + str4 + ": " + this.values.get(str4) + "\n";
    }
    return str3;
  }
}
