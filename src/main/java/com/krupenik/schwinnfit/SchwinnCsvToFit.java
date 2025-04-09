package com.krupenik.schwinnfit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.garmin.fit.ActivityMesg;
import com.garmin.fit.BufferEncoder;
import com.garmin.fit.DateTime;
import com.garmin.fit.File;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.Fit;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.Mesg;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.SubSport;

public class SchwinnCsvToFit {
  static final long SECONDS = 3;
  static final long FIT_EPOCH_S = 631065600;

  public static void main(String[] args) {
    DateTime startTime = null;

    if (0 == args.length) {
      System.err.println("Missing required argument <ISO_8601_Instant_String>");
      printUsageAndExit();
    }

    try {
      startTime = new DateTime(Instant.parse(args[0]).getEpochSecond() - FIT_EPOCH_S);
    } catch (DateTimeParseException e) {
      System.err.println("Invalid start time format '" + args[0] + "'. " + e.getMessage());
      printUsageAndExit();
    }

    SchwinnCsvReader reader = new SchwinnCsvReader(new BufferedReader(new InputStreamReader(System.in)).lines());
    reader.process();

    try {
      System.out.write(encodeActivity(startTime, createActivity(startTime, reader.getSummary(), reader.getRecords())));
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  private static void printUsageAndExit() {
    System.err.println("\nUsage: schwinn_csv_to_fit <ISO_8601_Instant_String>");
    System.exit(1);
  }

  private static List<Mesg> createActivity(DateTime startTime, ActivitySummary summary, List<ActivityRecord> records) {
    List<Mesg> messages = new ArrayList<>();
    DateTime endTime = new DateTime(startTime);
    endTime.add(summary.elapsedTime);

    DateTime timestamp = new DateTime(startTime);
    DateTime increment = new DateTime(SECONDS);

    for (ActivityRecord rec : records) {
      RecordMesg recordMesg = new RecordMesg();
      recordMesg.setTimestamp(timestamp);
      recordMesg.setDistance(rec.distanceM);
      recordMesg.setHeartRate(rec.heartRateBpm);
      recordMesg.setCadence(rec.cadenceRpm);
      recordMesg.setPower(rec.powerWatts);

      messages.add(recordMesg);
      timestamp.add(increment);
    }

    SessionMesg sessionMesg = new SessionMesg();
    sessionMesg.setMessageIndex(0);
    sessionMesg.setStartTime(startTime);
    sessionMesg.setTimestamp(endTime);
    sessionMesg.setTotalElapsedTime(summary.elapsedTime);
    sessionMesg.setTotalTimerTime(summary.elapsedTime);
    sessionMesg.setSport(Sport.CYCLING);
    sessionMesg.setSubSport(SubSport.INDOOR_CYCLING);
    sessionMesg.setFirstLapIndex(0);
    sessionMesg.setNumLaps(1);
    sessionMesg.setTotalDistance(summary.distanceM);
    sessionMesg.setAvgPower(summary.avgPowerWatts);
    sessionMesg.setMaxPower(summary.maxPowerWatts);
    sessionMesg.setAvgCadence(summary.avgCadenceRpm);
    sessionMesg.setMaxCadence(summary.maxCadenceRpm);
    sessionMesg.setAvgHeartRate(summary.avgHeartRateBpm);
    sessionMesg.setMaxHeartRate(summary.maxHeartRateBpm);
    sessionMesg.setTotalCalories(summary.calories);
    messages.add(sessionMesg);

    LapMesg lapMesg = new LapMesg();
    lapMesg.setMessageIndex(0);
    lapMesg.setStartTime(startTime);
    lapMesg.setTimestamp(endTime);
    lapMesg.setTotalElapsedTime(summary.elapsedTime);
    lapMesg.setTotalTimerTime(summary.elapsedTime);
    messages.add(lapMesg);

    ActivityMesg activityMesg = new ActivityMesg();
    activityMesg.setNumSessions(1);
    activityMesg.setTotalTimerTime(summary.elapsedTime);
    activityMesg.setTimestamp(endTime);
    TimeZone timeZone = TimeZone.getTimeZone("Europe/Warsaw");
    activityMesg.setLocalTimestamp(
        endTime.getTimestamp() + timeZone.getOffset((endTime.getTimestamp() + FIT_EPOCH_S) * 1000) / 1000);
    messages.add(activityMesg);

    return messages;
  }

  private static byte[] encodeActivity(DateTime startTime, List<Mesg> messages) {
    BufferEncoder encoder = new BufferEncoder(Fit.ProtocolVersion.V2_0);

    FileIdMesg fileIdMesg = new FileIdMesg();
    fileIdMesg.setType(File.ACTIVITY);
    fileIdMesg.setManufacturer(Manufacturer.DEVELOPMENT);
    fileIdMesg.setProduct(0);
    fileIdMesg.setTimeCreated(startTime);

    encoder.write(fileIdMesg);
    encoder.write(messages);

    return encoder.close();
  }
}
