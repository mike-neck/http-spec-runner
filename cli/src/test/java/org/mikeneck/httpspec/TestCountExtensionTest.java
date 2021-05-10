package org.mikeneck.httpspec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mikeneck.httpspec.StdoutExtensionTest.verificationResult;
import static org.mikeneck.httpspec.impl.HttpResponseAssertionFactory.failure;
import static org.mikeneck.httpspec.impl.HttpResponseAssertionFactory.success;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class TestCountExtensionTest {

  @Test
  void singleSuccess() {
    TestCountExtension extension = new TestCountExtension();

    VerificationResult result = verificationResult("single-success", success("http status", 200));

    extension.afterEachSpec(result);

    assertThat(extension.isSuccess()).isTrue();
  }

  @Test
  void singleFailure() {
    TestCountExtension extension = new TestCountExtension();

    VerificationResult result =
        verificationResult("single-success", failure("http status", 200, 400));

    extension.afterEachSpec(result);

    assertThat(extension.isSuccess()).isFalse();
  }

  @Test
  void bothCases() {
    TestCountExtension extension = new TestCountExtension();

    VerificationResult result =
        verificationResult(
            "single-failure", success("http status", 200), failure("http status", 200, 400));

    extension.afterEachSpec(result);

    assertThat(extension.isSuccess()).isFalse();
  }

  @Test
  void empty() {
    TestCountExtension extension = new TestCountExtension();

    VerificationResult result = verificationResult("empty-case");

    extension.afterEachSpec(result);

    assertThat(extension.isSuccess()).isTrue();
  }

  @Test
  void writingCountSuccessCase() {
    List<String> success = new ArrayList<>();
    List<String> failure = new ArrayList<>();
    List<String> normal = new ArrayList<>();
    Stdout stdout = stdout(success::add, failure::add, normal::add);

    TestCountExtension extension = new TestCountExtension();

    VerificationResult result =
        verificationResult(
            "success",
            success("http status1", 200),
            success("http status2", 200),
            success("http status3", 200));

    extension.afterEachSpec(result);
    extension.writeCount(stdout);

    assertAll(
        () -> assertThat(success).hasSize(4),
        () -> assertThat(failure).hasSize(0),
        () -> assertThat(normal).hasSize(1));
  }

  @Test
  void writingCountFailureCase() {
    List<String> success = new ArrayList<>();
    List<String> failure = new ArrayList<>();
    List<String> normal = new ArrayList<>();
    Stdout stdout = stdout(success::add, failure::add, normal::add);

    TestCountExtension extension = new TestCountExtension();

    VerificationResult failed =
        verificationResult(
            "failed",
            success("http status success", 200),
            failure("http status fail1", 200, 400),
            failure("http status fail2", 200, 400));
    VerificationResult passed = verificationResult("passed", success("http status", 200));

    extension.afterEachSpec(failed);
    extension.afterEachSpec(failed);
    extension.afterEachSpec(passed);
    extension.writeCount(stdout);

    assertAll(
        () -> assertThat(success).hasSize(0),
        () ->
            assertThat(failure)
                .hasSize(3)
                .anySatisfy(line -> assertThat(line).contains("failed-spec", "2"))
                .anySatisfy(line -> assertThat(line).contains("failed-assertions", "4")),
        () -> assertThat(normal).hasSize(2));
  }

  static Stdout stdout(
      Consumer<? super String> success,
      Consumer<? super String> failure,
      Consumer<? super String> normal) {
    return new Stdout() {
      @Override
      public void success(@NotNull String line) {
        success.accept(line);
      }

      @Override
      public void failure(@NotNull String line) {
        failure.accept(line);
      }

      @Override
      public void normal(@NotNull String line) {
        normal.accept(line);
      }
    };
  }
}
