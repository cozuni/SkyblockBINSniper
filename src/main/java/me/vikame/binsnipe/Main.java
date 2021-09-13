package me.vikame.binsnipe;

import java.awt.GraphicsEnvironment;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/* A (hopefully) simple to use BIN sniper for Hypixel Skyblock.
 *
 * Heavily inspired by 'csjh' and their SkyblockSniper python script (https://github.com/csjh/SkyblockSniper)
 */
public class Main {

  private static ScheduledExecutorService THREAD_POOL;

  public static void main(String[] args) throws IOException {
    Console console = System.console();
    if (console == null
        && !GraphicsEnvironment.isHeadless()) { // Ensure we have a console window to work with!
      String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString()
          .substring(6);

      StringBuilder allArgs = new StringBuilder();
      for (String str : args) {
        allArgs.append(str).append(' ');
      }

      // I feel the need to leave a comment here explaining this as it *may* look suspicious at a glance.

      /*
       * The above lines of code, alongside this one, start a new instance of the windows command prompt and execute the command below.
       * When the command below is put together, it is 'cmd /c start cmd /k java -jar Dfile.encoding=UTF-8 "${filename}" ${allArgs}'
       * which, when ran, simply runs the .JAR file that was executed (the BIN sniper!) and nothing else.
       *
       * This is done to ensure we have a console window to print output to, and so you can easily stop the program (via the exit button on the console)
       * and is in no way a suspicious piece of code. If you *still* do not trust this, you can simply run the JAR file via the command-line yourself
       * (via 'java -jar <jarFile>') which will not trigger this block of code.
       */
      Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k",
          "java -jar -Dfile.encoding=UTF-8 \"" + filename + "\" " + allArgs.toString().trim()});
    } else {
      System.setErr(System.out);

      File config = new File(System.getProperty("user.dir"), "BINSniper.config");
      if (config.exists()) {
        System.out.println("Loading sniper configuration...");
        try {
          FileReader reader = new FileReader(config);
          Properties properties = new Properties();
          properties.load(reader);

          System.out.println("Configuration:");

          for (Field field : Config.class.getDeclaredFields()) {
            if (!properties.containsKey(field.getName())) {
              continue;
            }

            try {
              String prop = properties.getProperty(field.getName());

              Class<?> type = field.getType();
              if (type == boolean.class || type == Boolean.class) {
                field.setBoolean(null, Boolean.parseBoolean(prop));
              } else if (type == int.class || type == Integer.class) {
                field.setInt(null, Integer.parseInt(prop));
              } else if (type == float.class || type == Float.class) {
                field.set(null, Float.parseFloat(prop));
              } else {
                System.out.println("Could not parse data type for " + field.getName() + ".");
                continue;
              }

              System.out.println(" > " + field.getName() + ": " + prop);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
              System.out.println("Failed to get default config value for '" + field + "'");
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Failed to read configuration data. Using defaults...");
        }
      } else {
        if (!config.createNewFile()) {
          System.out.println(
              "Couldn't create configuration file! You will be forced to use default sniper preferences.");
        } else {
          Properties properties = new Properties();

          for (Field field : Config.class.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
              continue;
            }

            try {
              properties.setProperty(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
              e.printStackTrace();
              System.out.println("Failed to get default config value for '" + field + "'");
            }
          }

          FileWriter writer = new FileWriter(config);
          properties.store(writer, "Skyblock BIN Sniper configuration data");
          writer.close();

          System.out.println("A Configuration file to edit sniper preferences has been created at '"
              + config.getAbsolutePath() + "'");
        }
      }

      THREAD_POOL = Executors.newScheduledThreadPool(Config.POOLED_THREAD_COUNT);

      Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));

      System.out.println();

      new BINSniper();
    }
  }

  public static void printDebug(String line) {
    if (Config.DEBUG) {
      System.out.println(line);
    }
  }

  public static ScheduledExecutorService pool() {
    return THREAD_POOL;
  }

  public static void shutdown() {
    THREAD_POOL.shutdown();
    try {
      if (!THREAD_POOL.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
        System.err.println("Failed to block until thread-pool finished queued tasks.");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void exec(Runnable runnable) {
    THREAD_POOL.submit(runnable);
  }

  public static <T> Future<T> exec(Callable<T> callable) {
    return THREAD_POOL.submit(callable);
  }

  public static void execLater(Runnable runnable, long delay, TimeUnit unit) {
    THREAD_POOL.schedule(runnable, delay, unit);
  }

  public static void schedule(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
    THREAD_POOL.scheduleAtFixedRate(runnable, initialDelay, delay, unit);
  }

  public static <T> void execAll(Collection<Callable<T>> collection) {
    for (Callable<T> callable : collection) {
      THREAD_POOL.submit(callable);
    }
  }

  public static <T> void execAll(Collection<T> collection, Consumer<T> consumer) {
    for (final T t : collection) {
      Callable<T> callable = () -> {
        consumer.accept(t);
        return t;
      };

      THREAD_POOL.submit(callable);
    }
  }

  public static <T> void execAll(Callable<T>[] array) {
    execAll(Arrays.asList(array));
  }

  public static <T> void execAll(T[] array, Consumer<T> consumer) {
    execAll(Arrays.asList(array), consumer);
  }

}
