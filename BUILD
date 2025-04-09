load("@rules_java//java:defs.bzl", "java_binary")

package(default_visibility = ["//visibility:public"])

java_library(
  name = "activity_summary",
  srcs = ["src/main/java/com/krupenik/schwinnfit/ActivitySummary.java"],
  deps = [],
)

java_library(
  name = "activity_record",
  srcs = ["src/main/java/com/krupenik/schwinnfit/ActivityRecord.java"],
  deps = [],
)

java_library(
  name = "schwinn_csv_reader",
  srcs = ["src/main/java/com/krupenik/schwinnfit/SchwinnCsvReader.java"],
  deps = [":activity_record", ":activity_summary"],
)

java_import(
  name = "garmin_fit_sdk",
  jars = ["lib/fit.jar"],
)

java_binary(
    name = "schwinn_csv_to_fit",
    main_class = "com.krupenik.schwinnfit.SchwinnCsvToFit",
    srcs = ["src/main/java/com/krupenik/schwinnfit/SchwinnCsvToFit.java"],
    deps = [":activity_record", ":activity_summary", ":schwinn_csv_reader", ":garmin_fit_sdk"],
)
