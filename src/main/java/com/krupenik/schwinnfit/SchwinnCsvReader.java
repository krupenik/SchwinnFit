package com.krupenik.schwinnfit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SchwinnCsvReader {
  static final String DELIMITER = "\s*,\s*";
  static final String SUMMARY_MARKER = "RIDE SUMMARY";
  static final String STAGE_SUMMARY_MARKER = "STAGE_";
  static final String DATA_MARKER = "RIDE DATA";
  static final Set<String> markers = new HashSet<>(
      Arrays.asList(SUMMARY_MARKER, STAGE_SUMMARY_MARKER, DATA_MARKER));

  Stream<String> lines;

  ActivitySummary summary;
  List<ActivityRecord> records = new ArrayList<>();

  public SchwinnCsvReader(Stream<String> lines) {
    this.lines = lines;
  }

  public ActivitySummary getSummary() {
    return summary;
  }

  public List<ActivityRecord> getRecords() {
    return records;
  }

  public void process() {
    String currentMarker = null;
    List<String> currentBlock = new ArrayList<>();

    for (String line : lines.map(line -> line.trim()).filter(line -> line != null && !line.isEmpty())
        .collect(Collectors.toList())) {
      if (markers.stream().anyMatch(line::startsWith)) {
        if (!currentBlock.isEmpty() && currentMarker != null) {
          processBlock(currentMarker, currentBlock);
        }
        currentBlock = new ArrayList<>();
        currentMarker = line.trim();
      } else {
        currentBlock.add(line);
      }
    }

    if (!currentBlock.isEmpty() && currentMarker != null) {
      processBlock(currentMarker, currentBlock);
    }
  }

  void processBlock(String marker, List<String> lines) {
    switch (marker) {
      case String s when s.startsWith(SUMMARY_MARKER):
        createSummary(lines);
        return;
      case String s when s.startsWith(DATA_MARKER):
        createRecords(lines);
        return;
      case String s when s.startsWith(STAGE_SUMMARY_MARKER):
        return;
      default:
        System.err.println("Unrecognized block marker: " + marker);
        return;
    }
  }

  void createSummary(List<String> lines) {
    float distanceM = 0;
    float elapsedTime = 0;
    int avgPowerWatts = 0;
    int maxPowerWatts = 0;
    short avgCadenceRpm = 0;
    short maxCadenceRpm = 0;
    short avgHeartRateBpm = 0;
    short maxHeartRateBpm = 0;
    int calories = 0;

    for (String line : lines) {
      String[] parts = line.split(DELIMITER);
      switch (parts[0]) {
        case "Total Time" -> elapsedTime = Float.parseFloat(parts[1]) * 60;
        case "Total Distance" -> distanceM = Float.parseFloat(parts[1]) * 1000;
        case "AVG Power" -> avgPowerWatts = Integer.parseInt(parts[1]);
        case "MAX Power" -> maxPowerWatts = Integer.parseInt(parts[1]);
        case "AVG RPM" -> avgCadenceRpm = Short.parseShort(parts[1]);
        case "MAX RPM" -> maxCadenceRpm = Short.parseShort(parts[1]);
        case "AVG HR" -> avgHeartRateBpm = Short.parseShort(parts[1]);
        case "MAX HR" -> maxHeartRateBpm = Short.parseShort(parts[1]);
        case "CAL" -> calories = Integer.parseInt(parts[1]);
      }
    }

    summary = new ActivitySummary(elapsedTime, distanceM, avgPowerWatts, maxPowerWatts, avgCadenceRpm, maxCadenceRpm,
        avgHeartRateBpm, maxHeartRateBpm, calories);
  }

  void createRecords(List<String> lines) {
    for (String line : lines) {
      // skip header
      if (line.equals("Power, RPM, HR, DISTANCE,")) {
        continue;
      }

      String[] parts = line.split(DELIMITER);
      records.add(new ActivityRecord(Integer.parseInt(parts[0]), Short.parseShort(parts[1]), Short.parseShort(parts[2]),
          Float.parseFloat(parts[3]) * 1000));
    }
  }
}
