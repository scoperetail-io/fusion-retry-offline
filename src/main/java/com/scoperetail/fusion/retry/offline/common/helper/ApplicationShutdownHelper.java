package com.scoperetail.fusion.retry.offline.common.helper;

/*-
 * *****
 * fusion-retry-offline
 * -----
 * Copyright (C) 2018 - 2021 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====
 */

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationShutdownHelper {
  private final ApplicationContext applicationContext;
  private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

  public synchronized void shutDownApplication() {
    if (!isShuttingDown.get()) {
      isShuttingDown.set(true);
      log.info("Application shut down initiated");
      log.info("Closing spring context");
      final int exitCode =
          SpringApplication.exit(
              applicationContext,
              new ExitCodeGenerator() {
                @Override
                public int getExitCode() {
                  // no errors
                  return 0;
                }
              });
      log.info("Spring context closed.Exiting application...");
      System.exit(exitCode);
    }
  }
}
