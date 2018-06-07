package com.hortonworks.spark.sql.hive.llap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.DataSourceV2;
import org.apache.spark.sql.sources.v2.ReadSupport;
import org.apache.spark.sql.sources.v2.SessionConfigSupport;
import org.apache.spark.sql.sources.v2.reader.DataSourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/*
 * Driver:
 *   UserCode -> HiveWarehouseConnector -> HiveWarehouseDataSourceReader -> HiveWarehouseDataReaderFactory
 * Task serializer:
 *   HiveWarehouseDataReaderFactory (Driver) -> bytes -> HiveWarehouseDataReaderFactory (Executor task)
 * Executor:
 *   HiveWarehouseDataReaderFactory -> HiveWarehouseDataReader
 */
public class HiveWarehouseConnector implements DataSourceV2, ReadSupport, SessionConfigSupport {

  private static Logger LOG = LoggerFactory.getLogger(HiveWarehouseConnector.class);

  @Override public DataSourceReader createReader(DataSourceOptions options) {
    try {
      return getDataSourceReader(getOptions(options));
    } catch (IOException e) {
      LOG.error("Error creating {}", getClass().getName());
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeException(e);
    }
  }

  @Override public String keyPrefix() {
    return HiveWarehouseSession.HIVE_WAREHOUSE_POSTFIX;
  }

  private static Map<String, String> getOptions(DataSourceOptions options) {
    System.out.println(options.asMap());
    return options.asMap();
  }

  protected DataSourceReader getDataSourceReader(Map<String, String> params) throws IOException {
    return new HiveWarehouseDataSourceReader(params);
  }

}
