package android.support.v7.app;

class TwilightCalculator
{
  private static final float ALTIDUTE_CORRECTION_CIVIL_TWILIGHT = -0.10471976F;
  private static final float C1 = 0.0334196F;
  private static final float C2 = 3.49066E-4F;
  private static final float C3 = 5.236E-6F;
  public static final int DAY = 0;
  private static final float DEGREES_TO_RADIANS = 0.017453292F;
  private static final float J0 = 9.0E-4F;
  public static final int NIGHT = 1;
  private static final float OBLIQUITY = 0.4092797F;
  private static final long UTC_2000 = 946728000000L;
  private static TwilightCalculator sInstance;
  public int state;
  public long sunrise;
  public long sunset;
  
  TwilightCalculator() {}
  
  static TwilightCalculator getInstance()
  {
    if (sInstance == null) {
      sInstance = new TwilightCalculator();
    }
    return sInstance;
  }
  
  public void calculateTwilight(long paramLong, double paramDouble1, double paramDouble2)
  {
    float f1 = (float)(paramLong - 946728000000L) / 8.64E7F;
    float f2 = 6.24006F + 0.01720197F * f1;
    double d1 = 3.141592653589793D + (1.796593063D + (f2 + 0.03341960161924362D * Math.sin(f2) + 3.4906598739326E-4D * Math.sin(2.0F * f2) + 5.236000106378924E-6D * Math.sin(3.0F * f2)));
    double d2 = -paramDouble2 / 360.0D;
    double d3 = d2 + (9.0E-4F + (float)Math.round(f1 - 9.0E-4F - d2)) + 0.0053D * Math.sin(f2) + -0.0069D * Math.sin(2.0D * d1);
    double d4 = Math.asin(Math.sin(d1) * Math.sin(0.4092797040939331D));
    double d5 = paramDouble1 * 0.01745329238474369D;
    double d6 = (Math.sin(-0.10471975803375244D) - Math.sin(d5) * Math.sin(d4)) / (Math.cos(d5) * Math.cos(d4));
    if (d6 >= 1.0D)
    {
      this.state = 1;
      this.sunset = -1L;
      this.sunrise = -1L;
      return;
    }
    if (d6 <= -1.0D)
    {
      this.state = 0;
      this.sunset = -1L;
      this.sunrise = -1L;
      return;
    }
    float f3 = (float)(Math.acos(d6) / 6.283185307179586D);
    this.sunset = (946728000000L + Math.round(8.64E7D * (d3 + f3)));
    this.sunrise = (946728000000L + Math.round(8.64E7D * (d3 - f3)));
    if ((this.sunrise < paramLong) && (this.sunset > paramLong))
    {
      this.state = 0;
      return;
    }
    this.state = 1;
  }
}
