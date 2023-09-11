package it.pagopa.qi.alertmanagement.utils;

import it.pagopa.generated.qi.alerts.v1.dto.AlertInfoDto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Alert webhook parser
 */
public class AlertParser {

    private interface AlertMappingKey {

        String getKey();
    }

    /**
     * Alert.labels key enumeration
     */
    public enum LabelsKey implements AlertMappingKey {
        OWNER("owner"),
        CODE("code");

        private final String key;

        LabelsKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }


    /**
     * Alert.values key enumeration
     */
    public enum NumericValuesKey implements AlertMappingKey {
        VALUE("value"),
        THRESHOLD("threshold");

        private final String key;

        NumericValuesKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private final AlertInfoDto alertInfoDto;
    private final Map<String, String> labels;
    private final Map<String, Double> values;

    /**
     * Constructor
     *
     * @param alertInfoDto the alertInfoDto alert structure to be parsed
     * @throws NullPointerException when input alert is null
     */
    public AlertParser(AlertInfoDto alertInfoDto) {
        Objects.requireNonNull(alertInfoDto);
        this.alertInfoDto = alertInfoDto;
        this.labels = alertInfoDto.getLabels();
        this.values = alertInfoDto.getValues();
    }


    /**
     * Get alerts.alert.labels value associated to input key
     *
     * @param labelsKey the searched labels key
     * @return the value associated to input key if present or an empty Optional
     */
    public Optional<String> getLabelValue(LabelsKey labelsKey) {
        return getValueFromMap(labelsKey, this.labels);
    }

    /**
     * Get alerts.alert.values value associated to input key
     *
     * @param numericValuesKey the searched labels key
     * @return the value associated to input key if present or an empty Optional
     */
    public Optional<Double> getNumericValue(NumericValuesKey numericValuesKey) {
        return getValueFromMap(numericValuesKey, this.values);
    }

    /**
     * Get the alert startsAt alert field value
     *
     * @return the alert start date field if present or an empty Optional
     */
    public Optional<OffsetDateTime> getStartAtDate() {
        return Optional.ofNullable(this.alertInfoDto.getStartsAt());
    }

    /**
     * Get the alert fingerprint
     *
     * @return alert fingerprint if present or an empty optional
     */
    public Optional<String> getAlertFingerprint() {
        return Optional.ofNullable(alertInfoDto.getFingerprint());
    }

    /**
     * Retrieve, from the input map, the value associated to input key
     *
     * @param key the searched key
     * @param map the map from which retrieve value
     * @param <V> the map generic value type
     * @return the value associated to input key if present or an empty Optional
     */
    private <V> Optional<V> getValueFromMap(AlertMappingKey key, Map<String, V> map) {
        if (map != null) {
            return Optional.ofNullable(map.get(key.getKey()));
        }
        return Optional.empty();
    }
}
