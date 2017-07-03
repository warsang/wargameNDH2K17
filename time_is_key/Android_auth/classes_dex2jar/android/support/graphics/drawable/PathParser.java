package android.support.graphics.drawable;

import android.graphics.Path;
import android.util.Log;
import java.util.ArrayList;

class PathParser
{
  private static final String LOGTAG = "PathParser";
  
  PathParser() {}
  
  private static void addNode(ArrayList<PathDataNode> paramArrayList, char paramChar, float[] paramArrayOfFloat)
  {
    paramArrayList.add(new PathDataNode(paramChar, paramArrayOfFloat));
  }
  
  public static boolean canMorph(PathDataNode[] paramArrayOfPathDataNode1, PathDataNode[] paramArrayOfPathDataNode2)
  {
    if ((paramArrayOfPathDataNode1 == null) || (paramArrayOfPathDataNode2 == null)) {}
    while (paramArrayOfPathDataNode1.length != paramArrayOfPathDataNode2.length) {
      return false;
    }
    for (int i = 0;; i++)
    {
      if (i >= paramArrayOfPathDataNode1.length) {
        break label63;
      }
      if ((paramArrayOfPathDataNode1[i].type != paramArrayOfPathDataNode2[i].type) || (paramArrayOfPathDataNode1[i].params.length != paramArrayOfPathDataNode2[i].params.length)) {
        break;
      }
    }
    label63:
    return true;
  }
  
  static float[] copyOfRange(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException();
    }
    int i = paramArrayOfFloat.length;
    if ((paramInt1 < 0) || (paramInt1 > i)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int j = paramInt2 - paramInt1;
    int k = Math.min(j, i - paramInt1);
    float[] arrayOfFloat = new float[j];
    System.arraycopy(paramArrayOfFloat, paramInt1, arrayOfFloat, 0, k);
    return arrayOfFloat;
  }
  
  public static PathDataNode[] createNodesFromPathData(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = 0;
    int j = 1;
    ArrayList localArrayList = new ArrayList();
    while (j < paramString.length())
    {
      int k = nextStart(paramString, j);
      String str = paramString.substring(i, k).trim();
      if (str.length() > 0)
      {
        float[] arrayOfFloat = getFloats(str);
        addNode(localArrayList, str.charAt(0), arrayOfFloat);
      }
      i = k;
      j = k + 1;
    }
    if ((j - i == 1) && (i < paramString.length())) {
      addNode(localArrayList, paramString.charAt(i), new float[0]);
    }
    return (PathDataNode[])localArrayList.toArray(new PathDataNode[localArrayList.size()]);
  }
  
  public static Path createPathFromPathData(String paramString)
  {
    Path localPath = new Path();
    PathDataNode[] arrayOfPathDataNode = createNodesFromPathData(paramString);
    if (arrayOfPathDataNode != null) {
      try
      {
        PathDataNode.nodesToPath(arrayOfPathDataNode, localPath);
        return localPath;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw new RuntimeException("Error in parsing " + paramString, localRuntimeException);
      }
    }
    return null;
  }
  
  public static PathDataNode[] deepCopyNodes(PathDataNode[] paramArrayOfPathDataNode)
  {
    PathDataNode[] arrayOfPathDataNode;
    if (paramArrayOfPathDataNode == null) {
      arrayOfPathDataNode = null;
    }
    for (;;)
    {
      return arrayOfPathDataNode;
      arrayOfPathDataNode = new PathDataNode[paramArrayOfPathDataNode.length];
      for (int i = 0; i < paramArrayOfPathDataNode.length; i++) {
        arrayOfPathDataNode[i] = new PathDataNode(paramArrayOfPathDataNode[i]);
      }
    }
  }
  
  private static void extract(String paramString, int paramInt, ExtractFloatResult paramExtractFloatResult)
  {
    int i = paramInt;
    int j = 0;
    paramExtractFloatResult.mEndWithNegOrDot = false;
    int k = 0;
    int m = 0;
    for (;;)
    {
      int n;
      if (i < paramString.length())
      {
        n = m;
        int i1 = paramString.charAt(i);
        m = 0;
        switch (i1)
        {
        }
      }
      while (j != 0)
      {
        paramExtractFloatResult.mEndPosition = i;
        return;
        j = 1;
        m = 0;
        continue;
        m = 0;
        if (i != paramInt)
        {
          m = 0;
          if (n == 0)
          {
            j = 1;
            paramExtractFloatResult.mEndWithNegOrDot = true;
            m = 0;
            continue;
            if (k == 0)
            {
              k = 1;
              m = 0;
            }
            else
            {
              j = 1;
              paramExtractFloatResult.mEndWithNegOrDot = true;
              m = 0;
              continue;
              m = 1;
            }
          }
        }
      }
      i++;
    }
  }
  
  private static float[] getFloats(String paramString)
  {
    int i = 1;
    int j;
    if (paramString.charAt(0) == 'z')
    {
      j = i;
      if (paramString.charAt(0) != 'Z') {
        break label39;
      }
    }
    for (;;)
    {
      if ((j | i) == 0) {
        break label44;
      }
      return new float[0];
      j = 0;
      break;
      label39:
      i = 0;
    }
    for (;;)
    {
      label44:
      int i1;
      try
      {
        float[] arrayOfFloat1 = new float[paramString.length()];
        k = 1;
        ExtractFloatResult localExtractFloatResult = new ExtractFloatResult();
        int m = paramString.length();
        n = 0;
        if (k < m)
        {
          extract(paramString, k, localExtractFloatResult);
          i1 = localExtractFloatResult.mEndPosition;
          if (k < i1)
          {
            i2 = n + 1;
            arrayOfFloat1[n] = Float.parseFloat(paramString.substring(k, i1));
            if (!localExtractFloatResult.mEndWithNegOrDot) {
              break label197;
            }
            k = i1;
            n = i2;
          }
        }
        else
        {
          float[] arrayOfFloat2 = copyOfRange(arrayOfFloat1, 0, n);
          return arrayOfFloat2;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new RuntimeException("error in parsing \"" + paramString + "\"", localNumberFormatException);
      }
      int i2 = n;
      continue;
      label197:
      int k = i1 + 1;
      int n = i2;
    }
  }
  
  private static int nextStart(String paramString, int paramInt)
  {
    for (;;)
    {
      if (paramInt < paramString.length())
      {
        int i = paramString.charAt(paramInt);
        if ((((i - 65) * (i - 90) > 0) && ((i - 97) * (i - 122) > 0)) || (i == 101) || (i == 69)) {}
      }
      else
      {
        return paramInt;
      }
      paramInt++;
    }
  }
  
  public static void updateNodes(PathDataNode[] paramArrayOfPathDataNode1, PathDataNode[] paramArrayOfPathDataNode2)
  {
    for (int i = 0; i < paramArrayOfPathDataNode2.length; i++)
    {
      paramArrayOfPathDataNode1[i].type = paramArrayOfPathDataNode2[i].type;
      for (int j = 0; j < paramArrayOfPathDataNode2[i].params.length; j++) {
        paramArrayOfPathDataNode1[i].params[j] = paramArrayOfPathDataNode2[i].params[j];
      }
    }
  }
  
  private static class ExtractFloatResult
  {
    int mEndPosition;
    boolean mEndWithNegOrDot;
    
    ExtractFloatResult() {}
  }
  
  public static class PathDataNode
  {
    float[] params;
    char type;
    
    PathDataNode(char paramChar, float[] paramArrayOfFloat)
    {
      this.type = paramChar;
      this.params = paramArrayOfFloat;
    }
    
    PathDataNode(PathDataNode paramPathDataNode)
    {
      this.type = paramPathDataNode.type;
      this.params = PathParser.copyOfRange(paramPathDataNode.params, 0, paramPathDataNode.params.length);
    }
    
    private static void addCommand(Path paramPath, float[] paramArrayOfFloat1, char paramChar1, char paramChar2, float[] paramArrayOfFloat2)
    {
      int i = 2;
      float f1 = paramArrayOfFloat1[0];
      float f2 = paramArrayOfFloat1[1];
      float f3 = paramArrayOfFloat1[2];
      float f4 = paramArrayOfFloat1[3];
      float f5 = paramArrayOfFloat1[4];
      float f6 = paramArrayOfFloat1[5];
      int j;
      switch (paramChar2)
      {
      default: 
        j = 0;
        int k = paramArrayOfFloat2.length;
        if (j < k) {
          switch (paramChar2)
          {
          }
        }
        break;
      case 'Z': 
      case 'z': 
      case 'L': 
      case 'M': 
      case 'T': 
      case 'l': 
      case 'm': 
      case 't': 
      case 'H': 
      case 'V': 
      case 'h': 
      case 'v': 
      case 'C': 
      case 'c': 
      case 'Q': 
      case 'S': 
      case 'q': 
      case 's': 
      case 'A': 
      case 'a': 
        for (;;)
        {
          label207:
          paramChar1 = paramChar2;
          j += i;
          break label207;
          paramPath.close();
          f1 = f5;
          f2 = f6;
          f3 = f5;
          f4 = f6;
          paramPath.moveTo(f1, f2);
          break;
          i = 2;
          break;
          i = 1;
          break;
          i = 6;
          break;
          i = 4;
          break;
          i = 7;
          break;
          f1 += paramArrayOfFloat2[(j + 0)];
          f2 += paramArrayOfFloat2[(j + 1)];
          if (j > 0)
          {
            paramPath.rLineTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
          }
          else
          {
            paramPath.rMoveTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
            f5 = f1;
            f6 = f2;
            continue;
            f1 = paramArrayOfFloat2[(j + 0)];
            f2 = paramArrayOfFloat2[(j + 1)];
            if (j > 0)
            {
              paramPath.lineTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
            }
            else
            {
              paramPath.moveTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
              f5 = f1;
              f6 = f2;
              continue;
              paramPath.rLineTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
              f1 += paramArrayOfFloat2[(j + 0)];
              f2 += paramArrayOfFloat2[(j + 1)];
              continue;
              paramPath.lineTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
              f1 = paramArrayOfFloat2[(j + 0)];
              f2 = paramArrayOfFloat2[(j + 1)];
              continue;
              paramPath.rLineTo(paramArrayOfFloat2[(j + 0)], 0.0F);
              f1 += paramArrayOfFloat2[(j + 0)];
              continue;
              paramPath.lineTo(paramArrayOfFloat2[(j + 0)], f2);
              f1 = paramArrayOfFloat2[(j + 0)];
              continue;
              paramPath.rLineTo(0.0F, paramArrayOfFloat2[(j + 0)]);
              f2 += paramArrayOfFloat2[(j + 0)];
              continue;
              float f25 = paramArrayOfFloat2[(j + 0)];
              paramPath.lineTo(f1, f25);
              f2 = paramArrayOfFloat2[(j + 0)];
              continue;
              paramPath.rCubicTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)], paramArrayOfFloat2[(j + 2)], paramArrayOfFloat2[(j + 3)], paramArrayOfFloat2[(j + 4)], paramArrayOfFloat2[(j + 5)]);
              f3 = f1 + paramArrayOfFloat2[(j + 2)];
              f4 = f2 + paramArrayOfFloat2[(j + 3)];
              f1 += paramArrayOfFloat2[(j + 4)];
              f2 += paramArrayOfFloat2[(j + 5)];
              continue;
              paramPath.cubicTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)], paramArrayOfFloat2[(j + 2)], paramArrayOfFloat2[(j + 3)], paramArrayOfFloat2[(j + 4)], paramArrayOfFloat2[(j + 5)]);
              f1 = paramArrayOfFloat2[(j + 4)];
              f2 = paramArrayOfFloat2[(j + 5)];
              f3 = paramArrayOfFloat2[(j + 2)];
              f4 = paramArrayOfFloat2[(j + 3)];
              continue;
              float f23;
              float f24;
              if ((paramChar1 != 'c') && (paramChar1 != 's') && (paramChar1 != 'C'))
              {
                int n = paramChar1;
                f23 = 0.0F;
                f24 = 0.0F;
                if (n != 83) {}
              }
              else
              {
                f23 = f1 - f3;
                f24 = f2 - f4;
              }
              paramPath.rCubicTo(f23, f24, paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)], paramArrayOfFloat2[(j + 2)], paramArrayOfFloat2[(j + 3)]);
              f3 = f1 + paramArrayOfFloat2[(j + 0)];
              f4 = f2 + paramArrayOfFloat2[(j + 1)];
              f1 += paramArrayOfFloat2[(j + 2)];
              f2 += paramArrayOfFloat2[(j + 3)];
              continue;
              float f21 = f1;
              float f22 = f2;
              if ((paramChar1 == 'c') || (paramChar1 == 's') || (paramChar1 == 'C') || (paramChar1 == 'S'))
              {
                f21 = 2.0F * f1 - f3;
                f22 = 2.0F * f2 - f4;
              }
              paramPath.cubicTo(f21, f22, paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)], paramArrayOfFloat2[(j + 2)], paramArrayOfFloat2[(j + 3)]);
              f3 = paramArrayOfFloat2[(j + 0)];
              f4 = paramArrayOfFloat2[(j + 1)];
              f1 = paramArrayOfFloat2[(j + 2)];
              f2 = paramArrayOfFloat2[(j + 3)];
              continue;
              paramPath.rQuadTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)], paramArrayOfFloat2[(j + 2)], paramArrayOfFloat2[(j + 3)]);
              f3 = f1 + paramArrayOfFloat2[(j + 0)];
              f4 = f2 + paramArrayOfFloat2[(j + 1)];
              f1 += paramArrayOfFloat2[(j + 2)];
              f2 += paramArrayOfFloat2[(j + 3)];
              continue;
              paramPath.quadTo(paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)], paramArrayOfFloat2[(j + 2)], paramArrayOfFloat2[(j + 3)]);
              f3 = paramArrayOfFloat2[(j + 0)];
              f4 = paramArrayOfFloat2[(j + 1)];
              f1 = paramArrayOfFloat2[(j + 2)];
              f2 = paramArrayOfFloat2[(j + 3)];
              continue;
              float f19;
              float f20;
              if ((paramChar1 != 'q') && (paramChar1 != 't') && (paramChar1 != 'Q'))
              {
                int m = paramChar1;
                f19 = 0.0F;
                f20 = 0.0F;
                if (m != 84) {}
              }
              else
              {
                f19 = f1 - f3;
                f20 = f2 - f4;
              }
              paramPath.rQuadTo(f19, f20, paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
              f3 = f1 + f19;
              f4 = f2 + f20;
              f1 += paramArrayOfFloat2[(j + 0)];
              f2 += paramArrayOfFloat2[(j + 1)];
              continue;
              float f17 = f1;
              float f18 = f2;
              if ((paramChar1 == 'q') || (paramChar1 == 't') || (paramChar1 == 'Q') || (paramChar1 == 'T'))
              {
                f17 = 2.0F * f1 - f3;
                f18 = 2.0F * f2 - f4;
              }
              paramPath.quadTo(f17, f18, paramArrayOfFloat2[(j + 0)], paramArrayOfFloat2[(j + 1)]);
              f3 = f17;
              f4 = f18;
              f1 = paramArrayOfFloat2[(j + 0)];
              f2 = paramArrayOfFloat2[(j + 1)];
            }
          }
        }
        float f12 = f1 + paramArrayOfFloat2[(j + 5)];
        float f13 = f2 + paramArrayOfFloat2[(j + 6)];
        float f14 = paramArrayOfFloat2[(j + 0)];
        float f15 = paramArrayOfFloat2[(j + 1)];
        float f16 = paramArrayOfFloat2[(j + 2)];
        boolean bool3;
        if (paramArrayOfFloat2[(j + 3)] != 0.0F)
        {
          bool3 = true;
          label1670:
          if (paramArrayOfFloat2[(j + 4)] == 0.0F) {
            break label1749;
          }
        }
        label1749:
        for (boolean bool4 = true;; bool4 = false)
        {
          drawArc(paramPath, f1, f2, f12, f13, f14, f15, f16, bool3, bool4);
          f1 += paramArrayOfFloat2[(j + 5)];
          f2 += paramArrayOfFloat2[(j + 6)];
          f3 = f1;
          f4 = f2;
          break;
          bool3 = false;
          break label1670;
        }
        float f7 = paramArrayOfFloat2[(j + 5)];
        float f8 = paramArrayOfFloat2[(j + 6)];
        float f9 = paramArrayOfFloat2[(j + 0)];
        float f10 = paramArrayOfFloat2[(j + 1)];
        float f11 = paramArrayOfFloat2[(j + 2)];
        boolean bool1;
        if (paramArrayOfFloat2[(j + 3)] != 0.0F)
        {
          bool1 = true;
          label1816:
          if (paramArrayOfFloat2[(j + 4)] == 0.0F) {
            break label1889;
          }
        }
        label1889:
        for (boolean bool2 = true;; bool2 = false)
        {
          drawArc(paramPath, f1, f2, f7, f8, f9, f10, f11, bool1, bool2);
          f1 = paramArrayOfFloat2[(j + 5)];
          f2 = paramArrayOfFloat2[(j + 6)];
          f3 = f1;
          f4 = f2;
          break;
          bool1 = false;
          break label1816;
        }
      }
      paramArrayOfFloat1[0] = f1;
      paramArrayOfFloat1[1] = f2;
      paramArrayOfFloat1[2] = f3;
      paramArrayOfFloat1[3] = f4;
      paramArrayOfFloat1[4] = f5;
      paramArrayOfFloat1[5] = f6;
    }
    
    private static void arcToBezier(Path paramPath, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9)
    {
      int i = (int)Math.ceil(Math.abs(4.0D * paramDouble9 / 3.141592653589793D));
      double d1 = paramDouble8;
      double d2 = Math.cos(paramDouble7);
      double d3 = Math.sin(paramDouble7);
      double d4 = Math.cos(d1);
      double d5 = Math.sin(d1);
      double d6 = d5 * (d2 * -paramDouble3) - d4 * (paramDouble4 * d3);
      double d7 = d5 * (d3 * -paramDouble3) + d4 * (paramDouble4 * d2);
      double d8 = paramDouble9 / i;
      for (int j = 0; j < i; j++)
      {
        double d9 = d1 + d8;
        double d10 = Math.sin(d9);
        double d11 = Math.cos(d9);
        double d12 = paramDouble1 + d11 * (paramDouble3 * d2) - d10 * (paramDouble4 * d3);
        double d13 = paramDouble2 + d11 * (paramDouble3 * d3) + d10 * (paramDouble4 * d2);
        double d14 = d10 * (d2 * -paramDouble3) - d11 * (paramDouble4 * d3);
        double d15 = d10 * (d3 * -paramDouble3) + d11 * (paramDouble4 * d2);
        double d16 = Math.tan((d9 - d1) / 2.0D);
        double d17 = Math.sin(d9 - d1) * (Math.sqrt(4.0D + d16 * (3.0D * d16)) - 1.0D) / 3.0D;
        double d18 = paramDouble5 + d17 * d6;
        double d19 = paramDouble6 + d17 * d7;
        double d20 = d12 - d17 * d14;
        double d21 = d13 - d17 * d15;
        paramPath.rLineTo(0.0F, 0.0F);
        paramPath.cubicTo((float)d18, (float)d19, (float)d20, (float)d21, (float)d12, (float)d13);
        d1 = d9;
        paramDouble5 = d12;
        paramDouble6 = d13;
        d6 = d14;
        d7 = d15;
      }
    }
    
    private static void drawArc(Path paramPath, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, boolean paramBoolean1, boolean paramBoolean2)
    {
      double d1 = Math.toRadians(paramFloat7);
      double d2 = Math.cos(d1);
      double d3 = Math.sin(d1);
      double d4 = (d2 * paramFloat1 + d3 * paramFloat2) / paramFloat5;
      double d5 = (d3 * -paramFloat1 + d2 * paramFloat2) / paramFloat6;
      double d6 = (d2 * paramFloat3 + d3 * paramFloat4) / paramFloat5;
      double d7 = (d3 * -paramFloat3 + d2 * paramFloat4) / paramFloat6;
      double d8 = d4 - d6;
      double d9 = d5 - d7;
      double d10 = (d4 + d6) / 2.0D;
      double d11 = (d5 + d7) / 2.0D;
      double d12 = d8 * d8 + d9 * d9;
      if (d12 == 0.0D)
      {
        Log.w("PathParser", " Points are coincident");
        return;
      }
      double d13 = 1.0D / d12 - 0.25D;
      if (d13 < 0.0D)
      {
        Log.w("PathParser", "Points are too far apart " + d12);
        float f = (float)(Math.sqrt(d12) / 1.99999D);
        drawArc(paramPath, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5 * f, paramFloat6 * f, paramFloat7, paramBoolean1, paramBoolean2);
        return;
      }
      double d14 = Math.sqrt(d13);
      double d15 = d14 * d8;
      double d16 = d14 * d9;
      double d17;
      double d18;
      double d19;
      double d20;
      boolean bool;
      if (paramBoolean1 == paramBoolean2)
      {
        d17 = d10 - d16;
        d18 = d11 + d15;
        d19 = Math.atan2(d5 - d18, d4 - d17);
        d20 = Math.atan2(d7 - d18, d6 - d17) - d19;
        if (d20 < 0.0D) {
          break label423;
        }
        bool = true;
        label325:
        if (paramBoolean2 != bool) {
          if (d20 <= 0.0D) {
            break label429;
          }
        }
      }
      label423:
      label429:
      for (d20 -= 6.283185307179586D;; d20 += 6.283185307179586D)
      {
        double d21 = d17 * paramFloat5;
        double d22 = d18 * paramFloat6;
        arcToBezier(paramPath, d21 * d2 - d22 * d3, d21 * d3 + d22 * d2, paramFloat5, paramFloat6, paramFloat1, paramFloat2, d1, d19, d20);
        return;
        d17 = d10 + d16;
        d18 = d11 - d15;
        break;
        bool = false;
        break label325;
      }
    }
    
    public static void nodesToPath(PathDataNode[] paramArrayOfPathDataNode, Path paramPath)
    {
      float[] arrayOfFloat = new float[6];
      char c = 'm';
      for (int i = 0; i < paramArrayOfPathDataNode.length; i++)
      {
        addCommand(paramPath, arrayOfFloat, c, paramArrayOfPathDataNode[i].type, paramArrayOfPathDataNode[i].params);
        c = paramArrayOfPathDataNode[i].type;
      }
    }
    
    public void interpolatePathDataNode(PathDataNode paramPathDataNode1, PathDataNode paramPathDataNode2, float paramFloat)
    {
      for (int i = 0; i < paramPathDataNode1.params.length; i++) {
        this.params[i] = (paramPathDataNode1.params[i] * (1.0F - paramFloat) + paramFloat * paramPathDataNode2.params[i]);
      }
    }
  }
}
