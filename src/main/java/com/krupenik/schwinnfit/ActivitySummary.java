package com.krupenik.schwinnfit;

class ActivitySummary {
  public final float elapsedTime;
  public final float distanceM;
  public final int avgPowerWatts;
  public final int maxPowerWatts;
  public final short avgCadenceRpm;
  public final short maxCadenceRpm;
  public final short avgHeartRateBpm;
  public final short maxHeartRateBpm;
  public final int calories;

  public ActivitySummary(float elapsedTime, float distanceM, int avgPowerWatts, int maxPowerWatts, short avgCadenceRpm,
      short maxCadenceRpm, short avgHeartRateBpm, short maxHeartRateBpm, int calories) {
    this.elapsedTime = elapsedTime;
    this.distanceM = distanceM;
    this.avgPowerWatts = avgPowerWatts;
    this.maxPowerWatts = maxPowerWatts;
    this.avgCadenceRpm = avgCadenceRpm;
    this.maxCadenceRpm = maxCadenceRpm;
    this.avgHeartRateBpm = avgHeartRateBpm;
    this.maxHeartRateBpm = maxHeartRateBpm;
    this.calories = calories;
  }

  @Override
  public String toString() {
    return "ActivitySummary{" + "elapsedTime=" + elapsedTime + ", distanceM=" + distanceM + ", avgPowerWatts="
        + avgPowerWatts + ", maxPowerWatts=" + maxPowerWatts + ", avgCadenceRpm=" + avgCadenceRpm + ", maxCadenceRpm="
        + maxCadenceRpm + ", avgHeartRateBpm=" + avgHeartRateBpm + ", maxHeartRateBpm=" + maxHeartRateBpm
        + ", calories=" + calories + "}";
  }
}
