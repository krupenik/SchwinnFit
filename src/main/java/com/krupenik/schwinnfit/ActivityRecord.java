package com.krupenik.schwinnfit;

class ActivityRecord {
  public final int powerWatts;
  public final short cadenceRpm;
  public final short heartRateBpm;
  public final float distanceM;

  public ActivityRecord(int powerWatts, short cadenceRpm, short heartRateBpm, float distanceM) {
    this.cadenceRpm = cadenceRpm;
    this.powerWatts = powerWatts;
    this.heartRateBpm = heartRateBpm;
    this.distanceM = distanceM;
  }

  @Override
  public String toString() {
    return "ActivityRecord{" + "powerWatts=" + powerWatts + ", cadenceRpm="
        + cadenceRpm + ", heartRateBpm=" + heartRateBpm + ", distanceM=" + distanceM + "}";
  }
}
