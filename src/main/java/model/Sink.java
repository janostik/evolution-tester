package model;

import java.util.Map;

/**
 * Created by jakub on 29/10/15.
 */
public interface Sink {

    void addRecord(Map<String, Object> record);

    void flush();
}
