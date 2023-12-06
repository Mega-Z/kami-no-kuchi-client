package com.megaz.knk.exception;

public class MetaDataQueryException extends RuntimeException{
    public MetaDataQueryException(String tableName) {
        super(tableName + "查询失败，元数据需要更新");
    }
}
