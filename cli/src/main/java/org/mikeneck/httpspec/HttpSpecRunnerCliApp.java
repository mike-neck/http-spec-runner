package org.mikeneck.httpspec;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

@CommandLine.Command(
    name = "http-spec-runner",
    mixinStandardHelpOptions = true,
    description = {"http-spec-runner is a testing library/tool for REST API using a spec files."})
public class HttpSpecRunnerCliApp implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-f", "file"},
      description = {"http spec file to use for test."})
  File file;

  @CommandLine.Option(
      names = {"-q", "quiet"},
      description = {"runs without any outputs.", "default false"},
      defaultValue = "false")
  boolean quiet = false;

  @CommandLine.Option(
      names = {"-c", "color"},
      description = {
        "displays output in ansi color mode",
        "default false",
        "will be ignored when quiet(-q) is turned on"
      },
      defaultValue = "false")
  boolean color = false;

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @Override
  public Integer call() {
    try {
      File yamlFile = Objects.requireNonNull(this.file, "usage: http-spec -f <file name>");

      PrintWriter output = spec.commandLine().getOut();
      Stdout out = Stdout.withMode(quiet).withColor(color).outputTo(output::println);
      StdoutExtension stdout = new StdoutExtension(out);
      TestCountExtension testCount = new TestCountExtension();
      Extension extension = stdout.merge(testCount);

      HttpSpecRunner httpSpecRunner = HttpSpecRunner.from(yamlFile, extension);
      List<@NotNull VerificationResult> results = httpSpecRunner.runForResult();

      testCount.writeCount(out);
      if (results.stream()
          .flatMap(it -> it.allAssertions().stream())
          .allMatch(HttpResponseAssertion::isSuccess)) {
        return 0;
      } else {
        return 1;
      }
    } catch (Exception e) {
      if (!quiet) {
        e.printStackTrace();
      }
      return 100;
    }
  }

  public static void main(String[] args) {
    CommandLine commandLine = new CommandLine(new HttpSpecRunnerCliApp());
    int exitCode = commandLine.execute(args);
    System.exit(exitCode);
  }
}
