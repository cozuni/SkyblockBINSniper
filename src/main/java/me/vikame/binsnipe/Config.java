package me.vikame.binsnipe;

public class Config {

  /* Whether to add the "Accept-Encoding: gzip" header to API requests.
   * I would highly recommend against disabling this as it greatly reduces bandwith usage when enabled.
   */
  public static final boolean USE_GZIP_COMPRESSION_ON_API_REQUESTS = true;

  /* Whether to ensure all API requests do not return a cached response.
   * This employs many tactics to ensure that we do not get a cached response,
   * such as the use of the "Pragma: no-cache" and "Cache-Control: no-cache" headers,
   * alongside a cache-buster parameter within the API request URL.
   *
   * I would recommend keeping this disabled, but if you do not care about your bandwith usage you
   * *may* see a speed-up from enabling this setting.
   */
  public static final boolean FORCE_NO_CACHE_API_REQUESTS = false;

  // Whether to ignore furniture items, as these are commonly market-manipulated.
  public static final boolean IGNORE_FURNITURE = true;

  // Whether to ignore cosmetic items, as these are commonly market-manipulated.
  public static final boolean IGNORE_COSMETICS = true;

  // Whether to treat all "dungeonizable" items as if they were starless.
  public static final boolean STRIP_STARS = false;

  // The minimum number of the same item that must be on the market to be considered for a flip.
  public static final int MIN_ITEMS_ON_MARKET = 2;

  // The maximum price you are willing to pay for an item to flip.
  public static final int MAX_BIN_PRICE = Integer.MAX_VALUE;

  // The minimum price the item to be flipped must be able to sell for.
  public static final int MIN_BIN_PRICE = 1_000_000;

  // The amount of profit required to log a flip. When checking this, we check if *either* the percentage profit gain or raw profit amount are above their respective threshold.
  public static final float MIN_PROFIT_PERCENTAGE = 110.0f;
  public static final int MIN_PROFIT_AMOUNT = 1_000_000;

  // The number of segments to a console-based loading bar used when displaying current BIN snipe progress.
  public static final int LOADING_BAR_SEGMENTS = 40;

}
